package com.puutaro.commandclick.proccess.import

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.CommandClickVariables
import java.io.File

object CmdVariableReplacer {

    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String
    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String


    fun replace(
        context: Context?,
        scriptPath: String,
        jsList: List<String>,
        setReplaceVariableCompleteMap: Map<String, String>? = null
    ): Map<String, String>? {
        val jsFileObj = File(scriptPath)
        if(
            !jsFileObj.isFile
        ) return setReplaceVariableCompleteMap
        val scriptFileName = jsFileObj.name
        val mainFannelPath = CcPathTool.getMainFannelFilePath(
            scriptFileName
        )
        if(
            mainFannelPath == scriptPath
        ) return setReplaceVariableCompleteMap

       val cmdValMap = CommandClickVariables.substituteVariableListFromHolder(
            jsList,
            settingSectionStart,
            settingSectionEnd
        )?.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }?.toMap()
           ?: return setReplaceVariableCompleteMap
        if (
            setReplaceVariableCompleteMap.isNullOrEmpty()
        ) return cmdValMap
        return setReplaceVariableCompleteMap + cmdValMap
    }
}