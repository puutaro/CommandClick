package com.puutaro.commandclick.proccess.qr

import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object SaveFromFannelServer {

    fun save(
        terminalFragment: TerminalFragment,
        mainUrl: String,
        fannelRawName: String,
    ){
        val currentAppDirPath = terminalFragment.currentAppDirPath
        CoroutineScope(Dispatchers.IO).launch {
            val fileList = withContext(Dispatchers.IO) {
                CurlManager.post(
                    mainUrl,
                    String(),
                    ReceivePathMacroType.GET_FILE_LIST.name,
                    2000
                )
            }
            withContext(Dispatchers.IO) {
                execSaveFile(
                    terminalFragment,
                    mainUrl,
                    fileList,
                    currentAppDirPath,
                    fannelRawName
                )
            }
        }
    }

    private suspend fun execSaveFile(
        terminalFragment: TerminalFragment,
        mainUrl: String,
        fileList: String,
        destiDirPath: String,
        fannelRawName: String = String(),
    ){
//        val cpStartTime = LocalDateTime.now()
//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qr_cat.txt",
//            String()
//        )
//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qr_cplist.txt",
//            String()
//        )
//        FileSystems.updateFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qr_trueFannel.txt",
//            String()
//        )
//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qr_fileList.txt",
//            "### $fannelRawName\n---\n$fileList"
//        )
        val cpFileList = fileList.split("\n").filter {
            it.startsWith(fannelRawName)
        }
        val cpFileListIndexSize = cpFileList.size - 1
        (cpFileList.indices).forEach {
            val relativeCpFilePath = cpFileList[it]
            withContext(Dispatchers.IO){
                delay(100)
            }
                val destiFileObj = withContext(Dispatchers.IO) {
                    File("$destiDirPath/$relativeCpFilePath")
                }
                val destiFileParentDirPath =  withContext(Dispatchers.IO) {
                    destiFileObj.parent
                        ?: String()
                }
                val destiFileName = withContext(Dispatchers.IO) {
                    destiFileObj.name
                }
                val con = withContext(Dispatchers.IO) {
//                    FileSystems.updateFile(
//                        UsePath.cmdclickDefaultAppDirPath,
//                        "qr_cplist.txt",
//                        relativeCpFilePath
//                    )
                    var conSrc = String()
                    for (i in 1..3) {
                        conSrc = CurlManager.post(
                            mainUrl,
                            "Content-type\ttext/plain,Connection\tclose",
                            relativeCpFilePath,
                            5000,
                        )
                        if (
                            conSrc.isNotEmpty()
                        ) break
                    }
                    conSrc
                }
//                FileSystems.updateFile(
//                    UsePath.cmdclickDefaultAppDirPath,
//                    "qr_cat.txt",
//                    "### ${destiFileObj.absolutePath}\n${con}"
//                )
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    FileSystems.writeFile(
                        destiFileParentDirPath,
                        destiFileName,
                        con
                    )
                }
//                withContext(Dispatchers.IO){
//                    FileSystems.writeFile(
//                        UsePath.cmdclickDefaultAppDirPath,
//                        "qr_cp_endTime1.txt",
//                        "### cpStartTime: ${cpStartTime}\ncpEndTime: ${LocalDateTime.now()}"
//                    )
//                }
                withContext(Dispatchers.Main){
                    if(it % 5 != 0) return@withContext
                    Toast.makeText(
                        terminalFragment.context,
                        "[$it / $cpFileListIndexSize] get ok ${destiFileName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if(it < cpFileListIndexSize) return@launch
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        terminalFragment.context,
                        "Get comp",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                withContext(Dispatchers.IO){
                    CurlManager.post(
                        mainUrl,
                        "Content-type\ttext/plain,Connection\tclose",
                        ReceivePathMacroType.CLOSE_COPY_SERVER.name,
                        2000,
                    )
                }
            }
        }
    }
}