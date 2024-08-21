package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.ShellTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object ListSettingsForListIndex  {
    enum class ListSettingKey(
        val key: String
    ) {
        LIST_DIR("listDir"),
        PREFIX("prefix"),
        SUFFIX("suffix"),
        FILTER_SHELL_PATH("filterShellPath"),
        EDIT_BY_DRAG("editByDrag"),
        SORT_TYPE("sortType"),
        COMP_PATH("compPath"),
        ON_REVERSE_LAYOUT("onReverseLayout"),
        ON_ONLY_EXIST_PATH("onOnlyExistPath"),
    }

    enum class stackFromBottomValue {
        ON
    }

    enum class EditByDragKey(
        val key: String,
    ){
        EDIT_BY_DRAG_DISABLE("editByDragDisable"),
    }

    enum class DisableValue {
        ON
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
        editFragment: EditFragment,
        indexListMap: Map<String, String>,
        tsvConList: List<String>
    ): List<String> {
        val isOnlyExistPath = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingKey.ON_ONLY_EXIST_PATH.key
        ) == OnOnlyExistPath.ON.name
        if(
            !isOnlyExistPath
        ) return tsvConList
        return tsvConList.filter {
            val titleAndPathList = it.split("\t")
            val path = titleAndPathList.lastOrNull()
                ?: return@filter false
            val pathObj = File(path)
            pathObj.isFile
                    || pathObj.isDirectory
        }
    }
    fun howDisableEditByDrag(
        editFragment: EditFragment,
        editByDragMap: Map<String, String>
    ): Boolean {
        return FilePrefixGetter.get(
            editFragment,
            editByDragMap,
            EditByDragKey.EDIT_BY_DRAG_DISABLE.key
        ) == DisableValue.ON.name
    }

    fun howReverseLayout(
        editFragment: EditFragment,
        indexListMap: Map<String, String>?,
    ): Boolean {
        return FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingKey.ON_REVERSE_LAYOUT.key
        ) == stackFromBottomValue.ON.name
    }

    fun getSortType(
        editFragment: EditFragment,
        indexListMap: Map<String, String>?,
    ): SortByKey {
        val sortByKeyStr = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingKey.SORT_TYPE.key
        )
        return SortByKey.values().firstOrNull {
            it.key == sortByKeyStr
        }  ?: SortByKey.LAST_UPDATE
    }

    fun makeEditByDragMap(
        listIndexConfigMap: Map<String, String>?,
    ): Map<String, String> {
        val listConfigMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LIST.key
        )
        return execMakeEditByDragMap(
            listConfigMap,
        )
    }

    private fun execMakeEditByDragMap(
        listConfigMap: Map<String, String>?,
    ): Map<String, String> {

        return listConfigMap?.get(
            ListSettingKey.EDIT_BY_DRAG.key
        ).let{
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
    }

    object ListIndexListMaker {

        private const val throughMark = "-"
        private const val blankListMark = "Let's press sync button at right bellow"
        private const val itemNameMark = "\${ITEM_NAME}"

        fun makeFileListHandler(
            editFragment: EditFragment,
            indexListMap: Map<String, String>,
            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey
        ): MutableList<String> {
            val busyboxExecutor = editFragment.busyboxExecutor
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "list.txt").absolutePath,
//                listOf(
//                    "tag: ${editFragment.tag}",
//                    "listIndexConfigMap: ${editFragment.listIndexConfigMap}",
//                    "indexListMap: ${indexListMap}",
//                    "listIndexTypeKey: ${listIndexTypeKey.key}",
//                ).joinToString("\n\n")
//            )
            return when(listIndexTypeKey) {
                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
                -> makeFannelListForListView().toMutableList()
                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
                    makeTsvConList(
                        editFragment,
                        indexListMap,
                        busyboxExecutor,
                    )
                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
                -> makeFileList(
                    editFragment,
                    indexListMap,
                    listIndexTypeKey,
                )
            }
        }

        fun getFilterDir(
            editFragment: EditFragment,
            indexListMap: Map<String, String>?,
            listIndexType : TypeSettingsForListIndex.ListIndexTypeKey,
        ): String {
            return when(listIndexType){
                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
                -> UsePath.cmdclickFannelItselfDirPath
                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
                -> String()
                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL ->
                    FilePrefixGetter.get(
                        editFragment,
                        indexListMap,
                        ListSettingKey.LIST_DIR.key,
                    ) ?: String()
            }
        }

        private fun makeFileList(
            editFragment: EditFragment,
            indexListMap: Map<String, String>,
            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey
        ): MutableList<String> {
            val busyboxExecutor = editFragment.busyboxExecutor
            val filterDir = getFilterDir(
                editFragment,
                indexListMap,
                listIndexTypeKey
            )
            FileSystems.createDirs(filterDir)
            val filterPrefixListCon = FilePrefixGetter.get(
                editFragment,
                indexListMap,
                ListSettingKey.PREFIX.key
            ) ?: String()
            val filterSuffixListCon = FilePrefixGetter.get(
                editFragment,
                indexListMap,
                ListSettingKey.SUFFIX.key
            ) ?: String()
            val currentFileList = FileSystems.sortedFiles(
                filterDir,
                "on"
            ).let {
                CompPathManager.concatByCompConWhenNormal(
                    editFragment,
                    indexListMap,
                    filterDir,
                    it
                )
            }
            val filterShellCon = getFilterShellCon(
                editFragment,
                indexListMap,
            )
            val fileListSource = makeFileListElement(
                currentFileList,
                busyboxExecutor,
                filterDir,
                filterPrefixListCon,
                filterSuffixListCon,
                filterShellCon,
            )
            if(
                fileListSource.isEmpty()
            ) return mutableListOf(throughMark)
            val sortType = getSortType(
                editFragment,
                indexListMap
            )
            return sortList(
                sortType,
                fileListSource,
            )
        }

        fun makeFileListElement(
            fileList: List<String>,
            busyboxExecutor: BusyboxExecutor?,
            filterDir: String,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            filterShellCon: String,
        ): List<String> {
            return fileList.filter {
                FilterPathTool.isFilterByFile(
                    it,
                    filterDir,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    false,
                    "&"
                )
            }.map {
                makeFilterShellCon(
                    it,
                    busyboxExecutor,
                    filterShellCon,
                )
            }.filter {
                it.isNotEmpty()
            }
        }

        private fun makeTsvConList(
            editFragment: EditFragment,
            indexListMap: Map<String, String>,
            busyboxExecutor: BusyboxExecutor?,
        ): MutableList<String> {
            val filterShellCon = getFilterShellCon(
                editFragment,
                indexListMap,
            )
            val tsvFilePath = FilePrefixGetter.get(
                editFragment,
                indexListMap,
                ListSettingKey.LIST_DIR.key,
            ) ?: String()
            val tsvFilePathObj = File(tsvFilePath)
            val tsvConListSrc = ReadText(
                tsvFilePath
            ).textToList().let {
                TsvTool.filterByColumnNum(
                    it,
                    2
                )
            }.map {
                val titleAndConList = it.split("\t")
                val con = titleAndConList.last()
                val title = titleAndConList.first().let {
                    makeFilterShellCon(
                        it,
                        busyboxExecutor,
                        filterShellCon,
                    )
                }
                "${title}\t${con}"
            }.let {
                howExistPathForTsv(
                    editFragment,
                    indexListMap,
                    it,
                )
            }
            val tsvConList = CompPathManager.concatByCompConWhenTsvEdit(
                editFragment,
                indexListMap,
                tsvConListSrc
            )
            val sortType = getSortType(
                editFragment,
                indexListMap
            )
            val sortedTsvConList = sortList(
                sortType,
                tsvConList,
            )
            updateTsv(
                sortType,
                tsvFilePathObj,
                sortedTsvConList,
            )
            return sortedTsvConList
        }

        fun sortList(
            sortType: SortByKey,
            tsvConList: List<String>,
        ): MutableList<String> {
            return when(sortType){
                SortByKey.LAST_UPDATE -> {
                    tsvConList
                        .reversed()
                        .toMutableList()
                }
                SortByKey.SORT -> {
                    tsvConList.sorted().reversed()
                        .toMutableList()
                }
                SortByKey.REVERSE -> {
                    tsvConList.sorted()
                        .toMutableList()
                }
            }
        }

        private fun updateTsv(
            sortType: SortByKey,
            tsvFilePathObj: File,
            sortedTsvConList: List<String>,
        ){
            val saveSortedTsvConList = sortListForTsvSave(
                sortType,
                sortedTsvConList,
            )
            TsvTool.updateTsv(
                tsvFilePathObj.absolutePath,
                saveSortedTsvConList
            )
        }
        fun sortListForTsvSave(
            sortType: SortByKey,
            tsvConList: List<String>,
        ): MutableList<String> {
            return when(sortType){
                SortByKey.LAST_UPDATE -> {
                    tsvConList
                        .reversed()
                        .toMutableList()
                }
                SortByKey.SORT -> {
                    tsvConList.sorted()
                        .toMutableList()
                }
                SortByKey.REVERSE -> {
                    tsvConList.sorted().reversed()
                        .toMutableList()
                }
            }
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
            editFragment: EditFragment,
            indexListMap: Map<String, String>?,
        ): String {
            val shellPath = FilePrefixGetter.get(
                editFragment,
                indexListMap,
                ListSettingKey.FILTER_SHELL_PATH.key,
            ) ?: String()
            return execGetFilterShellCon(
                editFragment,
                shellPath,
            )
        }

        private fun execGetFilterShellCon(
            editFragment: EditFragment,
            filterShellPath: String,
        ): String {
            if(
                filterShellPath.isEmpty()
            ) return String()
            val fannelInfoMap = editFragment.fannelInfoMap
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
                    editFragment.setReplaceVariableMap,
//                    currentAppDirPath,
                    currentFannelName
                )
            }
        }
    }


}

