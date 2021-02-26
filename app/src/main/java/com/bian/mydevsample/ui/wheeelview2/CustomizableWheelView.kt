package com.bian.mydevsample.ui.wheeelview2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bian.mydevsample.R
import com.bian.mydevsample.ui.randombubbles.toRadians
import com.bian.mydevsample.ui.wheelview.withAlpha
import kotlin.math.abs
import kotlin.math.sin

/**
 * author fhbianling@163.com
 * date 2021/2/25 17:23
 * 类描述：
 */
class CustomizableWheelView(ctx : Context, attr : AttributeSet) : View(ctx, attr) {
	private val paint by lazy {
		Paint().also {
			it.isAntiAlias = true
			it.textSize = 40f
		}
	}
	private val mCamera by lazy { Camera() }
	private val mMatrix by lazy { Matrix() }

	var autoRotate : Boolean = false
		set(value) {
			field = value
			values.clear()
			invalidate()
		}

	private val drawRect = RectF()

	private val itemCount = 5
	private var itemHeight = 0f
	private var angleStep = 0f
	private var rotateXBase : Float = 0f
	private var absBaseZ = 8f
	val values = mutableMapOf<Float, Float>()

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		itemHeight = measuredHeight / itemCount.toFloat()
		angleStep = 180f / itemCount
		drawRect.set(0f, 0f, measuredWidth.toFloat(), itemHeight)
		rotateXBase = - angleStep * ((itemCount) / 2)
	}

	override fun onDraw(canvas : Canvas?) {
		canvas ?: return
		for (i in 0 until itemCount) {
			val rotate = rotateXBase + angleStep * i

			paint.color = Color.BLUE
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 2f
			drawRect.offsetTo(0f, itemHeight * i)
			canvas.drawRect(drawRect, paint)
			canvas.save()

			val zOffset = abs(itemHeight * sin(rotate.toRadians()))
			mMatrix.reset()
			mCamera.save()
			val centerX = drawRect.centerX()
			val centerY = drawRect.centerY()
			mCamera.setLocation(0f, 0f, - absBaseZ - zOffset)
			mCamera.rotateX(- rotate)
			mCamera.getMatrix(mMatrix)
			mCamera.restore()

			mMatrix.postTranslate(centerX, centerY)
			mMatrix.preTranslate(- centerX, - centerY)
			canvas.concat(mMatrix)
			drawItem(canvas, drawRect, i, rotate % 360f)
			canvas.restore()
		}
		if (autoRotate) {
			rotateXBase += 1f
			rotateXBase %= 360f
			invalidate()
		}
	}

	private val bitmaps by lazy {
		ids.map {
			BitmapFactory.decodeResource(resources, it)
		}
	}

	private fun Float.isBack() : Boolean {
		return this in 90f .. 270f
	}

	private val r = RectF()
	private fun drawItem(canvas : Canvas, rectF : RectF, i : Int, rotateX : Float) {
		paint.style = Paint.Style.FILL_AND_STROKE
		val isBack = rotateX.isBack()
		val color = if (isBack) Color.GRAY else Color.MAGENTA
		paint.color = color.withAlpha((i + 1) / itemCount.toFloat())
		canvas.drawRect(drawRect, paint)
		paint.color = if (isBack) Color.LTGRAY else Color.WHITE
		paint.textSize = 56f
		val text = "$i:${rotateX}°"
		canvas.drawText(text, drawRect.centerX(), drawRect.centerY(), paint)
		val b = bitmaps[i % 5]
		val w = rectF.height() - 20f
		val h = w * b.height / b.width
		val t = (rectF.height() - h) / 2f + rectF.top
		r.set(10f, t, 10f + w, t + h)
		if (isBack) {
			paint.colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.ADD)
		}
		canvas.drawBitmap(b, null, r, paint)
		paint.colorFilter = null
	}

	companion object {

		private val ids = intArrayOf(
				R.mipmap.ic_1,
				R.mipmap.ic_2,
				R.mipmap.ic_3,
				R.mipmap.ic_4,
				R.mipmap.ic_5,
		)
	}
}