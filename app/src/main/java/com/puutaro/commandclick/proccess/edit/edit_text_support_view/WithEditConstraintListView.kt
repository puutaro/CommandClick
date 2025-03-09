package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ListViewToolForEditListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent.Template
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForEditListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionData
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ImportDataForImageAction
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionData
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.proccess.edit_list.EditImageViewSetter
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.proccess.edit_list.EditTextViewSetter
import com.puutaro.commandclick.proccess.edit_list.config_settings.ImageActionForConfigCon
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
import org.threeten.bp.LocalDateTime
import java.io.File
import java.lang.ref.WeakReference


object WithEditConstraintListView{
    
    private val delayTime = 1000L
    private val noIndexSign = -1
    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val typeSeparator = EditComponent.Template.typeSeparator
    private val onConsecKey = EditComponent.Template.EditComponentKey.ON_CONSEC.key
//    private val onClickViewsKey = EditComponent.Template.EditComponentKey.CLICK_VIEWS.key
    private val switchOn = EditComponent.Template.switchOn
    private val switchOff = EditComponent.Template.switchOff
    private val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
    val titleLayoutElevation = 10f
    private val starndardLayoutElevation = 9f

    enum class SceneType {
        TITLE,
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
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        editListConfigMapSrc: Map<String, String>?,
        editListTitleConstraint: ConstraintLayout?,
//        editListLinearAlignTitleLayout: FrameLayout?,
//        editListFragAlignTitleLayout: FrameLayout?,
        editListRecyclerView: RecyclerView,
        editListSearchEditText: AppCompatEditText,
        editBkConstraintLayout: ConstraintLayout?,
        editListFooterConstraintLayout: ConstraintLayout?,
        editListToolbarConstraintLayout: ConstraintLayout?,
        eachLayoutIdMap: Map<String, Int>,
        fannelContentsList: List<String>?,
        density: Float,
        requestBuilderSrc: RequestBuilder<Drawable>?
    ) {
        val dateList = mutableListOf<Pair<String, LocalDateTime>>()
        dateList.add("launch" to LocalDateTime.now())
        val context = fragment.context
            ?: return
        CoroutineScope(Dispatchers.Main).launch{
            listOf(
                editListTitleConstraint,
                editListRecyclerView,
                editListSearchEditText,
                editListFooterConstraintLayout,
                editListToolbarConstraintLayout
            ).forEach {
                it?.apply {
                    (layoutParams as ConstraintLayout.LayoutParams).apply {
                        elevation = starndardLayoutElevation
                    }
                }
            }
        }
        dateList.add("settingAc" to LocalDateTime.now())
        withContext(Dispatchers.IO) {
//            SettingActionManager.Companion.GlobalExitManager.init()
            SettingActionManager.init()
            SettingActionManager.dataInit()
            ImageActionManager.dataInit()
//            FileSystems.removeAndCreateDir(
//                UsePath.cmdclickDefaultSDebugAppDirPath
//            )
        }
        val globalVarNameToValueMapToSignal = let {
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
                        context,
                        fragment.activity,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        settingActionAsyncCoroutine,
                        null,
                        //listOf("testTopVar"),
                        null,
                        it,
                        keyToSubKeyConWhere,
                    )
                }
            }
        }
        val globalVarNameToValueMap =
            globalVarNameToValueMapToSignal.first
        val settingAcSignal = globalVarNameToValueMapToSignal.second
        if(
            SettingActionData.SettingActionExitManager.isStopAfter(settingAcSignal)
        ) return
        withContext(Dispatchers.IO) {
            SettingActionManager.init()
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultSDebugAppDirPath, "sglobalVarNameToValueMap.txt").absolutePath,
//                listOf(
//                    "globalVarNameToValueMap: ${globalVarNameToValueMap}\n",
//                ).joinToString("\n\n\n")
//            )
        }
        dateList.add("imageAc" to LocalDateTime.now())
        withContext(Dispatchers.IO) {
            ImageActionManager.init()
            FileSystems.removeAndCreateDir(
                UsePath.cmdclickDefaultIDebugAppDirPath
            )
        }
        val globalVarNameToBitmapMapToSignal =
            ImageActionForConfigCon.getImageConfigCon(
                editListConfigMapSrc,
            )?.let {
                CmdClickMap.replace(
                    it,
                    globalVarNameToValueMap
                )
            }.let {
                val imageActionManager = ImageActionManager()
                runBlocking {
                    val keyToSubKeyConWhere =
                        "${CommandClickScriptVariable.EDIT_LIST_CONFIG}, ${fannelInfoMap.map {
                            val key = SnakeCamelTool.snakeToCamel(it.key)
                            "${key}: ${it.value}"
                        }.joinToString(", ")}"
                    imageActionManager.exec(
                        context,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        null,
                        null,
                        imageActionAsyncCoroutine,
                        null,
    //                        listOf("testTopVar"),
                        null,
                        it,
                        keyToSubKeyConWhere,
                    )
                }
            }
        withContext(Dispatchers.IO) {
            ImportDataForImageAction.clearImportData()
            ImageActionManager.init()
        }
        val globalVarNameToBitmapMap =
            globalVarNameToBitmapMapToSignal.first
        val imageAcSignal =
            globalVarNameToBitmapMapToSignal.second
        if(
            ImageActionData.ImageActionExitManager.isStopAfter(imageAcSignal)
        ) return
