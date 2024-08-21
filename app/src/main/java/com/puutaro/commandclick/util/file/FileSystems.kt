package com.puutaro.commandclick.util.file

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.LogSystems
import org.apache.commons.io.FileUtils
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.DigestInputStream
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object FileSystems {

    fun createDirs(dirPath: String): Boolean {
        val dirPathObj = File(dirPath)
        try {
            if (dirPathObj.exists()) {
                return true
            }
            return dirPathObj.mkdirs()
        } catch(e: Exception) {
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e.stackTrace}"
            )
            return false
        }
    }

    fun createFiles(
        filePath: String,
    ) {
        if(
            filePath.isEmpty()
        ) return
        val filePathObj = File(filePath)
        if(filePathObj.isDirectory) return
        if(filePathObj.exists()) return
        try {
            filePathObj.createNewFile()
        } catch (e: java.lang.Exception){
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e.stackTrace}"
            )
        }
    }

    fun writeFile(
        filePath: String,
        contents: String
    ) {
        val filePathObj = File(filePath)
        val dirPath = filePathObj.parent
            ?: return
        if(
            dirPath == "-"
            || dirPath.isEmpty()
        ) return
        val fileName = filePathObj.name
        if(
            fileName == "-"
            || fileName.isEmpty()
        ) return
        createDirs(dirPath).let {
            isCreate ->
            if(!isCreate) return
        }
        try {
            filePathObj.writeText(contents)
        } catch (e: java.lang.Exception){
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e.stackTrace}"
            )
        }
    }

    fun writeFileToDirByTimeStamp(
        dirPath: String,
        contents: String
    ) {
        if(
            dirPath.isEmpty()
            || File(dirPath).isFile
        ) return
        createDirs(dirPath).let {
                isCreate ->
            if(!isCreate) return
        }
        val fileName = "${LocalDateTime.now()}.txt"
        val filePathObj = File(dirPath, fileName)
        try {
            filePathObj.writeText(contents)
        } catch (e: java.lang.Exception){
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e.stackTrace}"
            )
        }
    }


    fun removeFiles(
        filePath: String,
    ) {
        val filePathObj = File(filePath)
        if(
            !filePathObj.exists()
            || !filePathObj.isFile
        ) return
        try {
            filePathObj.delete()
        } catch (e: java.lang.Exception){
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e.stackTrace}"
            )
        }
    }

    fun removeDir(
        dirPath: String,
    ) {
        val filePath = File(dirPath)
        if(!filePath.isDirectory) return
        try {
            filePath.deleteRecursively()
        } catch (e: java.lang.Exception){
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e.stackTrace}"
            )
        }
    }

    fun updateLastModified(
        filePath: String
    ){
        val monitor1File = File(filePath)
        val fileName = monitor1File.name
        if(
            fileName ==
            CommandClickScriptVariable.EMPTY_STRING
            || fileName ==
                    CommandClickScriptVariable.EMPTY_STRING +
            UsePath.SHELL_FILE_SUFFIX
        ) return
        if(
            !monitor1File.exists()
        ) {
           createFiles(
               monitor1File.absolutePath
            )
            return
        }
        val time= System.currentTimeMillis()
        monitor1File.setLastModified(time)
    }

    fun updateWeekPastLastModified(
        filePath: String
    ){
        val monitor1File = File(filePath)
        val fileName = monitor1File.name
        if(
            fileName ==
            CommandClickScriptVariable.EMPTY_STRING
            || fileName ==
            CommandClickScriptVariable.EMPTY_STRING +
            UsePath.SHELL_FILE_SUFFIX
        ) return
        if(
            !monitor1File.exists()
        ) {
            createFiles(monitor1File.absolutePath)
            return
        }
        val currentTime= System.currentTimeMillis()
        val weekPastMillis =  (24 * 7) * (60 * 60) * 1000
        val weekPastTime = currentTime - weekPastMillis
        monitor1File.setLastModified(weekPastTime)
    }

    fun sortedFiles(
        dirPath: String,
        reverse: String = String()
    ): List<String> {
        try {
            val dirfiles = File(dirPath).listFiles()
            if(dirfiles == null) return listOf(String())
            if (reverse.isEmpty()) {
                dirfiles.sortWith(LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)
            } else {
                dirfiles.sortWith(LastModifiedFileComparator.LASTMODIFIED_REVERSE)
            }
            return dirfiles.map {
                it.name
            }
        }catch (e: java.lang.Exception){
            return emptyList()
        }
    }

    fun showDirList(
        dirPath: String
    ): List<String> {
        val directories =  File(dirPath).list {
                dir, name ->
            File(dir, name).isDirectory
        } ?: return emptyList()
        return directories.toList()
    }

    fun filterSuffixJsFiles(
        dirPath: String,
        reverse: String = String()
    ): List<String> {
        return sortedFiles(
            dirPath,
            reverse
        ).filter {
            it.endsWith(
                UsePath.JS_FILE_SUFFIX
            )
        }
    }

