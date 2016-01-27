# iobeam spark examples

This repository contains examples of iobeam spark applications [The full
reference API can be
found here](http://docs.iobeam.com/lib/analyze/#com.iobeam.spark.streams.package).


## Simple

The simple example is a minimal example that processes streaming data by
increasing each field "value" with 10.

## SimpleTriggers

SimpleTriggers shows how to output a stream of triggers. In the example a
trigger is generated for every device that reports battery level below a
threshold.

## BusBunching

BusBunching is when buses on a line gets clustered causing bad
utilisation. This example uses public bus position data from
[MTA](http://www.mta.info/) and identifies bunching problems. It shows methods
to group data on fields in the data as well as geographic regions. It also
shows how to create multiple output streams.

## BreatheNyc

BreatheNyc is an example where air quality is calculated. It shows how you can
calculate a mean for a geographic region.
