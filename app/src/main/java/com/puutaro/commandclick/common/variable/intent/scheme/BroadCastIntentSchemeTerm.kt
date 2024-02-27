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
    ),
    JS_DEBUG_NOTI(
    "com.puutaro.commandclick.js_debug.noti",
    "noti"
    ),
    JS_DEBUG_WATCH(
        "com.puutaro.commandclick.js_debug.watch",
        "watch"
    ),
    JS_DEBUG_CLOSE(
    "com.puutaro.commandclick.js_debug.close",
    "close"
    )
}