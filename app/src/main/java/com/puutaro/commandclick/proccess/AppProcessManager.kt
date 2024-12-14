package com.puutaro.commandclick.proccess

import android.content.Intent
import android.webkit.ValueCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ListJsDialogV2Script
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ValidFannelNameGetterForTerm
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

object AppProcessManager {

    private val ringStr = CmdClickIcons.RING.str

    fun killDialog(
        fragment: Fragment,
    ){
        execKillDialog(
            fragment,
            createKillTypeList()
        )
    }

    private fun execKillDialog(
        fragment: Fragment,
        killTypeList: List<String>
    ){
        val terminalFragment = when(fragment){
            is TerminalFragment -> fragment
            else -> TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment.activity,
            )
        } ?: return
        val currentValidFannelName =
            ValidFannelNameGetterForTerm.get(
                terminalFragment
            )
        val selectLongPressJs = ListJsDialogV2Script.make(
            currentValidFannelName,
            "Select kill type",
            killTypeList,
            saveTag = null,
        )
        terminalFragment.binding.terminalWebView.evaluateJavascript(
            selectLongPressJs,
            ValueCallback<String> { selectedKillTypeSrc ->
                val selectedProcess = QuoteTool.trimBothEdgeQuote(
                    selectedKillTypeSrc
                )
                if(
                    selectedProcess.isEmpty()
                ) return@ValueCallback
                killTypeHandler(
                    fragment,
                    selectedProcess,
                    currentValidFannelName,
                )
            })

//
//        killDialog = Dialog(
//            context
//        )
//        killDialog?.setContentView(
//            R.layout.list_dialog_layout
//        )
//        val listDialogTitle = killDialog?.findViewById<AppCompatTextView>(
//            R.id.list_dialog_title
//        )
//        listDialogTitle?.text = "Select kill type"
//        val listDialogMessage = killDialog?.findViewById<AppCompatTextView>(
//            R.id.list_dialog_message
//        )
//        listDialogMessage?.isVisible = false
//        val listDialogSearchEditText = killDialog?.findViewById<AppCompatEditText>(
//            R.id.list_dialog_search_edit_text
//        )
//        listDialogSearchEditText?.isVisible = false
//        val cancelButton = killDialog?.findViewById<AppCompatImageButton>(
//            R.id.list_dialog_cancel
//        )
//        cancelButton?.setOnClickListener {
//            killDialog?.dismiss()
//            killDialog = null
//        }
//
//        setKillTypeListView(
//            fragment,
//            killTypeList,
////            currentAppDirPath,
//            fannelName,
//        )
//        killDialog?.setOnCancelListener {
//            killDialog?.dismiss()
//            killDialog = null
//        }
//        killDialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        killDialog?.window?.setGravity(Gravity.BOTTOM)
//        killDialog?.show()
    }

//    private fun setKillTypeListView(
//        fragment: Fragment,
//        killTypeList: List<Pair<String, Int>>,
////        currentAppDirPath: String,
//        fannelName: String,
//    ) {
//        val context = fragment.context
//            ?: return
//        val killTypeListView =
//            killDialog?.findViewById<ListView>(
//                R.id.list_dialog_list_view
//            ) ?: return
//        val killTypeListAdapter = SubMenuAdapter(
//            context,
//            killTypeList.toMutableList()
//        )
//        killTypeListView.adapter = killTypeListAdapter
//        invokeKillTypeListItemSetClickListener(
//            fragment,
//            killTypeListView,
////            currentAppDirPath,
//            fannelName,
//        )
//    }

//    private fun setProcessListView(
//        fragment: Fragment,
//        processList: List<Pair<String, Int>>,
//    ) {
//        val context = fragment.context
//            ?: return
//        val processListView =
//            killDialog?.findViewById<ListView>(
//                R.id.list_dialog_list_view
//            ) ?: return
//        val processListAdapter = SubMenuAdapter(
//            context,
//            processList.toMutableList()
//        )
//        processListView.adapter = processListAdapter
//        invokeProcessListItemSetClickListener(
//            fragment,
//            processListView,
//        )
//    }

//    private fun createKillTypeList(): List<Pair<String, String>> {
//        return KillType.values().map {
//            it.str to ringStr
//        }
//    }

//    private fun createKillTypeListForCmdIndex(): List<Pair<String, Int>> {
//        return KillType.values().filter{
//            it != KillType.KILL_THIS
//        }.map {
//            it.str to icons8Wheel
//        }
//    }

    private fun createKillTypeList(): List<String> {
        return KillType.entries.map {
            "${it.str}\t${ringStr}"
        }
    }

