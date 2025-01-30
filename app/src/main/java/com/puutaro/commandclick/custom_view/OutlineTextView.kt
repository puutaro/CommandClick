package com.puutaro.commandclick.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
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

    var outlineWidthSrc = 2
    private var strokeColor = Color.WHITE
    private var fillColor = context.getColor(R.color.fill_gray)
    private val densityForOutline = context.resources.displayMetrics.density

    fun setStrokeColor(id: Int){
        strokeColor = context.getColor(id)
    }

    fun setFillColor(id: Int){
        fillColor = context.getColor(id)
    }
    override fun onDraw(canvas: Canvas) {
//        val strokeColor = when(
//            isRevOutLine
//        ){
//            true -> Color.BLACK
//            else -> Color.WHITE
//        }
        setTextColor(strokeColor)
        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = outlineWidthSrc * densityForOutline
        }
        super.onDraw(canvas)

//        val fillColor = when(
//            isRevOutLine
//        ){
//            true -> Color.WHITE
//            else -> Color.BLACK
//        }
        setTextColor(fillColor)
        paint.apply {
            style = Paint.Style.FILL
            strokeWidth = 0f
        }
        super.onDraw(canvas)
    }

//    fun revOutline(isRev: Boolean){
//        isRevOutLine = isRev
//
//    }
}