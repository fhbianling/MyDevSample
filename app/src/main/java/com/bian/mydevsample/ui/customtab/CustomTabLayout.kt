package com.bian.mydevsample.ui.customtab

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.math.MathUtils
import com.bian.mydevsample.tool.EvaluateTool.evaluate
import com.bian.mydevsample.tool.EvaluateTool.evaluateColor
import kotlin.math.abs

/**
 * author fhbianling@163.com
 * date 2020/12/3 9:32
 * 类描述：
 */
data class TabItem(
		@DrawableRes val icon : Int,
		val info : String,
		val colorShallow : Int,
		val colorDeep : Int,
		val colorCollapse : Int = Color.WHITE
)

private const val SQUARE_MAX_SIZE = 180f
private const val SQUARE_MIN_SIZE = 120f
private const val TEXT_PART_WIDTH = 240f
private const val SQUARE_ROUND = 15f
private const val ICON_SIZE = 60
private const val TEXT_FONT = 36f
private const val TEXT_MOVE_OFFSET = 60
private const val TEXT_COLOR = Color.WHITE
private const val ITEM_MARGIN = 40
private const val AUTO_ANIM_DURATION = 100L
private const val FORCE_SLIDE_NEXT_SPEED = 5
private const val START_X_OFFSET = 100f
private const val CALC_FIXED_MIN_ITEM_WIDTH = SQUARE_MAX_SIZE + ITEM_MARGIN
private const val CALC_SQUARE_LEFT_MARGIN = (SQUARE_MAX_SIZE - SQUARE_MIN_SIZE) / 2f

class CustomTabLayout(ctx : Context, attrSet : AttributeSet) : View(ctx, attrSet) {
	private var yCenter : Float = - 1f

	var items = listOf<TabItem>()
		set(value) {
			field = value
			updateItemDrawers()
		}

	private val itemDrawers = mutableListOf<ItemDrawer>()
	private var ratio = 0f
	private val paint = Paint().also {
		it.isAntiAlias = true
		it.textSize = TEXT_FONT
	}

	var onTabSelect : ((TabItem, Int) -> Unit)? = null

	private var xAnchor = 0f
	private var selectIndex = - 1
	override fun onDraw(canvas : Canvas?) {
		canvas?.apply {
			canvas.translate(START_X_OFFSET, 0f)
			var xStart = xAnchor
			selectIndex = (abs(xAnchor) / CALC_FIXED_MIN_ITEM_WIDTH.toDouble()).toInt()
			onTabSelect?.invoke(items[selectIndex], selectIndex)

			Log.d("CustomTabLayout", "select:$selectIndex")
			ratio = (abs(xAnchor) % CALC_FIXED_MIN_ITEM_WIDTH) / CALC_FIXED_MIN_ITEM_WIDTH
			ratio = MathUtils.clamp(ratio, 0f, 1f)
			itemDrawers.forEachIndexed { index, itemDrawer ->
				xStart = when (index) {
					selectIndex -> {
						itemDrawer.draw(canvas, paint, xStart, 1 - ratio) + ITEM_MARGIN
					}
					selectIndex + 1 -> {
						itemDrawer.draw(canvas, paint, xStart, ratio) + ITEM_MARGIN
					}
					else            -> {
						itemDrawer.draw(canvas, paint, xStart, 0f) + ITEM_MARGIN
					}
				}
			}
		}
	}

