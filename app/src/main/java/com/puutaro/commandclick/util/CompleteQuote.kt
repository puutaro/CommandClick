package com.puutaro.commandclick.util


class CompleteQuote {
    companion object {
        fun comp(
            currentVariableValue: String
        ): String {
            if(
                currentVariableValue.indexOf('\'') == -1
                && currentVariableValue.indexOf('"') == -1
            ) return "\"${currentVariableValue}\""
            val currentVariableValueLength = currentVariableValue.length - 1

            if(
                currentVariableValue.indexOf('"') == 0
                && currentVariableValue.lastIndexOf('"') == currentVariableValueLength
                && currentVariableValue.filter { it == '"' }.length == 2
            ) return currentVariableValue
            if(
                currentVariableValue.indexOf('"') == 0
                && currentVariableValue.lastIndexOf('"') != currentVariableValueLength
                && currentVariableValue.filter { it == '"' }.length == 1
            ) return currentVariableValue + '"'
            if(
                currentVariableValue.lastIndexOf('"') == currentVariableValueLength
                && currentVariableValue.indexOf('"') != 0
                && currentVariableValue.filter { it == '"' }.length == 1
            ) return '"' + currentVariableValue
            val middleCurrentVariableValue = currentVariableValue.substring(1, currentVariableValueLength)
            if(
                middleCurrentVariableValue.indexOf('\'') != -1
            ) return '"' + currentVariableValue + '"'

            if(
                currentVariableValue.indexOf('\'') == 0
                && currentVariableValue.lastIndexOf('\'') == currentVariableValueLength
                && currentVariableValue.filter { it == '\'' }.length == 2
            ) return currentVariableValue
            if(
                currentVariableValue.indexOf('\'') == 0
                && currentVariableValue.lastIndexOf('\'') != currentVariableValueLength
                && currentVariableValue.filter { it == '\'' }.length == 1
            ) return currentVariableValue + '\''
            if(
                currentVariableValue.lastIndexOf('\'') == currentVariableValueLength
                && currentVariableValue.indexOf('\'') != 0
                && currentVariableValue.filter { it == '\'' }.length == 1
            ) return '\'' + currentVariableValue
            if(
                middleCurrentVariableValue.indexOf('\"') != -1
            ) return '\'' + currentVariableValue + '\''
            if(currentVariableValue.indexOf(' ') == -1) return currentVariableValue

            return '"' + currentVariableValue + '"'
        }

    }
}