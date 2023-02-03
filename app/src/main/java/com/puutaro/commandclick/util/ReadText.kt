package com.puutaro.commandclick.util

import android.util.Log
import java.io.File


class ReadText(
    private val dirPath: String,
    private val fileName: String
) {
    fun readText(): String {
        val success = FileSystems.createDirs(dirPath)
        if(!success) {
            val err_message = "cannnot mkdir: ${dirPath}"
            Log.e(javaClass.name, err_message)
            return err_message
        }
        try {
            FileSystems.createFiles(
                dirPath,
                fileName,
            )
        } catch (e: java.lang.Exception) {
            val err_message = "cannot create file: ${dirPath}/${fileName}"
            Log.e(javaClass.name, err_message)
            return err_message
        }
        val targetFile = File(dirPath, fileName)
        return try{
            targetFile.readText()
        } catch(e: Exception) {
            ""
        }

    }

    fun readTextForHtml(): String {
        val success = FileSystems.createDirs(dirPath)
        if(!success) {
            val err_message = "cannnot mkdir: ${dirPath}"
            Log.e(javaClass.name, err_message)
            return err_message
        }
        try {
            FileSystems.createFiles(
                dirPath,
                fileName,
            )
        } catch (e: java.lang.Exception) {
            val err_message = "cannot create file: ${dirPath}/${fileName}"
            Log.e(javaClass.name, err_message)
            return err_message
        }
        val targetFile = File(dirPath, fileName)
        return try{
            val convertAtagLtGtToTmpStr = targetFile.readText()
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("%", "&#37;")
            convertAtagLtGtToTmpStr
        } catch(e: Exception) {
            ""
        }

    }

    fun txetToList(
    ): List<String> {
        val shellFile = File(
            dirPath,
            fileName
        )
        if(!shellFile.isFile){
            return listOf()
        }
        return shellFile.readLines()
    }
}