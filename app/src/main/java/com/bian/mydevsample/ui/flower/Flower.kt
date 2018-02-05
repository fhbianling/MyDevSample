package com.bian.mydevsample.ui.flower

import android.graphics.*
import android.view.View
import com.bian.util.core.L

/**
 * author 边凌
 * date 2018/2/5 15:31
 * 类描述：
 */
class Flower(view: View) {
    private var radius: Int = (Math.random() * 8 + 2).toInt()
    private var process: Float = 0f

    private val widthScope = view.width
    private val heightScope = view.height
    private val startX = (Math.random() * widthScope).toFloat()
    private val startY: Float = if (Math.random() > 0.3f) (Math.random() * heightScope).toFloat() else 0f
    private
    val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
        }
    }
    private
    val step = Math.random() * 0.25 + 0.05
    private
    val directionDegree = Math.random() * 65 + 15
    private
    val maxProcess = Math.random() * 0.6 + 0.4
    private
    val computer = Computer()
    val finish: Boolean get() = process < maxProcess

    fun move(canvas: Canvas) {
        debug(canvas)
    }

    private fun debug(canvas: Canvas) {
        L.d("move")
        paint.color = Color.RED
        val startX = computer.startPosition.x
        val startY = computer.startPosition.y
        val endX = computer.endPosition.x
        val endY = computer.endPosition.y
        val edgeX = computer.edgePosition.x
        val edgeY = computer.edgePosition.y

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawPath(Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }, paint)
        val effect = DashPathEffect(floatArrayOf(1f, 2f), 10f)
        paint.pathEffect = effect
        paint.color = Color.GRAY
        canvas.drawPath(Path().apply {
            moveTo(endX, endY)
            lineTo(edgeX, edgeY)
        }, paint)

        paint.textSize = 12f
        paint.strokeWidth = 0f
        paint.color = Color.BLACK
        canvas.drawText("(${startX.toInt()},${startY.toInt()})", startX, startY, paint)
        paint.color = Color.MAGENTA
        canvas.drawText("(${endX.toInt()},${endY.toInt()})", endX, endY, paint)
        val centerX = Math.abs((startX + endX) / 2)
        val centerY = Math.abs((startY + endY) / 2)
        paint.color = Color.BLUE
        canvas.drawText("maxProcess:${String.format("%.2f", maxProcess)}," +
                "directionDegree:${String.format("%.2f", directionDegree)}", centerX, centerY, paint)
        paint.color = Color.GREEN
        paint.style = Paint.Style.FILL
        val circleRadius = 4f
        canvas.drawCircle(centerX, centerY, circleRadius, paint)
        paint.color = Color.BLACK
        canvas.drawCircle(startX, startY, circleRadius, paint)
        paint.color = Color.BLUE
        canvas.drawCircle(endX, endY, circleRadius, paint)
        canvas.drawCircle(if (edgeX == 0f) edgeX + circleRadius else edgeX,
                if (edgeY == heightScope.toFloat()) edgeY - circleRadius else edgeY, circleRadius, paint)
    }

    override fun toString(): String {
        return "Flower(radius=$radius, " +
                "process=$process, " +
                "widthScope=$widthScope, " +
                "heightScope=$heightScope, " +
                "startX=$startX, " +
                "startY=$startY, " +
                "step=$step, " +
                "directionDegree=$directionDegree, " +
                "maxProcess=$maxProcess, " +
                "computer=$computer)"
    }


    inner class Computer {
        private val drawPosition = PointF()
        val startPosition by lazy {
            PointF().apply {
                x = startX
                y = startY
            }
        }

        val edgePosition by lazy {
            PointF().apply {
                val k = Math.tan(Math.toRadians(directionDegree + 90))
                val b = startPosition.y - k * startPosition.x
                val kToLeftBottom = (startPosition.y - heightScope) / (startPosition.x - 0)
                val edgePointX: Float
                val edgePointY: Float
                if (kToLeftBottom > k) {
                    edgePointX = ((heightScope - b) / k).toFloat()
                    edgePointY = heightScope.toFloat()
                } else {
                    edgePointX = 0f
                    edgePointY = b.toFloat()
                }
                x = edgePointX
                y = edgePointY
            }
        }

        val endPosition by lazy {
            PointF().apply {
                val endRate = 1 - maxProcess
                val edgePositionX = edgePosition.x
                val edgePositionY = edgePosition.y
                x = edgePositionX + (endRate * (startPosition.x - edgePositionX)).toFloat()
                y = edgePositionY - (endRate * (edgePositionY - startPosition.y)).toFloat()
            }
        }

    }
}