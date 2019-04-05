package com.thecubecast.reengine.data.dcputils

/**
 * Created by Darius on 03/01/2018
 */
open class Timer @JvmOverloads constructor(var timeLimit: Float = 1f, isTimerOn: Boolean = true,
                                      var turnOffWhenTimeIsReached: Boolean = false) {
    var elapsed: Float = 0f
    var isTimerOn: Boolean = isTimerOn
        private set

    /**
     * @return whether the timer reached the timeLimit
     */
    fun tick(delta: Float): Boolean {
        if (isTimerOn) {
            elapsed += delta
            if (elapsed > timeLimit) {
                elapsed -= timeLimit

                if (turnOffWhenTimeIsReached) {
                    turnOff()
                }

                return true
            }
        }

        return false
    }

    fun reset() {
        this.elapsed = 0f
    }

    fun turnOff() {
        isTimerOn = false
    }

    fun turnOn() {
        isTimerOn = true
    }
}

class LoopTimer(timeLimit: Float) : Timer(timeLimit, true, false)

/** Only ticks when the timeLimit is reached, then it turns off */
class AlarmTimer(timeLimit: Float) : Timer(timeLimit, true, true)
