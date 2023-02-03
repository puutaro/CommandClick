package com.puutaro.commandclick.util


class ComleteQuote {
    companion object {
        fun comp(
            currentVriableValue: String
        ): String {
            if(
                currentVriableValue.indexOf('\'') == -1
                && currentVriableValue.indexOf('"') == -1
                && currentVriableValue.indexOf(' ') == -1
                && currentVriableValue.indexOf('　') == -1
            ) return currentVriableValue
            val currentVriableValueLength = currentVriableValue.length - 1

            if(
                currentVriableValue.indexOf('"') == 0
                && currentVriableValue.lastIndexOf('"') == currentVriableValueLength
                && currentVriableValue.filter { it == '"' }.length == 2
            ) return currentVriableValue
            if(
                currentVriableValue.indexOf('"') == 0
                && currentVriableValue.lastIndexOf('"') != currentVriableValueLength
                && currentVriableValue.filter { it == '"' }.length == 1
            ) return currentVriableValue + '"'
            if(
                currentVriableValue.lastIndexOf('"') == currentVriableValueLength
                && currentVriableValue.indexOf('"') != 0
                && currentVriableValue.filter { it == '"' }.length == 1
            ) return '"' + currentVriableValue
            val middleCurrentVriableValue = currentVriableValue.substring(1, currentVriableValueLength)
            if(
                middleCurrentVriableValue.indexOf('\'') != -1
            ) return '"' + currentVriableValue + '"'

            if(
                currentVriableValue.indexOf('\'') == 0
                && currentVriableValue.lastIndexOf('\'') == currentVriableValueLength
                && currentVriableValue.filter { it == '\'' }.length == 2
            ) return currentVriableValue
            if(
                currentVriableValue.indexOf('\'') == 0
                && currentVriableValue.lastIndexOf('\'') != currentVriableValueLength
                && currentVriableValue.filter { it == '\'' }.length == 1
            ) return currentVriableValue + '\''
            if(
                currentVriableValue.lastIndexOf('\'') == currentVriableValueLength
                && currentVriableValue.indexOf('\'') != 0
                && currentVriableValue.filter { it == '\'' }.length == 1
            ) return '\'' + currentVriableValue
            if(
                middleCurrentVriableValue.indexOf('\"') != -1
            ) return '\'' + currentVriableValue + '\''
            if(currentVriableValue.indexOf(' ') == -1) return currentVriableValue

            return '"' + currentVriableValue + '"'
        }

    }
}