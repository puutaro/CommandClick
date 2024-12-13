package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ListViewToolForEditListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
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


object WithEditConstraintListView{
    
    private val delayTime = 500L
    private val noIndexSign = -1
    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val typeSeparator = EditComponent.Template.typeSeparator
    private val onConsecKey = EditComponent.Template.EditComponentKey.ON_CONSEC.key
    private val onClickViewsKey = EditComponent.Template.EditComponentKey.CLICK_VIEWS.key
    private val switchOn = EditComponent.Template.switchOn
    private val switchOff = EditComponent.Template.switchOff
    private val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key

    enum class SceneType {
        TOOLBAR,
        FOOTER,
        BK,
    }

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
        editListSearchEditText: AppCompatEditText,
        editBkFrame: FrameLayout?,
        editListFooterConstraintLayout: ConstraintLayout?,
        editListToolbarConstraintLayout: ConstraintLayout?,
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
        CoroutineScope(Dispatchers.IO).launch {
            setBk(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editListRecyclerView,
                editBkFrame,
                editListConfigMap,
                requestBuilderSrc,
                density,
            )
        }

//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "glistWith.txt").absolutePath,
//            listOf(
//                "tag: ${editFragment.tag}",
//                "listIndexConfigMap: ${listIndexConfigMap}",
//                "indexListMap: ${indexListMap}",
//                "listIndexTypeKey: ${listIndexTypeKey.key}",
//            ).joinToString("\n\n")
//        )
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
        val editConstraintListAdapter = withContext(Dispatchers.IO) {
            val indexListMap = EditListConfig.getConfigKeyMap(
                editListConfigMap,
                EditListConfig.EditListConfigKey.LIST.key,
                setReplaceVariableMap,
            )
            val lineMapList = ListSettingsForEditList.EditListMaker.makeLineMapListHandler(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                busyboxExecutor,
            )
            val layoutInflater = LayoutInflater.from(
                context
            )
            val frameMapAndFrameTagToContentsMapListToTagIdList =
                let {
                    val viewLayoutPath = ListSettingsForEditList.ViewLayoutPathManager.getViewLayoutPath(
                        fannelInfoMap,
                        setReplaceVariableMap,
                        indexListMap,
                        ListSettingsForEditList.ListSettingKey.VIEW_LAYOUT_PATH.key,
                    )
                    ListSettingsForEditList.ViewLayoutPathManager.parseForConstraint(
                        context,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        viewLayoutPath
                    )
                }
            EditConstraintListAdapter(
                WeakReference(fragment),
                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap + globalVarNameToValueMap,
                globalVarNameToValueMap,
                frameMapAndFrameTagToContentsMapListToTagIdList,
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
                editListRecyclerView.adapter = editConstraintListAdapter
            }
        }

        val layoutConfigMap = withContext(Dispatchers.IO) {
            LayoutSettingsForEditList.getLayoutConfigMap(
                editListConfigMap,
                null,
            )
        }
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.IO) {
//                ItemTouchHelperCallbackForEditListAdapter.set(
//                    fragment,
//                    fannelInfoMap,
//                    setReplaceVariableMap,
//                    editListRecyclerView,
//                    editComponentListAdapter,
//                    layoutConfigMap
//                )
//            }
//        }

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
                    editConstraintListAdapter.getLayoutConfigMap(),
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
            }
            CoroutineScope(Dispatchers.Main).launch{
                withContext(Dispatchers.IO) {
                    delay(1000)
                }
            }
        }
