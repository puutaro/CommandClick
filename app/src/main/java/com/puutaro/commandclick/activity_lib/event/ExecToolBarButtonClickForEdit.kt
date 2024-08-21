package com.puutaro.commandclick.activity_lib.event


import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCommandEdit
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecCancel
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOkForEdit
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool


object ExecToolBarButtonClickForEdit {
    fun execToolBarButtonClickForEdit(
        activity: MainActivity,
        callOwnerFragmentTag : String?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        fannelInfoMap: Map<String, String>,
        enableCmdEdit: Boolean,
    ){
        when(toolbarButtonBariantForEdit){
            ToolbarButtonBariantForEdit.OK ->
                ExecOkForEdit.execOkForEdit(
                    activity,
                    callOwnerFragmentTag,
                    fannelInfoMap,
                )
            ToolbarButtonBariantForEdit.HISTORY ->
                cancelHandler(
                    activity,
                    enableCmdEdit
                )
            ToolbarButtonBariantForEdit.EDIT -> {
                val onShortcutOff = EditFragmentArgs.Companion.OnShortcutSettingKey.OFF.key
//                val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//                    fannelInfoMap
//                )

                val currentFannelName = FannelInfoTool.getCurrentFannelName(
                    fannelInfoMap
                )

                val settingEditFragmentTag = FragmentTagManager.makeSettingValEditTag(
//                    currentAppDirPath,
                    currentFannelName,
                )
                val fannelInfoMapForNext = EditFragmentArgs.createFannelInfoMap(
//                    currentAppDirPath,
                    currentFannelName,
                    onShortcutOff,
                    FannelInfoSetting.current_fannel_state.defalutStr,
                )
                ExecCommandEdit.execCommandEdit(
                    activity,
                    settingEditFragmentTag,
                    EditFragmentArgs(
                        fannelInfoMapForNext,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT,
                    ),
                    activity.getString(R.string.edit_terminal_fragment)
                )
            }
            ToolbarButtonBariantForEdit.CANCEL -> {
                activity.supportFragmentManager.popBackStackImmediate()
            }
            else -> Toast.makeText(
                activity,
                "now implementing",
                Toast.LENGTH_SHORT
            ).show()

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