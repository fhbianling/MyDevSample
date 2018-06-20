package com.bian.mydevsample.ui.stream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.view.View
import com.bian.util.core.L

/**
 * author 边凌
 * date 2018/4/16 21:10
 * 类描述：
 */

class MusicStreamView(context: Context?, attributes: AttributeSet?) : View(context, attributes) {

    var data: ByteArray? = null

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
    }

    private var mVis: Visualizer? = null
    fun bind(vis: Visualizer) {
        mVis = vis
        vis.apply {
            captureSize = Visualizer.getCaptureSizeRange()[0]
            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                }

                override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                    fft?.apply {
                        data = copyOf()
                        invalidate()
                    }
                }

            }, Visualizer.getMaxCaptureRate() / 2, false, true)
            enabled = true
        }
    }

    fun release() {
        mVis?.enabled = false
    }

    fun pause() {
        mVis?.enabled = false
    }

    fun resume() {
        mVis?.enabled = true
    }

    private val path: Path = Path()
    private val lineCount = 16
    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            data?.apply {
                val lineWidth = width / lineCount
                path.reset()
                val combineCount = (size / (lineCount.toFloat())).toInt()
                L.d(combineCount)
                for (i in 0 until lineCount) {
                    val byte = (i * combineCount..(i + 1) * combineCount).filter { it < size }.sumBy { this[it].toInt() } / combineCount
                    val startX = lineWidth * i.toFloat()
                    val stopX = lineWidth * (i + 1f)
                    val ratio = ((byte + 128) / 256f) * 0.5f
                    val stopY = height * (1f - ratio)
                    if (i == 0) {
                        path.moveTo(startX, stopY)
                        path.lineTo(stopX, stopY)
                    } else {
                        path.lineTo(startX, stopY)
                        path.lineTo(stopX, stopY)
                    }
                }
                drawPath(path, paint)
            }
        }
    }

}