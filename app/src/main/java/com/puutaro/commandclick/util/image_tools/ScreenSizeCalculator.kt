package com.puutaro.commandclick.util.image_tools

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.math.roundToInt

object ScreenSizeCalculator {

    fun getScreenHeight(
        activity: FragmentActivity?,
    ): Int {
        val dpHeight = dpHeight(
            activity
        )
        val hideShowRate =
            if(dpHeight > 670f) 3.0f
            else if(dpHeight > 630) 3.5F
            else 4.0f
        return -(dpHeight / hideShowRate).toInt()
    }

    fun dpHeight(
        activity: Activity?
    ): Float {
        val defaultDpheight = 600f
        val density =  activity?.resources?.displayMetrics?.density
            ?: return defaultDpheight
        if(density == 0F) return defaultDpheight
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                activity.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.height() / density
        } else {
            val display = activity.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.heightPixels / density
        }
    }

    fun dpWidth(
        activity: Activity?
    ): Float {
        val defaultDpheight = 600f
        val density =  activity?.resources?.displayMetrics?.density
            ?: return defaultDpheight
        if(density == 0F) return defaultDpheight
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                activity.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.width() / density
        } else {
            val display = activity.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.widthPixels / density
        }
    }

    fun pxWidth(
        fragmentActivity: FragmentActivity?,
    ): Int {
        val defaultDpheight = 600
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics = fragmentActivity?.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.width()
        } else {
            val display = fragmentActivity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.widthPixels
        }
    }

    fun pxHeight(
        fragmentActivity: FragmentActivity?,
    ): Int {
        val defaultDpheight = 600
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                fragmentActivity?.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.width()
        } else {
            val display = fragmentActivity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.heightPixels
        }
    }

    fun getDensity(
        context: Context?
    ): Float {
        if(
            context == null
        ) return 0f
        return context.resources?.displayMetrics?.density ?: 0f
    }

    fun <T: Number>toDp(
        context: Context?,
        dps: T,
    ): Int {
        if(
            context == null
        ) return 0
        val density = context.resources.displayMetrics.density
        return (dps.toFloat() * density).roundToInt()
    }

    fun <T: Number>toDpByDensity(
        dps: T,
        density: Float,
    ): Int {
        return (dps.toFloat() * density).roundToInt()
    }

    fun toDpForFloat(
        context: Context?,
        dps: Float,
    ): Float {
        if(
            context == null
        ) return 0f
        val density = context.resources.displayMetrics.density
        return (dps * density)
    }

    fun toDpForFloatByDensity(
        dps: Float,
        density: Float,
    ): Float {
        return (dps * density)
    }

    fun <T: Number>toPx(
        context: Context?,
        px: T,
    ): Int{
        if(
            context == null
        ) return 0
        val density = context.resources.displayMetrics.density
        return (px.toFloat() / density).roundToInt()
    }
}
