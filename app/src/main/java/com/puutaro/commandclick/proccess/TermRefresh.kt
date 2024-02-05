package com.puutaro.commandclick.proccess

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

class TermRefresh {
    companion object {
        fun refresh(
            currentMonitorFileName: String
        ){
            val currentMonitorFilePath = File(
                UsePath.cmdclickMonitorDirPath,
                currentMonitorFileName
            ).absolutePath
            FileSystems.removeFiles(currentMonitorFilePath)
            FileSystems.createFiles(currentMonitorFilePath)
        }
    }
}