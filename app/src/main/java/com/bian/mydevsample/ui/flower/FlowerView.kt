package com.bian.mydevsample.ui.flower

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.bian.util.core.L

/**
 * author 边凌
 * date 2018/2/5 10:38
 * 类描述：
 */
class FlowerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val flowerList = mutableListOf<Flower>()

    private fun initParam() {
        for (i in 1..100) {
            flowerList.add(Flower(this))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (flowerList.isEmpty()) {
            initParam()
        }
        L.d("onDraw")
        canvas?.let {
            flowerList.forEach {
                it.move(canvas)
            }
        }
    }
}