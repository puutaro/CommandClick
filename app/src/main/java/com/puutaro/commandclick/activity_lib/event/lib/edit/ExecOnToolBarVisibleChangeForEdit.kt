package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.TerminalSizingForEdit
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod

object ExecOnToolBarVisibleChangeForEdit {
    fun execOnToolBarVisibleChangeForEdit(
        activity: MainActivity,
        toolBarVisible: Boolean
    ){
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        val cmdEditFragmentTag =
            FragmentTagManager.makeTag(
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
        val editFragment =
            try {
                activity.supportFragmentManager.findFragmentByTag(
                    cmdEditFragmentTag
                ) as EditFragment
            } catch (e: Exception) {
                return
            }
        val layoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        )
        layoutParam.weight = if(toolBarVisible) {
            TerminalSizingForEdit.VISIBLE.weight
        } else {
            TerminalSizingForEdit.INVISIBLE.weight
        }
        if(!editFragment.isVisible) return
        editFragment.binding.editFragment.layoutParams = layoutParam
    }
}
