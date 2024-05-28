package com.puutaro.commandclick.util.str

object StringLength {
    fun count(str: String): Int {
        return str.toCharArray().joinToString("") {
            if (
                it.toString().toByteArray().size <= 1
            ) "a"
            else "aa"
        }.count()
    }

    fun maxCountFromList(
        strList: List<String>?
    ): Int {
        return strList?.map {
            count(it)
        }?.maxOrNull() ?: 0
    }
}