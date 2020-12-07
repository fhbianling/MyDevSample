package com.bian.mydevsample.ui.randomcards

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils
import com.bian.mydevsample.R
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.activity_random_card.*
import java.util.*
import kotlin.time.ExperimentalTime

data class Item(val message : String, val sizeLevel : Int, val clickColor : Int, val tag : Int) {
	var checked = false

	companion object {
		const val TAG_VALUE = 1
		const val TAG_DECOR = 3
	}

	override fun toString() : String {
		return "Item($sizeLevel, $tag)"
	}

}


/**
 * author fhbianling@163.com
 * date 2020/12/4 11:09
 * 类描述：
 */
class RandomCardActivity : AppCompatActivity() {
	private val adapter by lazy { Adapter(LayoutInflater.from(this)) }

	@ExperimentalTime
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_random_card)
		rv.adapter = adapter
		rv.layoutManager = FlexboxLayoutManager(this).also {
			it.alignItems = AlignItems.BASELINE
			it.justifyContent = JustifyContent.SPACE_AROUND
		}
		var origin = mutableListOf<Item>()
		btn1.setOnClickListener {
			origin = generateRandomSource()
			val sources = generateFixedSource(origin)
			adapter.items = sources
			rv.scheduleLayoutAnimation()
		}
		btn2.setOnClickListener {
			val list = generateFixedSource(origin)
			rv.adapter = null
			adapter.items = list
			rv.adapter = adapter
			rv.scheduleLayoutAnimation()
		}
		val scaleAnimation = ScaleAnimation(0f, 1f,
		                                    0f, 1f,
		                                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
		                                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
		scaleAnimation.interpolator = OvershootInterpolator()
		scaleAnimation.duration = 500
		val lac = LayoutAnimationController(scaleAnimation)
		lac.delay = 0.05f
		lac.order = LayoutAnimationController.ORDER_RANDOM
//		lac.interpolator = OvershootInterpolator()
		rv.layoutAnimation = lac
	}

	private fun generateFixedSource(origin : MutableList<Item>) : MutableList<Item> {
		origin.shuffle()
		var remain = origin.size
		val result = mutableListOf<Item>()
		while (remain != 0) {
			remain --
			result.add(origin[remain])
			if (Math.random() > 0.3) {
				val randomItemCount = (1 + Math.random() + 0.2).toInt()
				repeat(randomItemCount) {
					result.add(createRandomDecor())
				}
			}
		}
		if (Math.random() > 0.5) {
			result.add(0, createRandomDecor())
		}
		return result
	}

	private fun createRandomDecor() = Item("", generateRandomSizeLevel(0, 2), 0, Item.TAG_DECOR)

	private fun generateRandomSource() : MutableList<Item> {
		val random = Random()
		val generateGaussSize = fun(min : Int, max : Int) : Int {
			return MathUtils.clamp(min + (random.nextGaussian() * (max - min) / 2f).toInt(),
			                       min,
			                       max)
		}
		val randomColor = fun() : Int {
			val r = (Math.random() * 255).toInt()
			val g = (Math.random() * 255).toInt()
			val b = (Math.random() * 255).toInt()
			return Color.rgb(r, g, b)
		}
		val count = 100
		val result = mutableListOf<Item>()
		repeat(count) {
			val sizeLevel = generateGaussSize.invoke(3, 6)
			result.add(Item("$it-$sizeLevel", sizeLevel, randomColor.invoke(), Item.TAG_VALUE))
		}
		return result
	}

	private fun generateRandomSizeLevel(min : Int, max : Int) : Int {
		return min + (Math.random() * (max - min)).toInt()
	}
}