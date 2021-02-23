package com.bian.mydevsample.ui.wheelview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bian.mydevsample.R
import com.bian.mydevsample.ui.wheelview.CustomWheelView.ItemDrawer
import com.bian.util.core.L
import kotlinx.coroutines.*
import kotlin.math.*

/**
 * author fhbianling@163.com
 * date 2021/1/14 15:14
 * 类描述：常用的滚轮控件无法实现我们需要的带色块的这种UI效果，这里直接提供一个自定义的滚轮控件
 * 通过 [CustomWheelView.setItemDrawer]可以为该类提供一个[ItemDrawer]实现类
 */
class CustomWheelView(ctx : Context, attrs : AttributeSet) : View(ctx, attrs) {
	/*-------------------  从attr中获得的属性  ---------------------*/
	// 该属性仅接受奇数值，也就是说，最中间的最大，上下其他项对称
	private val itemOnScreen : Int
	private val centerItemHeight : Int
	private val enableLoop : Boolean

	// 阻力系数，调整手势操作时滚轮滚动快慢
	private val resistance : Float

	/*-------------------  初始化时可以确定的信息,未特殊说明的角度都使用度数表达  ---------------------*/
	private val itemAngleStep : Int

	private val halfItemAngleStep : Int
	private val itemCornerAngle : Int
	private val circleRadius : Float
	private val wheelBounds = RectF()
	private val fullBounds = RectF()
	private val centerItemBounds = RectF()
	private val indexOffset : Int
	private val centerSelectOffset : Int

	/*-------------------  动态更改的属性  ---------------------*/
	private var itemDrawer : ItemDrawer? = null

	// 中间选中项在数据中的索引
	private var centerItemIndex = 0
		set(value) {
			field = value
			onItemSelectListener?.onItemSelect(value)
		}

	// 选中项矩形中点和垂直屏幕向外这根坐标轴的夹角
	// 该值永远在(0,itemAngleStep)之间波动
	// 当它达到边界处时，选中项变为上一项或下一项，并且该值被重置为0
	// 该值以度数表达
	private var fluctuation = 0

	// 用于缓存正在绘制的item的区域信息
	private var itemRectCache = RectF()

	private var onItemSelectListener : OnItemSelectListener? = null

