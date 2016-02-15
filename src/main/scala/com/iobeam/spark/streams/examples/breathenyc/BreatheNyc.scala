package com.iobeam.spark.streams.examples.breathenyc

import com.iobeam.spark.streams.{IobeamInterface, SparkApp}
import com.iobeam.spark.streams.model.{OutputStreams, TimeRecord, TimeSeriesStreamPartitioned}
import org.apache.spark.streaming.Duration

import scala.collection.mutable.ListBuffer

/**
  * BreatheNYC Spark Streaming app
  *
  * Input: DStream with data ["time", "latitude", "longitude", "temperature", "humidity",
  * "pressure"]
  *
  * Output: DStream with data ["time", "latitude", "longitude", "temperature", "comfortscore"  ]
  */

object BreatheNyc {
    val TIME_ACCELERATION = 30
    val LATITUDE = "latitude"
    val LONGITUDE = "longitude"
    val TEMPERATURE = "temperature"
    val HUMIDITY = "humidity"
    val COMFORT_TIME_SERIES_NAME = "comfort"
    val TRIGGER_EVENT_NAME = "score"
    val GEO_SMOOTHED_TIME_SERIES_NAME = "meancomfortlevel"
    // 4 digits GPS_BIN_SIZE is about 11 m
    val GPS_BIN_SIZE = Math.pow(10, 3)
    val GEO_SMOOTHING_GRID_SIDE = 5
    val WINDOW_LENGTH_MS = 15 * 60 * 1000 / TIME_ACCELERATION
    val WINDOW_SLIDE_DURATION_MS = 60 * 1000 / TIME_ACCELERATION
    val IDEAL_TEMP = 72.0
    val IDEAL_HUMIDITY = 50.0
}

class BreatheNyc(iobeamInterface: IobeamInterface) extends SparkApp("BreatheNYC") {

    override def processStream(iobeamInterface: IobeamInterface):
    OutputStreams = {
        val stream = iobeamInterface.getInputStreamBySource
        val s = stream.mapValues(a => new SensorReadings(a))
        // Streams of data arrive belonging to individual devices (buses).
        // We first map the sensors to the geo-region in which they belong (e.g., 11m^2 region)
        // For each geo-region, we then split the geo-region by into 15min sliding windows

        val bins = s.map(dataToBins).groupByKeyAndWindow(Duration(BreatheNyc.WINDOW_LENGTH_MS),
            Duration(BreatheNyc.WINDOW_SLIDE_DURATION_MS))

        // Calculate means of sensor readings for each geo region's window
        val means = bins.mapValues(a =>
            new SensorReadings((a.toList.map(_.time).sum / a.size.toDouble).toLong,
                a.toList.map(_.latitude).sum / a.size.toDouble,
                a.toList.map(_.longitude).sum / a.size.toDouble,
                a.toList.map(_.temperature).sum / a.size.toDouble,
                a.toList.map(_.humidity).sum / a.size.toDouble))

        val mergedComforts = means.mapValues(a => new ComfortLevel(a))

        // For each region-window, smooth score by inverse-distance-weighting with nearby
        // readings, as well as compute imputed scores for regions with no direct sensor data
        val geoSmoothed = mergedComforts.flatMap(dataToMultipleBins)
            .groupByKey().map(inverseDistanceSmoothing)

        // Output stream of geosmoothed comfort scores
        new OutputStreams(new TimeSeriesStreamPartitioned(BreatheNyc.GEO_SMOOTHED_TIME_SERIES_NAME,
            geoSmoothed.map(t => (t._1, t._2.toDataSet))))
    }

    class SensorReadings(val time: Long,
                         val latitude: Double,
                         val longitude: Double,
                         val temperature: Double,
                         val humidity: Double) extends Serializable {

        def this(timeRecord: TimeRecord) {
            this(timeRecord.time,
                timeRecord.requireDouble(BreatheNyc.LATITUDE),
                timeRecord.requireDouble(BreatheNyc.LONGITUDE),
                timeRecord.requireDouble(BreatheNyc.TEMPERATURE),
                timeRecord.requireDouble(BreatheNyc.HUMIDITY))
        }
    }

    def calcTempComfort(temp: Double): Double = {
        val threshold = {
            if (temp < 72.0) {
                50.0
            } else {
                20.0
            }
        }

        Math.min(Math.abs(temp - BreatheNyc.IDEAL_TEMP) / threshold, 1.0)
    }

    def calcHumidityComfort(humidity: Double): Double = {
        Math.min(Math.abs(humidity - BreatheNyc.IDEAL_HUMIDITY) / 25.0, 1.0)
    }

    def calcComfortLevel(temperatureScore: Double, humidityScore: Double): Double = {
        1 - (temperatureScore + humidityScore) / 2.0
    }

    class ComfortLevel(val time: Long,
                       val comfortLevel: Double,
                       val temperature: Double,
                       val humidity: Double,
                       val latitude: Double,
                       val longitude: Double) extends Serializable {

        def this(computation: SensorReadings) {
            this(computation.time,
                calcComfortLevel(computation.temperature, computation.humidity),
                computation.temperature,
                computation.humidity,
                computation.latitude,
                computation.longitude)
        }
    }

