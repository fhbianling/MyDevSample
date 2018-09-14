package com.bian.mydevsample.ui.flower

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.View

fun PointF.rotateCoordinateSystem(degree : Double) {
    val toRadians = Math.toRadians(degree)
    val cosDegree = Math.cos(toRadians)
    val sinDegree = Math.sin(toRadians)
    val x = this.x * cosDegree + this.y * sinDegree
    val y = - this.x * sinDegree + this.y * cosDegree
    this.set(x.toFloat(), y.toFloat())
}

fun PointF.translationCoordinateSystem(originX : Float, originY : Float) {
    val x = this.x + originX
    val y = this.y + originY
    this.set(x, y)
}

/**
 * 求平方值
 */
fun Int.square() : Double {
    return this.toDouble().square()
}

fun Float.square() : Double {
    return this.toDouble().square()
}

fun Double.square() : Double {
    return Math.pow(this, 2.0)
}

private const val SCATTERING_ANGLE = 30//散射角度范围，中轴向两侧各15度范围

/**
 * author 边凌
 * date 2018/2/5 15:31
 * 类描述：
 */
class Flower(view : View) {
    private val widthScope = view.width
    private val heightScope = view.height
    private var radius : Float = (Math.random() * 4 + 4).toFloat()
    private var computer = Computer()

    init {
        computer.initialValue()
    }

    fun draw(canvas : Canvas, paint : Paint) {
        if (computer.finish) {
            computer.initialValue()
        }
        val drawPosition = computer.calcDrawPosition()
        canvas.drawCircle(drawPosition.x, drawPosition.y, radius, paint)
    }

    inner class Computer {
        private var process = 0f
        private var startX = 0f
        private var startY = 0f
        private var step = 0f//步进值，实际代表速度
        private var directionDegree = 0.0
        private var maxProcess = 0f//路程
        private val drawPosition = PointF()
        private val endPosition = PointF()
        val finish : Boolean get() = process == maxProcess

        fun initialValue() {
            process = 0f
            directionDegree = Math.random() * SCATTERING_ANGLE - SCATTERING_ANGLE / 2f
            step = (Math.random() * 0.0045 + 0.0005).toFloat()
            maxProcess = (Math.random() * 0.6 + 0.4).toFloat()
            drawPosition.set(0f, 0f)

            generateRandomStartPosition()
        }

        private fun generateRandomStartPosition() {
            val r = Math.sqrt(widthScope.square() + heightScope.square()) / 2
            //原坐标系计算出的起始点x,y
            val startX = (Math.random() * 2 * r - r).toFloat()
            val startY = (- Math.random() * Math.sqrt(r.square() - startX.square())).toFloat()

            val endX = startX
            val endY = (Math.sqrt(r.square() - endX.square())).toFloat()

            drawPosition.set(startX, startY)
            endPosition.set(endX, endY)
            //待变换新坐标系原点x,y
            val originX = widthScope / 2f
            val originY = heightScope / 2f
            drawPosition.rotateCoordinateSystem(directionDegree)
            endPosition.rotateCoordinateSystem(directionDegree)
            drawPosition.translationCoordinateSystem(originX, originY)
            endPosition.translationCoordinateSystem(originX, originY)

            this.startX = drawPosition.x
            this.startY = drawPosition.y
        }

        fun calcDrawPosition() : PointF {
            process += step
            process = Math.min(maxProcess, process)
            return drawPosition * process
        }

        @Suppress("unused")
        private operator fun PointF.times(process : Float) : PointF {
            return drawPosition.apply {
                x = (endPosition.x - startX) * process + startX
                y = (endPosition.y - startY) * process + startY
            }
        }

    }

}