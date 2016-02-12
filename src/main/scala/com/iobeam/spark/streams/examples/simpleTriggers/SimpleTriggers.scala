package com.iobeam.spark.streams.examples.simpletriggers

import com.iobeam.spark.streams.model.{TriggerStream, TriggerEvent, OutputStreams, TimeRecord}
import com.iobeam.spark.streams.{IobeamInterface, SparkApp}

/**
 * Trigger example
 */
class SimpleTriggers extends SparkApp("SimpleApp") {
  override def processStream(iobeamInterface: IobeamInterface):
  OutputStreams = {
    val stream = iobeamInterface.getInputStreamBySource
    val filteredStream = stream
      //get rid of the config
      .map { case (devId, data) => (devId, data) }

      //filter on events that have low battery
      .filter {
      case (devId, data) =>
        //check if data has battery series and battery below 60%.
        data.getDouble("battery") match {
          case Some(battery) => battery < 0.60
          case None => false
        }
    }

    //create trigger event object.
    val triggerStream = filteredStream.map { case (devId, data) =>
      //create data that can be used in email/text/etc. templates.
      val triggerData = Map("devId" -> devId, "battery" -> data.requireDouble("battery"))

      //create event object for event type "batteryLow"
      TriggerEvent("batteryLow", new TimeRecord(data.time, triggerData))
    }

    new OutputStreams(TriggerStream(triggerStream))
  }
}
