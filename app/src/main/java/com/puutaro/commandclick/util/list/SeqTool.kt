package com.puutaro.commandclick.util.list

object SeqTool {
    fun <T> isNullOrEmpty(seq: Sequence<T>?): Boolean {
        return seq == null
                || !seq.any()
    }
}