package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeGitClone(
    val action: String,
    val scheme: String
) {
    STOP_GIT_CLONE(
        "com.puutaro.commandclick.git_clone_stop.launch",
        "stop",
    ),
}