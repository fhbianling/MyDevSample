package com.bian.mydevsample.ui.randombubbles

import android.graphics.Color
import android.os.Bundle
import com.bian.mydevsample.R
import com.bian.mydevsample.base.BaseActivity
import com.bian.util.core.L
import kotlinx.android.synthetic.main.activity_random_bubbles.*

/**
 * author fhbianling@163.com
 * date 2020/12/29 16:10
 * 类描述：
 */
class RandomBubblesDemo : BaseActivity() {
	override fun bindLayoutId() : Int {
		return R.layout.activity_random_bubbles
	}

	override fun initView(savedInstanceState : Bundle?) {
		btn.setOnClickListener {
			debug()
		}
	}

	private fun debug() {
		L.d("debug")
		val items = mutableListOf<RandomBubbles.Bubble>()
		for (i in 0 .. (3 + Math.random() * 5f).toInt()) {
			var string = ""
			for (j in 0 .. ((3 + Math.random() * 5f).toInt())) {
				string += "字"
			}
			items.add(RandomBubbles.Bubble(string, randomColor()))
		}
		bubble.setBubbles(items)
		bubbleCount.text = "气泡数量应有值:${items.size}"
	}

	private fun randomColor() : Int {
		val r = (Math.random() * 256).toInt()
		val g = (Math.random() * 256).toInt()
		val b = (Math.random() * 256).toInt()
		return Color.rgb(r, g, b)
	}
}