package com.puutaro.commandclick.activity_lib.event

import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCommandEdit
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecCancel
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOkForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit


class ExecToolBarButtonClickForEdit {
    companion object {
        fun execToolBarButtonClickForEdit(
            activity: MainActivity,
            callOwnerFragmentTag : String?,
            toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
            readSharePreffernceMap: Map<String, String>,
            enableCmdEdit: Boolean,
            isLongPress: Boolean = false
        ){

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
                    ExecCommandEdit.execCommandEdit(
                        activity,
                        activity.getString(R.string.setting_variable_edit_fragment)
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
                    ).show();
                }
            }
        }
    }
}


internal fun okHandler(
    activity: MainActivity,
    callOwnerFragmentTag : String?,
    readSharePreffernceMap: Map<String, String>,
    isLongPress: Boolean
) {
    if(isLongPress) {
        val execIntent = Intent(activity, activity::class.java)
        execIntent.setAction(Intent.ACTION_MAIN)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(execIntent)
        activity.finish()
        return
    }
    ExecOkForEdit.execOkForEdit(
        activity,
        callOwnerFragmentTag,
        readSharePreffernceMap,
    )
}

internal fun cancelHandler(
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