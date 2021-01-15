package com.bian.mydevsample.ui.wheelview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import com.bian.mydevsample.R
import com.bian.util.core.L
import kotlin.math.*

/**
 * author fhbianling@163.com
 * date 2021/1/14 15:14
 * 类描述：
 */
class CustomWheelView(ctx : Context, attrs : AttributeSet) : View(ctx, attrs) {
	/*-------------------  从attr中获得的属性  ---------------------*/
	// 该属性仅接受奇数值，也就是说，最中间的最大，上下其他项对称
	private val itemOnScreen : Int
	private val centerItemHeight : Int
	private var itemDrawer : ItemDrawer? = null
	private val enableLoop : Boolean

	// 阻力系数，调整手势操作时滚轮滚动快慢
	private val resistance : Float

	/*-------------------  初始化时可以确定的信息,未特殊说明的角度都使用度数表达  ---------------------*/
	private val itemAngleStep : Int
	private val halfItemAngleStep : Int
	private val itemCornerAngle : Int
	private val circleRadius : Float
	private val drawBounds = RectF()
	private val indexOffset : Int

	/*-------------------  动态更改的属性  ---------------------*/
	// 中间选中项在数据中的索引
	private var centerItemIndex = 0

	// 选中项矩形中点和垂直屏幕向外这根坐标轴的夹角
	// 该值永远在(0,itemAngleStep)之间波动
	// 当它达到边界处时，选中项变为上一项或下一项，并且该值被重置为0
	// 该值以度数表达
	private var fluctuation = 0

	// 用于缓存正在绘制的item的区域信息
	private var itemRectCache = RectF()

