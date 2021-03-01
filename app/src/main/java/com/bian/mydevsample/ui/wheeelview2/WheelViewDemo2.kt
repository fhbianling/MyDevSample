package com.bian.mydevsample.ui.wheeelview2

import android.os.Bundle
import com.bian.mydevsample.R
import com.bian.mydevsample.base.BaseActivity
import com.bian.util.core.L
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_wheel_view_2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * author fhbianling@163.com
 * date 2021/2/25 17:23
 * 类描述：
 */
class WheelViewDemo2 : BaseActivity() {
	override fun bindLayoutId() : Int {
		return R.layout.activity_wheel_view_2
	}

	@ExperimentalTime
	override fun initView(savedInstanceState : Bundle?) {
		autoScroll.setOnCheckedChangeListener { buttonView, isChecked ->
			wheel.autoRotate = isChecked
		}
		chart.isLogEnabled = true
		genChart.setOnClickListener {
			val map = wheel.values.map { Entry(it.key, it.value) }
			L.d(map.size)
			chart.data = LineData(LineDataSet(map, ""))
			chart.invalidate()
		}
	}
}