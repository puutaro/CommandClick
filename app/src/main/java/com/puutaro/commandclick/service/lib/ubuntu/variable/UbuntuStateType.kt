package com.puutaro.commandclick.service.lib.ubuntu.variable

enum class UbuntuStateType(
    val title: String,
    val message: String,
) {
    WAIT("Ubuntu wait..", "Ubuntu wait.."),
    UBUNTU_SETUP_WAIT(
        "Ubuntu Setup, Ok?",
        "Take 5 minutes for install"
    ),
    WIFI_WAIT("Connect wifi!", "Connect wifi! and restart"),
    ON_SETUP("Ubuntu Setup..", "Ubuntu Setup..(take 5 minutes)"),
    RUNNING("Ubuntu running..", "%d process.."),
}