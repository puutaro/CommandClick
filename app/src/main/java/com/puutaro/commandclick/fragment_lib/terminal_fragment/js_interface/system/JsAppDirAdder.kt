package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JsAppDirAdder(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    private val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_state
    )

    @JavascriptInterface
    fun add_S(){
        if(
            context == null
        ) return
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val type = ListIndexEditConfig.getListIndexType(
            editFragment,
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                return
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        val newAppDirSrc = JsDialog(terminalFragment).prompt(
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
            Toast.makeText(
                context,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
            LogSystems.stdErr("$e")
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

        CommandClickScriptVariable.makeAppDirAdminFile(
            UsePath.cmdclickAppDirAdminPath,
            scriptFileName
        )
        val addAppDirNameFilePath = File(
            UsePath.cmdclickAppDirAdminPath,
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