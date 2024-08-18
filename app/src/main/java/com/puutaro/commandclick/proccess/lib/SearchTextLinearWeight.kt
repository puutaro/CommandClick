package com.puutaro.commandclick.proccess.lib

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator

object SearchTextLinearWeight {

    fun calculate(
        fragment: Fragment
    ): Float {
        val dpHeight = ScreenSizeCalculator.dpHeight(fragment)
        val hideShowRate =
            if(dpHeight > 730f) 60f / dpHeight
            else if(dpHeight > 670f) 0.01f
            else if(dpHeight > 630) 0.05F
            else 0.1f
        return hideShowRate
    }
}