    class GeoSmoothedScore(val time: Long, val temp: Double, val humidity: Double,
                           val comfortLevel: Double, val lat: Double,
                           val long: Double) extends Serializable {
        val data = Map(BreatheNyc.LATITUDE -> lat, BreatheNyc.LONGITUDE -> long,
            BreatheNyc.TEMPERATURE -> temp, BreatheNyc.HUMIDITY -> humidity, BreatheNyc
                .COMFORT_TIME_SERIES_NAME -> comfortLevel)

        def toDataSet: TimeRecord = new TimeRecord(time, data)

        def getData: Map[TimeRecord.Key, Double] = data.asInstanceOf[Map[TimeRecord.Key, Double]]
    }

    def getBinnedGpsCoordinate(latOrLong: Double): Double = {
        Math.round(latOrLong * BreatheNyc.GPS_BIN_SIZE) / BreatheNyc.GPS_BIN_SIZE.toDouble
    }

    def getGpsGrid(lat: Double, long: Double, gridSide: Int): Seq[(Double, Double)] = {

        if (gridSide % 2 != 1) {
            throw new IllegalArgumentException("gridSide must be an odd number")
        }

        val stepsFromCenter = (gridSide - 1) / 2
        val listBuilder = new ListBuffer[(Double, Double)]

        val latLowerLeft = getBinnedGpsCoordinate(lat - stepsFromCenter / BreatheNyc.GPS_BIN_SIZE)
        val longLowerLeft = getBinnedGpsCoordinate(long - stepsFromCenter / BreatheNyc.GPS_BIN_SIZE)

        for (latIndex <- 0 to gridSide - 1) {
            for (longIndex <- 0 to gridSide - 1) {
                listBuilder.append((latLowerLeft + latIndex / BreatheNyc.GPS_BIN_SIZE,
                    longLowerLeft +
                        longIndex / BreatheNyc.GPS_BIN_SIZE))
            }
        }

        listBuilder.toSeq
    }

    def dataToBins(t: (String, SensorReadings)): ((Double, Double), SensorReadings) = {

        val (_, comp) = t

        val latBin = getBinnedGpsCoordinate(comp.latitude)
        val longBin = getBinnedGpsCoordinate(comp.longitude)

        ((latBin, longBin), comp)
    }

    // generate a list of cells that are affected by input cell, decided by grid size
    def dataToMultipleBins(t: ((Double, Double), ComfortLevel)): List[((Double, Double),
        ComfortLevel)] = {
        val ((latitude, longitude), comfortLevel) = t

        val listBuffer = new ListBuffer[((Double, Double), ComfortLevel)]

        for (cellCoordinates <- getGpsGrid(latitude, longitude, BreatheNyc
            .GEO_SMOOTHING_GRID_SIDE)) {
            listBuffer.append((cellCoordinates, comfortLevel))
        }

        listBuffer.toList
    }

    def latLongToKey(lat: Double, long: Double): String = {
        s"$lat|$long"
    }

    def inverseDistanceSmoothing(t: ((Double, Double), Iterable[ComfortLevel])):
    (String, GeoSmoothedScore) = {
        val ((lat, long), pointList) = t

        if (pointList.isEmpty) {
            throw new IllegalArgumentException("Empty list in inverseDistanceSmoothing")
        }

        val SMOOTHING = 20
        val SMALLEST_DISTANCE = 0.0000000001
        val POWER = 1

        var tempNominator = 0.0
        var humidNominator = 0.0
        var scoreNominator = 0.0
        var denominator = 0.0

        var timeSum = 0L

        for (comfortLevel <- pointList) {
            val otherLat = comfortLevel.latitude
            val otherLong = comfortLevel.longitude
            val score = comfortLevel.comfortLevel

            timeSum += comfortLevel.time

            val dist = Math.sqrt((otherLat - lat) * (otherLat - lat) +
                (otherLong - long) * (otherLong - long) +
                SMOOTHING * SMOOTHING)

            // If the point is really close to one of the data points,
            // return the data point value to avoid singularities

            if (dist < SMALLEST_DISTANCE) {
                return (latLongToKey(lat, long),
                    new GeoSmoothedScore(comfortLevel.time, comfortLevel.temperature, comfortLevel
                        .humidity, comfortLevel.comfortLevel, comfortLevel.latitude, comfortLevel.longitude))
            }

            scoreNominator += score / Math.pow(dist, POWER)
            tempNominator += comfortLevel.temperature / Math.pow(dist, POWER)
            humidNominator += comfortLevel.humidity / Math.pow(dist, POWER)
            denominator += 1 / Math.pow(dist, POWER)
        }

        val meanTime = timeSum / pointList.size
        (latLongToKey(lat, long), new GeoSmoothedScore(meanTime.toLong,
            tempNominator / denominator,
            humidNominator / denominator,
            scoreNominator / denominator,
            lat, long))
    }
}