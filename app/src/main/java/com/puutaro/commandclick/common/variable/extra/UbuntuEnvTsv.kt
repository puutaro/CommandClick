package com.puutaro.commandclick.common.variable.extra

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.NetworkTool

object UbuntuEnvTsv {

    fun makeTsv(
        context: Context?,
    ): String {
        return arrayOf(
            "WAIT_QUIZ_TSV_NAME" to UbuntuFiles.waitQuizTsvName,
            "UBUNTU_BACKUP_DIR_PATH" to UsePath.cmdclickUbuntuBackupDirPath,
            "UBUNTU_BACKUP_ROOTFS_DIR_PATH" to UbuntuFiles.getUbuntuBackupRootfsDirPathOnlyWrite(),
            "UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH" to UbuntuFiles.ubuntuBackupTempRootfsDirPath,
            "ROOTFS_TAR_NAME" to UbuntuFiles.rootfsTarName,
            "UBUNTU_EXTRA_STARTUP_SHELLS_PATH" to UbuntuFiles.ubuntuExtraStartupShellsTsvPath,
            "MUST_PROCESS_GREP_CMDS_TXT" to UbuntuFiles.mustProcessGrepCmdsTxt,
            "IP_V4_ADDRESS" to NetworkTool.getIpv4Address(context),
        ).map {
            "${it.first}\t${it.second}"
        }.joinToString("\n")
    }
}