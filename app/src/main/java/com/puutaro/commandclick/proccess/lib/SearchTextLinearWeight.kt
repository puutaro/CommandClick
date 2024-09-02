package com.puutaro.commandclick.proccess.lib

import android.app.Activity
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator

object SearchTextLinearWeight {

    fun calculate(
        activity: Activity?
    ): Float {
        val dpHeight = ScreenSizeCalculator.dpHeight(activity)
        val hideShowRate =
            if(dpHeight > 730f) 60f / dpHeight
            else if(dpHeight > 670f) 0.01f
            else if(dpHeight > 630) 0.05F
            else 0.1f
        return hideShowRate
    }
}