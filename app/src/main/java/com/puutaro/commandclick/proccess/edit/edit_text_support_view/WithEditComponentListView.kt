package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ListViewToolForEditListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForEditListAdapter
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.edit_list.EditFrameMaker
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.proccess.edit_list.config_settings.BkImageSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.LayoutSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.SettingActionForEditList
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import com.puutaro.commandclick.util.str.SnakeCamelTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File


object WithEditComponentListView{

    fun keyboardHide(
        fragment: Fragment,
    ){
        when(fragment) {
            is EditFragment -> {
                fragment.binding.editListSearchEditText.setText(String())
                val listener =
                    fragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment

                listener?.onKeyBoardVisibleChangeForEditFragment(
                    false,
                    true,
                )
            }
            else -> {}
        }
        Keyboard.hiddenKeyboardForFragment (fragment)
    }

    const val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    const val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

    fun create(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListConfigMapSrc: Map<String, String>?,
        editBackstackCountFrame: FrameLayout,
        editBackstackCountView: ShapeableImageView,
        editTextView: OutlineTextView,
        editTitleImage: AppCompatImageView,
        editListRecyclerView: RecyclerView,
        editListBkFrame: FrameLayout,
        editListSearchEditText: AppCompatEditText,
        editFooterLinearlayout: LinearLayoutCompat,
        editToolbarLinearLayout: LinearLayoutCompat?,
        fannelCenterButtonLayout: FrameLayout?,
        fannelContentsList: List<String>?,
    ) {
        val context = fragment.context ?: return
        runBlocking {
//            SettingActionManager.Companion.GlobalExitManager.init()
            SettingActionManager.Companion.BeforeActionImportMapManager.init()
        }
        val varNameToValueMap = let {

            SettingActionForEditList.getSettingConfigCon(
                editListConfigMapSrc,
            ).let {
                val settingActionManager = SettingActionManager()
                runBlocking {
                    val keyToSubKeyConWhere =
                        "${CommandClickScriptVariable.EDIT_LIST_CONFIG}, ${fannelInfoMap.map {
                            val key = SnakeCamelTool.snakeToCamel(it.key)
                            "${key}: ${it.value}"
                        }.joinToString(", ")}"
                    settingActionManager.exec(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        it,
                        keyToSubKeyConWhere,
                    )
                }
            }
        }
