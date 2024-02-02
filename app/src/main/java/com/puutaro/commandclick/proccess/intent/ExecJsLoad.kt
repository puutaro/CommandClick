package com.puutaro.commandclick.proccess.intent

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.ScriptArgs
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import java.io.File


object ExecJsLoad {

    fun execJsLoad(
        currentFragment: Fragment,
        recentAppDirPath: String,
        selectedJsFileName: String,
        jsContentsListSource: List<String>? = null,
        jsArgs: String = String()
    ) {
        if (
            !File(
                recentAppDirPath,
                selectedJsFileName
            ).isFile
        ) return
        val context = currentFragment.context
        when (currentFragment) {
            is CommandIndexFragment -> {
                val listener = currentFragment.context as? CommandIndexFragment.OnKeyboardVisibleListener
                listener?.onKeyBoardVisibleChange(
                    false,
                    true,
                    currentFragment.WebSearchSwitch

                )
            }
            is EditFragment -> {
                val listener = currentFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
                listener?.onKeyBoardVisibleChangeForEditFragment(
                    false,
                    true,
                )
            }
        }
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val jsContents = if (jsContentsListSource.isNullOrEmpty()) {
            ReadText(
                recentAppDirPath,
                selectedJsFileName
            ).readText()
        } else jsContentsListSource.joinToString("\n")
        val jsContentsList =
            ScriptPreWordReplacer.replace(
                jsContents,
                recentAppDirPath,
                selectedJsFileName,
            ).split("\n")
        val substituteSettingVariableList =
            CommandClickVariables.substituteVariableListFromHolder(
                jsContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

//        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
//            currentFragment,
//            substituteSettingVariableList,
//        )


        val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
        ) ?: CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE


        if(
            selectedJsFileName == UsePath.cmdclickStartupJsName
            || selectedJsFileName == UsePath.cmdclickInternetButtonExecJsFileName
            || selectedJsFileName == UsePath.cmdclickButtonExecShellFileName
        ) {
            val onUrlLaunchMacro = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickScriptVariable.ON_URL_LAUNCH_MACRO
            ) ?: CommandClickScriptVariable.ON_URL_LAUNCH_MACRO_DEFAULT_VALUE

            UrlLaunchMacro.launch(
                terminalViewModel,
                recentAppDirPath,
                onUrlLaunchMacro,
            )
            JavascriptExecuter.exec(
                currentFragment,
                terminalViewModel,
                substituteSettingVariableList,
                onUrlLaunchMacro,
            )
            return
        }

        val tempOnDisplayUpdate = terminalViewModel.onDisplayUpdate
        JavascriptExecuter.enableJsLoadInWebView(
            terminalViewModel
        )

        val execJsPath = decideLoadJsPath(
            recentAppDirPath,
            selectedJsFileName,
            jsArgs,
        )
        val loadJsContentsList = ScriptArgs.values().filter {
            it.str == jsArgs
        }.firstOrNull()?.let {
            emptyList()
        } ?: jsContentsList
        val launchUrlString = JavaScriptLoadUrl.make(
            context,
            execJsPath,
            loadJsContentsList
        ).toString()

        terminalViewModel.jsArguments = jsArgs

        jsUrlLaunchHandler(
            currentFragment,
            launchUrlString,
        )

        JavascriptExecuter.cleanUpAfterJsExc(
            terminalViewModel,
            tempOnDisplayUpdate,
        )

        JsFilePathToHistory.insert(
            recentAppDirPath,
            selectedJsFileName,
        )

