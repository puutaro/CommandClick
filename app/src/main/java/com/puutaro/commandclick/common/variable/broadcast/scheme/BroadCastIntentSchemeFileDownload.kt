package com.puutaro.commandclick.common.variable.broadcast.scheme

enum class BroadCastIntentSchemeFileDownload(
    val action: String,
    val scheme: String
) {
    STOP_FILE_DOWNLOAD(
        "com.puutaro.commandclick.transfer_client.stop",
        "stop"
    ),
    STAN_FILE_DOWNLOAD(
        "com.puutaro.commandclick.transfer_client.pend",
        "pend"
    ),
}