//        val varNameToValueMap2 = let {
//            editListConfigMapSrc?.get(
//                EditListConfig.EditListConfigKey.SETTING_ACTION2.key,
//            ).let {
//                val fannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
//                val settingActionManager = SettingActionManager()
//                runBlocking {
//                    settingActionManager.exec(
//                        fragment,
//                        fannelInfoMap,
//                        setReplaceVariableMapSrc,
//                        busyboxExecutor,
//                        it,
//                        "${CommandClickScriptVariable.EDIT_LIST_CONFIG} in ${fannelName}"
//                    )
//                }
//            }
//        }
//        val varNameToValueMap3 = let {
//            editListConfigMapSrc?.get(
//                EditListConfig.EditListConfigKey.SETTING_ACTION3.key,
//            ).let {
//                val fannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
//                val settingActionManager = SettingActionManager()
//                runBlocking {
//                    settingActionManager.exec(
//                        fragment,
//                        fannelInfoMap,
//                        setReplaceVariableMapSrc,
//                        busyboxExecutor,
//                        it,
//                        "${CommandClickScriptVariable.EDIT_LIST_CONFIG} in ${fannelName}"
//                    )
//                }
//            }
//        }
        runBlocking {
            SettingActionManager.Companion.BeforeActionImportMapManager.init()
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "svarNameToValueMap.txt").absolutePath,
//            listOf(
//                "varNameToValueMap: ${varNameToValueMap}\n",
//                "varNameToValueMap2: ${varNameToValueMap2}\n",
//                "varNameToValueMap3: ${varNameToValueMap3}\n",
//            ).joinToString("\n")
//        )
        val setReplaceVariableMap =
            (setReplaceVariableMapSrc ?: emptyMap()) +
                    varNameToValueMap
        val editListConfigMap = editListConfigMapSrc?.map {
            val key = CmdClickMap.replace(
                it.key,
                varNameToValueMap
            )
            val value = CmdClickMap.replace(
                it.value,
                varNameToValueMap
            )
            key to value
        }?.toMap()

        CoroutineScope(Dispatchers.Main).launch{
            val titleLayoutPathKey = EditListConfig.EditListConfigKey.TITLE_LAYOUT_PATH.key
            val titleSettingPath = editListConfigMap?.get(
                titleLayoutPathKey
            ) ?: String()
            val titleSettingMap = withContext(Dispatchers.IO){
                ListSettingVariableListMaker.makeFromSettingPath(
                    context,
                    titleSettingPath,
                    fannelInfoMap,
                    setReplaceVariableMap,
                ).map {
                    titleSettingMapSrc ->
                    val titleSectionKey = titleSettingMapSrc.key
                    val keyToSubKeyConSrc = titleSettingMapSrc.value
                    val keyToSubKeyConWhere =
                        "${titleLayoutPathKey} in ${CommandClickScriptVariable.EDIT_LIST_CONFIG}, ${fannelInfoMap.map {
                            val key = SnakeCamelTool.snakeToCamel(it.key)
                            "${key}: ${it.value}"
                        }.joinToString(", ")}"
                    val titleVarNameToValueMap = SettingActionManager().exec(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        keyToSubKeyConSrc,
                        keyToSubKeyConWhere
                    )
                    titleSectionKey to CmdClickMap.replace(
                        keyToSubKeyConSrc,
                        titleVarNameToValueMap
                    )
                }.toMap()
            }
            TitleImageAndViewSetter.set(
                fragment,
                editBackstackCountFrame,
                editBackstackCountView,
                editTextView,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editTitleImage,
                titleSettingMap
            )
        }
        val editListBkPairs = EditListConfig.getConfigKeyConList(
            editListConfigMap,
            EditListConfig.EditListConfigKey.BK.key
        )
        CoroutineScope(Dispatchers.Main).launch {
//            withContext(Dispatchers.Main) {
//                BkImageSettingsForEditList.makeBkImage(
//                    editListBkImage,
//                    editListBkPairs,
//                )
//            }
            val bkFrameLayout = withContext(Dispatchers.Main){
                BkImageSettingsForEditList.makeBkFrame(
                    context,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    editListBkPairs,
                )
            }
            withContext(Dispatchers.Main) {
                editListBkFrame.addView(bkFrameLayout)
            }
        }
        val indexListMap = EditListConfig.getConfigKeyMap(
            editListConfigMap,
            EditListConfig.EditListConfigKey.LIST.key
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "glistWith.txt").absolutePath,
//            listOf(
//                "tag: ${editFragment.tag}",
//                "listIndexConfigMap: ${listIndexConfigMap}",
//                "indexListMap: ${indexListMap}",
//                "listIndexTypeKey: ${listIndexTypeKey.key}",
//            ).joinToString("\n\n")
//        )
        val lineMapList = ListSettingsForEditList.EditListMaker.makeLineMapListHandler(
            fannelInfoMap,
            setReplaceVariableMap,
            indexListMap,
            busyboxExecutor,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lfileList.txt").absolutePath,
//            listOf(
//                "indexListMap: ${indexListMap}",
//                "fileList: ${fileList}",
//            ).joinToString("\n\n")
//        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lfannelContentsList.txt").absolutePath,
//            fannelContentsList?.joinToString("\n") ?: String()
//        )
        val editComponentListAdapter = EditComponentListAdapter(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap + varNameToValueMap,
            editListConfigMap,
            busyboxExecutor,
            indexListMap,
            lineMapList,
            fannelContentsList
        )
        val layoutConfigMap = LayoutSettingsForEditList.getLayoutConfigMap(
            editListConfigMap,
        )
        ItemTouchHelperCallbackForEditListAdapter.set(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
            editComponentListAdapter,
            layoutConfigMap
        )

        editListRecyclerView.adapter = editComponentListAdapter

        val isReverseLayout = LayoutSettingsForEditList.howReverseLayout(
            fannelInfoMap,
            setReplaceVariableMap,
            layoutConfigMap
        )
        LayoutSettingsForEditList.setLayout(
            context,
            editComponentListAdapter.getLayoutConfigMap(),
            editListRecyclerView,
            isReverseLayout,
        )
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                var prevYposi = 0f
                for(i in 1..10){
                    val curYPosi = withContext(Dispatchers.Main){
                        editListRecyclerView.y
                    }
                    if(
                        prevYposi != 0f
                        && prevYposi == curYPosi
                    ) break
                    prevYposi = curYPosi
//                    val isVisibleComp = withContext(Dispatchers.Main){
//                        isRecyclerViewFullyVisible(editListRecyclerView)
//                    }
//                    //gridLayout.findLastCompletelyVisibleItemPosition()
//                    if(isVisibleComp){
//                        break
//                    }
//                    if(
//                        isVisibleComp == lastItemIndex
//                    ) {
//                        break
//                    }
                    delay(100)
                }
            }
            withContext(Dispatchers.Main) {
                editListRecyclerView.scrollToPosition(0)
            }
            withContext(Dispatchers.IO){
                SettingActionManager.Companion.BeforeActionImportMapManager.init()
//                SettingActionManager.Companion.GlobalExitManager.init()
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            setToolbar(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                editToolbarLinearLayout,
                fannelCenterButtonLayout,
                editListConfigMap,
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            setFooter(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editFooterLinearlayout,
                editListRecyclerView,
                editListConfigMap,
            )
        }
        invokeItemSetClickListenerForFileList(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView
        )
        invokeItemSetTouchListenerForFileList(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView,
        )
        makeSearchEditText(
            fragment,
            fannelInfoMap,
            editComponentListAdapter,
            editListSearchEditText,
            editListConfigMap
        )
    }

    private suspend fun setFooter(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editFooterLinearlayout: LinearLayoutCompat,
        editListRecyclerView: RecyclerView,
        editListConfigMap: Map<String, String>?,
    ){
        setFooterOrToolbar(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editFooterLinearlayout,
            editListRecyclerView,
            null,
            null,
            editListConfigMap,
        )
    }

    private fun isRecyclerViewFullyVisible(
        recyclerView: RecyclerView
    ): Boolean {
        if (!recyclerView.isAttachedToWindow) return false
        val rect = Rect()
        val isVisibleRecyclerView = recyclerView.getGlobalVisibleRect(rect)
        if (!isVisibleRecyclerView) return false
        return (rect.bottom - rect.top) >= recyclerView.height
    }


    private suspend fun setToolbar(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editToolbarLinearLayout: LinearLayoutCompat?,
        fannelCentrButtonLayout: FrameLayout?,
        editListConfigMap: Map<String, String>?,
    ){
        setFooterOrToolbar(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            null,
            editListRecyclerView,
            editToolbarLinearLayout,
            fannelCentrButtonLayout,
            editListConfigMap,
        )
    }

    private suspend fun setFooterOrToolbar(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editFooterLinearlayout: LinearLayoutCompat?,
        editListRecyclerView: RecyclerView,
        editToolbarLinearLayout: LinearLayoutCompat?,
        fannelCenterButtonLayout: FrameLayout?,
        editListConfigMap: Map<String, String>?,

    ) {
        val context = fragment.context
            ?: return
        val editComponentListAdapter =
            editListRecyclerView.adapter as? EditComponentListAdapter
        val plusKeyToSubKeyConWhere =
            fannelInfoMap.map {
                val key = SnakeCamelTool.snakeToCamel(it.key)
                "${key}: ${it.value}"
            }.joinToString(", ")
        val isEditToolbar = editToolbarLinearLayout != null
        val debugWhere = when(isEditToolbar){
            true -> "toolBar"
            else -> "footer"
        }
        val layoutKey = when(isEditToolbar){
            true -> EditListConfig.EditListConfigKey.TOOLBAR_LAYOUT_PATH
            else -> EditListConfig.EditListConfigKey.FOOTER_LAYOUT_PATH
        }.key
        val footerOrToolbarLayoutPath = ListSettingsForEditList.ViewLayoutPathManager.getViewLayoutPath(
            fannelInfoMap,
            setReplaceVariableMap,
            editListConfigMap,
            layoutKey,
        )
        val isOnlyCmdValEdit =
            footerOrToolbarLayoutPath == EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.name
        val frameMapToFrameTagAndVerticalKeysListToLinearMapList = when(isOnlyCmdValEdit) {
            true -> ListSettingsForEditList.ViewLayoutPathManager.parseFromList(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.macroConList,
                "${EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.name} in $plusKeyToSubKeyConWhere"
                )
            else -> ListSettingsForEditList.ViewLayoutPathManager.parse(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                footerOrToolbarLayoutPath
            )
        }
        val frameMap = frameMapToFrameTagAndVerticalKeysListToLinearMapList?.first ?: emptyMap()
        val frameTagList = frameMap.keys
        val frameTagToVerticalKeysCon = frameMapToFrameTagAndVerticalKeysListToLinearMapList?.second ?: emptyList()
        val verticalTagToLinearKeysListMap = frameMapToFrameTagAndVerticalKeysListToLinearMapList?.third ?: emptyMap()

        val tagKey = EditComponent.Template.EditComponentKey.TAG.key
        val typeSeparator = EditComponent.Template.typeSeparator
        val isConsecKey = EditComponent.Template.EditComponentKey.IS_CONSEC.key
        val onClickKey = EditComponent.Template.EditComponentKey.ON_CLICK.key
        val jsActionKeyList = JsActionKeyManager.JsActionsKey.entries.map {
            it.key
        }
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        val alreadyUseTagList = mutableListOf<String>()
        val noIndexSign = -1
        withContext(Dispatchers.Main) {
            val weightSumFloat = 1f
            frameTagList.forEach { frameTag ->
                withContext(Dispatchers.IO){
                    EditComponent.AdapterSetter.tagDuplicateErrHandler(
                        context,
                        EditComponent.Template.TagManager.TagGenre.FRAME_TAG,
                        frameTag,
                        alreadyUseTagList,
                        "${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                        plusKeyToSubKeyConWhere,
                    )?.let {
                        alreadyUseTagList.add(it)
                    }
                }
                val verticalTagToKeyPairsListToVarNameValueMapList = withContext(Dispatchers.IO){
                    EditComponent.AdapterSetter.makeVerticalTagAndKeyPairsListToVarNameToValueMap(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        frameTag,
                        frameTagToVerticalKeysCon,
                        emptyMap(),
                        String(),
                        String(),
                        String(),
                        null,
                    )
                }
                val verticalLinerWeight = withContext(Dispatchers.IO) {
                    EditComponent.AdapterSetter.culcVerticalLinerWeight(
                        verticalTagToKeyPairsListToVarNameValueMapList
                    )
                }
//                val verticalLinerWeight = weightSumFloat / verticalTagToKeyPairsListToVarNameValueMapList.size
                verticalTagToKeyPairsListToVarNameValueMapList.forEach setVertical@ {
                    verticalTagToKeyPairsListToVarNameToValueMap ->
                    val verticalTag = verticalTagToKeyPairsListToVarNameToValueMap.first
                    withContext(Dispatchers.IO){
                        EditComponent.AdapterSetter.tagDuplicateErrHandler(
                            context,
                            EditComponent.Template.TagManager.TagGenre.VERTICAL_TAG,
                            EditComponent.Template.TagManager.extractVerticalTag(verticalTag),
                            alreadyUseTagList,
                            "${verticalTag}: ${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                            plusKeyToSubKeyConWhere,
                        )?.let {
                            alreadyUseTagList.add(it)
                        }
                    }
                    val keyPairsListToVarNameToValueMap = verticalTagToKeyPairsListToVarNameToValueMap.second
                    val verticalKeyPairs = keyPairsListToVarNameToValueMap.first
                    val verticalVarNameToValueMap = keyPairsListToVarNameToValueMap.second
                    val isVerticalEnable = withContext(Dispatchers.IO) {
                        PairListTool.getValue(
                            verticalKeyPairs,
                            enableKey,
                        ).let { enableStr ->
                            enableStr != switchOff
                        }
                    }
                    if(
                        !isVerticalEnable
                    ) return@setVertical
                    val verticalLinearLayout = withContext(Dispatchers.Main) {
                        when (editToolbarLinearLayout != null) {
                            true -> null
                            else -> EditComponent.AdapterSetter.makeVerticalLinear(
                                context,
                                null,
                                verticalKeyPairs,
                                verticalLinerWeight,
                                verticalTag,
                            )
                        }
                    }
                    val linearKeysList =
                        withContext(Dispatchers.IO) {
                            verticalTagToLinearKeysListMap.get(verticalTag)
                                ?.let { linearKeysListSrc ->
                                    when (isEditToolbar) {
                                        true -> listOf(
                                            linearKeysListSrc.firstOrNull() ?: emptyList()
                                        )

                                        else -> linearKeysListSrc
                                    }
                                }
                        }
                    linearKeysList?.forEach setHorizon@ { linearKeyValues ->
                        val horizonLinearLayout = withContext(Dispatchers.Main) {
                            when (isEditToolbar) {
                                true -> editToolbarLinearLayout
                                else -> LinearLayoutCompat(context).apply {
                                    tag = frameTag
                                    val linearParam = LinearLayoutCompat.LayoutParams(
                                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                                    )
                                    layoutParams = linearParam
                                    weightSum = weightSumFloat
                                    orientation = LinearLayoutCompat.HORIZONTAL
                                    gravity = Gravity.CENTER
                                }
                            }
                        }
                        val linearFrameTagToKeyPairsList = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.makeLinearFrameTagToKeyPairsList(
                                linearKeyValues,
                                frameTag,
                                frameTag,
                                frameTag,
                                noIndexSign,
                            )
                        }
                        val linearKeyValueSize = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.culcLinearKeyValueSize(
                                linearFrameTagToKeyPairsList,
                                verticalKeyPairs,
                            )
                        }
                        val layoutWeight = withContext(Dispatchers.IO) culcFrameLayoutWeight@ {
                            when (isEditToolbar && !isOnlyCmdValEdit) {
                                true -> weightSumFloat / (linearKeyValueSize + 1)
                                else -> {
                                    if(
                                        linearKeyValueSize == 0
                                    ) return@culcFrameLayoutWeight 0f
                                    weightSumFloat / linearKeyValueSize
                                }
                            }
                        }
                        if (isEditToolbar) {
                            withContext(Dispatchers.Main) {
                                let {
                                    val layoutParam =
                                        fannelCenterButtonLayout?.layoutParams as? LinearLayoutCompat.LayoutParams
                                    if (layoutParam?.weight == layoutWeight) return@let
                                    layoutParam?.weight = layoutWeight
                                    fannelCenterButtonLayout?.layoutParams = layoutParam
                                }
                            }
                        }
                        var horizonVarNameToValueMap = verticalVarNameToValueMap
                        linearKeyValues.forEachIndexed setFrame@{
                            index, linearFrameKeyPairsListConSrc ->
                            val isHorizonSetting =
                                index == 0
                            val varNameToValueMap = withContext(Dispatchers.IO) updateLinearKeyParsListCon@ {
                                EditComponent.AdapterSetter.makeFrameVarNameToValueMap(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    editComponentListAdapter,
                                    verticalVarNameToValueMap,
                                    "${verticalTag}: ${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                                    linearFrameKeyPairsListConSrc,
                                    frameTag,
                                    frameTag,
                                    frameTag,
                                    noIndexSign,
                                )
                            }
                            when(isHorizonSetting){
                                true -> {
                                    horizonVarNameToValueMap += varNameToValueMap
                                }
                                else -> {}
                            }
                            val frameVarNameToValueMap = when(isHorizonSetting) {
                                true -> horizonVarNameToValueMap
                                else -> horizonVarNameToValueMap + varNameToValueMap
                            }
                            val linearFrameKeyPairsListCon = CmdClickMap.replace(
                                linearFrameKeyPairsListConSrc,
                                frameVarNameToValueMap
                            )
                            val linearFrameKeyPairsList = withContext(Dispatchers.IO) {
                                CmdClickMap.createMap(
                                    linearFrameKeyPairsListCon,
                                    typeSeparator
                                )
                            }
                            withContext(Dispatchers.IO) {
                                EditComponent.AdapterSetter.isNotLinearKeyErr(
                                    context,
                                    EditComponent.Template.LayoutKey.VERTICAL.key,
                                    verticalKeyPairs,
                                    "verticalTag: ${verticalTag}, ${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                                    plusKeyToSubKeyConWhere,
                                )
                            }.let {
                                    isNotHorizonKeyErr ->
                                if(isNotHorizonKeyErr) return@setVertical
                            }
                            val linearFrameTag = withContext(Dispatchers.IO) {
                                PairListTool.getValue(
                                    linearFrameKeyPairsList,
                                    tagKey,
                                )?.let {
                                    CmdClickMap.replace(
                                        it,
                                        frameVarNameToValueMap
                                    )
                                } ?: String()
                            }
                            withContext(Dispatchers.IO){
                                val tagGenre = when(isHorizonSetting){
                                    true -> EditComponent.Template.TagManager.TagGenre.HORIZON_TAG
                                    else -> EditComponent.Template.TagManager.TagGenre.EDIT_FRAME_TAG
                                }
                                EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                    context,
                                    tagGenre,
                                    linearFrameTag,
                                    alreadyUseTagList,
                                    "${verticalTag}: ${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                                    plusKeyToSubKeyConWhere,
                                )?.let {
                                    alreadyUseTagList.add(it)
                                }
                            }
                            let {
                                if (!isHorizonSetting) return@let
                                withContext(Dispatchers.IO) {
                                    EditComponent.AdapterSetter.isNotLinearKeyErr(
                                        context,
                                        EditComponent.Template.LayoutKey.HORIZON.key,
                                        linearFrameKeyPairsList,
                                        "verticalTag: ${verticalTag}, ${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                                        plusKeyToSubKeyConWhere,
                                    )
                                }.let {
                                    isNotHorizonKeyErr ->
                                    if (
                                        isNotHorizonKeyErr
                                    ) return@setHorizon
                                }
                                if (isEditToolbar){
                                    return@setFrame
                                }
                                PairListTool.getValue(
                                    linearFrameKeyPairsList,
                                    enableKey,
                                ).let enable@ {
                                        enableStr ->
                                    if(
                                        enableStr != switchOff
                                    ) return@enable
                                    return@setHorizon
                                }
                                withContext(Dispatchers.Main) {
                                    EditComponent.AdapterSetter.setHorizonLinear(
                                        horizonLinearLayout,
//                                        verticalKeyPairs,
                                        linearFrameKeyPairsList
                                    )
                                }
                                return@setFrame
                            }

                            editComponentListAdapter?.footerKeyPairListConMap?.put(
                                linearFrameTag,
                                linearFrameKeyPairsListCon
                            )
                            val linearFrameLayout = withContext(Dispatchers.Main) {
                                EditFrameMaker.make(
                                    context,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    linearFrameKeyPairsList,
                                    0,
                                    layoutWeight,
                                    linearFrameTag,
//                                false,
                                    editComponentListAdapter?.totalSettingValMap,
                                )
                            } ?: return@setFrame
                            withContext(Dispatchers.IO) execClick@{
                                if (
                                    linearFrameKeyPairsListCon.isNullOrEmpty()
                                ) return@execClick
                                val onClick = PairListTool.getValue(
                                    linearFrameKeyPairsList,
                                    onClickKey,
                                ) != switchOff
                                val isConsec =
                                    PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        isConsecKey,
                                    ) == EditComponent.Template.switchOn
                                jsActionKeyList.any {
                                    jsActionKey ->
                                    !PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        jsActionKey,
                                    ).isNullOrEmpty()
                                }.let { isJsAc ->
                                    val isJsAcClick = !isJsAc
                                            && linearFrameLayout.tag != null
                                    if (
                                        isJsAcClick
                                        || !onClick
                                    ) {
//                                        linearFrameLayout.setBackgroundResource(0)
//                                        linearFrameLayout.isClickable = false
                                        return@let
                                    }
                                    withContext(Dispatchers.Main) execExecClick@ {
                                        val outValue = TypedValue()
                                        context.theme.resolveAttribute(
                                            android.R.attr.selectableItemBackground,
                                            outValue,
                                            true
                                        )
                                        val clickViewList = linearFrameLayout.children.filter {
                                            it is OutlineTextView
                                                    || it is AppCompatImageView
                                        }
                                        clickViewList.forEach { clickView ->
                                            clickView.setBackgroundResource(outValue.resourceId)
                                            clickView.isClickable = true
                                            if (!isConsec) {
                                                clickView.setOnClickListener {
                                                    execJsAction(
                                                        fragment,
                                                        fannelInfoMap,
                                                        setReplaceVariableMap,
                                                        editListRecyclerView,
                                                        linearFrameKeyPairsListCon
                                                    )
                                                }
                                                return@execExecClick
                                            }
                                            with(clickView) {
                                                var consecutiveJob: Job? = null
                                                setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                                                    var execTouchJob: Job? = null
                                                    when (event.action) {
                                                        MotionEvent.ACTION_DOWN -> {
                                                            consecutiveJob?.cancel()
                                                            consecutiveJob =
                                                                CoroutineScope(Dispatchers.IO).launch {
                                                                    var roopTimes = 0
                                                                    while (true) {
                                                                        execTouchJob =
                                                                            CoroutineScope(
                                                                                Dispatchers.Main
                                                                            ).launch {
                                                                                execJsAction(
                                                                                    fragment,
                                                                                    fannelInfoMap,
                                                                                    setReplaceVariableMap,
                                                                                    editListRecyclerView,
                                                                                    linearFrameKeyPairsListCon
                                                                                )
                                                                            }
                                                                        withContext(Dispatchers.IO) {
                                                                            if (
                                                                                roopTimes == 0
                                                                            ) delay(300)
                                                                            else delay(60)
                                                                        }
                                                                        roopTimes++
                                                                    }
                                                                }
                                                        }

                                                        MotionEvent.ACTION_UP,
                                                        MotionEvent.ACTION_CANCEL,
                                                            -> {
                                                            v.performClick()
                                                            execTouchJob?.cancel()
                                                            consecutiveJob?.cancel()
                                                        }
                                                    }
                                                    true
                                                })
                                            }
                                        }
                                    }
                                }
                            }
                            horizonLinearLayout?.addView(linearFrameLayout)
                        }
                        verticalLinearLayout?.addView(horizonLinearLayout)
                    }
                    editFooterLinearlayout?.addView(verticalLinearLayout)
                }
            }
        }
    }

    private fun execJsAction(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView?,
        jsAcCon: String,
    ){
        val mainFannelPath = File(
            UsePath.cmdclickDefaultAppDirPath,
            FannelInfoTool.getCurrentFannelName(fannelInfoMap)
        ).absolutePath
        val editComponentListAdapter = editListRecyclerView?.adapter as? EditComponentListAdapter
        EditComponentListAdapter.MainFannelUpdater.saveFannelCon(
            editComponentListAdapter?.fannelContentsList,
            fannelInfoMap,
            jsAcCon
        )
        val jsActionMap = JsActionTool.makeJsActionMap(
            fragment,
            fannelInfoMap,
            jsAcCon,
            setReplaceVariableMap,
            mainFannelPath
        )
        JsPathHandlerForToolbarButton.handle(
            fragment,
            String(),
            null,
            jsActionMap,
        )
    }

    private fun invokeItemSetClickListenerForFileList(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView
    ) {
        val editComponentListAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
        editComponentListAdapter.editAdapterClickListener =
            object: EditComponentListAdapter.OnEditAdapterClickListener {
                override fun onEditAdapterClick(
                    itemView: View,
                    holder: EditComponentListAdapter.EditListViewHolder,
                    editListPosition: Int,
                ) {
                    val frameLayout = itemView as FrameLayout
                    val tag = frameLayout.tag as String?
                        ?: return
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lclcik.txt").absolutePath,
//                        listOf(
//                            "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                            "lineMapList: ${editComponentListAdapter.lineMapList}",
//                        ).joinToString("\n")
//                    )
                    val selectedItemLineMap =
                        editComponentListAdapter.lineMapList.getOrNull(holder.bindingAdapterPosition)
                            ?: return
                    keyboardHide(
                        fragment,
                    )

                    val frameOrLinearCon = EditComponent.Template.ReplaceHolder.replaceHolder(
                        holder.keyPairListConMap.get(tag),
                        holder.srcTitle,
                        holder.srcCon,
                        holder.srcImage,
                        editListPosition,
                    ) ?: return
                    frameLayout.children.firstOrNull{
                        it is OutlineTextView
                    }?.let {
                        val textView = it as OutlineTextView
                        val curSettingValue = textView.autofillHints?.firstOrNull()
//                        FileSystems.writeFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lclickUpdate_inwithEdit.txt").absolutePath,
//                            listOf(
//                                "tag: ${tag}",
//                                "text: ${textView.text}",
//                                "settingValMap: ${editComponentListAdapter.initSettingValMap}",
//                                "cmdValMap: ${editComponentListAdapter.initCmdValMap}",
//                                "hint: ${textView.autofillHints?.firstOrNull()}"
//                            ).joinToString("\n")
//                        )
                        editComponentListAdapter.handleClickEvent(
                            editListRecyclerView,
                            tag,
                            curSettingValue,
                            editListPosition,
                            frameOrLinearCon
                        )
                    }
                    EditListConfig.handle(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        editListRecyclerView,
//                        false,
                        selectedItemLineMap,
                        frameOrLinearCon,
                        editListPosition
                    )
                }
            }
    }


    private fun invokeItemSetTouchListenerForFileList(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
    ) {
        var execTouchJob: Job? = null
        var consecutiveJob: Job? = null
        val editComponentListAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
        editComponentListAdapter.editAdapterTouchUpListener = object: EditComponentListAdapter.OnEditAdapterTouchUpListener {
            override fun onEditAdapterTouchUp(
                itemView: View,
                holder: EditComponentListAdapter.EditListViewHolder,
                editListPosition: Int
            ) {
                execTouchJob?.cancel()
                consecutiveJob?.cancel()
                return
            }
        }

        editComponentListAdapter.editAdapterTouchDownListener = object: EditComponentListAdapter.OnEditAdapterTouchDownListener {
            override fun onEditAdapterTouchDown(
                itemView: View,
                holder: EditComponentListAdapter.EditListViewHolder,
                editListPosition: Int
            ) {
                val frameLayout = itemView as FrameLayout
                val tag = itemView.tag as String?
                    ?: return
                val selectedItemLineMap =
                    editComponentListAdapter.lineMapList.getOrNull(holder.bindingAdapterPosition)
                        ?: return
                val frameOrLinearCon = holder.keyPairListConMap.get(tag)
                    ?: return
                val textView = frameLayout.children.firstOrNull{
                    it is OutlineTextView
                } as? OutlineTextView
                consecutiveJob?.cancel()
                consecutiveJob = CoroutineScope(Dispatchers.IO).launch {
                    var roopTimes = 0
                    while (true) {
                        execTouchJob = CoroutineScope(Dispatchers.Main).launch touch@ {
                            withContext(Dispatchers.Main) {
                                val curSettingValue = textView?.autofillHints?.firstOrNull()
//                        FileSystems.writeFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lclickUpdate_inwithEdit.txt").absolutePath,
//                            listOf(
//                                "tag: ${tag}",
//                                "text: ${textView.text}",
//                                "settingValMap: ${editComponentListAdapter.initSettingValMap}",
//                                "cmdValMap: ${editComponentListAdapter.initCmdValMap}",
//                                "hint: ${textView.autofillHints?.firstOrNull()}"
//                            ).joinToString("\n")
//                        )
                                editComponentListAdapter.handleClickEvent(
                                    editListRecyclerView,
                                    tag,
                                    curSettingValue,
                                    editListPosition,
                                    frameOrLinearCon
                                )
                                EditListConfig.handle(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    editListRecyclerView,
//                                    true,
                                    selectedItemLineMap,
                                    frameOrLinearCon,
                                    editListPosition
                                )
                            }
                        }
                        withContext(Dispatchers.IO){
                            if(
                                roopTimes == 0
                            ) delay(300)
                            else delay(60)
                        }
                        roopTimes++
                    }
                }
                return
            }
        }

    }

    private fun makeSearchEditText(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        editComponentListAdapter: EditComponentListAdapter,
        searchText: AppCompatEditText,
        editListConfigMap: Map<String, String>?
    ) {
        val searchBoxMap = EditListConfig.getConfigKeyMap(
            editListConfigMap,
            EditListConfig.EditListConfigKey.SEARCH_BOX.key
        )
        val inVisible =
            searchBoxMap.get(
                SearchBoxSettingsForEditList.SearchBoxSettingKey.VISIBLE.key
            ) == SearchBoxSettingsForEditList.SearchBoxVisibleKey.OFF.name
        if(inVisible){
            searchText.isVisible = false
            return
        }
        searchText.hint = searchBoxMap.get(
            SearchBoxSettingsForEditList.SearchBoxSettingKey.HINT.key
        ).let {
            when(it.isNullOrEmpty()) {
                false -> SearchBoxSettingsForEditList.makeCurrentVariableValueInEditText(
                    fragment,
                    fannelInfoMap,
                    it
                )
                else -> TitleImageAndViewSetter.makeDefaultTitle(
                    fragment,
                    fannelInfoMap,
                    false,
                )
            }
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = ListSettingsForEditList.EditListMaker.makeLineMapListHandler(
                    editComponentListAdapter.fannelInfoMap,
                    editComponentListAdapter.setReplaceVariableMap,
                    editComponentListAdapter.editListMap,
                    editComponentListAdapter.busyboxExecutor,
                ).filter {
                    lineMap ->
                    val title = lineMap.get(
                        ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
                    ) ?: String()
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        title.lowercase()
                    )
                }
                ListViewToolForEditListAdapter.editListUpdateFileList(
                    editComponentListAdapter,
                    filteredUrlHistoryList,
                )
            }
        })
    }
}

