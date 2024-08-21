package com.puutaro.commandclick.proccess.list_index_for_edit

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ClickScriptSaver
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecClickUpdate
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.MaxStringLength
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.JsPathHandlerForQrAndListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.CheckItemSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DescSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.FileNameKeyForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File


object ListIndexEditConfig {

    private const val editTargetContents = "\${EDIT_TARGET_CONTENTS}"

    fun handle(
        editFragment: EditFragment,
        isLongClick: Boolean,
        selectedItem: String,
        holder: ListIndexForEditAdapter.ListIndexListViewHolder,
        listIndexPosition: Int
    ){
        val clickConfigListCon = makeClickConfigListStr(
            editFragment,
            isLongClick,
        )
//        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexForEditAdapter.indexListMap,
//            ListIndexForEditAdapter.listIndexTypeKey
//        )
        val fannelInfoMap =
            editFragment.fannelInfoMap
//        val extraRepValMap = mapOf(
//            "ITEM_NAME" to selectedItem,
//            "INDEX_LIST_DIR_PATH" to filterDir,
//            "POSITION" to holder.bindingAdapterPosition.toString()
//        )
//        val setReplaceVariableMap = CmdClickMap.concatRepValMap(
//            editFragment.setReplaceVariableMap,
//            extraRepValMap
//        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "list.txt").absolutePath,
//            listOf(
//                "setReplaceVariableMap: ${editFragment.setReplaceVariableMap}"
//            ).joinToString("\n\n")
//        )
        val mainFannelPath = File(
            UsePath.cmdclickDefaultAppDirPath,
            FannelInfoTool.getCurrentFannelName(fannelInfoMap)
        ).absolutePath
        val jsActionMap = JsActionTool.makeJsActionMap(
            editFragment,
            fannelInfoMap,
            clickConfigListCon,
            editFragment.setReplaceVariableMap,
            mainFannelPath
        )
        val clickConfigPairList = CmdClickMap.createMap(
            clickConfigListCon,
            '|'
        )
        val listIndexArgsMaker = ListIndexArgsMaker(
            editFragment,
            clickConfigPairList,
        )

        ClickScriptSaver.save(
            editFragment,
            listIndexArgsMaker
        )
        ExecClickUpdate.update(
            editFragment,
            listIndexArgsMaker,
            holder,
        )

        JsPathHandlerForQrAndListIndex.handle(
            editFragment,
            jsActionMap,
            selectedItem,
            listIndexPosition,
        )
    }

    fun getConfigKeyMap(
        listIndexConfigMap: Map<String, String>?,
        configKey: String,
    ): Map<String, String> {

        return listIndexConfigMap?.get(
            configKey
        ).let{
            CmdClickMap.createMap(
                it,
                '|'
            )
        }.toMap()
    }

    private fun makeClickConfigListStr(
        editFragment: EditFragment,
        isLongClick: Boolean,
    ): String? {
        val buttonClickMapKey = when(
            isLongClick
        ){
            true -> ListIndexConfigKey.LONG_CLICK.key
            else -> ListIndexConfigKey.CLICK.key

        }
        return editFragment.listIndexConfigMap
            ?.get(buttonClickMapKey)
    }

    fun getListIndexType(
        editFragment: EditFragment,
    ): TypeSettingsForListIndex.ListIndexTypeKey {
        val listIndexTypeStr = editFragment.listIndexConfigMap
            ?.get(ListIndexConfigKey.TYPE.key)
        return TypeSettingsForListIndex.ListIndexTypeKey.values().firstOrNull {
            it.key == listIndexTypeStr
        } ?: TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
    }

    fun setCheckToMaterialCardView(
        materialCardView: MaterialCardView,
        checkItemConfigMap: Map<String, String>,
        listIndexList: MutableList<String>,
        position: Int,
    ){
        val context = materialCardView.context
            ?: return
        val defaultType = CheckItemSettingsForListIndex.CheckType.NO
        val getCheckType = checkItemConfigMap.get(
            CheckItemSettingsForListIndex.CheckItemSettingKey.TYPE.key
        ).let {
            typeName ->
            if(
                typeName.isNullOrEmpty()
            ) return@let defaultType
            CheckItemSettingsForListIndex.CheckType.values().firstOrNull {
                it.type == typeName
            } ?: defaultType
        }
        when(getCheckType){
            CheckItemSettingsForListIndex.CheckType.LAST -> {
                val lastIndex = listIndexList.size - 1
                if(
                    position != lastIndex
                ) {
                    materialCardView.isChecked = false
                    return
                }
            }
            CheckItemSettingsForListIndex.CheckType.NO ->
                return
        }
        val defaultIconId = R.drawable.icons8_check_ok
        val iconId = checkItemConfigMap.get(
            CheckItemSettingsForListIndex.CheckItemSettingKey.ICON.key
        ).let {
            fetchIconName ->
            if(
                fetchIconName.isNullOrEmpty()
            ) return@let defaultIconId
            CmdClickIcons.values().firstOrNull {
                it.str == fetchIconName
            }?.id ?:defaultIconId
        }
        materialCardView.checkedIcon = AppCompatResources.getDrawable(context, iconId)
        materialCardView.isChecked = true
    }

