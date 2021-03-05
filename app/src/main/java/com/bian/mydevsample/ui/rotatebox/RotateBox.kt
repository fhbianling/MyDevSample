package com.bian.mydevsample.ui.rotatebox

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bian.mydevsample.R

/**
 * author fhbianling@163.com
 * date 2021/3/5 15:26
 * 类描述：
 */
class RotateBox(ctx : Context, attrs : AttributeSet) : View(ctx, attrs) {
	private val boxSize = 400f

	private val mPaint = Paint()
	private val mTextPaint = Paint()
	private val mCamera = Camera()
	private val mMatrix = Matrix()
	private val baseRectF = RectF(0f, 0f, boxSize, boxSize)
	var rotateXBase = 0f
		set(value) {
			field = value
//			invalidate()
		}
	var rotateYBase = 0f
		set(value) {
			field = value
//			invalidate()
		}
	var rotateZBase = 0f
		set(value) {
			field = value
//			invalidate()
		}
	private var enableRandomRotate = false
	private val bitmaps by lazy {
		Array<Bitmap>(6) {
			BitmapFactory.decodeResource(resources, ids[it])
		}
	}

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val l = (measuredWidth - baseRectF.width()) / 2f
		val t = (measuredHeight - baseRectF.height()) / 2f
		baseRectF.offsetTo(l, t)
	}

	override fun onDraw(canvas : Canvas?) {
		canvas ?: return

		val cX = baseRectF.centerX()
		val cY = baseRectF.centerY()
		mTextPaint.textSize = 40f
		mTextPaint.color = Color.WHITE

		boxOp.forEachIndexed { index, (rX, rY, rZ, tXr, tYr, tZr) ->
			canvas.save()
			mMatrix.reset()
			mCamera.save()
//			mCamera.rotate(rotateXBase + rX, rotateYBase + rY, rotateZBase + rZ)
			mCamera.rotate(rotateXBase, rotateYBase, rotateZBase)
			mCamera.rotate(rX, rY, rZ)
//			mCamera.rotateX(rotateXBase + rX)
//			mCamera.rotateY(rotateYBase + rY)
//			mCamera.rotateZ(rotateZBase + rZ)
			mCamera.translate(tXr * boxSize, tYr * boxSize, tZr * boxSize)
			mCamera.getMatrix(mMatrix)
			mMatrix.postTranslate(cX, cY)
			mMatrix.preTranslate(- cX, - cY)
			canvas.concat(mMatrix)
			mPaint.color = Color.BLUE
			mPaint.style = Paint.Style.STROKE
			mPaint.strokeWidth = 2f
			canvas.drawRect(baseRectF, mPaint)
			val b = bitmaps[index]
			canvas.drawBitmap(b, null, baseRectF, mPaint)
			mPaint.color = Color.RED
			mPaint.strokeWidth = 5f
			canvas.drawLine(baseRectF.left, baseRectF.top, baseRectF.right, baseRectF.top, mPaint)
			mPaint.color = Color.YELLOW
			mPaint.strokeWidth = 5f
			canvas.drawLine(baseRectF.left, baseRectF.top, baseRectF.left, baseRectF.bottom, mPaint)
			canvas.drawText(index.toString(), cX, cY, mTextPaint)
			canvas.restore()
			mCamera.restore()
		}

		if (enableRandomRotate) {
			calculateRandomRotate()
			invalidate()
		}
	}

	fun randomRotate(enable : Boolean) {
		this.enableRandomRotate = enable
		invalidate()
	}

	fun reset() {
		rotateXBase = 0f
		rotateYBase = 0f
		rotateZBase = 0f
		this.enableRandomRotate = false
		invalidate()
	}

	private fun calculateRandomRotate() {
		rotateXBase += 0.1f
		rotateYBase += 0.1f
		rotateZBase += 0.1f
	}

	companion object {
		private val boxOp = listOf(
				Op(0f, 0f, 0f, 0f, 0f, 0f),
				Op(0f, 180f, 0f, 0f, 0f, - 1f),
				Op(0f, 90f, 0f, 0.5f, 0f, - 0.5f),
				Op(0f, 270f, 0f, - 0.5f, 0f, - 0.5f),
				Op(90f, 0f, 0f, 0f, 0.5f, - 0.5f),
				Op(270f, 0f, 0f, 0f, - 0.5f, - 0.5f),
		)
		private val ids = intArrayOf(
				R.mipmap.ic_1,
				R.mipmap.ic_2,
				R.mipmap.ic_3,
				R.mipmap.ic_4,
				R.mipmap.ic_5,
				R.mipmap.ic_6
		)
	}

	private data class Op(
			val rotateX : Float,
			val rotateY : Float,
			val rotateZ : Float,
			val translateXRatio : Float,
			val translateYRatio : Float,
			val translateZRatio : Float,
	)
}