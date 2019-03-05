package com.thecubecast.reengine.data.dcputils

import com.badlogic.gdx.math.Interpolation

/**
 * Created by Darius on 15/04/2018.
 */
abstract class Action(duration: Float, interpolation: Interpolation = Interpolation.linear) {
    val interpolator = Interpolator(interpolation, duration, 0f, 1f)
    var pauseTimer = false
    private var finalUpdateDone = false

    open fun update(delta: Float) {
        if (interpolator.hasFinished) {
            if (finalUpdateDone) return

            finalUpdate()
            finalUpdateDone = true
            return
        }

        if (!pauseTimer) interpolator.update(delta)
        updateProgress(interpolator.getValue())
    }

    /** @param percent Progress from 0 to 1 (completed) affected by interpolation.
     * Note that interpolation is already applied, and percent may go above 1 */
    abstract fun updateProgress(percent: Float)

    /** Always runs before finishing, even if the duration is 0 */
    abstract fun finalUpdate()

    fun forceFinish() {
        interpolator.timer.elapsed = interpolator.timer.timeLimit
    }

    /** Prepares the Action to be reused, reset all variables */
    open fun reset() {
        interpolator.timer.elapsed = 0f
        finalUpdateDone = false
    }

    open val hasFinished: Boolean
        get() = interpolator.hasFinished && finalUpdateDone

    companion object {
        fun run(delay: Float, function: () -> Unit) = RunAction(delay, function)
        fun run(function: () -> Unit) = RunAction(0f, function)
        fun wait(delay: Float) = WaitAction(delay)
        fun sequence(vararg actions: Action) = SequenceAction(*actions)
        fun parallel(vararg actions: Action) = ParallelAction(*actions)
        fun value(initialValue: Float, endValue: Float, duration: Float, interpolation: Interpolation, valueUpdated: (Float) -> Unit = {})
                = ValueAction(initialValue, endValue, duration, interpolation, valueUpdated)
    }
}
