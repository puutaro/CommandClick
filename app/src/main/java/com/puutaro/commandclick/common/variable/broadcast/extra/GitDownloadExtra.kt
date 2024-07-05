package com.puutaro.commandclick.common.variable.broadcast.extra

enum class GitDownloadExtra(
    val schema: String
) {
    PREFIX("prefix"),
    CURRENT_APP_DIR_PATH_FOR_TRANSFER("current_app_dir_path"),
    FANNEL_LIST_PATH("fannel_list_path"),
    PARENT_DIR_PATH_FOR_FILE_UPLOAD("parent_dir_path"),
    FANNEL_RAW_NAME("fannel_raw_name"),
}