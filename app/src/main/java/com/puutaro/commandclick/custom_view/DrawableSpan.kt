package com.puutaro.commandclick.custom_view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan

class DrawableSpan(private val drawable: Drawable?) : ReplacementSpan() {
    private val padding: Rect = Rect()

    init {
        drawable?.getPadding(padding)
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val rect = RectF(x, top.toFloat(), x + measureText(paint, text, start, end), bottom.toFloat())
        drawable?.setBounds(rect.left.toInt() - padding.left, rect.top.toInt() - padding.top, rect.right.toInt() + padding.right, rect.bottom.toInt() + padding.bottom)
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
        drawable?.draw(canvas)
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int = Math.round(paint.measureText(text, start, end))

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float = paint.measureText(text, start, end)

}