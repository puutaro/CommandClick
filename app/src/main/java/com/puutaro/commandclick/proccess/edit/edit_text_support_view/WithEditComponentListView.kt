package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForListIndexAdapter
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.list_index_for_edit.EditFrameMaker
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.BkImageSettingsForEditList
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        listIndexConfigMap: Map<String, String>?,
        editTextView: AppCompatTextView,
        editTitleImage: AppCompatImageView,
        editListRecyclerView: RecyclerView,
        editListBkFrame: FrameLayout,
        editListSearchEditText: AppCompatEditText,
        editFooterLinearlayout: LinearLayoutCompat,
        fannelContentsList: List<String>?,
    ) {
        val context = fragment.context ?: return

        CoroutineScope(Dispatchers.Main).launch{
            val titleSettingPath = listIndexConfigMap?.get(
                ListIndexEditConfig.ListIndexConfigKey.TITLE_SETTING_PATH.key
            ) ?: String()
            val titleSettingMap = withContext(Dispatchers.IO){
                ListSettingVariableListMaker.makeFromSettingPath(
                    titleSettingPath,
                    fannelInfoMap,
                    setReplaceVariableMap,
                )
            }
            TitleImageAndViewSetter.set(
                fragment,
                editTextView,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editTitleImage,
                titleSettingMap
            )
        }
        val editListBkPairs = ListIndexEditConfig.getConfigKeyConList(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.BK.key
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
        val indexListMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LIST.key
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
        val lineMapList = ListSettingsForListIndex.ListIndexListMaker.makeLineMapListHandler(
            fannelInfoMap,
            setReplaceVariableMap,
            indexListMap,
            busyboxExecutor,
//            listIndexTypeKey
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lfileList.txt").absolutePath,
//            listOf(
//                "indexListMap: ${indexListMap}",
//                "fileList: ${fileList}",
//            ).joinToString("\n\n")
//        )

//        val editListRecyclerView =
//            binding.editListRecyclerView
//        val constraintLayoutParam = editListRecyclerView.layoutParams as ConstraintLayout.LayoutParams
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lfannelContentsList.txt").absolutePath,
//            fannelContentsList?.joinToString("\n") ?: String()
//        )
        val editComponentListAdapter = EditComponentListAdapter(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            listIndexConfigMap,
            busyboxExecutor,
            indexListMap,
            lineMapList,
            fannelContentsList
        )
        val layoutConfigMap = LayoutSettingsForListIndex.getLayoutConfigMap(
            listIndexConfigMap,
        )
        ItemTouchHelperCallbackForListIndexAdapter.set(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
            editComponentListAdapter,
            layoutConfigMap
        )

        editListRecyclerView.adapter = editComponentListAdapter


        val isReverseLayout = LayoutSettingsForListIndex.howReverseLayout(
            fannelInfoMap,
            setReplaceVariableMap,
            layoutConfigMap
        )
        LayoutSettingsForListIndex.setLayout(
            context,
            editComponentListAdapter.getLayoutConfigMap(),
            editListRecyclerView,
            isReverseLayout,
        )

//        if(isReverseLayout) {
//            ListViewToolForListIndexAdapter.scrollToBottom(
//                editListRecyclerView,
//                editComponentListAdapter,
//            )
//        }
        CoroutineScope(Dispatchers.Main).launch {
            setFooter(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editFooterLinearlayout,
                listIndexConfigMap,
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
            listIndexConfigMap
        )
    }

    private suspend fun setFooter(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editFooterLinearlayout: LinearLayoutCompat,
        listIndexConfigMap: Map<String, String>?,
    ) {
        val context = fragment.context
            ?: return
        val footerLayoutPath = ListSettingsForListIndex.ViewLayoutPathManager.getViewLayoutPath(
            fannelInfoMap,
            setReplaceVariableMap,
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.FOOTER_LAYOUT_PATH.key,
        )
        val frameMapListToLinearMapList = ListSettingsForListIndex.ViewLayoutPathManager.parse(
            fannelInfoMap,
            setReplaceVariableMap,
            footerLayoutPath
        )
        val frameMap = frameMapListToLinearMapList?.first ?: emptyMap()
        val frameTagList = frameMap.keys
        val frameTagToLinearKeysListMap = frameMapListToLinearMapList?.second ?: emptyMap()

        val tagKey = EditComponent.Template.EditComponentKey.TAG.key
        val typeSeparator = EditComponent.Template.typeSeparator
        val isConsecKey = EditComponent.Template.EditComponentKey.IS_CONSEC.key
//            GridLayoutManager
        withContext(Dispatchers.Main) {
            frameTagList.forEach { frameTag ->
                frameTagToLinearKeysListMap.get(frameTag)?.forEach {
                        linearKeys ->

                    val linearParam = LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                    )
                    val weightSumFloat = 1f
                    val linearLayout = LinearLayoutCompat(context).apply {
                        layoutParams = linearParam
                        weightSum = weightSumFloat
                        orientation = LinearLayoutCompat.HORIZONTAL
                    }

                    val layoutWeight = weightSumFloat / linearKeys.size
                    linearKeys.forEach setFrame@{ linearFrameKeyPairsListConSrc ->
                        val linearFrameKeyPairsListCon = withContext(Dispatchers.IO) {
                            EditComponent.Template.ReplaceHolder.replaceHolder(
                                linearFrameKeyPairsListConSrc,
                                frameTag,
                                frameTag
                            )
                        }
                        val linearFrameKeyPairsList = withContext(Dispatchers.IO) {
                            CmdClickMap.createMap(
                                linearFrameKeyPairsListCon,
                                typeSeparator
                            )
                        }
                        val linearFrameTag = withContext(Dispatchers.IO) {
                            PairListTool.getValue(
                                linearFrameKeyPairsList,
                                tagKey,
                            ) ?: String()
                        }
                        val linearFrameLayout = EditFrameMaker.make(
                            context,
                            fannelInfoMap,
                            setReplaceVariableMap,
                            busyboxExecutor,
                            linearFrameKeyPairsList,
                            0,
                            layoutWeight,
                            linearFrameTag,
                            false,
                            null
                        ) ?: return@setFrame
                        val linearKeyList = JsActionKeyManager.JsActionsKey.values().map {
                            it.key
                        }
                        withContext(Dispatchers.IO) execClick@ {
                            if(
                                linearFrameKeyPairsListCon.isNullOrEmpty()
                            ) return@execClick
                            val isConsec =
                                PairListTool.getValue(
                                    linearFrameKeyPairsList,
                                    isConsecKey,
                                ) == EditComponent.Template.switchOn
                            linearKeyList.any {
                                !PairListTool.getValue(
                                    linearFrameKeyPairsList,
                                    it,
                                ).isNullOrEmpty()
                            }.let { isJsAc ->
                                if (
                                    !isJsAc
                                    && linearFrameLayout.tag != null
                                ) {
                                    linearFrameLayout.setBackgroundResource(0)
                                    linearFrameLayout.isClickable = false
                                    return@let
                                }
                                val outValue = TypedValue()
                                context.theme.resolveAttribute(
                                    android.R.attr.selectableItemBackground,
                                    outValue,
                                    true
                                )
                                linearFrameLayout.setBackgroundResource(outValue.resourceId)
                                linearFrameLayout.isClickable = true
                                if(!isConsec){
                                    linearFrameLayout.setOnClickListener {
                                        execJsAction(
                                            fragment,
                                            fannelInfoMap,
                                            setReplaceVariableMap,
                                            linearFrameKeyPairsListCon
                                        )
                                    }
                                    return@execClick
                                }
                                with(linearFrameLayout) {
                                    var consecutiveJob: Job? = null
                                    setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                                        var execTouchJob: Job? = null
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> {
                                                consecutiveJob?.cancel()
                                                consecutiveJob = CoroutineScope(Dispatchers.IO).launch {
                                                    var roopTimes = 0
                                                    while (true) {
                                                        execTouchJob = CoroutineScope(Dispatchers.Main).launch {
                                                            execJsAction(
                                                                fragment,
                                                                fannelInfoMap,
                                                                setReplaceVariableMap,
                                                                linearFrameKeyPairsListCon
                                                            )
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
                                            }
                                            MotionEvent.ACTION_UP,
                                            MotionEvent.ACTION_CANCEL, -> {
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
                        linearLayout.addView(linearFrameLayout)
                    }
                    editFooterLinearlayout.addView(linearLayout)
                }
            }
        }
    }

    private fun execJsAction(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        jsAcCon: String,
    ){
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
                    holder: EditComponentListAdapter.ListIndexListViewHolder,
                    listIndexPosition: Int,
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

                    val frameOrLinearCon = holder.keyPairListConMap.get(tag)
                        ?: return
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
                            listIndexPosition,
                            frameOrLinearCon
                        )
                    }
                    ListIndexEditConfig.handle(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        editListRecyclerView,
//                        false,
                        selectedItemLineMap,
                        frameOrLinearCon,
                        listIndexPosition
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
                holder: EditComponentListAdapter.ListIndexListViewHolder,
                listIndexPosition: Int
            ) {
                execTouchJob?.cancel()
                consecutiveJob?.cancel()
                return
            }
        }

        editComponentListAdapter.editAdapterTouchDownListener = object: EditComponentListAdapter.OnEditAdapterTouchDownListener {
            override fun onEditAdapterTouchDown(
                itemView: View,
                holder: EditComponentListAdapter.ListIndexListViewHolder,
                listIndexPosition: Int
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
                                    listIndexPosition,
                                    frameOrLinearCon
                                )
                                ListIndexEditConfig.handle(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    editListRecyclerView,
//                                    true,
                                    selectedItemLineMap,
                                    frameOrLinearCon,
                                    listIndexPosition
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
        listIndexConfigMap: Map<String, String>?
    ) {
        val searchBoxMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.SEARCH_BOX.key
        )
        val inVisible =
            searchBoxMap.get(
                SearchBoxSettingsForListIndex.SearchBoxSettingKey.VISIBLE.key
            ) == SearchBoxSettingsForListIndex.SearchBoxVisibleKey.OFF.name
        if(inVisible){
            searchText.isVisible = false
            return
        }
        searchText.hint = searchBoxMap.get(
            SearchBoxSettingsForListIndex.SearchBoxSettingKey.HINT.key
        ).let {
            when(it.isNullOrEmpty()) {
                false -> SearchBoxSettingsForListIndex.makeCurrentVariableValueInEditText(
                    fragment,
                    fannelInfoMap,
                    it
                )
                else -> TitleImageAndViewSetter.makeDefaultTitle(
                    fragment,
                    fannelInfoMap,
                )
            }
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = ListSettingsForListIndex.ListIndexListMaker.makeLineMapListHandler(
                    editComponentListAdapter.fannelInfoMap,
                    editComponentListAdapter.setReplaceVariableMap,
                    editComponentListAdapter.indexListMap,
                    editComponentListAdapter.busyboxExecutor,
                ).filter {
                    lineMap ->
                    val title = lineMap.get(
                        ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
                    ) ?: String()
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        title.lowercase()
                    )
                }
                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                    editComponentListAdapter,
                    filteredUrlHistoryList,
                )
            }
        })
    }
}

