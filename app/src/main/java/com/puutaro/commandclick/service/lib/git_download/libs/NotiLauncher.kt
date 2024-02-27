package com.puutaro.commandclick.service.lib.git_download.libs

import android.R
import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.GitDownloadService

object NotiLauncher {

    fun noExistFileNoti(
        gitDownloadService: GitDownloadService,
        fannelName: String,
    ){
        val parentRelativeDirPath = gitDownloadService.parentRelativeDirPath
        val fannelRelativePath = if(
            parentRelativeDirPath.isNullOrEmpty()
        ) fannelName
        else "$parentRelativeDirPath/$fannelName"
        gitDownloadService.notificationBuilder
            ?.setSmallIcon(R.drawable.stat_sys_download_done)
            ?.setContentTitle(
                GitDownloadStatus.FAILURE_GREP_FILE_LIST.title,
            )
            ?.setContentText(
                GitDownloadStatus.FAILURE_GREP_FILE_LIST.message.format(
                    fannelRelativePath
                ),
            )
        gitDownloadService.notificationBuilder?.clearActions()
        gitDownloadService.notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.icons8_cancel,
            GitDownloadLabels.CLOSE.label,
            gitDownloadService.cancelPendingIntent
        )?.build()?.let {
            gitDownloadService.notificationManager.notify(
                gitDownloadService.chanelId,
                it
            )
        }
    }

    fun compCloseNoti(
        gitDownloadService: GitDownloadService
    ){
        BroadcastSender.normalSend(
            gitDownloadService,
            BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
        )
        BroadcastSender.normalSend(
            gitDownloadService,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )

        val fannelRawName = gitDownloadService.fannelRawName
        gitDownloadService.notificationBuilder
            ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
            ?.setContentTitle(
                GitDownloadStatus.COMP.title
            )
            ?.setContentText(
                GitDownloadStatus.COMP.message.format(
                    fannelRawName
                )
            )
        gitDownloadService.notificationBuilder?.clearActions()
        gitDownloadService.notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.icons8_cancel,
            GitDownloadLabels.CLOSE.label,
            gitDownloadService.cancelPendingIntent
        )?.build()?.let {
            gitDownloadService.notificationManager.notify(
                gitDownloadService.chanelId,
                it
            )
        }
    }
}