	init {
		val ta = ctx.obtainStyledAttributes(attrs, R.styleable.CustomWheelView)
		itemOnScreen = ta.getInteger(R.styleable.CustomWheelView_itemOnScreen, 5)
		centerItemHeight =
				ta.getDimensionPixelSize(R.styleable.CustomWheelView_centerItemHeight, 150)
		enableLoop = ta.getBoolean(R.styleable.CustomWheelView_enableLoop, true)
		resistance = ta.getFloat(R.styleable.CustomWheelView_resistance, 2.5f)
		ta.recycle()
		if (itemOnScreen % 2 == 0) throw IllegalArgumentException("item on screen must be odd number")
		val itemAngleStepRadians = TWO_PI / (itemOnScreen * 2)
		itemAngleStep = Math.toDegrees(itemAngleStepRadians).toInt()
		halfItemAngleStep = itemAngleStep / 2
		itemCornerAngle = (180 - itemAngleStep) / 2
		circleRadius = ((centerItemHeight / 2f) / sin((itemAngleStepRadians) / 2f)).toFloat()
		indexOffset = (itemOnScreen - 1) / 2
		centerSelectOffset = itemAngleStep * ((itemOnScreen - 1) / 2)
		L.d("center item height:$centerItemHeight")
	}

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		val baseHeight = circleRadius * 2
		val height = baseHeight + paddingTop + paddingBottom
		val heightSpec = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
		super.onMeasure(widthMeasureSpec, heightSpec)
		wheelBounds.set(
				paddingLeft.toFloat(),
				paddingTop.toFloat(),
				measuredWidth - paddingRight.toFloat(),
				measuredHeight - paddingBottom.toFloat()
		)
		fullBounds.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
		val t = (measuredHeight - centerItemHeight) / 2f
		centerItemBounds.set(wheelBounds.left, t, wheelBounds.right, t + centerItemHeight)
	}

	fun resetState(initialItemIndex : Int = 0) {
		centerItemIndex = initialItemIndex
		L.d("reset state")
		updateFluctuation(0, true)
		invalidate()
	}

	override fun onDraw(canvas : Canvas) {
		super.onDraw(canvas)
		if (itemDrawer == null) return
		val itemCount = itemDrawer !!.getItemCount()
		itemDrawer?.drawDecor(canvas, fullBounds, wheelBounds, centerItemBounds)
		for (i in - indexOffset - 1 .. indexOffset + 1) {
			val originIndex = centerItemIndex + i
			val index = originIndex.safeLoopCollectionIndex(itemCount)
			val canBeDraw = (enableLoop || (originIndex == index))
			val itemTopAngle = (i + indexOffset) * itemAngleStep + fluctuation
			if (itemTopAngle !in - halfItemAngleStep .. 180 - halfItemAngleStep) {
				continue
			}
			val t01 = if (itemTopAngle > 90) {
				wheelBounds.top + circleRadius + circleRadius * cosDegree(180 - itemTopAngle.toDouble()).toFloat()
			} else {
				(wheelBounds.top + circleRadius - circleRadius * cosDegree(itemTopAngle.toDouble())).toFloat()
			}
			// item对应的圆上的切面在屏幕上的投影高度
			val projectionHeight : Float =
					abs((centerItemHeight * cosDegree(((itemCornerAngle - itemTopAngle).toDouble()))).toFloat())
			itemRectCache.set(
					wheelBounds.left,
					max(t01, wheelBounds.top),
					wheelBounds.right,
					min(t01 + projectionHeight, wheelBounds.bottom)
			)
			if (canBeDraw) {
				itemDrawer !!.drawItem(
						index,
						canvas,
						itemRectCache,
						itemTopAngle,
						itemAngleStep,
						centerItemBounds
				)
			}
		}
	}

	private fun cosDegree(degree : Double) : Double {
		return cos(Math.toRadians(degree))
	}

	private var lastEvent = PointF()
	private var speed = 0f

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event : MotionEvent?) : Boolean {
		when (event?.action) {
			MotionEvent.ACTION_DOWN -> {
				lastEvent.set(event.rawX, event.rawY)
				return true
			}
			MotionEvent.ACTION_MOVE -> {
				val diffY = event.rawY - lastEvent.y
				speed = diffY
				lastEvent.set(event.rawX, event.rawY)

				val nextFluctuation =
						((diffY / resistance).toInt() + fluctuation).safeInRange(- 1, itemAngleStep)
				if (! enableLoop) {
					if (centerItemIndex == 0 && nextFluctuation > 0) {
						return true
					}
					itemDrawer?.let {
						if (centerItemIndex == it.getItemCount() - 1
								&& nextFluctuation < 0
						) {
							return true
						}
					}
				}
				L.d("on touch event")
				updateFluctuation(nextFluctuation)
				invalidate()
				return true
			}
			MotionEvent.ACTION_UP -> {
				autoScrollAnimation()
				return true
			}
		}

		return super.onTouchEvent(event)
	}

	private var va : ValueAnimator? = null
	var end : Int = 0
	private fun autoScrollAnimation() {
		if (va?.isRunning == true) {
			va?.cancel()
		}
		val endForLast = itemAngleStep - fluctuation
		val endForNext = - fluctuation
		val endForRollback = - fluctuation
		var rollback = false
		var end = if (abs(speed) > SPEED_LIMIT) {
			when {
				speed > 0 -> endForLast.also { L.d("speed limit last") } // 上一项
				else      -> endForNext.also { L.d("speed limit next") } // 下一项
			}
		} else {
			val quarter = itemAngleStep / 4
			val minusQuarter = itemAngleStep - quarter
			when (fluctuation) {
				in 0 .. quarter -> endForNext.also { L.v("no limit next") } // 下一项
				in quarter .. minusQuarter -> endForRollback.also {
					rollback = true
					L.v("rollback")
				} // 归位
				else                       -> endForLast.also { L.d("no limit last") } // 上一项
			}
		}
		if (! enableLoop) {
			val goLastWhenAtStart = centerItemIndex == 0 && end == endForLast
			val goNextWhenAtEnd = itemDrawer != null
					&& centerItemIndex == itemDrawer !!.getItemCount() - 1 && end == endForNext
			if (goLastWhenAtStart || goNextWhenAtEnd) {
				end = endForRollback
				rollback = true
			}
		}
		if (end == 0) {
			invalidate()
			return
		}
		this.end = end
		L.i("execute animation-start:$fluctuation,index:$centerItemIndex,animation-start:0,animation-end:$end")

		GlobalScope.launch {
			var count = abs(end)
			val sign = sign(end.toDouble()).toInt()
			while (count != 0) {
				count --
				withContext(Dispatchers.Main) {
					val f = fluctuation + sign
					updateFluctuation(f, rollback)
					invalidate()
				}
				delay(10)
			}
		}

//		va = ValueAnimator.ofInt(0, end).also {
//			it.addUpdateListener { va ->
//				val diff = va.animatedValue as Int
//				L.d("on animation:diff:$diff")
//				fluctuation += sign(diff.toDouble()).toInt()
//				invalidate()
//			}
//			it.addListener(object : SimpleAnimatorListener() {
//				override fun onAnimationEnd(animation : Animator?) {
//					invalidate()
//				}
//			})
//			it.duration = DURATION
//		}
//		va?.start()
	}

	private fun updateFluctuation(f : Int, rollBack : Boolean = false) {
		val itemCount = itemDrawer?.getItemCount()
		when {
			f == itemAngleStep -> {
				itemCount?.let {
					centerItemIndex = (centerItemIndex - 1).safeLoopCollectionIndex(it)
				}
				fluctuation = 0
			}
			f < 0              -> {
				if (! rollBack) {
					itemCount?.let {
						centerItemIndex = (centerItemIndex + 1).safeLoopCollectionIndex(it)
					}
				}
				fluctuation = itemAngleStep
			}
			else               -> {
				fluctuation = f
			}
		}
	}

	fun setItemDrawer(itemDrawer : ItemDrawer) {
		this.itemDrawer = itemDrawer.also {
			it.attachWheelView(this)
		}
		resetState()
	}

	fun getCurrentItem() : Int = centerItemIndex

	abstract class ItemDrawer {
		private var wheelView : CustomWheelView? = null

		/**
		 * @param itemTopAngle item顶部与圆心连线与屏幕的夹角（度数），当该值为0时，item的顶部和滚轮的顶部对齐
		 * @param itemAngleSpan item跨过的度数
		 */
		abstract fun drawItem(
				position : Int, canvas : Canvas,
				rect : RectF, itemTopAngle : Int, itemAngleSpan : Int, centerItemBounds : RectF
		)

		abstract fun drawDecor(
				canvas : Canvas,
				fullBounds : RectF,
				wheelBounds : RectF,
				centerItemBounds : RectF
		)

		abstract fun getItemCount() : Int
		open fun getInitialItemIndex() : Int = 0

		fun attachWheelView(wheelView : CustomWheelView) {
			this.wheelView = wheelView
		}

		fun notifyDataSetChanged() {
			if (wheelView != null) {
				this.wheelView?.resetState(getInitialItemIndex())
			}
		}
	}

	fun interface OnItemSelectListener {
		fun onItemSelect(index : Int)
	}

	fun setOnItemSelectListener(onItemSelectListener : OnItemSelectListener) {
		this.onItemSelectListener = onItemSelectListener
	}

	open class TextItemDrawer<T>(val gravity : Gravity = Gravity.Center, private val stringFactory : ((T) -> String)? = null) :
			ItemDrawer() {
		enum class Gravity {
			Start, Center, Right
		}

		private val paint = Paint().also {
			it.textSize = primaryTextSize
			it.color = primaryTextColor
			it.isAntiAlias = true
		}
		private val textPaint = TextPaint()

		protected var items : List<T>? = null
		protected var paddingStart = 0f
		protected var paddingEnd = 0f
		protected var primaryTextSize : Float = 60f
		protected var primaryTextColor : Int = Color.WHITE
		protected var minorTextSize : Float? = null
		protected var minorTextColor : Int? = null
		protected var maxTextWidth : Int? = null
		protected var dividerWidth : Float = 1f
		protected var dividerColor : Int = Color.parseColor("#2E3546")
		protected var initialIndex = 0
		protected var drawDivider = true
		protected var baseAvailableTextWidth = - 1f
		protected var avail = - 1f


		override fun drawItem(
				position : Int,
				canvas : Canvas,
				rect : RectF,
				itemTopAngle : Int,
				itemAngleSpan : Int,
				centerItemBounds : RectF
		) {
			items?.getOrNull(position)?.let {
				val text = stringFactory?.invoke(it) ?: it.toString()
				val itemCenterAngle = itemTopAngle + itemAngleSpan / 2f
				val ratio : Float = 1 - abs(itemCenterAngle - 90f) / 90f
				val useMinorTextAppearance = minorTextSize != null
				if (! useMinorTextAppearance) {
					val textSize = primaryTextSize * (0.5f + ratio * 0.5f)
					val color =
							ratio.lerpColor(primaryTextColor.withAlpha(0.5f), primaryTextColor)
					drawTextForCanvas(textSize, color, text, rect, canvas)
				} else {
					if (RectF.intersects(rect, centerItemBounds)) {
						canvas.save()
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
							canvas.clipOutRect(centerItemBounds)
						} else {
							canvas.clipRect(centerItemBounds, Region.Op.DIFFERENCE)
						}
						drawTextForCanvas(minorTextSize !!, minorTextColor !!, text, rect, canvas)
						canvas.restore()
						canvas.save()
						canvas.clipRect(centerItemBounds)
						drawTextForCanvas(primaryTextSize, primaryTextColor, text, rect, canvas)
						canvas.restore()
					} else {
						drawTextForCanvas(minorTextSize !!, minorTextColor !!, text, rect, canvas)
					}
				}
				textPaint.color = ratio.lerpColor(Color.WHITE, Color.BLACK).withAlpha(0.3f)
				canvas.drawRect(rect, textPaint)
			}
		}

		private fun drawTextForCanvas(
				textSize : Float,
				color : Int,
				text : String,
				rect : RectF,
				canvas : Canvas
		) {
			ensureBaseAvailableTextWidth(rect.width())
			textPaint.textSize = textSize
			textPaint.color = color
			val ellipsizedText =
					TextUtils.ellipsize(text,
					                    textPaint,
					                    avail,
					                    TextUtils.TruncateAt.MIDDLE).toString()
			val textWidth = textPaint.measureText(ellipsizedText)
			val textHeight = textPaint.descent() - textPaint.ascent()
			val textBaseLine = rect.bottom - (rect.height() - textHeight) / 2f - textPaint.descent()
			val textStart = when (gravity) {
				Gravity.Start -> rect.left + paddingStart
				Gravity.Center -> (rect.width() - textWidth) / 2f
				Gravity.Right -> rect.right - textWidth - paddingEnd
			}
			canvas.drawText(ellipsizedText, textStart, textBaseLine, textPaint)
		}

		private fun ensureBaseAvailableTextWidth(rectWidth : Float) {
			if (baseAvailableTextWidth != - 1f) return
			baseAvailableTextWidth = rectWidth - paddingStart - paddingEnd
			avail = if (maxTextWidth != null) min(
					maxTextWidth !!.toFloat(),
					baseAvailableTextWidth
			) else baseAvailableTextWidth
		}

		override fun drawDecor(
				canvas : Canvas,
				fullBounds : RectF,
				wheelBounds : RectF,
				centerItemBounds : RectF
		) {
			if (! drawDivider) return
			paint.color = dividerColor
			canvas.drawLine(
					centerItemBounds.left,
					centerItemBounds.top,
					centerItemBounds.right,
					centerItemBounds.top + dividerWidth,
					paint
			)
			canvas.drawLine(
					centerItemBounds.left,
					centerItemBounds.bottom - dividerWidth,
					centerItemBounds.right,
					centerItemBounds.bottom,
					paint
			)
		}

		override fun getItemCount() : Int = items?.size ?: 0

		fun setData(data : List<T>, initialIndex : Int = 0) {
			items = data
			this.initialIndex = initialIndex
			notifyDataSetChanged()
		}

		fun setSelect(index : Int = 0) {
			this.initialIndex = index
			notifyDataSetChanged()
		}

		override fun getInitialItemIndex() : Int = this.initialIndex

		fun setMinorTextAppearance(textSize : Float, textColor : Int, maxTextWidth : Int? = null) {
			this.minorTextSize = textSize
			this.minorTextColor = textColor
			this.maxTextWidth = maxTextWidth
			notifyDataSetChanged()
		}

		fun setPrimaryTextAppearance(textSize : Float, textColor : Int) {
			this.primaryTextSize = textSize
			this.primaryTextColor = textColor
			notifyDataSetChanged()
		}

		fun setPadding(paddingStart : Float, paddingEnd : Float) {
			this.paddingStart = paddingStart
			this.paddingEnd = paddingEnd
			baseAvailableTextWidth = - 1f
			notifyDataSetChanged()
		}

		fun setDividerAppearance(dividerWidth : Float, dividerColor : Int, drawDivider : Boolean) {
			this.dividerWidth = dividerWidth
			this.dividerColor = dividerColor
			this.drawDivider = drawDivider
			notifyDataSetChanged()
		}

	}

	companion object {
		private const val TWO_PI = Math.PI * 2
		private const val SPEED_LIMIT = 3f
		private const val DURATION = 150L
		private fun Int.safeLoopCollectionIndex(collectionSize : Int) : Int {
			var itemIndex = this
			if (itemIndex < 0) {
				itemIndex += collectionSize
			} else if (itemIndex >= collectionSize) {
				itemIndex -= collectionSize
			}
			return itemIndex
		}
	}
}