//        return
//        val imageAcTestDirPath = File(UsePath.cmdclickDefaultAppDirPath, "imageAc").absolutePath
//        FileSystems.removeAndCreateDir(
//            imageAcTestDirPath
//        )
//        globalVarNameToBitmapMap.forEach {
//            val bitmap = it.value
//                ?: return@forEach
//            FileSystems.writeFromByteArray(
//                File(
//                    imageAcTestDirPath,
//                    "${it.key}.png"
//                ).absolutePath,
//                BitmapTool.convertBitmapToByteArray(
//                    bitmap
//                )
//            )
//        }
        withContext(Dispatchers.IO) {
            ImageActionManager.init()
        }
        val setReplaceVariableMap = withContext(Dispatchers.IO) {
            (setReplaceVariableMapSrc ?: emptyMap())
        }
        dateList.add("makeEditConfig" to LocalDateTime.now())
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
        dateList.add("setBk" to LocalDateTime.now())
        CoroutineScope(Dispatchers.IO).launch {
            setBk(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
                busyboxExecutor,
                editListRecyclerView,
                editBkConstraintLayout,
                editListConfigMap,
                eachLayoutIdMap,
                requestBuilderSrc,
                density,
            )
        }
        dateList.add("setTitle" to LocalDateTime.now())
        CoroutineScope(Dispatchers.IO).launch {
            setTitle(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
                busyboxExecutor,
                editListRecyclerView,
                editListTitleConstraint,
                editListConfigMap,
                eachLayoutIdMap,
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
        dateList.add("makeAdapter" to LocalDateTime.now())
        val editConstraintListAdapter = withContext(Dispatchers.IO) {
            dateList.add("setAdapter_indexListMap" to LocalDateTime.now())
            val indexListMap = EditListConfig.getConfigKeyMap(
                editListConfigMap,
                EditListConfig.EditListConfigKey.LIST.key,
                setReplaceVariableMap,
            )
            dateList.add("setAdapter_lineMapList" to LocalDateTime.now())
            val lineMapList = ListSettingsForEditList.EditListMaker.makeLineMapListHandler(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                busyboxExecutor,
            )
            dateList.add("setAdapter_layoutInflater" to LocalDateTime.now())
            val layoutInflater = LayoutInflater.from(
                context
            )
            dateList.add("setAdapter_frameMapAndFrameTagToContentsMapListToTagIdList" to LocalDateTime.now())
            val frameMapAndFrameTagToContentsMapListToTagIdList =
                let {
                    val viewLayoutPath = ListSettingsForEditList.ViewLayoutPathManager.getViewLayoutPath(
                        fannelInfoMap,
                        setReplaceVariableMap,
                        indexListMap,
                        ListSettingsForEditList.ListSettingKey.VIEW_LAYOUT_PATH.key,
                    )
                    dateList.add("setAdapter_ListSettingsForEditList.ViewLayoutPathManager.parseForConstraint" to LocalDateTime.now())
                    ListSettingsForEditList.ViewLayoutPathManager.parseForConstraint(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        globalVarNameToValueMap,
                        globalVarNameToBitmapMap,
                        settingActionAsyncCoroutine,
                        imageActionAsyncCoroutine,
                        viewLayoutPath
                    )
                }
            dateList.add("setAdapter_EditConstraintListAdapter" to LocalDateTime.now())
            EditConstraintListAdapter(
                WeakReference(fragment),
                layoutInflater,
                fannelInfoMap,
                setReplaceVariableMap + globalVarNameToValueMap,
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
                frameMapAndFrameTagToContentsMapListToTagIdList,
                editListConfigMap,
                busyboxExecutor,
                indexListMap,
                lineMapList,
                fannelContentsList,
                density
            )
        }
        dateList.add("setAdapter" to LocalDateTime.now())
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "leditDateList.txt").absolutePath,
            dateList.joinToString("\n")
        )
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
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                ItemTouchHelperCallbackForEditListAdapter.set(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    editListRecyclerView,
                    editConstraintListAdapter,
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
                SettingActionManager.init()
            }
            CoroutineScope(Dispatchers.Main).launch{
                withContext(Dispatchers.IO) {
                    delay(1000)
                }
            }
        }

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
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
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
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
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
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToValueMap: Map<String, String>,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
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
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            busyboxExecutor,
            editListRecyclerView,
            null,
            null,
            editListFooterConstraintLayout,
            null,
            editListConfigMap,
            null,
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
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
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
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            busyboxExecutor,
            editListRecyclerView,
            null,
            null,
            null,
            editListToolbarConstraintLayout,
            editListConfigMap,
            null,
            requestBuilderSrc,
            density,
            outValue
        )
    }

    private suspend fun setTitle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editListTitleConstraintLayout: ConstraintLayout?,
        editListConfigMap: Map<String, String>?,
        eachLayoutIdMap: Map<String, Int>,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ){
        createViewLayout(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            busyboxExecutor,
            editListRecyclerView,
            editListTitleConstraintLayout,
            null,
            null,
            null,
            editListConfigMap,
            eachLayoutIdMap,
            requestBuilderSrc,
            density,
            null,
        )
    }

    private suspend fun setBk(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editBkConstraintLayout: ConstraintLayout?,
        editListConfigMap: Map<String, String>?,
        eachLayoutIdMap: Map<String, Int>,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ){
        createViewLayout(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            busyboxExecutor,
            editListRecyclerView,
            null,
            editBkConstraintLayout,
            null,
            null,
            editListConfigMap,
            eachLayoutIdMap,
            requestBuilderSrc,
            density,
            null,
        )
    }

    private suspend fun createViewLayout(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        editListTitleConstraintLayout: ConstraintLayout?,
        editBkConstraintLayout: ConstraintLayout?,
        editListFooterConstraintLayout: ConstraintLayout?,
        editListToolbarConstraintLayout: ConstraintLayout?,
        editListConfigMap: Map<String, String>?,
        eachLayoutIdMap: Map<String, Int>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        outValue: TypedValue?,
        ) {
        val context = fragment.context
            ?: return
        val fragmentActivityRef = WeakReference(fragment.activity)
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
            (editListTitleConstraintLayout != null) -> SceneType.TITLE
            (editListFooterConstraintLayout != null) -> SceneType.FOOTER
            (editListToolbarConstraintLayout != null) -> SceneType.TOOLBAR
            (editBkConstraintLayout != null) -> SceneType.BK
            else -> return
        }
        val dateList = mutableListOf<Pair<String, LocalDateTime>>()
        dateList.add("start" to LocalDateTime.now())
        val debugWhere = sceneType.name
        val layoutKey = when(sceneType){
            SceneType.TITLE -> EditListConfig.EditListConfigKey.TITLE_LAYOUT_PATH
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
        dateList.add("raedLayout" to LocalDateTime.now())
        val frameMapAndFrameTagToContentsMapListToTagIdList = when(isOnlyCmdValEdit) {
            true -> ListSettingsForEditList.ViewLayoutPathManager.parseFromListForConstraint(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.macroConList,
                "${EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.name} in $plusKeyToSubKeyConWhere"
                )
            else -> ListSettingsForEditList.ViewLayoutPathManager.parseForConstraint(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                globalVarNameToValueMap,
                globalVarNameToBitmapMap,
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                viewLayoutPath,
            )
        }
        dateList.add("makeMap" to LocalDateTime.now())
        val frameMap = frameMapAndFrameTagToContentsMapListToTagIdList?.first ?: emptyMap()
        val frameTagToContentsMapList =
            frameMapAndFrameTagToContentsMapListToTagIdList?.second
        val tagToIdListSrc =
            frameMapAndFrameTagToContentsMapListToTagIdList?.third
//        if(sceneType == SceneType.BK) {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lbk00.txt").absolutePath,
//                listOf(
//                    "editListConfigMap: ${editListConfigMap}",
//                    "frameMap: ${frameMap}",
//                    "frameTagToContentsMapList: ${frameTagToContentsMapList}"
//                ).joinToString("\n\n\n")
//            )
//        }
        dateList.add("visiblity gone" to LocalDateTime.now())
        if(
            frameMap.isEmpty()
        ){
            when(sceneType) {
                SceneType.TITLE
                        -> withContext(Dispatchers.Main) {
                    editListTitleConstraintLayout?.visibility = View.GONE
                }
                SceneType.FOOTER
                    -> withContext(Dispatchers.Main) {
                    editListFooterConstraintLayout?.visibility = View.GONE
                }
                SceneType.TOOLBAR ->  withContext(Dispatchers.Main){
                    editListToolbarConstraintLayout?.visibility = View.GONE
                }
                SceneType.BK -> withContext(Dispatchers.Main) {
                    editBkConstraintLayout?.visibility = View.GONE
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
            val isDuplicateTagErr = correctFrameTag.isNullOrEmpty()
            if(
                isDuplicateTagErr
            ) return@frameTagCheck true
            false
        }
        if(isDuplicateFrameTagErr) return
        dateList.add("make frame varNameToValueMap" to LocalDateTime.now())
        val frameKeyPairsConToVarNameValueMapToSignal = withContext(Dispatchers.IO) {
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
                ) return@let null to null
                val settingActionManager = SettingActionManager()
                val varNameToValueMapToSignal = settingActionManager.exec(
                    context,
                    fragmentActivityRef.get(),
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    settingActionAsyncCoroutine,
                    globalVarNameToValueMap?.map{
                        it.key
                    },
                    globalVarNameToValueMap,
                    innerFrameKeyPairsConSrc,
                    "frameTag: ${frameTag}, ${plusKeyToSubKeyConWhere}",
                    editConstraintListAdapterArg = editConstraintListAdapter
                )
                val varNameToValueMap = varNameToValueMapToSignal.first +
                        (globalVarNameToValueMap ?: emptyMap())
                val signal = varNameToValueMapToSignal.second
                val innerFrameKeyPairsCon = CmdClickMap.replace(
                    innerFrameKeyPairsConSrc,
                    varNameToValueMap
                )
                innerFrameKeyPairsCon to Pair(varNameToValueMap, signal)
            }
        }
        dateList.add("make varNameToValueStrMap end" to LocalDateTime.now())
        val frameKeyPairsCon =
            frameKeyPairsConToVarNameValueMapToSignal.first
                ?: String()
        val frameVarNameValueMapToSignal =
            frameKeyPairsConToVarNameValueMapToSignal.second
        val frameVarNameValueMap =
            frameVarNameValueMapToSignal?.first ?: emptyMap()
        val frameSettingAcSignal = frameVarNameValueMapToSignal?.second
        if(
            SettingActionData.SettingActionExitManager.isStopAfter(frameSettingAcSignal)
        ) return
        dateList.add("make tagIdMap" to LocalDateTime.now())
        val tagIdMapSrc = withContext(Dispatchers.IO){
            tagToIdListSrc?.map {
                val key = CmdClickMap.replace(
                    it.key,
                    frameVarNameValueMap
                )
                key to it.value
            }?.toMap() ?: emptyMap()
        }
        dateList.add("make varNameToBitmap" to LocalDateTime.now())
        val varNameToBitmapMapInFrameToSignal = withContext(Dispatchers.IO) {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lglobal_frame.txt").absolutePath,
//                listOf(
//                    "globalVarNameToBitmapMap: ${globalVarNameToBitmapMap}"
//                ).joinToString("\n")
//            )
            ImageActionManager().exec(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                null,
                null,
                imageActionAsyncCoroutine,
                globalVarNameToBitmapMap.map {
                    it.key
                },
                globalVarNameToBitmapMap,
                frameKeyPairsCon,
                totalMapListElInfo,
            )
        }
        val varNameToBitmapMapInFrame = varNameToBitmapMapInFrameToSignal.first
        val imageAcSignal = varNameToBitmapMapInFrameToSignal.second
        if(
            ImageActionData.ImageActionExitManager.isStopAfter(imageAcSignal)
        ) return
        dateList.add("make imagevitmap end" to LocalDateTime.now())
        if(
            sceneType == SceneType.BK
//            && contentsTagSrc?.first != "titleBkFrameBk"
        ) {
           FileSystems.writeFile(
               File(UsePath.cmdclickDefaultAppDirPath, "l${sceneType}.txt").absolutePath,
               dateList.joinToString("\n")
           )
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
//            if(frameTag == "bkFrame") {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lbk.txt").absolutePath,
//                    listOf(
//                        "frameTag: ${frameTag}",
//                        "frameTagToContentsMapList: ${frameTagToContentsMapList}",
//                        "frameVarNameValueMap: ${frameVarNameValueMap}",
//                    ).joinToString("\n\n\n")
//                )
//            }
            withContext(Dispatchers.IO) {
                contentsChannel.close()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            for (contentsMapToKeySubKeysPair in contentsChannel){
                val contentsKeyValues =
                    contentsMapToKeySubKeysPair.first
                val contentsTagToKeyPairsList = contentsMapToKeySubKeysPair.second
                CoroutineScope(Dispatchers.IO).launch {
                    contentsKeyValues.forEachIndexed execSetContents@{ execSetContentsIndex, contentsKeyPairsListConSrc ->
                        CoroutineScope(Dispatchers.IO).launch execSetContentsCoroutine@{
                            val contentsTagSrc =
                                contentsTagToKeyPairsList.getOrNull(execSetContentsIndex)
                            val mapListElInfoForExecContents =
                                sequenceOf(
                                    "contentsTagSrc: ${contentsTagSrc}",
                                    totalMapListElInfo
                                ).joinToString(", ")
                            PairListTool.getValue(
                                CmdClickMap.createMap(
                                    contentsKeyPairsListConSrc,
                                    typeSeparator
                                ),
                                enableKey,
                            ).let { enableStr ->
                                if (
                                    enableStr == switchOff
                                ) return@execSetContentsCoroutine
                            }
//                        if(
//                            sceneType == SceneType.BK
//                            && contentsTagSrc?.first != "titleBkFrameBk"
//                            ) {
//                            return@execSetContentsCoroutine
//                        }
//                            if(sceneType == SceneType.BK) {
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "lbk_contens.txt").absolutePath,
//                                    listOf(
//                                        "contentsTag: ${contentsTagSrc}",
//                                        "frameVarNameValueMap: ${frameVarNameValueMap}",
////                                        "varNameToValueMap: ${varNameToValueMap}",
////                                        "contentsVarNameToValueMap: ${contentsVarNameToValueMap}",
//                                        "time: ${LocalDateTime.now()}",
//                                    ).joinToString("\n") + "\n\n=========\n\n"
//                                )
//                            }
                            val varNameToValueMapToSignal =
                                withContext(Dispatchers.IO) updateLinearKeyParsListCon@{
                                    Template.ReplaceHolder.replaceHolder(
                                        contentsKeyPairsListConSrc,
                                        frameTag,
                                        frameTag,
                                        frameTag,
                                        noIndexSign,
                                    ).let { contentsKeyPairsListConSrcWithReplace ->
                                        if (
                                            contentsKeyPairsListConSrcWithReplace.isNullOrEmpty()
                                        ) return@let emptyMap<String, String>() to null
                                        val topVarNameToValueMapForContents =
                                            ((globalVarNameToValueMap
                                                ?: emptyMap()) + frameVarNameValueMap)
                                        SettingActionManager().exec(
                                            context,
                                            fragmentActivityRef.get(),
                                            fannelInfoMap,
                                            setReplaceVariableMap,
                                            busyboxExecutor,
                                            settingActionAsyncCoroutine,
                                            topVarNameToValueMapForContents.map {
                                                it.key
                                            },
                                            topVarNameToValueMapForContents,
                                            contentsKeyPairsListConSrcWithReplace,
                                            mapListElInfoForExecContents,
                                            editConstraintListAdapterArg = editConstraintListAdapter,
                                        )
                                    }
                                }
                            val varNameToValueMap = varNameToValueMapToSignal.first
                            val contentsSettingAcSignal = varNameToValueMapToSignal.second
                            val contentsVarNameToValueMap = frameVarNameValueMap + varNameToValueMap
                            val contentsKeyPairsListCon = CmdClickMap.replace(
                                contentsKeyPairsListConSrc,
                                contentsVarNameToValueMap
                            )
                            val contentsKeyPairsList = withContext(Dispatchers.IO) {
                                CmdClickMap.createMap(
                                    contentsKeyPairsListCon,
                                    typeSeparator
                                )
                            }
                            withContext(Dispatchers.IO) {
                                PairListTool.getValue(
                                    contentsKeyPairsList,
                                    enableKey,
                                )
                            }.let { enableStr ->
                                if (
                                    enableStr == switchOff
                                ) return@execSetContentsCoroutine
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
                                sequenceOf(
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
                                    val isDuplicateTagErr =
                                        correctContentsTag.isNullOrEmpty()
                                    if (
                                        isDuplicateTagErr
                                    ) return@contentsTagCheck true
                                    false
                                }
                            if (isContentsTagErr) {
                                return@execSetContentsCoroutine
                            }


                            editConstraintListAdapter?.footerKeyPairListConMap?.put(
                                execContentsTag,
                                contentsKeyPairsListCon
                            )
//                        val enableClick =
//                            withContext(Dispatchers.IO) {
//                                EditComponent.Template.ClickManager.isClickEnable(
//                                    contentsKeyPairsList.toMap()
//                                )
//                            }
//                        val clickViewStrList = withContext(Dispatchers.IO) {
//                            EditComponent.Template.ClickViewManager.makeClickViewStrList(
//                                contentsKeyPairsList
//                            )
//                        }
                            val baseLayoutForAdd = makeBaseLayout(
                                sceneType,
                                editListTitleConstraintLayout,
                                editListToolbarConstraintLayout,
                                editListFooterConstraintLayout,
                                editBkConstraintLayout,
                            )
                            val tagIdMap = withContext(Dispatchers.IO) {
                                makeTagIdMap(
                                    sceneType,
                                    tagIdMapSrc,
                                    eachLayoutIdMap,
                                )
                            }
                            val idInt = withContext(Dispatchers.IO) {
                                tagIdMap.get(
                                    execContentsTag
                                )
                            }
                            val viewType = Template.ViewTypeManager.getViewType(
                                contentsKeyPairsList
                            )
                            when (viewType) {
                                Template.ViewTypeManager.ViewType.TEXT ->
                                    setTextView(
                                        fragment,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        editListRecyclerView,
                                        execContentsTag,
                                        contentsKeyPairsList,
                                        contentsKeyPairsListCon,
                                        tagIdMap,
                                        idInt,
                                        sceneType,
                                        baseLayoutForAdd,
//                                    enableClick,
//                                    clickViewStrList,
                                        outValue,
                                        density,
                                        mapListElInfoForContentsTagWithReplace,
                                    )

                                Template.ViewTypeManager.ViewType.FRAME ->
                                    setFrameLayout(
                                        fragment,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        editListRecyclerView,
                                        execContentsTag,
                                        contentsKeyPairsList,
                                        contentsKeyPairsListCon,
                                        tagIdMap,
                                        idInt,
                                        sceneType,
                                        alreadyUseTagList,
                                        alreadyUseTagListMutex,
                                        imageActionAsyncCoroutine,
                                        globalVarNameToBitmapMap,
                                        varNameToBitmapMapInFrame,
                                        baseLayoutForAdd,
//                                    enableClick,
//                                    clickViewStrList,
                                        outValue,
                                        requestBuilderSrc,
                                        density,
                                        mapListElInfoForContentsTagWithReplace,
                                    )

                                Template.ViewTypeManager.ViewType.IMAGE ->
                                    setImageView(
                                        fragment,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        editListRecyclerView,
                                        execContentsTag,
                                        contentsKeyPairsList,
                                        contentsKeyPairsListCon,
                                        tagIdMap,
                                        idInt,
                                        sceneType,
                                        imageActionAsyncCoroutine,
                                        globalVarNameToBitmapMap,
                                        varNameToBitmapMapInFrame,
                                        baseLayoutForAdd,
//                                    enableClick,
//                                    clickViewStrList,
                                        outValue,
                                        requestBuilderSrc,
                                        density,
                                        mapListElInfoForContentsTagWithReplace,
                                    )
                            }
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
        val dateList = mutableListOf<Pair<String, LocalDateTime>>()
        dateList.add("make frameTagToContentsKeysListMapWithReplace" to LocalDateTime.now())
        val frameTagToContentsKeysListMapWithReplace =
            frameTagToContentsMapList?.map {
                val key = CmdClickMap.replace(
                    it.key,
                    frameVarNameToValueMap,
                )
                key to it.value
            }?.toMap()
        dateList.add("make contentsKeysList" to LocalDateTime.now())
        val contentsKeysList =
            withContext(Dispatchers.IO) {
                frameTagToContentsKeysListMapWithReplace?.get(frameTag)
            }
        dateList.add("async publish" to LocalDateTime.now())
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
        dateList.add("end asyncpublish" to LocalDateTime.now())
        if(frameTag == "bkFrame") {
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "lBkFrame.txt").absolutePath,
                dateList.joinToString("\n")
            )
        }
    }

    private suspend fun setFrameLayout(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        execContentsTag: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsKeyPairsListCon: String,
        tagIdMap: Map<String, Int>,
        idInt: Int?,
        sceneType: SceneType,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
        varNameToBitmapMapInFrame: Map<String, Bitmap?>?,
        baseLayoutForAdd: ConstraintLayout?,
//        enableClick: Boolean,
//        clickViewStrList: List<String>,
        outValue: TypedValue?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        mapListElInfoForContentsTagWithReplace: String,
    ){
        val context = fragment?.context
            ?: return
        val fragmentActivityRef = WeakReference(fragment.activity)
        val editConstraintListAdapter =
            editListRecyclerView?.adapter as? EditConstraintListAdapter
        withContext(Dispatchers.Main) setLinearFrameLayout@{
            val textTagToMapForContents = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.makeTextTagToMap(
                    contentsKeyPairsList,
                    execContentsTag,
                    editConstraintListAdapter?.getTotalSettingValMap(),
                )
            }
            val imageTagToMapForContents = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.makeImageTagToMap(
                    contentsKeyPairsList,
                )
            }
            val returnContentsFrameLayout = withContext(Dispatchers.Main) {
                EditComponent.AdapterSetter.makeContentsFrameLayout(
                    context,
                    idInt,
                    execContentsTag,
                    contentsKeyPairsList,
                    textTagToMapForContents,
                    imageTagToMapForContents,
                    null,
                )
            }
            val contentsLayout = withContext(Dispatchers.IO){
                returnContentsFrameLayout.frameLayoutRef.get()
            } ?: return@setLinearFrameLayout
//                                    withContext(Dispatchers.Main) {
//                                            EditComponent.AdapterSetter.makeContentsFrameLayout(
//                                                context
//                                            )
//                                        }
            val tagToTextViewListForContents =
                returnContentsFrameLayout.tagToTextViewListRef.get()
                    ?: return@setLinearFrameLayout
            val tagToImageViewListForContents =
                returnContentsFrameLayout.tagToImageViewListRef.get()
                    ?: return@setLinearFrameLayout
            val clickTagToViewList =
                returnContentsFrameLayout.clickTagToViewListRef.get()
            val clickViewTagList = clickTagToViewList?.map {
                    (tagName, _) ->
                tagName
            }
            val textOrImageTagListForContents =
                textTagToMapForContents.keys.toList() +
                        imageTagToMapForContents.keys.toList()
            val tagGenreForContents =
                EditComponent.Template.TagManager.TagGenre.TEXT_OR_IMAGE_TAG
            val isDuplicateTextOrImageTagErrJobForContents = withContext(Dispatchers.IO) {
                val alreadyUseTagListSrc =
                    EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                        alreadyUseTagList,
                        alreadyUseTagListMutex
                    )
                run dupCheckTextOrImageTag@{
                    textOrImageTagListForContents.forEach {
                            textOrImageTag ->
                        val correctContentsTag =
                            EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                context,
                                tagGenreForContents,
                                textOrImageTag,
                                alreadyUseTagListSrc,
                                mapListElInfoForContentsTagWithReplace,
                                String(),
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
                        ) return@dupCheckTextOrImageTag true
                    }
                    false
                }
            }
            if(
                isDuplicateTextOrImageTagErrJobForContents
            ) return@setLinearFrameLayout
            CoroutineScope(Dispatchers.IO).launch {
//                                    val varNameToBitmapMapInContents =
                val imageView = withContext(Dispatchers.IO){
                    contentsLayout.children.firstOrNull {
                            view ->
                        view is AppCompatImageView
                    } as? AppCompatImageView
                }
                withContext(Dispatchers.IO){
                    val topLevelVarNameToBitmapMap =
                        globalVarNameToBitmapMap + (varNameToBitmapMapInFrame ?: emptyMap())
//                                        FileSystems.updateFile(
//                                            File(UsePath.cmdclickDefaultAppDirPath, "lglobal.txt").absolutePath,
//                                            listOf(
//                                                "topLevelVarNameToBitmapMap: ${topLevelVarNameToBitmapMap}"
//                                            ).joinToString("\n")
//                                        )
                    ImageActionManager().exec(
                        context,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        imageView,
                        requestBuilderSrc,
                        imageActionAsyncCoroutine,
                        topLevelVarNameToBitmapMap.map {
                            it.key
                        },
                        topLevelVarNameToBitmapMap,
                        contentsKeyPairsListCon,
                        mapListElInfoForContentsTagWithReplace
                    )
                }
            }
            withContext(Dispatchers.Main) {
                baseLayoutForAdd?.addView(contentsLayout)
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
                    textTagToMapForContents,
                    tagToTextViewListForContents,
                    imageTagToMapForContents,
                    tagToImageViewListForContents,
                    0,
//                                        execContentsTag,
//                                        editConstraintListAdapter?.totalSettingValMap,
                    mapListElInfoForContentsTagWithReplace,
                    clickViewTagList,
//                        enableClick,
//                        clickViewStrList,
                    outValue,
                    requestBuilderSrc,
                    density,
                )
            }
            val clickViewList = clickTagToViewList?.map {
                    (_, view) ->
                view
            }
            if(
                clickViewList.isNullOrEmpty()
            ) return@setLinearFrameLayout
            CoroutineScope(Dispatchers.IO).launch execClick@{
                if(
                    sceneType == SceneType.BK
//                                || outValue == null
                ) return@execClick
                clickHandler(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    editListRecyclerView,
                    contentsKeyPairsListCon,
                    contentsKeyPairsList,
                    clickViewList,
//                    enableClick,
//                    clickViewStrList,
//                                outValue,
                )
            }
        }
    }


    private suspend fun setImageView(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        execContentsTag: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsKeyPairsListCon: String,
        tagIdMap: Map<String, Int>,
        idInt: Int?,
        sceneType: SceneType,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        globalVarNameToBitmapMap: Map<String, Bitmap?>,
        varNameToBitmapMapInFrame: Map<String, Bitmap?>?,
        baseLayoutForAdd: ConstraintLayout?,
//        enableClick: Boolean,
//        clickViewStrList: List<String>,
        outValue: TypedValue?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        mapListElInfoForContentsTagWithReplace: String,
    ){
        val context = fragment?.context
            ?: return
        val editConstraintListAdapter =
            editListRecyclerView?.adapter as? EditConstraintListAdapter
        val imageView = withContext(Dispatchers.Main) {
            EditComponent.AdapterSetter.makeImageViewLayout(
                context,
                idInt,
                execContentsTag,
            )
        }
        withContext(Dispatchers.Main) {
            baseLayoutForAdd?.addView(imageView)
        }
        CoroutineScope(Dispatchers.IO).launch {
//                                    val varNameToBitmapMapInContents =
            withContext(Dispatchers.IO){
                val topLevelVarNameToBitmapMap =
                    globalVarNameToBitmapMap + (varNameToBitmapMapInFrame ?: emptyMap())
//                                        FileSystems.updateFile(
//                                            File(UsePath.cmdclickDefaultAppDirPath, "lglobal.txt").absolutePath,
//                                            listOf(
//                                                "topLevelVarNameToBitmapMap: ${topLevelVarNameToBitmapMap}"
//                                            ).joinToString("\n")
//                                        )
                ImageActionManager().exec(
                    context,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    imageView,
                    requestBuilderSrc,
                    imageActionAsyncCoroutine,
                    topLevelVarNameToBitmapMap.map {
                        it.key
                    },
                    topLevelVarNameToBitmapMap,
                    contentsKeyPairsListCon,
                    mapListElInfoForContentsTagWithReplace,
                    editConstraintListAdapterArg = editConstraintListAdapter,
                )
            }
        }
        val enableClick =
            withContext(Dispatchers.IO) {
                EditComponent.Template.ClickManager.isClickEnable(
                    contentsKeyPairsList.toMap()
                )
            }
        CoroutineScope(Dispatchers.Main).launch {
            EditImageViewSetter.setForConstraint(
                context,
                tagIdMap,
                imageView,
                contentsKeyPairsList,
                0,
                enableClick,
                outValue,
                requestBuilderSrc,
                density,
                mapListElInfoForContentsTagWithReplace,
            )
        }
        CoroutineScope(Dispatchers.IO).launch execClick@{
            if(
                sceneType == SceneType.BK
//                                || outValue == null
            ) return@execClick
            clickHandler(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
                contentsKeyPairsListCon,
                contentsKeyPairsList,
                listOf(imageView),
//                enableClick,
//                clickViewStrList,
//                                outValue,
            )
        }
    }

    private suspend fun setTextView(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        execContentsTag: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsKeyPairsListCon: String,
        tagIdMap: Map<String, Int>,
        idInt: Int?,
        sceneType: SceneType,
        baseLayoutForAdd: ConstraintLayout?,
//        enableClick: Boolean,
//        clickViewStrList: List<String>,
        outValue: TypedValue?,
        density: Float,
        mapListElInfoForContentsTagWithReplace: String,
    ){
        val context = fragment?.context
            ?: return
        val editConstraintListAdapter =
            editListRecyclerView?.adapter as? EditConstraintListAdapter
        val textView = withContext(Dispatchers.Main) {
            EditComponent.AdapterSetter.makeTextViewLayout(
                context,
                idInt,
                execContentsTag,
            )
        }
        withContext(Dispatchers.Main) {
            baseLayoutForAdd?.addView(textView)
        }
        val enableClick =
            withContext(Dispatchers.IO) {
                EditComponent.Template.ClickManager.isClickEnable(
                    contentsKeyPairsList.toMap()
                )
            }
        CoroutineScope(Dispatchers.Main).launch {
            EditTextViewSetter.setForConstraint(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                tagIdMap,
                textView,
                contentsKeyPairsList,
                contentsKeyPairsListCon,
                editConstraintListAdapter?.getTotalSettingValMap()?.get(
                    execContentsTag
                ),
                0,
                enableClick,
                outValue,
                density,
                mapListElInfoForContentsTagWithReplace,
            )
        }
        CoroutineScope(Dispatchers.IO).launch execClick@{
            if(
                sceneType == SceneType.BK
//                                || outValue == null
            ) return@execClick
            clickHandler(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
                contentsKeyPairsListCon,
                contentsKeyPairsList,
                listOf(textView),
//                enableClick,
//                clickViewStrList,
//                                outValue,
            )
        }
    }

    private fun makeBaseLayout(
        sceneType: SceneType,
        editListTitleConstraintLayout: ConstraintLayout?,
        editListToolbarConstraintLayout: ConstraintLayout?,
        editListFooterConstraintLayout: ConstraintLayout?,
        editBkConstraintLayout: ConstraintLayout?,
    ): ConstraintLayout? {
        return when(sceneType){
            SceneType.TITLE
                -> editListTitleConstraintLayout
            SceneType.TOOLBAR
                -> editListToolbarConstraintLayout
            SceneType.FOOTER ->  editListFooterConstraintLayout
            SceneType.BK -> editBkConstraintLayout
        }
    }

    private fun makeTagIdMap(
        sceneType: SceneType,
        tagIdMapSrc: Map<String, Int>,
        eachLayoutIdMap: Map<String, Int>?,
    ): Map<String, Int> {
        return when(sceneType){
            SceneType.TITLE,
            SceneType.TOOLBAR,
            SceneType.FOOTER -> tagIdMapSrc
            SceneType.BK -> tagIdMapSrc + (eachLayoutIdMap ?: emptyMap())
        }
    }

    private suspend fun clickHandler(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView?,
        contentsKeyPairsListCon: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        clickViewList: List<View>?,
//        contentsFrameLayout: FrameLayout,
//        enableClick: Boolean,
//        clickViewStrList: List<String>,
//        outValue: TypedValue,
    ){

        if (
            contentsKeyPairsListCon.isNullOrEmpty()
        ) return
        withContext(Dispatchers.IO) execExecClick@{
//            val clickViewList =
//                EditComponent.Template.ClickViewManager.makeClickViewList(
//                    contentsFrameLayout.children,
//                    clickViewStrList,
////                    PairListTool.getValue(
////                        contentsKeyPairsList,
////                        onClickViewsKey,
////                    )
//                )
            val isConsec =
                PairListTool.getValue(
                    contentsKeyPairsList,
                    onConsecKey,
                ) == EditComponent.Template.switchOn
            clickViewList?.forEachIndexed { index, clickView ->
//                val enableClick = EditComponent.Template.ClickManager.isClickEnable(contentsKeyPairsList)
//                withContext(Dispatchers.Main) {
//                    clickView.isClickable = enableClick
//                }
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.IO){
//                        delay(delayTime)
//                    }
//                    withContext(Dispatchers.Main){
//                        when(enableClick) {
//                            false -> clickView.setBackgroundResource(0)
//                            else -> clickView.setBackgroundResource(outValue.resourceId)
//                        }
//                    }
//                }
//                if(!enableClick) return@execExecClick
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
        editConstraintListAdapter.editAdapterClickListener =
            object: EditConstraintListAdapter.OnEditAdapterClickListener {
                override fun onEditAdapterClick(
                    itemView: View,
                    frameTag: String?,
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
//                            "itemView: ${itemView.tag}",
//                            "frameTag: ${frameTag}",
//                        ).joinToString("\n")
//                    )
//                    val frameLayout = when(true){
//                        (itemView is MaterialCardView) -> {
//                            itemView.children.firstOrNull {
//                                it is FrameLayout
//                            } as FrameLayout
//                        }
//                        (itemView is FrameLayout) -> itemView as FrameLayout
//                        else -> null
//                    } ?: return
//                    val frameLayout = itemView as FrameLayout
                    val tag = frameTag
                        ?: itemView.tag?.toString()
                        ?: return
//                        frameLayout.tag as String?
//                        ?: return
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
//                    frameLayout.children.firstOrNull{
//                        it is OutlineTextView
//                    }
                    editConstraintListAdapter.getTotalSettingValMap().get(tag)?.let {
                            curSettingValue ->
//                        val textView = it as OutlineTextView
//                        val curSettingValue = textView.autofillHints?.firstOrNull()
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
        var execTouchJob: Job? = null
        var consecutiveJob: Job? = null
        editConstraintListAdapter.editAdapterTouchUpListener = object: EditConstraintListAdapter.OnEditAdapterTouchUpListener {
            override fun onEditAdapterTouchUp(
                itemView: View,
                frameTag: String?,
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
                frameTag: String?,
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
//                val frameLayout = when(true){
//                    (itemView is MaterialCardView) -> {
//                        itemView.children.firstOrNull {
//                            it is FrameLayout
//                        } as FrameLayout
//                    }
//                    (itemView is FrameLayout) -> itemView as FrameLayout
//                    else -> null
//                } ?: return
//                val frameLayout = itemView as FrameLayout
                val tag = frameTag
                    ?: itemView.tag as String?
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
//                val textView = frameLayout.children.firstOrNull{
//                    it is OutlineTextView
//                } as? OutlineTextView
                consecutiveJob?.cancel()
                consecutiveJob = CoroutineScope(Dispatchers.IO).launch {
                    var roopTimes = 0
                    while (true) {
                        execTouchJob = CoroutineScope(Dispatchers.Main).launch touch@ {
                            withContext(Dispatchers.Main) {
                                val curSettingValue =
                                    editConstraintListAdapter.getTotalSettingValMap().get(tag)
//                                    textView?.autofillHints?.firstOrNull()
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
                            title.lowercase().contains(
                                searchText.text.toString()
                                    .lowercase()
                                    .replace("\n", "")
                            )
//                            Regex(
//                                searchText.text.toString()
//                                    .lowercase()
//                                    .replace("\n", "")
//                            ).containsMatchIn(
//                                title.lowercase()
//                            )
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

