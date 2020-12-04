package com.bian.mydevsample.tool

import android.graphics.Color

/**
 * author fhbianling@163.com
 * date 2020/12/4 14:50
 * 类描述：
 */
object EvaluateTool {
	fun evaluate(ratio : Float, min : Float, max : Float) : Float {
		return ratio * (max - min) + min
	}

	fun evaluateColor(ratio : Float, startColor : Int, endColor : Int) : Int {
		val r = evaluate(
				ratio, Color.red(startColor).toFloat(), Color.red(endColor).toFloat()
		).toInt()
		val g = evaluate(
				ratio, Color.green(startColor).toFloat(), Color.green(endColor).toFloat()
		).toInt()
		val b = evaluate(
				ratio, Color.blue(startColor).toFloat(), Color.blue(endColor).toFloat()
		).toInt()
		return Color.rgb(r, g, b)
	}
}