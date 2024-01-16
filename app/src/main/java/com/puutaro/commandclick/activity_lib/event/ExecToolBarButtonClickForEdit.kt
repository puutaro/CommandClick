package com.puutaro.commandclick.activity_lib.event

import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCommandEdit
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecCancel
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOkForEdit
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod


object ExecToolBarButtonClickForEdit {
    fun execToolBarButtonClickForEdit(
        activity: MainActivity,
        callOwnerFragmentTag : String?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        readSharePreferenceMap: Map<String, String>,
        enableCmdEdit: Boolean,
    ){
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        when(toolbarButtonBariantForEdit){
            ToolbarButtonBariantForEdit.OK ->
                ExecOkForEdit.execOkForEdit(
                    activity,
                    callOwnerFragmentTag,
                    readSharePreferenceMap,
                )
            ToolbarButtonBariantForEdit.HISTORY ->
                cancelHandler(
                    activity,
                    enableCmdEdit
                )
            ToolbarButtonBariantForEdit.EDIT -> {
                val onShortcutOff = EditFragmentArgs.Companion.OnShortcutSettingKey.OFF.key
                val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
                    sharePref,
                    SharePrefferenceSetting.current_app_dir
                )
                val currentFannelName = SharePreferenceMethod.getStringFromSharePreference(
                    sharePref,
                    SharePrefferenceSetting.current_fannel_name
                )
                val settingEditFragmentTag = FragmentTagManager.makeSettingValEditTag(
                    currentAppDirPath,
                    currentFannelName,
                )
                val readSharePreferenceMapForNext = EditFragmentArgs.createReadSharePreferenceMap(
                    currentAppDirPath,
                    currentFannelName,
                    onShortcutOff,
                )
                ExecCommandEdit.execCommandEdit(
                    activity,
                    settingEditFragmentTag,
                    EditFragmentArgs(
                        readSharePreferenceMapForNext,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT,
                    ),
                    activity.getString(R.string.edit_terminal_fragment)
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