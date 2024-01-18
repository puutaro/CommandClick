package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

class JsScript(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val languageTypeHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP[
                LanguageTypeSelects.JAVA_SCRIPT
        ]
    private val labelingStartHolder = languageTypeHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
    )
    private val labelingEndHolder = languageTypeHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
    )
    private val settingStartHolder = languageTypeHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    )
    private val settingEndHolder = languageTypeHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    )
    private val commandStartHolder = languageTypeHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    )
    private val commandEndHolder = languageTypeHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    )
    private val readSharedPreferences = terminalFragment.readSharedPreferences

    @JavascriptInterface
    fun subLabelingVars(
        jsContents: String
    ): String {
        if(
            labelingStartHolder.isNullOrEmpty()
            || labelingEndHolder.isNullOrEmpty()
        ) return String()
        return CommandClickVariables.substituteVariableListFromHolder(
            jsContents.split("\n"),
            labelingStartHolder,
            labelingEndHolder
        )?.filter {
            val onStartHolder = it.startsWith(labelingStartHolder)
                    && it.endsWith(labelingStartHolder)
            val onEndHolder = it.startsWith(labelingEndHolder)
                    && it.endsWith(labelingEndHolder)
            !onStartHolder && !onEndHolder
        }?.joinToString("\n") ?: String()
    }

    @JavascriptInterface
    fun subSettingVars(
        jsContents: String
    ): String {
        if(
            settingStartHolder.isNullOrEmpty()
            || settingEndHolder.isNullOrEmpty()
        ) return String()
        return CommandClickVariables.substituteVariableListFromHolder(
            jsContents.split("\n"),
            settingStartHolder,
            settingEndHolder
        )?.filter {
            val onStartHolder = it.startsWith(settingStartHolder)
                    && it.endsWith(settingStartHolder)
            val onEndHolder = it.startsWith(settingEndHolder)
                    && it.endsWith(settingEndHolder)
            !onStartHolder && !onEndHolder
        }?.joinToString("\n") ?: String()
    }

    @JavascriptInterface
    fun subCmdVars(
        jsContents: String
    ): String {
        if(
            commandStartHolder.isNullOrEmpty()
            || commandEndHolder.isNullOrEmpty()
        ) return String()
        return CommandClickVariables.substituteVariableListFromHolder(
            jsContents.split("\n"),
            commandStartHolder,
            commandEndHolder
        )?.filter {
            val onStartHolder = it.startsWith(commandStartHolder)
                    && it.endsWith(commandStartHolder)
            val onEndHolder = it.startsWith(commandEndHolder)
                    && it.endsWith(commandEndHolder)
            !onStartHolder && !onEndHolder
        }?.joinToString("\n") ?: String()
    }

    @JavascriptInterface
    fun subValOnlyValue(
        targetValName: String,
        valString: String
    ): String {
        val targetValPrefix = "${targetValName}="
        return valString.split("\n").filter {
            it.startsWith(targetValPrefix)
        }.map {
            val removedValName = it.removePrefix(targetValPrefix)
            QuoteTool.trimBothEdgeQuote(removedValName)
        }.joinToString("\n")
    }

    @JavascriptInterface
    fun convertSetValPathToOneLine(
        setVariableFilePath: String,
    ): String {
        val setVariableFilePathObj = File(setVariableFilePath)
        val setVariableTypesConfigDirPath = setVariableFilePathObj.parent
            ?: return String()
        val setVariableTypesConfigName = setVariableFilePathObj.name
        return listOf(
            CommandClickScriptVariable.SET_VARIABLE_TYPE,
            SettingFile.read(
                setVariableTypesConfigDirPath,
                setVariableTypesConfigName
            )
        ).filter{ it.isNotEmpty() }.joinToString("=")
    }

    @JavascriptInterface
    fun convertRepValPathToOneLine(
        setVariableFilePath: String,
    ): String {
        val setVariableFilePathObj = File(setVariableFilePath)
        val setVariableTypesConfigDirPath = setVariableFilePathObj.parent
            ?: return String()
        val setVariableTypesConfigName = setVariableFilePathObj.name
        return listOf(
            CommandClickScriptVariable.SET_REPLACE_VARIABLE,
            SettingFile.read(
                setVariableTypesConfigDirPath,
                setVariableTypesConfigName
            )
        ).filter{ it.isNotEmpty() }.joinToString("=")
    }

    @JavascriptInterface
    fun convertConfigToOneLine(
        setVariableFilePath: String,
    ): String {
        val setVariableFilePathObj = File(setVariableFilePath)
        val setVariableTypesConfigDirPath = setVariableFilePathObj.parent
            ?: return String()
        val setVariableTypesConfigName = setVariableFilePathObj.name
        return SettingFile.read(
            setVariableTypesConfigDirPath,
            setVariableTypesConfigName
        )
    }

    @JavascriptInterface
    fun bothQuoteTrim(
        valString: String
    ): String {
        return QuoteTool.trimBothEdgeQuote(valString)
    }

    @JavascriptInterface
    fun replaceCommandVariable(
        scriptContents: String,
        replaceTabList: String,
    ): String {
        return CommandClickVariables.replaceVariableInHolder(
            scriptContents,
            replaceTabList,
            commandStartHolder,
            commandEndHolder,
        )
    }

    @JavascriptInterface
    fun replaceSettingVariable(
        scriptContents: String,
        replaceTabList: String,
    ): String {
        return CommandClickVariables.replaceVariableInHolder(
            scriptContents,
            replaceTabList,
            settingStartHolder,
            settingEndHolder,
        )
    }

    @JavascriptInterface
    fun readCmdValsCon(
        subFannelOrFannelPath: String,
    ): String {
        val fannelPath = CcPathTool.getMainFannelFilePath(
            subFannelOrFannelPath
        )
        val fannelPathObj = File(fannelPath)
        val parentDirPath = fannelPathObj.parent
            ?: return String()
        val fannelName = fannelPathObj.name
        val mainFannelCon = ReadText(
            parentDirPath,
            fannelName
        ).readText()
        return subCmdVars(
            mainFannelCon
        )
    }

    @JavascriptInterface
    fun makeFannelCon(
        settingValCon: String,
        cmdValCon: String,
    ): String {
        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        val cmdSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
        ) as String

        val cmdSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
        ) as String


        val settingConListWithHolder = listOf(
            settingSectionStart,
            settingValCon,
            settingSectionEnd
        )
        val cmdConListWithHolder = listOf(
            cmdSectionStart,
            cmdValCon,
            cmdSectionEnd
        )
        return listOf(
            listOf(String()),
            settingConListWithHolder,
            cmdConListWithHolder,
            listOf(String()),
        ).flatten().joinToString("\n\n")
    }

}