package com.puutaro.commandclick.common.variable.broadcast.scheme

enum class BroadCastIntentSchemeUbuntu(
    val action: String,
    val scheme: String
) {
    START_UBUNTU_SERVICE(
        "com.puutaro.commandclick.ubuntu_service.start",
        "start",
    ),
    STOP_UBUNTU_SERVICE(
        "com.puutaro.commandclick.ubuntu_service.stop",
        "stop",
    ),
    UPDATE_PROCESS_NUM_NOTIFICATION(
        "com.puutaro.commandclick.ubuntu_service.update_process_num",
        "update_process_num",
    ),
    IS_ACTIVE_UBUNTU_SERVICE(
        "com.puutaro.commandclick.ubuntu_service.is_active",
        "is_active",
    ),
    CMD_KILL_BY_ADMIN(
        "com.puutaro.commandclick.ubuntu_service.background_cmd_kill",
        "background_cmd_kill",
    ),
    BACKGROUND_CMD_START(
        "com.puutaro.commandclick.ubuntu_service.background_cmd_start",
        "background_cmd_start",
    ),
//    ADMIN_CMD_START(
//        "com.puutaro.commandclick.ubuntu_service.admin_cmd_start",
//        "admin_cmd_start",
//    ),
    RESTART_UBUNTU_SERVICE_FROM_ACTIVITY(
        "com.puutaro.commandclick.ubuntu_service.restart",
        "restart",
    ),
    OPEN_FANNEL(
        "com.puutaro.commandclick.ubuntu_service.open_fannel",
        "open_fannel",
    ),
    WIFI_WAIT_NITIFICATION(
        "com.puutaro.commandclick.ubuntu_service.wifi_wait",
        "wifi_wait",
    ),
    ON_UBUNTU_SETUP_NOTIFICATION(
        "com.puutaro.commandclick.ubuntu_service.on_ubuntu_setup",
        "on_ubuntu_setup",
    ),
    ON_UBUNTU_SETUP_QUIZ_NOTIFICATION(
        "com.puutaro.commandclick.ubuntu_service.on_ubuntu_setup_quiz",
        "on_ubuntu_setup_quiz",
    ),
    ON_RUNNING_NOTIFICATION(
        "com.puutaro.commandclick.ubuntu_service.on_running",
        "on_running",
    ),
    ON_SLEEPING_NOTIFICATION(
        "com.puutaro.commandclick.ubuntu_service.sleeping",
        "sleeping",
    ),
    FOREGROUND_CMD_START(
        "com.puutaro.commandclick.ubuntu_service.shell2http",
        "shell2http",
    ),
    DOWN_LOAD_ERR_NOTI(
    "com.puutaro.commandclick.ubuntu_service.download_err_noti",
        String()
    ),
    COPY_TO_SD_CARD(
    "com.puutaro.commandclick.ubuntu_service.copy_dir_to_sd_card",
        String()
    ),
    DELETE_FROM_SD_CARD(
        "com.puutaro.commandclick.ubuntu_service.delete_from_sd_card",
        String()
    ),
}