package com.bian.mydevsample.ui.wheelview

import android.os.Bundle
import com.bian.mydevsample.R
import com.bian.mydevsample.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wheel_view.*

/**
 * author fhbianling@163.com
 * date 2021/1/14 15:14
 * 类描述：
 */
class WheelViewDemo : BaseActivity() {
	override fun bindLayoutId() : Int {
		return R.layout.activity_wheel_view
	}

	override fun initView(savedInstanceState : Bundle?) {
		val drawer = CustomWheelView.TextItemDrawer<String>()
		wheelView.setItemDrawer(drawer)
		val item = mutableListOf<String>()
		repeat(20) {
			item.add(it.toString())
		}
		drawer.setData(item)

		plus.setOnClickListener {
			wheelView.add()
		}
		minus.setOnClickListener {
			wheelView.minus()
		}
	}
}