package com.puutaro.commandclick.util.tsv

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object TsvTool {

    private val twoColumnNum = 2
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

    fun updateTsvByKey(
        tsvPath: String?,
        updateTsvConList: List<String>,
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        var updateTsvCon =
            ReadText(tsvPath).readText()
        updateTsvConList.forEach {
            val keyValueList = it.split("\t")
            val keyName = keyValueList.firstOrNull()
            if(
                keyName.isNullOrEmpty()
            ) return@forEach
            val value = keyValueList.getOrNull(1)
                ?: String()
            val tempUpdateTsvCon = execUpdateTsvByKey(
                updateTsvCon,
                keyName,
                value
            )
            if(
                tempUpdateTsvCon.isNullOrEmpty()
            ) return@forEach
            updateTsvCon = tempUpdateTsvCon
        }
        if(
            updateTsvCon.isEmpty()
        ) return
        FileSystems.writeFile(
            tsvPath,
            updateTsvCon
        )
    }

    private fun execUpdateTsvByKey(
        updateTsvCon: String,
        key: String,
        value: String,
    ): String? {
        val curTsvConMap = updateTsvCon
            .replace("\t", "=")
            .let {
                CmdClickMap.createMap(
                    it,
                    '\n'
                )
            }.toMap()
        val updateTsvConMap =
            curTsvConMap + mapOf(key to value)
        if(
            curTsvConMap == updateTsvConMap
        ) return null
        return updateTsvConMap.map {
            "${it.key}\t${it.value}"
        }.joinToString("\n")

    }

    fun updateTsvByRemove(
        tsvPath: String?,
        removeItemLineList: List<String>,
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val tsvPathObj = File(tsvPath)
        if(!tsvPathObj.isFile) return
        val removeTsvCon = ReadText(tsvPath).textToList().filter {
            !removeItemLineList.contains(it.trim())
        }.joinToString("\n")
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "remove.txt").absolutePath,
//            listOf(
//                "removeItemLineList: ${removeItemLineList}",
//                "tsvPath: ${tsvPath}",
//                "tsvCon: ${ReadText(tsvPath).readText()}",
//                "removeTsvCon: ${removeTsvCon}"
//            ).joinToString("\n\n")
//        )
        FileSystems.writeFile(
            tsvPath,
            removeTsvCon
        )
    }

    fun insertTsvInFirst(
        tsvPath: String?,
        recentUpdateTsvLine: String,
        tsvConList: List<String>? = null
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val tsvPathObj = File(tsvPath)
        if(
            !tsvPathObj.isFile
        ) return
        val srcTsvConList = when(tsvConList.isNullOrEmpty()) {
            false -> tsvConList
            else -> ReadText(tsvPath).textToList()
        }
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

    fun filterByColumnNum(
        srcList: List<String>,
        columnNum: Int,
    ): List<String>{
        return srcList.filter {
            it.split("\t").size == columnNum
        }
    }

    fun getFirstValue(
        path: String,
    ): String {
        return getFirstLine(path)
            .split("\t")
            .getOrNull(1)
            ?: String()
    }

    fun getFirstKey(
        path: String,
    ): String {
        return getFirstLine(path)
            .split("\t")
            .getOrNull(0)
            ?: String()
    }

    fun getFirstLine(
        path: String,
    ): String {
        return ReadText(
            path
        ) .textToList().let {
            filterByColumnNum(
                it,
                twoColumnNum
            )
        }.firstOrNull() ?: String()
    }

    fun getSecondRow(
        con: String,
    ): String {
        return filterByColumnNum(
            con.split("\n"),
            twoColumnNum
        ).map {
            it.split("\t").lastOrNull()
                ?: String()
        }.filter {
            it.isNotEmpty()
        }.joinToString("\n")
    }

    fun getFirstRow(
        con: String,
    ): String {
        return filterByColumnNum(
            con.split("\n"),
            twoColumnNum
        ).map {
            it.split("\t").firstOrNull()
                ?: String()
        }.filter {
            it.isNotEmpty()
        }.joinToString("\n")
    }

    fun getKeyValue(
        path: String,
        key: String,
    ): String {
        return filterByColumnNum(
            ReadText(path).textToList(),
            twoColumnNum
        ).firstOrNull {
            it.startsWith("${key}\t")
        }?.split("\t")
            ?.lastOrNull()
            ?: String()
    }
}