package com.puutaro.commandclick.util.str

object SnakeCamelTool {
    fun snakeToCamel(line: String): String {
        val pattern = "_([a-z])".toRegex()
        return line.replace(pattern) { it.groupValues[1].uppercase() }
    }
}