package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexDuplicate
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class JsAppDirAdder(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun add_S(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val type = ListIndexEditConfig.getListIndexType(
            editFragment,
        )
        when(type){
//            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                return
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        val newAppDirSrc = JsDialog(terminalFragmentRef).prompt(
            "Input new app dir name",
            String(),
            String(),
        )
        if(
            newAppDirSrc.isEmpty()
        ) return
        try {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execAddAppDir(
                        editFragment,
                        newAppDirSrc,
                    )
                }
            }
        } catch (e: Exception){
            ToastUtils.showShort(e.toString())
            LogSystems.stdErr(
                context,
                "$e"
            )
        }
    }

    private fun execAddAppDir(
        editFragment: EditFragment,
        newAppDirSrc: String,
    ){
        val jsFileSuffix = UsePath.JS_FILE_SUFFIX
        val isJsSuffix = newAppDirSrc.endsWith(jsFileSuffix)
        val scriptFileName = if (
            isJsSuffix
        ) newAppDirSrc
        else newAppDirSrc + jsFileSuffix
        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
        ListIndexDuplicate.isFileDetect(
//            cmdclickAppDirAdminPath,
            scriptFileName
        ).let {
            isDetect ->
            if(
                isDetect
            ) return
        }
        CommandClickScriptVariable.makeAppDirAdminFile(
            cmdclickAppDirAdminPath,
            scriptFileName
        )
        val addAppDirNameFilePath = File(
            cmdclickAppDirAdminPath,
            scriptFileName
        ).absolutePath
        ExecAddForListIndexAdapter.sortInAddFile(
            editFragment,
            addAppDirNameFilePath
        )
        val createAppDirName = when (isJsSuffix) {
            true -> newAppDirSrc.removeSuffix(jsFileSuffix)
            else -> newAppDirSrc
        }
        val createAppDirPath = "${UsePath.cmdclickAppDirPath}/${createAppDirName}"
        FileSystems.createDirs(
            createAppDirPath
        )
        FileSystems.createDirs(
            "${createAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        )
    }
}