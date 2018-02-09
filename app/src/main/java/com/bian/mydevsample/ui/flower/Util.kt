package com.bian.mydevsample.ui.flower

import android.graphics.PointF

/**
 * author 边凌
 * date 2018/2/9 14:40
 * 类描述：
 */
object Util {
    fun rotateCoordinateSystem(pointF : PointF, degree : Double) {
        val toRadians = Math.toRadians(degree)
        val cosDegree = Math.cos(toRadians)
        val sinDegree = Math.sin(toRadians)
        val x = pointF.x * cosDegree + pointF.y * sinDegree
        val y = - pointF.x * sinDegree + pointF.y * cosDegree
        pointF.set(x.toFloat(), y.toFloat())
    }

    fun translationCoordinateSystem(pointF : PointF, originX : Float, originY : Float) {
        val x = pointF.x + originX
        val y = pointF.y + originY
        pointF.set(x, y)
    }
}