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
//        debug(canvas)
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
        private var left = 0f
        private var top = 0f
        private var right = widthScope.toFloat()
        private var bottom = heightScope.toFloat()
        private var step = 0f
        private var directionDegree = 0.0
        private var maxProcess = 0f
        private var drawPosition = PointF()
        private var endX = startX
        private var endY = startY
        val finish : Boolean get() = process == maxProcess

        fun initialValue() {
            generateRandomInitCondition()
            computeBoundaryCondition()
        }

        private fun generateRandomInitCondition() {
            process = 0f

            directionDegree = 90 + 1 * Math.random()
            generateRandomStartPosition()
            step = (Math.random() * 0.0045 + 0.0005).toFloat()
            maxProcess = (Math.random() * 0.6 + 0.4).toFloat()
            drawPosition.set(0f, 0f)
            endX = startX
            endY = startY
        }

        private fun generateRandomStartPosition() {
            val aHalfScopeDiff = Math.abs(widthScope - heightScope) / 2f
            val maxScope = Math.max(widthScope, heightScope)
            val enlargeScopeMin = - aHalfScopeDiff
            val enlargeScopeMax = maxScope - aHalfScopeDiff

            val randomEnlargedScope = (Math.random() * (enlargeScopeMax - enlargeScopeMin) + enlargeScopeMin).toFloat()


            if (widthScope > heightScope) {
                startX = (Math.random() * maxScope).toFloat()
                startY = randomEnlargedScope
                left = 0f
                right = maxScope.toFloat()
                top = enlargeScopeMin
                bottom = enlargeScopeMax
            } else {
                startX = randomEnlargedScope
                startY = (Math.random() * maxScope).toFloat()
                left = enlargeScopeMin
                right = enlargeScopeMax
                top = 0f
                bottom = maxScope.toFloat()
            }
        }

        private fun computeBoundaryCondition() {
            val k = Math.tan(Math.toRadians(directionDegree))
            val b = startY - k * startX

            //下边界交点
            val edge1Y = bottom
            val edge1X = (edge1Y - b) / k

            //左边界或右边界交点
            val edge2X = if (k > 0) right else left
            val edge2Y = (edge2X * k + b).toFloat()

            if (edge2Y > bottom) {
                endX = edge1X.toFloat()
                endY = edge1Y
            } else {
                endX = edge2X
                endY = edge2Y
            }

            drawPosition.set(startX, startY)
        }

        fun getDrawPosition() : PointF {
            process += step
            process = Math.min(maxProcess, process)
            return drawPosition * process
        }

        @Suppress("unused")
        private operator fun PointF.times(process : Float) : PointF {
            val dstX = ((endX - startX) / maxProcess) * process + startX
            val dstY = ((endY - startY) / maxProcess) * process + startY
            drawPosition.set(dstX, dstY)
            return drawPosition
        }

        override fun toString() : String {
            return "Computer(process=$process, startX=$startX, startY=$startY, step=$step, directionDegree=$directionDegree, maxProcess=$maxProcess, endX=$endX, endY=$endY)"
        }


    }
}