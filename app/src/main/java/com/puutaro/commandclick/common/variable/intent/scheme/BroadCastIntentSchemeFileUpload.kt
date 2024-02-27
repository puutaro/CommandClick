package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeFileUpload(
    val action: String,
    val scheme: String
) {
    STOP_FILE_UPLOAD(
        "com.puutaro.commandclick.upload_server.stop",
        "stop"
    ),
    STAN_FILE_UPLOAD(
        "com.puutaro.commandclick.upload_server.stan",
        "stan"
    ),
}