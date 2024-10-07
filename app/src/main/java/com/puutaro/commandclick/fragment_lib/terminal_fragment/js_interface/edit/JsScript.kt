package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import java.lang.ref.WeakReference

class JsScript(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {
//    private val context = terminalFragment.context
//    private val languageTypeHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP[
//                LanguageTypeSelects.JAVA_SCRIPT
//        ]
    private val labelingStartHolder =  CommandClickScriptVariable.LABELING_SEC_START
//        languageTypeHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
//    )
    private val labelingEndHolder =  CommandClickScriptVariable.LABELING_SEC_END
//        languageTypeHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
//    )
    private val settingStartHolder =  CommandClickScriptVariable.SETTING_SEC_START
//        languageTypeHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    )
    private val settingEndHolder = CommandClickScriptVariable.SETTING_SEC_END
//        languageTypeHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    )
    private val commandStartHolder =  CommandClickScriptVariable.CMD_SEC_START
//    languageTypeHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//    )
    private val commandEndHolder =  CommandClickScriptVariable.CMD_SEC_END
//    CommandClickScriptVariable.SETTING_SEC_END,
//    languageTypeHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//    )
//    private val setReplaceVariableMap = terminalFragment.setReplaceVariableMap

    @JavascriptInterface
    fun subLabelingVars(
        jsContents: String
    ): String {
//        if(
//            labelingStartHolder.isNullOrEmpty()
//            || labelingEndHolder.isNullOrEmpty()
//        ) return String()
        return CommandClickVariables.extractValListFromHolder(
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
        val settingVaribleCon = CommandClickVariables.extractValListFromHolder(
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
        return settingVaribleCon
    }

    @JavascriptInterface
    fun subCmdVars(
        jsContents: String
    ): String {
        if(
            commandStartHolder.isNullOrEmpty()
            || commandEndHolder.isNullOrEmpty()
        ) return String()
        val commandVariableCon = CommandClickVariables.extractValListFromHolder(
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
        return commandVariableCon
    }

    @JavascriptInterface
    fun subValOnlyValue(
        targetValName: String,
        settingValsString: String
    ): String {
        val targetValPrefix = "${targetValName}="
        val targetSettingVariableValue = settingValsString.split("\n").filter {
            it.startsWith(targetValPrefix)
        }.map {
            val removedValName = it.removePrefix(targetValPrefix)
            QuoteTool.trimBothEdgeQuote(removedValName)
        }.joinToString("\n")
        return targetSettingVariableValue
    }

    @JavascriptInterface
    fun bothQuoteTrim(
        valString: String
    ): String {
        val conWithBothQuoteTrim = QuoteTool.trimBothEdgeQuote(valString)
        return conWithBothQuoteTrim
    }

    @JavascriptInterface
    fun replaceCommandVariable(
        scriptContents: String,
        replaceNewlineSepaCon: String,
    ): String {
        val replacedCommandVariableCon = CommandClickVariables.replaceVariableInHolder(
            scriptContents.split("\n"),
            replaceNewlineSepaCon,
            commandStartHolder,
            commandEndHolder,
        ).joinToString("\n")
        return replacedCommandVariableCon
    }

    @JavascriptInterface
    fun replaceSettingVariable(
        scriptContents: String,
        replaceNewlineSepaCon: String,
    ): String {
        val replacedSettingVariableCon = CommandClickVariables.replaceVariableInHolder(
            scriptContents.split("\n"),
            replaceNewlineSepaCon,
            settingStartHolder,
            settingEndHolder,
        ).joinToString("\n")
        return replacedSettingVariableCon
    }

    @JavascriptInterface
    fun readCmdValsCon(
        subFannelOrFannelPath: String,
    ): String {
        val fannelPath = CcPathTool.getMainFannelFilePath(
            subFannelOrFannelPath
        )
        val mainFannelCon = ReadText(
            fannelPath
        ).readText()
        val commandVarialbeCon = subCmdVars(
            mainFannelCon
        )
        return commandVarialbeCon
    }

    @JavascriptInterface
    fun extractSettingValName(
        settingValsCon: String
    ): String {
        val settingValsRegex = Regex("^[a-zA-Z0-9]+=")
        val settingVaribleNames = settingValsCon.split("\n").filter {
            val trimLine = it.trim()
            val notCommentOut =
                !trimLine.startsWith("//")
            val isSettingVal =
                settingValsRegex.containsMatchIn(trimLine)
            notCommentOut
                    && isSettingVal
                    && trimLine.isNotEmpty()
        }.map {
            val trimLine = it.trim()
            trimLine.split("=")
                .firstOrNull()
                ?: String()
        }.filter {
            it.isNotEmpty()
        }.joinToString("\n")
        return settingVaribleNames
    }
}