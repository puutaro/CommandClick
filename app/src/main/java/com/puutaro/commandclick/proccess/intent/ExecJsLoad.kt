package com.puutaro.commandclick.proccess.intent

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import java.io.File


object ExecJsLoad {
    fun execJsLoad(
        currentFragment: Fragment,
        recentAppDirPath: String,
        selectedJsFileName: String,
        jsContentsListSource: List<String>? = null,
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

        val fannelDirName = selectedJsFileName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
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
                fannelDirName,
                selectedJsFileName,
            ).split("\n")
        val substituteSettingVariableList =
            CommandClickVariables.substituteVariableListFromHolder(
                jsContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
            currentFragment,
            substituteSettingVariableList,
        )


        val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
        ) ?: CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE


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
            context,
            terminalViewModel,
            substituteSettingVariableList,
            onUrlLaunchMacro,
        )


        terminalViewModel.onDisplayUpdate = true
        val launchUrlString = JavaScriptLoadUrl.make(
            context,
            "${recentAppDirPath}/${selectedJsFileName}",
            jsContentsList
        ).toString()

        terminalViewModel.jsArguments = String()

        jsUrlLaunchHandler(
            currentFragment,
            launchUrlString,
        )

        JsFilePathToHistory.insert(
            recentAppDirPath,
            selectedJsFileName,
        )

        val terminalOutputMode = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.TERMINAL_OUTPUT_MODE,
        )?.trim(' ') ?: CommandClickScriptVariable.TERMINAL_OUTPUT_MODE_DEFAULT_VALUE
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

    private fun jsUrlLaunchHandler(
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
                            R.string.edit_execute_terminal_fragment
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


