package com.puutaro.commandclick.util

import android.util.Log
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.io.IOException
import java.nio.file.Files


class FileSystems {
    companion object {

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
            if(fileName == "-") return
            val filePath = File(dirPath, fileName)
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
                CommandClickShellScript.EMPTY_STRING
                || fileName ==
                        CommandClickShellScript.EMPTY_STRING +
                CommandClickShellScript.SHELL_FILE_SUFFIX
            ) return
            val monitor1File = File(
                dirPath,
                fileName
            )
            if(!monitor1File.exists()) {
               createFiles(
                    dirPath,
                    fileName
                )
            } else {
                val time= System.currentTimeMillis();
                monitor1File.setLastModified(time)
            }
        }

        fun sortedFiles(
            dirPath: String,
            reverse: String = ""
        ): List<String> {
            val dirfiles = File(dirPath).listFiles()
            if(dirfiles == null) return listOf("-")
            if( reverse == "") {
                dirfiles.sortWith(LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)
            } else {
                dirfiles.sortWith(LastModifiedFileComparator.LASTMODIFIED_REVERSE)
            }
            return dirfiles.map{
                it.name
            }
        }

        fun filterSuffixShellFiles(
            dirPath: String,
            reverse: String = ""
        ): List<String> {
            return sortedFiles(
                dirPath,
                reverse
            ).filter {
                it.endsWith(
                    CommandClickShellScript.SHELL_FILE_SUFFIX
                )
            }
        }

        fun filtersStartWithFileName(
            dirPath: String,
            reverse: String = ""
        ): List<String> {
            val cmdclickMonitorFileNameSuffix = UsePath.cmdclickMonitorFileNameSuffix
            return sortedFiles(
                dirPath,
                reverse
            ).filter {
                it.startsWith(
                    cmdclickMonitorFileNameSuffix
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
                Files.move(from.toPath(), to.toPath())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        fun copyFile(
            sourceShellFilePath: String,
            destiShellFilePath: String
        ){
            try {
                Files.copy(
                    File(sourceShellFilePath).toPath(),
                    File(destiShellFilePath).toPath()
                )
            } catch (e: Exception) {
                return
            }
        }

        fun moveFile(
            sourceShellFilePath: String,
            destiShellFilePath: String
        ){
            try {
                Files.move(
                    File(sourceShellFilePath).toPath(),
                    File(destiShellFilePath).toPath()
                )
            } catch (e: Exception) {
                return
            }
        }
    }
}