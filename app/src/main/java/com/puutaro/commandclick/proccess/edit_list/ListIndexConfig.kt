package com.puutaro.commandclick.proccess.edit_list

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.MaxStringLength
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.JsPathHandlerForQrAndEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.CheckItemSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.DescSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.FileNameKeyForEditList
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File


object EditListConfig {

    private const val editTargetContents = "\${EDIT_TARGET_CONTENTS}"

    fun handle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        selectedItemLineMap: Map<String, String>,
        jsAcCon: String,
        editListPosition: Int
    ){
//        val clickConfigListCon = makeClickConfigListStr(
//            editFragment,
//            isLongClick,
//        )
//        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexForEditAdapter.indexListMap,
//            ListIndexForEditAdapter.listIndexTypeKey
//        )
//        val fannelInfoMap =
//            fragment.fannelInfoMap
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
            fragment,
            fannelInfoMap,
            jsAcCon,
            setReplaceVariableMap,
            mainFannelPath
        )
//        val clickConfigPairList = CmdClickMap.createMap(
//            jsAcCon,
//            '|'
//        )
//        val listIndexArgsMaker = ListIndexArgsMaker(
//            fannelInfoMap,
//            setReplaceVariableMap,
//            clickConfigPairList,
//        )

//        ClickScriptSaver.save(
//            listIndexArgsMaker
//        )
//        val editComponentListAdapter = editListRecyclerView.adapter as EditComponentListAdapter
//        ExecClickUpdate.update(
//            fragment,
//            fannelInfoMap,
//            setReplaceVariableMap,
//            editComponentListAdapter,
//            listIndexArgsMaker,
//            listIndexPosition,
//        )
        JsPathHandlerForQrAndEditList.handle(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView,
            jsActionMap,
            selectedItemLineMap,
            editListPosition,
        )
    }

    fun getConfigKeyMap(
        editListConfigMap: Map<String, String>?,
        configKey: String,
    ): Map<String, String> {

        return getConfigKeyConList(
            editListConfigMap,
            configKey,
        ).toMap()
    }

    fun getConfigKeyConList(
        editListConfigMap: Map<String, String>?,
        configKey: String,
    ): List<Pair<String, String>> {

        return editListConfigMap?.get(
            configKey
        ).let{
            CmdClickMap.createMap(
                it,
                '|'
            )
        }
    }

//    private fun makeClickConfigListStr(
//        editFragment: EditFragment,
//        isLongClick: Boolean,
//    ): String? {
//        val buttonClickMapKey = when(
//            isLongClick
//        ){
//            true -> ListIndexConfigKey.LONG_CLICK.key
//            else -> ListIndexConfigKey.CLICK.key
//
//        }
//        return editFragment.listIndexConfigMap
//            ?.get(buttonClickMapKey)
//    }

