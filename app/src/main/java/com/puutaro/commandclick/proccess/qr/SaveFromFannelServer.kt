package com.puutaro.commandclick.proccess.qr

import android.widget.Toast
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object SaveFromFannelServer {

    fun save(
        terminalFragment: TerminalFragment,
        mainUrl: String,
        getPathOrFannelRawName: String,
    ){
        val macroSeparator = QrSeparator.sepalator.str
        val currentAppDirPath = terminalFragment.currentAppDirPath
        val parentDirPath = File(getPathOrFannelRawName).parent
            ?.removeSuffix("/")
            ?: String()
        val getFileListMacro = ReceivePathMacroType.GET_FILE_LIST.name
        val getFilePathAndArg = if(
            getPathOrFannelRawName.startsWith("/")
        ) "$getFileListMacro $macroSeparator $parentDirPath"
        else getFileListMacro


        CoroutineScope(Dispatchers.IO).launch {
            val fileListCon = withContext(Dispatchers.IO) {
                var fileListConSrc = String()
                for(i in 1..3) {
                    fileListConSrc = CurlManager.post(
                        mainUrl,
                        String(),
                        getFilePathAndArg,
                        2000
                    )
                    if(
                        fileListConSrc.isNotEmpty()
                    ) break
                    delay(300)
                }
                fileListConSrc
            }
            withContext(Dispatchers.IO){
                FileSystems.writeFile(
                    UsePath.cmdclickDefaultAppDirPath,
                    "qrFileList.txt",
                    "### ${getFilePathAndArg}\n${fileListCon}"
                )
            }
            withContext(Dispatchers.IO) {
                execSaveFile(
                    terminalFragment,
                    mainUrl,
                    fileListCon,
                    currentAppDirPath,
                    getPathOrFannelRawName
                )
            }
        }
    }

    private suspend fun execSaveFile(
        terminalFragment: TerminalFragment,
        mainUrl: String,
        fileListCon: String,
        currentAppDirPath: String,
        getPathOrFannelRawName: String = String(),
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
        FileSystems.writeFile(
            UsePath.cmdclickDefaultAppDirPath,
            "qr_fileListCon.txt",
            fileListCon
        )
        val cpFileList = makeCpFileListCon(
            getPathOrFannelRawName,
            fileListCon,
            currentAppDirPath,
        )
        withContext(Dispatchers.IO){
            FileSystems.writeFile(
                UsePath.cmdclickDefaultAppDirPath,
                "qrFileList_execSaveFile.txt",
                "${cpFileList.joinToString("\n")}"
            )
        }
        return
        val cpFileListIndexSize = cpFileList.size - 1
        (cpFileList.indices).forEach {
            val cpFilePath = cpFileList[it]

            val destiFileObj = withContext(Dispatchers.IO) {
                File(cpFilePath)
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
                            cpFilePath,
                            5000,
                        )
                        if (
                            conSrc.isNotEmpty()
                        ) break
                        delay(300)
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

    private fun makeCpFileListCon(
        getPathOrFannelRawName: String,
        fileListCon: String,
        currentAppDirPath: String,
    ): List<String> {
        FileSystems.writeFile(
            UsePath.cmdclickDefaultAppDirPath,
            "qrStartWith.txt",
            "${getPathOrFannelRawName}\n" + CcPathTool.convertIfFunnelRawNamePathToFullPath(
                currentAppDirPath,
                getPathOrFannelRawName
            )
        )
        return fileListCon.let {
            CcPathTool.convertIfFunnelRawNamePathToFullPath(
                currentAppDirPath,
                it
            )
        }.let {
            CcPathTool.convertAppDirPathToLocal(
                it,
                currentAppDirPath
            )
        }.split("\n").filter {
            it.startsWith(
                CcPathTool.convertIfFunnelRawNamePathToFullPath(
                    currentAppDirPath,
                    getPathOrFannelRawName
                )
            )
        }
    }

}