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
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.subMenuAdapter
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object AppProcessManager {

    private var killDialog: Dialog? = null
    private val icons8Wheel = R.drawable.icons8_wheel
    private const val cmdClickMonitorFileName = UsePath.cmdClickMonitorFileName_2

    fun killDialog(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val context = fragment.context
            ?: return
        val ubuntuFiles = UbuntuFiles(context)

        killDialog = Dialog(
            context
        )
        killDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val listDialogTitle = killDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )
        listDialogTitle?.text = "Select kill type"
        val listDialogMessage = killDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = killDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = killDialog?.findViewById<AppCompatImageButton>(
            R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            killDialog?.dismiss()
        }

        setKillTypeListView(
            fragment,
            createKillTypeList(),
            UbuntuFiles(context),
            currentAppDirPath,
            fannelName,
        )
        killDialog?.setOnCancelListener {
            killDialog?.dismiss()
        }
        killDialog?.window?.setGravity(Gravity.BOTTOM)
        killDialog?.show()
    }

    private fun setKillTypeListView(
        fragment: Fragment,
        killTypeList: List<Pair<String, Int>>,
        ubuntuFiles: UbuntuFiles,
        currentAppDirPath: String,
        fannelName: String,
    ) {
        val context = fragment.context
            ?: return
        val killTypeListView =
            killDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val killTypeListAdapter = subMenuAdapter(
            context,
            killTypeList.toMutableList()
        )
        killTypeListView.adapter = killTypeListAdapter
        invokeKillTypeListItemSetClickListener(
            fragment,
            killTypeListView,
            ubuntuFiles,
            currentAppDirPath,
            fannelName,
        )
    }

    private fun setProcessListView(
        fragment: Fragment,
        processList: List<Pair<String, Int>>,
    ) {
        val context = fragment.context
            ?: return
        val processListView =
            killDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val processListAdapter = subMenuAdapter(
            context,
            processList.toMutableList()
        )
        processListView.adapter = processListAdapter
        invokeProcessListItemSetClickListener(
            fragment,
            processListView,
        )
    }

    private fun createKillTypeList(): List<Pair<String, Int>> {
        return KillType.values().map {
            it.str to icons8Wheel
        }
    }

    private fun invokeKillTypeListItemSetClickListener(
        fragment: Fragment,
        dialogListView: ListView,
        ubuntuFiles: UbuntuFiles,
        currentAppDirPath: String,
        fannelName: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            killDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as subMenuAdapter
            val selectedProcess = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            killTypeHandler(
                fragment,
                selectedProcess,
                ubuntuFiles,
                currentAppDirPath,
                fannelName,
            )
            return@setOnItemClickListener
        }
    }

    private fun killTypeHandler(
        fragment: Fragment,
        selectedProcess: String,
        ubuntuFiles: UbuntuFiles,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val context = fragment.context
            ?: return
        val busyboxExecutor = BusyboxExecutor(
            context,
            ubuntuFiles
        )
        when(selectedProcess){
            KillType.KILL_ALL.str
            -> busyboxExecutor.executeKillApp(cmdClickMonitorFileName)
            KillType.KILL_THIS.str -> execKillThis(
                fragment,
                currentAppDirPath,
                fannelName,
            )
            KillType.SELECT_KILL.str -> selectKillProcessDialog(
                fragment
            )
        }
    }

    private fun invokeProcessListItemSetClickListener(
        fragment: Fragment,
        dialogListView: ListView,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            killDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as subMenuAdapter
            val selectedProcess = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            execKillProcess(
                fragment,
                selectedProcess
            )
            return@setOnItemClickListener
        }
    }

    private fun createProcessList(
    ): List<Pair<String, Int>> {
        val processListSrc = ReadText(
            UsePath.cmdclickTempProcessDirPath,
            UsePath.cmdclickTempProcessesTxt,
        ).textToList()
        return processListSrc.filter {
                it.isNotEmpty()
            }.map {
                it to icons8Wheel
            }
    }

    private fun selectKillProcessDialog(
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
        killDialog = Dialog(
            context
        )
        killDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val killProcessListDialogTitle = killDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )
        killProcessListDialogTitle?.text = "Select Kill process"
        val killProcessListDialogMessage = killDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )
        killProcessListDialogMessage?.isVisible = false
        val killProcessListDialogSearchEditText = killDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )
        killProcessListDialogSearchEditText?.isVisible = false
        val cancelButton = killDialog?.findViewById<AppCompatImageButton>(
            R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            killDialog?.dismiss()
        }

        setProcessListView(
            fragment,
            createProcessList(),
        )
        killDialog?.setOnCancelListener {
            killDialog?.dismiss()
        }
        killDialog?.window?.setGravity(Gravity.BOTTOM)
        killDialog?.show()
    }

    private fun execKillThis(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        if(
            fannelName.endsWith(UsePath.SHELL_FILE_SUFFIX)
        ) {
            killThisTermuxShell(
                fragment,
                currentAppDirPath,
                fannelName
            )
            return
        }
        val fannelDirName = CcPathTool.makeFannelDirName(
            fannelName
        )
        val fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
        val killProcessListTabSepaStr = createProcessList().filter {
            it.first.contains(fannelDirPath)
        }.map { it.first }.joinToString("\t")
        execKillProcess(
            fragment,
            killProcessListTabSepaStr
        )
    }

    private fun execKillProcess(
        fragment: Fragment,
        selectedProcess: String,
    ){
        val context = fragment.context
            ?: return
        if(
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ) {
            Toast.makeText(
                context,
                "Install ubuntu from above notification",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if(
            selectedProcess.isEmpty()
        ) return
        val killProcessIntent = Intent()
        killProcessIntent.action = BroadCastIntentScheme.BACKGROUND_CMD_KILL.action
        killProcessIntent.putExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobTypeList.schema,
            selectedProcess
        )
        fragment.context?.sendBroadcast(killProcessIntent)
        Toast.makeText(
            context,
            "killing..",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun killThisTermuxShell(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String
    ){
        val context = fragment.context
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        val factExecCmd =
            "ps aux | grep \"${fannelName}\" " +
                    " | grep -v grep |  awk '{print \$2}' | xargs -I{} kill {} "
        val outputPath = "${UsePath.cmdclickMonitorDirPath}/${currentMonitorFileName}"
        val execCmd = " touch \"${fannelName}\"; " +
                "echo \"### \$(date \"+%Y/%m/%d-%H:%M:%S\") ${factExecCmd}\" " +
                ">> \"${outputPath}\";" + "${factExecCmd} >> \"${outputPath}\"; "
        Toast.makeText(
            context,
            "killing..",
            Toast.LENGTH_SHORT
        ).show()
        ExecBashScriptIntent.ToTermux(
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE,
            context,
            execCmd
        )
    }
}

private enum class KillType(
    val str: String,
) {
    KILL_ALL("kill app"),
    KILL_THIS("kill this process"),
    SELECT_KILL("select kill")
}