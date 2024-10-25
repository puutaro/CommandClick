package com.puutaro.commandclick.proccess.shape

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.GradientDrawable

class GradientAnimationDrawable(
    start: Int = Color.rgb(0, 143, 209),
    center: Int = Color.rgb(1, 106, 154),
    end: Int = Color.rgb(28, 179, 249),
    frameDuration: Int = 3000,
    enterFadeDuration: Int = 0,
    exitFadeDuration: Int = 3000
) : AnimationDrawable() {

    private val gradientStart = GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, intArrayOf(start, center, end))
        .apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

    private val gradientCenter = GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, intArrayOf(center, end, start))
        .apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

    private val gradientEnd = GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, intArrayOf(end, start, center))
        .apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

    init {
        addFrame(gradientStart, frameDuration)
        addFrame(gradientCenter, frameDuration)
        addFrame(gradientEnd, frameDuration)
        setEnterFadeDuration(enterFadeDuration)
        setExitFadeDuration(exitFadeDuration)
        isOneShot = false
    }

}
