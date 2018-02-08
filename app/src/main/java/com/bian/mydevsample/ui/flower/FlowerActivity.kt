package com.bian.mydevsample.ui.flower

import android.app.Activity
import android.os.Bundle
import com.bian.mydevsample.R
import com.bian.util.core.L
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
            L.d("seedDirection:" + mFlower.seedDirection)
        }
    }
}