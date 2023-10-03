package com.puutaro.commandclick.common.variable

enum class UbuntuServerIntentExtra(
    val schema: String
) {
    ubuntuStartCommand("ubuntuStartCommand"),
    ubuntuCroutineJobTypeList("ubuntu_croutine_job_type"),
    backgroundShellPath("backgroud_shell_path"),
    backgroundArgsTabSepaStr("background_args_tab_sepa_str"),
    monitorFileName("monitorFileName"),
    fannelDirPath("fannelDirPath"),
    fannelName("fannelName"),
    restart_or_stop_front_system("start_or_stop_front_system"),
}

enum class RESTART_OR_KILL_FRONT_SYSTEM {
    START,
    KILL
}