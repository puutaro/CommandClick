package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText

class JsScript(
    terminalFragment: TerminalFragment
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
    private val readSharedPreferences = terminalFragment.readSharePreferenceMap
    private val filePrefix = "file://"
    private val setReplaceVariableMap = terminalFragment.setReplaceVariableMap

    @JavascriptInterface
    fun subLabelingVars(
        jsContents: String
    ): String {
        if(
            labelingStartHolder.isNullOrEmpty()
            || labelingEndHolder.isNullOrEmpty()
        ) return String()
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
        return CommandClickVariables.extractValListFromHolder(
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
        return CommandClickVariables.extractValListFromHolder(
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
        currentFannelPath: String,
        setVariableFilePath: String,
    ): String {
        return listOf(
            CommandClickScriptVariable.SET_VARIABLE_TYPE,
            SettingFile.read(
                setVariableFilePath,
                currentFannelPath,
                setReplaceVariableMap,
            )
        ).filter{ it.isNotEmpty() }.joinToString("=")
    }

//    @JavascriptInterface
//    fun convertRepValPathToOneLine(
//        fannelPath: String,
//        setVariableConfigPath: String,
//    ): String {
//        return listOf(
//            CommandClickScriptVariable.SET_REPLACE_VARIABLE,
//            SettingFile.read(
//                setVariableConfigPath,
//                fannelPath,
//                setReplaceVariableMap,
//            )
//        ).filter{ it.isNotEmpty() }.joinToString("=")
//    }

//    @JavascriptInterface
//    fun convertConfigToOneLine(
//        setVariableConfigPath: String,
//    ): String {
//        return SettingFile.read(
//            setVariableConfigPath,
//        )
//    }

    @JavascriptInterface
    fun bothQuoteTrim(
        valString: String
    ): String {
        return QuoteTool.trimBothEdgeQuote(valString)
    }

    @JavascriptInterface
    fun replaceCommandVariable(
        scriptContents: String,
        replaceNewlineSepaCon: String,
    ): String {
        return CommandClickVariables.replaceVariableInHolder(
            scriptContents,
            replaceNewlineSepaCon,
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
        val mainFannelCon = ReadText(
            fannelPath
        ).readText()
        return subCmdVars(
            mainFannelCon
        )
    }

//    @JavascriptInterface
//    fun makeFannelCon(
//        settingValConSrc: String,
//        cmdValConSrc: String,
//    ): String {
//        val languageType = LanguageTypeSelects.JAVA_SCRIPT
//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//                languageType
//            )
//        val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//
//        val settingSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String
//        val cmdSectionStart = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//        ) as String
//
//        val cmdSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//        ) as String
//        val settingConListWithHolder = listOf(
//            settingSectionStart,
//            makeSettingValCon(settingValConSrc),
//            settingSectionEnd
//        )
//        val cmdConListWithHolder = listOf(
//            cmdSectionStart,
//            cmdValConSrc,
//            cmdSectionEnd
//        )
//        return listOf(
//            listOf(String()),
//            settingConListWithHolder,
//            cmdConListWithHolder,
//            listOf(String()),
//        ).flatten().joinToString("\n\n")
//    }

//    private fun makeSettingValCon(
//        settingValConSrc: String,
//    ): String {
//        val valNameRegex = Regex("[a-zA-Z0-9]+=.*")
//        return settingValConSrc.split("\n").filter {
//            val valLineList = it.split("=")
//            it.isNotEmpty()
//                    && it.matches(valNameRegex)
//                    && valLineList.size >= 2
//        }.map {
//            val valLineList = it.split("=")
//            val valName = valLineList.firstOrNull()
//                ?: return@map String()
//            val valValueSrc = QuoteTool.trimBothEdgeQuote(
//                valLineList.filterIndexed { index, s -> index > 0 }
//                    .joinToString("\n")
//            )
//            val valValue = when(
//                valValueSrc.startsWith(filePrefix)
//            ){
//                true -> {
//                    val filePath = valValueSrc.removePrefix(filePrefix)
//                    SettingFile.read(
//                        filePath
//                    )
//                }
//                else -> valValueSrc
//            }
//            "${valName}=\"${valValue}\""
//        }.joinToString("\n")
//    }
}