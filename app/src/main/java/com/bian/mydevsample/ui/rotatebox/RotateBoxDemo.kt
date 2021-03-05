package com.bian.mydevsample.ui.rotatebox

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import com.bian.mydevsample.R
import com.bian.mydevsample.base.BaseActivity
import kotlinx.android.synthetic.main.activity_rotate_box.*

/**
 * author fhbianling@163.com
 * date 2021/3/5 15:26
 * 类描述：
 */
class RotateBoxDemo : BaseActivity() {
	override fun bindLayoutId() : Int {
		return R.layout.activity_rotate_box
	}

	override fun initView(savedInstanceState : Bundle?) {
		enableRandom.setOnCheckedChangeListener { buttonView, isChecked ->
			box.randomRotate(isChecked)
		}
		rotateX.setupWithTextView(rotateXShow) {
			box.rotateXBase = it.toFloat()
		}
		rotateY.setupWithTextView(rotateYShow) {
			box.rotateYBase = it.toFloat()
		}
		rotateZ.setupWithTextView(rotateZShow) {
			box.rotateZBase = it.toFloat()
		}
		reset.setOnClickListener {
			enableRandom.isChecked = false
			box.reset()
		}
	}

	private fun SeekBar.setupWithTextView(tv : TextView, hook : ((Int) -> Unit)) {
		setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar : SeekBar?, progress : Int, fromUser : Boolean) {
				tv.text = progress.toString()
				hook(progress)
			}

			override fun onStartTrackingTouch(seekBar : SeekBar?) {
			}

			override fun onStopTrackingTouch(seekBar : SeekBar?) {
			}
		})
	}
}