	init {
		val ta = ctx.obtainStyledAttributes(attrs, R.styleable.CustomWheelView)
		itemOnScreen = ta.getInteger(R.styleable.CustomWheelView_itemOnScreen, 5)
		centerItemHeight =
				ta.getDimensionPixelSize(R.styleable.CustomWheelView_centerItemHeight, 150)
		enableLoop = ta.getBoolean(R.styleable.CustomWheelView_enableLoop, true)
		resistance = ta.getFloat(R.styleable.CustomWheelView_resistance, 2f)
		ta.recycle()
		if (itemOnScreen % 2 == 0) throw IllegalArgumentException("item on screen must be odd number")
		val itemAngleStepRadians = TWO_PI / (itemOnScreen * 2)
		itemAngleStep = Math.toDegrees(itemAngleStepRadians).toInt()
		halfItemAngleStep = itemAngleStep / 2
		itemCornerAngle = (180 - itemAngleStep) / 2
		circleRadius = ((centerItemHeight / 2f) / sin((itemAngleStepRadians) / 2f)).toFloat()
		indexOffset = (itemOnScreen - 1) / 2
	}

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		val baseHeight = circleRadius * 2
		val height = baseHeight + paddingTop + paddingBottom
		val heightSpec = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
		super.onMeasure(widthMeasureSpec, heightSpec)
		drawBounds.set(paddingLeft.toFloat(),
		               paddingTop.toFloat(),
		               measuredWidth - paddingRight.toFloat(),
		               measuredHeight - paddingBottom.toFloat())
		L.d("drawBounds:$drawBounds")
		resetState()
	}

	private fun resetState() {
		centerItemIndex = 0
		fluctuation = 0
	}

	override fun onDraw(canvas : Canvas) {
		super.onDraw(canvas)
		if (itemDrawer == null) return
		val itemCount = itemDrawer !!.getItemCount()
		for (i in - indexOffset - 1 .. indexOffset + 1) {
			val originIndex = centerItemIndex + i
			val index = originIndex.safeLoopCollectionIndex(itemCount)
			val canBeDraw = (enableLoop || (originIndex == index))
			val itemTopAngle = (i + indexOffset) * itemAngleStep + fluctuation
			if (itemTopAngle !in - halfItemAngleStep .. 180 - halfItemAngleStep) {
				continue
			}
			val t01 = if (itemTopAngle > 90) {
				drawBounds.top + circleRadius + circleRadius * cosDegree(180 - itemTopAngle.toDouble()).toFloat()
			} else {
				(drawBounds.top + circleRadius - circleRadius * cosDegree(itemTopAngle.toDouble())).toFloat()
			}
			// item对应的圆上的切面在屏幕上的投影高度
			val projectionHeight : Float = abs((centerItemHeight * cosDegree(((itemCornerAngle - itemTopAngle).toDouble()))).toFloat())
			itemRectCache.set(drawBounds.left,
			                  max(t01, drawBounds.top),
			                  drawBounds.right,
			                  min(t01 + projectionHeight, drawBounds.bottom))
			if (canBeDraw) {
				itemDrawer !!.drawItem(index, canvas, itemRectCache, itemTopAngle, itemAngleStep)
			}
		}

		if (fluctuation > itemAngleStep) {
			// 滚轮从右侧看逆时针滚动，从正面看向下滚动
			// 中间项变为上一项
			centerItemIndex = (centerItemIndex - 1).safeLoopCollectionIndex(itemCount)
			fluctuation = 0
		} else if (fluctuation < 0) {
			// 滚轮从右侧看顺时针滚动，从正面看向上滚动
			// 中间项变为下一项
			centerItemIndex = (centerItemIndex + 1).safeLoopCollectionIndex(itemCount)
			fluctuation = itemAngleStep
		}
	}

	fun add() {
		fluctuation ++
		invalidate()
	}

	fun minus() {
		fluctuation --
		invalidate()
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
				fluctuation += (diffY / resistance).toInt()
				invalidate()
				speed = diffY
				lastEvent.set(event.rawX, event.rawY)
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
	private fun autoScrollAnimation() {
		L.d("execute animation")
		va?.cancel()
		val start = fluctuation
		val end = if (abs(speed) > SPEED_LIMIT) {
			if (speed > 0) {
				itemAngleStep + 1
			} else {
				- 1
			}
		} else {
			val quarter = itemAngleStep / 4
			val minusQuarter = itemAngleStep - quarter
			when (start) {
				in 0 .. quarter -> {
					- 1
				}
				in quarter .. minusQuarter -> {
					0
				}
				else                       -> {
					itemAngleStep + 1
				}
			}
		}
		va = ValueAnimator.ofInt(start, end).also {
			it.addUpdateListener { va ->
				fluctuation = va.animatedValue as Int
				invalidate()
			}
			it.duration = DURATION
		}
		va?.start()
	}

	fun setItemDrawer(itemDrawer : ItemDrawer) {
		this.itemDrawer = itemDrawer.also {
			it.attachWheelView(this)
		}
		invalidate()
	}

	abstract class ItemDrawer {
		private var wheelView : CustomWheelView? = null

		/**
		 * @param itemTopAngle item顶部与圆心连线与屏幕的夹角（度数），当该值为0时，item的顶部和滚轮的顶部对齐
		 * @param itemAngleSpan item跨过的度数
		 */
		abstract fun drawItem(position : Int, canvas : Canvas,
		                      rect : RectF, itemTopAngle : Int, itemAngleSpan : Int)

		abstract fun getItemCount() : Int

		fun attachWheelView(wheelView : CustomWheelView) {
			this.wheelView = wheelView
		}

		fun notifyDataSetChanged() {
			if (wheelView != null) {
				this.wheelView?.resetState()
				this.wheelView?.invalidate()
			}
		}
	}

	class TextItemDrawer<T>(val gravity : Gravity = Gravity.Center, private val stringFactory : ((T) -> String)? = null) :
			ItemDrawer() {
		enum class Gravity {
			Start, Center, Right
		}

		private var items : List<T>? = null
			set(value) {
				field = value
				notifyDataSetChanged()
			}
		var textSize : Float = 60f
			set(value) {
				field = value
				notifyDataSetChanged()
			}
		var textColor : Int = Color.WHITE
			set(value) {
				field = value
				notifyDataSetChanged()
			}
		private val paint = Paint().also {
			it.textSize = textSize
			it.color = textColor
		}
		private val textHeight get() = paint.descent() - paint.ascent()

		override fun drawItem(position : Int, canvas : Canvas, rect : RectF, itemTopAngle : Int, itemAngleSpan : Int) {
			items?.getOrNull(position)?.let {
				val central = itemTopAngle + itemAngleSpan / 2f
				val ratio : Float = 1 - abs(central - 90f) / 90f
				val color = ratio.lerpColor(Color.RED, Color.GREEN)
				paint.color = color
				canvas.drawRect(rect, paint)

				paint.color = textColor
				paint.textSize = textSize * (0.5f + ratio * 0.5f)
				L.d("$position text size:${paint.textSize}")
				val text = stringFactory?.invoke(it) ?: it.toString()
				val textWidth = paint.measureText(text)
				val textBaseLine = rect.bottom - (rect.height() - textHeight) / 2f - paint.descent()
				val textStart = when (gravity) {
					Gravity.Start -> rect.left
					Gravity.Center -> (rect.width() - textWidth) / 2f
					Gravity.Right -> rect.right - textWidth
				}
				canvas.drawText(text, textStart, textBaseLine, paint)
			}
		}

		override fun getItemCount() : Int = items?.size ?: 0

		fun setData(data : List<T>) {
			items = data
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

		fun Float.lerpColor(@ColorInt start : Int, @ColorInt end : Int) : Int {
			val a = this.lerpValue(Color.alpha(start), Color.alpha(end))
			val r = this.lerpValue(Color.red(start), Color.red(end))
			val g = this.lerpValue(Color.green(start), Color.green(end))
			val b = this.lerpValue(Color.blue(start), Color.blue(end))
			return Color.argb(a, r, g, b)
		}

		fun Float.lerpValue(start : Int, end : Int) : Int {
			return ((end - start) * this + start).roundToInt()
		}
	}
}