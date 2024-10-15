package com.puutaro.commandclick.custom_view

import com.puutaro.commandclick.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Pair
import java.util.WeakHashMap


//class MagicTextView : androidx.appcompat.widget.AppCompatTextView {
//    private var outerShadows: ArrayList<Shadow>? = null
//    private var innerShadows: ArrayList<Shadow>? = null
//    private var canvasStore: WeakHashMap<String, Pair<Canvas, Bitmap?>>? = null
//    private var tempCanvas: Canvas? = null
//    private var tempBitmap: Bitmap? = null
//    private var foregroundDrawable: Drawable? = null
//    private var strokeWidth = 0f
//    private var strokeColor: Int? = null
//    private var strokeJoin: Paint.Join? = null
//    private var strokeMiter = 0f
//    private lateinit var lockedCompoundPadding: IntArray
//    private var frozen = false
//    private val porterDuffXferModeSrcTop = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
//    private val porterDuffXfermodeDstTop = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
//
//    constructor(context: Context) : super(context) {
//        init(null)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        init(attrs)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
//        context,
//        attrs,
//        defStyle
//    ) {
//        init(attrs)
//    }
//
//    fun init(attrs: AttributeSet?) {
//        outerShadows = ArrayList()
//        innerShadows = ArrayList()
//        if (canvasStore == null) {
//            canvasStore = WeakHashMap()
//        }
//        attrs?.let {
//            val a = context.obtainStyledAttributes(it, R.styleable.MagicTextView)
//            val typefaceName = a.getString(R.styleable.MagicTextView_typeface)
//            typefaceName?.let {
//                val tf = Typeface.createFromAsset(
//                    context.assets,
//                    String.format("fonts/%s.ttf", it)
//                )
//                setTypeface(tf)
//            }
//            if (
//                a.hasValue(R.styleable.MagicTextView_foreground)
//            ) {
//                val foreground = a.getDrawable(R.styleable.MagicTextView_foreground)
//                foreground?.let {
//                    setForegroundDrawable(it)
//                } ?: let {
//                    this.setTextColor(a.getColor(R.styleable.MagicTextView_foreground, -0x1000000))
//                }
//            }
//            if (
//                a.hasValue(R.styleable.MagicTextView_mBackground)
//            ) {
//                val background = a.getDrawable(R.styleable.MagicTextView_mBackground)
//                background?.let {
//                    setBackground(background)
//                } ?: let {
//                    setBackgroundColor(a.getColor(R.styleable.MagicTextView_mBackground, -0x1000000))
//                }
//            }
//            if (
//                a.hasValue(R.styleable.MagicTextView_innerShadowColor)
//            ) {
//                addInnerShadow(
//                    a.getDimensionPixelSize(R.styleable.MagicTextView_innerShadowRadius, 0)
//                        .toFloat(),
//                    a.getDimensionPixelOffset(R.styleable.MagicTextView_innerShadowDx, 0).toFloat(),
//                    a.getDimensionPixelOffset(R.styleable.MagicTextView_innerShadowDy, 0).toFloat(),
//                    a.getColor(R.styleable.MagicTextView_innerShadowColor, -0x1000000)
//                )
//            }
//            if (
//                a.hasValue(R.styleable.MagicTextView_outerShadowColor)
//            ) {
//                addOuterShadow(
//                    a.getDimensionPixelSize(R.styleable.MagicTextView_outerShadowRadius, 0)
//                        .toFloat(),
//                    a.getDimensionPixelOffset(R.styleable.MagicTextView_outerShadowDx, 0).toFloat(),
//                    a.getDimensionPixelOffset(R.styleable.MagicTextView_outerShadowDy, 0).toFloat(),
//                    a.getColor(R.styleable.MagicTextView_outerShadowColor, -0x1000000)
//                )
//            }
//            if (
//                a.hasValue(R.styleable.MagicTextView_strokeColor)
//            ) {
//                val strokeWidth =
//                    a.getDimensionPixelSize(R.styleable.MagicTextView_strokeWidth, 1).toFloat()
//                val strokeColor = a.getColor(R.styleable.MagicTextView_strokeColor, -0x1000000)
//                val strokeMiter =
//                    a.getDimensionPixelSize(R.styleable.MagicTextView_strokeMiter, 10).toFloat()
//                var strokeJoin: Paint.Join? = null
//                when (a.getInt(R.styleable.MagicTextView_strokeJoinStyle, 0)) {
//                    0 -> strokeJoin = Paint.Join.MITER
//                    1 -> strokeJoin = Paint.Join.BEVEL
//                    2 -> strokeJoin = Paint.Join.ROUND
//                }
//                this.setStroke(strokeWidth, strokeColor, strokeJoin, strokeMiter)
//            }
//            a.recycle()
//        }
//        val innerShadowList = innerShadows
//        val isInnerShadowList =
//            innerShadowList != null
//                && innerShadowList.size > 0
//        if (
//            isInnerShadowList
//            || foregroundDrawable != null
//        ) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null)
//        }
//    }
//
//    private fun setStroke(width: Float, color: Int, join: Paint.Join?, miter: Float) {
//        strokeWidth = width
//        strokeColor = color
//        strokeJoin = join
//        strokeMiter = miter
//    }
//
////    fun setStroke(width: Float, color: Int) {
////        setStroke(width, color, Paint.Join.MITER, 10f)
////    }
//
//    fun addOuterShadow(r: Float, dx: Float, dy: Float, color: Int) {
//        val rVal = when (r == 0f) {
//            true -> 0.0001f
//            else -> r
//        }
//        outerShadows?.add(Shadow(rVal, dx, dy, color))
//    }
//
//    private fun addInnerShadow(r: Float, dx: Float, dy: Float, color: Int) {
//        val rVal = when (r == 0f) {
//            true -> 0.0001f
//            else -> r
//        }
//        innerShadows?.add(Shadow(rVal, dx, dy, color))
//    }
//
////    fun clearInnerShadows() {
////        innerShadows!!.clear()
////    }
////
////    fun clearOuterShadows() {
////        outerShadows!!.clear()
////    }
//
//    private fun setForegroundDrawable(d: Drawable?) {
//        foregroundDrawable = d
//    }
//
//    override fun getForeground(): Drawable? {
//        return if (
//            foregroundDrawable == null
//        ) foregroundDrawable
//        else ColorDrawable(this.currentTextColor)
//    }
//
//    public override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        freeze()
//        val restoreBackground = this.background
//        val restoreDrawables = this.compoundDrawables
//        val restoreColor = this.currentTextColor
//        setCompoundDrawables(null, null, null, null)
//        outerShadows?.forEach {
//            shadow ->
//            setShadowLayer(shadow.r, shadow.dx, shadow.dy, shadow.color)
//            super.onDraw(canvas)
//        }
//        setShadowLayer(0f, 0f, 0f, 0)
//        this.setTextColor(restoreColor)
//        if (
//            foregroundDrawable != null
//            && foregroundDrawable is BitmapDrawable
//        ) {
//            generateTempCanvas()
//            super.onDraw(tempCanvas)
//            val paint = (foregroundDrawable as BitmapDrawable).paint
//            paint.xfermode = porterDuffXferModeSrcTop
//            foregroundDrawable?.bounds = canvas.clipBounds
//            tempCanvas?.let {
//                foregroundDrawable?.draw(it)
//            }
//            tempBitmap?.let {
//                canvas.drawBitmap(it, 0f, 0f, null)
//            }
//            tempCanvas?.let {
//                tempCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//            }
//
//        }
//        strokeColor?.let {
//            val paint = this.paint
//            paint.style = Paint.Style.STROKE
//            paint.strokeJoin = strokeJoin
//            paint.strokeMiter = strokeMiter
//            this.setTextColor(it)
//            paint.strokeWidth = strokeWidth
//            super.onDraw(canvas)
//            paint.style = Paint.Style.FILL
//            this.setTextColor(restoreColor)
//        }
//        val innerShadowList = innerShadows
//        if (
//            innerShadowList != null
//            && innerShadowList.size > 0
//        ) {
//            generateTempCanvas()
//            val paint = this.paint
//            for (shadow in innerShadowList) {
//                this.setTextColor(shadow.color)
//                super.onDraw(tempCanvas)
//                this.setTextColor(-0x1000000)
//                paint.xfermode = porterDuffXfermodeDstTop
//                paint.maskFilter = BlurMaskFilter(shadow.r, BlurMaskFilter.Blur.NORMAL)
//                tempCanvas?.save()
//                tempCanvas?.translate(shadow.dx, shadow.dy)
//                super.onDraw(tempCanvas)
//                tempCanvas?.restore()
//                tempBitmap?.let {
//                    canvas.drawBitmap(it, 0f, 0f, null)
//                }
//                tempCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//                paint.xfermode = null
//                paint.maskFilter = null
//                this.setTextColor(restoreColor)
//                setShadowLayer(0f, 0f, 0f, 0)
//            }
//        }
//        this.setCompoundDrawablesWithIntrinsicBounds(
//            restoreDrawables[0],
//            restoreDrawables[1],
//            restoreDrawables[2],
//            restoreDrawables[3]
//        )
//        background = restoreBackground
////        setBackgroundDrawable(restoreBackground)
//        this.setTextColor(restoreColor)
//        unfreeze()
//    }
//
//    private fun generateTempCanvas() {
//        val key = String.format("%dx%d", width, height)
//        val stored = canvasStore?.get(key)
//        when (stored != null) {
//            true -> {
//                tempCanvas = stored.first
//                tempBitmap = stored.second
//            }
//            else -> {
//                tempCanvas = Canvas()
//                tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                tempCanvas?.setBitmap(tempBitmap)
//                canvasStore?.put(key, Pair(tempCanvas, tempBitmap))
//            }
//        }
//    }
//
//    // Keep these things locked while onDraw in processing
//    private fun freeze() {
//        lockedCompoundPadding = intArrayOf(
//            compoundPaddingLeft,
//            compoundPaddingRight,
//            compoundPaddingTop,
//            compoundPaddingBottom
//        )
//        frozen = true
//    }
//
//    private fun unfreeze() {
//        frozen = false
//    }
//
//    override fun requestLayout() {
//        if (!frozen) super.requestLayout()
//    }
//
//    override fun postInvalidate() {
//        if (!frozen) super.postInvalidate()
//    }
//
//    override fun postInvalidate(left: Int, top: Int, right: Int, bottom: Int) {
//        if (!frozen) super.postInvalidate(left, top, right, bottom)
//    }
//
//    override fun invalidate() {
//        if (!frozen) super.invalidate()
//    }
//
////    override fun invalidate(rect: Rect) {
////        if (!frozen) super.invalidate(rect)
////    }
////
////    override fun invalidate(l: Int, t: Int, r: Int, b: Int) {
////        if (!frozen) super.invalidate(l, t, r, b)
////    }
//
//    override fun getCompoundPaddingLeft(): Int {
//        return if (!frozen) super.getCompoundPaddingLeft() else lockedCompoundPadding[0]
//    }
//
//    override fun getCompoundPaddingRight(): Int {
//        return if (!frozen) super.getCompoundPaddingRight() else lockedCompoundPadding[1]
//    }
//
//    override fun getCompoundPaddingTop(): Int {
//        return if (!frozen) super.getCompoundPaddingTop() else lockedCompoundPadding[2]
//    }
//
//    override fun getCompoundPaddingBottom(): Int {
//        return if (!frozen) super.getCompoundPaddingBottom() else lockedCompoundPadding[3]
//    }
//
//    class Shadow(var r: Float, var dx: Float, var dy: Float, var color: Int)
//}