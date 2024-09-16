package com.puutaro.commandclick.custom_view

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.R

class ShadowConstraintLayout : ConstraintLayout {

    private var borderPaint = Paint()
    private var rectPaint = Paint()
    private var shadowPaint = Paint()

    private var shadowPath = Path()
    protected var clipPath = Path()
    private var rectBackgroundPath = Path()

    protected var clipRectF = RectF()
    private var rectBackgroundRectF = RectF()
    private var borderRectF = RectF()

    private val porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)

    private var shadowColor = Color.BLACK

    private var shadowStrokeWidth = 15f

    protected var blurRadius = 50f
    private var cornerRadius = 8f
    private var shadowStartOffset = 0f
    private var shadowEndOffset = 0f
    private var shadowTopOffset = 0f
    private var shadowBottomOffset = 0f
    private var shadowStartY = java.lang.Float.MIN_VALUE
    private var enableShadow = false
    private var enableBorder = false
    private var borderHeight = 0f
    private lateinit var blurMaskFilter: BlurMaskFilter

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    fun init(attrs: AttributeSet?) {
        readAttrs(attrs)
        blurMaskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }

    open fun readAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray =
                context.theme.obtainStyledAttributes(attrs, R.styleable.ShadowConstraintLayout, 0, 0)
            shadowTopOffset = typedArray.getDimension(R.styleable.ShadowConstraintLayout_shadowTopOffset, dpToPx(context, 0))
            shadowBottomOffset = typedArray.getDimension(R.styleable.ShadowConstraintLayout_shadowBottomOffset, dpToPx(context, 0))
            shadowStartOffset = typedArray.getDimension(R.styleable.ShadowConstraintLayout_shadowStartOffset, dpToPx(context, 0))
            shadowEndOffset = typedArray.getDimension(R.styleable.ShadowConstraintLayout_shadowEndOffset, dpToPx(context, 0))
            shadowStartY = typedArray.getDimension(
                R.styleable.ShadowConstraintLayout_shadowStartY,
                java.lang.Float.MIN_VALUE
            )
            shadowColor = typedArray.getColor(R.styleable.ShadowConstraintLayout_shadowColor, Color.BLACK)
            shadowStrokeWidth =
                typedArray.getDimension(
                    R.styleable.ShadowConstraintLayout_shadowStrokeWidth,
                    dpToPx(context, 1)
                )
            cornerRadius = typedArray.getDimension(
                R.styleable.ShadowConstraintLayout_cornerRadius,
                dpToPx(context, 3)
            )
            blurRadius =
                typedArray.getDimension(R.styleable.ShadowConstraintLayout_blurRadius, dpToPx(context, 8))
            enableShadow = typedArray.getBoolean(R.styleable.ShadowConstraintLayout_enableShadow, true)
            enableBorder = typedArray.getBoolean(R.styleable.ShadowConstraintLayout_enableBorder, false)
            borderHeight = typedArray.getDimension(R.styleable.ShadowConstraintLayout_borderHeight, 0f)
            typedArray.recycle()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        clipPath.reset()

        clipRectF.top = 0f
        clipRectF.left = 0f
        clipRectF.right = canvas.width.toFloat()
        clipRectF.bottom = canvas.height.toFloat()
        clipPath.addRoundRect(clipRectF, cornerRadius, cornerRadius, Path.Direction.CW)

        canvas.clipPath(clipPath)
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        if (enableShadow) {
            drawShadow(canvas)
        }
        drawRectBackground(canvas)
        if (enableBorder) {
            drawBorder(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawBorder(canvas: Canvas) {

        val borderColor = ContextCompat.getColor(context, R.color.file_dark_green_color)

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = borderColor
        borderPaint.strokeWidth = 0.5f

        borderRectF.top = height - borderHeight
        borderRectF.left = 0f
        borderRectF.right = width.toFloat()
        borderRectF.bottom = height.toFloat()
        canvas.drawRoundRect(borderRectF, cornerRadius, cornerRadius, borderPaint)
    }


    private fun drawRectBackground(canvas: Canvas) {

        rectPaint.style = Paint.Style.FILL
        rectPaint.color = Color.WHITE
        rectPaint.xfermode = porterDuffXfermode
        rectBackgroundRectF.top = 0f
        rectBackgroundRectF.left = 0f
        rectBackgroundRectF.right = width.toFloat()
        rectBackgroundRectF.bottom = height.toFloat()
        rectBackgroundPath.reset()
        rectBackgroundPath.addRect(rectBackgroundRectF, Path.Direction.CW)
        canvas.drawRoundRect(rectBackgroundRectF, cornerRadius, cornerRadius, rectPaint)
    }

    private fun drawShadow(canvas: Canvas) {
        canvas.save()
        shadowPaint.isAntiAlias = true
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.color = shadowColor
        shadowPaint.strokeWidth = shadowStrokeWidth
        shadowPaint.xfermode = porterDuffXfermode

        shadowPaint.maskFilter = blurMaskFilter

        shadowTopOffset = dpToPx(context,6)
        shadowBottomOffset = dpToPx(context,-2)
        shadowStartOffset = dpToPx(context,2)
        shadowEndOffset = dpToPx(context,-2)

        shadowPath.reset()
        shadowPath.moveTo((width + (shadowEndOffset)), shadowStartY + shadowTopOffset)               //Top Right
        shadowPath.lineTo((shadowStartOffset), shadowStartY+shadowTopOffset)                         // TR -> TL
        shadowPath.lineTo((shadowStartOffset), (height + shadowBottomOffset))                           // TL -> BL
        shadowPath.lineTo((width + shadowEndOffset), (height + shadowBottomOffset))                   // BL -> BR
        shadowPath.lineTo((width + shadowEndOffset), shadowStartY + shadowTopOffset)                  // BR -> TR

        canvas.drawPath(shadowPath, shadowPaint)
        canvas.restore()
    }

    fun dpToPx(context: Context, dp: Int): Float {
        return dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toFloat()
    }
}