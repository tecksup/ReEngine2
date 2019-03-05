package com.thecubecast.reengine.data.dcputils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array

/**
 * Created by Darius on 17/01/2018
 */
open class TextureAnimation<T>(val frames: Array<T>, frameDuration: Float = 1f) {
    var elapsedTime = 0f
    var isPaused = false

    var totalAnimationDuration: Float = 0f
        private set
    var type: Type

    enum class Type {
        NORMAL, REVERSED, NORMAL_REVERSED_LOOP, STOP_IN_EACH_NEW_FRAME
    }

    private var normalReversedCycle = Type.NORMAL

    fun changeFrameDurationKeepingFrameIndex(frameDuration: Float) {
        val frame = frameIndex
        val progress = getDecimalPart(elapsedTime / frameDuration)
        this.frameDuration = frameDuration
        this.elapsedTime = frame * frameDuration
        this.elapsedTime += frameDuration * progress
    }

    private fun getDecimalPart(number: Float): Float {
        return number - number.toInt()
    }

    /** Be aware that changing frameDuration while animation is running will affect the current animation frame.
     * Use [changeFrameDurationKeepingFrameIndex] instead*/
    var frameDuration: Float = frameDuration
        set(value) {
            field = value
            updateTotalAnimationDuration()
        }

    val numberOfFrames: Int
        get() = frames.size

    private var finishedOneLoop = false

    var stayOnLastFrameAfterFinished = false

    init {
        this.type = Type.NORMAL
        updateTotalAnimationDuration()
    }

    private fun updateTotalAnimationDuration() {
        this.totalAnimationDuration = frameDuration * frames.size
    }

    fun getFrame(delta: Float): T {
        if (!isPaused) {
            when (type) {
                Type.NORMAL -> {
                    elapsedTime += delta
                    if (finishedNormalAnimation()) {
                        elapsedTime = 0f
                        finishedOneLoop = true
                    }
                }

                Type.REVERSED -> {
                    elapsedTime -= delta
                    if (finishedReversedAnimation()) {
                        elapsedTime = totalAnimationDuration - 0.0001f
                        finishedOneLoop = true
                    }
                }

                Type.NORMAL_REVERSED_LOOP -> if (normalReversedCycle == Type.NORMAL) {
                    elapsedTime += delta
                    if (finishedNormalAnimation()) {
                        normalReversedCycle = Type.REVERSED
                        elapsedTime = totalAnimationDuration - frameDuration - 0.001f
                    }
                } else if (normalReversedCycle == Type.REVERSED) {
                    elapsedTime -= delta
                    if (finishedReversedAnimation()) {
                        normalReversedCycle = Type.NORMAL
                        elapsedTime = frameDuration
                        finishedOneLoop = true
                    }
                }
                Type.STOP_IN_EACH_NEW_FRAME -> {
                    val previousFrame = frameIndex
                    elapsedTime += delta
                    val newFrame = frameIndex
                    if (newFrame != previousFrame) {
                        pause()
                        setFrame(newFrame)
                    }

                    if (finishedNormalAnimation()) {
                        finishedOneLoop = true
                    }
                }
            }
        }

        return frameFromElapsedTime
    }

    /** Whether the animation finished playing forward
     *
     *
     * More detailed: Returns true when the elapsed time is bigger than maximum elapsed time (depending on
     * number of frames and frame duration)  */
    fun finishedNormalAnimation(): Boolean {
        return elapsedTime >= totalAnimationDuration
    }

    /** Whether the animation finished playing backwards
     *
     *
     * More detailed: Returns true when the elapsed time is smaller than 0 */
    fun finishedReversedAnimation(): Boolean {
        return elapsedTime <= 0
    }

    /** Starts counting loops since the last reset. What a loop is depends on type of animation. *(So, for example, in a
     * normalReversed animation, a loop is counted when the animation goes forward and backwards 1 time*
     *
     * **Warning: is represented by an internal boolean instead of the elapsed time, which means that if the
     * animation is paused the boolean value will not be updated, even if the animation technically ended**
     *
     * **Use [.finishedNormalAnimation] or [.finishedReversedAnimation] instead**  */
    fun hasFinishedOneLoop(): Boolean {
        return finishedOneLoop
    }

    private val frameFromElapsedTime: T
        get() {
            val index = frameIndex
            return frames[index]
        }

    val frameIndex: Int
        get() {
            var index = MathUtils.floor((elapsedTime / frameDuration))
            index = Math.max(index, 0)
            index = Math.min(index, frames.size - 1)
            return index
        }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun reset() {
        elapsedTime = 0f
        finishedOneLoop = false
    }

    fun setFrame(frameIndex: Int) {
        elapsedTime = frameDuration * frameIndex
        finishedOneLoop = false
    }
}
