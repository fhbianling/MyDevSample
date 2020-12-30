package com.bian.mydevsample.ui.randombubbles

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bian.util.core.L
import kotlinx.coroutines.*
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.min
import kotlin.system.measureTimeMillis

/**
 * author fhbianling@163.com
 * date 2020/12/30 9:05
 * 类描述：
 */
class RandomBubbles(ctx : Context, attrs : AttributeSet) : View(ctx, attrs) {
	private val bubbles = mutableListOf<Bubble>()
	private val circleMap = mutableMapOf<PointF, Float>()
	private val bubbleItems = mutableListOf<BubbleItem>()
	private var expectHeight = 0
	private val paint = Paint().also {
		it.isAntiAlias = true
		it.textSize = 14f * 3f
	}

	private suspend fun calculateExpectHeight() : Int {
		return withContext(Dispatchers.IO) {
			val bubbleItems = mutableListOf<BubbleItem>()
			bubbles.shuffle()
			val id = System.currentTimeMillis()
			L.d("calculate task[$id] is running")
			circleMap.clear()
			val initialWidth = measuredWidth
			val halfWidth = initialWidth / 2f
			val maxRadius = initialWidth / 3f
			// 向下扩展
			val xRange = 0f .. initialWidth.toFloat()
			var yRange = 0f .. initialWidth.toFloat()
			var translateTop = Float.MAX_VALUE

			for (i in 0 until bubbles.size) {
				var pointF : PointF
				var radius : Float
				var minBubbleRadius = paint.measureText(bubbles[i].text)
				if (minBubbleRadius > maxRadius) {
					minBubbleRadius = maxRadius
				}
				var loopCount = 0
				FindValidPoint@ while (true) {
					loopCount ++
					if (loopCount > 500) {
						yRange *= 1.5f
						L.d("scale y range,drop loop count:$loopCount")
						loopCount = 0
					}
					pointF = randomPoints(xRange, yRange)
					val pointMaxRadius = min(min(maxRadius, pointF.x), initialWidth - pointF.x)
					if (pointMaxRadius < minBubbleRadius || circleMap.containsKey(pointF)) continue

					radius = rangeRandom(minBubbleRadius .. pointMaxRadius)
					for ((existPoint, itsRadius) in circleMap) {
						if (pointF.distance(existPoint) < itsRadius + radius + 20f) {
							continue@FindValidPoint
						}
					}
					break
				}
				L.d("loop count:$loopCount")
				val diff = pointF.y - radius
				if (diff < 0f) {
					translateTop = min(diff, translateTop)
				}
				if (pointF.y + radius > yRange.endInclusive) {
					yRange = 0f .. pointF.y + radius
				}
				circleMap[pointF] = radius
				bubbleItems.add(BubbleItem(bubbles[i], radius, pointF))
			}
			if (translateTop != Float.MAX_VALUE) {
				circleMap.forEach {
					it.key.y = it.key.y - translateTop
				}
				yRange = 0f .. yRange.endInclusive - translateTop
				L.d("translate top")
			}
			val heightSpec = MeasureSpec.makeMeasureSpec(yRange.endInclusive.toInt(),
			                                             MeasureSpec.EXACTLY)
			this@RandomBubbles.bubbleItems.clear()
			this@RandomBubbles.bubbleItems.addAll(bubbleItems)
			L.d("calculate task[$id] is end")
			heightSpec
		}
	}

	private var job : Job? = null
	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		if (expectHeight != 0) {
			val heightSpec = MeasureSpec.makeMeasureSpec(expectHeight, MeasureSpec.EXACTLY)
			super.onMeasure(widthMeasureSpec, heightSpec)
		} else {
			super.onMeasure(widthMeasureSpec, widthMeasureSpec)
			if (job != null && ! job !!.isCompleted) {
				job?.cancel()
				L.d("cancel job")
			}
			job = GlobalScope.launch {
				val time = measureTimeMillis {
					expectHeight = calculateExpectHeight()
				}
				L.d("cost time:$time")
				withContext(Dispatchers.Main) {
					requestLayout()
				}
			}
		}
	}

	private var lastEvent = PointF()

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event : MotionEvent?) : Boolean {
		when (event?.action) {
			MotionEvent.ACTION_DOWN -> {
				lastEvent.set(event.x, event.y)
				return true
			}
			MotionEvent.ACTION_MOVE -> {
				val diff = event.y - lastEvent.y
				translationX
			}
		}
		return super.onTouchEvent(event)
	}

	override fun onDraw(canvas : Canvas?) {
		if (canvas == null) return
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = 5f
		val lineHeight = paint.descent() - paint.ascent()
		L.d("bubble item size:${bubbleItems.size}")
		bubbleItems.forEach { (bubble, radius, center) ->
			paint.color = bubble.color
			canvas.drawCircle(center.x, center.y, radius, paint)
			val measureWidth = paint.measureText(bubble.text)
			paint.color = Color.WHITE
			canvas.drawText(bubble.text,
			                (center.x - radius + (2 * radius - measureWidth) / 2f),
			                center.y + (lineHeight) / 2f - paint.descent(),
			                paint)
		}
	}

	fun setBubbles(bubbles : List<Bubble>) {
		L.d("set bubbles:${bubbles.size}")
		this.bubbles.clear()
		this.bubbles.addAll(bubbles)
		bubbleItems.clear()
		expectHeight = 0
		requestLayout()
	}

	private fun randomPoints(rangeX : ClosedFloatingPointRange<Float>, rangeY : ClosedFloatingPointRange<Float>) : PointF {
		return PointF(rangeRandom(rangeX), rangeRandom(rangeY))
	}

	private fun rangeRandom(range : ClosedFloatingPointRange<Float>) : Float {
		return (Math.random() * (range.endInclusive - range.start)).toFloat() + range.start
	}

	private fun PointF.distance(p : PointF) : Float {
		return hypot(x - p.x, y - p.y)
	}

	data class Bubble(val text : String, val color : Int)
	data class BubbleItem(val bubble : Bubble, val radius : Float, val p : PointF)

	private operator fun ClosedFloatingPointRange<Float>.times(scale : Float) = start * scale .. endInclusive * scale
}
