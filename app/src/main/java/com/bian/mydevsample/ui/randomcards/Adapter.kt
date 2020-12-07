package com.bian.mydevsample.ui.randomcards

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bian.mydevsample.R
import com.bian.mydevsample.tool.EvaluateTool
import kotlin.math.abs

class Adapter(private val inflater : LayoutInflater) :
		RecyclerView.Adapter<Adapter.DecorHolder>() {
	var items : MutableList<Item>? = null
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	companion object {
		private val VALUE_ITEM_SIZE_CONFIG = arrayOf(100, 180, 300, 300, 380, 400, 600)
		private const val VALUE_UNCHECK_COLOR = Color.WHITE
		private val DECOR_COLOR = Color.rgb(102, 101, 135)
		private const val ANIM_DURATION = 300L
		private const val SCALE_OFFSET = 0.1f
	}

	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : DecorHolder {
		val inflate = inflater.inflate(R.layout.item_randomcard, parent, false)
		if (inflate.layoutParams is ViewGroup.MarginLayoutParams) {
			(inflate.layoutParams as ViewGroup.MarginLayoutParams).let {
				it.marginStart = 30
				it.marginEnd = 30
				it.topMargin = 30
				it.bottomMargin = 30
			}
		}
		if (viewType <= 2) {
			return DecorHolder(inflate)
		}
		return Holder(inflate, viewType)
	}

	override fun onBindViewHolder(holder : DecorHolder, position : Int) {
		holder.bindItem(items?.getOrNull(position))
	}

	override fun getItemViewType(position : Int) : Int {
		return items?.getOrNull(position)?.sizeLevel ?: 0
	}

	override fun getItemCount() : Int {
		return items?.size ?: 0
	}

	inner class Holder(v : View, viewType : Int) : DecorHolder(v) {
		init {
			bg.setOnClickListener {
				val tag = bg.tag ?: return@setOnClickListener
				if (tag !is Item || tag.tag != Item.TAG_VALUE) return@setOnClickListener
				tag.checked = ! tag.checked
				startAnimation(tag)
			}
//			if (viewType >= 3)
//				itemView.animation = AnimationUtils.loadAnimation(v.context, R.anim.item_anim3)
		}

		private val paint = Paint().also {
			it.maskFilter = BlurMaskFilter(40f, BlurMaskFilter.Blur.OUTER)
		}

		override fun bindItem(item : Item?) {
			super.bindItem(item)
			if (item?.tag == Item.TAG_VALUE) {
				bg.setLayerPaint(paint)
			} else {
				bg.setLayerPaint(null)
			}
		}

		private fun startAnimation(item : Item) {
			var startRatio = 0f
			var endRatio = 1f
			if (! item.checked) {
				startRatio = 1f
				endRatio = 0f
			}
			val d = bg.background as BlurGradientDrawable
			ValueAnimator.ofFloat(startRatio, endRatio).also {
				it.addUpdateListener { va ->
					val ratio = va.animatedValue as Float
					val color = EvaluateTool.evaluateColor(ratio,
					                                       VALUE_UNCHECK_COLOR,
					                                       item.clickColor)
					tv.alpha = 1 - ratio
					icon.alpha = ratio
					val scale = ((1 - abs(ratio - 0.5) / 0.5f) * SCALE_OFFSET + 1).toFloat()
					bg.scaleX = scale
					bg.scaleY = scale
					d.setDrawableColor(color)
				}
				it.duration = ANIM_DURATION
			}.start()
		}
	}

	open inner class DecorHolder(v : View) : RecyclerView.ViewHolder(v) {
		protected val tv : TextView = v.findViewById(R.id.tv)
		protected val icon : ImageView = v.findViewById(R.id.icon)
		protected val bg = v
		open fun bindItem(item : Item?) {
			tv.text = item?.message ?: ""
			if (bg.background == null || bg.background !is BlurGradientDrawable) {
				bg.background = createBlurGradientDrawable()
			}
			val drawable = bg.background as BlurGradientDrawable
			when (item?.tag) {
				Item.TAG_VALUE -> {
					drawable.setDrawableColor(if (item.checked) item.clickColor else VALUE_UNCHECK_COLOR)
				}
				Item.TAG_DECOR -> {
					drawable.setDrawableColor(DECOR_COLOR)
				}
			}
			drawable.useBlur = item?.tag == Item.TAG_VALUE
			bg.tag = item
			tv.alpha = if (item !!.checked) 0f else 1f
			icon.alpha = 1 - tv.alpha
			item.apply {
				val targetWidth = VALUE_ITEM_SIZE_CONFIG[item.sizeLevel]
				val lp = itemView.layoutParams
				if (lp.width != targetWidth) {
					lp.width = targetWidth
					lp.height = targetWidth
					itemView.layoutParams = lp
				}
			}
			itemView.elevation = if (item.sizeLevel > 2) 1f else 0f
		}

		private fun createBlurGradientDrawable() : GradientDrawable {
			val array = FloatArray(8) { 40f }
			return BlurGradientDrawable().also {
				it.cornerRadii = array
			}
		}
	}

	private class BlurGradientDrawable : GradientDrawable() {
		var useBlur = false
		private val paint = Paint().also {
			it.maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.OUTER)
		}
		private val mRect = RectF()
		private var drawableColor = 0
		fun setDrawableColor(color : Int) {
			this.drawableColor = color
			setColor(color)
		}

		override fun draw(canvas : Canvas) {
			super.draw(canvas)
			// 给背景增加一层外层发光效果
			if (! useBlur || drawableColor == VALUE_UNCHECK_COLOR) return
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
				bounds.apply {
					mRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
				}
				cornerRadii?.get(0)?.apply {
					val rad = this.coerceAtMost(mRect.width().coerceAtMost(mRect.height()) * 0.5f)
					paint.color = drawableColor
					canvas.drawRoundRect(mRect, rad, rad, paint)
				}
			}
		}
	}
}