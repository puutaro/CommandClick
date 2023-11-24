package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.LogSystems
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


object CopyFannelServer {
    private var intentRequestMonitorJob: Job? = null
    private val macroSeparator = QrSeparator.sepalator.str

    fun exit(
        terminalFragment: TerminalFragment
    ){
        terminalFragment.copyFannelSocket?.close()
    }

    fun launch(
        terminalFragment: TerminalFragment
    ){
        terminalFragment.copyFannelSocket?.close()
        intentRequestMonitorJob?.cancel()
        intentRequestMonitorJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                execCopy (
                    terminalFragment
                )
            }
        }
    }

    private suspend fun execCopy (
        terminalFragment: TerminalFragment
    ){
        withContext(Dispatchers.IO) {
            terminalFragment.copyFannelSocket?.close()
        }
        terminalFragment.copyFannelSocket = withContext(Dispatchers.IO) {
            ServerSocket(UsePort.COPY_FANNEL_PORT.num)
        }

        while (true) {
            var isTerminated = false
            val client = withContext(Dispatchers.IO) {
                try {
                    LogSystems.stdSys(
                        "accept start"
                    )
                    terminalFragment.copyFannelSocket?.accept()
                } catch (e:Exception){
                    LogSystems.stdErr("${e}")
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
                        terminalFragment,
                        terminalFragment.currentAppDirPath,
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
        terminalFragment: TerminalFragment,
        currentAppDirPath: String,
        receivePath: String,
    ): ByteArray {
        return  when {
            receivePath.startsWith(ReceivePathMacroType.GET_FILE_LIST.name)
            -> catFileList(
                currentAppDirPath,
                receivePath
            )
            ReceivePathMacroType.CLOSE_COPY_SERVER.name == receivePath
            -> closeCopyServer(
                terminalFragment
            )
            else -> catFileCon(
                currentAppDirPath,
                receivePath
            )
        }
    }

    private fun catFileList(
        currentAppDirPath: String,
        receivePath: String,
    ): ByteArray {
        val parentDirPath = makeParentDirPath(
            currentAppDirPath,
            receivePath,
        )
//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qrParentDirPath.txt",
//            parentDirPath
//        )
        val fileListCon = File(parentDirPath).walk().map{
            if(!it.isFile) return@map String()
            it.absolutePath
        }.joinToString("\n")
            .trim()
            .replace(Regex("\n\n*"), "\n")
//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qrFileList_inserver.txt",
//            fileListCon
//        )
        return fileListCon.toByteArray()
//            .removePrefix("$currentAppDirPath/")
//            .removePrefix(currentAppDirPath)
//            .replace("\n$currentAppDirPath/", "\n")
//            .replace(Regex("\n\n*"), "\n")
//            .trim()
    }

    private fun closeCopyServer(
        terminalFragment: TerminalFragment
    ): ByteArray {
        terminalFragment.copyFannelSocket?.close()
        intentRequestMonitorJob?.cancel()
        return byteArrayOf()
    }

    private fun catFileCon(
        currentAppDirPath: String,
        receivePath: String
    ): ByteArray {

        val catFilePath = convertServerFilePath(
            currentAppDirPath,
            receivePath,
        )
        val catFilePathObj = File(catFilePath)
        if(!catFilePathObj.isFile)  {
            LogSystems.stdErr("not found $catFilePath")
            return byteArrayOf()
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
        receivePathAndArg: String,
    ): String {
        val parentDirSrc =
            receivePathAndArg
                .removeSuffix("/")
                .split(macroSeparator)
                .getOrNull(1)
                ?.trim()
        if(
            parentDirSrc.isNullOrEmpty()
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



