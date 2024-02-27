package com.puutaro.commandclick.service.lib.file_upload

import com.puutaro.commandclick.service.FileUploadService
import com.puutaro.commandclick.service.lib.BroadcastManagerForService

object FileUploadFinisher {
    fun exit(
        fileUploadService: FileUploadService
    ){
        BroadcastManagerForService.unregisterBroadcastReceiver(
            fileUploadService,
            fileUploadService.broadcastReceiverForFileUplaod,
        )
        fileUploadService.notificationManager.cancel(
            fileUploadService.chanelId
        )
        fileUploadService.copyFannelSocket?.close()
        fileUploadService.fileUploadJob?.cancel()
        fileUploadService.stopSelf()
    }
}