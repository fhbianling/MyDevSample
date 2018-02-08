package com.bian.mydevsample.ui.flower

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * author 边凌
 * date 2018/2/5 10:38
 * 类描述：
 */
class FlowerView(context : Context?, attrs : AttributeSet?) : View(context, attrs) {

    private val flowerList = mutableListOf<Flower>()
    private val paint : Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
    }
    private val centerX by lazy {
        width / 2f
    }

    private val centerY by lazy {
        height / 2f
    }
    var seedDirection = - 90f
        set(value) {
            field = value - 90f
            flowerList.clear()
            initParam()
        }
    var autoMove : Boolean = true
        set(value) {
            field = value
            if (value) invalidate()
        }

    private fun initParam() {
        for (i in 1 .. 100) {
            flowerList.add(Flower(this))
        }
    }

    override fun onDraw(canvas : Canvas?) {
        if (flowerList.isEmpty()) {
            initParam()
        }
        canvas?.let {
            canvas.save()
            canvas.rotate(seedDirection, centerX, centerY)
            flowerList.forEach {
                it.move(canvas, paint)
            }
            canvas.restore()
            if (autoMove) {
                invalidate()
            }
        }
    }

}