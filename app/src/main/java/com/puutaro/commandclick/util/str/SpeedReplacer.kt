package com.puutaro.commandclick.util.str

object SpeedReplacer {
    fun replace(
        srcCon: String,
        oldNewPair: List<Pair<String, String>>,
    ): String {
        val builder = StringBuilder(
            srcCon
        )
        oldNewPair.forEach {
            val oldString = it.first
            val newString = it.second
            var index = builder.indexOf(oldString)
            while (index != -1) {
                builder.replace(
                    index,
                    index + oldString.length,
                    newString
                )
                index = builder.indexOf(
                    oldString,
                    index + newString.length
                )
            }
        }
        return builder.toString()
    }
}