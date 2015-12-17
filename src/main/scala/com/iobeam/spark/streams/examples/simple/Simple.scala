package com.iobeam.spark.streams.examples.simple

import com.iobeam.spark.streams.config.DeviceConfig
import com.iobeam.spark.streams.model.{TimeSeriesStreamSimple, TimeRecord, OutputStreams}
import com.iobeam.spark.streams.{SparkApp, AppRunner}
import org.apache.logging.log4j
import org.apache.logging.log4j.LogManager
import org.apache.spark.streaming.dstream.DStream

object Simple {
  val SERIES_NAME = "series"
}

/**
 * Adds 10 to all values.
 */
class Simple extends SparkApp("SimpleApp") {
  override def processStream(stream: DStream[(String, (TimeRecord, DeviceConfig))]):
  OutputStreams = {
    val derivedStream = stream.map { case (dev_id, (data, config)) => data } map {
      ds: TimeRecord => {
        LogManager.getLogger(this.getClass).info(s"In SimpleApp")
        val oldValue = ds.requireDouble("value")
        val data = Map[String, Any]("value" -> (oldValue + 10))
        new TimeRecord(ds.time, data)
      }
    }

    new OutputStreams(new TimeSeriesStreamSimple(Simple.SERIES_NAME, derivedStream))
  }
}
