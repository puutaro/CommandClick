package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
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
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
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
import com.puutaro.commandclick.util.file.FileSystems
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
import java.lang.ref.WeakReference


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

    const val settingSectionStart = CommandClickScriptVariable.SETTING_SEC_START
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
        editFooterHorizonLayout: LinearLayoutCompat,
        verticalLinearListForFooter: List<LinearLayoutCompat?>,
        indexAndHorizonLinearListForFooter: List<List<LinearLayoutCompat?>>,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        fannelCenterButtonLayout: FrameLayout?,
        fannelContentsList: List<String>?,
    ) {
        val context = fragment.context
            ?: return
        runBlocking {
//            SettingActionManager.Companion.GlobalExitManager.init()
            SettingActionManager.Companion.BeforeActionImportMapManager.init()
        }
        val globalVarNameToValueMap = let {

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
                    globalVarNameToValueMap
        val editListConfigMap = editListConfigMapSrc?.map {
            val key = CmdClickMap.replace(
                it.key,
                globalVarNameToValueMap
            )
            val value = CmdClickMap.replace(
                it.value,
                globalVarNameToValueMap
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
        val layoutInflater = LayoutInflater.from(
                context
            )
        val requestBuilderSrc: RequestBuilder<Drawable>? =
            fragment.context?.let {
                Glide.with(it)
                    .asDrawable()
                    .sizeMultiplier(0.1f)
            }
        CoroutineScope(Dispatchers.Main).launch {
//            withContext(Dispatchers.Main) {
//                BkImageSettingsForEditList.makeBkImage(
//                    editListBkImage,
//                    editListBkPairs,
//                )
//            }
            val bkFrameLayout = withContext(Dispatchers.Main){
                val buttonFrameLayout = layoutInflater.inflate(
                    R.layout.icon_caption_layout_for_edit_list,
                    null
                ) as FrameLayout?
                BkImageSettingsForEditList.makeBkFrame(
                    context,
                    buttonFrameLayout,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    editListBkPairs,
                    requestBuilderSrc,
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
            WeakReference(fragment),
            layoutInflater,
            fannelInfoMap,
            setReplaceVariableMap + globalVarNameToValueMap,
            globalVarNameToValueMap,
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
                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap + globalVarNameToValueMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editListRecyclerView,
                editToolbarHorizonLayout,
                fannelCenterButtonLayout,
                editListConfigMap,
                requestBuilderSrc,
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            setFooter(
                fragment,
                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editFooterHorizonLayout,
                verticalLinearListForFooter,
                indexAndHorizonLinearListForFooter,
                editListRecyclerView,
                editListConfigMap,
                requestBuilderSrc,
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
        layoutInflater: LayoutInflater,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>,
        busyboxExecutor: BusyboxExecutor?,
        editFooterHorizonLayout: LinearLayoutCompat,
        verticalLinearListForFooter: List<LinearLayoutCompat?>,
        indexAndHorizonLinearListForFooter: List<List<LinearLayoutCompat?>>,
        editListRecyclerView: RecyclerView,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?
    ){
        setFooterOrToolbar(
            fragment,
            layoutInflater,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            editFooterHorizonLayout,
            verticalLinearListForFooter,
            indexAndHorizonLinearListForFooter,
            editListRecyclerView,
            null,
            null,
            editListConfigMap,
            requestBuilderSrc
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
        layoutInflater: LayoutInflater,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        fannelCentrButtonLayout: FrameLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?
    ){
        setFooterOrToolbar(
            fragment,
            layoutInflater,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            null,
            null,
            null,
            editListRecyclerView,
            editToolbarHorizonLayout,
            fannelCentrButtonLayout,
            editListConfigMap,
            requestBuilderSrc,
        )
    }

    private suspend fun setFooterOrToolbar(
        fragment: Fragment,
        layoutInflater: LayoutInflater,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editFooterHorizonLayout: LinearLayoutCompat?,
        verticalLinearListForFooter: List<LinearLayoutCompat?>?,
        indexAndHorizonLinearListForFooter: List<List<LinearLayoutCompat?>>?,
        editListRecyclerView: RecyclerView,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        fannelCenterButtonLayout: FrameLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?
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
        val isEditToolbar = editToolbarHorizonLayout != null
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
        val frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList = when(isOnlyCmdValEdit) {
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
        val frameMap = frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList?.first ?: emptyMap()
        val verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList =
            frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList?.second
                ?: Triple(emptyList(), emptyList(), emptyMap())
        val frameTagList = frameMap.keys
        val frameTagToVerticalKeysConList =
            verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList.first
        val verticalTagToHorizonKeysConList =
            verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList.second
        val horizonTagToContentsKeysListMap =
            verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList.third
//                .let {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "seframeMapListToLinearMapList_lin_listview.txt").absolutePath,
//                    listOf(
//                        "frameMap: ${frameMap}",
//                        "frameTagToVerticalKeysConList: ${frameTagToVerticalKeysConList}",
//                        "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
//                        "horizonTagToLinearKeysListMap: ${it}",
//                    ).joinToString("\n\n\n") + "\n\n===========\n\n"
//                )
//                it
//            }

        val tagKey = EditComponent.Template.EditComponentKey.TAG.key
        val typeSeparator = EditComponent.Template.typeSeparator
        val onConsecKey = EditComponent.Template.EditComponentKey.ON_CONSEC.key
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
//            val buttonFrameLayoutInflater = LayoutInflater.from(context)
            val frameTag = frameTagList.firstOrNull()
                ?: return@withContext
//            frameTagList.forEach { frameTag ->
            val isDuplicateFrameTagErr = withContext(Dispatchers.IO) frameTagCheck@ {
                val correctFrameTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                    context,
                    EditComponent.Template.TagManager.TagGenre.FRAME_TAG,
                    frameTag,
                    alreadyUseTagList,
                    "${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                    plusKeyToSubKeyConWhere,
                )
                correctFrameTag?.let {
                    alreadyUseTagList.add(it)
                }
                val isDuplidateTagErr = correctFrameTag.isNullOrEmpty()
                if(
                    isDuplidateTagErr
                ) return@frameTagCheck true
                false
            }
            if(isDuplicateFrameTagErr) return@withContext
            val verticalTagToKeyPairsListToVarNameValueMapList = withContext(Dispatchers.IO){
                EditComponent.AdapterSetter.makeLinearTagAndKeyPairsListToVarNameToValueMap(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    frameTag,
                    frameTagToVerticalKeysConList,
                    globalVarNameToValueMap ?: emptyMap(),
                    String(),
                    String(),
                    String(),
                    null,
                    String()
                )
            }
            val verticalLinerWeight = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.culcVerticalLinerWeight(
                    verticalTagToKeyPairsListToVarNameValueMapList
                )
            }
            val mapListElInfoForVertical =
                listOf(
                    debugWhere,
                    "frameTag: ${frameTag}",
                    plusKeyToSubKeyConWhere,
                ).joinToString(", ")
//                val verticalLinerWeight = weightSumFloat / verticalTagToKeyPairsListToVarNameValueMapList.size
            verticalTagToKeyPairsListToVarNameValueMapList.forEachIndexed setVertical@ {
                verticalIndex, verticalTagToKeyPairsListToVarNameToValueMap ->
                val verticalTag = verticalTagToKeyPairsListToVarNameToValueMap.first
                withContext(Dispatchers.IO){
                    EditComponent.AdapterSetter.tagDuplicateErrHandler(
                        context,
                        EditComponent.Template.TagManager.TagGenre.VERTICAL_TAG,
                        verticalTag,
                        alreadyUseTagList,
                        mapListElInfoForVertical,
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
                    when (editToolbarHorizonLayout != null) {
                        true -> null
                        else -> {
                            val readyVerticalLinearLayout =
                                verticalLinearListForFooter?.getOrNull(verticalIndex)
                            val verticalLinearLayoutSrc =
                                EditComponent.AdapterSetter.makeVerticalLinear(
                                    context,
                                    readyVerticalLinearLayout,
                                    null,
                                    verticalKeyPairs,
                                    verticalLinerWeight,
                                    verticalTag,
                                )
                            readyVerticalLinearLayout ?: let {
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "lreadyVerticalLinear_in_footer.txt").absolutePath,
//                                    listOf(
//                                        "frameTag: ${frameTag}",
//                                        "verticalTag: ${verticalTag}",
//                                        "verticalIndex: ${verticalIndex}",
//                                    ).joinToString("\n")
//                                )
                                editFooterHorizonLayout?.addView(verticalLinearLayoutSrc)
                            }
                            verticalLinearLayoutSrc
                        }
                    }
                }



                val horizonTagToKeyPairsListToVarNameToValueMapList = withContext(Dispatchers.IO){
                    EditComponent.AdapterSetter.makeLinearTagAndKeyPairsListToVarNameToValueMap(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        verticalTag,
                        verticalTagToHorizonKeysConList,
                        verticalVarNameToValueMap,
                        String(),
                        String(),
                        String(),
                        noIndexSign,
                        String(),
                    )
                }
                val readyHorizonLayoutList = indexAndHorizonLinearListForFooter?.getOrNull(verticalIndex)
                val horizonLayoutStartId = 55000
                val mapListElInfoForHorizon =
                    listOf(
                        "verticalTag: ${verticalTag}",
                        mapListElInfoForVertical,
                    ).joinToString(", ")
                horizonTagToKeyPairsListToVarNameToValueMapList.forEachIndexed setHorizon@ {
                        horizonIndex, horizonTagToKeyPairsListToVarNameToValueMap ->
                    val curExtraHorizonLinearId = horizonLayoutStartId + horizonIndex
                    val keyPairsListToVarNameToValueMapForHorizon = horizonTagToKeyPairsListToVarNameToValueMap.second
                    val horizonKeyPairs = keyPairsListToVarNameToValueMapForHorizon.first
                    val horizonVarNameToValueMap =
                        keyPairsListToVarNameToValueMapForHorizon.second + verticalVarNameToValueMap
                    val horizonTag = CmdClickMap.replace(
                        horizonTagToKeyPairsListToVarNameToValueMap.first,
                        horizonVarNameToValueMap,
                    )
                    val horizonTagDuplicateErr = withContext(Dispatchers.IO) horizonTagCeck@ {
                        val correctHorizonTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                            context,
                            EditComponent.Template.TagManager.TagGenre.HORIZON_TAG,
                            horizonTag,
                            alreadyUseTagList,
                            String(),
                            mapListElInfoForHorizon,
                        )
                        correctHorizonTag?.let {
                            alreadyUseTagList.add(it)
                        }
                        val isDuplidateTagErr = correctHorizonTag.isNullOrEmpty()
                        if(
                            isDuplidateTagErr
                        ) return@horizonTagCeck true
                        false
                    }
                    if(horizonTagDuplicateErr) return@setHorizon
                    withContext(Dispatchers.IO) {
                        EditComponent.AdapterSetter.isNotLinearKeyErr(
                            context,
                            EditComponent.Template.LayoutKey.HORIZON.key,
                            horizonKeyPairs,
                            String(),
                            "horizonTag: ${horizonTag}, ${mapListElInfoForHorizon}",
                        )
                    }.let {
                            isNotHorizonKeyErr ->
                        if(isNotHorizonKeyErr) return@setHorizon
                    }
                    val isHorizonEnable = withContext(Dispatchers.IO) {
                        PairListTool.getValue(
                            horizonKeyPairs,
                            enableKey,
                        ).let { enableStr ->
                            enableStr != switchOff
                        }
                    }
                    if(
                        !isHorizonEnable
                    ) return@setHorizon
                    val extractHorizonLayout =
                        readyHorizonLayoutList
                            ?.getOrNull(horizonIndex)
                            ?: verticalLinearLayout?.findViewById<LinearLayoutCompat>(
                                curExtraHorizonLinearId
                            )
                    val horizonLinearLayout = withContext(Dispatchers.Main) {
                        when (isEditToolbar) {
                            true -> editToolbarHorizonLayout
                            else -> {
                                let {
                                    extractHorizonLayout
                                        ?: LinearLayoutCompat(context).apply {
                                            id = curExtraHorizonLinearId
                                        }
                                }.apply {
                                    tag = frameTag
                                    EditComponent.AdapterSetter.makeHorizonLinear(
                                        context,
                                        extractHorizonLayout,
                                        null,
                                        horizonKeyPairs,
                                        horizonTag,
                                    )
                                }
                            }
                        }
                    }
                    if(extractHorizonLayout == null) {
                        verticalLinearLayout?.addView(horizonLinearLayout)
                    }
                    val horizonTagToContentsKeysListMapWithReplace = horizonTagToContentsKeysListMap.map {
                        val key = CmdClickMap.replace(
                            it.key,
                            horizonVarNameToValueMap,
                        )
                        key to it.value
                    }.toMap()
                    val contentsKeysList =
                        withContext(Dispatchers.IO) {
                            horizonTagToContentsKeysListMapWithReplace.get(horizonTag)
                                ?.let { contentsKeysListSrc ->
                                    when (isEditToolbar) {
                                        true -> listOf(
                                            contentsKeysListSrc.firstOrNull() ?: emptyList()
                                        )
                                        else -> contentsKeysListSrc
                                    }
                                }
                        }
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "smakeHorizonLinear_in_listview.txt").absolutePath,
//                        listOf(
//                            "verticalTag: ${verticalTag}",
//                            "isNotExtract: ${extractHorizonLayout == null}",
//                            "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
//                            "horizonKeyPairs: ${horizonKeyPairs}",
//                            "horizonTag: ${horizonTag}",
//                            "horizonTagToContentsKeysListMapWithReplace: ${horizonTagToContentsKeysListMapWithReplace}",
//                            "horizonKeyPairs: ${horizonKeyPairs}",
//                            "contentsKeysList: ${contentsKeysList}"
//                        ).joinToString("\n\n\n") + "\n\n======\n\n"
//                    )


                    contentsKeysList?.forEachIndexed setContents@ { contentsIndex, contentsKeyValues ->
                        val contentsTagToKeyPairsList = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.makeLinearFrameTagToKeyPairsList(
                                contentsKeyValues,
                                horizonVarNameToValueMap,
                                frameTag,
                                frameTag,
                                frameTag,
                                noIndexSign,
                            )
                        }
                        val contentsKeyValueSize = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.culcLinearKeyValueSize(
                                contentsTagToKeyPairsList,
                            )
                        }
                        val layoutWeight = withContext(Dispatchers.IO) culcFrameLayoutWeight@ {
                            when (isEditToolbar && !isOnlyCmdValEdit) {
                                true -> weightSumFloat / (contentsKeyValueSize + 1)
                                else -> {
                                    if(
                                        contentsKeyValueSize == 0
                                    ) return@culcFrameLayoutWeight 0f
                                    weightSumFloat / contentsKeyValueSize
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
                        var contentsVarNameToValueMap = horizonVarNameToValueMap
                        contentsKeyValues.forEachIndexed execSetContents@{
                                execSetContentsIndex, contentsKeyPairsListConSrc ->
                            val contentsTagSrc = contentsTagToKeyPairsList.getOrNull(execSetContentsIndex)
                            val mapListElInfoForContentsTag =
                                listOf(
                                    "contentsTagSrc: ${contentsTagSrc}",
                                    "horizonTag: ${horizonTag}",
                                    mapListElInfoForHorizon
                                ).joinToString(", ")
                            val varNameToValueMap = withContext(Dispatchers.IO) updateLinearKeyParsListCon@ {
                                EditComponent.AdapterSetter.makeFrameVarNameToValueMap(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    editComponentListAdapter,
                                    contentsVarNameToValueMap,
                                    mapListElInfoForContentsTag,
                                    contentsKeyPairsListConSrc,
                                    frameTag,
                                    frameTag,
                                    frameTag,
                                    noIndexSign,
                                )
                            }
                            contentsVarNameToValueMap += varNameToValueMap
                            val linearFrameKeyPairsListCon = CmdClickMap.replace(
                                contentsKeyPairsListConSrc,
                                contentsVarNameToValueMap
                            )
                            val contentsKeyPairsList = withContext(Dispatchers.IO) {
                                CmdClickMap.createMap(
                                    linearFrameKeyPairsListCon,
                                    typeSeparator
                                )
                            }
                            val contentsTag = withContext(Dispatchers.IO) {
                                PairListTool.getValue(
                                    contentsKeyPairsList,
                                    tagKey,
                                )?.let {
                                    CmdClickMap.replace(
                                        it,
                                        contentsVarNameToValueMap
                                    )
                                } ?: String()
                            }
                            val mapListElInfoForContentsTagWithReplace =
                                listOf(
                                    "contentsTag: ${contentsTag}",
                                    "horizonTag: ${horizonTag}",
                                    mapListElInfoForHorizon
                                ).joinToString(", ")
                            val isContentsTagErr = withContext(Dispatchers.IO)contentsTagCheck@ {
                                val tagGenre = EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
                                val isTagBlankErr = ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
                                    context,
                                    contentsTag,
                                    mapListElInfoForContentsTagWithReplace,
                                    tagGenre
                                )
                                if(
                                    isTagBlankErr
                                ) return@contentsTagCheck true
                                val correctContentsTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                    context,
                                    tagGenre,
                                    contentsTag,
                                    alreadyUseTagList,
                                    mapListElInfoForContentsTagWithReplace,
                                    String(),
                                )
                                correctContentsTag?.let {
                                    alreadyUseTagList.add(it)
                                }
                                val isDuplidateTagErr = correctContentsTag.isNullOrEmpty()
                                if(
                                    isDuplidateTagErr
                                ) return@contentsTagCheck true
                                false
                            }
                            if(isContentsTagErr){
                                return@setVertical
                            }


                            editComponentListAdapter?.footerKeyPairListConMap?.put(
                                contentsTag,
                                linearFrameKeyPairsListCon
                            )
                            val linearFrameLayout = withContext(Dispatchers.Main) setLinearFrameLayout@ {
                                PairListTool.getValue(
                                    contentsKeyPairsList,
                                    enableKey,
                                ).let {
                                        enableStr ->
                                    if(
                                        enableStr == switchOff
                                    ) return@setLinearFrameLayout null
                                }
                                val buttonFrameLayout = layoutInflater.inflate(
                                    R.layout.icon_caption_layout_for_edit_list,
                                    null
                                ) as FrameLayout
                                horizonLinearLayout?.addView(buttonFrameLayout)
                                CoroutineScope(Dispatchers.Main).launch {
                                    EditFrameMaker.make(
                                        context,
                                        buttonFrameLayout,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        contentsKeyPairsList,
                                        0,
                                        layoutWeight,
                                        contentsTag,
                                        editComponentListAdapter?.totalSettingValMap,
                                        requestBuilderSrc
                                    )
                                }
                                buttonFrameLayout
                            } ?: return@execSetContents
                            CoroutineScope(Dispatchers.IO).launch execClick@ {
                                if (
                                    linearFrameKeyPairsListCon.isNullOrEmpty()
                                ) return@execClick
                                val onClick = withContext(Dispatchers.IO) {
                                    PairListTool.getValue(
                                        contentsKeyPairsList,
                                        onClickKey,
                                    ) != switchOff
                                }
                                val isConsec = withContext(Dispatchers.IO) {
                                    PairListTool.getValue(
                                        contentsKeyPairsList,
                                        onConsecKey,
                                    ) == EditComponent.Template.switchOn
                                }
                                val isNotClickSetting = withContext(Dispatchers.IO) {
                                    jsActionKeyList.any { jsActionKey ->
                                        !PairListTool.getValue(
                                            contentsKeyPairsList,
                                            jsActionKey,
                                        ).isNullOrEmpty()
                                    }.let { isJsAc ->
                                        val isJsAcClick = !isJsAc
                                                && linearFrameLayout.tag != null
                                        isJsAcClick
                                                || !onClick
                                    }
                                }
                                if(
                                    isNotClickSetting
                                ) return@execClick
                                withContext(Dispatchers.Main) execExecClick@{
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
                    }

                }
            }
//            }
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
                    val frameOrLinearConSrc = runBlocking {
                        holder.getKeyPairListConMap().get(tag)
                    }
                    val frameOrLinearCon = EditComponent.Template.ReplaceHolder.replaceHolder(
                        frameOrLinearConSrc,
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
                            fragment,
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
                val frameOrLinearCon = runBlocking {
                    holder.getKeyPairListConMap().get(tag)
                } ?: return
//                val frameOrLinearCon = holder.keyPairListConMap.get(tag)
//                    ?: return
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
                                    fragment,
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

