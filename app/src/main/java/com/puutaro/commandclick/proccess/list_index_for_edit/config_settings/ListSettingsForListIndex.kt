package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.edit.lib.LayoutSettingFile
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.CcFilterTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.ShellTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object ListSettingsForListIndex  {

    private val sectionSeparator = EditComponent.Template.sectionSeparator
    private val typeSeparator = EditComponent.Template.typeSeparator
    private val keySeparator = EditComponent.Template.keySeparator
    private val valueSeparator = EditComponent.Template.valueSeparator

    enum class ListSettingKey(
        val key: String
    ) {
        MAP_LIST_PATH("mapListPath"),
        VIEW_LAYOUT_PATH("viewLayoutPath"),
        DEFAULT_FRAME_TAG("defaultFrameTag"),
        PREFIX("prefix"),
        SUFFIX("suffix"),
        FILTER_SHELL_PATH("filterShellPath"),
        SORT_TYPE("sortType"),
        COMP_MAP_LIST_PATH("compMapListPath"),
        ON_ONLY_EXIST_PATH("onOnlyExistPath"),
    }


    object ViewLayoutPathManager{

        fun getViewLayoutPath(
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            configMap: Map<String, String>?,
            keyName: String,
        ): String {
            return FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                configMap,
                keyName,
            ) ?: String()
        }

        fun parse(
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            viewLayoutPath: String,
        ):  Pair<
                Map<String, String >,
                Map<String, List< List<String> > >,
                >?
//                Triple<
//                Map<String,List<Pair<String, String>> >,
//                Map<String, List< List<String> > >,
//                Map<String, List< List<List<Pair<String, String>>> > >,
//                >?
        {
            var curFrameTag = String()
//            val framePairList: MutableList< Pair<String, List<Pair<String, String>> > > = mutableListOf()
            val framePairsConList: MutableList< Pair<String, String > > = mutableListOf()
            val linearPairConList: MutableList< Pair<String, List<String> > > = mutableListOf()
//            val linearPairList: MutableList< Pair<String, List<List<Pair<String, String>>> > > = mutableListOf()

//            val viewLayoutPath = FilePrefixGetter.get(
//                fannelInfoMap,
//                setReplaceVariableMap,
//                indexListMap,
//                ListSettingKey.VIEW_LAYOUT_PATH.key,
//            ) ?: String()
            val viewLayoutPathObj = File(viewLayoutPath)
            if(
                !viewLayoutPathObj.isFile
            ) return null
            val fannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val fannelPath = File(UsePath.cmdclickDefaultAppDirPath, fannelName).absolutePath
            val viewLayoutListSrc = LayoutSettingFile.read(
                viewLayoutPathObj.absolutePath,
                fannelPath,
                setReplaceVariableMap,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout.txt").absolutePath,
//                listOf(
////                    "indexListMap: ${indexListMap}",
//                    "viewLayoutPath: ${viewLayoutPath}",
//                    "File(viewLayoutPath).isFile: ${File(viewLayoutPath).isFile}",
//                    "viewLayoutPath: ${viewLayoutPath}",
//                    "viewLayoutListSrc: ${viewLayoutListSrc.joinToString("####")}",
//                ).joinToString("\n\n") + "\n\n-----------\n\n"
//            )
            val typeSeparator = EditComponent.Template.typeSeparator
            val frameTypeName = EditComponent.Template.LayoutKey.FRAME.key
            val liearTypeName = EditComponent.Template.LayoutKey.LINEAR.key
            val tagKey = EditComponent.Template.EditComponentKey.TAG.key
            viewLayoutListSrc.forEachIndexed { index,
                                               smallLayoutMapCon ->
                val smallLayoutMapConList = smallLayoutMapCon.split("=")
                val layoutKey =
                    smallLayoutMapConList.firstOrNull()
                        ?: return@forEachIndexed
                val layoutTypePairConList =
                    smallLayoutMapConList
                        .filterIndexed { innerIndex, _ -> innerIndex > 0 }
                        .joinToString("=")
                        .let {
                            val trimSectionCon = QuoteTool.trimBothEdgeQuote(it)
                            QuoteTool.splitBySurroundedIgnore(
                                trimSectionCon,
                                sectionSeparator
                            ).filter {
                                it.isNotEmpty()
                            }
                        }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_parse.txt").absolutePath,
//                    listOf(
//                        "layoutKey: ${layoutKey}",
//                        "smallLayoutMapCon: ${smallLayoutMapCon}",
//                        "framePairsConList: ${framePairsConList}",
//                        "linearPairConList: ${linearPairConList}",
//                    ).joinToString("\n") + "\n----\n"
//                )
                when (true) {
                    (layoutKey == frameTypeName) -> {
                        val frameLayoutKeyPairListCon =
                            layoutTypePairConList.firstOrNull()
                                ?: String()
                        val tag =
                            CmdClickMap.createMap(
                                frameLayoutKeyPairListCon,
                                typeSeparator
                            ).firstOrNull {
                                val key = it.first
                                key == tagKey
                            }?.second.let {
                                QuoteTool.trimBothEdgeQuote(it)
                            }
                        curFrameTag = tag
                        val frameTagToCon = Pair(
                            curFrameTag,
                            frameLayoutKeyPairListCon
                        )
                        if (
                            frameTagToCon.first.isEmpty()
                        ) return@forEachIndexed
                        framePairsConList.add(frameTagToCon)
                    }

                    (layoutKey == liearTypeName) -> {
                        val frameTagToLinearKeyPairCon = Pair(
                            curFrameTag,
                            layoutTypePairConList
                        )
                        if (
                            frameTagToLinearKeyPairCon.first.isEmpty()
                        ) return@forEachIndexed
                        linearPairConList.add(frameTagToLinearKeyPairCon)
                    }

                    else -> {}
                }
            }
//                val layoutTypePairList =
//                    smallLayoutMapConList
//                        .filterIndexed { innerIndex, _ ->  innerIndex > 0 }
//                        .joinToString("=")
//                        .let {
//                            val trimSectionCon = QuoteTool.trimBothEdgeQuote(it)
//                            val sectionConList = QuoteTool.splitBySurroundedIgnore(
//                                trimSectionCon,
//                                sectionSeparator
//                            ).filter {
//                                it.isNotEmpty()
//                            }
////                            FileSystems.updateFile(
////                                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_parse_loop.txt").absolutePath,
////                                listOf(
////                                    "layoutKey: ${layoutKey}",
////                                    "trimSectionCon: ${trimSectionCon}",
////                                    "sectionConList: ${sectionConList}",
////                                    "sectionConList0: ${sectionConList.firstOrNull()}",
////                                ).joinToString("\n") + "\n----\n"
////                            )
//                            sectionConList.map {
//                                sectionCon ->
////                                FileSystems.updateFile(
////                                    File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_parse_loop2.txt").absolutePath,
////                                    listOf(
////                                        "layoutKey: ${layoutKey}",
////                                        "trimSectionCon: ${trimSectionCon}",
////                                        "sectionConList: ${sectionConList}",
////                                        "sectionConList0: ${sectionConList.firstOrNull()}",
////                                    ).joinToString("\n") + "\n----\n"
////                                )
//                                CmdClickMap.createMap(
//                                    sectionCon,
//                                    typeSeparator
//                                ).filter {
//                                    it.first.isNotEmpty()
//                                }
//                            }
//                        }


//                when(true) {
//                    (layoutKey == frameTypeName) -> {
//                        val frameLayoutKeyPairList =
//                            layoutTypePairList.firstOrNull()
//                                ?: emptyList()
//                        val tag =
//                            frameLayoutKeyPairList.firstOrNull {
//                                val key = it.first
//                                key == tagKey
//                            }?.second.let {
//                                QuoteTool.trimBothEdgeQuote(it)
//                            }
//                        curFrameTag = tag
//                        val frameTagToCon = Pair(
//                            curFrameTag,
//                            frameLayoutKeyPairList
//                        )
//                        if(
//                            frameTagToCon.first.isEmpty()
//                        ) return@forEachIndexed
//                        framePairList.add(frameTagToCon)
//                    }
//                    (layoutKey == liearTypeName) -> {
//                        val frameTagToLinearKeyPairList = Pair(
//                            curFrameTag,
//                            layoutTypePairList
//                        )
//                        if(
//                            frameTagToLinearKeyPairList.first.isEmpty()
//                        ) return@forEachIndexed
//                        linearPairList.add(frameTagToLinearKeyPairList)
//                    }
//                    else -> {}
//                }
//            }
            val frameTagList = mutableListOf<String>()
            linearPairConList.forEach {
                val frameTag = it.first
                if(
                    frameTag.isNotEmpty()
                    && !frameTagList.contains(frameTag)
                ) frameTagList.add(frameTag)
            }
            val frameTagToLinearPairConListMap = frameTagList.map {
                    frameTag ->
                frameTag to linearPairConList.filter {
                    it.first == frameTag
                }.map {
                    it.second
                }
            }.toMap()
//            val frameTagToLinearKeysListMap = frameTagList.map {
//                frameTag ->
//                frameTag to linearPairList.filter {
//                    it.first == frameTag
//                }.map {
//                    it.second
//                }
//            }.toMap()
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_parse_end.txt").absolutePath,
                listOf(
//                    "layoutKey: ${layoutKey}",
                    "framePairsConList: ${framePairsConList}",
                    "linearPairConList: ${linearPairConList}",
                    "\npair: ${Pair(
                        framePairsConList.toMap(),
                        frameTagToLinearPairConListMap,
//                frameTagToLinearKeysListMap
                    )}"
                ).joinToString("\n") + "\n----\n"
            )
            return Pair(
                framePairsConList.toMap(),
                frameTagToLinearPairConListMap,
//                frameTagToLinearKeysListMap
            )
        }
    }

    object MapListPathManager {

        const val mapListSeparator = ','

        enum class Key(val key: String) {
            SRC_TITLE("srcTitle"),
            SRC_CON("srcCon"),
            SRC_IMAGE("srcImage"),
            VIEW_LAYOUT_TAG("viewLayoutTag"),
        }
    }

    enum class OnOnlyExistPath {
        ON
    }

    enum class SortByKey(
        val key: String
    ) {
        LAST_UPDATE("lastUpdate"),
        SORT("sort"),
        REVERSE("reverse"),
    }

    fun howExistPathForTsv(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        indexListMap: Map<String, String>,
        mapList: List<Map<String, String>>
    ): List<Map<String, String>> {
        val isOnlyExistPath = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            indexListMap,
            ListSettingKey.ON_ONLY_EXIST_PATH.key
        ) == OnOnlyExistPath.ON.name
        if(
            !isOnlyExistPath
        ) return mapList
        return mapList.filter {
            map ->
            val pathEntry = map.get(
                MapListPathManager.Key.SRC_CON.key
            ) ?: return@filter false
            val pathObj = File(pathEntry)
            pathObj.isFile
                    || pathObj.isDirectory
        }
    }

    fun getSortType(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        indexListMap: Map<String, String>?,
    ): SortByKey {
        val sortByKeyStr = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            indexListMap,
            ListSettingKey.SORT_TYPE.key
        )
        return SortByKey.values().firstOrNull {
            it.key == sortByKeyStr
        }  ?: SortByKey.LAST_UPDATE
    }

    object ListIndexListMaker {

        private const val blankListMark = "Let's press sync button at right bellow"
        private const val itemNameMark = "\${ITEM_NAME}"

        fun makeLineMapListHandler(
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            indexListMap: Map<String, String>,
            busyboxExecutor: BusyboxExecutor?
//            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey
        ): MutableList<Map<String, String>> {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "list.txt").absolutePath,
//                listOf(
//                    "tag: ${editFragment.tag}",
//                    "listIndexConfigMap: ${editFragment.listIndexConfigMap}",
//                    "indexListMap: ${indexListMap}",
//                    "listIndexTypeKey: ${listIndexTypeKey.key}",
//                ).joinToString("\n\n")
//            )
            return makeLineMapList(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                busyboxExecutor,
            )
//            return when(listIndexTypeKey) {
////                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////                -> makeFannelListForListView().toMutableList()
//                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
//                    makeTsvConList(
//                        editFragment,
//                        indexListMap,
//                        busyboxExecutor,
//                    )
//                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//                -> makeFileList(
//                    editFragment,
//                    indexListMap,
//                    listIndexTypeKey,
//                )
//            }
        }

