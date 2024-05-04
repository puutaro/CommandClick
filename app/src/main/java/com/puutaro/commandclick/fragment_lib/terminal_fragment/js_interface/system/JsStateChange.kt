package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

class JsStateChange(
    private val terminalFragment: TerminalFragment
) {

    private val extraMapSeparator = '|'

    @JavascriptInterface
    fun change(
        stateName: String,
        extraMapCon: String,
    ){
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            extraMapSeparator,
        ).toMap()
        val onListDirUpdaterKey = ExtraMapKey.ON_LIST_DIR_UPDATER.key
        val listDirTsvPathKey = ExtraMapKey.LIST_DIR_TSV_PATH.key
        val listDirValueKey = ExtraMapKey.LIST_DIR_VALUE.key
        ListDirTsv.update(
            extraMap,
            onListDirUpdaterKey,
            listDirTsvPathKey,
            listDirValueKey,
        )

        val onInfoSaveKey = ExtraMapKey.ON_INFO_SAVE.key
        val saveInfoPathKey = ExtraMapKey.SAVE_INFO_PATH.key
        val extraSaveInfoKey = ExtraMapKey.EXTRA_SAVE_INFO.key
        InfoSaver.save(
            extraMap,
            onInfoSaveKey,
            saveInfoPathKey,
            listDirTsvPathKey,
            extraSaveInfoKey
        )

        val enableAddToBackStackValue = extraMap.get(
            ExtraMapKey.ENABLE_ADD_TO_BACKSTACK.key
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsState.txt").absolutePath,
//            listOf(
//                "extraMap: ${extraMap}",
//                "key: ${ExtraMapKey.ENABLE_ADD_TO_BACKSTACK.key}",
//                "enableAddToBackStackValue: ${enableAddToBackStackValue}",
//            ).joinToString("\n\n")
//        )
        StageChanger.change(
            terminalFragment,
            stateName,
            enableAddToBackStackValue,
        )
    }

    private object ListDirTsv {

        private const val onListDirUpdaterOn = "ON"

        fun update(
            extraMap: Map<String, String>,
            onListDirUpdaterKey: String,
            listDirTsvPathKey: String,
            listDirValueKey: String,
        ){
            val offListDirUpdater = extraMap.get(
                onListDirUpdaterKey
            ) != onListDirUpdaterOn
            if(
                offListDirUpdater
            ) return
            val listDirTsvPath =
                FilePathGetter.get(
                    extraMap,
                    listDirTsvPathKey
                ) ?: return
            val listDirValue = getListDirValue(
                extraMap,
                listDirValueKey,
            ) ?: return
            val listDirKey =
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key
            TsvTool.updateTsvByKey(
                listDirTsvPath,
                listOf("${listDirKey}\t${listDirValue}")
            )
        }

        private fun getListDirValue(
            extraMap: Map<String, String>,
            listDirValueKey: String,
        ): String? {
            val listDirOrTsvPath = extraMap.get(
                listDirValueKey
            )
            if(
                listDirOrTsvPath.isNullOrEmpty()
            ) {
                ToastUtils.showShort(
                    "${listDirValueKey} must be specify"
                )
                return null
            }
            return listDirOrTsvPath
        }
    }

    private object InfoSaver {

        private const val onInfoSaveOn = "ON"

        fun save(
            extraMap: Map<String, String>,
            onInfoSaveKey: String,
            saveInfoPathKey: String,
            listDirTsvPathKey: String,
            extraSaveInfoKey: String,
        ){
            val offInfoSave =  extraMap.get(
                onInfoSaveKey
            ) != onInfoSaveOn
            if(
                offInfoSave
            ) return

            val saveInfoPath =
                FilePathGetter.get(
                    extraMap,
                    saveInfoPathKey
                ) ?: return
            val saveInfo = makeSaveInfo(
                extraMap,
                listDirTsvPathKey,
                extraSaveInfoKey,
            )
                ?: return
            FileSystems.writeFile(
                saveInfoPath,
                saveInfo,
            )
        }

        private fun makeSaveInfo(
            extraMap: Map<String, String>,
            listDirTsvPathKey: String,
            extraSaveInfoKey: String,
        ): String? {
            val listDirTsvPath =
                FilePathGetter.get(
                    extraMap,
                    listDirTsvPathKey
                ) ?: return null
            val listDirKey =
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key
            val saveListDirPath = TsvTool.getKeyValue(
                listDirTsvPath,
                listDirKey,
            )
            val saveListDirName = File(saveListDirPath).name
            val saveListDirRawName =
                CcPathTool.trimAllExtend(saveListDirName)
            val extraSaveInfo = extraMap.get(
                extraSaveInfoKey
            ) ?: return saveListDirRawName
            return "${saveListDirRawName} ${extraSaveInfo}"
        }
    }

    private object StageChanger {

        private const val enableAddToBackStackOn = "ON"

        fun change(
            terminalFragment: TerminalFragment,
            stateName: String,
            enableAddToBackStackValue: String?,
        ){
            val disableAddToBackStack =
                enableAddToBackStackValue != enableAddToBackStackOn
            JsCmdValFrag(terminalFragment).stateChange_S(
                stateName,
                disableAddToBackStack
            )
        }
    }
}

private object FilePathGetter {
    fun get(
        extraMap: Map<String, String>,
        targetKey: String,
    ): String? {
        val filePath = extraMap.get(
            targetKey
        )
        if(
            filePath.isNullOrEmpty()
        ) {
            ToastUtils.showShort(
                "${targetKey} must be specify"
            )
            return null
        }
        return filePath
    }
}
private enum class ExtraMapKey(
    val key: String
){
    ON_LIST_DIR_UPDATER("onListDirUpdater"),
    LIST_DIR_TSV_PATH("listDirTsvPath"),
    ON_INFO_SAVE("onInfoSave"),
    SAVE_INFO_PATH("saveInfoPath"),
    EXTRA_SAVE_INFO("extraSaveInfo"),
    ENABLE_ADD_TO_BACKSTACK("enableAddToBackStack"),
    LIST_DIR_VALUE("listDirValue"),
}