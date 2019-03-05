package com.thecubecast.reengine.data.dcputils

import com.badlogic.gdx.math.Interpolation

/**
 * Created by Darius on 10/04/2018.
 */
class Interpolator(var interpolation: Interpolation, var duration: Float, var startValue: Float, var endValue: Float) {
    val timer = Timer(10000000f)

    fun getValue(): Float {
        return interpolation.apply(mapToRange(timer.elapsed, 0f, duration, startValue, endValue))
    }

    fun update(delta: Float) {
        timer.tick(delta)
    }

    fun resetElapsed() {
        timer.reset()
    }

    val hasFinished
        get() = timer.elapsed > duration

    private fun mapToRange(inputNumber: Number, inputLow: Number, inputHigh: Number, outputLow: Number, outputHigh: Number): Float {
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
}
