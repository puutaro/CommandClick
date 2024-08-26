package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

object ScreenSizeCalculator {
    fun dpHeight(
        fragment: Fragment
    ): Float {
        val defaultDpheight = 600f
        val density =  fragment.activity?.resources?.displayMetrics?.density
            ?: return defaultDpheight
        if(density == 0F) return defaultDpheight
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                fragment.activity?.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.height() / density
        } else {
            val display = fragment.activity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.heightPixels / density
        }
    }

    fun dpWidth(
        fragment: Fragment
    ): Float {
        val defaultDpheight = 600f
        val density =  fragment.activity?.resources?.displayMetrics?.density
            ?: return defaultDpheight
        if(density == 0F) return defaultDpheight
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                fragment.activity?.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.width() / density
        } else {
            val display = fragment.activity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.widthPixels / density
        }
    }

    fun pxWidth(
        fragment: Fragment
    ): Int {
        val defaultDpheight = 600
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                fragment.activity?.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.width()
        } else {
            val display = fragment.activity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.widthPixels
        }
    }

    fun pxHeight(
        fragment: Fragment
    ): Int {
        val defaultDpheight = 600
        return if(
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                fragment.activity?.windowManager?.currentWindowMetrics
                    ?: return defaultDpheight
            windowMetrics.bounds.width()
        } else {
            val display = fragment.activity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.heightPixels
        }
    }

    fun <T: Number>toDp(
        context: Context?,
        dps: T,
    ): Int{
        if(
            context == null
        ) return 0
        val density = context.resources.displayMetrics.density
        return (dps.toFloat() * density).roundToInt()
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
