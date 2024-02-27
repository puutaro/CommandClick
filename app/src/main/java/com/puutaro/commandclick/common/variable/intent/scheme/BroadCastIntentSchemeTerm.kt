package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeTerm(
    val action: String,
    val scheme: String
) {
    MONITOR_TEXT_PATH(
        "com.puutaro.commandclick.url.monitor_text_path",
        "monitor_text_path"
    ),
    MONITOR_MANAGER(
        "com.puutaro.commandclick.url.monitor_manager",
        "monitor_manager"
    ),
    ULR_LAUNCH(
        "com.puutaro.commandclick.url.launch",
        "url",
    ),
    HTML_LAUNCH(
    "com.puutaro.commandclick.html.launch",
    "edit_path",
    ),
    FZHTML_LAUNCH(
        "com.puutaro.commandclick.fzhtml.launch",
        "edit_path",
    ),
    MONITOR_TOAST(
        "com.puutaro.commandclick.monitorToast.launch",
        "message"
    )
}