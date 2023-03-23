package com.puutaro.commandclick.common.variable

enum class BroadCastIntentScheme(
    val action: String,
    val scheme: String
) {
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
    STOP_GIT_CLONE(
    "com.puutaro.commandclick.git_clone_stop.launch",
    "stop",
    ),
}