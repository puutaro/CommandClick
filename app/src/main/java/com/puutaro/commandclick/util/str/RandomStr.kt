package com.puutaro.commandclick.util.str

object RandomStr {
    fun make(
        lengthSrc: Int
    ): String {
        val length = when(lengthSrc < 1){
            true -> 1
            else -> lengthSrc
        }
        return ('A'..'z').map { it }
            .shuffled()
            .subList(1, length)
            .joinToString("")
    }
}