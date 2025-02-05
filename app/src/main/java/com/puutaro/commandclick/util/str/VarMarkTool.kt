package com.puutaro.commandclick.util.str

object VarMarkTool {

    fun replaceByValue(
        con :String,
        varName: String,
        varValue: String,
    ): String {
        return try {
            con.replace(
                "(?<!\\\\)[$][{]$varName[}]]".toRegex(),
                varValue,
            )
//            con.replace(
//                Regex("""([^\\])[$][{]${varName}[}]"""),
//                "$1${varValue}"
//            ).replace(
//                Regex("""^[$][{]${varName}[}]"""),
//                varValue
//            )
        } catch (e: Exception){
            con
        }
    }
}