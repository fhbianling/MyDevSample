package com.bian.mydevsample.ui.flower

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * author 边凌
 * date 2018/2/9 14:06
 * 类描述：
 */
private const val MIN_SIDE_LENGTH = 100f

class OvalDegreeIndicator(context : Context?, attrs : AttributeSet?) : View(context, attrs) {

    var angle : Double = 0.0
        set(value) {
            field = value
            invalidate()
        }
    var angelChangeListener : ((Double) -> Unit)? = null
    private var radius = 0f

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }
    private val strokeWidth = 5f
    private val offSet = 8f
    private val indicatorRadius = strokeWidth + offSet / 2 - 1
    private val indicatorPoint = PointF()
    private val centerX by lazy {
        width / 2f
    }

    private val centerY by lazy {
        height / 2f
    }

    override fun onDraw(canvas : Canvas?) {
        indicatorPoint.set(radius, 0f)
        indicatorPoint.rotateCoordinateSystem(- angle)
        indicatorPoint.translationCoordinateSystem(centerX, centerY)

        paint.color = Color.WHITE
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        canvas?.drawCircle(centerX, centerY, radius, paint)
        canvas?.drawLine(centerX, centerY, indicatorPoint.x, indicatorPoint.y, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.BLUE
        canvas?.drawCircle(indicatorPoint.x, indicatorPoint.y, indicatorRadius, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event : MotionEvent?) : Boolean {
        return calcAngel(event) || super.onTouchEvent(event)
    }

    private fun calcAngel(event : MotionEvent?) : Boolean {
        event?.let {
            angle = Math.toDegrees(Math.atan2(event.y - centerY.toDouble(),
                                              event.x - centerX.toDouble()))
            if (angle < 0) {
                angle += 360
            }
            angelChangeListener?.invoke(angle)
            invalidate()
            return true
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var measuredSideLength = Math.min(widthSize, heightSize).toFloat()
        measuredSideLength = Math.max(measuredSideLength, MIN_SIDE_LENGTH)
        radius = measuredSideLength / 2 - strokeWidth / 2 - offSet
        setMeasuredDimension(measuredSideLength.toInt(), measuredSideLength.toInt())
    }
}