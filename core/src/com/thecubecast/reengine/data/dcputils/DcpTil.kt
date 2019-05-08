package com.thecubecast.reengine.data.dcputils

fun getAngleBetweenPoints(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    var angle = Math.toDegrees(Math.atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())).toFloat()
    if (angle < 0) angle += 360f
    return angle
}