package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.CommandClickVariables

class JsScript(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val languageTypeHolderMap =
        CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP[
                LanguageTypeSelects.JAVA_SCRIPT
        ]
    private val labelingStartHolder = languageTypeHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.LABELING_SEC_START
    )
    private val labelingEndHolder = languageTypeHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.LABELING_SEC_END
    )
    private val settingStartHolder = languageTypeHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
    )
    private val settingEndHolder = languageTypeHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
    )
    private val commandStartHolder = languageTypeHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_START
    )
    private val commandEndHolder = languageTypeHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_END
    )

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
            BothEdgeQuote.trim(removedValName)
        }.joinToString("\n")
    }

    @JavascriptInterface
    fun bothQuoteTrim(
        valString: String
    ): String {
        return BothEdgeQuote.trim(valString)
    }
}