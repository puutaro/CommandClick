package com.puutaro.commandclick.service.lib.file_download.libs

enum class FileDownLoadStatus(
    val title: String,
    val message: String
) {
    RUNNING(
        "File download..",
        "downloading.."
    ),
    STAN(
        "File download fail",
        "Cannot download"
    ),
    COMP(
        "File Download comp",
        "Get comp %s"
    ),
    GET_FILE_LIST(
        "File download",
        "[%ss] Get file list.."
    ),
    FAILURE_FILE_LIST(
        "Download fail",
    "Cannot get file list: %s",
    ),
    FAILURE_GREP_FILE_LIST(
        "Download fail",
        "No exist: %s",
    ),
    CONNECTION_ERR(
    "Download fail",
    "Connection err: %s",
    )
}