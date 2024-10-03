package com.puutaro.commandclick.activity_lib.event

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCommandEdit
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs


object ExecLongClickMenuItemsforCmdIndex {
    fun execLongClickMenuItemsforCmdIndex(
        activity: MainActivity,
        longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
        editFragmentTag: String,
        editFragmentArgs: EditFragmentArgs,
        terminalFragmentTag: String
    ){
        when(longClickMenuItemsforCmdIndex){
            LongClickMenuItemsforCmdIndex.EDIT -> {
                ExecCommandEdit.execCommandEdit(
                    activity,
                    editFragmentTag,
                    editFragmentArgs,
                    terminalFragmentTag
                )
            }
            LongClickMenuItemsforCmdIndex.EXEC_HISTORY -> {
                val execIntent = Intent(activity, activity::class.java)
                execIntent.setAction(Intent.ACTION_MAIN)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.finish()
                activity.startActivity(execIntent)
            }
        }
    }
}