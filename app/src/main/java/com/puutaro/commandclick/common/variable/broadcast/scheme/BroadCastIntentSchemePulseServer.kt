package com.puutaro.commandclick.common.variable.broadcast.scheme

enum class BroadCastIntentSchemePulseServer(
    val action: String,
    val scheme: String
) {
    STOP_PULSE_RECIEVER(
        "com.puutaro.commandclick.pulse_server.stop",
        "stop",
    ),
    RESTART_PULSE_RECIEVER(
        "com.puutaro.commandclick.pulse_server.restart",
        "restart",
    ),
}