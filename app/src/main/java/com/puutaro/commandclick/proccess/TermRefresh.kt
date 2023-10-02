package com.puutaro.commandclick.proccess

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems

class TermRefresh {
    companion object {
        fun refresh(
            currentMonitorFileName: String
        ){
            FileSystems.removeFiles(
                UsePath.cmdclickMonitorDirPath,
                currentMonitorFileName,
            )
            FileSystems.createFiles(
                UsePath.cmdclickMonitorDirPath,
                currentMonitorFileName,
            )
        }
    }
}