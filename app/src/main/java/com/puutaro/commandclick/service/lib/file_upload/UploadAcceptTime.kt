package com.puutaro.commandclick.service.lib.file_upload

import com.puutaro.commandclick.service.FileUploadService
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object UploadAcceptTime {
    fun monitor(
        fileUploadService: FileUploadService
    ){
        val cmdclickTempFileTransferServiceDirPath = fileUploadService.cmdclickTempFileUploadServiceDirPath
        val transferServiceAcceptTimeTxtName = fileUploadService.uploadServiceAcceptTimeTxtName
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(60000)
                    val currentDateTime = LocalDateTime.now()
                    val closeMinutes = 5L
                    val previousAcceptTime = ReadText(
                        File(
                            cmdclickTempFileTransferServiceDirPath,
                            transferServiceAcceptTimeTxtName
                        ).absolutePath,
                    ).readText().let { previousAcceptTimeStringSrc ->
                        val previousAcceptTimeString = previousAcceptTimeStringSrc.replace("\n", "")
                        try {
                            LocalDateTime.parse(previousAcceptTimeString)
                        } catch (e: Exception) {
                            FileSystems.writeFile(
                                File(
                                    cmdclickTempFileTransferServiceDirPath,
                                    transferServiceAcceptTimeTxtName
                                ).absolutePath,
                                currentDateTime.toString()
                            )
                            currentDateTime
                        }
                    }
                    val closeAcceptTime = previousAcceptTime.plusMinutes(closeMinutes)
                    if (
                        currentDateTime.isAfter(closeAcceptTime)
                    ) break
                }
            }
            withContext(Dispatchers.IO) {
                FileUploadFinisher.exit(fileUploadService)
            }
        }

    }
}