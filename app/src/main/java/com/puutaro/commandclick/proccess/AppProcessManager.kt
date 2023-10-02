package com.puutaro.commandclick.proccess

import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.MenuListAdapter
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.ReadText

object AppProcessManager {

    private var killProcessDialog: Dialog? = null
    private val appKill = "app kill"
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel
    private val cmdClickMonitorFileName = UsePath.cmdClickMonitorFileName_2

    fun killDialog(
        fragment: Fragment
    ){
        val context = fragment.context
            ?: return
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuLaunchCompFile.isFile
        ) {
            Toast.makeText(
                context,
                "Install ubuntu from above notification",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        killProcessDialog = Dialog(
            context
        )
        killProcessDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val listDialogTitle = killProcessDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        listDialogTitle?.text = "Select Kill process"
        val listDialogMessage = killProcessDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = killProcessDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = killProcessDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            killProcessDialog?.dismiss()
        }

        setListView(
            fragment,
            createProcessList(),
            ubuntuFiles
        )
        killProcessDialog?.setOnCancelListener {
            killProcessDialog?.dismiss()
        }
        killProcessDialog?.window?.setGravity(Gravity.BOTTOM)
        killProcessDialog?.show()
    }

    private fun setListView(
        fragment: Fragment,
        processList: List<Pair<String, Int>>,
        ubuntuFiles: UbuntuFiles,
    ) {
        val context = fragment.context
            ?: return
        val subMenuListView =
            killProcessDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = MenuListAdapter(
            context,
            processList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeListItemSetClickListenerForListDialog(
            fragment,
            subMenuListView,
            ubuntuFiles
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        fragment: Fragment,
        dialogListView: ListView,
        ubuntuFiles: UbuntuFiles,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            killProcessDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as MenuListAdapter
            val selectedProcess = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            menuScriptHandler(
                fragment,
                selectedProcess,
                ubuntuFiles,
            )
            return@setOnItemClickListener
        }
    }

    private fun menuScriptHandler(
        fragment: Fragment,
        selectedProcess: String,
        ubuntuFiles: UbuntuFiles,
    ){
        val context = fragment.context
            ?: return
        val busyboxExecutor = BusyboxExecutor(
            context,
            ubuntuFiles
        )
        when(selectedProcess){
            appKill
            -> busyboxExecutor.executeKillApp(cmdClickMonitorFileName)
            else -> execKillProcess(
                fragment,
                selectedProcess,
            )
        }
    }

    private fun execKillProcess(
        fragment: Fragment,
        selectedProcess: String,
    ){
        if(
            selectedProcess.isEmpty()
        ) return
        val context = fragment.context
        val killProcessIntent = Intent()
        killProcessIntent.action = BroadCastIntentScheme.BACKGROUND_CMD_KILL.action
        killProcessIntent.putExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobType.schema,
            selectedProcess
        )
        fragment.context?.sendBroadcast(killProcessIntent)
        Toast.makeText(
            context,
            "killing..",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun createProcessList(
    ): List<Pair<String, Int>> {
        val processListSrc = listOf(appKill) + ReadText(
            UsePath.cmdclickTempProcessDirPath,
            UsePath.cmdclickTempProcessesTxt,
        ).textToList()
        return processListSrc.filter {
                it.isNotEmpty()
            }.map {
                it to icons8Wheel
            }
    }
}