//    fun getRecentAppDirPath(): String {
//        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
//        val jsSuffix = UsePath.JS_FILE_SUFFIX
//        val cmdclickSystemAppDirNameJs = "${UsePath.cmdclickDefaultAppDirName}${jsSuffix}"
//        val recentAppDirName = filterSuffixJsFiles(
//            cmdclickAppDirAdminPath,
//            "on"
//        ).filter { it != cmdclickSystemAppDirNameJs }.firstOrNull()?.removeSuffix(
//            jsSuffix
//        ) ?: UsePath.cmdclickDefaultAppDirName
//        return File(
//            UsePath.cmdclickAppDirPath,
//            recentAppDirName,
//        ).absolutePath
//    }

    fun filterSuffixShellOrJsFiles(
        dirPath: String,
        reverse: String = String()
    ): List<String> {
        return sortedFiles(
            dirPath,
            reverse
        ).filter {
            it.endsWith(
                UsePath.SHELL_FILE_SUFFIX
            ) || it.endsWith(
                UsePath.JS_FILE_SUFFIX
            )
        }
    }

    fun filterSuffixShellOrJsOrHtmlFiles(
        dirPath: String,
        reverse: String = String()
    ): List<String> {
        return sortedFiles(
            dirPath,
            reverse
        ).filter {
            it.endsWith(
                UsePath.SHELL_FILE_SUFFIX
            ) || it.endsWith(
                UsePath.JS_FILE_SUFFIX
            ) || it.endsWith(
                UsePath.HTML_FILE_SUFFIX
            )
        }
    }


    fun copyDirectory(
        sourceDirPath: String,
        destiDirPath: String,
    ){
        val from = File(sourceDirPath)
        val to = File(destiDirPath)
        try {
            from.copyRecursively(to, true)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun moveDirectory(
        sourceDirPath: String,
        destiDirPath: String,
    ){
        copyDirectory(
            sourceDirPath,
            destiDirPath
        )
        removeDir(sourceDirPath)
    }

    fun moveFile(
        sourceShellFilePath: String,
        destiShellFilePath: String
    ){
        if(
            !File(sourceShellFilePath).isFile
        ) return
        try {
            copyFile(
                sourceShellFilePath,
                destiShellFilePath,
            )
            removeFiles(
                sourceShellFilePath
            )
        } catch (e: Exception) {
            return
        }
    }

    fun copyFile(
        sourceShellFilePath: String,
        destiShellFilePath: String
    ){
        if(
            !File(sourceShellFilePath).isFile
        ) return
        try {
            val parentDirPath = File(destiShellFilePath).parent
                ?: return
            createDirs(parentDirPath).let {
                    isCreate ->
                if(!isCreate) return
            }
            Files.copy(
                File(sourceShellFilePath).toPath(),
                File(destiShellFilePath).toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        } catch (e: Exception) {
            LogSystems.stdErrByNoBroad(
                "${e.cause}, ${e.message}, ${e}"
            )
            return
        }
    }

    fun removeAndCreateDir(
        dirPath: String
    ){
        removeDir(
            dirPath
        )
        createDirs(
            dirPath
        )
    }

    fun updateFile(
        filePath: String,
        updateCon: String,
    ){
        val currentCon =
            ReadText(
                filePath
            ).readText()
        writeFile(
            filePath,
            "${currentCon}\n${updateCon}"
        )
    }

    fun writeFromByteArray(
        filePath: String,
        byteArrayCon: ByteArray,
    ){
        val file = File(filePath)
        removeFiles(file.absolutePath)
        val dirPath = file.parent ?: return
        createDirs(dirPath).let {
                isCreate ->
            if(!isCreate) return
        }
        if(file.isDirectory) return
        try {
            FileUtils.writeByteArrayToFile(file, byteArrayCon)
        } catch(e: Exception){
            LogSystems.stdWarn(e.toString())
        }
    }

    fun savePngFromBitMap(
        filePath: String,
        bitmap: Bitmap,
        quality: Int = 100
    ){
        try {
            val filePathObj = File(filePath)
            val dirPath = filePathObj.parent
                ?: return
            createDirs(
                dirPath
            )
            removeFiles(filePath)
            val outputStream = FileOutputStream(filePathObj)
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
            //outputStream.flush()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogSystems.stdErrByNoBroad(e.stackTrace.toString())
        }
    }

    fun checkSum(
        filePath: String
    ): String {
        val targetFile = File(filePath)
        if(
            !targetFile.isFile
        ) return filePath
        try {
            val inputStream = FileInputStream(targetFile)
            return DigestInputStream(
                inputStream,
                MessageDigest.getInstance("MD5")
            ).use { input ->
                val buffer = ByteArray(1024 * 1024) // buffer size は任意のサイズ (ここでは1MB)
                var read = 0
                while (read != -1) {
                    // ファイル末尾まで読み込む
                    read = input.read(buffer)
                }
                input.messageDigest.digest().joinToString("") { "%02x".format(it) }
            }
        } catch (e: Exception){
            LogSystems.stdErrByNoBroad(e.toString())
            return filePath
        }
    }

    fun execCopyFileWithDir(
        srcFileObj: File,
        destiFilePathObjSrc: File,
        isOverride: Boolean = false
    ): String {
        val sourceFileDirPath = srcFileObj.parent
            ?: return String()
        val sourceFilePath = srcFileObj.absolutePath
        val destiFileDirPath = destiFilePathObjSrc.parent
            ?: return String()
        val destiFilePath = when(
            destiFilePathObjSrc.isFile
            && !isOverride
        ) {
            true -> {
                CcPathTool.makeRndSuffixFilePath(
                    destiFilePathObjSrc.absolutePath
                )
//                val fileName = destiFilePathObjSrc.name
//                val fileRawName = CcPathTool.makeFannelRawName(fileName)
//                val extend = CcPathTool.subExtend(destiFilePathObjSrc.name)
//                "${destiFileDirPath}/" +
//                        "${fileRawName}_${CommandClickScriptVariable.makeCopyPrefix()}${extend}"
            }
            else ->
                destiFilePathObjSrc.absolutePath
        }
        copyFile(
            sourceFilePath,
            destiFilePath
        )
        val destiFilePathObj = File(destiFilePath)
        val sourceFannelDir =
            CcPathTool.makeFannelDirName(
                srcFileObj.name
            )
        val sourceFannelDirPath = "${sourceFileDirPath}/${sourceFannelDir}"
        val destiFannelDir = CcPathTool.makeFannelDirName(
            destiFilePathObj.name
        )
        val destiFannelDirPath = "${destiFileDirPath}/${destiFannelDir}"
        copyDirectory(
            sourceFannelDirPath,
            destiFannelDirPath
        )
        return destiFilePath
    }

    fun moveFileWithDir(
        srcFileObj: File,
        destiFilePathObj: File,
        isOverride: Boolean = false
    ){
        execCopyFileWithDir(
            srcFileObj,
            destiFilePathObj,
            isOverride
        )
        removeFileWithDir(
            srcFileObj,
        )
    }

    fun removeFileWithDir(
        srcFileObj: File,
    ){
        val parentDirPath = srcFileObj.parent
            ?: return
        val fannelDirName = CcPathTool.makeFannelDirName(srcFileObj.name)
        val fannelDirPath = "${parentDirPath}/${fannelDirName}"
        removeDir(fannelDirPath)
        removeFiles(srcFileObj.absolutePath)
    }

    fun switchLastModify(
        fromFileObj: File,
        toFileObj: File,
    ){
        if(
            !fromFileObj.isFile
            || !toFileObj.isFile
        ) return
        val fromLastModify = fromFileObj.lastModified()
        val toLastModify = toFileObj.lastModified()
        fromFileObj.setLastModified(toLastModify)
        toFileObj.setLastModified(fromLastModify)
    }

    fun getLocalDatetimeFromString(): LocalDateTime {
        //現在日時を取得
        val now = LocalDateTime.now()
        println(now)


        //フォーマットを指定
        val format = "yyyy/MM/dd HH:mm:ss"
        val f: DateTimeFormatter = DateTimeFormatter.ofPattern(format)
        val localDatetimeStr = now.format(f)
        val dtf = DateTimeFormatter.ofPattern(format)
        return LocalDateTime.parse(localDatetimeStr, dtf)
    }
}
