package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.AltRegexTool
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
                    AltRegexTool.trim(line).isEmpty()
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

        val output = StringBuilder()
        reader.forEachLine { line ->
            if (
                AltRegexTool.trim(line).isEmpty()
            ) return@forEachLine
            output.append("\n${line}")
        }
        return output.toString()
    }
}