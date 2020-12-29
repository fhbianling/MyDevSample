package com.bian.mydevsample.ui.randombubbles

import android.util.Log
import com.bian.mydevsample.ui.randombubbles.DelaunayMath.Edge
import com.bian.mydevsample.ui.randombubbles.DelaunayMath.Tri
import com.bian.util.core.L
import java.nio.FloatBuffer
import kotlin.system.measureTimeMillis

/**
 * author fhbianling@163.com
 * date 2020/8/6 14:04
 * 类描述：
 */
class VoronoiMesh private constructor(
		var tris : List<Tri>,
		tri : Tri,
		private val rect : Rect
) {
	var size = - 1
	val voronoiPrimitives = mutableListOf<VoronoiPrimitive>()
	var edges : Collection<Edge>? = null
	private val baseSuperTri = Tri(tri.p0.copy(), tri.p1.copy(), tri.p2.copy())

	init {
		update()
	}

	private fun update() {
		L.d("update edges size:${edges?.size}")
		edges = generate(tris)
	}

	private fun generate(tris : List<Tri>) : MutableSet<Edge> {
		val edges = mutableSetOf<Edge>()
		val maps = mutableMapOf<GLPointF, VoronoiPrimitive>()
		for (i in tris.indices) {
			val triI = tris[i]
			updateVoronoi(triI, maps)
			// 下面索引的解释
			// 位于triI之前索引的三角形triH
			// 要么和triI存在相同边，在triH所属的循环中已经生成了维诺边
			// 要么和triI不存在相同边，因此也没有必要去判断是否存在维诺边
			// 所以下面的triJ直接从i之后的位置开始循环
			val c = triI.center
			for (j in (i + 1) until tris.size) {
				val triJ = tris[j]
				val edge = triI.findSameEdge(triJ) ?: continue
				edges.add(Edge(c, triJ.center))
				generateVoronoi(edge.p0, triJ.center, maps)
				generateVoronoi(edge.p1, triJ.center, maps)
			}
		}
		voronoiPrimitives.clear()
		voronoiPrimitives.addAll(maps.values)
		return edges
	}

	private fun updateVoronoi(tri : Tri, maps : MutableMap<GLPointF, VoronoiPrimitive>) {
		generateVoronoi(tri.p0, tri.center, maps)
		generateVoronoi(tri.p1, tri.center, maps)
		generateVoronoi(tri.p2, tri.center, maps)
	}

	private fun generateVoronoi(
			p : GLPointF,
			center : GLPointF,
			maps : MutableMap<GLPointF, VoronoiPrimitive>
	) {
		var primitive = maps[p]
		if (primitive == null) {
			primitive = VoronoiPrimitive(p)
			maps[p] = primitive
		}
		primitive.include(center)
	}

	// Lloyd's 放松算法
	// 直接生成的维诺图过于随机，通过放松算法使其分布稍微平均些
	fun lloydsRelax() {
		// 放松算法的核心：
		// 生成维诺图后，每个维诺多边形的基点实际上就是某个或多个Delaunay三角形的公用顶点
		// 将这个顶点替换为这个维诺多边形的质心
		// 然后通过对质心集重新生成维诺图，得到放松后的维诺图。
		val points = mutableListOf<GLPointF>()
		voronoiPrimitives.forEach { v ->
			v.update()
			val centroid = v.centroid
			Log.d("VoronoiMesh", "centroid:$centroid")
			centroid?.takeIf { rect.containsPoint(it) }?.apply {
				points.add(this)
			}
		}
		val tris = DelaunayMath.processDelaunayTri(points.toTypedArray(), baseSuperTri)
		Log.d("VoronoiMesh", "lloydsRelax:${tris.size}")
		this.tris = tris
	}

	class VoronoiPrimitive(private val base : GLPointF) {
		var points = mutableSetOf<GLPointF>()
		private var triBuffer : FloatBuffer? = null
		var centroid : GLPointF? = null// 质心
		fun include(p : GLPointF) {
			points.add(p)
			triBuffer = null
		}

		fun update() {
			var o = GLPointF(0f, 0f)
			points.forEach {
				o.offset(it)
			}
			o /= points.size.toFloat()
			centroid = o
		}
	}

	companion object {
		fun createRandom(
				size : Int,
				xRange : ClosedFloatingPointRange<Float>,
				yRange : ClosedFloatingPointRange<Float>
		) : VoronoiMesh {
			return internalCreate(size, xRange, yRange)
		}

		private fun internalCreate(
				size : Int,
				xRange : ClosedFloatingPointRange<Float>,
				yRange : ClosedFloatingPointRange<Float>
		) : VoronoiMesh {
			val (delaunayTris, tri) = DelaunayMath.generateDelaunayTris(
					size,
					xRange.start,
					xRange.endInclusive,
					yRange.start,
					yRange.endInclusive
			)
			L.d("delaunay tris:${delaunayTris.size}")
			return VoronoiMesh(
					delaunayTris, tri,
					Rect(
							xRange.start,
							xRange.endInclusive,
							yRange.start,
							yRange.endInclusive
					)
			)
		}

		private data class Rect(
				val xStart : Float,
				val xEnd : Float,
				val yStart : Float,
				val yEnd : Float
		) {
			fun containsPoint(p : GLPointF) : Boolean {
				Log.d("Rect", "p:$p,range:[$xStart-$xEnd],[$yStart-$yEnd]")
				return p.x in xStart .. xEnd && p.y in yStart .. yEnd
			}
		}
	}

}

