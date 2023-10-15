package com.puutaro.commandclick.common.variable.extra

import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles

object UbuntuEnvTsv {
    fun makeTsv(): String {
        return listOf(
            "WAIT_QUIZ_TSV_NAME" to UbuntuFiles.waitQuizTsvName,
        ).map {
            "${it.first}\t${it.second}"
        }.joinToString("\n")
    }
}