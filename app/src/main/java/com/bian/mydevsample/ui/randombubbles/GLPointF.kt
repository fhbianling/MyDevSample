package com.bian.mydevsample.ui.randombubbles

import kotlin.math.hypot

data class GLPointF(var x : Float, var y : Float) {
	override fun toString() : String {
		return "($x, $y)"
	}

	fun set(x : Float, y : Float) {
		this.x = x
		this.y = y
	}

	fun set(p : GLPointF) {
		this.x = p.x
		this.y = p.y
	}

	fun copy() : GLPointF {
		return GLPointF(x, y)
	}

	fun distance(p : GLPointF) : Float {
		return hypot(p.x - x, p.y - y)
	}

	fun offset(p : GLPointF, negative : Boolean = false) : GLPointF {
		if (negative) {
			this.x -= p.x
			this.y -= p.y
		} else {
			this.x += p.x
			this.y += p.y
		}
		return this
	}

	operator fun compareTo(p : GLPointF) : Int {
		if (x < p.x) return - 1
		if (y < p.y) return - 1
		if (x == p.x && y == p.y) return 0
		return 1
	}

	operator fun minus(p : GLPointF) : GLPointF {
		return GLPointF(x - p.x, y - p.y)
	}

	operator fun unaryMinus() : GLPointF {
		return GLPointF(- x, - y)
	}

	operator fun plus(p : GLPointF) : GLPointF {
		return GLPointF(
				x + p.x,
				y + p.y,
		)
	}

	operator fun div(float : Float) : GLPointF {
		return GLPointF(
				x / float,
				y / float,
		)
	}

	override fun equals(other : Any?) : Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GLPointF

		if (x != other.x) return false
		if (y != other.y) return false
		return true
	}

	override fun hashCode() : Int {
		var result = x.hashCode()
		result = 31 * result + y.hashCode()
		return result
	}


	companion object {
		fun zero() = GLPointF(0f, 0f)
	}
}
