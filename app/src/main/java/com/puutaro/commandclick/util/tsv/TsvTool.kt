package com.puutaro.commandclick.util.tsv

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object TsvTool {
    fun updateTsv(
        tsvPath: String?,
        listIndexList: List<String>,
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val saveTsvCon = listIndexList.joinToString("\n")
        val curTsvCon = ReadText(tsvPath).readText()
        if(
            curTsvCon.isNotEmpty()
            && saveTsvCon == curTsvCon
        ) return
        FileSystems.writeFile(
            tsvPath,
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
        val removeTsvCon = ReadText(tsvPath,).textToList().filter {
            !removeItemLineList.contains(it.trim())
        }.joinToString("\n")
        FileSystems.writeFile(
            tsvPath,
            removeTsvCon
        )
    }

    fun updateTsvByClick(
        tsvPath: String?,
        recentUpdateTsvLine: String,
    ){
        if(tsvPath.isNullOrEmpty()) return
        val tsvPathObj = File(tsvPath)
        if(!tsvPathObj.isFile) return
        val srcTsvConList = ReadText(tsvPath).textToList()
        val updateTsvCon = listOf(recentUpdateTsvLine) + srcTsvConList.filter {
            it != recentUpdateTsvLine
        }
        FileSystems.writeFile(
            tsvPath,
            updateTsvCon.joinToString("\n")
        )
    }

    fun updateTsvByReplace(
        tsvPath: String?,
        srcAndRepLinePairList: List<Pair<String, String>>,
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val tsvPathObj = File(tsvPath)
        if(!tsvPathObj.isFile) return
        val srcTsvConList = ReadText(tsvPath).textToList()
        val replaceTsvConList = srcTsvConList.map {
            srcTsvLine ->
            val hitTsvLine = srcAndRepLinePairList.find {
                it.first == srcTsvLine
            }
            when(hitTsvLine == null) {
                true -> srcTsvLine
                else -> hitTsvLine.second
            }
        }

        if(
            srcTsvConList == replaceTsvConList
        ) return
        FileSystems.writeFile(
            tsvPath,
            replaceTsvConList.joinToString("\n")
        )
    }

    fun insertByLastUpdate(
        tsvPath: String,
        insertLine: String,
    ){
        val updateTsvCon = listOf(
            insertLine,
            ReadText(tsvPath).readText(),
        ).joinToString("\n")
        FileSystems.writeFile(
            tsvPath,
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