//        CoroutineScope(Dispatchers.IO).launch {
//            val editListBkPairs = withContext(Dispatchers.IO) {
//                EditListConfig.getConfigKeyConList(
//                    editListConfigMap,
//                    EditListConfig.EditListConfigKey.BK_LAYOUT_PATH.key,
//                )
//            }
//           withContext(Dispatchers.Main){
////                val buttonFrameLayout = layoutInflater.inflate(
////                    R.layout.icon_caption_layout_for_edit_list,
////                    null
////                ) as FrameLayout?
//                BkImageSettingsForEditList.makeBkFrame(
//                    context,
//                    editListBkFrame,
//                    fannelInfoMap,
//                    setReplaceVariableMap,
//                    busyboxExecutor,
//                    editListBkPairs,
//                    requestBuilderSrc,
//                    density,
//                )
//            }
////            withContext(Dispatchers.Main) {
////                editListBkFrame.addView(bkFrameLayout)
////            }
//        }

        val outValue = withContext(Dispatchers.IO) {
            val outValueSrc = TypedValue()
            context.theme?.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValueSrc,
                true
            )
            outValueSrc
        }
        CoroutineScope(Dispatchers.IO).launch {
            setToolbar(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap + globalVarNameToValueMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editListRecyclerView,
                editListToolbarConstraintLayout,
                editListConfigMap,
                requestBuilderSrc,
                density,
                outValue
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            setFooter(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                globalVarNameToValueMap,
                busyboxExecutor,
                editListFooterConstraintLayout,
                editListRecyclerView,
                editListConfigMap,
                requestBuilderSrc,
                density,
                outValue
            )
        }
        CoroutineScope(Dispatchers.IO).launch{
            makeSearchEditText(
                fragment,
                fannelInfoMap,
                editConstraintListAdapter,
                editListSearchEditText,
                editListConfigMap,
                setReplaceVariableMap,
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            invokeItemSetClickListenerForFileList(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                editConstraintListAdapter
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            invokeItemSetTouchListenerForFileList(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                editConstraintListAdapter
            )
        }
    }

    private suspend fun setFooter(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>,
        busyboxExecutor: BusyboxExecutor?,
        editListFooterConstraintLayout: ConstraintLayout?,
        editListRecyclerView: RecyclerView,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        outValue: TypedValue,
    ){
        createViewLayout(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            editListRecyclerView,
            null,
            editListFooterConstraintLayout,
            null,
            editListConfigMap,
            requestBuilderSrc,
            density,
            outValue
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
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editListToolbarConstraintLayout: ConstraintLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        outValue: TypedValue,
    ){
        createViewLayout(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            editListRecyclerView,
            null,
            editListToolbarConstraintLayout,
            null,
            editListConfigMap,
            requestBuilderSrc,
            density,
            outValue
        )
    }

    private suspend fun setBk(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editBkFrame: FrameLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ){
        createViewLayout(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            globalVarNameToValueMap,
            busyboxExecutor,
            editListRecyclerView,
            editBkFrame,
            null,
            null,
            editListConfigMap,
            requestBuilderSrc,
            density,
            null,
        )
    }

    private suspend fun createViewLayout(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        globalVarNameToValueMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editBkFrame: FrameLayout?,
        editListFooterConstraintLayout: ConstraintLayout?,
        editListToolbarConstraintLayout: ConstraintLayout?,
        editListConfigMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        outValue: TypedValue?,
        ) {
        val context = fragment.context
            ?: return
        val editConstraintListAdapter =
            editListRecyclerView.adapter as? EditConstraintListAdapter
        val plusKeyToSubKeyConWhere =
            fannelInfoMap.map {
                val key = SnakeCamelTool.snakeToCamel(it.key)
                "${key}: ${it.value}"
            }.joinToString(", ")
//        val isEditToolbar = editListToolbarConstraintLayout != null
        val sceneType = when(true){
            (editListFooterConstraintLayout != null) -> SceneType.FOOTER
            (editListToolbarConstraintLayout != null) -> SceneType.TOOLBAR
            (editBkFrame != null) -> SceneType.BK
            else -> return
        }
        val debugWhere = sceneType.name
        val layoutKey = when(sceneType){
            SceneType.TOOLBAR -> EditListConfig.EditListConfigKey.TOOLBAR_LAYOUT_PATH
            SceneType.FOOTER-> EditListConfig.EditListConfigKey.FOOTER_LAYOUT_PATH
            SceneType.BK -> EditListConfig.EditListConfigKey.BK_LAYOUT_PATH
        }.key
        val viewLayoutPath = ListSettingsForEditList.ViewLayoutPathManager.getViewLayoutPath(
            fannelInfoMap,
            setReplaceVariableMap,
            editListConfigMap,
            layoutKey,
        )
        val isOnlyCmdValEdit =
            viewLayoutPath == EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.name
        val frameMapAndFrameTagToContentsMapListToTagIdList = when(isOnlyCmdValEdit) {
            true -> ListSettingsForEditList.ViewLayoutPathManager.parseFromListForConstraint(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.macroConList,
                "${EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.name} in $plusKeyToSubKeyConWhere"
                )
            else -> ListSettingsForEditList.ViewLayoutPathManager.parseForConstraint(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                viewLayoutPath
            )
        }
        val frameMap = frameMapAndFrameTagToContentsMapListToTagIdList?.first ?: emptyMap()
        val frameTagToContentsMapList =
            frameMapAndFrameTagToContentsMapListToTagIdList?.second
        val tagToIdListSrc =
            frameMapAndFrameTagToContentsMapListToTagIdList?.third
//        if(sceneType == SceneType.BK) {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lbk.txt").absolutePath,
//                listOf(
//                    "editListConfigMap: ${editListConfigMap}",
//                    "frameMap: ${frameMap}",
//                    "frameTagToContentsMapList: ${frameTagToContentsMapList}"
//                ).joinToString("\n\n\n")
//            )
//        }
        if(
            frameMap.isEmpty()
        ){
            when(sceneType) {
                SceneType.FOOTER
                    -> withContext(Dispatchers.Main) {
                    editListFooterConstraintLayout?.visibility = View.GONE
                }
                SceneType.TOOLBAR ->  withContext(Dispatchers.Main){
                    editListToolbarConstraintLayout?.visibility = View.GONE
                }
                SceneType.BK -> withContext(Dispatchers.Main) {
                    editBkFrame?.visibility = View.GONE
                }
            }
            return
        }
        val frameTagList = frameMap.keys
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

        val alreadyUseTagListMutex = Mutex()
        val alreadyUseTagList = mutableListOf<String>()
        val frameTag = frameTagList.firstOrNull()
            ?: return
        val totalMapListElInfo =
            "${debugWhere} frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}"
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
        val frameVarNameValueMap = withContext(Dispatchers.IO) {
            val frameKeyPairsConSrc = frameMap.get(frameTag)
            EditComponent.Template.ReplaceHolder.replaceHolder(
                frameKeyPairsConSrc,
                String(),
                String(),
                String(),
                noIndexSign,
            ).let {
                    innerFrameKeyPairsConSrc ->
                if(
                    innerFrameKeyPairsConSrc.isNullOrEmpty()
                ) return@let emptyMap()
                val settingActionManager = SettingActionManager()
                val varNameToValueMap = settingActionManager.exec(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    innerFrameKeyPairsConSrc,
                    "frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                        editConstraintListAdapterArg = editConstraintListAdapter
                ) + (globalVarNameToValueMap ?: emptyMap())
               varNameToValueMap
            }
        }
        val tagIdMapSrc = withContext(Dispatchers.IO){
            tagToIdListSrc?.map {
                val key = CmdClickMap.replace(
                    it.key,
                    frameVarNameValueMap
                )
                key to it.value
            }?.toMap() ?: emptyMap()
        }

        val contentsChannel = Channel<
               Pair<List<String>, List<Pair<String, String?>>>,
                >(100)

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                publishContents(
                    context,
                    frameTag,
                    frameTagToContentsMapList,
                    frameVarNameValueMap,
                    contentsChannel,
                    totalMapListElInfo,
                )
            }
            withContext(Dispatchers.IO) {
                contentsChannel.close()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            for (contentsMapToKeySubKeysPair in contentsChannel){
                val contentsKeyValues =
                    contentsMapToKeySubKeysPair.first
                val contentsTagToKeyPairsList = contentsMapToKeySubKeysPair.second
                contentsKeyValues.forEachIndexed execSetContents@{ execSetContentsIndex, contentsKeyPairsListConSrc ->
                    CoroutineScope(Dispatchers.IO).launch execSetContentsCoroutine@{
                        val contentsTagSrc =
                            contentsTagToKeyPairsList.getOrNull(execSetContentsIndex)
                        val mapListElInfoForExecContents =
                            listOf(
                                "contentsTagSrc: ${contentsTagSrc}",
                                totalMapListElInfo
                            ).joinToString(", ")
                        val varNameToValueMap =
                            withContext(Dispatchers.IO) updateLinearKeyParsListCon@{
                                EditComponent.AdapterSetter.makeFrameVarNameToValueMap(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    editConstraintListAdapter,
                                    frameVarNameValueMap,
                                    mapListElInfoForExecContents,
                                    contentsKeyPairsListConSrc,
                                    frameTag,
                                    frameTag,
                                    frameTag,
                                    noIndexSign,
                                )
                            }
                        val contentsVarNameToValueMap = frameVarNameValueMap + varNameToValueMap
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
                        val execContentsTag = withContext(Dispatchers.IO) {
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
                                "execContentsTag: ${execContentsTag}",
                                totalMapListElInfo
                            ).joinToString(", ")
                        val isContentsTagErr =
                            withContext(Dispatchers.IO) contentsTagCheck@{
                                val tagGenre =
                                    EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
                                val alreadyUseTagListSrc =
                                    EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                                        alreadyUseTagList,
                                        alreadyUseTagListMutex
                                    )
                                val correctContentsTag =
                                    EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                        context,
                                        tagGenre,
                                        execContentsTag,
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


                        editConstraintListAdapter?.footerKeyPairListConMap?.put(
                            execContentsTag,
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
                                val contentsLayout =
                                    withContext(Dispatchers.Main) {
                                            EditComponent.AdapterSetter.makeContentsFrameLayout(
                                                context
                                            )
                                        }

                                val baseLayoutForAdd = when(sceneType){
                                    SceneType.TOOLBAR
                                        -> editListToolbarConstraintLayout
                                    SceneType.FOOTER ->  editListFooterConstraintLayout
                                    SceneType.BK -> editBkFrame
                                }
                                withContext(Dispatchers.Main) {
                                    baseLayoutForAdd?.addView(contentsLayout)
                                }
                                val idInt = withContext(Dispatchers.IO){
                                    tagIdMapSrc.get(
                                        execContentsTag
                                    )
                                }
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    if(isEditToolbar && execSetContentsIndex == 0){
//                                        (editListToolbarConstraintLayout?.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
//                                            FileSystems.updateFile(
//                                                File(UsePath.cmdclickDefaultAppDirPath, "lfanlCenter.txt").absolutePath,
//                                                listOf(
//                                                    "execSetContentsIndex: ${execSetContentsIndex}",
//                                                    "tagIdMap: ${tagIdMap}",
//                                                    "execContentsTag: ${execContentsTag}",
//                                                    "id: ${tagIdMap.get(execContentsTag)}",
//                                                ).joinToString("\n")
//                                            )
//                                            tagIdMap.get(execContentsTag)?.let {
//                                                endToEnd = ConstraintLayout.LayoutParams.UNSET
//                                                endToStart = it
//                                            }
//                                        }
//                                    }
//                                }
                                val tagIdMap = when(sceneType){
                                    SceneType.TOOLBAR,
                                    SceneType.FOOTER ->  tagIdMapSrc
                                    SceneType.BK -> null
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    EditConstraintFrameMaker.make(
                                        context,
                                        idInt,
                                        tagIdMap,
                                        contentsLayout,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        contentsKeyPairsList,
                                        0,
                                        execContentsTag,
                                        editConstraintListAdapter?.totalSettingValMap,
                                        requestBuilderSrc,
                                        density,
                                    )
                                }
                                contentsLayout
                            } ?: return@execSetContentsCoroutine
                        CoroutineScope(Dispatchers.IO).launch execClick@{
                            if(
                                sceneType == SceneType.BK
                                || outValue == null
                            ) return@execClick
                            clickHandler(
                                fragment,
                                fannelInfoMap,
                                setReplaceVariableMap,
                                editListRecyclerView,
                                linearFrameKeyPairsListCon,
                                contentsKeyPairsList,
                                contentsFrameLayout,
                                outValue,
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun publishContents(
        context: Context?,
        frameTag: String,
        frameTagToContentsMapList: Map<String, List<List<String>>>?,
        frameVarNameToValueMap: Map<String, String>,
        contentsChannel: Channel<
                Pair<
                        List<String>,
                        List<Pair<String, String?>>
                        >,
                >,
        totalMapListElInfo: String,
    ) {
        val frameTagToContentsKeysListMapWithReplace =
            frameTagToContentsMapList?.map {
                val key = CmdClickMap.replace(
                    it.key,
                    frameVarNameToValueMap,
                )
                key to it.value
            }?.toMap()
        val contentsKeysList =
            withContext(Dispatchers.IO) {
                frameTagToContentsKeysListMapWithReplace?.get(frameTag)
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
        withContext(Dispatchers.IO) {
            val jobList =
                contentsKeysList?.mapIndexed setContents@{ contentsIndex, contentsKeyValues ->
                    async {
                        val contentsTagToKeyPairsList = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.makeContentsTagToKeyPairsList(
                                context,
                                contentsKeyValues,
                                frameVarNameToValueMap,
                                String(),
                                String(),
                                String(),
                                noIndexSign,
                                totalMapListElInfo,
                            )
                        }
//                        val contentsKeyValueSize = withContext(Dispatchers.IO) {
//                            EditComponent.AdapterSetter.culcLinearKeyValueSize(
//                                contentsTagToKeyPairsList,
//                            )
//                        }
//                        val layoutWeight = withContext(Dispatchers.IO) culcFrameLayoutWeight@{
//                            when (isEditToolbar && !isOnlyCmdValEdit) {
//                                true -> weightSumFloat / (contentsKeyValueSize + 1)
//                                else -> {
//                                    if (
//                                        contentsKeyValueSize == 0
//                                    ) return@culcFrameLayoutWeight 0f
//                                    weightSumFloat / contentsKeyValueSize
//                                }
//                            }
//                        }
//                        if (isEditToolbar) {
//                            withContext(Dispatchers.Main) {
//                                let {
//                                    val layoutParam =
//                                        editListToolbarConstraintLayout?.layoutParams as? LinearLayoutCompat.LayoutParams
//                                    if (layoutParam?.weight == layoutWeight) return@let
//                                    layoutParam?.weight = layoutWeight
//                                    editListToolbarConstraintLayout?.layoutParams = layoutParam
//                                }
//                            }
//                        }
                        withContext(Dispatchers.IO) {
                            contentsChannel.send(
                                Pair(
                                    contentsKeyValues,
                                    contentsTagToKeyPairsList,
                                    ),

                            )
                        }
                    }
                }
            jobList?.forEach { it.await() }
        }
    }

    private suspend fun clickHandler(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView?,
        contentsKeyPairsListCon: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsFrameLayout: FrameLayout,
        outValue: TypedValue,
    ){

        if (
            contentsKeyPairsListCon.isNullOrEmpty()
        ) return
        withContext(Dispatchers.IO){
            delay(delayTime)
        }
        val enableClick = EditComponent.Template.ClickManager.isClickEnable(contentsKeyPairsList)
        if (
            !enableClick
        ) return
        withContext(Dispatchers.IO) execExecClick@{
            val clickViewList =
                EditComponent.Template.ClickViewManager.makeClickViewList(
                    contentsFrameLayout.children,
                    PairListTool.getValue(
                        contentsKeyPairsList,
                        onClickViewsKey,
                    )
                )
            val isConsec =
                PairListTool.getValue(
                    contentsKeyPairsList,
                    onConsecKey,
                ) == EditComponent.Template.switchOn
            clickViewList.forEachIndexed { index, clickView ->
                withContext(Dispatchers.Main) {
                    clickView.setBackgroundResource(outValue.resourceId)
                    clickView.isClickable = true
                }
                if (!isConsec) {
                    withContext(Dispatchers.Main) {
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lclick.indialog_setOnClickListener.txt").absolutePath,
//                            listOf(
//                                "clickViewList: ${clickViewList.toList().size}",
//                                "index: ${index}",
//                                "is OutlineTextView: ${clickView is OutlineTextView}",
//                                "is AppCompatImageView: ${clickView is AppCompatImageView}",
//                                "contentsKeyPairsList: ${contentsKeyPairsList}",
//                                "isConsec: ${isConsec}",
//                                "isNotClickSetting: ${isNotClickSetting}",
//                                "onClick: ${onClick}",
//                            ).joinToString("\n") + "\n\n============\n\n\n"
//                        )
                        clickView.setOnClickListener {
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "lclick.indialog_setOnClickListener_on_Click.txt").absolutePath,
//                                listOf(
//                                    "clickViewList: ${clickViewList.toList().size}",
//                                    "index: ${index}",
//                                    "is OutlineTextView: ${clickView is OutlineTextView}",
//                                    "is AppCompatImageView: ${clickView is AppCompatImageView}",
//                                    "contentsKeyPairsList: ${contentsKeyPairsList}",
//                                    "isConsec: ${isConsec}",
//                                    "isNotClickSetting: ${isNotClickSetting}",
//                                    "onClick: ${onClick}",
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
                            execJsAction(
                                fragment,
                                fannelInfoMap,
                                setReplaceVariableMap,
                                editListRecyclerView,
                                contentsKeyPairsListCon
                            )
                        }
                    }
                    return@forEachIndexed
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
                                android.view.MotionEvent.ACTION_DOWN -> {
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
                                                            contentsKeyPairsListCon
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

                                android.view.MotionEvent.ACTION_UP,
                                android.view.MotionEvent.ACTION_CANCEL,
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
        val editConstraintListAdapter = editListRecyclerView?.adapter as? EditConstraintListAdapter
        EditConstraintListAdapter.MainFannelUpdater.saveFannelCon(
            editConstraintListAdapter?.fannelContentsList,
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
        editConstraintListAdapter: EditConstraintListAdapter,
    ) {
//        withContext(Dispatchers.IO) {
//            delay(delayTime)
//        }
        editConstraintListAdapter.editAdapterClickListener =
            object: EditConstraintListAdapter.OnEditAdapterClickListener {
                override fun onEditAdapterClick(
                    itemView: View,
                    holder: EditConstraintListAdapter.EditListViewHolder,
                    editListPosition: Int,
                ) {
//                    ToastUtils.showShort("${itemView is MaterialCardView}")
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lOnClidek00.txt").absolutePath,
//                        listOf(
//                            "editListPosition: ${editListPosition}",
//                            "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                            "is FrameLayout: ${itemView is FrameLayout}",
//                            "is MaterialCardView: ${itemView is MaterialCardView}",
//                            "itemView: ${itemView.tag}"
//                        ).joinToString("\n")
//                    )
                    val frameLayout = when(true){
                        (itemView is MaterialCardView) -> {
                            itemView.children.firstOrNull {
                                it is FrameLayout
                            } as FrameLayout
                        }
                        (itemView is FrameLayout) -> itemView as FrameLayout
                        else -> null
                    } ?: return
//                    val frameLayout = itemView as FrameLayout
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
                        editConstraintListAdapter.lineMapList.getOrNull(holder.bindingAdapterPosition)
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
                        editConstraintListAdapter.handleClickEvent(
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
        editConstraintListAdapter: EditConstraintListAdapter,
    ) {
//        withContext(Dispatchers.IO) {
//            delay(delayTime)
//        }
        var execTouchJob: Job? = null
        var consecutiveJob: Job? = null
        editConstraintListAdapter.editAdapterTouchUpListener = object: EditConstraintListAdapter.OnEditAdapterTouchUpListener {
            override fun onEditAdapterTouchUp(
                itemView: View,
                holder: EditConstraintListAdapter.EditListViewHolder,
                editListPosition: Int
            ) {
                execTouchJob?.cancel()
                consecutiveJob?.cancel()
                return
            }
        }

        editConstraintListAdapter.editAdapterTouchDownListener = object: EditConstraintListAdapter.OnEditAdapterTouchDownListener {
            override fun onEditAdapterTouchDown(
                itemView: View,
                holder: EditConstraintListAdapter.EditListViewHolder,
                editListPosition: Int
            ) {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lOnClidek00.txt").absolutePath,
//                    listOf(
//                        "editListPosition: ${editListPosition}",
//                        "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                        "is FrameLayout: ${itemView is FrameLayout}",
//                        "is MaterialCardView: ${itemView is MaterialCardView}",
//                    ).joinToString("\n")
//                )
                val frameLayout = when(true){
                    (itemView is MaterialCardView) -> {
                        itemView.children.firstOrNull {
                            it is FrameLayout
                        } as FrameLayout
                    }
                    (itemView is FrameLayout) -> itemView as FrameLayout
                    else -> null
                } ?: return
//                val frameLayout = itemView as FrameLayout
                val tag = itemView.tag as String?
                    ?: return
                val selectedItemLineMap =
                    editConstraintListAdapter.lineMapList.getOrNull(holder.bindingAdapterPosition)
                        ?: return
                val frameOrLinearCon = runBlocking {
                    holder.getKeyPairListConMap().get(tag)
                } ?: return
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lOnClidek.txt").absolutePath,
//                    listOf(
//                        "tag: ${tag}",
//                        "editListPosition: ${editListPosition}",
//                        "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                        "frameOrLinearCon: ${frameOrLinearCon}",
//                    ).joinToString("\n")
//                )
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
                                editConstraintListAdapter.handleClickEvent(
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
        editConstraintListAdapter: EditConstraintListAdapter,
        searchText: AppCompatEditText,
        editListConfigMap: Map<String, String>?,
        setReplaceVariableMap: Map<String, String>?,
    ) {
        val searchBoxMap = withContext(Dispatchers.IO) {
            EditListConfig.getConfigKeyMap(
                editListConfigMap,
                EditListConfig.EditListConfigKey.SEARCH_BOX.key,
                setReplaceVariableMap
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
                            editConstraintListAdapter.fannelInfoMap,
                            editConstraintListAdapter.setReplaceVariableMap,
                            editConstraintListAdapter.indexListMap,
                            editConstraintListAdapter.busyboxExecutor,
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
                    ListViewToolForEditListAdapter.editConstraintListUpdateFileList(
                        editConstraintListAdapter,
                        filteredUrlHistoryList,
                    )
                }
            })
        }
    }
}