//    private fun invokeKillTypeListItemSetClickListener(
//        fragment: Fragment,
//        dialogListView: ListView,
////        currentAppDirPath: String,
//        fannelName: String,
//    ) {
//
//        dialogListView.setOnItemClickListener {
//                parent, View, pos, id
//            ->
//            killDialog?.dismiss()
//            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
//            val selectedProcess = menuListAdapter.getItem(pos)
//                ?: return@setOnItemClickListener
//            killTypeHandler(
//                fragment,
//                selectedProcess,
////                currentAppDirPath,
//                fannelName,
//            )
//            return@setOnItemClickListener
//        }
//    }

    private fun killTypeHandler(
        fragment: Fragment,
        selectedKillType: String,
//        currentAppDirPath: String,
        fannelName: String,
    ){
        when(selectedKillType){
            KillType.KILL_ALL.str
            -> LinuxCmd.killAllProcess()
            KillType.KILL_THIS.str -> execKillThis(
                fragment,
                fannelName,
            )
            KillType.SELECT_KILL.str -> selectKillProcessDialog(
                fragment
            )
        }
    }

//    private fun invokeProcessListItemSetClickListener(
//        fragment: Fragment,
//        dialogListView: ListView,
//    ) {
//
//        dialogListView.setOnItemClickListener {
//                parent, View, pos, id
//            ->
//            killDialog?.dismiss()
//            killDialog = null
//            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
//            val selectedProcess = menuListAdapter.getItem(pos)
//                ?: return@setOnItemClickListener
//            execKillProcess(
//                fragment,
//                selectedProcess
//            )
//            return@setOnItemClickListener
//        }
//    }

    private fun createProcessList(
    ): List<String> {
        val processListSrc = ReadText(
            File(
                UsePath.cmdclickTempProcessDirPath,
                UsePath.cmdclickTempProcessesTxt
            ).absolutePath,
        ).textToList()
        return processListSrc.filter {
                it.isNotEmpty()
            }.map {
                "${it}\t${ringStr}"
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
        val terminalFragment = when(fragment){
            is TerminalFragment -> fragment
            else -> TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment.activity,
            )
        } ?: return
        val currentValidFannelName =
            ValidFannelNameGetterForTerm.get(
                terminalFragment
            )
        val selectLongPressJs = ListJsDialogV2Script.make(
            currentValidFannelName,
            "Select Kill process",
            createProcessList(),
            saveTag = null,
        )
        terminalFragment.binding.terminalWebView.evaluateJavascript(
            selectLongPressJs,
            ValueCallback<String> { selectedProcessSrc ->
                val selectedProcess = QuoteTool.trimBothEdgeQuote(
                    selectedProcessSrc
                )
                if(
                    selectedProcess.isEmpty()
                ) return@ValueCallback
                execKillProcess(
                    fragment,
                    selectedProcess
                )
            })



//        killDialog = Dialog(
//            context
//        )
//        killDialog?.setContentView(
//            R.layout.list_dialog_layout
//        )
//        val killProcessListDialogTitle = killDialog?.findViewById<AppCompatTextView>(
//            R.id.list_dialog_title
//        )
//        killProcessListDialogTitle?.text = "Select Kill process"
//        val killProcessListDialogMessage = killDialog?.findViewById<AppCompatTextView>(
//            R.id.list_dialog_message
//        )
//        killProcessListDialogMessage?.isVisible = false
//        val killProcessListDialogSearchEditText = killDialog?.findViewById<AppCompatEditText>(
//            R.id.list_dialog_search_edit_text
//        )
//        killProcessListDialogSearchEditText?.isVisible = false
//        val cancelButton = killDialog?.findViewById<AppCompatImageButton>(
//            R.id.list_dialog_cancel
//        )
//        cancelButton?.setOnClickListener {
//            killDialog?.dismiss()
//            killDialog = null
//        }
//
//        setProcessListView(
//            fragment,
//            createProcessList(),
//        )
//        killDialog?.setOnCancelListener {
//            killDialog?.dismiss()
//            killDialog = null
//        }
//        killDialog?.window?.setGravity(Gravity.BOTTOM)
//        killDialog?.show()
    }

    private fun execKillThis(
        fragment: Fragment,
        fannelName: String,
    ){
        val settingVariable = CommandClickVariables.returnSettingVariableList(
            ReadText(
                File(
                    UsePath.cmdclickDefaultAppDirPath,
                    fannelName
                ).absolutePath,
            ).textToList(),
//            languageType
        )
        val fannelDirName = CcPathTool.makeFannelDirName(
            fannelName
        )
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val fannelDirPath = "${cmdclickDefaultAppDirPath}/${fannelDirName}"
        val killProcessListTabSepaStr = createProcessList().filter {
            tergetFannelPathTagIconStr ->
            val tergetFannelPathTagIconStrList = tergetFannelPathTagIconStr.split("\t")
            val tergetFannelPath = tergetFannelPathTagIconStrList.first()
            val isContainFannelDirPath = tergetFannelPath.contains(fannelDirPath)
            val isContainFannelPath = tergetFannelPath.contains(
                "$cmdclickDefaultAppDirPath/$fannelName"
            )
            isContainFannelDirPath || isContainFannelPath
        }.map {
            tergetFannelPathTagIconStr ->
            val tergetFannelPathTagIconStrList = tergetFannelPathTagIconStr.split("\t")
            val tergetFannelPath = tergetFannelPathTagIconStrList.first()
            tergetFannelPath
        }.joinToString("\t")
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
}

private enum class KillType(
    val str: String,
) {
    KILL_ALL("kill app"),
    KILL_THIS("kill this process"),
    SELECT_KILL("select kill")
}