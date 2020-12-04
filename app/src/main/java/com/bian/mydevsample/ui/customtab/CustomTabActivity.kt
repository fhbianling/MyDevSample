package com.bian.mydevsample.ui.customtab

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bian.mydevsample.R
import kotlinx.android.synthetic.main.activity_customtab.*

class CustomTabActivity : AppCompatActivity() {
	private val items = listOf(
			TabItem(
					R.mipmap.ic_test,
					"文一",
					Color.parseColor("#FF69B4"),
					Color.parseColor("#FF1493")
			),
			TabItem(
					R.mipmap.ic_test,
					"文二",
					Color.parseColor("#BA55D3"),
					Color.parseColor("#9400D3")
			),
			TabItem(
					R.mipmap.ic_test,
					"文三",
					Color.parseColor("#6A5ACD"),
					Color.parseColor("#483D8B")
			),
			TabItem(
					R.mipmap.ic_test,
					"文四",
					Color.parseColor("#8FBC8F"),
					Color.parseColor("#32CD32")
			),
			TabItem(
					R.mipmap.ic_test,
					"文五",
					Color.parseColor("#F4A460"),
					Color.parseColor("#D2691E")
			),
	)

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_customtab)
		customTabLayout.items = items
		val rvDataMap = mutableMapOf<Int, MutableList<String>>()
		for (i in items.indices) {
			rvDataMap[i] = mutableListOf()
			val generationCount = (Math.random() * 20 + 5).toInt()
			repeat(generationCount) {
				rvDataMap[i]?.add("[$i-$it]阿巴阿巴阿巴")
			}
		}
		rv.layoutManager = LinearLayoutManager(this)
		val adapter = Adapter(LayoutInflater.from(this))
		rv.adapter = adapter
		adapter.items = rvDataMap[0]

		val lac = LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.item_anim))
		lac.delay = 0.15f
		lac.order = LayoutAnimationController.ORDER_NORMAL
		rv.layoutAnimation = lac
		var last = - 1
		customTabLayout.onTabSelect = { tab, i ->
			if (i != last) {
				Log.d("MainActivity", "select:$i")
				adapter.items = rvDataMap[i]
				rv.post {
					rv.scheduleLayoutAnimation()
				}
			}
			last = i
		}
	}
}

private class Adapter(private val inflater : LayoutInflater) :
		RecyclerView.Adapter<Adapter.Holder>() {
	var items : List<String>? = null
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	private class Holder(v : View) : RecyclerView.ViewHolder(v) {
		val tv : TextView = v.findViewById(R.id.tv)

		init {
			v.animation = AnimationUtils.loadAnimation(v.context, R.anim.item_anim)
		}
	}

	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : Holder {
		return Holder(inflater.inflate(R.layout.item_rv, parent, false))
	}

	override fun onBindViewHolder(holder : Holder, position : Int) {
		items?.getOrNull(position)?.apply {
			holder.tv.text = this
		}
	}

	override fun getItemCount() : Int {
		return items?.size ?: 0
	}
}