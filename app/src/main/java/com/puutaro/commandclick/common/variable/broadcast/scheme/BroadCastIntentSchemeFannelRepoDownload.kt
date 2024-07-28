package com.puutaro.commandclick.common.variable.broadcast.scheme

enum class BroadCastIntentSchemeFannelRepoDownload(
    val action: String,
    val scheme: String
) {
    STOP_FANNEL_REPO_DOWNLOAD(
        "com.puutaro.commandclick.git_clone_stop.launch",
        "stop",
    ),
}