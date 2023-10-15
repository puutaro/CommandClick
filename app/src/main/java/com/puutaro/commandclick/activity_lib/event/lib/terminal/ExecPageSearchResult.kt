package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod

object ExecPageSearchResult {
    fun reflect(
        activity: MainActivity,
        activeMatchOrdinal: Int,
        numberOfMatches: Int
    ) {
        val cmdIndexFragment = try {
            activity.supportFragmentManager.findFragmentByTag(
                activity.getString(R.string.command_index_fragment)
            ) as CommandIndexFragment
        } catch (e: Exception) {
            null
        }
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
        val cmdEditFragment = try {
            activity.supportFragmentManager.findFragmentByTag(
                cmdEditFragmentTag
            ) as EditFragment
        } catch (e: Exception) {
            null
        }
        val isVisibleCommandIndexFragment = cmdIndexFragment?.isVisible == true
        val isVisibleCmdEditFragment = cmdEditFragment?.isVisible == true
        if(
            isVisibleCommandIndexFragment
            && isVisibleCmdEditFragment
        ) return
        if(
            isVisibleCommandIndexFragment
            && cmdIndexFragment != null
        ){
            execReflectForCmdIndex(
                activity,
                activeMatchOrdinal,
                numberOfMatches,
                cmdIndexFragment,
            )
            return
        }
        if(
            cmdEditFragment == null
        ) return
        execReflectForCmdEdit(
            activity,
            activeMatchOrdinal,
            numberOfMatches,
            cmdEditFragment,
        )

    }
}

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
            activity.getColor(R.color.black)
        )
    }
}

private fun execReflectForCmdEdit(
    activity: MainActivity,
    activeMatchOrdinal: Int,
    numberOfMatches: Int,
    cmdEditFragment: EditFragment,
){
    val binding = cmdEditFragment.binding
    val pageSearch = binding.pageSearch
    val cmdEditSearchTotal = pageSearch.cmdindexSearchTotal
    val displayActivePerTotal = "${activeMatchOrdinal}/${numberOfMatches}"
    cmdEditSearchTotal.setText(displayActivePerTotal)
    if (numberOfMatches == 0) {
        cmdEditSearchTotal.setTextColor(
            activity.getColor(com.termux.shared.R.color.dark_red)
        )
    } else {
        cmdEditSearchTotal.setTextColor(
            activity.getColor(R.color.black)
        )
    }
}