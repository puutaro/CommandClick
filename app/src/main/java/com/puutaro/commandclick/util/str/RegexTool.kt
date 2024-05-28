package com.puutaro.commandclick.util.str

object RegexTool {

    fun convert(
        con: String
    ): Regex {
        return try {
            Regex(con)
        } catch (e: Exception){
            val removeMarkCon = con.replace(
                Regex(
                    "[^a-zA-Z0-9_/-]",

                ),
                String()
            )
            Regex(removeMarkCon)
        }
    }
}