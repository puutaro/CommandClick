package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool

//object ExecPageSearchResult {
//    fun reflect(
//        activity: MainActivity,
//        activeMatchOrdinal: Int,
//        numberOfMatches: Int
//    ) {
//        val cmdIndexFragment = try {
//            activity.supportFragmentManager.findFragmentByTag(
//                activity.getString(R.string.command_index_fragment)
//            ) as CommandIndexFragment
//        } catch (e: Exception) {
//            null
//        }
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
//        val cmdEditFragment = try {
//            activity.supportFragmentManager.findFragmentByTag(
//                cmdEditFragmentTag
//            ) as EditFragment
//        } catch (e: Exception) {
//            null
//        }
//        val isVisibleCommandIndexFragment = cmdIndexFragment?.isVisible == true
//        val isVisibleCmdEditFragment = cmdEditFragment?.isVisible == true
//        if(
//            isVisibleCommandIndexFragment
//            && isVisibleCmdEditFragment
//        ) return
//        if(
//            isVisibleCommandIndexFragment
//            && cmdIndexFragment != null
//        ){
//            execReflectForCmdIndex(
//                activity,
//                activeMatchOrdinal,
//                numberOfMatches,
//                cmdIndexFragment,
//            )
//            return
//        }
//        if(
//            cmdEditFragment == null
//        ) return
////        execReflectForCmdEdit(
////            activity,
////            activeMatchOrdinal,
////            numberOfMatches,
////            cmdEditFragment,
////        )
//
//    }
//}

private fun execReflectForCmdIndex(
    activity: MainActivity,
    activeMatchOrdinal: Int,
    numberOfMatches: Int,
    cmdIndexFragment: CommandIndexFragment,
){
    val binding = cmdIndexFragment.binding
    val pageSearch = binding.pageSearch
    val cmdindexSearchTotal = pageSearch.cmdindexSearchTotal
    val displayActivePerTotal = "${activeMatchOrdinal}/${numberOfMatches}"
    cmdindexSearchTotal.setText(displayActivePerTotal)
    if (numberOfMatches == 0) {
        cmdindexSearchTotal.setTextColor(
            activity.getColor(com.termux.shared.R.color.dark_red)
        )
    } else {
        cmdindexSearchTotal.setTextColor(
            activity.getColor(R.color.fill_gray)
        )
    }
}

//private fun execReflectForCmdEdit(
//    activity: MainActivity,
//    activeMatchOrdinal: Int,
//    numberOfMatches: Int,
//    cmdEditFragment: EditFragment,
//){
//    val binding = cmdEditFragment.binding
//    val pageSearch = binding.pageSearch
//    val cmdEditSearchTotal = pageSearch.cmdindexSearchTotal
//    val displayActivePerTotal = "${activeMatchOrdinal}/${numberOfMatches}"
//    cmdEditSearchTotal.setText(displayActivePerTotal)
//    if (numberOfMatches == 0) {
//        cmdEditSearchTotal.setTextColor(
//            activity.getColor(com.termux.shared.R.color.dark_red)
//        )
//    } else {
//        cmdEditSearchTotal.setTextColor(
//            activity.getColor(R.color.fill_gray)
//        )
//    }
//}