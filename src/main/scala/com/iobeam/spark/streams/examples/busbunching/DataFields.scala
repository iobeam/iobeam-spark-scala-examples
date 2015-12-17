package com.iobeam.spark.streams.examples.busbunching

import com.iobeam.spark.streams.model.TimeRecord

/**
 * Import fields for MTA Bus bunching analysis
 */

sealed class DataField(val name: String, val description: String){
    def getKey: TimeRecord.Key = name
}

object DataField {
    def withName(name: String): DataField = {
        name.toLowerCase match {
            case LAT.name => LAT
            case LONG.name => LONG
            case ROUTE.name => ROUTE
            case DIRECTION.name => DIRECTION
            case BUSID.name => BUSID
            case PROGRESS.name => PROGRESS
            case _ => throw new IllegalArgumentException(s"No such field: $name")
        }
    }
}

case object LAT extends DataField("latitude", "Latitude")
case object LONG extends DataField("longitude", "Longitude")
case object ROUTE extends DataField("route", "Route")
case object DIRECTION extends DataField("direction", "Direction")
case object BUSID extends DataField("busid", "BusID")
case object PROGRESS extends DataField("progress", "Progress")

