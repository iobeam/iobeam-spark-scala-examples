package com.iobeam.spark.streams.examples.busbunching

import scala.collection.mutable.ListBuffer

import com.iobeam.spark.streams.SparkApp
import com.iobeam.spark.streams.config.DeviceConfig
import com.iobeam.spark.streams.model.{TimeSeriesStreamPartitioned, TimeRecord, OutputStreams}
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.dstream.DStream
import RouteLengths.getRouteLength


/**
 * BusBunching Spark Streaming app
 *
 * Real-time NYC MTA data from http://bustime.mta.info/wiki/Developers/SIRIStopMonitoring
 *
 * Input: DStream with data ["time", "latitude", "longitude", "route", "direction", "busid",
 * "progress"]
 *
 * Output: DStream with data ["time", "latitude", "longitude", "busid", "bunching factor"]
 *
 */

object BusBunching {
    val TIME_ACCELERATION = 10
    val BUS_POSITIONS_TIMESERIES = "buspositions"
    val BUNCHING_TIME_SERIES_NAME = "bunchlevel"
    val GEO_SMOOTHED_TIME_SERIES_NAME = "meanbunchlevel"
    // 4 digits GPS_BIN_SIZE is about 11 m
    val GPS_BIN_SIZE = Math.pow(10, 3)
    val GEO_SMOOTHING_GRID_SIDE = 1
    val WINDOW_LENGTH_MS = 10 * 60 * 1000 / TIME_ACCELERATION
    val WINDOW_SLIDE_DURATION_MS = 60 * 1000 / TIME_ACCELERATION

    val LONGITUDE = "longitude"
    val LATITUDE = "latitude"
    val ROUTE = "route"
    val DIRECTION = "direction"
    val BUSID = "busid"
    val PROGRESS = "progress"
}

class BusBunching() extends SparkApp("BusBunching") {

    override def processStream(stream: DStream[(String, (TimeRecord, DeviceConfig))]):
    OutputStreams = {

        val s = stream.mapValues(a => (new BusPosition(a._1), a._2))

        // Streams of data arrive belonging to individual devices (buses).
        // We collect readings in a time window and take the first reading of each bus.
        val readings = s.map(positionsByBus)
            .groupByKeyAndWindow(Duration(BusBunching.WINDOW_LENGTH_MS), Duration(BusBunching
                .WINDOW_SLIDE_DURATION_MS))
            .mapValues(a => a.toList.sortWith(_._1.time < _._1.time)).mapValues(a => a.head)

        // We then collect a window of readings and group them by route and direction. It ensures
        // that all readings from a route ends up in the same place which is needed when bunching is
        // calculated.
        val windowedReadingsByRouteAndId = readings.map(busesToRoutesAndDirection).groupByKey()

        // Then we calculate bunching factor of each bus.
        // The bunching factor is the ratio of the distance to the next bus and the ideal distance.
        val bunchingLevels = windowedReadingsByRouteAndId
            .flatMapValues(getRouteBunchingLevel)

        val badBunchingLevels = bunchingLevels.filter(a => a._2._1.value < 1.0)

        // Calculate the geoSmoothed bunching
        val geoSmoothedBunching = badBunchingLevels
            .flatMap(dataToMultipleBins)
            .groupByKey()
            .map(smoothBunching)
            .map(a => (s"${a._1._1}_${a._2._1}}", a._2))

        new OutputStreams(new TimeSeriesStreamPartitioned(BusBunching.BUNCHING_TIME_SERIES_NAME,
            bunchingLevels.mapValues(a => a._1.toTimeRecord)),
        new TimeSeriesStreamPartitioned(BusBunching.GEO_SMOOTHED_TIME_SERIES_NAME, geoSmoothedBunching.mapValues(a => a._1.toTimeRecord)))

    }

    class BusPosition(val time: Long,
                      val deviceConfig: Option[DeviceConfig],
                      val route: String,
                      val direction: Int,
                      val busId: String,
                      val latitude: Double,
                      val longitude: Double,
                      val progress: Double) extends Serializable{

        def this(timeRecord: TimeRecord) = {
            this(timeRecord.time,
                None,
                timeRecord.requireString(BusBunching.ROUTE),
                timeRecord.requireString(BusBunching.DIRECTION).toInt,
                timeRecord.requireString(BusBunching.BUSID),
                timeRecord.requireDouble(BusBunching.LATITUDE),
                timeRecord.requireDouble(BusBunching.LONGITUDE),
                timeRecord.requireDouble(BusBunching.PROGRESS)
            )
        }
    }

    class GeoSmoothedBunching(val time: Long, val value: Double, val route: String,
                              val direction: Int, val lat: Double, val long: Double) {
        def toTimeRecord: TimeRecord = {
            val data = Map(BusBunching.LATITUDE -> this.lat, BusBunching.LONGITUDE -> this.long, BusBunching.BUNCHING_TIME_SERIES_NAME -> value)
            new TimeRecord(this.time, data)
        }
    }

    class BunchingLevel(val time: Long,
                        val value: Double,
                        val route: String,
                        val direction: Int,
                        val busId: String,
                        val latitude: Double,
                        val longitude: Double) extends Serializable {

        def toTimeRecord: TimeRecord = {
            val data = Map(BusBunching.LATITUDE -> this.latitude, BusBunching.LONGITUDE -> this.longitude, BusBunching.BUS_POSITIONS_TIMESERIES -> value)
            new TimeRecord(this.time, data)
        }
    }

    def getBinnedGpsCoordinate(latOrLong: Double): Double = {
        Math.round(latOrLong * BusBunching.GPS_BIN_SIZE).toInt / BusBunching.GPS_BIN_SIZE.toDouble
    }

