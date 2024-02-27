package com.puutaro.commandclick.service.lib.file_upload.libs

enum class FileUploadStatus(
    val title: String,
    val message: String
){
    RUNNING(
        "File upload..",
        "uploading.."
    ),
    STAN(
    "File upload fail",
    "Cannot upload"
    )
}