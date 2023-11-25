package com.puutaro.commandclick.common.variable.intent.extra

enum class FileDownloadExtra(
    val schema: String
) {
    MAIN_URL("main_url"),
    CURRENT_APP_DIR_PATH_FOR_TRANSFER("current_app_dir_path"),
    FULL_PATH_OR_FANNEL_RAW_NAME("full_path_or_fannel_raw_name")
}