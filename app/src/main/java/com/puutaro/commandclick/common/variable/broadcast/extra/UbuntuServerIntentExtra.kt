package com.puutaro.commandclick.common.variable.broadcast.extra

enum class UbuntuServerIntentExtra(
    val schema: String
) {
    ubuntuStartCommand("ubuntu_start_command"),
    ubuntuRestoreSign("ubuntu_restore_sign"),
    ubuntuCroutineJobTypeListForKill("ubuntu_coroutine_job_type"),
    adminShellPath("shell_path"),
    adminArgsTabSepaStr("args"),
    adminMonitorFileName("monitor_file_name"),
    backgroundShellPath("shell_path"),
    backgroundArgsTabSepaStr("args"),
    backgroundMonitorFileName("monitor_file_name"),
    backgroundResFilePath("res_file_path"),
    fannelDirPath("fannel_dir_path"),
    fannelName("fannel_name"),
    foregroundShellPath("shell_path"),
    foregroundArgsTabSepaStr("args"),
    foregroundTimeout("foreground_timeout"),
}
