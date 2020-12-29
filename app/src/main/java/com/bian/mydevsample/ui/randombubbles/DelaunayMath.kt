package com.bian.mydevsample.ui.randombubbles

import kotlin.math.sqrt
import kotlin.math.tan

object DelaunayMath {
	fun generateDelaunayTris(
			pointSize : Int,
			xStart : Float, xEnd : Float, yStart : Float, yEnd : Float
	) : Pair<List<Tri>, Tri> {
		val points = generateRandomPoints(pointSize, xStart, xEnd, yStart, yEnd)
		val superTri = calculateSuperTri(xStart, xEnd, yStart, yEnd)
		val tris = processDelaunayTri(points, superTri)
		return Pair(tris, superTri)
	}

	fun processDelaunayTri(points : Array<GLPointF>, superTri : Tri) : List<Tri> {
		val tris = mutableListOf<Tri>()
		tris.add(superTri)
		val edges = mutableListOf<Edge>()
		val repeatEdges = mutableListOf<Edge>()
		val newAddTris = mutableListOf<Tri>()
		points.forEach { p ->
			edges.clear()
			repeatEdges.clear()
			newAddTris.clear()
			// 找到已经生成的DelaunayTri中包含该点的那些tri
			val containPointTris = tris.filter { it.containsPoint(p) }
			// 从tris的总集合中删除这些tris，因为接下来会针对它们全部重新生成DelaunayTri
			tris.removeAll(containPointTris)

			// 找到tris中所有的公共边
			containPointTris.forEach { tri ->
				tri.edges.forEach { edge ->
					edge?.apply {
						if (edges.contains(edge)) {
							repeatEdges.add(edge)
						} else {
							edges.add(edge)
						}
					}
				}
			}
			// 删除公共边
			edges.removeAll(repeatEdges)

			// 用剩余的边和插入点重新生成DelaunayTri
			edges.forEach {
				newAddTris.add(Tri(it.p0, it.p1, p))
			}
			// 进行LOP优化，调整对角线
			val optimized = lopProcess(newAddTris)
			tris.addAll(optimized)
		}
		return tris
	}

	// LOP(Local Optimization Procedure) 局部优化
	private fun lopProcess(newAddTris : MutableList<Tri>) : List<Tri> {
		for (i in 0 until newAddTris.size) {
			for (j in (i + 1) until newAddTris.size) {
				val triI = newAddTris[i]
				val triJ = newAddTris[j]
				val edge = triI.findSameEdge(triJ) ?: continue
				val isTriICircleMax = triI.radius > triJ.radius
				val tri = if (isTriICircleMax) triI else triJ
				val outsidePI = triI.notOnEdgePoint(edge) ?: continue
				val outsidePJ = triJ.notOnEdgePoint(edge) ?: continue
				val p0 = if (isTriICircleMax) outsidePJ else outsidePI
				val distance = tri.center.distance(p0)
				if (distance < tri.radius) {
					// 需要修正对角线
					val p1 = if (isTriICircleMax) outsidePI else outsidePJ
					val p2 = edge.p0
					val p3 = edge.p1
					triI.p0 = p2
					triI.p1 = p0
					triI.p2 = p1
					triI.updateEdges()
					triJ.p0 = p3
					triJ.p1 = p0
					triJ.p2 = p1
					triJ.updateEdges()
				}
			}
		}
		return newAddTris
	}

	private fun calculateSuperTri(xStart : Float, xEnd : Float, yStart : Float, yEnd : Float) : Tri {
		val lt = GLPointF(xStart, yEnd)
		val rt = GLPointF(xEnd, yEnd)
		val lb = GLPointF(xStart, yStart)
		val rb = GLPointF(xEnd, yStart)
		val hLength = lt.distance(rt)
		val vLength = lt.distance(lb)
		val vOffset = hLength * 0.5f * tan(60f.toRadians())
		val hOffset = vLength * tan(30f.toRadians())
		val offsetP = GLPointF(hOffset, 0f)
		val p1 = (lt + (rt - lt) / 2f) + GLPointF(0f, vOffset)
		val p2 = lb - offsetP
		val p3 = rb + offsetP
		return Tri(p1, p2, p3)
	}

	private fun generateRandomPoints(
			pointSize : Int,
			xStart : Float, xEnd : Float, yStart : Float, yEnd : Float
	) : Array<GLPointF> {
		val xScalar = xEnd - xStart
		val yScalar = yEnd - yStart
		val random = {
			val x = xStart + xScalar * Math.random()
			val y = yStart + yScalar * Math.random()
			GLPointF(x.toFloat(), y.toFloat())
		}
		val glPoints = Array(pointSize) { GLPointF.zero() }
		for (i in 0 until pointSize) {
			glPoints[i] = random.invoke()
		}
		return glPoints
	}

	class Tri(var p0 : GLPointF, var p1 : GLPointF, var p2 : GLPointF) {
		//外接圆圆心
		var center = GLPointF.zero()
			private set

		//外接圆半径
		var radius = 0f
			private set
		var edges : Array<Edge>

		init {
			edges = update()
		}

		fun updateEdges() {
			edges = update()
		}

		private fun update() : Array<Edge> {
			val (x0, y0) = p0
			val (x1, y1) = p1
			val (x2, y2) = p2
			val x4 =
					((y1 - y0) * (y2 * y2 - y0 * y0 + x2 * x2 - x0 * x0) -
							(y2 - y0) * (y1 * y1 - y0 * y0 + x1 * x1 - x0 * x0)) /
							(2.0 * ((x2 - x0) * (y1 - y0) - (x1 - x0) * (y2 - y0)))
			val y4 =
					((x1 - x0) * (x2 * x2 - x0 * x0 + y2 * y2 - y0 * y0) -
							(x2 - x0) * (x1 * x1 - x0 * x0 + y1 * y1 - y0 * y0)) /
							(2.0 * ((y2 - y0) * (x1 - x0) - (y1 - y0) * (x2 - x0)))
			val r = sqrt((x0 - x4) * (x0 - x4) + (y0 - y4) * (y0 - y4))
			center.x = x4.toFloat()
			center.y = y4.toFloat()
			radius = r.toFloat()
			return arrayOf(Edge(p0, p1),
			               Edge(p0, p2),
			               Edge(p1, p2))
		}

		// 找到不在这条边上的那个点,传入的edge应为三角形三边之一
		fun notOnEdgePoint(edge : Edge) : GLPointF? {
			if (edge == edges[0]) return p2
			if (edge == edges[1]) return p1
			if (edge == edges[2]) return p0
			return null
		}

		fun findSameEdge(tri : Tri) : Edge? {
			edges.forEach { e1 ->
				tri.edges.forEach { e2 ->
					if (e1 == e2) {
						return e1
					}
				}
			}
			return null
		}

		fun containsPoint(p : GLPointF) = center.distance(p) < radius
	}

	data class Edge(var p0 : GLPointF, var p1 : GLPointF) {

		override fun equals(other : Any?) : Boolean {
			if (other == null) return false
			if (this === other) return true
			if (javaClass != other.javaClass) return false

			other as Edge

			return (p0 == other.p0 && p1 == other.p1) ||
					(p0 == other.p1 && p1 == other.p0)
		}

		override fun hashCode() : Int {
			var result = p0.hashCode()
			result = 31 * result + p1.hashCode()
			return result
		}
	}
}

fun Number.toRadians() : Float {
	return Math.toRadians(this.toDouble()).toFloat()
}