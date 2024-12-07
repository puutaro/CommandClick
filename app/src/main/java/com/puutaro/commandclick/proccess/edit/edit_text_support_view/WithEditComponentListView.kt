package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
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
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import com.puutaro.commandclick.util.str.SnakeCamelTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference


object WithEditComponentListView{
    
    private val delayTime = 1500L

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

    suspend fun create(
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
        verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter: List<List<List<FrameLayout?>>>,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        fannelCenterButtonLayout: FrameLayout?,
        fannelContentsList: List<String>?,
        density: Float,
        requestBuilderSrc: RequestBuilder<Drawable>?
    ) {
        val context = fragment.context
            ?: return
        withContext(Dispatchers.IO) {
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
        withContext(Dispatchers.IO) {
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
        val setReplaceVariableMap = withContext(Dispatchers.IO) {
            (setReplaceVariableMapSrc ?: emptyMap()) +
                    globalVarNameToValueMap
        }
        val editListConfigMap = withContext(Dispatchers.IO) {
            editListConfigMapSrc?.map {
                val key = CmdClickMap.replace(
                    it.key,
                    globalVarNameToValueMap
                )
                val value = CmdClickMap.replace(
                    it.value,
                    globalVarNameToValueMap
                )
                key to value
            }
        }?.toMap()
        CoroutineScope(Dispatchers.IO).launch{
            val titleLayoutPathKey = EditListConfig.EditListConfigKey.TITLE_LAYOUT_PATH.key
            val titleSettingPath = withContext(Dispatchers.IO) {
                editListConfigMap?.get(
                    titleLayoutPathKey
                )
            } ?: String()
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
                titleSettingMap,
                requestBuilderSrc
            )
        }
        val editListBkPairs = withContext(Dispatchers.IO) {
            EditListConfig.getConfigKeyConList(
                editListConfigMap,
                EditListConfig.EditListConfigKey.BK.key
            )
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
        val editComponentListAdapter = withContext(Dispatchers.IO) {
            val layoutInflater = LayoutInflater.from(
                context
            )
            EditComponentListAdapter(
                WeakReference(fragment),
                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap + globalVarNameToValueMap,
                globalVarNameToValueMap,
                editListConfigMap,
                busyboxExecutor,
                indexListMap,
                lineMapList,
                fannelContentsList,
                density
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                editListRecyclerView.adapter = editComponentListAdapter
            }
        }

        val layoutConfigMap = withContext(Dispatchers.IO) {
            LayoutSettingsForEditList.getLayoutConfigMap(
                editListConfigMap,
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                ItemTouchHelperCallbackForEditListAdapter.set(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    editListRecyclerView,
                    editComponentListAdapter,
                    layoutConfigMap
                )
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                val isReverseLayout = withContext(Dispatchers.IO) {
                    LayoutSettingsForEditList.howReverseLayout(
                        fannelInfoMap,
                        setReplaceVariableMap,
                        layoutConfigMap
                    )
                }
                LayoutSettingsForEditList.setLayout(
                    context,
                    editComponentListAdapter.getLayoutConfigMap(),
                    editListRecyclerView,
                    isReverseLayout,
                )
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                var prevYposi = 0f
                for(i in 1..10){
                    val curYPosi = editListRecyclerView.y
                    if(
                        prevYposi != 0f
                        && prevYposi == curYPosi
                    ) break
                    prevYposi = curYPosi
                    delay(100)
                }
            }
//            withContext(Dispatchers.Main) {
//                editListRecyclerView.scrollToPosition(0)
//            }
            withContext(Dispatchers.IO){
                SettingActionManager.Companion.BeforeActionImportMapManager.init()
//                SettingActionManager.Companion.GlobalExitManager.init()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                BkImageSettingsForEditList.makeBkImage(
//                    editListBkImage,
//                    editListBkPairs,
//                )
//            }
           withContext(Dispatchers.Main){
//                val buttonFrameLayout = layoutInflater.inflate(
//                    R.layout.icon_caption_layout_for_edit_list,
//                    null
//                ) as FrameLayout?
                BkImageSettingsForEditList.makeBkFrame(
                    context,
                    editListBkFrame,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    editListBkPairs,
                    requestBuilderSrc,
                    density,
                )
            }
//            withContext(Dispatchers.Main) {
//                editListBkFrame.addView(bkFrameLayout)
//            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            setToolbar(
                fragment,
//                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap + globalVarNameToValueMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editListRecyclerView,
                editToolbarHorizonLayout,
                fannelCenterButtonLayout,
                editListConfigMap,
                requestBuilderSrc,
                density,
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            setFooter(
                fragment,
//                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editFooterHorizonLayout,
                verticalLinearListForFooter,
                indexAndHorizonLinearListForFooter,
                verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter,
                editListRecyclerView,
                editListConfigMap,
                requestBuilderSrc,
                density,
            )
        }
        CoroutineScope(Dispatchers.IO).launch{
            makeSearchEditText(
                fragment,
                fannelInfoMap,
                editComponentListAdapter,
                editListSearchEditText,
                editListConfigMap
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            invokeItemSetClickListenerForFileList(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                editComponentListAdapter
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            invokeItemSetTouchListenerForFileList(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                editComponentListAdapter
            )
        }
    }

    private suspend fun setFooter(
        fragment: Fragment,
//        layoutInflater: LayoutInflater,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>,
        busyboxExecutor: BusyboxExecutor?,
        editFooterHorizonLayout: LinearLayoutCompat,
        verticalLinearListForFooter: List<LinearLayoutCompat?>,
        indexAndHorizonLinearListForFooter: List<List<LinearLayoutCompat?>>,
        verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter: List<List<List<FrameLayout?>>>,
        editListRecyclerView: RecyclerView,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ){
        setFooterOrToolbar(
            fragment,
//            layoutInflater,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            editFooterHorizonLayout,
            verticalLinearListForFooter,
            indexAndHorizonLinearListForFooter,
            verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter,
            editListRecyclerView,
            null,
            null,
            editListConfigMap,
            requestBuilderSrc,
            density,
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
//        layoutInflater: LayoutInflater,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        fannelCentrButtonLayout: FrameLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ){
        setFooterOrToolbar(
            fragment,
//            layoutInflater,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            null,
            null,
            null,
            null,
            editListRecyclerView,
            editToolbarHorizonLayout,
            fannelCentrButtonLayout,
            editListConfigMap,
            requestBuilderSrc,
            density,
        )
    }

    private suspend fun setFooterOrToolbar(
        fragment: Fragment,
//        layoutInflater: LayoutInflater,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editFooterHorizonLayout: LinearLayoutCompat?,
        verticalLinearListForFooter: List<LinearLayoutCompat?>?,
        indexAndHorizonLinearListForFooter: List<List<LinearLayoutCompat?>>?,
        verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter: List<List<List<FrameLayout?>>>?,
        editListRecyclerView: RecyclerView,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        fannelCenterButtonLayout: FrameLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
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
        val alreadyUseTagListMutex = Mutex()
        val alreadyUseTagList = mutableListOf<String>()
        val noIndexSign = -1
        val weightSumFloat = 1f
        val frameTag = frameTagList.firstOrNull()
            ?: return
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
        if(isDuplicateFrameTagErr) return
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
        val verticalChannel = Channel<
                Pair<
                        Int,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat?,
                                >
                        >
                >(verticalTagToKeyPairsListToVarNameValueMapList.size)
        val horizonChannel = Channel<
                Triple<
                        String,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat?,
                                >,
                        Pair<
                                String,
                                List<List<FrameLayout?>>?,
                                >,
                        >
                >(100)
        CoroutineScope(Dispatchers.IO).launch {
            makeVerticalUseList(
                context,
                alreadyUseTagList,
                alreadyUseTagListMutex,
                editToolbarHorizonLayout,
                editFooterHorizonLayout,
                verticalChannel,
                verticalTagToKeyPairsListToVarNameValueMapList,
                verticalLinearListForFooter,
                mapListElInfoForVertical,
                plusKeyToSubKeyConWhere,
                verticalLinerWeight,
                density
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val asyncTaskList = mutableListOf<Deferred<Unit>>()
            for (indexToVerticalUse in verticalChannel) {
                val verticalUse = indexToVerticalUse.second
                val job = async {
                    val verticalIndexToTag = verticalUse.first
                    val verticalIndex = verticalIndexToTag.first
                    val verticalTag = verticalIndexToTag.second
                    val verticalVarNameToValueMap = verticalUse.second
                    val verticalLinearLayout = verticalUse.third
                    val horizonTagToKeyPairsListToVarNameToValueMapList = withContext(Dispatchers.IO) {
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
                    val readyHorizonLayoutList =
                        indexAndHorizonLinearListForFooter?.getOrNull(verticalIndex)
                    val mapListElInfoForHorizon =
                        listOf(
                            "verticalTag: ${verticalTag}",
                            mapListElInfoForVertical,
                        ).joinToString(", ")
                    val horizonIndexAndReadyContentsLayoutListForFooter =
                        verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter?.getOrNull(
                            verticalIndex
                        )
                    withContext(Dispatchers.IO) {
                        makeHorizonUseList(
                            context,
                            frameTag,
                            verticalIndex,
                            isEditToolbar,
                            verticalLinearLayout,
                            verticalVarNameToValueMap,
                            horizonChannel,
                            horizonTagToKeyPairsListToVarNameToValueMapList,
                            mapListElInfoForHorizon,
                            readyHorizonLayoutList,
                            horizonIndexAndReadyContentsLayoutListForFooter,
                            editToolbarHorizonLayout,
                            alreadyUseTagList,
                            alreadyUseTagListMutex,
                            density,
                        )
                    }
                }
                asyncTaskList.add(job)
            }
            asyncTaskList.forEach {
                it.await()
            }
            horizonChannel.close()
        }

        CoroutineScope(Dispatchers.IO).launch {
            for (registerIndexToHorizonUseToExtraInfo in horizonChannel) {
                CoroutineScope(Dispatchers.IO).launch {
                    val horizonUse = registerIndexToHorizonUseToExtraInfo.second
                    val horizonIndexToTag = horizonUse.first
                    val horizonIndex = horizonIndexToTag.first
                    val horizonTag = horizonIndexToTag.second
                    val horizonVarNameToValueMap = horizonUse.second
                    val horizonLinearLayout = horizonUse.third
                    val extraInfo = registerIndexToHorizonUseToExtraInfo.third
                    val mapListElInfoForHorizon = extraInfo.first
                    val horizonIndexAndReadyContentsLayoutListForFooter =
                        extraInfo.second
                    val horizonTagToContentsKeysListMapWithReplace =
                        horizonTagToContentsKeysListMap.map {
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

                    val readyContentsLayoutListForFooter =
                        horizonIndexAndReadyContentsLayoutListForFooter?.getOrNull(horizonIndex)
                    contentsKeysList?.forEachIndexed setContents@{ contentsIndex, contentsKeyValues ->
                        CoroutineScope(Dispatchers.IO).launch {
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
                            val layoutWeight = withContext(Dispatchers.IO) culcFrameLayoutWeight@{
                                when (isEditToolbar && !isOnlyCmdValEdit) {
                                    true -> weightSumFloat / (contentsKeyValueSize + 1)
                                    else -> {
                                        if (
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
                            contentsKeyValues.forEachIndexed execSetContents@{ execSetContentsIndex, contentsKeyPairsListConSrc ->
                                CoroutineScope(Dispatchers.IO).launch execSetContentsCoroutine@{
                                    val contentsTagSrc =
                                        contentsTagToKeyPairsList.getOrNull(execSetContentsIndex)
                                    val mapListElInfoForContentsTag =
                                        listOf(
                                            "contentsTagSrc: ${contentsTagSrc}",
                                            "horizonTag: ${horizonTag}",
                                            mapListElInfoForHorizon
                                        ).joinToString(", ")
                                    val varNameToValueMap =
                                        withContext(Dispatchers.IO) updateLinearKeyParsListCon@{
                                            EditComponent.AdapterSetter.makeFrameVarNameToValueMap(
                                                fragment,
                                                fannelInfoMap,
                                                setReplaceVariableMap,
                                                busyboxExecutor,
                                                editComponentListAdapter,
                                                horizonVarNameToValueMap,
                                                mapListElInfoForContentsTag,
                                                contentsKeyPairsListConSrc,
                                                frameTag,
                                                frameTag,
                                                frameTag,
                                                noIndexSign,
                                            )
                                        }
                                    val contentsVarNameToValueMap = horizonVarNameToValueMap + varNameToValueMap
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
                                    val isContentsTagErr =
                                        withContext(Dispatchers.IO) contentsTagCheck@{
                                            val tagGenre =
                                                EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
                                            val isTagBlankErr =
                                                ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
                                                    context,
                                                    contentsTag,
                                                    mapListElInfoForContentsTagWithReplace,
                                                    tagGenre
                                                )
                                            if (
                                                isTagBlankErr
                                            ) return@contentsTagCheck true
                                            val alreadyUseTagListSrc =
                                                EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                                                    alreadyUseTagList,
                                                    alreadyUseTagListMutex
                                                )
                                            val correctContentsTag =
                                                EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                                    context,
                                                    tagGenre,
                                                    contentsTag,
                                                    alreadyUseTagListSrc,
                                                    mapListElInfoForContentsTagWithReplace,
                                                    String(),
                                                )
                                            correctContentsTag?.let {
                                                alreadyUseTagListMutex.withLock {
                                                    alreadyUseTagList.add(it)
                                                }
                                            }
                                            val isDuplidateTagErr =
                                                correctContentsTag.isNullOrEmpty()
                                            if (
                                                isDuplidateTagErr
                                            ) return@contentsTagCheck true
                                            false
                                        }
                                    if (isContentsTagErr) {
                                        return@execSetContentsCoroutine
                                    }


                                    editComponentListAdapter?.footerKeyPairListConMap?.put(
                                        contentsTag,
                                        linearFrameKeyPairsListCon
                                    )
                                    val contentsFrameLayout =
                                        withContext(Dispatchers.Main) setLinearFrameLayout@{
                                            withContext(Dispatchers.IO) {
                                                PairListTool.getValue(
                                                    contentsKeyPairsList,
                                                    enableKey,
                                                )
                                            }.let { enableStr ->
                                                if (
                                                    enableStr == switchOff
                                                ) return@setLinearFrameLayout null
                                            }
                                            val extractContentsLayout =
                                                when (isEditToolbar) {
                                                    false -> readyContentsLayoutListForFooter
                                                        ?.getOrNull(execSetContentsIndex)

                                                    else -> null
                                                }
                                            val contentsLayout =
                                                extractContentsLayout
                                                    ?: let {
                                                        withContext(Dispatchers.Main) {
                                                            EditComponent.AdapterSetter.makeContentsFrameLayout(
                                                                context
                                                            )
                                                        }
                                                    }
                                            if (extractContentsLayout == null) {
                                                withContext(Dispatchers.Main) {
                                                    horizonLinearLayout?.addView(contentsLayout)
                                                }
                                            }
                                            CoroutineScope(Dispatchers.Main).launch {
                                                EditFrameMaker.make(
                                                    context,
                                                    contentsLayout,
                                                    fannelInfoMap,
                                                    setReplaceVariableMap,
                                                    busyboxExecutor,
                                                    contentsKeyPairsList,
                                                    0,
                                                    layoutWeight,
                                                    contentsTag,
                                                    editComponentListAdapter?.totalSettingValMap,
                                                    requestBuilderSrc,
                                                    density,
                                                )
                                            }
//                                    val makedContents = contentsLayout.layoutParams as LinearLayoutCompat.LayoutParams
//                                    FileSystems.updateFile(
//                                        File(UsePath.cmdclickDefaultAppDirPath, "leditFrame.txt").absolutePath,
//                                        listOf(
//                                            "tag: ${contentsLayout.tag}",
//                                            "contentsTag: ${contentsTag}",
//                                            "weight: ${makedContents.weight}",
//                                            "width: ${makedContents.width}",
//                                        ).joinToString("\n") + "\n\n===========\n\n"
//                                    )
                                            contentsLayout
                                        } ?: return@execSetContentsCoroutine
                                    CoroutineScope(Dispatchers.IO).launch execClick@{
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
                                                        && contentsFrameLayout.tag != null
                                                isJsAcClick
                                                        || !onClick
                                            }
                                        }
                                        if (
                                            isNotClickSetting
                                        ) return@execClick
                                        withContext(Dispatchers.IO) execExecClick@{
                                            val outValue = TypedValue()
                                            context.theme.resolveAttribute(
                                                android.R.attr.selectableItemBackground,
                                                outValue,
                                                true
                                            )
                                            val clickViewList =
                                                contentsFrameLayout.children.filter {
                                                    it is OutlineTextView
                                                            || it is AppCompatImageView
                                                }
                                            clickViewList.forEach { clickView ->
                                                withContext(Dispatchers.Main) {
                                                    clickView.setBackgroundResource(outValue.resourceId)
                                                    clickView.isClickable = true
                                                }
                                                if (!isConsec) {
                                                    withContext(Dispatchers.Main) {
                                                        clickView.setOnClickListener {
                                                            execJsAction(
                                                                fragment,
                                                                fannelInfoMap,
                                                                setReplaceVariableMap,
                                                                editListRecyclerView,
                                                                linearFrameKeyPairsListCon
                                                            )
                                                        }
                                                    }
                                                    return@execExecClick
                                                }
                                                withContext(Dispatchers.Main) {
                                                    withContext(Dispatchers.IO) {
                                                        delay(1500)
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
                                                                                withContext(
                                                                                    Dispatchers.IO
                                                                                ) {
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
                    }
                }
            }
        }
    }

    private suspend fun makeVerticalUseList(
        context: Context,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        editFooterHorizonLayout: LinearLayoutCompat?,
        verticalChannel: Channel<
                Pair<
                        Int,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat?
                                >
                        >
                >,
        verticalTagToKeyPairsListToVarNameValueMapList: List<Pair<String, Pair<List<Pair<String, String>>, Map<String, String>>>>,
        verticalLinearListForFooter: List<LinearLayoutCompat?>?,
        mapListElInfoForVertical: String,
        plusKeyToSubKeyConWhere: String,
        verticalLinerWeight: Float,
        density: Float
    ) {
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        withContext(Dispatchers.IO) {
            val jobList = verticalTagToKeyPairsListToVarNameValueMapList.mapIndexed setVertical@{ verticalIndex, verticalTagToKeyPairsListToVarNameToValueMap ->
                async {
                    val verticalTag = verticalTagToKeyPairsListToVarNameToValueMap.first
                    val keyPairsListToVarNameToValueMap =
                        verticalTagToKeyPairsListToVarNameToValueMap.second
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
                    if (
                        !isVerticalEnable
                    ) return@async

                    val correctContentsTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                        context,
                        EditComponent.Template.TagManager.TagGenre.VERTICAL_TAG,
                        verticalTag,
                        alreadyUseTagList,
                        mapListElInfoForVertical,
                        plusKeyToSubKeyConWhere,
                    )
                    correctContentsTag?.let {
                        alreadyUseTagListMutex.withLock {
                            alreadyUseTagList.add(it)
                        }
                    }
                    val isDuplicateTagErr =
                        correctContentsTag.isNullOrEmpty()
                    if (
                        isDuplicateTagErr
                    ) return@async
                    EditComponent.AdapterSetter.isNotLinearKeyErr(
                        context,
                        EditComponent.Template.LayoutKey.VERTICAL.key,
                        verticalKeyPairs,
                        "verticalTag: ${verticalTag}, ${mapListElInfoForVertical}",
                        String(),
                    ).let { isNotVerticalKeyErr ->
                        if (isNotVerticalKeyErr) return@async
                    }

                    val verticalLinearLayout = withContext(Dispatchers.Main) {
                        when (editToolbarHorizonLayout != null) {
                            true -> null
                            else -> {
                                val extractVerticalLinearLayout =
                                    verticalLinearListForFooter?.getOrNull(verticalIndex)
                                val verticalLinearLayoutSrc =
                                    extractVerticalLinearLayout ?: let {
                                        LinearLayoutCompat(context).apply {
                                            val verticalParams = withContext(Dispatchers.IO){
                                                LinearLayoutCompat.LayoutParams(
                                                    0,
                                                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                                                ).apply {
                                                    weight = verticalLinerWeight
                                                }
                                            }
                                            layoutParams = verticalParams
                                        }
                                    }
                                extractVerticalLinearLayout ?: let {
                                    editFooterHorizonLayout?.addView(verticalLinearLayoutSrc)
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    EditComponent.AdapterSetter.setVerticalLinear(
                                        context,
                                        verticalLinearLayoutSrc,
                                        verticalKeyPairs,
                                        verticalLinerWeight,
                                        verticalTag,
                                        density,
                                    )
                                }

//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "lvertical.txt").absolutePath,
//                                    listOf(
//                                        "verticalLinearLayoutSrc: ${verticalLinearLayoutSrc == null}",
//                                        "verticalLinearLayoutSrc: ${verticalLinearLayoutSrc.tag}",
//                                        "verticalLinearLayoutSrc: ${verticalLinearLayoutSrc.weightSum}",
//                                        "width: ${lp.width}",
//                                        "height: ${lp.height}",
//                                        "weight: ${lp.weight}",
//                                    ).joinToString("\n")
//                                )
                                verticalLinearLayoutSrc
                            }
                        }
                    }
                    withContext(Dispatchers.IO) {
                        verticalChannel.send(
                            Pair(
                                verticalIndex,
                                Triple(
                                    Pair(
                                        verticalIndex,
                                        verticalTag
                                    ),
                                    verticalVarNameToValueMap.toMap(),
                                    verticalLinearLayout,
                                )
                            )
                        )
                    }
                }
            }
            jobList.forEach { it.await() }
            verticalChannel.close()
        }
    }

    private suspend fun makeHorizonUseList(
        context: Context,
        frameTag: String,
        verticalIndex: Int,
        isEditToolbar: Boolean,
        verticalLinearLayout: LinearLayoutCompat?,
        verticalVarNameToValueMap: Map<String, String>,
        horizonChannel: Channel<
                Triple<
                        String,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat?,
                                >,
                        Pair<
                                String,
                                List<List<FrameLayout?>>?,
                                >,
                        >
                >,
        horizonTagToKeyPairsListToVarNameToValueMapList: List<Pair<String, Pair<List<Pair<String, String>>, Map<String, String>>>>,
        mapListElInfoForHorizon: String,
        readyHorizonLayoutList: List<LinearLayoutCompat?>?,
        horizonIndexAndReadyContentsLayoutListForFooter: List<List<FrameLayout?>>?,
        editToolbarHorizonLayout: LinearLayoutCompat?,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        density: Float,
    ) {
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        val weightSumFloat = 1f
        val horizonLayoutStartId = 55000
        withContext(Dispatchers.IO) {
            val jobList =horizonTagToKeyPairsListToVarNameToValueMapList.mapIndexed setHorizon@{ horizonIndex, horizonTagToKeyPairsListToVarNameToValueMap ->
                async {
                    val curExtraHorizonLinearId = horizonLayoutStartId + horizonIndex
                    val keyPairsListToVarNameToValueMapForHorizon =
                        horizonTagToKeyPairsListToVarNameToValueMap.second
                    val horizonKeyPairs = keyPairsListToVarNameToValueMapForHorizon.first
                    val horizonVarNameToValueMap =
                        keyPairsListToVarNameToValueMapForHorizon.second + verticalVarNameToValueMap
                    val horizonTag = CmdClickMap.replace(
                        horizonTagToKeyPairsListToVarNameToValueMap.first,
                        horizonVarNameToValueMap,
                    )
                    val horizonTagDuplicateErr = withContext(Dispatchers.IO) horizonTagCeck@{
                        val alreadyUseTagListSrc = EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                            alreadyUseTagList,
                            alreadyUseTagListMutex
                        )
                        val correctHorizonTag =
                            EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                context,
                                EditComponent.Template.TagManager.TagGenre.HORIZON_TAG,
                                horizonTag,
                                alreadyUseTagListSrc,
                                String(),
                                mapListElInfoForHorizon,
                            )
                        correctHorizonTag?.let {
                            alreadyUseTagListMutex.withLock {
                                alreadyUseTagList.add(it)
                            }
                        }
                        val isDuplidateTagErr = correctHorizonTag.isNullOrEmpty()
                        if (
                            isDuplidateTagErr
                        ) return@horizonTagCeck true
                        false
                    }
                    if (horizonTagDuplicateErr) return@async
                    withContext(Dispatchers.IO) {
                        EditComponent.AdapterSetter.isNotLinearKeyErr(
                            context,
                            EditComponent.Template.LayoutKey.HORIZON.key,
                            horizonKeyPairs,
                            String(),
                            "horizonTag: ${horizonTag}, ${mapListElInfoForHorizon}",
                        )
                    }.let { isNotHorizonKeyErr ->
                        if (isNotHorizonKeyErr) return@async
                    }
                    val isHorizonEnable = withContext(Dispatchers.IO) {
                        PairListTool.getValue(
                            horizonKeyPairs,
                            enableKey,
                        ).let { enableStr ->
                            enableStr != switchOff
                        }
                    }
                    if (
                        !isHorizonEnable
                    ) return@async
                    val extractHorizonLayout =
                        readyHorizonLayoutList
                            ?.getOrNull(horizonIndex)
                            ?: withContext(Dispatchers.Main) {
                                verticalLinearLayout?.findViewById<LinearLayoutCompat>(
                                    curExtraHorizonLinearId
                                )
                            }
                    val horizonLinearLayout = withContext(Dispatchers.Main) {
                        when (isEditToolbar) {
                            true -> editToolbarHorizonLayout
                            else -> {
                                let {
                                    extractHorizonLayout
                                        ?: LinearLayoutCompat(context).apply {
                                            id = curExtraHorizonLinearId
                                            val horizonParams = withContext(Dispatchers.IO){
                                                LinearLayoutCompat.LayoutParams(
                                                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                                                )
                                            }
                                            layoutParams = horizonParams
                                        }
                                }.apply {
                                    tag = frameTag
                                    weightSum = weightSumFloat
                                }
                            }
                        }
                    }
                    if (extractHorizonLayout == null) {
                        withContext(Dispatchers.Main) {
                            verticalLinearLayout?.addView(horizonLinearLayout)
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        EditComponent.AdapterSetter.setHorizonLinear(
                            context,
                            extractHorizonLayout,
                            horizonKeyPairs,
                            horizonTag,
                            density,
                        )
                    }
                    withContext(Dispatchers.IO) {
                        val registerIndex = "${verticalIndex}${horizonIndex}"
                        horizonChannel.send(
                            Triple(
                                registerIndex,
                                Triple(
                                    Pair(
                                        horizonIndex,
                                        horizonTag
                                    ),
                                    horizonVarNameToValueMap.toMap(),
                                    horizonLinearLayout,
                                ),
                                Pair(
                                    mapListElInfoForHorizon,
                                    horizonIndexAndReadyContentsLayoutListForFooter,
                                )
                            )
                        )
                    }
                }
            }
            jobList.forEach { it.await() }
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

    private suspend fun invokeItemSetClickListenerForFileList(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editComponentListAdapter: EditComponentListAdapter,
    ) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
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


    private suspend fun invokeItemSetTouchListenerForFileList(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editComponentListAdapter: EditComponentListAdapter,
    ) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
        var execTouchJob: Job? = null
        var consecutiveJob: Job? = null
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

    private suspend fun makeSearchEditText(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        editComponentListAdapter: EditComponentListAdapter,
        searchText: AppCompatEditText,
        editListConfigMap: Map<String, String>?
    ) {
        val searchBoxMap = withContext(Dispatchers.IO) {
            EditListConfig.getConfigKeyMap(
                editListConfigMap,
                EditListConfig.EditListConfigKey.SEARCH_BOX.key
            )
        }
        val onVisible =
            withContext(Dispatchers.IO) {
                searchBoxMap.get(
                    SearchBoxSettingsForEditList.SearchBoxSettingKey.VISIBLE.key
                ) != SearchBoxSettingsForEditList.SearchBoxVisibleKey.OFF.name
            }
        if(!onVisible) {
            withContext(Dispatchers.Main) {
                searchText.isVisible = false
            }
            return
        }
        withContext(Dispatchers.Main) {
            searchText.isVisible = true
        }

        withContext(Dispatchers.Main) {
            searchText.hint = searchBoxMap.get(
                SearchBoxSettingsForEditList.SearchBoxSettingKey.HINT.key
            ).let {
                when (it.isNullOrEmpty()) {
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
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (!searchText.hasFocus()) return
                    val filteredUrlHistoryList =
                        ListSettingsForEditList.EditListMaker.makeLineMapListHandler(
                            editComponentListAdapter.fannelInfoMap,
                            editComponentListAdapter.setReplaceVariableMap,
                            editComponentListAdapter.editListMap,
                            editComponentListAdapter.busyboxExecutor,
                        ).filter { lineMap ->
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
}

