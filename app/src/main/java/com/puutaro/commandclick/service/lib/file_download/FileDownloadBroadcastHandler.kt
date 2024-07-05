package com.puutaro.commandclick.service.lib.file_download

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeFileDownload
import com.puutaro.commandclick.service.FileDownloadService
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadStatus
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadLabels


object FileDownloadBroadcastHandler {
    fun handle(
        fileDownloadService: FileDownloadService,
        intent: Intent,
    ){
        val action = intent.action
        when(action){
            BroadCastIntentSchemeFileDownload.STOP_FILE_DOWNLOAD.action
            -> stop(
                fileDownloadService,
                intent,
            )
            BroadCastIntentSchemeFileDownload.STAN_FILE_DOWNLOAD.action
            -> stanNoti(
                fileDownloadService,
                intent,
            )
        }
    }

    private fun stop(
        fileDownloadService: FileDownloadService,
        intent: Intent,
    ){
        BroadcastManagerForService.unregisterBroadcastReceiver(
            fileDownloadService,
            fileDownloadService.broadcastReceiverForFileDownlaod,
        )
        fileDownloadService.notificationManager.cancel(
            fileDownloadService.chanelId
        )
        fileDownloadService.fileDownloadJob?.cancel()
        fileDownloadService.getListFileConJob?.cancel()
        fileDownloadService.stopSelf()

    }

    private fun stanNoti(
        fileDownloadService: FileDownloadService,
        intent: Intent,
    ){
        fileDownloadService.notificationBuilder ?: return
        val cancelPendingIntent = fileDownloadService.cancelPendingIntent
        fileDownloadService.notificationBuilder?.apply{
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setAutoCancel(true)
            setContentTitle(FileDownloadStatus.STAN.title)
            setContentText(FileDownloadStatus.STAN.message)
            setDeleteIntent(
                cancelPendingIntent
            )
            clearActions()
            addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileDownloadLabels.CLOSE.label,
                cancelPendingIntent
            )
        }?.let {
            notificationBuilder ->
            notificationBuilder.build().let {
                fileDownloadService.notificationManager.notify(
                    fileDownloadService.chanelId,
                    it
                )
            }
        }

    }

}