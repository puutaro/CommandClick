package com.puutaro.commandclick.util

import android.util.Log
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Date


object FileSystems {

    fun createDirs(dirPath: String): Boolean {
        val dirPathObj = File(dirPath)
        try {
            if (dirPathObj.exists()) {
                return true
            }
            return dirPathObj.mkdirs()
        } catch(e: Exception) {
            Log.e("FileSystems", "not mkdir ${dirPath}")
            return false
        }
    }

    fun createFiles(
        dirPath: String,
        fileName: String,
    ) {
        if(fileName == "-") return
        val filePath = File(dirPath, fileName)
        if(filePath.exists()) return
        try {
            filePath.createNewFile()
        } catch (e: java.lang.Exception){
            Log.e(
                "createFiles",
                "cannot make file: " +
                    "${e.cause}, ${e.message}, ${e.stackTrace}, ${filePath.absolutePath}"
            )
        }
    }

    fun writeFile(
        dirPath: String,
        fileName: String,
        contents: String
    ) {
        if(
            fileName == "-"
            || fileName.isEmpty()
        ) return
        val filePath = File(dirPath, fileName)
        if(!filePath.isDirectory){
            createDirs(dirPath)
        }
        try {
            filePath.writeText(contents)
        } catch (e: java.lang.Exception){
            Log.e(
                "writeFiles",
                "cannot write file: " +
                        "${e.cause}, ${e.message}, ${e.stackTrace}, ${filePath.absolutePath}"
            )
        }
    }


    fun removeFiles(
        dirPath: String,
        fileName: String,
    ) {
        val filePath = File(dirPath, fileName)
        if(!filePath.exists()) return
        try {
            filePath.delete()
        } catch (e: java.lang.Exception){
            Log.e(
                "deleteFiles",
                "cannot delete file: " +
                        "${e.cause}, ${e.message}, ${e.stackTrace}, ${filePath.absolutePath}"
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
            Log.e(
                "removeDir",
                "cannot remove file: " +
                        "${e.cause}, ${e.message}, ${e.stackTrace}, ${filePath.absolutePath}"
            )
        }
    }


    fun updateLastModified(
        dirPath: String,
        fileName: String
    ){
        if(
            fileName ==
            CommandClickScriptVariable.EMPTY_STRING
            || fileName ==
                    CommandClickScriptVariable.EMPTY_STRING +
            UsePath.SHELL_FILE_SUFFIX
        ) return
        val monitor1File = File(
            dirPath,
            fileName
        )
        if(
            !monitor1File.exists()
        ) {
           createFiles(
                dirPath,
                fileName
            )
            return
        }
        val time= System.currentTimeMillis()
        monitor1File.setLastModified(time)
    }

    fun updateWeekPastLastModified(
        dirPath: String,
        fileName: String
    ){
        if(
            fileName ==
            CommandClickScriptVariable.EMPTY_STRING
            || fileName ==
            CommandClickScriptVariable.EMPTY_STRING +
            UsePath.SHELL_FILE_SUFFIX
        ) return
        val monitor1File = File(
            dirPath,
            fileName
        )
        if(
            !monitor1File.exists()
        ) {
            createFiles(
                dirPath,
                fileName
            )
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
        val dirfiles = File(dirPath).listFiles()
        if(dirfiles == null) return listOf(String())
        if( reverse.isEmpty()) {
            dirfiles.sortWith(LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)
        } else {
            dirfiles.sortWith(LastModifiedFileComparator.LASTMODIFIED_REVERSE)
        }
        return dirfiles.map{
            it.name
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
        val from = File(sourceDirPath)
        val to = File(destiDirPath)
        try {
            Files.move(
                from.toPath(),
                to.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun moveFile(
        sourceShellFilePath: String,
        destiShellFilePath: String
    ){
        if(
            !File(sourceShellFilePath).isFile
        ) return
        try {
            Files.move(
                File(sourceShellFilePath).toPath(),
                File(destiShellFilePath).toPath(),
                StandardCopyOption.REPLACE_EXISTING
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
            Files.copy(
                File(sourceShellFilePath).toPath(),
                File(destiShellFilePath).toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        } catch (e: Exception) {
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
}