//    fun getListIndexType(
//        editFragment: EditFragment,
//    ): TypeSettingsForListIndex.ListIndexTypeKey {
//        val listIndexTypeStr = editFragment.listIndexConfigMap
//            ?.get(ListIndexConfigKey.TYPE.key)
//        return TypeSettingsForListIndex.ListIndexTypeKey.values().firstOrNull {
//            it.key == listIndexTypeStr
//        } ?: TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//    }

    fun setCheckToMaterialCardView(
        materialCardView: MaterialCardView,
        checkItemConfigMap: Map<String, String>,
        listIndexList: MutableList<String>,
        position: Int,
    ){
        val context = materialCardView.context
            ?: return
        val defaultType = CheckItemSettingsForEditList.CheckType.NO
        val getCheckType = checkItemConfigMap.get(
            CheckItemSettingsForEditList.CheckItemSettingKey.TYPE.key
        ).let {
            typeName ->
            if(
                typeName.isNullOrEmpty()
            ) return@let defaultType
            CheckItemSettingsForEditList.CheckType.values().firstOrNull {
                it.type == typeName
            } ?: defaultType
        }
        when(getCheckType){
            CheckItemSettingsForEditList.CheckType.LAST -> {
                val lastIndex = listIndexList.size - 1
                if(
                    position != lastIndex
                ) {
                    materialCardView.isChecked = false
                    return
                }
            }
            CheckItemSettingsForEditList.CheckType.NO ->
                return
        }
        val defaultIconId = R.drawable.icons8_check_ok
        val iconId = checkItemConfigMap.get(
            CheckItemSettingsForEditList.CheckItemSettingKey.ICON.key
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
//        listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
        fileNameTextView: AppCompatTextView?,
        fileName: String,
        listIndexConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
    ): String? {
        val fileNameConfigMap = CmdClickMap.createMap(
            listIndexConfigMap?.get(
                EditListConfigKey.NAME.key
            ),
            '|'
        ).toMap()
        val isHide = fileNameConfigMap.containsKey(
            FileNameKeyForEditList.EditListFileNameKey.ON_HIDE.key
        )
        if (isHide) {
            fileNameTextView?.isVisible = false
            return null
        }
        val fileNameBeforeCut = makeFileName(
//            listIndexTypeKey,
            fileName,
            fileNameConfigMap,
            busyboxExecutor
        )
        return MaxStringLength.cut(
            fileNameBeforeCut,
            100,
            fileNameConfigMap.get(FileNameKeyForEditList.EditListFileNameKey.LENGTH.key)
        )
//        fileNameTextView?.text = MaxStringLength.cut(
//            fileNameBeforeCut,
//        100,
//                fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.LENGTH.key)
//        )
    }

    private fun makeFileName(
//        listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
        fileNameSrc: String,
        fileNameConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
    ): String {
        return makeFannelName(
            fileNameSrc.split("\t").firstOrNull()
                ?: String(),
            fileNameConfigMap,
            busyboxExecutor,
        )
//        return when(listIndexTypeKey){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////            -> fileNameSrc
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> {
//                makeFannelName(
//                    fileNameSrc.split("\t").firstOrNull()
//                        ?: String(),
//                    fileNameConfigMap,
//                    busyboxExecutor,
//                )
//            }
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> makeFannelName(
//                fileNameSrc,
//                fileNameConfigMap,
//                busyboxExecutor,
//            )
//        }
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
            fileNameConfigMap.containsKey(FileNameKeyForEditList.EditListFileNameKey.REMOVE_EXTEND.key).let {
                if (!it) return@let fileNameSrc
                CcPathTool.trimAllExtend(fileNameSrc)
            }
        val compPrefixedFileName =
            fileNameConfigMap.get(FileNameKeyForEditList.EditListFileNameKey.COMP_PREFIX.key).let {
                if (
                    it.isNullOrEmpty()
                ) return@let removedExtendFileName
                UsePath.compPrefix(fileNameSrc, it)
            }
        val compSuffixedFileName =
            fileNameConfigMap.get(FileNameKeyForEditList.EditListFileNameKey.COMP_SUFFIX.key).let {
                if (
                    it.isNullOrEmpty()
                ) return@let compPrefixedFileName
                UsePath.compExtend(fileNameSrc, it)
            }
        if(
            busyboxExecutor == null
        ) return compSuffixedFileName
        return fileNameConfigMap.get(FileNameKeyForEditList.EditListFileNameKey.SHELL_PATH.key).let {
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
//        val parentDirPath: String,
        val fileNameOrInstallFannelLine: String,
        val fileCon: String,
        val editListConfigMap: Map<String, String>?,
        val busyboxExecutor: BusyboxExecutor?
    )
    fun makeFileDesc(
        makeFileDescArgsMaker: MakeFileDescArgsMaker
    ): String? {
        return  makeDescCon(
            makeFileDescArgsMaker,
            makeFileDescArgsMaker
                .fileNameOrInstallFannelLine
                .split("\t")
                .lastOrNull() ?: String()
        )
//        return when(ListIndexAdapter.listIndexTypeKey){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL ->
////                FannelListVariable.getDesc(
////                    makeFileDescArgsMaker.fileNameOrInstallFannelLine
////                )
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
//                makeDescCon(
//                    makeFileDescArgsMaker,
//                    makeFileDescArgsMaker
//                        .fileNameOrInstallFannelLine
//                        .split("\t")
//                        .lastOrNull() ?: String()
//                )
//            }
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL -> {
//                makeDescCon(
//                    makeFileDescArgsMaker,
//                    makeFileDescArgsMaker.fileCon
//                )
//            }
//        }
    }

    private fun makeDescCon(
        makeFileDescArgsMaker: MakeFileDescArgsMaker,
        srcCon: String,
    ): String? {
        val defaultMaxTakeLength = 50
        val defaultTakeFileCon = srcCon.take(defaultMaxTakeLength)
        val editListConfigMap = makeFileDescArgsMaker.editListConfigMap
        if (
            editListConfigMap.isNullOrEmpty()
        ) return null
        val descValue = editListConfigMap.get(EditListConfigKey.DESC.key)
        if (
            descValue.isNullOrEmpty()
        ) return null
        val descConfigMap = CmdClickMap.createMap(
            descValue,
            '|'
        ).toMap()
        val descConByCut = descConfigMap.get(DescSettingsForEditList.EditListDescKey.LENGTH.key).let {
            MaxStringLength.cut(
                srcCon,
                defaultMaxTakeLength,
                it
            )
        }
        val busyboxExecutor = makeFileDescArgsMaker.busyboxExecutor
            ?: return null
        return descConfigMap.get(
            DescSettingsForEditList.EditListDescKey.SHELL_PATH.key
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

    enum class EditListConfigKey(
        val key: String,
    ) {
        TYPE("type"),
        LAYOUT("layout"),
        FOOTER_LAYOUT_PATH("footerLayoutPath"),
        TOOLBAR_LAYOUT_PATH("toolbarLayoutPath"),
        NAME("name"),
        DESC("desc"),
        BK("bk"),
        CHECK_ITEM("checkItem"),
        CLICK("click"),
//        LONG_CLICK("longClick"),
        LIST("list"),
        TITLE_LAYOUT_PATH("titleLayoutPath"),
        SEARCH_BOX("searchBox"),
//        PERFORM("perform"),
        DELETE("delete"),
    }

    object ToolbarLayoutPath {
        enum class ToolbarLayoutMacro(
            val macroConList: List<String>,
        ){
            FOR_ONLY_CMD_VAL_EDIT(
                toolbarLayoutForOnlyCmdValEdit,
            ),
        }
    }


    private val toolbarLayoutForOnlyCmdValEdit = """
    --
    frame=
        |tag=okForCmdValEdit
        ,
    ---

    linear=
        |tag=ok
        |text=
            displayText=`${'$'}{SRC_STR}`
            ?srcStr=`OK`
        |textProperty=
            ?maxLines=1
        |image=
            paths="ok"
        |imageProperty=
            ?scale=`fitCenter`
        |onSave=ON
        |isConsec=OFF
        |var=runDisplayCurSettingValue
            ?func=jsBackstack.exec
        ,
    """.trimIndent().split("\n")
}
