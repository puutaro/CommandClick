package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.util.Log
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.fragment.CommandIndexFragment

object ExecCmdListAjustForKeyboard {
    fun ajust(
        cmdIndexfragmentTag: String,
        supportFragmentManager: FragmentManager,
        weight: Float
    ){
        val targetCmdIndexFragment = try {
            supportFragmentManager.findFragmentByTag(cmdIndexfragmentTag) as CommandIndexFragment
        } catch(e: java.lang.Exception){
            Log.d(this.toString(), "not exist ${cmdIndexfragmentTag}")
            return
        }
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        param.weight = weight
        targetCmdIndexFragment.view?.layoutParams = param
    }
}