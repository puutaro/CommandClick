package com.puutaro.commandclick.util

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption


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
            CommandClickScriptVariable.SHELL_FILE_SUFFIX
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
                CommandClickScriptVariable.JS_FILE_SUFFIX
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
                CommandClickScriptVariable.SHELL_FILE_SUFFIX
            ) || it.endsWith(
                CommandClickScriptVariable.JS_FILE_SUFFIX
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
                CommandClickScriptVariable.SHELL_FILE_SUFFIX
            ) || it.endsWith(
                CommandClickScriptVariable.JS_FILE_SUFFIX
            ) || it.endsWith(
                CommandClickScriptVariable.HTML_FILE_SUFFIX
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

    fun readFromAssets(
        context: Context?,
        assetRelativePath: String,
    ): String {
        val fis2: InputStream =
            context?.assets?.open(
                assetRelativePath
            ) ?: return String()
        val contents = try {
            fis2.bufferedReader().use {
                it.readText()
            }
        } catch(e: Exception) {
            fis2.close()
            return String()
        }
        fis2.close()
        return contents
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

    fun copyFileOrDir(
        context: Context?,
        path: String,
        replacePrefix: String,
        targetDirPath: String,
    ) {
        val assetManager = context?.assets
            ?: return
        var assets: Array<String>? = null
        try {
            assets = assetManager.list(path)
                ?: return
            if (
                assets.size == 0
            ) {
                copyFile(
                    context,
                    path,
                    replacePrefix,
                    targetDirPath
                )
                return
            }
            val dir = File(
                "${targetDirPath}/${path.removePrefix("${replacePrefix}/")}"
            )
            if (
                !dir.exists()
            ) dir.mkdir()
            for (i in assets.indices) {
                copyFileOrDir(
                    context,
                    path + "/" + assets[i],
                    replacePrefix,
                    targetDirPath
                )
            }
        } catch (ex: IOException) {
            Log.e("tag", "I/O Exception", ex)
        }
    }

    private fun copyFile(
        context: Context?,
        filename: String,
        replacePrefix: String,
        targetDirPath: String
    ) {
        val assetManager = context?.getAssets()
            ?: return
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(filename)
            val newFileName = targetDirPath + "/" + filename.removePrefix("${replacePrefix}/")
            out = FileOutputStream(newFileName)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null
            out.flush()
            out.close()
            out = null
        } catch (e: java.lang.Exception) {
            Log.e("tag", e.message!!)
        }
    }
}
