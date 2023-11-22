package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket

object CopyFannelServer {
    private const val keySeparator = "!"
    private var responseString = String()
    private var intentRequestMonitorJob: Job? = null

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
        val context = terminalFragment.context
            ?: return
        withContext(Dispatchers.IO) {
            terminalFragment.copyFannelSocket?.close()
        }
        terminalFragment.copyFannelSocket = withContext(Dispatchers.IO) {
            ServerSocket(UsePort.COPY_FANNEL_PORT.num)
        }

        while (true) {
            var isTerminated = false
            responseString = String()
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
                    var headerLine: String? = null
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
                    responseString = catResponseHandler(
                        terminalFragment,
                        terminalFragment.currentAppDirPath,
                        receivePath.trim(),
                    )
//                    responseString = payload.toString()
                    val response = String.format(
                        "HTTP/1.1 200 OK\nContent-Length: %d\r\n\r\n%s",
                        responseString.length,
                        responseString
                    )
                    writer.write(response.toByteArray())
                }catch (e: Exception){
                    LogSystems.stdErr(
                        "inuptstream err ${e}"
                    )
                } finally {
                    client.close()
                }
            }
        }
    }

    private fun catResponseHandler(
        terminalFragment: TerminalFragment,
        currentAppDirPath: String,
        receivePath: String,
    ): String {
        return  when(receivePath){
            ReceivePathMacroType.GET_FILE_LIST.name
            -> catFileList(
                currentAppDirPath
            )
            ReceivePathMacroType.CLOSE_COPY_SERVER.name
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
        currentAppDirPath: String
    ): String {
        return File(currentAppDirPath).walk().map{
            if(!it.isFile) return@map String()
            it.absolutePath
        }.joinToString("\n")
            .trim()
            .removePrefix("$currentAppDirPath/")
            .removePrefix(currentAppDirPath)
            .replace("\n$currentAppDirPath/", "\n")
            .replace(Regex("\n\n*"), "\n")
            .trim()
    }

    private fun closeCopyServer(
        terminalFragment: TerminalFragment
    ): String {
        terminalFragment.copyFannelSocket?.close()
        intentRequestMonitorJob?.cancel()
        return String()
    }

    private fun catFileCon(
        currentAppDirPath: String,
        receivePath: String
    ): String {
        val catFilePath = "$currentAppDirPath/$receivePath"
        val catFilePathObj = File(catFilePath)
        val logAbsoluteFilePath =  catFilePathObj.absolutePath
        if(!catFilePathObj.isFile)  {
            LogSystems.stdErr("not found $logAbsoluteFilePath")
            return String()
        }
        val limitHandredMegaByte = 100000000
        val catFileSize = catFilePathObj.length()
        if(catFileSize == 0L) {
            LogSystems.stdWarn("Blank file  $logAbsoluteFilePath")
            return String()
        }
        if(
            catFileSize > limitHandredMegaByte
        ) {
            LogSystems.stdErr("file size too many ${catFileSize} / $logAbsoluteFilePath")
            return String()
        }
        val parentDirPath = catFilePathObj.parent
            ?: let {
                LogSystems.stdErr("not found $logAbsoluteFilePath")
                return String()
            }
        val fileName = catFilePathObj.name
        return ReadText(
            parentDirPath,
            fileName
        ).readText()
    }
}


enum class ReceivePathMacroType(
){
    GET_FILE_LIST,
    CLOSE_COPY_SERVER,
}



