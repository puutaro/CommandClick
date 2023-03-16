package com.puutaro.commandclick.proccess.intent

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
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
            CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
        ) as String

        val jsContentsList = if (jsContentsListSource.isNullOrEmpty()) {
            ReadText(
                recentAppDirPath,
                selectedJsFileName
            ).textToList()
        } else jsContentsListSource
        val substituteSettingVariableList =
            CommandClickVariables.substituteVariableListFromHolder(
                jsContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
            currentFragment,
            recentAppDirPath,
            substituteSettingVariableList,
        )


        val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickShellScript.ON_UPDATE_LAST_MODIFY
        ) ?: CommandClickShellScript.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE


        val onUrlLaunchMacro = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickShellScript.ON_URL_LAUNCH_MACRO
        ) ?: CommandClickShellScript.ON_URL_LAUNCH_MACRO_DEFAULT_VALUE

        UrlLaunchMacro.launch(
            terminalViewModel,
            recentAppDirPath,
            onUrlLaunchMacro,
        )

        JavascriptExecuter.exec(
            terminalViewModel,
            substituteSettingVariableList,
            onUrlLaunchMacro,
        )


        terminalViewModel.onDisplayUpdate = true
        val launchUrlString = JavaScriptLoadUrl.make(
            "${recentAppDirPath}/${selectedJsFileName}",
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
            CommandClickShellScript.TERMINAL_OUTPUT_MODE,
        )?.trim(' ') ?: CommandClickShellScript.TERMINAL_OUTPUT_MODE_DEFAULT_VALUE
        terminalViewModel.onBottomScrollbyJs = !(
                terminalOutputMode ==
                        SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
                )

        if (
            onUpdateLastModify
            == SettingVariableSelects.Companion.OnUpdateLastModifySelects.OFF.name
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