//        fun getFilterDir(
//            editFragment: EditFragment,
//            indexListMap: Map<String, String>?,
//            listIndexType : TypeSettingsForListIndex.ListIndexTypeKey,
//        ): String {
//            return when(listIndexType){
////                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////                -> UsePath.cmdclickFannelItselfDirPath
//                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//                -> String()
//                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL ->
//                    FilePrefixGetter.get(
//                        editFragment,
//                        indexListMap,
//                        ListSettingKey.LIST_DIR.key,
//                    ) ?: String()
//            }
//        }

//        private fun makeFileList(
//            editFragment: EditFragment,
//            indexListMap: Map<String, String>,
////            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey
//        ): MutableList<String> {
//            val busyboxExecutor = editFragment.busyboxExecutor
//            val filterDir = String()
////            getFilterDir(
////                editFragment,
////                indexListMap,
////                listIndexTypeKey
////            )
//            FileSystems.createDirs(filterDir)
//            val filterPrefixListCon = FilePrefixGetter.get(
//                editFragment,
//                indexListMap,
//                ListSettingKey.PREFIX.key
//            ) ?: String()
//            val filterSuffixListCon = FilePrefixGetter.get(
//                editFragment,
//                indexListMap,
//                ListSettingKey.SUFFIX.key
//            ) ?: String()
//            val currentFileList = FileSystems.sortedFiles(
//                filterDir,
//                "on"
//            ).let {
//                CompPathManager.concatByCompConWhenNormal(
//                    editFragment,
//                    indexListMap,
//                    filterDir,
//                    it
//                )
//            }
//            val filterShellCon = getFilterShellCon(
//                editFragment,
//                indexListMap,
//            )
//            val fileListSource = makeFileListElement(
//                currentFileList,
//                busyboxExecutor,
//                filterDir,
//                filterPrefixListCon,
//                filterSuffixListCon,
//                filterShellCon,
//            )
//            if(
//                fileListSource.isEmpty()
//            ) return mutableListOf(throughMark)
//            val sortType = getSortType(
//                editFragment,
//                indexListMap
//            )
//            return sortList(
//                sortType,
//                fileListSource,
//            )
//        }

        private fun filterMapList(
            lineMapList: List<Map<String, String>>,
            busyboxExecutor: BusyboxExecutor?,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            filterShellCon: String,
        ): List<Map<String, String>> {
            val srcLabelKey = MapListPathManager.Key.SRC_TITLE.key
            val valueSeparatorStr = valueSeparator.toString()
            return lineMapList.filter {
                lineMap ->
                val srcTitle = lineMap.get(
                    srcLabelKey
                )
                if(
                    srcTitle.isNullOrEmpty()
                ) return@filter false
                CcFilterTool.isFilterByStr(
                    srcTitle,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    false,
                    valueSeparatorStr
                )
            }.map {
                    lineMap ->
                val srcTitle = lineMap.get(
                    srcLabelKey
                ) ?: return@map lineMap
                val filterTitle = makeFilterShellCon(
                    srcTitle,
                    busyboxExecutor,
                    filterShellCon,
                )
                lineMap + mapOf(
                    srcLabelKey to filterTitle
                )
            }.filter {
                    lineMap ->
                val srcTitle = lineMap.get(
                    srcLabelKey
                ) ?: return@filter false
                srcTitle.isNotEmpty()
            }
        }

        private fun makeLineMapList(
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            indexListMap: Map<String, String>,
            busyboxExecutor: BusyboxExecutor?,
        ): MutableList<Map<String, String>> {
            val mapListPath = FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingKey.MAP_LIST_PATH.key,
            ) ?: String()
            val fannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val tsvFilePathObj = File(mapListPath)
            val lineMapListSrc = ReadText(
                mapListPath
            ).readText().let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
                    fannelName
                )
            }.split("\n").map {
                CmdClickMap.createMap(
                    it,
                    MapListPathManager.mapListSeparator
                ).toMap()
            }.let {
                mapList ->
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lmapList.txt").absolutePath,
//                    listOf(
//                        "indexListMap: ${indexListMap}",
//                        "mapListPath: ${mapListPath}",
//                        "mapList: ${mapList}",
//                    ).joinToString("\n\n")
//                )
                howExistPathForTsv(
                    fannelInfoMap,
                    setReplaceVariableMap,
                    indexListMap,
                    mapList,
                )
            }
            val lineMapListBeforeFilter = CompPathManager.concatByCompConWhenTsvEdit(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                lineMapListSrc
            )

            val filterPrefixListCon = indexListMap.get(
                ListSettingKey.PREFIX.key
            ) ?: String()
            val filterSuffixListCon = indexListMap.get(
                ListSettingKey.SUFFIX.key
            ) ?: String()

            val filterShellCon = indexListMap.get(
                ListSettingKey.FILTER_SHELL_PATH.key
            ) ?: String()

            val lineMapList = filterMapList(
                    lineMapListBeforeFilter,
                    busyboxExecutor,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    filterShellCon,
            )
            val sortType = getSortType(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap
            )
