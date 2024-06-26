package com.puutaro.commandclick.proccess.intent

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variant.ScriptArgsMapList
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.history.UrlHistoryRegister
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


object ExecJsLoad {

    private val nameKey = ScriptArgsMapList.ScriptArgsKey.NAME.key
    private val dirNameKey = ScriptArgsMapList.ScriptArgsKey.DIR_NAME.key
    private val jsNameKey = ScriptArgsMapList.ScriptArgsKey.JS_NAME.key


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

        val jsContents = if (
            jsContentsListSource.isNullOrEmpty()
        ) {
            ReadText(
                File(
                    recentAppDirPath,
                    selectedJsFileName
                ).absolutePath
            ).readText()
        } else jsContentsListSource.joinToString("\n")
        val jsContentsList =
            ScriptPreWordReplacer.replace(
                jsContents,
                recentAppDirPath,
                selectedJsFileName,
            ).split("\n")
        val substituteSettingVariableList =
            CommandClickVariables.extractValListFromHolder(
                jsContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
        ) ?: CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE

        val sharePref = currentFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        val currentFannelName = SharePrefTool.getStringFromSharePref(
            sharePref,
            SharePrefferenceSetting.current_fannel_name
        )
        val isCmdIndex = selectedJsFileName == UsePath.cmdclickPreferenceJsName
                && currentFannelName != UsePath.cmdclickPreferenceJsName
        if(
            isCmdIndex
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
        val updateScriptArgsMapList = makeUpdateScriptArgsMapList(
            currentFragment,
            recentAppDirPath,
            selectedJsFileName,
        )
        val execJsPath = decideLoadJsPath(
            recentAppDirPath,
            selectedJsFileName,
            updateScriptArgsMapList,
            jsArgs,
        )
        val loadJsContentsList = updateScriptArgsMapList.firstOrNull {
            it.get(nameKey) == jsArgs
        }?.let {
            emptyList()
        } ?: jsContentsList
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsActin_jsload.txt").absolutePath,
//            listOf(
//                "execJsPath: ${execJsPath}",
//                "jsArgs: ${jsArgs}",
//                "updateScriptArgsMapList: ${updateScriptArgsMapList}",
//                "jsContentsListSource: ${jsContentsListSource}",
//                "jsContentsList: ${jsContentsList}",
//                "loadJsContentsList: ${loadJsContentsList}",
//                "loadJsContentsList: ${loadJsContentsList}",
//            ).joinToString("\n\n")
//        )
        terminalViewModel.jsArguments = jsArgs
        JavascriptExecuter.jsOrActionHandler(
            currentFragment,
            execJsPath,
            loadJsContentsList
        )

        JavascriptExecuter.cleanUpAfterJsExc(
            terminalViewModel,
            tempOnDisplayUpdate,
        )

        UrlHistoryRegister.insertJsPath(
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
            File(
                recentAppDirPath,
                selectedJsFileName
            ).absolutePath
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
        val cmddlickExternalExecReplaceTxt = CommandClickScriptVariable.CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT
        val replaceMarkMap = systemExecReplaceTextList.mapIndexed { index, value ->
            val RepTextSuffix = index + 1
            val repValMark = "${cmddlickExternalExecReplaceTxt}${RepTextSuffix}"
            repValMark to value
        }.toMap()
        val jsContentsListSource = ReadText(
            execJsPathObj.absolutePath
        ).textToList()
        val externalJsCon = JavaScriptLoadUrl.make(
            context,
            execJsPathObj.absolutePath,
            jsContentsListSource,
            extraRepValMap = replaceMarkMap
        ) ?: return
        JavascriptExecuter.jsUrlLaunchHandler(
            fragment,
            externalJsCon
        )
    }

    private fun makeUpdateScriptArgsMapList(
        fragment: Fragment,
        scriptDirPath: String,
        scriptName: String,
    ): List<Map<String, String>> {
        val context = fragment.context
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(scriptName)
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        val readSharePreferenceMap = SharePrefTool.getReadSharePrefMap(fragment, String())
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            File(scriptDirPath, scriptName).absolutePath
        )
        val currentSettingFannelPath = FannelStateRooterManager.getSettingFannelPath(
            readSharePreferenceMap,
            setReplaceVariableMap
        )
        val settingSectionVariableList = FannelStateRooterManager.makeSettingVariableList(
            readSharePreferenceMap,
            setReplaceVariableMap,
            settingSectionStart,
            settingSectionEnd,
            currentSettingFannelPath,
        )
        return ScriptArgsMapList.makeUpdateScriptArgsMapList(
            context,
            scriptDirPath,
            scriptName,
            settingSectionVariableList,
        )
    }

    fun jsConLaunchHandler(
        fragment: Fragment,
        jsConSrc: String,
    ){
        val jsCon = JavaScriptLoadUrl.makeFromContents(
            fragment.context,
            jsConSrc.split("\n")
        ) ?: return
        JavascriptExecuter.jsUrlLaunchHandler(
            fragment,
            jsCon
        )
    }


    private fun decideLoadJsPath(
        scriptDirPath: String,
        scriptName: String,
        updateScriptArgsMapList: List<Map<String, String>>,
        jsArgs: String,
    ): String {
        val currentScriptPath =
            File(scriptDirPath, scriptName).absolutePath
        return updateScriptArgsMapList.firstOrNull {
            it.get(nameKey) == jsArgs
        }?.let {
            val fannelDirName = CcPathTool.makeFannelDirName(scriptName)
            val dirName = it.get(dirNameKey)
            if(dirName.isNullOrEmpty()){
                LogSystems.stdWarn("Blank dirName: ${dirName}")
                return@let currentScriptPath
            }
            val jsName = it.get(jsNameKey)
            if(jsName.isNullOrEmpty()){
                LogSystems.stdWarn("Blank jsName: ${jsName}")
                return@let currentScriptPath
            }
            val exeJsPath = "${scriptDirPath}/$fannelDirName/${dirName}/${jsName}"
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "args_decideLoadJsPath.txt").absolutePath,
//                listOf(
//                    "scriptDirPath: ${scriptDirPath}",
//                    "fannelDirName: ${fannelDirName}",
//                    "dirName: ${dirName}",
//                    "jsName: ${jsName}",
//                    "exeJsPath: ${exeJsPath}"
//                ).joinToString("\n\n\n")
//            )
            val exeJsPathObj = File(exeJsPath)
            if(
                !exeJsPathObj.isFile
            ) return currentScriptPath
            exeJsPath
        } ?: currentScriptPath
    }
}


