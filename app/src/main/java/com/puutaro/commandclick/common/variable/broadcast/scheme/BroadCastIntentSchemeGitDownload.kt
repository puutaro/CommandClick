package com.puutaro.commandclick.common.variable.broadcast.scheme

enum class BroadCastIntentSchemeGitDownload(
    val action: String,
    val scheme: String
) {
    STOP_GIT_DOWNLOAD(
        "com.puutaro.commandclick.git_download_service.stop",
        "stop"
    ),
    STAN_GIT_DOWNLOAD(
        "com.puutaro.commandclick.git_download_service.pend",
        "pend"
    ),
}