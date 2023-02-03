package com.puutaro.commandclick.common.variable

enum class BroadCastIntentScheme(
    val action: String,
    val scheme: String
) {
    ULR_LAUNCH(
        "com.puutaro.commandclick.url.launch",
        "url",
    )
}