package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment

object ExecUrlLoadFragmentProccess {
    fun execUrlLoadCmdIndexFragment (
        activity: MainActivity,
    ){
        val cmdIndexFragmentTag = activity.getString(R.string.command_index_fragment)
        val supportFragmentManager = activity.supportFragmentManager
        val cmdIndexFragment = try {
            supportFragmentManager.findFragmentByTag(cmdIndexFragmentTag) as CommandIndexFragment
        } catch(e: java.lang.Exception){
            Log.d(this.toString(), "not exist ${cmdIndexFragmentTag}")
            return
        }
        if(!cmdIndexFragment.isVisible) return
        val context = cmdIndexFragment.context
        val binding = cmdIndexFragment.binding
//        val cmdindexInternetButton = binding.cmdindexInternetButton
//        binding.cmdListSwipeToRefresh.isVisible = false
//        context?.let {
//            cmdindexInternetButton.setImageResource(
//                SearchSwichImage.WEB.image
//            )
//            cmdindexInternetButton.imageTintList = context.getColorStateList(R.color.terminal_color)
//            cmdindexInternetButton.setBackgroundTintList(it.getColorStateList(R.color.icon_selected_color))
//            (it.getColor(R.color.white))
//        }
        val linearLayoutParam =
            cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
        if(linearLayoutParam.weight == ReadLines.LONGTH) {
            ExecTerminalLongOrShort.open<CommandIndexFragment>(
                cmdIndexFragmentTag,
                supportFragmentManager,
            )
        }
        cmdIndexFragment.WebSearchSwitch = true
    }


//    fun execUrlLoadCmdVriableEditFragment (
//        activity: MainActivity,
//    ){
//        val sharePref = FannelInfoTool.getSharePref(activity)
////        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
////            sharePref,
////            FannelInfoSetting.current_app_dir
////        )
//        val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
//            sharePref,
//            FannelInfoSetting.current_fannel_name
//        )
//        val currentFannelState = FannelInfoTool.getStringFromFannelInfo(
//            sharePref,
//            FannelInfoSetting.current_fannel_state
//        )
//        val cmdEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
////            currentAppDirPath,
//            currentFannelName,
//            currentFannelState
//        )
//        val supportFragmentManager = activity.supportFragmentManager
//        val editFragment = try {
//            supportFragmentManager.findFragmentByTag(
//                cmdEditFragmentTag
//            ) as EditFragment
//        } catch(e: java.lang.Exception){
//            Log.d(this.toString(), "not exist ${cmdEditFragmentTag}")
//            return
//        }
//        if(!editFragment.isVisible) return
//        EditLayoutViewHideShow.exec(
//            editFragment,
//            false
//        )
//        val linearLayoutParam =
//            editFragment.binding.editFragment.layoutParams as LinearLayoutCompat.LayoutParams
//        if(
//            linearLayoutParam.weight != ReadLines.LONGTH
//        ) return
//        ExecTerminalLongOrShort.open<CommandIndexFragment>(
//            cmdEditFragmentTag,
//            supportFragmentManager,
//        )
//
//    }
}