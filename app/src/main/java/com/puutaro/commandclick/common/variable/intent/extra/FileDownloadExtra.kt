package com.puutaro.commandclick.common.variable.intent.extra

enum class FileDownloadExtra(
    val schema: String
) {
    MAIN_URL("main_url"),
    CURRENT_APP_DIR_PATH_FOR_DOWNLOAD("current_app_dir_path"),
    CURRENT_APP_DIR_PATH_FOR_UPLOADER("curent_app_dir_path_for_uploader"),
    FULL_PATH_OR_FANNEL_RAW_NAME("full_path_or_fannel_raw_name"),
    IS_MOVE_TO_CURRENT_DIR("isMoveToCurrentDir"),
}