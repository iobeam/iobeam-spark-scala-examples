package com.iobeam.spark.streams.examples.simple

import com.iobeam.spark.streams.model.{TimeSeriesStreamSimple, TimeRecord, OutputStreams}
import com.iobeam.spark.streams.{IobeamInterface, SparkApp}

object Simple {
  val SERIES_NAME = "series"
}

/**
 * Adds 10 to the field value.
 */
class Simple extends SparkApp("SimpleApp") {
  override def processStream(iobeamInterface: IobeamInterface):
  OutputStreams = {
    val stream = iobeamInterface.getInputStreamRecords
    val derivedStream = stream.map {
      ds: TimeRecord => {
        val oldValue = ds.requireDouble("value")
        val data = Map[String, Any]("value" -> (oldValue + 10))
        new TimeRecord(ds.time, data)
      }
    }

    new OutputStreams(new TimeSeriesStreamSimple(Simple.SERIES_NAME, derivedStream))
  }
}
