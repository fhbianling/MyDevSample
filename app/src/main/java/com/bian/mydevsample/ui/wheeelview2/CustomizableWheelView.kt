package com.bian.mydevsample.ui.wheeelview2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bian.mydevsample.R
import com.bian.mydevsample.ui.randombubbles.toRadians
import com.bian.mydevsample.ui.wheelview.lerpColor
import com.bian.mydevsample.ui.wheelview.withAlpha
import kotlin.math.abs
import kotlin.math.cos
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
	private var radius = 0f
	private var corner = 0f
	val values = mutableMapOf<Float, Float>()

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		angleStep = 180f / itemCount
		rotateXBase = 0f
		radius = measuredHeight / 2f
		itemHeight = radius * sin((angleStep / 2f).toRadians()) * 2
		drawRect.set(0f, 0f, measuredWidth.toFloat(), itemHeight)
		corner = (180f - angleStep) / 2
	}

	override fun onDraw(canvas : Canvas?) {
		canvas ?: return
		rotateWheelDraw(canvas)
		drawDebugLine(canvas)
		if (autoRotate) {
			rotateXBase += 0.2f
			if (rotateXBase <= 360f) {
				invalidate()
			}
		}
	}

	private fun drawDebugLine(canvas : Canvas) {
		val l = 0f
		val r = measuredWidth.toFloat()
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = 2f
		paint.pathEffect = null
		for (i in 0 until itemCount) {
			val topAngle = angleStep * i
			val bottomAngle = topAngle + angleStep
			val t1 = radius - radius * cos(topAngle.toRadians())
			val t2 = radius - radius * cos(bottomAngle.toRadians())
			paint.color = Color.RED
			canvas.drawLine(l, t1 + 3f, r, t1 + 3f, paint)
			paint.color = Color.BLUE
			canvas.drawLine(l, t2 - 3f, r, t2 - 3f, paint)
		}
	}


	//	private var firstVisibleItemIndex = Pair(0, 0) // first:在数据项中的位置，second：在滚轮中的位置
	private fun rotateWheelDraw(canvas : Canvas) {
		for (wheelIndex in 0 until itemCount * 2) {
			val rotateOfWheelItem = (rotateXBase + angleStep * wheelIndex) % 360
			values[wheelIndex.toFloat()] = rotateOfWheelItem
			if (rotateOfWheelItem.isBack()) continue
			val offsetTop = radius - radius * cos(rotateOfWheelItem.toRadians())
//			canvas.drawLine(0f,)
//			drawRect.offsetTo(0f, offsetTop)
//			val alpha = if (rotateOfWheelItem > 90f) (180f - rotateOfWheelItem - angleStep) else rotateOfWheelItem
//			val dH = itemHeight - abs(itemHeight * cos((corner - alpha).toRadians()))
//			drawRect.inset(0f, dH)
//			paint.style = Paint.Style.FILL_AND_STROKE
//			paint.strokeWidth = 3f
//			paint.pathEffect = DashPathEffect(floatArrayOf(1f, 5f), 1f)
//			paint.color = ((wheelIndex + 1) / itemCount * 2f).lerpColor(Color.YELLOW,
//			                                                            Color.GREEN).withAlpha(0.3f)
//			canvas.drawRect(drawRect, paint)
//			paint.style = Paint.Style.FILL
//			paint.color = Color.WHITE
//			paint.textSize = 60f
//			canvas.drawText(wheelIndex.toString(), drawRect.centerX(), drawRect.centerY(), paint)
//			drawRect.inset(0f, - dH)
//			canvas.save()


//			mMatrix.reset()
//			mCamera.save()
//			val centerX = drawRect.centerX()
//			val centerY = drawRect.centerY()
//			val zOffset = abs(itemHeight * sin(rotateOfWheelItem.toRadians()))
//			mCamera.setLocation(0f, 0f, - absBaseZ - zOffset)
//			mCamera.rotateX(- rotateOfWheelItem)
//			mCamera.getMatrix(mMatrix)
//			mCamera.restore()
//
//			mMatrix.postTranslate(centerX, centerY)
//			mMatrix.preTranslate(- centerX, - centerY)
//			canvas.concat(mMatrix)
//			drawItem(canvas, drawRect, wheelIndex, rotateOfWheelItem % 360f)
//			canvas.restore()
		}
	}

	private fun debugDraw(canvas : Canvas) {
		for (i in 0 until itemCount) {
			val rotate = rotateXBase + angleStep * i

			paint.color = Color.BLUE
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 2f

			drawRect.offsetTo(0f, itemHeight * i)
			canvas.drawRect(drawRect, paint)
			canvas.save()

			mMatrix.reset()
			mCamera.save()
			val centerX = drawRect.centerX()
			val centerY = drawRect.centerY()
			val zOffset = abs(itemHeight * sin(rotate.toRadians()))
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
	}

	private val bitmaps by lazy {
		ids.map {
			BitmapFactory.decodeResource(resources, it)
		}
	}

	private fun Float.isBack() : Boolean {
		return this in 180f - angleStep / 2f .. 360f - angleStep / 2f
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