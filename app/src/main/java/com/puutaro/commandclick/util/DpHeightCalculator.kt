package com.puutaro.commandclick.util

import android.os.Build
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment

object DpHeightCalculator {
    fun calculate(
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
}