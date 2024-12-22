package com.puutaro.commandclick.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.R


class OutlineTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

//    private var isRevOutLine = false

    var strokeWidthSrc = 2
    private var strokeColor = Color.WHITE
    private var fillColor = context.getColor(R.color.fill_gray)
    private val densityForOutline = context.resources.displayMetrics.density

    fun setStrokeColor(id: Int){
        strokeColor = try {
            ContextCompat.getColor(context, id)
        } catch (e: Exception){
            id
        }
    }

    fun setFillColor(id: Int){
        fillColor = try {
            ContextCompat.getColor(context, id)
        } catch (e: Exception){
            id
        }

    }
    override fun onDraw(canvas: Canvas) {
        setTextColor(strokeColor)
        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthSrc * densityForOutline
        }
        super.onDraw(canvas)

        setTextColor(fillColor)
        paint.apply {
            style = Paint.Style.FILL
            strokeWidth = 0f
        }
        super.onDraw(canvas)


//        paint.isAntiAlias = true
//
//        // グラデーションの設定 (例: 赤から青へのグラデーション)
//        val gradient = LinearGradient(0f, 0f, width.toFloat(), 0f, Color.RED, Color.BLUE, Shader.TileMode.CLAMP)
//        paint.shader = gradient
//
//        // 最初の文字の幅を取得
//        val firstCharSrc = text.toString().firstOrNull()?.toString() ?: return
//        val firstCharWidth = paint.measureText(firstCharSrc)
//
//        // 最初の文字をグラデーションで描画
//        paint.shader = gradient
//        canvas?.drawText(firstCharSrc, 0f, baseline.toFloat(), paint)

//        // 残りの文字を通常のテキストで描画
//        paint.shader = null
//        canvas.drawText(text.toString().substring(1), firstCharWidth, baseline, paint)
    }
}