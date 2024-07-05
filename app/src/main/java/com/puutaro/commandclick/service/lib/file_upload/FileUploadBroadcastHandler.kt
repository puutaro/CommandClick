package com.puutaro.commandclick.service.lib.file_upload

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeFileUpload
import com.puutaro.commandclick.service.FileUploadService
import com.puutaro.commandclick.service.lib.file_upload.libs.FileUploadLabels
import com.puutaro.commandclick.service.lib.file_upload.libs.FileUploadStatus

object FileUploadBroadcastHandler {
    fun handle(
        fileUploadService: FileUploadService,
        intent: Intent,
    ) {
        val action = intent.action
        when (action) {
            BroadCastIntentSchemeFileUpload.STOP_FILE_UPLOAD.action
            -> stop(
                fileUploadService,
                intent,
            )
            BroadCastIntentSchemeFileUpload.STAN_FILE_UPLOAD.action
            -> stanNoti(
                fileUploadService,
                intent,
            )
        }
    }

    private fun stop(
        fileUploadService: FileUploadService,
        intent: Intent,
    ) {
        FileUploadFinisher.exit(fileUploadService)
    }
    
    private fun stanNoti(
        fileUploadService: FileUploadService,
        intent: Intent,
    ){
        fileUploadService.notificationBuilder ?: return
        val cancelPendingIntent = fileUploadService.cancelPendingIntent
        fileUploadService.notificationBuilder?.apply{
            setSmallIcon(android.R.drawable.stat_sys_upload_done)
            setAutoCancel(true)
            setContentTitle(FileUploadStatus.STAN.title)
            setContentText(FileUploadStatus.STAN.message)
            setDeleteIntent(
                cancelPendingIntent
            )
            clearActions()
            addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileUploadLabels.CLOSE.label,
                cancelPendingIntent
            )
        }?.let {
                notificationBuilder ->
            notificationBuilder.build().let {
                fileUploadService.notificationManager.notify(
                    fileUploadService.chanelId,
                    it
                )
            }
            fileUploadService.fileUploadJob?.cancel()
            fileUploadService.monitorAcceptTimeJob?.cancel()
        }
    }
}