//            val isReverseLayout = howReverseLayout(
//                fannelInfoMap,
//                setReplaceVariableMap,
//                indexListMap
//            )
            val sortedLineMapList = sortList(
                sortType,
                lineMapList,
//                isReverseLayout
            )
            updateMapListFile(
                sortType,
                tsvFilePathObj,
                sortedLineMapList,
            )
            return sortedLineMapList.toMutableList()
        }


//        private fun makeTsvConList2(
//            fannelInfoMap: Map<String, String>,
//            setReplaceVariableMap: Map<String, String>?,
//            indexListMap: Map<String, String>,
//            busyboxExecutor: BusyboxExecutor?,
//        ):  List<List<Pair<String, List<List<Pair<String, String>>>>>>
////                List<
////                List<
////                        Pair<
////                                String,
////                                List<List<List<Pair<String, String>>>>
////                                >
////                        >
////                >
//        {
////            val filterShellCon = getFilterShellCon(
////                fannelInfoMap,
////                setReplaceVariableMap,
////                indexListMap,
////            )
//            val tsvFilePath = FilePrefixGetter.get(
//                fannelInfoMap,
//                setReplaceVariableMap,
//                indexListMap,
//                ListSettingKey.TSV_PATH.key,
//            ) ?: String()
//            val fannelName = FannelInfoTool.getCurrentFannelName(
//                fannelInfoMap
//            )
//            val fannelPath = File(UsePath.cmdclickDefaultAppDirPath, fannelName).absolutePath
//            val tsvFilePathObj = File(tsvFilePath)
//            val blockLayoutKeyList = EditComponent.Template.blockLayoutKeyList
//            val tsvConListSrc = LayoutSettingFile.read(
//                tsvFilePathObj.absolutePath,
//                fannelPath,
//                setReplaceVariableMap,
//            )
//            val layoutList = mutableListOf<List< Pair<String, List< List<Pair<String, String>> >> >>()
//            val smallLayoutList = mutableListOf< Pair<String, List< List<Pair<String, String>> >> >()
//            val tsvConListSrcLastIndex = tsvConListSrc.lastIndex
//            tsvConListSrc.forEachIndexed {
//                    index,
//                    smallLayoutMapCon ->
//                val smallLayoutMapConList = smallLayoutMapCon.split("=")
//                val layoutKey =
//                    smallLayoutMapConList.firstOrNull()
//                        ?: return@forEachIndexed
//                val layoutTypePairList =
//                    smallLayoutMapConList
//                        .filterIndexed { innerIndex, _ ->  innerIndex > 0 }
//                        .joinToString(String())
//                        .let {
//                            val trimSectionCon = QuoteTool.trimBothEdgeQuote(it)
//                            val sectionConList = QuoteTool.splitBySurroundedIgnore(
//                                trimSectionCon,
//                                sectionSeparator
//                            )
//                            sectionConList.map {
//                                CmdClickMap.createMap(
//                                    trimSectionCon,
//                                    typeSeparator
//                                )
//                            }
//                        }
//                val isBlockLayoutKey = blockLayoutKeyList.contains(layoutKey)
//                when(true) {
//                    isBlockLayoutKey -> {
//                        if(
//                            smallLayoutList.isNotEmpty()
//                        ) {
//                            layoutList.add(smallLayoutList)
//                            smallLayoutList.clear()
//                        }
//                        smallLayoutList.add(
//                            Pair(
//                                layoutKey,
//                                layoutTypePairList
//                            )
//                        )
//                    }
//                    else -> {
//                        smallLayoutList.add(
//                            Pair(
//                                layoutKey,
//                                layoutTypePairList
//                            )
//                        )
//                        if(
//                            index != tsvConListSrcLastIndex
//                        ) return@forEachIndexed
//                        layoutList.add(smallLayoutList)
//                        smallLayoutList.clear()
//                    }
//                }
//            }
////            return layoutList
////            val tsvConList = CompPathManager.concatByCompConWhenTsvEdit(
////                fannelInfoMap,
////                setReplaceVariableMap,
////                indexListMap,
////                tsvConListSrc
////            )
//            val sortType = getSortType(
//                fannelInfoMap,
//                setReplaceVariableMap,
//                indexListMap
//            )
////            val sortTag = getSortTag(
////                fannelInfoMap,
////                setReplaceVariableMap,
////                indexListMap,
////            )
//            val sortedTsvConList = sortList(
//                sortType,
//                layoutList,
//            )
//            updateTsv(
//                sortType,
//                tsvFilePathObj,
//                sortedTsvConList,
//            )
//            return sortedTsvConList
//        }

