package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForListIndexAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.BkImageSettingsForEditList
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.Keyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        editListRecyclerView: RecyclerView,
        editListBkFrame: FrameLayout,
        editListSearchEditText: AppCompatEditText,
        fannelContentsList: List<String>?,
    ) {
        val context = fragment.context ?: return
//        val binding = editFragment.binding
//        val editListSearchEditText = binding.editListSearchEditText
//        val editListBkFrame = binding.editListBkFrame
//        val editListBkImage = binding.editListBkImage


//        binding.editListLinearLayout.isVisible = true
//        binding.editTextScroll.isVisible = false
//        val listIndexConfigMap = editFragment.listIndexConfigMap
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
//        val listIndexTypeKey = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
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
//        invokeQrLogoSetClickListenerForFileList(
//            editFragment,
//            binding.editListRecyclerView,
//        )
//        invokeQrLogoSetLongClickListenerForFileList(editFragment)
//        invokeItemSetLongTimeClickListenerForIndexList(
//            editFragment,
//            editListRecyclerView,
//        )
        makeSearchEditText(
            fragment,
            fannelInfoMap,
            editComponentListAdapter,
            editListSearchEditText,
            listIndexConfigMap
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
//                            "lineMapList: ${listIndexForEditAdapter.lineMapList}",
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
                        it is AppCompatTextView
                    }?.let {
                        val textView = it as AppCompatTextView
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
                    it is AppCompatTextView
                } as? AppCompatTextView
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

//    private fun invokeItemSetClickListenerForFileList(
//        editFragment: EditFragment,
//        editListRecyclerView: RecyclerView
//    ) {
//        val listIndexForEditAdapter =
//            editListRecyclerView.adapter as EditComponentListAdapter
//        listIndexForEditAdapter.editAdapterClickListener =
//            object: EditComponentListAdapter.OnEditAdapterClickListener {
//                override fun onEditAdapterClick(
//                    itemView: View,
//                    holder: EditComponentListAdapter.ListIndexListViewHolder,
//                    listIndexPosition: Int,
//                ) {
//                    keyboardHide(
//                        editFragment,
//                    )
//                    val selectedItemLineMap = listIndexForEditAdapter.lineMapList[holder.bindingAdapterPosition]
////                    holder.fileName
//                    ListIndexEditConfig.handle(
//                        editFragment,
//                        false,
//                        selectedItemLineMap,
//                        holder,
//                        listIndexPosition
//                    )
//                }
//        }
//    }

//    private fun invokeQrLogoSetClickListenerForFileList(
//        editFragment: EditFragment,
//        editListRecyclerView: RecyclerView,
//    ) {
//        val listIndexForEditAdapter =
//            editListRecyclerView.adapter as EditComponentListAdapter
//        listIndexForEditAdapter.fileQrLogoClickListener = object: EditComponentListAdapter.OnFileQrLogoItemClickListener {
//            override fun onFileQrLogoClick(
//                itemView: View,
//                holder: EditComponentListAdapter.ListIndexListViewHolder,
//                listIndexPosition: Int,
//            ) {
//                keyboardHide(editFragment)
//                val selectedItemLineMap = listIndexForEditAdapter.lineMapList[holder.bindingAdapterPosition]
//                QrDialogClickHandler.handle(
//                    false,
//                    editFragment,
//                    selectedItemLineMap,
//                    holder.bindingAdapterPosition,
//                )
//            }
//        }
//    }

//    private fun invokeQrLogoSetLongClickListenerForFileList(
//        editFragment: EditFragment,
//    ) {
//        FannelLogoLongClickDoForListIndex.invoke(
//            editFragment,
//        )
//    }

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
//                    ListIndexAdapter.listIndexTypeKey
                ).filter {
                    lineMap ->
                    val title = lineMap.get(
                        ListSettingsForListIndex.MapListPathManager.Key.SRC_LABEL.key
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

//    private fun invokeItemSetLongTimeClickListenerForIndexList(
//        editFragment: EditFragment,
//        editListRecyclerView: RecyclerView,
//    ){
//        val indexForEditAdapter = editListRecyclerView.adapter as EditComponentListAdapter
//        indexForEditAdapter.itemLongClickListener = object : EditComponentListAdapter.OnItemLongClickListener {
//            override fun onItemLongClick(
//                itemView: View,
//                holder: EditComponentListAdapter.ListIndexListViewHolder,
//                listIndexPosition: Int
//            ) {
//                val selectedItemLineMap = indexForEditAdapter.lineMapList[holder.bindingAdapterPosition]
////                    holder.fileName
//                ListIndexEditConfig.handle(
//                    editFragment,
//                    true,
//                    selectedItemLineMap,
//                    holder,
//                    listIndexPosition,
//                )
//            }
//        }
//    }
}

