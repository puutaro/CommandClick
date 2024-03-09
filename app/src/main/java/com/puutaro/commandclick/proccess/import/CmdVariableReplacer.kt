package com.puutaro.commandclick.proccess.import

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object CmdVariableReplacer {

    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
    private val cmdValSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    private val cmdValSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String


    fun replace(
        scriptPath: String,
        setReplaceVariableCompleteMap: Map<String, String>? = null
    ): Map<String, String>? {
        val mainFannelPath = CcPathTool.getMainFannelFilePath(
            scriptPath
        )
        if(
            mainFannelPath == scriptPath
        ) return setReplaceVariableCompleteMap
        val mainFannelPathObj = File(mainFannelPath)
        if(
            !mainFannelPathObj.isFile
        ) return setReplaceVariableCompleteMap

        val jsList = ReadText(
            mainFannelPath
        ).textToList()
       val cmdValMap = CommandClickVariables.extractValListFromHolder(
            jsList,
            cmdValSectionStart,
            cmdValSectionEnd
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