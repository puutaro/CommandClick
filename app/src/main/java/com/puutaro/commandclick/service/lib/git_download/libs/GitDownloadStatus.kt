package com.puutaro.commandclick.service.lib.git_download.libs

enum class GitDownloadStatus(
    val title: String,
    val message: String
) {
    RUNNING(
        "Git download..",
        "downloading.."
    ),
    STAN(
        "Git download fail",
        "Cannot download"
    ),
    COMP(
        "Git Download comp",
        "Get comp %s"
    ),
    GET_FILE_LIST(
        "Git download",
        "[%ss] Get file list.."
    ),
    GIT_CLONE(
        "Git clone..",
        "[%ss] clone.."
    ),
    COPY(
        "Copy..",
    "Copy %s"
    ),
    FAILURE_FILE_LIST(
        "Download fail",
        "Cannot get file list: %s",
    ),
    FAILURE_GIT_CLONE(
        "Git clone fail",
        "Cannot clone: %s",
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