package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

object LongPressMenuTool {

    fun makeExecJsPath(
        terminalFragment: TerminalFragment,
        currentAppDirPath: String,
        selectedScriptNameOrPath: File,
    ): String {
        val currentFannelName = makeCurrentFannelName(
            terminalFragment,
        )
        val fannelDirName = CcPathTool.makeFannelDirName(currentFannelName)
        val isScriptName = selectedScriptNameOrPath.parent.isNullOrEmpty()
        return when(
            isScriptName
        ) {
            true
            -> "${currentAppDirPath}/${selectedScriptNameOrPath.name}"

            else
            -> ScriptPreWordReplacer.replace(
                selectedScriptNameOrPath.absolutePath,
                currentAppDirPath,
                fannelDirName,
                currentFannelName,
            )
        }
    }


    fun makeJsConSrc(
        execJsPath: String,
    ): String {
        val execJsPathObj = File(execJsPath)
        val parentDirPath = execJsPathObj.parent ?: String()
        val jsName = execJsPathObj.name
        return ReadText(
            parentDirPath,
            jsName,
        ).readText()
    }

    fun makeLongPressScriptList(
        terminalFragment: TerminalFragment,
        srcLongPressListConSrc: String,
    ): List<String> {
        val currentAppDirPath =
            terminalFragment.currentAppDirPath
        val currentFannelName =
            makeCurrentFannelName(
                terminalFragment,
            )
        val fannelDirName =
            CcPathTool.makeFannelDirName(currentFannelName)

        val mainJsList =  ReadText(
            currentAppDirPath,
            currentFannelName,
        ).textToList()
        val repValMap = JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
            mainJsList,
            currentAppDirPath,
            fannelDirName,
            currentFannelName
        )
        val srcLongPressListCon = srcLongPressListConSrc.split("\n").map {
            QuoteTool.trimBothEdgeQuote(it)
        }.joinToString("\n")
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            srcLongPressListCon,
            repValMap,
            currentAppDirPath,
            fannelDirName,
            currentFannelName
        ).split("\n")
    }

    private fun makeCurrentFannelName(
        terminalFragment: TerminalFragment,
    ): String {
        val context = terminalFragment.context
            ?: return String()
        val activity = terminalFragment.activity
            ?: return String()
        val editExecuteFragmentTag = context.getString(R.string.edit_execute_terminal_fragment)
        return when(
            terminalFragment.tag == editExecuteFragmentTag
        ){
            true -> {
                val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
                SharePreffrenceMethod.getStringFromSharePreffrence(
                    sharePref,
                    SharePrefferenceSetting.current_script_file_name
                )
            }
            else -> {
                String()
            }

        }
    }
}