package com.bian.mydevsample.ui.randombubbles

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bian.util.core.L
import kotlin.math.min

/**
 * author fhbianling@163.com
 * date 2020/12/29 16:11
 * 类描述：
 */

class RandomBubblesView(ctx : Context, attrs : AttributeSet) : View(ctx, attrs) {
	private val density = 4
	private val bubbleItems = mutableListOf<BubbleItem>()
	private var initialRectF = RectF()
	private val paint = Paint().also {
		it.isAntiAlias = true
	}
	private var voronoiMesh : VoronoiMesh? = null
	private val circleMap = mutableMapOf<GLPointF, Float>()
	private val drawDebug = true

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		L.d("on measure")
		if (initialRectF.right == 0f || initialRectF.bottom == 0f) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
			initialRectF.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
		} else {
			val scale = bubbleItems.size / density
			val heightSpec = MeasureSpec.makeMeasureSpec((initialRectF.height() * scale).toInt(),
			                                             MeasureSpec.EXACTLY)
			super.onMeasure(widthMeasureSpec, heightSpec)
		}
		var count = 0
		while (circleMap.size != bubbleItems.size) {
			circleMap.clear()
			voronoiMesh = VoronoiMesh.createRandom(bubbleItems.size * 3,
			                                       0f .. measuredWidth.toFloat(),
			                                       0f .. measuredHeight.toFloat())
			L.d("voronoi primitives:${voronoiMesh !!.voronoiPrimitives.size}")
			for (i in 0 .. 3) {
				voronoiMesh?.lloydsRelax()
			}
			voronoiMesh !!.voronoiPrimitives.forEach {
				var minDistance = Float.MAX_VALUE
				it.points.forEach { p ->
					val d = it.centroid !!.distance(p)
					minDistance = min(d, minDistance)
				}
				if (circleMap.size != bubbleItems.size) {
					tryPick(it.centroid !!, minDistance)
				}
			}
			count ++
		}
		L.d("use voronoi,count:$count")
	}

	override fun onDraw(canvas : Canvas?) {
		L.d("on draw")
		if (voronoiMesh == null) return
		val c = canvas ?: return
		if (drawDebug) {
			drawDebug(c)
		}
		if (bubbleItems.size == 0 || circleMap.isEmpty()) return
		var index = 0
		circleMap.forEach {
			val p = it.key
			paint.color = bubbleItems.getOrNull(index)?.color ?: Color.RED
			c.drawCircle(p.x, p.y, it.value, paint)
			index ++
		}
	}

	private fun drawDebug(c : Canvas) {
		paint.color = Color.parseColor("#8000ff00")
		c.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
		paint.color = Color.WHITE
		L.d("real draw")
		voronoiMesh?.edges?.forEach {
			c.drawLine(it.p0.x, it.p0.y, it.p1.x, it.p1.y, paint)
		}
		paint.color = Color.YELLOW
		voronoiMesh?.tris?.forEach {
			it.edges.forEach { edge ->
				c.drawLine(edge.p0.x, edge.p0.y, edge.p1.x, edge.p1.y, paint)
			}
		}
		voronoiMesh !!.voronoiPrimitives.forEach {
			paint.color = Color.BLUE
			c.drawCircle(it.centroid !!.x, it.centroid !!.y, 10f, paint)
			paint.color = Color.BLACK
			it.points.forEach { p ->
				c.drawCircle(p.x, p.y, 5f, paint)
			}
		}
	}

	private fun tryPick(glPointF : GLPointF, radius : Float) : Boolean {
		if (circleMap[glPointF] != null) return false
		if (glPointF.x !in radius .. measuredWidth - radius) return false
		if (glPointF.y !in radius .. measuredHeight - radius) return false
		circleMap.forEach {
			if (it.key.distance(glPointF) < it.value + radius)
				return false
		}
		circleMap[glPointF] = radius
		return true
	}

	fun setBubbleItems(bubbleItems : List<BubbleItem>) {
		L.d("set bubble items,count:${bubbleItems.size}")
		this.bubbleItems.clear()
		this.bubbleItems.addAll(bubbleItems)
		requestLayout()
	}

	data class BubbleItem(val text : String, val color : Int)
}