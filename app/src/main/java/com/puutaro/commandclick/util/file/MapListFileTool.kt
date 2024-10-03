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
        val separatorStr = separator.toString()
        val saveMapListCon = mapList.map { lineMap ->
            lineMap.map {
                "${it.key}=${it.value}"
            }.joinToString(separatorStr)
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
        lineMapList: List<Map<String, String>>? = null
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
        val srcMapList = when (lineMapList.isNullOrEmpty()) {
            false -> lineMapList
            else -> ReadText(mapListPath).textToList().map {
                CmdClickMap.createMap(
                    it,
                    mapListSeparator,
                ).toMap()
            }
        }
        val updateMapListCon = listOf(recentUpdateMap) + srcMapList.filter { lineMap ->
            lineMap != recentUpdateMap
        }
        FileSystems.writeFile(
            mapListPath,
            updateMapListCon.joinToString("\n")
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
                lineMap.map {
                    "${it.key}=${it.value}"
                }.joinToString(mapListSeparator.toString())
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
            lineMap.map {
                "${it.key}=${it.value}"
            }.joinToString(mapListSeparator.toString())
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
}