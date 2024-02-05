package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.file.FileSystems
import java.io.BufferedReader
import java.io.File

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
                    File(
                        monitorDirPath,
                        monitorFileName,
                    ).absolutePath,
                    line
                )
            }
        } catch (e: Exception) {
            FileSystems.updateFile(
                File(
                    monitorDirPath,
                    monitorFileName
                ).absolutePath,
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