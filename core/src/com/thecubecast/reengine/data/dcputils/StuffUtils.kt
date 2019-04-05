package com.thecubecast.reengine.data.dcputils

/** Clamps input values to input range, then maps them output range.
 *
 * inputLow must be lower than inputHigh, but outputLow can be bigger than outputHigh; map still will happen correctly  */
fun mapToRange(inputNumber: Number, inputLow: Number, inputHigh: Number, outputLow: Number, outputHigh: Number): Float {
    var thisOutputLow = outputLow.toFloat()
    var thisOutputHigh = outputHigh.toFloat()
    if (inputNumber.toFloat() < inputLow.toFloat()) return thisOutputLow
    if (inputNumber.toFloat() > inputHigh.toFloat()) return thisOutputHigh

    var switched = false
    if (thisOutputLow > thisOutputHigh) {
        val temp = thisOutputHigh
        thisOutputHigh = thisOutputLow
        thisOutputLow = temp
        switched = true
    }

    val scale = (thisOutputHigh - thisOutputLow) / (inputHigh.toFloat() - inputLow.toFloat())
    val value = (inputNumber.toFloat() - inputLow.toFloat()) * scale + thisOutputLow

    return if (switched) {
        thisOutputLow - value + thisOutputHigh
    } else value
}