package com.puutaro.commandclick.util.file

import com.puutaro.commandclick.util.str.SpeedReplacer
import java.io.File


class ReadText(
    private val filePath: String
//    private val dirPath: String,
//    private val fileName: String
) {

    companion object {
        val leavesLineForTerm = 500
    }
    fun readText(): String {
        if(
            filePath.isEmpty()
        ) return String()
//        if(dirPath.isEmpty()) return String()
//        if(fileName.isEmpty()) return String()
        val targetFile = File(filePath)
        if(!targetFile.isFile) return String()
        return try{
            targetFile.readText()
        } catch(e: Exception) {
            String()
        }

    }

    fun readTextForHtml(): String {
        if(
            filePath.isEmpty()
        ) return String()
//        if(dirPath.isEmpty()) return String()
//        if(fileName.isEmpty()) return String()
        val targetFile = File(filePath)
        if(
            !targetFile.isFile
        ) return String()
        return try{
            SpeedReplacer.replace(
                targetFile.readText(),
                sequenceOf(
                    Pair("<", "&lt;"),
                    Pair(">", "&gt;"),
                    Pair("%", "&#37;"),
                )
            )
        } catch(e: Exception) {
            String()
        }
    }

    fun textToList(
    ): List<String> {
        if(
            filePath.isEmpty()
        ) return emptyList()
//        if(dirPath.isEmpty()) return emptyList()
//        if(fileName.isEmpty()) return emptyList()
        val targetFile = File(filePath)
        if(
            !targetFile.isFile
        ) return emptyList()
        return try {
            targetFile.readLines()
        } catch(e: Exception){
            emptyList()
        }
    }
}