//        private fun makeTsvConList2(
//            fannelInfoMap: Map<String, String>,
//            setReplaceVariableMap: Map<String, String>?,
//            indexListMap: Map<String, String>,
//            busyboxExecutor: BusyboxExecutor?,
//        ): MutableList<String> {
//            val filterShellCon = getFilterShellCon(
//                fannelInfoMap: Map<String, String>,
//            setReplaceVariableMap: Map<String, String>?,,
//                indexListMap,
//            )
//            val tsvFilePath = FilePrefixGetter.get(
//                fannelInfoMap,
//                setReplaceVariableMap,
//                indexListMap,
//                ListSettingKey.LIST_DIR.key,
//            ) ?: String()
//            val tsvFilePathObj = File(tsvFilePath)
//            val tsvConListSrc = ReadText(
//                tsvFilePath
//            ).textToList().let {
//                TsvTool.filterByColumnNum(
//                    it,
//                    2
//                )
//            }.map {
//                val titleAndConList = it.split("\t")
//                val con = titleAndConList.last()
//                val title = titleAndConList.first().let {
//                    makeFilterShellCon(
//                        it,
//                        busyboxExecutor,
//                        filterShellCon,
//                    )
//                }
//                "${title}\t${con}"
//            }.let {
//                howExistPathForTsv(
//                    editFragment,
//                    indexListMap,
//                    it,
//                )
//            }
//            val tsvConList = CompPathManager.concatByCompConWhenTsvEdit(
//                editFragment,
//                indexListMap,
//                tsvConListSrc
//            )
//            val sortType = getSortType(
//                editFragment,
//                indexListMap
//            )
//            val sortedTsvConList = sortList(
//                sortType,
//                tsvConList,
//            )
//            updateTsv(
//                sortType,
//                tsvFilePathObj,
//                sortedTsvConList,
//            )
//            return sortedTsvConList
//        }

        fun sortList(
            sortType: SortByKey,
            lineMapList: List<Map<String, String>>,
//            isReverseLayout: Boolean
        ): List<Map<String, String>> {
            return when(sortType){
                SortByKey.LAST_UPDATE -> {
//                    val sortedLineMapList =
//                        if(isReverseLayout) lineMapList.reversed()
//                        else lineMapList
                    lineMapList
                        .toMutableList()
                }
                SortByKey.SORT -> {
                        val sortedLineMapListSrc = lineMapList
                            .sortedWith(
                                compareBy {
                                        map ->
                                    map.get(MapListPathManager.Key.SRC_TITLE.key)
                                }
                            )
//                    val sortedLineMapList =
//                        if(isReverseLayout) sortedLineMapListSrc.reversed()
//                        else sortedLineMapListSrc
                    sortedLineMapListSrc.toMutableList()
                }
                SortByKey.REVERSE -> {
                    val sortedLineMapListSrc = lineMapList.sortedWith(
                        compareBy {
                                map ->
                            map.get(MapListPathManager.Key.SRC_TITLE.key)
                        }
                    ).reversed()
//                    val sortedLineMapList =
//                        if(isReverseLayout) sortedLineMapListSrc.reversed()
//                        else sortedLineMapListSrc
                    sortedLineMapListSrc.toMutableList()
                }
            }
        }

        private fun updateMapListFile(
            sortType: SortByKey,
            tsvFilePathObj: File,
            sortedLineMapList: List<Map<String, String>>,
        ){
            val saveSortedTsvConList = sortListForTsvSave(
                sortType,
                sortedLineMapList,
            )
            MapListFileTool.update(
                tsvFilePathObj.absolutePath,
                saveSortedTsvConList,
                MapListPathManager.mapListSeparator
            )
        }
        fun sortListForTsvSave(
            sortType: SortByKey,
            sortedLineMapList: List<Map<String, String>>,
        ): MutableList<Map<String, String>> {
            val sortedList = when(sortType){
                SortByKey.LAST_UPDATE -> {
                    sortedLineMapList
//                        .reversed()
                        .toMutableList()
                }
                SortByKey.SORT -> {
                    sortedLineMapList.sortedWith(
                        compareBy {
                                map ->
                            map.get(MapListPathManager.Key.SRC_TITLE.key)
                        }
                    )
                        .toMutableList()
                }
                SortByKey.REVERSE -> {
                    sortedLineMapList.sortedWith(
                        compareBy {
                                map ->
                            map.get(MapListPathManager.Key.SRC_TITLE.key)
                        }
                    )
                        .reversed()
                        .toMutableList()
                }
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lsort.txt").absolutePath,
//                listOf(
//                    "sortType: ${sortType}",
//                    "sortedLineMapList: ${sortedLineMapList}",
//                    "sortedList: ${sortedList}",
//                ).joinToString("\n")
//            )
            return sortedList
        }

        private fun makeFilterShellCon(
            srcCon: String,
            busyboxExecutor: BusyboxExecutor?,
            filterShellCon: String,
        ): String {
//            if(
//                filterShellCon.isEmpty()
//                || busyboxExecutor == null
//            ) return srcCon
            val extraArgsMap = mapOf(
                itemNameMark to srcCon
            )
            return ShellTool.filter(
                srcCon,
                busyboxExecutor,
                filterShellCon,
                extraArgsMap
            )
//            busyboxExecutor.getCmdOutput(
//                filterShellCon.replace(
//                    itemNameMark,
//                    srcCon,
//                )
//            )
        }

        private fun makeFannelListForListView(): List<String> {
            val fannelListSource = ReadText(
                File(
                    UsePath.cmdclickFannelListDirPath,
                    UsePath.fannelListMemoryName
                ).absolutePath,
            ).readText()
                .replace(Regex("\\*\\*([a-zA-Z0-9]*)\\*\\*"), "*$1")
                .split(FannelListVariable.cmdclickFannelListSeparator)
            return if (
                fannelListSource.isNotEmpty()
                && !fannelListSource
                    .firstOrNull()
                    ?.trim()
                    .isNullOrEmpty()
            ) {
                fannelListSource
            } else mutableListOf(blankListMark)
        }

        fun getFilterShellCon(
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            indexListMap: Map<String, String>?,
        ): String {
            val shellPath = FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingKey.FILTER_SHELL_PATH.key,
            ) ?: String()
            return execGetFilterShellCon(
                fannelInfoMap,
                setReplaceVariableMap,
                shellPath,
            )
        }

        private fun execGetFilterShellCon(
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            filterShellPath: String,
        ): String {
            if(
                filterShellPath.isEmpty()
            ) return String()
//            val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//                fannelInfoMap
//            )
            val currentFannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            return ReadText(
                filterShellPath
            ).readText().let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
//                    currentAppDirPath,
                    currentFannelName
                )
            }
        }
    }


}

