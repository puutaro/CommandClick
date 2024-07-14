package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.common.variable.network.UsePort

enum class UbuntuBasicProcess(
    val cmd: String,
    val extra: String,
) {
    PROOT("proot", String()),
    HTTPSHD("httpshd", UsePort.HTTP2_SHELL_PORT.num.toString()),
    DROPBEAR("dropbear", UsePort.DROPBEAR_SSH_PORT.num.toString()),
    WSSH("wssh", UsePort.WEB_SSH_TERM_PORT.num.toString()),
}