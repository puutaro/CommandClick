package com.puutaro.commandclick.util.tsv

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object TsvTool {
    fun updateTsv(
        tsvPath: String?,
        listIndexList: MutableList<String>,
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val tsvPathObj = File(tsvPath)
        val tsvParentDirPath = tsvPathObj.parent
            ?: return
        val tsvName = tsvPathObj.name
        val saveTsvCon = listIndexList.joinToString("\n")
        val curTsvCon = ReadText(
            tsvParentDirPath,
            tsvName
        ).readText()
        if(
            curTsvCon.isNotEmpty()
            && saveTsvCon == curTsvCon
        ) return
        FileSystems.writeFile(
            tsvParentDirPath,
            tsvName,
            saveTsvCon
        )
    }

    fun updateTsvByRemove(
        tsvPath: String?,
        removeItemLineList: List<String>,
    ){
        if(tsvPath.isNullOrEmpty()) return
        val tsvPathObj = File(tsvPath)
        if(!tsvPathObj.isFile) return
        val tsvParentDirPath = tsvPathObj.parent
            ?: return
        val tsvName = tsvPathObj.name
        val removeTsvCon = ReadText(
            tsvParentDirPath,
            tsvName,
        ).textToList().filter {
            !removeItemLineList.contains(it.trim())
        }.joinToString("\n")
        FileSystems.writeFile(
            tsvParentDirPath,
            tsvName,
            removeTsvCon
        )
    }

    fun insertByLastUpdate(
        tsvPath: String,
        insertLine: String,
    ){
        val tsvPathObj = File(tsvPath)
        val tsvParentPath = tsvPathObj.parent ?: return
        val tsvName = tsvPathObj.name
        val updateTsvCon = listOf(
            insertLine,
            ReadText(
                tsvParentPath,
                tsvName
            ).readText(),
        ).joinToString("\n")
        FileSystems.writeFile(
            tsvParentPath,
            tsvName,
            updateTsvCon
        )
    }

    fun uniqByTitle(list: List<String>): List<String> {
        return list.filterIndexed { index, el ->
            val titleConLineList = el.split("\t")
            val title = titleConLineList.firstOrNull()
                ?: return@filterIndexed false
            list.indexOfFirst {
                it.startsWith(title)
            } == index
        }
    }
}