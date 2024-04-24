package com.puutaro.commandclick.common.variable.variant

enum class RequestCode(
    val code: Int
) {
    FOLDER_PICKER_FOR_GET_FILE(20),
    FILE_PICKER_FOR_GET_FILE(21),
    FOLDER_PICKER_FOR_GET_FILE_LIST(22),
    FOLDER_PICKER_FOR_DIR_AND_COPY(23),

}