    fun makeFileNameText(
        listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
        fileNameTextView: AppCompatTextView?,
        fileName: String,
        listIndexConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
    ): String? {
        val fileNameConfigMap = CmdClickMap.createMap(
            listIndexConfigMap?.get(
                ListIndexConfigKey.NAME.key
            ),
            '|'
        ).toMap()
        val isHide = fileNameConfigMap.containsKey(
            FileNameKeyForListIndex.ListIndexFileNameKey.ON_HIDE.key
        )
        if (isHide) {
            fileNameTextView?.isVisible = false
            return null
        }
        val fileNameBeforeCut = makeFileName(
            listIndexTypeKey,
            fileName,
            fileNameConfigMap,
            busyboxExecutor
        )
        return MaxStringLength.cut(
            fileNameBeforeCut,
            100,
            fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.LENGTH.key)
        )
//        fileNameTextView?.text = MaxStringLength.cut(
//            fileNameBeforeCut,
//        100,
//                fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.LENGTH.key)
//        )
    }

    private fun makeFileName(
        listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
        fileNameSrc: String,
        fileNameConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
    ): String {
        return when(listIndexTypeKey){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> fileNameSrc
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> {
                makeFannelName(
                    fileNameSrc.split("\t").firstOrNull()
                        ?: String(),
                    fileNameConfigMap,
                    busyboxExecutor,
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> makeFannelName(
                fileNameSrc,
                fileNameConfigMap,
                busyboxExecutor,
            )
        }
    }

    private fun makeFannelName(
        fileNameSrc: String,
        fileNameConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
    ): String {
        if (
            fileNameConfigMap.isNullOrEmpty()
        ) return fileNameSrc
        val removedExtendFileName =
            fileNameConfigMap.containsKey(FileNameKeyForListIndex.ListIndexFileNameKey.REMOVE_EXTEND.key).let {
                if (!it) return@let fileNameSrc
                CcPathTool.trimAllExtend(fileNameSrc)
            }
        val compPrefixedFileName =
            fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.COMP_PREFIX.key).let {
                if (
                    it.isNullOrEmpty()
                ) return@let removedExtendFileName
                UsePath.compPrefix(fileNameSrc, it)
            }
        val compSuffixedFileName =
            fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.COMP_SUFFIX.key).let {
                if (
                    it.isNullOrEmpty()
                ) return@let compPrefixedFileName
                UsePath.compExtend(fileNameSrc, it)
            }
        if(
            busyboxExecutor == null
        ) return compSuffixedFileName
        return fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.SHELL_PATH.key).let {
            if (
                it.isNullOrEmpty()
            ) return@let compSuffixedFileName
            val shellPathObj = File(it)
            if (
                !shellPathObj.isFile
            ) return@let compPrefixedFileName
            val shellCon = ReadText(
                shellPathObj.absolutePath
            ).readText().replace(
                editTargetContents,
                compSuffixedFileName,
            )
            return@let busyboxExecutor.getCmdOutput(
                shellCon,
            )
        }
    }

    class MakeFileDescArgsMaker(
        val parentDirPath: String,
        val fileNameOrInstallFannelLine: String,
        val fileCon: String,
        val listIndexConfigMap: Map<String, String>?,
        val busyboxExecutor: BusyboxExecutor?
    )
    fun makeFileDesc(
        makeFileDescArgsMaker: MakeFileDescArgsMaker
    ): String? {
        return when(ListIndexForEditAdapter.listIndexTypeKey){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL ->
                FannelListVariable.getDesc(
                    makeFileDescArgsMaker.fileNameOrInstallFannelLine
                )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                makeDescCon(
                    makeFileDescArgsMaker,
                    makeFileDescArgsMaker
                        .fileNameOrInstallFannelLine
                        .split("\t")
                        .lastOrNull() ?: String()
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL -> {
                makeDescCon(
                    makeFileDescArgsMaker,
                    makeFileDescArgsMaker.fileCon
                )
            }
        }
    }

    private fun makeDescCon(
        makeFileDescArgsMaker: MakeFileDescArgsMaker,
        srcCon: String,
    ): String? {
        val defaultMaxTakeLength = 50
        val defaultTakeFileCon = srcCon.take(defaultMaxTakeLength)
        val listIndexConfigMap = makeFileDescArgsMaker.listIndexConfigMap
        if (
            listIndexConfigMap.isNullOrEmpty()
        ) return null
        val descValue = listIndexConfigMap.get(ListIndexConfigKey.DESC.key)
        if (
            descValue.isNullOrEmpty()
        ) return null
        val descConfigMap = CmdClickMap.createMap(
            descValue,
            '|'
        ).toMap()
        val descConByCut = descConfigMap.get(DescSettingsForListIndex.ListIndexDescKey.LENGTH.key).let {
            MaxStringLength.cut(
                srcCon,
                defaultMaxTakeLength,
                it
            )
        }
        val busyboxExecutor = makeFileDescArgsMaker.busyboxExecutor
            ?: return null
        return descConfigMap.get(
            DescSettingsForListIndex.ListIndexDescKey.SHELL_PATH.key
        ).let {
            if (
                it.isNullOrEmpty()
            ) return@let defaultTakeFileCon
            val shellPathObj = File(it)
            if (
                !shellPathObj.isFile
            ) return@let defaultTakeFileCon
            val shellCon = ReadText(
                shellPathObj.absolutePath
            ).readText().replace(
                editTargetContents,
                descConByCut,
            )
            return@let busyboxExecutor.getCmdOutput(
                shellCon,
            )
        }
    }

    enum class ListIndexConfigKey(
        val key: String,
    ) {
        TYPE("type"),
        LAYOUT("layout"),
        NAME("name"),
        DESC("desc"),
        CHECK_ITEM("checkItem"),
        CLICK("click"),
        LONG_CLICK("longClick"),
        LIST("list"),
        SEARCH_BOX("searchBox"),
        PERFORM("perform"),
        DELETE("delete"),
    }
}