	private val lastEvent : PointF = PointF()
	private var minXAnchor = - 1f
	private var speed = - 1f

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event : MotionEvent?) : Boolean {
		when (event?.action) {
			MotionEvent.ACTION_DOWN -> {
				lastEvent.set(event.x, event.y)
				return true
			}
			MotionEvent.ACTION_UP -> {
				startAutoAnim()
				return true
			}
			MotionEvent.ACTION_MOVE -> {
				val diffX = event.x - lastEvent.x
				xAnchor += diffX
				xAnchor = safeAnchor(xAnchor)
				speed = event.x - lastEvent.x
				lastEvent.set(event.x, event.y)
				invalidate()
				return true
			}
		}
		return super.onTouchEvent(event)
	}

	private var valueAnimator : ValueAnimator? = null
	private fun startAutoAnim() {
		val start = xAnchor
		var end = xAnchor

		val abs = abs(xAnchor)
		if (abs % CALC_FIXED_MIN_ITEM_WIDTH > 0.5) {
			val leftBorder = - (abs + CALC_FIXED_MIN_ITEM_WIDTH - abs % CALC_FIXED_MIN_ITEM_WIDTH)
			val rightBorder = - (abs - abs % CALC_FIXED_MIN_ITEM_WIDTH)
			if (abs(speed) > FORCE_SLIDE_NEXT_SPEED) {
				// 进入条件方法体就意味着此时speed只有>0或者<0两种情况，没有==0的情况，所以不用考虑
				end = if (speed > 0) {
					// 速度快时，强制右滑item
					rightBorder
				} else {
					// 速度快时，强制左滑item
					leftBorder
				}
			} else {
				end = if (ratio > 0.5f) {
					leftBorder
				} else {
					rightBorder
				}
			}
		}
		end = safeAnchor(end)
		if (start == end) return onTabSelect?.invoke(items[selectIndex], selectIndex) ?: Unit

		valueAnimator?.cancel()
		valueAnimator = ValueAnimator.ofFloat(start, end)
		valueAnimator?.duration = AUTO_ANIM_DURATION
		valueAnimator?.addUpdateListener { va ->
			xAnchor = va.animatedValue as Float
			invalidate()
		}
		valueAnimator?.start()
	}

	private fun safeAnchor(value : Float) : Float {
		var result = 0f.coerceAtMost(value)
		result = minXAnchor.coerceAtLeast(result)
		return result
	}

	private fun updateItemDrawers() {
		itemDrawers.clear()
		items.forEach {
			itemDrawers.add(ItemDrawer(it, context.resources, yCenter))
		}
		invalidate()
	}

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		yCenter = measuredHeight / 2f
		updateItemDrawers()
		val size = itemDrawers.size
		minXAnchor =
				- (CALC_FIXED_MIN_ITEM_WIDTH * (size - 1))
	}

	private class ItemDrawer(val tabItem : TabItem, resource : Resources, val yCenter : Float) {
		private val rectOfSquare = RectF()
		private val rectOfIcon = RectF().also {
			it.top = yCenter - ICON_SIZE / 2f
			it.bottom = yCenter + ICON_SIZE / 2f
		}
		private val rectOfBackground = RectF()

		private val bitmap by lazy { BitmapFactory.decodeResource(resource, tabItem.icon) }

		private var textWidth : Float = - 1f
		private var baseLineOfText : Float = - 1f

		fun draw(canvas : Canvas, paint : Paint, xStart : Float, ratio : Float) : Float {
			drawBackground(ratio, xStart, paint, canvas)
			val leftPosOfIcon = drawSquare(ratio, xStart, paint, canvas)
			drawIcon(ratio, paint, leftPosOfIcon, canvas)
			drawText(ratio, paint, canvas)
			return rectOfBackground.right
		}

		private fun drawText(ratio : Float, paint : Paint, canvas : Canvas) {
			if (ratio >= 0.7f) {
				val fixedRatio = (ratio - 0.7f) / 0.3f
				val centerOfText = rectOfSquare.right + TEXT_PART_WIDTH / 2f
				if (textWidth == - 1f) {
					textWidth = paint.measureText(tabItem.info)
				}
				val leftMax = centerOfText - textWidth / 2f + TEXT_MOVE_OFFSET
				val leftMin = centerOfText - textWidth / 2f
				val leftOfText = evaluate(fixedRatio, leftMax, leftMin)
				if (baseLineOfText == - 1f) {
					val textHeight = paint.fontMetrics.bottom - paint.fontMetrics.top
					baseLineOfText = yCenter + textHeight / 2f - paint.fontMetrics.bottom
				}
				paint.color = TEXT_COLOR
				paint.alpha = (fixedRatio * 255).toInt()
				canvas.drawText(tabItem.info, leftOfText, baseLineOfText, paint)
			}
			paint.alpha = 255
		}

		private fun drawIcon(ratio : Float, paint : Paint, leftPosOfIcon : Float, canvas : Canvas) {
			val colorOfIconTint = evaluateColor(ratio, tabItem.colorShallow, tabItem.colorCollapse)
			val porterDuffColorFilter =
					PorterDuffColorFilter(colorOfIconTint, PorterDuff.Mode.MULTIPLY)
			paint.colorFilter = porterDuffColorFilter
			rectOfIcon.left = leftPosOfIcon
			rectOfIcon.right = leftPosOfIcon + ICON_SIZE
			canvas.drawBitmap(bitmap, null, rectOfIcon, paint)
			paint.colorFilter = null
		}

		private fun drawSquare(ratio : Float, xStart : Float, paint : Paint, canvas : Canvas) : Float {
			val colorOfSquare = evaluateColor(ratio, tabItem.colorCollapse, tabItem.colorDeep)
			val leftPosOfSquare = evaluate(ratio, 0f, CALC_SQUARE_LEFT_MARGIN) + xStart
			val squareWidth = evaluate(ratio, SQUARE_MAX_SIZE, SQUARE_MIN_SIZE)
			val leftPosOfIcon = leftPosOfSquare + (squareWidth - ICON_SIZE) / 2f
			rectOfSquare.left = leftPosOfSquare
			rectOfSquare.right = leftPosOfSquare + squareWidth
			rectOfSquare.top = yCenter - squareWidth / 2f
			rectOfSquare.bottom = yCenter + squareWidth / 2f
			paint.color = colorOfSquare
			canvas.drawRoundRect(rectOfSquare, SQUARE_ROUND, SQUARE_ROUND, paint)
			return leftPosOfIcon
		}

		private fun drawBackground(ratio : Float, xStart : Float, paint : Paint, canvas : Canvas) {
			val colorOfBackground =
					evaluateColor(ratio, tabItem.colorCollapse, tabItem.colorShallow)
			val width = evaluate(
					ratio, SQUARE_MAX_SIZE,
					SQUARE_MAX_SIZE + CALC_SQUARE_LEFT_MARGIN * 2 + TEXT_PART_WIDTH
			)
			val height = SQUARE_MAX_SIZE
			rectOfBackground.left = xStart
			rectOfBackground.right = xStart + width
			rectOfBackground.top = yCenter - height / 2f
			rectOfBackground.bottom = yCenter + height / 2f
			paint.style = Paint.Style.FILL
			paint.color = colorOfBackground
			canvas.drawRoundRect(
					rectOfBackground, CALC_SQUARE_LEFT_MARGIN,
					CALC_SQUARE_LEFT_MARGIN, paint
			)
		}

	}
}
