package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
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
    private var cmdVariableContents = String()

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
        return replaceVariableInHolder(
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
        return replaceVariableInHolder(
            scriptContents,
            replaceTabList,
            settingStartHolder,
            settingEndHolder,
        )
    }

    @JavascriptInterface
    fun readCmdValsCon(
        subFannelOrFannelPath: String,
    ) {
        val fannelPath = CcPathTool.getMainFannelFilePath(
            subFannelOrFannelPath
        )
        val fannelPathObj = File(fannelPath)
        val parentDirPath = fannelPathObj.parent
            ?: return
        val fannelName = fannelPathObj.name
        val mainFannelCon = ReadText(
            parentDirPath,
            fannelName
        ).readText()
        cmdVariableContents = subCmdVars(
            mainFannelCon
        )
    }

    @JavascriptInterface
    fun getCmdVal(
        targetValName: String,
    ): String {
        return subValOnlyValue(
            targetValName,
            cmdVariableContents,
        );
    };

    private fun replaceVariableInHolder(
        scriptContents: String,
        replaceTabList: String,
        startHolder: String?,
        endHolder: String?,
    ): String {
        var countStartHolder = 0
        var countEndHolder = 0
        if(
            startHolder.isNullOrEmpty()
        ) return scriptContents
        if(
            endHolder.isNullOrEmpty()
        ) return scriptContents
        val replaceMap = replaceTabList.split("\t").map {
            val keyValueList = it.split("=")
            val keyValueListSize = keyValueList.size
            if(keyValueList.size < 2) return it
            val key = keyValueList.first()
            val value = keyValueList
                .takeLast(keyValueListSize - 1)
                .joinToString("=")
            key to value
        }.toMap()
        return scriptContents.split('\n').map {
            if(
                it.startsWith(startHolder)
                && it.endsWith(startHolder)
            ) countStartHolder++
            if(
                it.startsWith(endHolder)
                && it.endsWith(endHolder)
            ) countEndHolder++
            if(
                countStartHolder == 0
                || countEndHolder > 0
            ) return@map it
            val keyValueList = it.split("=")
            val keyValueListSize = keyValueList.size
            val key = keyValueList.first()
            val replaceValue = replaceMap.get(key)?.let{
                QuoteTool.trimBothEdgeQuote(it)
            } ?: return@map it
            if(
                keyValueListSize < 2
            ) return@map it
            "${key}=\"${replaceValue}\""
        }.joinToString("\n")
    }
}