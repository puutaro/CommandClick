package com.puutaro.commandclick.activity_lib.event

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCommandEdit
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecCancel
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOkForEdit
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod


object ExecToolBarButtonClickForEdit {
    fun execToolBarButtonClickForEdit(
        activity: MainActivity,
        callOwnerFragmentTag : String?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        readSharePreffernceMap: Map<String, String>,
        enableCmdEdit: Boolean,
        isLongPress: Boolean = false
    ){
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        when(toolbarButtonBariantForEdit){
            ToolbarButtonBariantForEdit.OK -> {
                okHandler(
                    activity,
                    callOwnerFragmentTag,
                    readSharePreffernceMap,
                    isLongPress
                )
            }
            ToolbarButtonBariantForEdit.HISTORY -> {
                cancelHandler(
                    activity,
                    enableCmdEdit
                )
            }
            ToolbarButtonBariantForEdit.EDIT -> {
                val onShortcut = FragmentTagManager.makeListFromTag(
                    callOwnerFragmentTag ?: String()
                ).getOrNull(FragmentTagManager.modeIndex)
                    ?: String()
                val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
                    sharePref,
                    SharePrefferenceSetting.current_app_dir
                )
                val currentFannelName = SharePreferenceMethod.getStringFromSharePreference(
                    sharePref,
                    SharePrefferenceSetting.current_fannel_name
                )
                val settingEditFragmentTag = FragmentTagManager.makeTag(
                    FragmentTagManager.Prefix.settingEditPrefix.str,
                    currentAppDirPath,
                    currentFannelName,
                    onShortcut
                )
                val readSharePreferenceMapForNext = EditFragmentArgs.createReadSharePreferenceMap(
                    currentAppDirPath,
                    currentFannelName,
                    onShortcut,
                )
                ExecCommandEdit.execCommandEdit(
                    activity,
                    settingEditFragmentTag,
                    EditFragmentArgs(readSharePreferenceMapForNext),
                )
            }
            ToolbarButtonBariantForEdit.CANCEL -> {
                activity.supportFragmentManager.popBackStackImmediate()
            }
            else -> {
                Toast.makeText(
                    activity,
                    "now inplementing",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


private fun okHandler(
    activity: MainActivity,
    callOwnerFragmentTag : String?,
    readSharePreffernceMap: Map<String, String>,
    isLongPress: Boolean
) {
    if(isLongPress) {
        val execIntent = Intent(activity, activity::class.java)
        execIntent.setAction(Intent.ACTION_MAIN)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.finish()
        activity.startActivity(execIntent)
        return
    }
    ExecOkForEdit.execOkForEdit(
        activity,
        callOwnerFragmentTag,
        readSharePreffernceMap,
    )
}

private fun cancelHandler(
    activity: MainActivity,
    enableCmdEdit: Boolean
){
    if(enableCmdEdit) {
        ExecCancel.execCancel(
            activity,
        )
        return
    }
    activity.supportFragmentManager.popBackStackImmediate()
}