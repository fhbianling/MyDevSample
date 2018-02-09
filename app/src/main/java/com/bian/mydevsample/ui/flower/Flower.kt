package com.bian.mydevsample.ui.flower

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View

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

    fun move(canvas : Canvas, paint : Paint) {
        if (computer.finish) {
            computer.initialValue()
        }
        val drawPosition = computer.getDrawPosition()
        canvas.drawCircle(drawPosition.x, drawPosition.y, radius, paint)

//        debug(canvas, paint)
    }

    private fun debug(canvas : Canvas, paint : Paint) {
        val tempColor = paint.color
        val tempStrokeWidth = paint.strokeWidth
        paint.color = Color.BLUE
        paint.strokeWidth = 3f
        canvas.drawLine(computer.startX,
                        computer.startY,
                        computer.getDrawPosition().x,
                        computer.getDrawPosition().y,
                        paint)
        paint.color = tempColor
        paint.strokeWidth = tempStrokeWidth
    }

    inner class Computer {
        private var process = 0f
        var startX = 0f
        var startY = 0f
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
            startX = (Math.random() * 2 * r - r).toFloat()
            startY = (- Math.random() * Math.sqrt(Math.pow(r, 2.0) - Math.pow(startX.toDouble(),
                                                                              2.0))).toFloat()
            val endX = startX
            val endY = (Math.sqrt(Math.pow(r, 2.0) - Math.pow(endX.toDouble(), 2.0))).toFloat()

            drawPosition.set(startX, startY)
            endPosition.set(endX, endY)
            val originX = widthScope / 2f
            val originY = heightScope / 2f
            transformPointFromRotate(drawPosition, directionDegree)
            transformPointFromRotate(endPosition, directionDegree)
            transformPointFromTranslate(drawPosition, originX, originY)
            transformPointFromTranslate(endPosition, originX, originY)

            startX = drawPosition.x
            startY = drawPosition.y
        }

        fun getDrawPosition() : PointF {
            process += step
            process = Math.min(maxProcess, process)
            return drawPosition * process
        }

        @Suppress("unused")
        private operator fun PointF.times(process : Float) : PointF {
            val dstX = (endPosition.x - startX) * process + startX
            val dstY = (endPosition.y - startY) * process + startY
            drawPosition.set(dstX, dstY)
            return drawPosition
        }

        private fun transformPointFromRotate(pointF : PointF, degree : Double) : PointF {
            val toRadians = Math.toRadians(degree)
            val cosDegree = Math.cos(toRadians)
            val sinDegree = Math.sin(toRadians)
            val x = pointF.x * cosDegree + pointF.y * sinDegree
            val y = - pointF.x * sinDegree + pointF.y * cosDegree
            pointF.set(x.toFloat(), y.toFloat())
            return pointF
        }

        private fun transformPointFromTranslate(pointF : PointF, originX : Float, originY : Float) : PointF {
            val x = pointF.x + originX
            val y = pointF.y + originY
            pointF.set(x, y)
            return pointF
        }

    }

}