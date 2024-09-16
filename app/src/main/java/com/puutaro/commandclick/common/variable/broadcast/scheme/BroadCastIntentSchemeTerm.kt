package com.puutaro.commandclick.common.variable.broadcast.scheme

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
//    FZHTML_LAUNCH(
//        "com.puutaro.commandclick.fzhtml.launch",
//        "edit_path",
//    ),
    MONITOR_TOAST(
        "com.puutaro.commandclick.monitorToast.launch",
        "message"
    ),
    DEBUGGER_NOTI(
    "com.puutaro.commandclick.js_debug.noti",
    "debugger_noti"
    ),
    DEBUGGER_JS_WATCH(
        "com.puutaro.commandclick.js_debug.watch",
        "js_debug_watch"
    ),
    DEBUGGER_SYS_WATCH(
        "com.puutaro.commandclick.sys_debug.watch",
        "sys_debug_watch"
    ),
    DEBUGGER_CLOSE(
    "com.puutaro.commandclick.js_debug.close",
    "debugger_close"
    ),
    POCKET_WEBVIEW_LAUNCH(
        "com.puutaro.commandclick.pocket_webview_launch.launch",
        ""
    ),
//    POCKET_WEBVIEW_PRELOAD_URL(
//        "com.puutaro.commandclick.pocket_webview_preloadurl.load",
//        ""
//    ),
    POCKET_WEBVIEW_LOAD_URL(
        "com.puutaro.commandclick.pocket_webview_loadurl.load",
        ""
    ),
    FANNEL_PIN_BAR_UPDATE(
        "com.puutaro.commandclick.fannel_pin_bar_update.update",
        String()
    )
}