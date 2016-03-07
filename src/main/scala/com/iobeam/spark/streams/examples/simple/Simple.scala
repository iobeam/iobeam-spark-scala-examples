package com.iobeam.spark.streams.examples.simple

import com.iobeam.spark.streams.model.{TimeSeriesStreamPartitioned, TimeRecord,
OutputStreams}
import com.iobeam.spark.streams.{IobeamInterface, SparkApp}
import com.iobeam.spark.streams.annotation.SparkRun

object Simple {
  val SERIES_NAME = "series"
}

/**
 * Adds 10 to the field value.
 */
@SparkRun("Simple")
class Simple extends SparkApp("SimpleApp") {
  override def processStream(iobeamInterface: IobeamInterface):
  OutputStreams = {
    val stream = iobeamInterface.getInputStreamBySource
    val derivedStream = stream.mapValues {
      ds: TimeRecord => {
        val oldValue = ds.requireDouble("value")
        val data = Map[String, Any]("value-new" -> (oldValue + 10))
        new TimeRecord(ds.time, data)
      }
    }

    new OutputStreams(new TimeSeriesStreamPartitioned(derivedStream))
  }
}