        val terminalOutputMode = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.TERMINAL_OUTPUT_MODE,
        )?.trim() ?: CommandClickScriptVariable.TERMINAL_OUTPUT_MODE_DEFAULT_VALUE
        terminalViewModel.onBottomScrollbyJs = !(
                terminalOutputMode ==
                        SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
                )

        if (
            onUpdateLastModify
            == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
        ) return
        FileSystems.updateLastModified(
            recentAppDirPath,
            selectedJsFileName
        )
    }

    fun execExternalJs(
        fragment: Fragment,
        currentAppDirPath: String,
        fannelName: String,
        systemExecReplaceTextList: List<String>,
    ){
        val context = fragment.context
            ?: return
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val externalExecJsPath = "${currentAppDirPath}/${fannelDirName}/${UsePath.externalExecJsDirName}/${UsePath.externalJsForExecFannel}"
        val fannelPathObj = File("${currentAppDirPath}/${fannelName}")
        val externalExecJsPathObj = File(externalExecJsPath)
        val isExternalExecJsPath = externalExecJsPathObj.isFile
        val execJsPathObj = when(isExternalExecJsPath){
            true -> externalExecJsPathObj
            else -> fannelPathObj
        }
        val parentDirPath = execJsPathObj.parent
            ?: return
        val execJsName = execJsPathObj.name
        val cmddlickExternalExecReplaceTxt = CommandClickScriptVariable.CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT
        val replaceMarkMap = systemExecReplaceTextList.mapIndexed { index, value ->
            val RepTextSuffix = index + 1
            val repValMark = "${cmddlickExternalExecReplaceTxt}${RepTextSuffix}"
            repValMark to value
        }.toMap()
        val jsContentsListSource = ReadText(
            parentDirPath,
            execJsName,
        ).readText().let {
            srcJsCon ->
            var replacedJsCon = srcJsCon
            replaceMarkMap.forEach {
                val repMark = "\${${it.key}}"
                val repValue = it.value
                replacedJsCon = replacedJsCon.replace(
                    repMark,
                    repValue,
                )
            }
            replacedJsCon
        }.split("\n")
        val externalJsCon = JavaScriptLoadUrl.make(
            context,
            "${parentDirPath}/${execJsName}",
            jsContentsListSource
        ) ?: return
        jsUrlLaunchHandler(
            fragment,
            externalJsCon
        )
    }

    fun jsUrlLaunchHandler(
        currentFragment: Fragment,
        launchUrlString: String,
    ){
        when (currentFragment) {
            is CommandIndexFragment -> {
                currentFragment.jsExecuteJob?.cancel()
                currentFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
                    val onLaunchUrl = EnableTerminalWebView.check(
                        currentFragment,
                        currentFragment.context?.getString(
                            R.string.index_terminal_fragment
                        )
                    )
                    launchUrlByWebView(
                        currentFragment,
                        onLaunchUrl,
                        launchUrlString
                    )
                }
            }
            is EditFragment -> {
                currentFragment.jsExecuteJob?.cancel()
                currentFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
                    val onLaunchUrl = EnableTerminalWebView.check(
                        currentFragment,
                        currentFragment.context?.getString(
                            R.string.edit_terminal_fragment
                        )
                    )
                    launchUrlByWebView(
                        currentFragment,
                        onLaunchUrl,
                        launchUrlString
                    )
                }
            }
            is TerminalFragment -> {
                currentFragment.binding.terminalWebView.loadUrl(launchUrlString)
            }
        }
    }

    private fun decideLoadJsPath(
        scriptDirPath: String,
        scriptName: String,
        jsArgs: String,
    ): String {
        val currentScriptPath = "$scriptDirPath/$scriptName"
        return ScriptArgs.values().filter {
            it.str == jsArgs
        }.firstOrNull()?.let {
            val fannelDirName = CcPathTool.makeFannelDirName(scriptName)
            val exeJsPath = "${scriptDirPath}/$fannelDirName/${it.dirName}/${it.jsName}"
            val urlHistoryClickPathObj = File(exeJsPath)
            if(!urlHistoryClickPathObj.isFile) return currentScriptPath
            exeJsPath
        } ?: currentScriptPath
    }

    private suspend fun launchUrlByWebView(
        currentFragment: Fragment,
        onLaunchUrl: Boolean,
        launchUrlString: String
    ){
        if(!onLaunchUrl) return
        withContext(Dispatchers.Main) {
            when(currentFragment) {
                is CommandIndexFragment -> {
                    val listener =
                        currentFragment.context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
                    listener?.onLaunchUrlByWebView(
                        launchUrlString,
                    )
                }
                is EditFragment -> {
                    val listener = currentFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                    listener?.onLaunchUrlByWebViewForEdit(
                        launchUrlString
                    )
                }
                else -> {}
            }
        }
    }
}


