package com.puutaro.commandclick.service.lib.git_download

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeGitDownload
import com.puutaro.commandclick.service.GitDownloadService
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadLabels
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadStatus

object GitDownloadBroadcastHandler {
    fun handle(
        gitDownloadService: GitDownloadService,
        intent: Intent,
    ){
        val action = intent.action
        when(action){
            BroadCastIntentSchemeGitDownload.STOP_GIT_DOWNLOAD.action
            -> stop(
                gitDownloadService,
                intent,
            )
            BroadCastIntentSchemeGitDownload.STAN_GIT_DOWNLOAD.action
            -> stanNoti(
                gitDownloadService,
                intent,
            )
        }
    }

    private fun stop(
        gitDownloadService: GitDownloadService,
        intent: Intent,
    ){
        GitdownLoadServiceFinisher.finish(gitDownloadService)

    }

    private fun stanNoti(
        gitDownloadService: GitDownloadService,
        intent: Intent,
    ) {
        gitDownloadService.notificationBuilder ?: return
        val extraMessage = intent.getStringExtra(
            BroadCastIntentSchemeGitDownload.STAN_GIT_DOWNLOAD.scheme,
        ) ?: GitDownloadStatus.STAN.message
        val cancelPendingIntent = gitDownloadService.cancelPendingIntent
        gitDownloadService.notificationBuilder?.apply {
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setAutoCancel(true)
            setContentTitle(GitDownloadStatus.STAN.title)
            setContentText(extraMessage)
            setDeleteIntent(
                cancelPendingIntent
            )
            clearActions()
            addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                GitDownloadLabels.CLOSE.label,
                cancelPendingIntent
            )
        }?.let { notificationBuilder ->
            notificationBuilder.build().let {
                gitDownloadService.notificationManager.notify(
                    gitDownloadService.chanelId,
                    it
                )
            }
        }
    }
}