    def getGpsGrid(lat: Double, long: Double, gridSide: Int): Seq[(Double, Double)] = {

        if (gridSide % 2 != 1) {
            throw new IllegalArgumentException("gridSide must be an odd number")
        }

        val stepsFromCenter = (gridSide - 1) / 2
        val listBuilder = new ListBuffer[(Double, Double)]

        val latLowerLeft = getBinnedGpsCoordinate(lat - stepsFromCenter / BusBunching.GPS_BIN_SIZE)
        val longLowerLeft = getBinnedGpsCoordinate(long - stepsFromCenter / BusBunching
            .GPS_BIN_SIZE)

        for (latIndex <- 0 to gridSide - 1) {
            for (longIndex <- 0 to gridSide - 1) {
                listBuilder.append((latLowerLeft + latIndex / BusBunching.GPS_BIN_SIZE,
                    longLowerLeft +
                        longIndex / BusBunching.GPS_BIN_SIZE))
            }
        }

        listBuilder.toSeq
    }

    // generate a list of cells that are affected by input cell, decided by grid size
    def dataToMultipleBins(t: (String, (BunchingLevel, DeviceConfig))): List[((Double, Double), (BunchingLevel,
        DeviceConfig))] = {
        val (_, (dataSet, conf)) = t

        val listBuffer = new ListBuffer[((Double, Double), (BunchingLevel, DeviceConfig))]

        for (cellCoordinates <- getGpsGrid(dataSet.latitude, dataSet.longitude, BusBunching
            .GEO_SMOOTHING_GRID_SIDE)) {
            listBuffer.append((cellCoordinates, (dataSet, conf)))
        }

        listBuffer.toList
    }

    def latLongToKey(lat: Double, long: Double): String = {
        s"$lat|$long"
    }

    def positionsByBus(t: (String, (BusPosition, DeviceConfig))): (String,
        (BusPosition, DeviceConfig)) = {
        val (_, (busPosition, conf)) = t

        (busPosition.busId, (busPosition, conf))
    }

    def busesToRoutesAndDirection(t: (String, (BusPosition, DeviceConfig))): (String,
        (BusPosition, DeviceConfig)) = {
        val (_, (busPosition, conf)) = t
        val route = busPosition.route
        val direction = busPosition.direction
        val key = s"${route}_$direction"
        (key, (busPosition, conf))
    }

    def getRouteBunchingLevel(busPositions: Iterable[(BusPosition, DeviceConfig)]): List[
        (BunchingLevel, DeviceConfig)] = {

        val busPositionsList = busPositions
            .toList
            .sortWith(_._1.progress < _._1.progress)

        val listBuffer = ListBuffer[(BunchingLevel, DeviceConfig)]()

        val firstBus = busPositionsList.head._1
        val routeLength = getRouteLength(firstBus.route, firstBus.direction)
        val nBuses = busPositionsList.length

        for (i <- 0 until busPositionsList.length - 1) {
            val (busPosition, conf) = busPositionsList(i)
            val (busBefore, _) = busPositionsList(i + 1)

            val distance = busBefore.progress - busPosition.progress
            val bunchingLevelValue = distance / (routeLength / (nBuses + 1))

            val bunchingLevel = new BunchingLevel(busPosition.time, bunchingLevelValue, busPosition
                .route, busPosition.direction, busPosition.busId, busPosition.latitude, busPosition.longitude)

            listBuffer.append((bunchingLevel, conf))
        }
        listBuffer.toList
    }

    def smoothBunching(t: ((Double, Double), Iterable[(BunchingLevel, DeviceConfig)])):
    ((Double, Double), (GeoSmoothedBunching, DeviceConfig)) = {
        val ((latBin, lonBin), pointList) = t

        if (pointList.isEmpty) {
            throw new IllegalArgumentException("Empty list in bunchingSmoothing")
        }

        val SMOOTHING = 20
        val SMALLEST_DISTANCE = 0.0000000001
        val POWER = 1

        var nominator = 0.0
        var denominator = 0.0

        var timeSum = 0L

        for ((dataSet, conf) <- pointList) {
            val bunchingLevel = dataSet.asInstanceOf[BunchingLevel]
            val otherLat = bunchingLevel.latitude
            val otherLong = bunchingLevel.longitude
            timeSum += bunchingLevel.time

            val dist = Math.sqrt((otherLat - latBin) * (otherLat - latBin) +
                (otherLong - lonBin) * (otherLong - lonBin) +
                SMOOTHING * SMOOTHING)

            // If the point is really close to one of the data points,
            // return the data point value to avoid singularities

            if (dist < SMALLEST_DISTANCE) {

                val geoSmoothedBunching = new GeoSmoothedBunching(bunchingLevel.time,
                    bunchingLevel.value,
                    bunchingLevel.route,
                    bunchingLevel.direction,
                    latBin,
                    lonBin)

                return ((latBin, lonBin), (geoSmoothedBunching, conf))
            }

            nominator += bunchingLevel.value / Math.pow(dist, POWER)
            denominator += 1 / Math.pow(dist, POWER)
        }

        val (dataSet, conf) = pointList.head
        val bunchingLevelpLevel = dataSet.asInstanceOf[BunchingLevel]
        val geoSmoothedBunching = new GeoSmoothedBunching(bunchingLevelpLevel.time,
            nominator / denominator,
            bunchingLevelpLevel.route,
            bunchingLevelpLevel.direction,
            latBin,
            lonBin)

        ((latBin, lonBin), (geoSmoothedBunching, conf))
    }
}
