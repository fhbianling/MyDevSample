package com.bian.mydevsample.ui.stream

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.bian.mydevsample.R
import com.bian.mydevsample.base.BaseActivity
import com.bian.util.core.PermissionUtil
import kotlinx.android.synthetic.main.activity_music_stream.*

/**
 * author 边凌
 * date 2018/4/16 21:08
 * 类描述：
 */
class MusicStreamActivity : BaseActivity() {
	override fun initView(savedInstanceState : Bundle?) {
		play.setOnClickListener {
			PermissionUtil.checkAndRequestPermissions(this, Manifest.permission.RECORD_AUDIO)
			if (ActivityCompat.checkSelfPermission(this,
			                                       Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
				playMusic()
			}
		}
	}

	private var player : MediaPlayer? = null

	private fun playMusic() {
		if (player == null) {
			player = MediaPlayer.create(this, R.raw.time)
			msv.bind(Visualizer(player !!.audioSessionId))
		}
		player?.apply {
			if (isPlaying) {
				pause()
				play.text = "播放"
				msv.pause()
			} else {
				start()
				play.text = "暂停"
				msv.resume()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		player?.stop()
		msv.release()
	}

	override fun bindLayoutId() = R.layout.activity_music_stream

}