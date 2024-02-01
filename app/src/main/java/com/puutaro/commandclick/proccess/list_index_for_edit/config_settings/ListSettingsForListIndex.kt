package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePreferenceMethod
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
        ON_DELETE_CON_FILE("onDeleteConFile"),
    }

    enum class EditByDragKey(
        val key: String,
    ){
        DISABLE("disable"),
        DISABLE_DELETE_CONFIRM("disableDeleteConfirm"),
    }

    enum class DisableValue {
        OFF
    }

    enum class DisableDeleteConfirm {
        OFF
    }

    enum class OnDeleteConFileValue {
        OFF
    }

    enum class SortByKey(
        val key: String
    ) {
        LAST_UPDATE("lastUpdate"),
        SORT("sort"),
        REVERSE("reverse"),

    }


    fun howDisableEditByDrag(
        editByDragMap: Map<String, String>
    ): Boolean {
        return editByDragMap.get(
            EditByDragKey.DISABLE.key
        ) == DisableValue.OFF.name
    }

    fun howDisableDeleteConfirm(
        editByDragMap: Map<String, String>
    ): Boolean {
        return editByDragMap.get(
            EditByDragKey.DISABLE_DELETE_CONFIRM.key
        ) == DisableDeleteConfirm.OFF.name
    }

    fun getSortType(
        indexListMap: Map<String, String>?,
    ): SortByKey {
        return indexListMap?.get(
            ListSettingKey.SORT_TYPE.key
        ).let {
            sortKey ->
            SortByKey.values().firstOrNull {
                it.key == sortKey
            } ?: SortByKey.LAST_UPDATE
        }
    }

    fun howOnDeleteConFileValue(
        indexListMap: Map<String, String>?,
    ): Boolean {
        return indexListMap?.get(
            ListSettingKey.ON_DELETE_CON_FILE.key
        ) != OnDeleteConFileValue.OFF.name
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

    fun getListSettingKeyHandler(
        editFragment: EditFragment,
        indexListMap: Map<String, String>?,
        listKeyName: String,
    ): String {
        val filePrefix = EditSettings.filePrefix
        val readSharePreffernceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val listDirGetValue = indexListMap?.get(listKeyName)
        if(
            listDirGetValue.isNullOrEmpty()
        ) return String()
        val replaceListDirValue = SetReplaceVariabler.execReplaceByReplaceVariables(
            listDirGetValue,
            setReplaceVariableMap,
            currentAppDirPath,
            currentFannelName,
        )
        val listDirValue = QuoteTool.trimBothEdgeQuote(replaceListDirValue)
        val isFileSpecify = listDirValue.startsWith(filePrefix)
        return when(isFileSpecify){
            false -> replaceListDirValue
            else -> {
                val listDirFilePath = replaceListDirValue.removePrefix(filePrefix)
                val listDirFilePathObj = File(listDirFilePath)
                val listDirFileParentDirPath = listDirFilePathObj.parent
                    ?: return String()
                val listDirFileName = listDirFilePathObj.name
                ReadText(
                    listDirFileParentDirPath,
                    listDirFileName,
                ).readText().let {
                    val listSettingKeyMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                        it,
                        setReplaceVariableMap,
                        currentAppDirPath,
                        currentFannelName,
                    ).replace(
                        "\t",
                        "=",
                    )
                    CmdClickMap.createMap(
                        listSettingKeyMapCon,
                        "\n"
                    ).toMap().get(
                        listKeyName
                    )
                } ?: String()
            }
        }
    }
    private fun execMakeEditByDragMap(
        listConfigMap: Map<String, String>?,
    ): Map<String, String> {

        return listConfigMap?.get(
            ListSettingKey.EDIT_BY_DRAG.key
        ).let{
            CmdClickMap.createMap(
                it,
                "!"
            )
        }.toMap()
    }

    object ListIndexListMaker {

        private const val throughMark = "-"
        private const val noExtend = "NoExtend"
        private const val subMenuSeparator = "&"
        private const val blankListMark = "Let's press sync button at right bellow"
        private const val itemNameMark = "\${ITEM_NAME}"

        fun makeFileListHandler(
            editFragment: EditFragment,
            indexListMap: Map<String, String>,
            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey
        ): MutableList<String> {
            val busyboxExecutor = editFragment.busyboxExecutor
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

        private fun judgeBySuffixForIndex(
            targetStr: String,
            filterSuffix: String,
        ): Boolean {
            if(filterSuffix != noExtend) {
                return filterSuffix.split(subMenuSeparator).any {
                    targetStr.endsWith(it)
                }
            }
            return !Regex("\\..*$").containsMatchIn(targetStr)
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
                    getListSettingKeyHandler(
                        editFragment,
                        indexListMap,
                        ListSettingKey.LIST_DIR.key,
                    )
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
            val filterPrefix = getListSettingKeyHandler(
                editFragment,
                indexListMap,
                ListSettingKey.PREFIX.key
            )
            val filterSuffix = getListSettingKeyHandler(
                editFragment,
                indexListMap,
                ListSettingKey.SUFFIX.key
            )
            val filterShellCon = getFilterShellCon(
                editFragment,
                indexListMap,
            )
            val fileListSource = makeFileListElement(
                FileSystems.sortedFiles(
                    filterDir,
                    "on"
                ),
                busyboxExecutor,
                filterDir,
                filterPrefix,
                filterSuffix,
                filterShellCon,
            )
            if(
                fileListSource.isEmpty()
            ) return mutableListOf(throughMark)
            val sortType = getSortType(indexListMap)
            return sortList(
                sortType,
                fileListSource,
            )
        }

        fun makeFileListElement(
            fileList: List<String>,
            busyboxExecutor: BusyboxExecutor?,
            filterDir: String,
            filterPrefix: String,
            filterSuffix: String,
            filterShellCon: String,
        ): List<String> {
            return fileList.filter {
                it.startsWith(filterPrefix)
                        && judgeBySuffixForIndex(
                    it,
                    filterSuffix
                ) && File("${filterDir}/$it").isFile
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
            val tsvFilePath = getListSettingKeyHandler(
                editFragment,
                indexListMap,
                ListSettingKey.LIST_DIR.key,
            )
            val tsvFilePathObj = File(tsvFilePath)
            val tsvParentDirPath = tsvFilePathObj.parent
                ?: return mutableListOf()
            val tsvName = tsvFilePathObj.name
            val tsvConList = ReadText(
                tsvParentDirPath,
                tsvName
            ).textToList().filter {
                it.split("\t").size == 2
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
            }
            val sortType = getSortType(indexListMap)
            return sortList(
                sortType,
                tsvConList,
            )
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
                    tsvConList.sorted().reversed()
                        .toMutableList()
                }
                SortByKey.REVERSE -> {
                    tsvConList.sorted()
                        .toMutableList()
                }
            }
        }

        private fun makeFilterShellCon(
            srcCon: String,
            busyboxExecutor: BusyboxExecutor?,
            filterShellCon: String,
        ): String {
            if(
                filterShellCon.isEmpty()
                || busyboxExecutor == null
            ) return srcCon
            return busyboxExecutor.getCmdOutput(
                filterShellCon.replace(
                    itemNameMark,
                    srcCon,
                )
            )
        }

        private fun makeFannelListForListView(): List<String> {
            val fannelListSource = ReadText(
                UsePath.cmdclickFannelListDirPath,
                UsePath.fannelListMemoryName,
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
            val shellPath = getListSettingKeyHandler(
                editFragment,
                indexListMap,
                ListSettingKey.FILTER_SHELL_PATH.key,
            )
            return execGetFilterShellCon(
                editFragment,
                shellPath,
            )
        }

        private fun execGetFilterShellCon(
            editFragment: EditFragment,
            shellPath: String,
        ): String {
            if(
                shellPath.isEmpty()
            ) return String()
            val readSharePreferenceMap = editFragment.readSharePreferenceMap
            val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreferenceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreferenceMap,
                SharePrefferenceSetting.current_fannel_name
            )
            val filterShellPathObj = File(shellPath)
            val shellParentDirPath = filterShellPathObj.parent
                ?: return String()
            return ReadText(
                shellParentDirPath,
                filterShellPathObj.name
            ).readText().let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    editFragment.setReplaceVariableMap,
                    currentAppDirPath,
                    currentFannelName
                )
            }
        }
    }
}