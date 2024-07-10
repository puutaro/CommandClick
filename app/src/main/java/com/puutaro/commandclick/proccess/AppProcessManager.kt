package com.puutaro.commandclick.proccess

import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

object AppProcessManager {

    private var killDialog: Dialog? = null
    private val icons8Wheel = R.drawable.icons8_wheel

    fun killDialog(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        execKillDialog(
            fragment,
            currentAppDirPath,
            fannelName,
            createKillTypeList()
        )
    }

    fun killDialogForCmdIndex(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        execKillDialog(
            fragment,
            currentAppDirPath,
            fannelName,
            createKillTypeListForCmdIndex()
        )
    }
    private fun execKillDialog(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
        killTypeList: List<Pair<String, Int>>
    ){
        val context = fragment.context
            ?: return

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
            killDialog = null
        }

        setKillTypeListView(
            fragment,
            killTypeList,
            currentAppDirPath,
            fannelName,
        )
        killDialog?.setOnCancelListener {
            killDialog?.dismiss()
            killDialog = null
        }
        killDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        killDialog?.window?.setGravity(Gravity.BOTTOM)
        killDialog?.show()
    }

    private fun setKillTypeListView(
        fragment: Fragment,
        killTypeList: List<Pair<String, Int>>,
        currentAppDirPath: String,
        fannelName: String,
    ) {
        val context = fragment.context
            ?: return
        val killTypeListView =
            killDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val killTypeListAdapter = SubMenuAdapter(
            context,
            killTypeList.toMutableList()
        )
        killTypeListView.adapter = killTypeListAdapter
        invokeKillTypeListItemSetClickListener(
            fragment,
            killTypeListView,
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
        val processListAdapter = SubMenuAdapter(
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

    private fun createKillTypeListForCmdIndex(): List<Pair<String, Int>> {
        return KillType.values().filter{
            it != KillType.KILL_THIS
        }.map {
            it.str to icons8Wheel
        }
    }

    private fun invokeKillTypeListItemSetClickListener(
        fragment: Fragment,
        dialogListView: ListView,
        currentAppDirPath: String,
        fannelName: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            killDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
            val selectedProcess = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            killTypeHandler(
                fragment,
                selectedProcess,
                currentAppDirPath,
                fannelName,
            )
            return@setOnItemClickListener
        }
    }

    private fun killTypeHandler(
        fragment: Fragment,
        selectedProcess: String,
        currentAppDirPath: String,
        fannelName: String,
    ){
        when(selectedProcess){
            KillType.KILL_ALL.str
            -> LinuxCmd.killAllProcess()
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
            killDialog = null
            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
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
            File(
                UsePath.cmdclickTempProcessDirPath,
                UsePath.cmdclickTempProcessesTxt
            ).absolutePath,
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
            ToastUtils.showShort("Launch ubuntu")
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
            killDialog = null
        }

        setProcessListView(
            fragment,
            createProcessList(),
        )
        killDialog?.setOnCancelListener {
            killDialog?.dismiss()
            killDialog = null
        }
        killDialog?.window?.setGravity(Gravity.BOTTOM)
        killDialog?.show()
    }

    private fun execKillThis(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(
                fannelName
            )
        val settingVariable = CommandClickVariables.returnSettingVariableList(
            ReadText(
                File(
                    currentAppDirPath,
                    fannelName
                ).absolutePath,
            ).textToList(),
            languageType
        )
        val shellExecEnv = CommandClickVariables.substituteCmdClickVariable(
            settingVariable,
            CommandClickScriptVariable.SHELL_EXEC_ENV
        ) ?: CommandClickScriptVariable.SHELL_EXEC_ENV_DEFAULT_VALUE

        if(
            fannelName.endsWith(UsePath.SHELL_FILE_SUFFIX)
            && shellExecEnv == SettingVariableSelects.ShellExecEnvSelects.TERMUX.name
        ) {
            killThisTermuxShell(
                fragment,
                fannelName
            )
            return
        }
        val fannelDirName = CcPathTool.makeFannelDirName(
            fannelName
        )
        val fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
        val killProcessListTabSepaStr = createProcessList().filter {
            val tergetFannelPath = it.first
            val isContainFannelDirPath = tergetFannelPath.contains(fannelDirPath)
            val isContainFannelPath = tergetFannelPath.contains(
                "$currentAppDirPath/$fannelName"
            )
            isContainFannelDirPath ||  isContainFannelPath
        }.map { it.first }.joinToString("\t")
        execKillProcess(
            fragment,
            killProcessListTabSepaStr
        )
    }

    private fun execKillProcess(
        fragment: Fragment,
        selectedProcessTabSepa: String,
    ){
        val context = fragment.context
            ?: return
        if(
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ) {
            ToastUtils.showShort("Launch ubuntu")
            return
        }
        if(
            selectedProcessTabSepa.isEmpty()
        ) return
        ToastUtils.showShort("killing..")
        val killProcessIntent = Intent()
        killProcessIntent.action = BroadCastIntentSchemeUbuntu.CMD_KILL_BY_ADMIN.action
        killProcessIntent.putExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobTypeListForKill.schema,
            selectedProcessTabSepa
        )
        fragment.context?.sendBroadcast(killProcessIntent)
    }

    private fun killThisTermuxShell(
        fragment: Fragment,
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
        ToastUtils.showShort("killing..")
        ExecBashScriptIntent.ToTermux(
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