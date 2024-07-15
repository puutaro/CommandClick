package com.puutaro.commandclick.common.variable.extra

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles

object UbuntuEnvTsv {

    private val rootfsTarGzName = UbuntuFiles.rootfsTarGzName
    fun makeTsv(): String {
        return listOf(
            "WAIT_QUIZ_TSV_NAME" to UbuntuFiles.waitQuizTsvName,
            "UBUNTU_BACKUP_ROOTFS_PATH" to "${UsePath.cmdclickUbuntuBackupDirPath}/${rootfsTarGzName}",
            "UBUNTU_BACKUP_TEMP_ROOTFS_PATH" to "${UsePath.cmdclickUbuntuBackupTempDirPath}/${rootfsTarGzName}",
            "UBUNTU_EXTRA_STARTUP_SHELLS_PATH" to UbuntuFiles.ubuntuExtraStartupShellsTsvPath,
            "MUST_PROCESS_GREP_CMDS_TXT" to UbuntuFiles.mustProcessGrepCmdsTxt,
        ).map {
            "${it.first}\t${it.second}"
        }.joinToString("\n")
    }
}