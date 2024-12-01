package com.puutaro.commandclick.proccess.edit_list.config_settings

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.edit.lib.LayoutSettingFile
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.CcFilterTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.ShellTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.SnakeCamelTool
import org.jsoup.Jsoup
import java.io.File

object ListSettingsForEditList  {

    private val sectionSeparator = EditComponent.Template.sectionSeparator
    private val typeSeparator = EditComponent.Template.typeSeparator
    private val keySeparator = EditComponent.Template.keySeparator
    private val valueSeparator = EditComponent.Template.valueSeparator
    enum class LogErrLabel(val label: String) {
        VIEW_LAYOUT("View layout")
    }

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
            context: Context?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            viewLayoutPath: String,
        ):  Triple<
                Map<String, String >,
                List<Pair<String, String>>,
                Map<String, List< List<String> > >,
                >?
        {
            val viewLayoutPathObj = File(viewLayoutPath)
            if(
                !viewLayoutPathObj.isFile
            ) return null
            val fannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val fannelPath = File(UsePath.cmdclickDefaultAppDirPath, fannelName).absolutePath
            val viewLayoutListSrc = LayoutSettingFile.read(
                context,
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
            val plusKeyToSubKeyConWhere =
                fannelInfoMap.map {
                    val key = SnakeCamelTool.snakeToCamel(it.key)
                    "${key}: ${it.value}"
                }.joinToString(", ")
            return execParse(
                context,
                viewLayoutListSrc,
                plusKeyToSubKeyConWhere,
            )
        }

        fun parseFromList(
            context: Context?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            viewLayoutConList: List<String>,
            whereForLog: String,
        ):  Triple<
                Map<String, String >,
                List<Pair<String, String>>,
                Map<String, List< List<String> > >,
                >?
        {

            val fannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val viewLayoutListSrc = LayoutSettingFile.readFromList(
                context,
                viewLayoutConList,
                fannelName,
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
            return execParse(
                context,
                viewLayoutListSrc,
                whereForLog,
            )
        }

        private fun execParse(
            context: Context?,
            viewLayoutListSrc: List<String>,
            whereForLog: String,
        ): Triple<
                Map<String, String >,
                List<Pair<String, String>>,
                Map<String, List< List<String> > >,
                >?
        {
            val typeSeparator = EditComponent.Template.typeSeparator
            val frameTypeName = EditComponent.Template.LayoutKey.FRAME.key
            val verticalTypeName = EditComponent.Template.LayoutKey.VERTICAL.key
            val horizonTypeName = EditComponent.Template.LayoutKey.HORIZON.key
            val tagKey = EditComponent.Template.EditComponentKey.TAG.key
            var curFrameTag = String()
            var curVerticalTag = String()
            val framePairsConList: MutableList< Pair<String, String > > = mutableListOf()
            val verticalPairsConList: MutableList< Pair<String, String > > = mutableListOf()
            val linearPairConList: MutableList< Pair<String, List<String> > > = mutableListOf()
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
                                ViewLayoutCheck.isTagBlankErr(
                                    context,
                                    it,
                                    whereForLog,
                                    ListSettingsForEditList.ViewLayoutCheck.TagErrGenre.FRAME_TAG
                                ).let {
                                        isTagBlankErr ->
                                    if(
                                        isTagBlankErr
                                    ) return Triple(
                                        emptyMap(),
                                        emptyList(),
                                        emptyMap()
                                    )
                                }
                                ViewLayoutCheck.isVariableUseErr(
                                    context,
                                    it,
                                    whereForLog,
                                    ListSettingsForEditList.ViewLayoutCheck.TagErrGenre.FRAME_TAG
                                ).let {
                                        isTagBlankErr ->
                                    if(
                                        isTagBlankErr
                                    ) return Triple(
                                        emptyMap(),
                                        emptyList(),
                                        emptyMap()
                                    )
                                }
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
                    (layoutKey == verticalTypeName) -> {
                        val verticalLayoutKeyPairListCon =
                            layoutTypePairConList.firstOrNull()
                                ?: String()
                        val tag =
                            CmdClickMap.createMap(
                                verticalLayoutKeyPairListCon,
                                typeSeparator
                            ).firstOrNull {
                                val key = it.first
                                key == tagKey
                            }?.second.let {
                                if(it.isNullOrEmpty()) {
                                    val spanVerticalTag =
                                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                            CheckTool.errRedCode,
                                            "Vertical tag"
                                        )
                                    val spanWhereForLog =
                                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                            CheckTool.errBrown,
                                            whereForLog
                                        )
                                    val errSrcMessage = "${spanVerticalTag} must specify"
                                    val errMessage =
                                        "[View layout] ${errSrcMessage} about ${spanWhereForLog}"
                                    LogSystems.broadErrLog(
                                        context,
                                        Jsoup.parse(errSrcMessage).text(),
                                        errMessage,
                                    )
                                    return Triple(
                                        emptyMap(),
                                        emptyList(),
                                        emptyMap()
                                    )
                                }
                                ViewLayoutCheck.isTagBlankErr(
                                    context,
                                    it,
                                    whereForLog,
                                    ListSettingsForEditList.ViewLayoutCheck.TagErrGenre.VERTICAL_TAG
                                ).let {
                                    isTagBlankErr ->
                                    if(
                                        isTagBlankErr
                                    ) return Triple(
                                        emptyMap(),
                                        emptyList(),
                                        emptyMap()
                                    )
                                }
                                ViewLayoutCheck.isVariableUseErr(
                                    context,
                                    it,
                                    whereForLog,
                                    ListSettingsForEditList.ViewLayoutCheck.TagErrGenre.VERTICAL_TAG
                                ).let {
                                        isTagBlankErr ->
                                    if(
                                        isTagBlankErr
                                    ) return Triple(
                                        emptyMap(),
                                        emptyList(),
                                        emptyMap()
                                    )
                                }
                                QuoteTool.trimBothEdgeQuote(it)
                            }
                        curVerticalTag = EditComponent.Template.TagManager.makeVerticalTag(
                            curFrameTag,
                            tag,
                        )
                        val verticalTagToCon = Pair(
                            curFrameTag,
                            verticalLayoutKeyPairListCon
                        )
                        if (
                            verticalTagToCon.first.isEmpty()
                        ) return@forEachIndexed
                        verticalPairsConList.add(verticalTagToCon)
                    }

                    (layoutKey == horizonTypeName) -> {
                        val frameTagToLinearKeyPairCon = Pair(
                            curVerticalTag,
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_parse_end.txt").absolutePath,
//                listOf(
////                    "layoutKey: ${layoutKey}",
//                    "framePairsConList: ${framePairsConList}",
//                    "linearPairConList: ${linearPairConList}",
//                    "\npair: ${Pair(
//                        framePairsConList.toMap(),
//                        frameTagToLinearPairConListMap,
//                    )}"
//                ).joinToString("\n") + "\n----\n"
//            )
            return Triple(
                framePairsConList.toMap(),
                verticalPairsConList,
                frameTagToLinearPairConListMap,
            )
        }
    }

    object ViewLayoutCheck {

        enum class TagErrGenre(
            val genre: String,
        ) {
            FRAME_TAG("Frame tag"),
            VERTICAL_TAG("Vertical tag")
        }
        fun isTagBlankErr(
            context: Context?,
            tagStr: String?,
            whereForLog: String,
            tagErrGenre: TagErrGenre,
        ): Boolean {
            if(
                !tagStr.isNullOrEmpty()
            ) return false
            val spanVerticalTag =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    tagErrGenre.genre
                )
            val spanWhereForLog =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    whereForLog
                )
            val errSrcMessage = "${spanVerticalTag} must specify"
            val errMessage =
                "[View layout] ${errSrcMessage} about ${spanWhereForLog}"
            LogSystems.broadErrLog(
                context,
                Jsoup.parse(errSrcMessage).text(),
                errMessage,
            )
            return true
        }

        fun isVariableUseErr(
            context: Context?,
            tagStr: String?,
            whereForLog: String,
            tagErrGenre: TagErrGenre,
        ): Boolean {
            if(
                tagStr.isNullOrEmpty()
            ) return false
            val variableRegex = Regex("[$][{][a-zA-Z0-9]+[}]")
            if (
                !variableRegex.matches(tagStr)
            )  return false
            val spanVerticalTag =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    tagErrGenre.genre
                )
            val spanWhereForLog =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    whereForLog
                )
            val errSrcMessage = "${spanVerticalTag} must not use variable"
            val errMessage =
                "[View layout] ${errSrcMessage} about ${spanWhereForLog}"
            LogSystems.broadErrLog(
                context,
                Jsoup.parse(errSrcMessage).text(),
                errMessage,
            )
            return true
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
        return SortByKey.entries.firstOrNull {
            it.key == sortByKeyStr
        }  ?: SortByKey.LAST_UPDATE
    }

    object EditListMaker {

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
        }

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
            ListSettingsForEditList.ListSettingKey.COMP_MAP_LIST_PATH.key
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
                        ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key to fileObj.name,
                        ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key to fileObj.absolutePath
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
        val mapListSeparator = ListSettingsForEditList.MapListPathManager.mapListSeparator
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
                ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
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