private object CompPathManager {

//    fun concatByCompConWhenNormal(
//        fannelInfoMap: Map<String, String>,
//        setReplaceVariableMap: Map<String, String>?,
//        indexListMap: Map<String, String>,
//        parentDirPath: String,
//        fileList: List<String>
//    ): List<String> {
//        val initFilePath = FilePrefixGetter.get(
//            fannelInfoMap,
//            setReplaceVariableMap,
//            indexListMap,
//            ListSettingsForListIndex.ListSettingKey.COMP_TSV_PATH.key
//        )
//        if(
//            initFilePath.isNullOrEmpty()
//        ) return fileList
//        val initFilePathObj = File(initFilePath)
//        val initConList = when(true){
//            initFilePathObj.isFile ->
//                makeInitConFromFile(
//                    fannelInfoMap,
//                    setReplaceVariableMap,
//                    initFilePathObj,
//                )
//            initFilePathObj.isDirectory -> {
//                val initFileConSrcDir = initFilePathObj.absolutePath
//                FileSystems.sortedFiles(
//                    initFileConSrcDir,
//                    "on"
//                )
//            }
//            else -> return fileList
//        }
//        return concatInitConAndConList(
//            fileList,
//            initConList,
//            parentDirPath,
//        )
//    }
    fun concatByCompConWhenTsvEdit(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        indexListMap: Map<String, String>,
        lineMapListSrc: List<Map<String, String>>
    ): List<Map<String, String>> {
//        if(
//            tsvConList.isNotEmpty()
//        ) return tsvConList
        val compMapListPath = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.COMP_MAP_LIST_PATH.key
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lComp.txt").absolutePath,
//            listOf(
//                "indexListMap: ${indexListMap}",
//                "COMP_MAP_LIST_PATH: ${indexListMap.get(
//                    ListSettingsForListIndex.ListSettingKey.COMP_MAP_LIST_PATH.key
//                )}",
//                "compMapListPath: ${compMapListPath}",
//            ).joinToString("\n")
//        )
        if(
            compMapListPath.isNullOrEmpty()
        ) return lineMapListSrc
        val initMapListPathObj = File(compMapListPath)
        val initMapList = when(true){
            initMapListPathObj.isFile ->
                makeInitConFromFile(
                    fannelInfoMap,
                    setReplaceVariableMap,
                    initMapListPathObj,
                )
            initMapListPathObj.isDirectory -> {
                val initTsvConSrcDir = initMapListPathObj.absolutePath
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "linitTsvConSrcDir.txt").absolutePath,
//                    listOf(
//                        "initTsvConSrcDir: ${initTsvConSrcDir}",
//                        "list: ${FileSystems.sortedFiles(
//                            initTsvConSrcDir
//                        ).map {
//                            val fileObj = File(initTsvConSrcDir, it)
//                            mapOf(
//                                ListSettingsForListIndex.MapListPathManager.Key.SRC_LABEL.key to fileObj.name,
//                                ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key to fileObj.absolutePath
//                            )
//                        }}",
//                    ).joinToString("\n")
//                )
                FileSystems.sortedFiles(
                    initTsvConSrcDir
                ).map {
                    val fileObj = File(initTsvConSrcDir, it)
                    mapOf(
                        ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key to fileObj.name,
                        ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key to fileObj.absolutePath
                    )
                }
            }
            else -> return lineMapListSrc
        }
        return concatInitConAndConList(
            lineMapListSrc,
            initMapList,
        )
    }

    private fun makeInitConFromFile(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        initTsvPathObj: File,
    ): List<Map<String, String>> {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        return ReadText(
            initTsvPathObj.absolutePath
        ).readText().let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName,
            )
        }.split("\n").map {
            CmdClickMap.createMap(
                it,
                mapListSeparator
            ).toMap()
        }
    }

    private fun concatInitConAndConList(
        conList: List<Map<String, String>>,
        initConList: List<Map<String, String>>,
        parentDirPath: String,
    ): List<Map<String, String>> {
        val concatConList = concatInitConAndConList(
            conList,
            initConList,
        )
        val insertInitConList = concatConList.filter {
            !conList.contains(it)
        }
        insertInitConList.forEach {
            map ->
            val srcFileName = map.get(
                ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
            )  ?: return@forEach
            val insertFilePath = File(
                parentDirPath,
                srcFileName
            ).absolutePath
            FileSystems.writeFile(
                insertFilePath,
                String()
            )
        }
        return concatConList
    }

    private fun concatInitConAndConList(
        conList: List<Map<String, String>>,
        initMapList: List<Map<String, String>>,
    ): List<Map<String, String>> {
        if(
            initMapList.isEmpty()
        ) return conList
        val insertInitTsvConList = initMapList.filter {
            !conList.contains(it)
        }
        if(
            insertInitTsvConList.isEmpty()
        ) return conList
        return insertInitTsvConList + conList
    }
}