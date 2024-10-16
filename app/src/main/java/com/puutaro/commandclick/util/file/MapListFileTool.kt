package com.puutaro.commandclick.util.file

import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object MapListFileTool {
    fun update(
        mapListFilePath: String?,
        mapList: List<Map<String, String>>,
        separator: Char,
    ) {
        if (
            mapListFilePath.isNullOrEmpty()
        ) return
        val saveMapListCon = mapList.map { lineMap ->
            convertLineMapToLine(
                lineMap,
                separator
            )
        }.joinToString("\n")
        val curMapListCon = ReadText(mapListFilePath).readText()
        if (
            curMapListCon.isNotEmpty()
            && saveMapListCon == curMapListCon
        ) return
        FileSystems.writeFile(
            mapListFilePath,
            saveMapListCon
        )
    }

    fun insertMapFileInFirst(
        mapListPath: String?,
        recentUpdateMap: Map<String, String>,
        curLineMapList: List<Map<String, String>>? = null
    ) {
        if (
            mapListPath.isNullOrEmpty()
        ) return
        val mapListPathObj = File(mapListPath)
        if (
            !mapListPathObj.isFile
        ) return
        val mapListSeparator =
            ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val srcMapList = when (curLineMapList.isNullOrEmpty()) {
            false -> curLineMapList
            else -> ReadText(mapListPath).textToList().map {
                CmdClickMap.createMap(
                    it,
                    mapListSeparator,
                ).toMap()
            }
        }
        val updateMapList = listOf(recentUpdateMap) + srcMapList.filter { lineMap ->
            lineMap != recentUpdateMap
        }
        val updateMapListCon = updateMapList.map {
            lineMap ->
            convertLineMapToLine(
                lineMap,
                mapListSeparator
            )
        }.joinToString("\n")
        FileSystems.writeFile(
            mapListPath,
            updateMapListCon
        )
    }

    fun updateMapListFileByReplace(
        mapListPath: String?,
        srcAndRepLinePairMapList: List<Pair<Map<String, String>, Map<String, String>>>
    ) {
        if (
            mapListPath.isNullOrEmpty()
        ) return
        val tsvPathObj = File(mapListPath)
        if (!tsvPathObj.isFile) return
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val srcLineMapList = ReadText(mapListPath).textToList().map {
            CmdClickMap.createMap(
                it,
                mapListSeparator
            ).toMap()
        }
        val replaceTsvConList = srcLineMapList.map { srcLineMap ->
            val hitLineMap = srcAndRepLinePairMapList.find {
                it.first == srcLineMap
            }
            when (hitLineMap == null) {
                true -> srcLineMap
                else -> hitLineMap.second
            }
        }
        if (
            srcLineMapList == replaceTsvConList
        ) return
        FileSystems.writeFile(
            mapListPath,
            replaceTsvConList.map { lineMap ->
                convertLineMapToLine(
                    lineMap,
                    mapListSeparator
                )
            }.joinToString("\n")
        )
    }

    fun updateMapListFileByRemove(
        tsvPath: String?,
        removeItemLineMapList: List<Map<String, String>>,
    ){
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val tsvPathObj = File(tsvPath)
        if(!tsvPathObj.isFile) return
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val removeTsvCon = ReadText(tsvPath).textToList().map{
            CmdClickMap.createMap(
                it,
                mapListSeparator
            ).toMap()
        }.filter {
            !removeItemLineMapList.contains(it)
        }.map{
            lineMap ->
            convertLineMapToLine(
                lineMap,
                mapListSeparator
            )
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

    fun insertByLastUpdate(
        mapListPath: String,
        insertLineMap: Map<String, String>,
    ){
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val insertLine = convertLineMapToLine(
            insertLineMap,
            mapListSeparator
        )
        val updateTsvCon = listOf(
            insertLine,
            ReadText(mapListPath).readText(),
        ).joinToString("\n")
        FileSystems.writeFile(
            mapListPath,
            updateTsvCon
        )
    }

    fun convertLineMapToLine(
        lineMap: Map<String, String>,
        separator: Char,
    ): String {
        val separatorStr = separator.toString()
        return lineMap.map {
                lineMapEntry ->
            "${lineMapEntry.key}=${lineMapEntry.value}"
        }.joinToString(separatorStr)
    }
}