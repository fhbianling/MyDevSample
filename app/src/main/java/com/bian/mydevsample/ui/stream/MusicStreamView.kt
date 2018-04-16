package com.bian.mydevsample.ui.stream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.view.View

/**
 * author 边凌
 * date 2018/4/16 21:10
 * 类描述：
 */

class MusicStreamView(context: Context?, attributes: AttributeSet?) : View(context, attributes) {

    var waveData: ByteArray? = null

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
    }

    private var mVis: Visualizer? = null
    fun bind(vis: Visualizer) {
        mVis = vis
        vis.apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]
            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                    waveform?.apply {
                        waveData = copyOf()
                        invalidate()
                    }
                }

                override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                }

            }, Visualizer.getMaxCaptureRate() / 2, true, false)
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
    private val lineCount = 8
    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            waveData?.apply {
                val lineWidth = width / lineCount
                path.reset()
                val combineCount = size / 10f.toInt()
                for (i in 0 until lineCount) {
                    val byte = (i * combineCount..(i + 1) * combineCount).sumBy { this[it].toInt() } / combineCount
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