package com.puutaro.commandclick.util

object RecordNumToMapNameValueInHolder {

    fun parse(
        scriptContentsList: List<String>?,
        startHolderName: String,
        endHolderName: String,
    ): Map<String, String>? {
        if(
            scriptContentsList.isNullOrEmpty()
        ) return null
        val commandPromptStartNum = scriptContentsList.indexOf(
            startHolderName
        )
        val commandPromptEndNum = scriptContentsList.indexOf(
            endHolderName
        )
        val substituteCmdStartEndContentList = if(
            commandPromptStartNum > 0
            && commandPromptEndNum > 0
            && commandPromptStartNum < commandPromptEndNum
        ) {
            scriptContentsList.slice(
                commandPromptStartNum..commandPromptEndNum
            )
        } else {
            return null
        }
        val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
        return substituteCmdStartEndContentList.asSequence().map {
                substituteCmdStartEndContentStr ->
            val result = cmdclickVariableRegex.containsMatchIn(
                substituteCmdStartEndContentStr
            )
            if(
                !result
            ) return@map String() to String()
            makeResultEntryMap(
                substituteCmdStartEndContentStr,
            )
        }.filter { it.first.isNotEmpty() }.toMap().toMutableMap()
    }

    private fun makeResultEntryMap(
        substituteCmdStartEndContentStr: String,
    ): Pair<String, String> {

        val substituteCmdStartEndContentList = substituteCmdStartEndContentStr.split("=")
        if(
            substituteCmdStartEndContentList.size < 2
        ) return String() to String()
        val variableName =
            substituteCmdStartEndContentList.firstOrNull()
                ?: return String() to String()
        val variableValue = substituteCmdStartEndContentList.filterIndexed { index, s ->
            index > 0
        }.joinToString("=")
        return variableName to variableValue
    }
}
