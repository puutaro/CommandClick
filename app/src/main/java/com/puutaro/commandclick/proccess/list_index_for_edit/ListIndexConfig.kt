package com.puutaro.commandclick.proccess.list_index_for_edit

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.JsPathHandlerForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.CheckItemSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ClickSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DescSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.FileNameKeyForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.ReadText
import java.io.File


object ListIndexEditConfig {

    val editTargetContents = "EDIT_TARGET_CONTENTS"

    fun handle(
        editFragment: EditFragment,
        isLongClick: Boolean,
        selectedItem: String,
    ){
        val clickConfigMap = makeClickConfig(
            editFragment,
            isLongClick
        )

        val jsPathMacroStr = execMakeJsPathMacro(
            clickConfigMap,
            String()
        )
        val listIndexArgsMaker = ListIndexArgsMaker(
            editFragment,
            isLongClick,
            clickConfigMap,
        )
        val extraMapForJsPath = ExtraArgsTool.createExtraMapFromMap(
            listIndexArgsMaker.clickConfigMap,
            "!",
        )
        FileSystems.writeFile(
            UsePath.cmdclickDefaultAppDirPath,
    "listIndex.txt",
    "clickConfigMap: ${clickConfigMap}\n\n" +
            "jsPathMacroStr: ${jsPathMacroStr}\n\n" +
            "isLongClick: ${isLongClick}\n\n" +
            "extraMapForJsPath: ${extraMapForJsPath}"
        )
        if(
            jsPathMacroStr.isEmpty()
        ) return

        JsPathHandlerForListIndex.handle(
            listIndexArgsMaker,
            extraMapForJsPath,
            jsPathMacroStr,
            selectedItem,
        )
    }

    private fun makeClickConfig(
        editFragment: EditFragment,
        isLongClick: Boolean,
    ): Map<String, String> {
        val buttonClickMapKey = when(
            isLongClick
        ){
            true -> ListIndexConfigKey.LONG_CLICK.key
            else -> ListIndexConfigKey.CLICK.key

        }
        return editFragment.listIndexConfigMap
            ?.get(buttonClickMapKey).let { clickConfigMapStr ->
                if (
                    clickConfigMapStr.isNullOrEmpty()
                ) return@let emptyMap()
                CmdClickMap.createMap(clickConfigMapStr, "|").toMap()
            }
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

    private fun execMakeJsPathMacro(
        clickConfigMap: Map<String, String>,
        defaultButtonMacroStr: String,
    ): String {
        if(
            clickConfigMap.isEmpty()
        ) return String()
        val clickJsMacroStr =
            clickConfigMap.get(ClickSettingsForListIndex.ClickSettingKey.JS_PATH.key)
        return JsPathMacroForSettingButton.values().firstOrNull {
            it.name == clickJsMacroStr
        }?.name.let name@ {
            if(it != null) return@name it
            if(
                clickJsMacroStr.isNullOrEmpty()
            ) return@name defaultButtonMacroStr
            clickJsMacroStr
        }
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

    fun setFileNameTextView(
        listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
        fileNameTextView: AppCompatTextView?,
        fileName: String,
        listIndexConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor,
    ) {
        val fileNameConfigMap = CmdClickMap.createMap(
            listIndexConfigMap?.get(
                ListIndexConfigKey.NAME.key
            ),
            "|"
        ).toMap()
        val isHide = fileNameConfigMap.containsKey(FileNameKeyForListIndex.ListIndexFileNameKey.ON_HIDE.key)
        if (isHide) {
            fileNameTextView?.isVisible = false
            return
        }
        fileNameTextView?.text = makeFileName(
            listIndexTypeKey,
            fileName,
            fileNameConfigMap,
            busyboxExecutor
        )
    }

    private fun makeFileName(
        listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
        fileNameSrc: String,
        fileNameConfigMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor,
    ): String {
        if(listIndexTypeKey == TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL){
            return fileNameSrc
        }
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
        return fileNameConfigMap.get(FileNameKeyForListIndex.ListIndexFileNameKey.SHELL_PATH.key).let {
                if (
                    it.isNullOrEmpty()
                ) return@let compSuffixedFileName
                val shellPathObj = File(it)
                if (
                    !shellPathObj.isFile
                ) return@let compPrefixedFileName
                val shellDirPath = shellPathObj.parent
                    ?: return@let compSuffixedFileName
                val shellCon = ReadText(
                    shellDirPath,
                    shellPathObj.name
                ).readText().replace(
                    "\${$editTargetContents}",
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
        val busyboxExecutor: BusyboxExecutor
    )
    fun makeFileDesc(
        makeFileDescArgsMaker: MakeFileDescArgsMaker
    ): String? {
        if(
            ListIndexForEditAdapter.listIndexTypeKey ==
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
        ) return makeFileDescArgsMaker
            .fileNameOrInstallFannelLine
            .split("\n")
            .getOrNull(1)
            ?.trim()
            ?.removePrefix("-")
            ?: String()
        val defaultMaxTakeLength = 50
        val fileCon = makeFileDescArgsMaker.fileCon
        val defaultTakeFileCon = fileCon.take(defaultMaxTakeLength)
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
            "|"
        ).toMap()
        descConfigMap.get(DescSettingsForListIndex.ListIndexDescKey.LENGTH.key).let {
            if (
                it.isNullOrEmpty()
            ) return@let defaultTakeFileCon
            val maxTakeLength = try {
                it.toInt()
            } catch (e: Exception) {
                defaultMaxTakeLength
            }
            return fileCon.take(maxTakeLength)
        }
        val busyboxExecutor = makeFileDescArgsMaker.busyboxExecutor
        return descConfigMap.get(DescSettingsForListIndex.ListIndexDescKey.SHELL_PATH.key).let {
            if (
                it.isNullOrEmpty()
            ) return@let defaultTakeFileCon
            val shellPathObj = File(it)
            if (
                !shellPathObj.isFile
            ) return@let defaultTakeFileCon
            val shellDirPath = shellPathObj.parent
                ?: return@let defaultTakeFileCon
            val shellCon = ReadText(
                shellDirPath,
                shellPathObj.name
            ).readText().replace(
                "\${$editTargetContents}",
                fileCon,
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
        NAME("name"),
        DESC("desc"),
        CHECK_ITEM("checkItem"),
        CLICK("click"),
        LONG_CLICK("longClick"),
        LIST("list"),
        SEARCH_BOX("searchBox"),
    }
}
