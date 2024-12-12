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
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
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
            CheckItemSettingsForEditList.CheckType.entries.firstOrNull {
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
            CmdClickIcons.entries.firstOrNull {
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
        SETTING_ACTION("settingAction"),
        SETTING_ACTION2("settingAction2"),
        SETTING_ACTION3("settingAction3"),
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
                ToolbarLayoutForOnlyCmdValEdit.toolbarLayoutForOnlyCmdValEdit2,
            ),
        }
    }

    private object ToolbarLayoutForOnlyCmdValEdit {

        private val typeSeparator = EditComponent.Template.typeSeparator
        private val keySeparator = EditComponent.Template.keySeparator
        private val sectionSeparator = EditComponent.Template.sectionSeparator

        private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
        private val marginTopKey = EditComponent.Template.EditComponentKey.MARGIN_TOP.key
        private val marginBottomKey = EditComponent.Template.EditComponentKey.MARGIN_BOTTOM.key
        private val marginStartKey = EditComponent.Template.EditComponentKey.MARGIN_START.key
        private val marginEndKey = EditComponent.Template.EditComponentKey.MARGIN_END.key
        private val paddingTopKey = EditComponent.Template.EditComponentKey.PADDING_TOP.key
        private val paddingBottomKey = EditComponent.Template.EditComponentKey.PADDING_BOTTOM.key
        private val paddingStartKey = EditComponent.Template.EditComponentKey.PADDING_START.key
        private val paddingEndKey = EditComponent.Template.EditComponentKey.PADDING_END.key
        private val onSaveKey = EditComponent.Template.EditComponentKey.ON_SAVE.key
        private val onClickViewsKey = EditComponent.Template.EditComponentKey.CLICK_VIEWS.key
        private val onConsecKey = EditComponent.Template.EditComponentKey.ON_CONSEC.key
        private val gravityKey = EditComponent.Template.EditComponentKey.GRAVITI.key
        private val topToTopKey = EditComponent.Template.EditComponentKey.TOP_TO_TOP.key
        private val topToBottomKey = EditComponent.Template.EditComponentKey.TOP_TO_BOTTOM.key
        private val bottomToBottomKey = EditComponent.Template.EditComponentKey.BOTTOM_TO_BOTTOM.key
        private val bottomToTopKey = EditComponent.Template.EditComponentKey.BOTTOM_TO_TOP.key
        private val startToStartKey = EditComponent.Template.EditComponentKey.START_TO_START.key
        private val startToEndKey = EditComponent.Template.EditComponentKey.START_TO_END.key
        private val endToEndKey = EditComponent.Template.EditComponentKey.END_TO_END.key
        private val endToStartKey = EditComponent.Template.EditComponentKey.END_TO_START.key

        private val parentIdStr = EditComponent.Template.ConstraintManager.ConstraintParameter.PARENT_ID.str
        private val unsetStr = EditComponent.Template.ConstraintManager.ConstraintParameter.UNSET.str

        private val imageKey = EditComponent.Template.EditComponentKey.IMAGE.key
        private val imagePropertyKey = EditComponent.Template.EditComponentKey.IMAGE_PROPERTY.key
        private val textKey = EditComponent.Template.EditComponentKey.TEXT.key
        private val textPropertyKey = EditComponent.Template.EditComponentKey.TEXT_PROPERTY.key
        private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
        private val widthKey = EditComponent.Template.EditComponentKey.WIDTH.key
        private val constClickImageViewStr =  EditComponent.Template.ClickViewManager.ClickViews.IMAGE.str

        private val imagePathsKey = EditComponent.Template.ImageManager.ImageKey.PATHS.key
        private val imageDelayKey = EditComponent.Template.ImageManager.ImageKey.DELAY.key
        private val imageTagKey = EditComponent.Template.ImagePropertyManager.PropertyKey.TAG.key
        private val imageColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.COLOR.key
        private val imageAlphaKey = EditComponent.Template.ImagePropertyManager.PropertyKey.ALPHA.key
        private val imageScaleKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE.key
        private val imageWidthKey = EditComponent.Template.ImagePropertyManager.PropertyKey.WIDTH.key
        private val imageHeightKey = EditComponent.Template.ImagePropertyManager.PropertyKey.HEIGHT.key
        private val imageMarginTopKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_TOP.key
        private val imageMarginStartKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_START.key
        private val imageMarginEndKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_END.key
        private val imageMarginBottomKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_BOTTOM.key
        private val imagePaddingTopKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_TOP.key
        private val imagePaddingStartKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_START.key
        private val imagePaddingEndKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_END.key
        private val imagePaddingBottomKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_BOTTOM.key

        private val displayTextKey = EditComponent.Template.TextManager.TextKey.DISPLAY_TEXT.key
        private val srcStrKey = EditComponent.Template.TextManager.TextKey.SRC_STR.key


        private val textSizeKey = EditComponent.Template.TextPropertyManager.Property.SIZE.key
        private val textStyleKey = EditComponent.Template.TextPropertyManager.Property.STYLE.key
        private val textWidthKey = EditComponent.Template.TextPropertyManager.Property.WIDTH.key
        private val textTagKey = EditComponent.Template.TextPropertyManager.Property.TAG.key
        private val textColorKey = EditComponent.Template.TextPropertyManager.Property.COLOR.key
        private val strokeColorKey = EditComponent.Template.TextPropertyManager.Property.STROKE_COLOR.key
        private val strokeWidthKey = EditComponent.Template.TextPropertyManager.Property.STROKE_WIDTH.key
        private val textAlphaKey = EditComponent.Template.TextPropertyManager.Property.ALPHA.key
        private val textMaxLinesKey = EditComponent.Template.TextPropertyManager.Property.MAX_LINES.key
        private val textMarginTopKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_TOP.key
        private val textMarginStartKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_START.key
        private val textMarginEndKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_END.key
        private val textMarginBottomKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_BOTTOM.key
        private val textPaddingTopKey = EditComponent.Template.TextPropertyManager.Property.PADDING_TOP.key
        private val textPaddingStartKey = EditComponent.Template.TextPropertyManager.Property.PADDING_START.key
        private val textPaddingEndKey = EditComponent.Template.TextPropertyManager.Property.PADDING_END.key
        private val textPaddingBottomKey = EditComponent.Template.TextPropertyManager.Property.PADDING_BOTTOM.key

        private val srcStrHolder = EditComponent.Template.ReplaceHolder.SrcReplaceHolders.SRC_STR.key
        private val fitCenterImageScale = EditComponent.Template.ImagePropertyManager.ImageScale.FIT_CENTER.scale
        private val fitXyImageScale = EditComponent.Template.ImagePropertyManager.ImageScale.FIT_XY.scale
        private val textBoldStyle = EditComponent.Template.TextPropertyManager.TextStyle.BOLD.key
        val toolbarLayoutForOnlyCmdValEdit = """
    --
    ${EditComponent.Template.LayoutKey.FRAME.key}=
        ${typeSeparator}${tagKey}=okForCmdValEdit
        ${sectionSeparator}
    ---
    
    ${EditComponent.Template.LayoutKey.VERTICAL.key}=
        ${typeSeparator}${tagKey}=vertical1
        ${sectionSeparator}
    
    ---
    ${EditComponent.Template.LayoutKey.HORIZON.key}=
        ${typeSeparator}${tagKey}=horizonTag
        ${sectionSeparator}
    ---
    ${EditComponent.Template.LayoutKey.CONTENTS.key}=
        ${typeSeparator}${tagKey}=ok
        ${typeSeparator}${textKey}=
            ${displayTextKey}=`${srcStrHolder}`
            ${keySeparator}${srcStrKey}=`OK`
        ${typeSeparator}${textPropertyKey}=
            ${keySeparator}${textStyleKey}=`${textBoldStyle}`
            ${keySeparator}${textMaxLinesKey}=1
        ${typeSeparator}${imageKey}=
            ${imagePathsKey}="ok"
        ${typeSeparator}${imagePropertyKey}=
            ${keySeparator}${imageScaleKey}=`${fitCenterImageScale}`
        ${typeSeparator}${onSaveKey}=ON
        ${typeSeparator}${onConsecKey}=OFF
        ${typeSeparator}var=runDisplayCurSettingValue
            ?func=jsBackstack.exec
        ${sectionSeparator}
    """.trimIndent().split("\n")

        val toolbarLayoutForOnlyCmdValEdit2 = """
    --
    ${EditComponent.Template.LayoutKey.FRAME.key}=
        ${typeSeparator}${tagKey}=okForCmdValEdit
    ---
    
    ${EditComponent.Template.LayoutKey.CONTENTS.key}=
        ${typeSeparator}${tagKey}=ok
        ${typeSeparator}${topToTopKey}=${parentIdStr}
        ${typeSeparator}${startToStartKey}=${parentIdStr}
        ${typeSeparator}${endToEndKey}=${parentIdStr}
        ${typeSeparator}${bottomToBottomKey}=${parentIdStr}
        ${typeSeparator}${widthKey}=MATCH
        ${typeSeparator}${heightKey}=50
        ${typeSeparator}${onClickViewsKey}=${constClickImageViewStr}
        ${typeSeparator}${textKey}=
            ${displayTextKey}=`${srcStrHolder}`
            ${keySeparator}${srcStrKey}=`OK`
        ${typeSeparator}${textPropertyKey}=
            ${keySeparator}${textWidthKey}=`WRAP`
            ${keySeparator}${textStyleKey}=`${textBoldStyle}`
            ${keySeparator}${textMaxLinesKey}=1
        ${typeSeparator}${imageKey}=
            ${imagePathsKey}="ok"
        ${typeSeparator}${imagePropertyKey}=
            ${keySeparator}${imageWidthKey}=`MATCH`
            ${keySeparator}${imageScaleKey}=`${fitCenterImageScale}`
        ${typeSeparator}${onSaveKey}=ON
        ${typeSeparator}${onConsecKey}=OFF
        ${typeSeparator}var=runDisplayCurSettingValue
            ?func=jsBackstack.exec
        ${sectionSeparator}
    """.trimIndent().split("\n")
    }
}
