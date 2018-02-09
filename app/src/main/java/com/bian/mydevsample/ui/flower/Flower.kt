package com.bian.mydevsample.ui.flower

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import com.bian.mydevsample.ui.flower.Util.rotateCoordinateSystem
import com.bian.mydevsample.ui.flower.Util.translationCoordinateSystem

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
        private var step = 0f
        private var directionDegree = 0.0
        private var maxProcess = 0f
        private val drawPosition = PointF()
        private val endPosition = PointF()
        private val scatteringAngle = 30
        val finish : Boolean get() = process == maxProcess

        fun initialValue() {
            process = 0f
            directionDegree = Math.random() * scatteringAngle - scatteringAngle / 2f
            step = (Math.random() * 0.0045 + 0.0005).toFloat()
            maxProcess = (Math.random() * 0.6 + 0.4).toFloat()
            drawPosition.set(0f, 0f)

            generateRandomStartPosition()
        }

        private fun generateRandomStartPosition() {
            val r = Math.sqrt(Math.pow(widthScope.toDouble(),
                                       2.0) + Math.pow(heightScope.toDouble(), 2.0)) / 2
            //原坐标系计算出的起始点x,y
            val startX = (Math.random() * 2 * r - r).toFloat()
            val startY = (- Math.random() *
                    Math.sqrt(Math.pow(r, 2.0) - Math.pow(startX.toDouble(), 2.0))).toFloat()

            val endX = startX
            val endY = (Math.sqrt(Math.pow(r, 2.0) - Math.pow(endX.toDouble(), 2.0))).toFloat()

            drawPosition.set(startX, startY)
            endPosition.set(endX, endY)
            //待变换新坐标系原点x,y
            val originX = widthScope / 2f
            val originY = heightScope / 2f
            rotateCoordinateSystem(drawPosition, directionDegree)
            rotateCoordinateSystem(endPosition, directionDegree)
            translationCoordinateSystem(drawPosition, originX, originY)
            translationCoordinateSystem(endPosition, originX, originY)

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