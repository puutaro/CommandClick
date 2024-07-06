package com.puutaro.commandclick.service.lib.file_upload

import android.content.Context
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.qr.CpFileKey
import com.puutaro.commandclick.service.FileUploadService
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServer
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.time.LocalDateTime


object CopyFannelServer {
    private var intentRequestMonitorJob: Job? = null
    private val limitFileMegaByteLength = 5000000

    fun exit(
        fileUploadService: FileUploadService
    ){
        fileUploadService.copyFannelSocket?.close()
    }

    fun launch(
        fileUploadService: FileUploadService
    ){
        fileUploadService.copyFannelSocket?.close()
        intentRequestMonitorJob?.cancel()
        intentRequestMonitorJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                execCopy (
                    fileUploadService
                )
            }
        }
    }

    private suspend fun execCopy (
        fileUploadService: FileUploadService
    ){
        val context = fileUploadService.applicationContext
        withContext(Dispatchers.IO) {
            if (
                fileUploadService.copyFannelSocket != null
                && fileUploadService.copyFannelSocket?.isClosed != true
            ) fileUploadService.copyFannelSocket?.close()
        }
        fileUploadService.copyFannelSocket = withContext(Dispatchers.IO) {
            ServerSocket(UsePort.COPY_FANNEL_PORT.num)
        }
        val cmdclickTempFileTransferServiceDirPath = fileUploadService.cmdclickTempFileUploadServiceDirPath
        val transferServiceAcceptTimeTxtName = fileUploadService.uploadServiceAcceptTimeTxtName

        while (true) {
            var isTerminated = false
            val client = withContext(Dispatchers.IO) {
                try {
                    FileSystems.writeFile(
                        File(
                            cmdclickTempFileTransferServiceDirPath,
                            transferServiceAcceptTimeTxtName
                        ).absolutePath,
                        LocalDateTime.now().toString()
                    )
                    LogSystems.stdSys(
                        "accept start"
                    )
                    fileUploadService.copyFannelSocket?.accept()
                } catch (e:Exception){
                    LogSystems.stdErr(
                        context,
                        "${e}"
                    )
                    isTerminated = true
                    null
                }
            } ?: return
            if(isTerminated) return
            withContext(Dispatchers.IO) {
                val isr = InputStreamReader(client.getInputStream())
                val br = BufferedReader(isr)
                val writer: OutputStream = client.getOutputStream()
                    ?: return@withContext
                try {
                    //code to read and print headers
                    var headerLine: String?
                    var responseCon = String()
                    while (
                        br.readLine().also {
                            headerLine = it
                        }.isNotEmpty()
                    ) {
                        responseCon += "\t$headerLine"
                    }
                    val payload = StringBuilder()
                    while (br.ready()) {
                        payload.append(br.read().toChar())
                    }
                    delay(300)
                    val receivePath = payload.toString()
                    val responseBodyByteArray = catResponseHandler(
                        fileUploadService,
                        fileUploadService.currentAppDirPath,
                        receivePath.trim(),
                    )

                    val responseHeader = String.format(
                        "HTTP/1.1 200 OK\n" +
                                "Content-Length: %d\r\n\r\n%s",
                        responseBodyByteArray.size,
                        String()
                    )
                    writer.write(
                        responseHeader.toByteArray() + responseBodyByteArray
                    )
                    writer.flush()
                }catch (e: Exception){
                    LogSystems.stdErr(
                        context,
                        "input stream err ${e}"
                    )
                } finally {
                    isr.close()
                    br.close()
                    writer.close()
                    client.close()
                }
            }
        }
    }

    private fun catResponseHandler(
        fileUploadService: FileUploadService,
        currentAppDirPath: String,
        cpFileMapStr: String,
    ): ByteArray {
        val context = fileUploadService.applicationContext
        val cpFileMap = CmdClickMap.createMap(
            cpFileMapStr,
            '\t'
        ) .toMap()
//            cpFileMapStr
//            .split("\t").map {
//                CcScript.makeKeyValuePairFromSeparatedString(
//                    it,
//                "="
//                )
//        }.toMap()
        val cpFileMacro = cpFileMap.get(CpFileKey.CP_FILE_MACRO_FOR_SERVICE.key)
        return  when(cpFileMacro) {
            ReceivePathMacroType.GET_FILE_LIST.name
            -> catFileList(
                currentAppDirPath,
                cpFileMap
            )
            ReceivePathMacroType.CLOSE_COPY_SERVER.name
            -> closeCopyServer(
                fileUploadService
            )
            else -> catFileCon(
                context,
                currentAppDirPath,
                cpFileMap
            )
        }
    }

    private fun catFileList(
        currentAppDirPathSrc: String,
        cpFileMap: Map<String, String>,
    ): ByteArray {
        val path = cpFileMap.get(
            CpFileKey.PATH.key
        ) ?: return byteArrayOf()
        val currentAppDirPath = cpFileMap.get(
            CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key
        ).let {
            if(
                it.isNullOrEmpty()
            ) return@let currentAppDirPathSrc
            it
        }

        val parentDirPath = makeParentDirPath(
            currentAppDirPath,
            path,
        )
        val fileListCon = File(parentDirPath).walk().map{
            if(!it.isFile) return@map String()
            if(it.length() > limitFileMegaByteLength) return@map String()
            it.absolutePath
        }.joinToString("\n")
            .trim()
            .replace(Regex("\n\n*"), "\n")
            .split("\n").sorted().joinToString("\n")
        return fileListCon.toByteArray()
    }

    private fun closeCopyServer(
        fileUploadService: FileUploadService
    ): ByteArray {
        FileUploadFinisher.exit(fileUploadService)
        return byteArrayOf()
    }

    private fun catFileCon(
        context: Context?,
        currentAppDirPathSrc: String,
        cpFileMap: Map<String, String>
    ): ByteArray {
        val path = cpFileMap.get(
            CpFileKey.PATH.key
        ) ?: return byteArrayOf()
        val currentAppDirPath = cpFileMap.get(
            CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key
        ).let {
            if(
                it.isNullOrEmpty()
            ) return@let currentAppDirPathSrc
            it
        }
        val catFilePath = convertServerFilePath(
            currentAppDirPath,
            path,
        )
        val catFilePathObj = File(catFilePath)
        if(!catFilePathObj.isFile)  {
            LogSystems.stdErr(
                context,
                "not found $catFilePath"
            )
            return CurlManager.invalidResponse.toByteArray()
        }
        val fis = FileInputStream(catFilePathObj)
        val inputStream = BufferedInputStream(fis)
        val fileBytes = ByteArray(catFilePathObj.length().toInt())
        inputStream.read(fileBytes)
        inputStream.close()
        return fileBytes
    }

    private fun makeParentDirPath(
        currentAppDirPath: String,
        parentDirSrc: String,
    ): String {
        if(
            parentDirSrc.isEmpty()
        ) return currentAppDirPath
        return CcPathTool.convertAppDirPathToLocal(
            parentDirSrc,
            currentAppDirPath
        )
    }

    private fun convertServerFilePath(
        currentAppDirPath: String,
        receivePath: String,
    ): String {
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        if(
            !receivePath.startsWith(cmdclickAppDirPath)
        ) return receivePath
        val currentAppDirPathRegex = Regex("^${UsePath.cmdclickAppDirPath}/[^/]*")
        return receivePath.replace(
            currentAppDirPathRegex,
            currentAppDirPath
        )
    }
}

enum class ReceivePathMacroType {
    GET_FILE_LIST,
    CLOSE_COPY_SERVER,
}



