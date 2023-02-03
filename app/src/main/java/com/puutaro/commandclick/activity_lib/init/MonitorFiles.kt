package com.puutaro.commandclick.activity_lib.init

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.ReadText
import java.io.File

class MonitorFiles {
    companion object {
        fun trim(
            fileName: String
        ){
            val timLineNum = 1000
            val readText = ReadText(
                UsePath.cmdclickMonitorDirPath,
                fileName
            )
            val contentsList = readText.readText()
                .lines()
            if(
                contentsList.count() < timLineNum
            ) return
            val saveContents = contentsList
                .takeLast(timLineNum)
                .joinToString("\n")
            File(
                UsePath.cmdclickMonitorDirPath,
                fileName,
            ).writeText(saveContents)
        }
    }
}