package com.bian.mydevsample.ui.wheeelview2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bian.mydevsample.ui.wheelview.withAlpha

/**
 * author fhbianling@163.com
 * date 2021/2/25 17:23
 * 类描述：
 */
class WheelView2(ctx : Context, attr : AttributeSet) : View(ctx, attr) {
	private val paint by lazy {
		Paint().also {
			it.isAntiAlias = true
			it.textSize = 40f
		}
	}
	private val mCamera by lazy { Camera() }
	val mMatrix by lazy { Matrix() }
	var rectSize = 300f
		set(value) {
			field = value
			requestLayout()
		}

	var rotateX : Float = 0f
		set(value) {
			field = value
			invalidate()
		}
	var rotateY : Float = 0f
		set(value) {
			field = value
			invalidate()
		}
	var rotateZ : Float = 0f
		set(value) {
			field = value
			invalidate()
		}

	var locationX : Float = 0f
		set(value) {
			field = value
			invalidate()
		}
	var locationY : Float = 0f
		set(value) {
			field = value
			invalidate()
		}
	var locationZ : Float = 0f
		set(value) {
			field = value
			invalidate()
		}

	private val drawRect = RectF()

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		drawRect.set(0f, 0f, rectSize, rectSize)
		val s = (measuredWidth - rectSize) / 2f
		val t = (measuredHeight - rectSize) / 2f
		drawRect.offsetTo(s, t)
	}

	override fun onDraw(canvas : Canvas?) {
		canvas ?: return
		paint.color = Color.BLUE
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = 2f
		canvas.drawRect(drawRect, paint)
		canvas.save()
		mCamera.save()
		mCamera.setLocation(locationX, locationY, locationZ)
		mCamera.rotateX(rotateX)
		mCamera.rotateY(rotateY)
		mCamera.rotateZ(rotateZ)
		mCamera.getMatrix(mMatrix)
		mCamera.restore()
		canvas.concat(mMatrix)
		paint.style = Paint.Style.FILL_AND_STROKE
		paint.color = Color.RED.withAlpha(0.3f)
		canvas.drawRect(drawRect, paint)
		paint.color = Color.BLACK
		canvas.restore()
	}
}