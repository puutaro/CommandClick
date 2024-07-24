package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool

object ExecOnLongTermKeyBoardOpenAdjustForEdit {
    fun adjust(
        activity: MainActivity,
        weight: Float
    ){
        val sharePref = FannelInfoTool.getSharePref(activity)
        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
            sharePref,
            FannelInfoSetting.current_app_dir
        )
        val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
            sharePref,
            FannelInfoSetting.current_fannel_name
        )
        val currentFannelState = FannelInfoTool.getStringFromFannelInfo(
            sharePref,
            FannelInfoSetting.current_fannel_state
        )
        val cmdEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
            currentAppDirPath,
            currentFannelName,
            currentFannelState,
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