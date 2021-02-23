package com.bian.mydevsample.ui.wheelview

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

/**
 * author fhbianling@163.com
 * date 2021/2/23 14:15
 * 类描述：
 */

fun Int.withAlpha(alpha: Float): Int {
	val r = Color.red(this)
	val g = Color.green(this)
	val b = Color.blue(this)
	return Color.argb((alpha * 255f).toInt(), r, g, b)
}

fun Float.lerpColor(@ColorInt start: Int, @ColorInt end: Int): Int {
	val a = this.lerpValue(Color.alpha(start), Color.alpha(end))
	val r = this.lerpValue(Color.red(start), Color.red(end))
	val g = this.lerpValue(Color.green(start), Color.green(end))
	val b = this.lerpValue(Color.blue(start), Color.blue(end))
	return Color.argb(a, r, g, b)
}

fun Float.lerpValue(start: Int, end: Int): Int {
	return ((end - start) * this + start).roundToInt()
}
fun Int.safeInRange(min: Int, max: Int): Int {
	return min.coerceAtLeast(this.coerceAtMost(max))
}

abstract class SimpleAnimatorListener : Animator.AnimatorListener {

	override fun onAnimationStart(animation: Animator?) {
	}

	override fun onAnimationEnd(animation: Animator?) {
	}

	override fun onAnimationCancel(animation: Animator?) {
	}

	override fun onAnimationRepeat(animation: Animator?) {
	}
}