private object CompPathManager {

    fun concatByCompConWhenNormal(
        editFragment: EditFragment,
        indexListMap: Map<String, String>,
        parentDirPath: String,
        fileList: List<String>
    ): List<String> {
        val initFilePath = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.COMP_PATH.key
        )
        if(
            initFilePath.isNullOrEmpty()
        ) return fileList
        val initFilePathObj = File(initFilePath)
        val initConList = when(true){
            initFilePathObj.isFile ->
                makeInitConFromFile(
                    editFragment,
                    initFilePathObj,
                )
            initFilePathObj.isDirectory -> {
                val initFileConSrcDir = initFilePathObj.absolutePath
                FileSystems.sortedFiles(
                    initFileConSrcDir,
                    "on"
                )
            }
            else -> return fileList
        }.let {
            TsvTool.filterByColumnNum(
                it,
                1
            )
        }
        return concatInitConAndConList(
            fileList,
            initConList,
            parentDirPath,
        )
    }
    fun concatByCompConWhenTsvEdit(
        editFragment: EditFragment,
        indexListMap: Map<String, String>,
        tsvConList: List<String>
    ): List<String> {
//        if(
//            tsvConList.isNotEmpty()
//        ) return tsvConList
        val initTsvPath = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.COMP_PATH.key
        )
        if(
            initTsvPath.isNullOrEmpty()
        ) return tsvConList
        val initTsvPathObj = File(initTsvPath)
        val initTsvConList = when(true){
            initTsvPathObj.isFile ->
                makeInitConFromFile(
                    editFragment,
                    initTsvPathObj,
                )
            initTsvPathObj.isDirectory -> {
                val initTsvConSrcDir = initTsvPathObj.absolutePath
                FileSystems.sortedFiles(
                    initTsvConSrcDir
                ).map {
                    "${it}\t${File(initTsvConSrcDir, it).absolutePath}"
                }
            }
            else -> return tsvConList
        }.let {
            TsvTool.filterByColumnNum(
                it,
                2
            )
        }
        return concatInitConAndConList(
            tsvConList,
            initTsvConList,
        )
    }

    private fun makeInitConFromFile(
        editFragment: EditFragment,
        initTsvPathObj: File,
    ): List<String> {
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        return ReadText(
            initTsvPathObj.absolutePath
        ).readText().let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName,
            ).split("\n")
        }
    }

    private fun concatInitConAndConList(
        conList: List<String>,
        initConList: List<String>,
        parentDirPath: String,
    ): List<String> {
        val concatConList = concatInitConAndConList(
            conList,
            initConList,
        )
        val insertInitConList = concatConList.filter {
            !conList.contains(it)
        }
        insertInitConList.forEach {
            val insertFilePath = File(
                parentDirPath,
                it
            ).absolutePath
            FileSystems.writeFile(
                insertFilePath,
                String()
            )
        }
        return concatConList
    }

    private fun concatInitConAndConList(
        conList: List<String>,
        initConList: List<String>,
    ): List<String> {
        if(
            initConList.isEmpty()
        ) return conList
        val insertInitTsvConList = initConList.filter {
            !conList.contains(it)
        }
        if(
            insertInitTsvConList.isEmpty()
        ) return conList
        return insertInitTsvConList + conList
    }
}