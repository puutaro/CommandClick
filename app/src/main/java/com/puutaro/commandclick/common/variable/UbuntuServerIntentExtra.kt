package com.puutaro.commandclick.common.variable

enum class UbuntuServerIntentExtra(
    val schema: String
) {
    ubuntuStartCommand("ubuntuStartCommand"),
    ubuntuCroutineJobTypeListForKill("ubuntu_croutine_job_type"),
    adminShellPath("shell_path"),
    adminArgsTabSepaStr("cmd_args_tab_sepa_str"),
    adminMonitorFileName("monitorFileName"),
    backgroundShellPath("shell_path"),
    backgroundArgsTabSepaStr("cmd_args_tab_sepa_str"),
    backgroundMonitorFileName("monitorFileName"),
    fannelDirPath("fannelDirPath"),
    fannelName("fannelName"),
    foregroundShellPath("shell_path"),
    foregroundArgsTabSepaStr("cmd_args_tab_sepa_str"),
    foregroundTimeout("foregroundTimeout"),
}
