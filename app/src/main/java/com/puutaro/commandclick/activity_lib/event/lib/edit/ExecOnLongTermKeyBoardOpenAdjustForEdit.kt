package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object ExecOnLongTermKeyBoardOpenAdjustForEdit {
    fun adjust(
        activity: MainActivity,
        weight: Float
    ){
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            SharePreferenceMethod.getStringFromSharePreference(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreferenceMethod.getStringFromSharePreference(
                sharePref,
                SharePrefferenceSetting.current_fannel_name
            ),
            FragmentTagManager.Suffix.ON.str
        )
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