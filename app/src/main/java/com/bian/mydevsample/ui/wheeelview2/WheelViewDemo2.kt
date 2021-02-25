package com.bian.mydevsample.ui.wheeelview2

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import com.bian.mydevsample.R
import com.bian.mydevsample.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wheel_view_2.*

/**
 * author fhbianling@163.com
 * date 2021/2/25 17:23
 * 类描述：
 */
class WheelViewDemo2 : BaseActivity() {
	override fun bindLayoutId() : Int {
		return R.layout.activity_wheel_view_2
	}

	override fun initView(savedInstanceState : Bundle?) {
		rotateXSeekBar.doSeek(rotateX) {
			wheel.rotateX = it.toFloat()
		}
		rotateYSeekBar.doSeek(rotateY) {
			wheel.rotateY = it.toFloat()
		}
		rotateZSeekBar.doSeek(rotateZ) {
			wheel.rotateZ = it.toFloat()
		}
		locationXSeekBar.doSeek(locationX) {
			wheel.locationX = it.toFloat()
		}
		locationYSeekBar.doSeek(locationY) {
			wheel.locationY = it.toFloat()
		}
		locationZSeekBar.doSeek(locationZ) {
			wheel.locationZ = it.toFloat()
		}
	}

	private fun SeekBar.doSeek(tv : TextView, onSeekChange : ((Int) -> Unit)) {
		setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar : SeekBar?, progress : Int, fromUser : Boolean) {
				tv.text = progress.toString()
				onSeekChange.invoke(progress)
			}

			override fun onStartTrackingTouch(seekBar : SeekBar?) {
			}

			override fun onStopTrackingTouch(seekBar : SeekBar?) {
			}
		})
	}
}