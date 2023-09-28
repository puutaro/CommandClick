package com.puutaro.commandclick.common.variable.network

enum class UsePort(
    val num: Int
) {
    pluseRecieverPort(10080),
    pcPulseSetServer(10081),
    UBUNTU_PULSE_RECEIVER_PORT(10090),
    UBUNTU_PC_PULSE_SET_SERVER_PORT(10091),
    HTTP2_SHELL_PORT(15000),
    WEB_SSH_TERM_PORT(18080),
    DROPBEAR_SSH_PORT(10022)
}