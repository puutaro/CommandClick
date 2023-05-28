package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.util.Log
import android.widget.LinearLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.EditFragment

object ExecOnLongTermKeyBoardOpenAdjustForEdit {
    fun adjust(
        activity: MainActivity,
        weight: Float
    ){
        val cmdEditFragmentTag = activity.getString(R.string.cmd_variable_edit_fragment)
        val targetFragment = try {
            activity.supportFragmentManager.findFragmentByTag(cmdEditFragmentTag) as EditFragment
        } catch(e: java.lang.Exception){
            Log.d(this.toString(), "not exist ${cmdEditFragmentTag}")
            return
        }
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        param.weight = weight
        targetFragment.view?.layoutParams = param
    }
}