package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod

object ExecOnLongTermKeyBoardOpenAdjustForEdit {
    fun adjust(
        activity: MainActivity,
        weight: Float
    ){
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_script_file_name
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