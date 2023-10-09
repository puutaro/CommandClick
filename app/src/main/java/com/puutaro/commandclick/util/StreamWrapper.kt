package com.puutaro.commandclick.util

import java.io.BufferedReader

object StreamWrapper {
    fun writeByReader(
        reader: BufferedReader,
        monitorDirPath: String,
        monitorFileName: String,
    ){
        try {
            reader.forEachLine { line ->
                if (
                    line.trim().isEmpty()
                ) return@forEachLine
                FileSystems.updateFile(
                    monitorDirPath,
                    monitorFileName,
                    line
                )
            }
        } catch (e: Exception) {
            FileSystems.updateFile(
                monitorDirPath,
                monitorFileName,
                "## ${this::javaClass.name} ${e.toString()}"
            )
        }
    }

    fun extractByReader(
        reader: BufferedReader
    ): String {

        var output = String()
        reader.forEachLine { line ->
            if (
                line.trim().isEmpty()
            ) return@forEachLine
            output += "\n${line}"
        }
        return output
    }
}