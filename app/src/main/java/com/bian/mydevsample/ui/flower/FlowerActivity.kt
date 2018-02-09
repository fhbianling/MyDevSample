package com.bian.mydevsample.ui.flower

import android.app.Activity
import android.os.Bundle
import com.bian.mydevsample.R
import kotlinx.android.synthetic.main.activity_flower.*

/**
 * author 边凌
 * date 2018/2/5 17:12
 * 类描述：
 */
class FlowerActivity : Activity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flower)
        mBtn.setOnClickListener {
            mFlower.invalidate()
        }
        mAutoMove.setOnClickListener {
            mFlower.autoMove = ! mFlower.autoMove
        }
        changeSeedDirection.setOnClickListener {
            mFlower.seedDirection = (Math.random() * 360f).toFloat()
            angleInfo.text = String.format("%.2f", mFlower.seedDirection)
            degreeIndicator.angle = mFlower.seedDirection.toDouble()
        }
        degreeIndicator.angelChangeListener = {
            mFlower.seedDirection = it.toFloat()
            angleInfo.text = String.format("%.2f", it)
        }
    }
}