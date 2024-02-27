package com.puutaro.commandclick.service.lib.git_download

import com.puutaro.commandclick.service.GitDownloadService
import com.puutaro.commandclick.service.lib.BroadcastManagerForService

object GitdownLoadServiceFinisher {
    fun finish(
        gitDownloadService: GitDownloadService
    ){
        BroadcastManagerForService.unregisterBroadcastReceiver(
            gitDownloadService,
            gitDownloadService.broadcastReceiverForGitDownload,
        )
        gitDownloadService.notificationManager.cancel(
            gitDownloadService.chanelId
        )
        gitDownloadService.gitDownloadJob?.cancel()
        gitDownloadService.getListFileConJob?.cancel()
        gitDownloadService.stopSelf()
    }
}