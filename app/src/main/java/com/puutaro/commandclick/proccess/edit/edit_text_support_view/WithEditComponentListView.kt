package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForListIndexAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.util.Keyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object WithEditComponentListView{

    fun keyboardHide(
        editFragment: EditFragment,
    ){
        editFragment.binding.editListSearchEditText.setText(String())
        val listener = editFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
        listener?.onKeyBoardVisibleChangeForEditFragment(
            false,
            true,
        )
        Keyboard.hiddenKeyboardForFragment(editFragment)
    }

    val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

    fun create(
        editFragment: EditFragment,
    ) {
        val context = editFragment.context ?: return
        val binding = editFragment.binding
        val editListSearchEditText = binding.editListSearchEditText

//        binding.editListLinearLayout.isVisible = true
//        binding.editTextScroll.isVisible = false
        val listIndexConfigMap = editFragment.listIndexConfigMap
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
        val fileList = ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            indexListMap,
            editFragment.busyboxExecutor,
//            listIndexTypeKey
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lfileList.txt").absolutePath,
//            listOf(
//                "indexListMap: ${indexListMap}",
//                "fileList: ${fileList}",
//            ).joinToString("\n\n")
//        )

        val editListRecyclerView =
            binding.editListRecyclerView
//        val constraintLayoutParam = editListRecyclerView.layoutParams as ConstraintLayout.LayoutParams
        val editComponentListAdapter = EditComponentListAdapter(
            editFragment.context,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.listIndexConfigMap,
            editFragment.busyboxExecutor,
            indexListMap,
            fileList,
        )
        ItemTouchHelperCallbackForListIndexAdapter.set(
            editFragment,
            editListRecyclerView,
            editComponentListAdapter,
        )

        editListRecyclerView.adapter = editComponentListAdapter


        val isReverseLayout = ListSettingsForListIndex.howReverseLayout(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            indexListMap
        )
        LayoutSettingsForListIndex.setLayout(
            context,
            editComponentListAdapter.getLayoutConfigMap(),
            editListRecyclerView,
            isReverseLayout,
        )
        ListViewToolForListIndexAdapter.scrollToBottom(
            editListRecyclerView,
            editComponentListAdapter,
        )
        invokeItemSetClickListenerForFileList(
            editFragment,
            binding.editListRecyclerView
        )
        invokeItemSetTouchListenerForFileList(
            editFragment,
            binding.editListRecyclerView
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
            editFragment,
            editComponentListAdapter,
            editListSearchEditText,
            listIndexConfigMap
        )
    }

    private fun invokeItemSetClickListenerForFileList(
        editFragment: EditFragment,
        editListRecyclerView: RecyclerView
    ) {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
        listIndexForEditAdapter.editAdapterClickListener =
            object: EditComponentListAdapter.OnEditAdapterClickListener {
                override fun onEditAdapterClick(
                    itemView: View,
                    holder: EditComponentListAdapter.ListIndexListViewHolder,
                    listIndexPosition: Int,
                ) {
                    val tag = itemView.tag as String?
                        ?: return
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lclcik.txt").absolutePath,
//                        listOf(
//                            "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                            "lineMapList: ${listIndexForEditAdapter.lineMapList}",
//                        ).joinToString("\n")
//                    )
                    val selectedItemLineMap =
                        listIndexForEditAdapter.lineMapList.getOrNull(holder.bindingAdapterPosition)
                            ?: return
                    keyboardHide(
                        editFragment,
                    )
                    val jsAcCon = holder.keyPairListConMap.get(tag)
                        ?: return
                    ListIndexEditConfig.handle(
                        editFragment,
                        false,
                        selectedItemLineMap,
                        jsAcCon,
                        listIndexPosition
                    )
                }
            }
    }


    private fun invokeItemSetTouchListenerForFileList(
        editFragment: EditFragment,
        editListRecyclerView: RecyclerView
    ) {
        var execTouchJob: Job? = null
        var consecutiveJob: Job? = null
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
        listIndexForEditAdapter.editAdapterTouchUpListener = object: EditComponentListAdapter.OnEditAdapterTouchUpListener {
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

        listIndexForEditAdapter.editAdapterTouchDownListener = object: EditComponentListAdapter.OnEditAdapterTouchDownListener {
            override fun onEditAdapterTouchDown(
                itemView: View,
                holder: EditComponentListAdapter.ListIndexListViewHolder,
                listIndexPosition: Int
            ) {
                val tag = itemView.tag as String?
                    ?: return
                val selectedItemLineMap =
                    listIndexForEditAdapter.lineMapList.getOrNull(holder.bindingAdapterPosition)
                        ?: return
                val jsAcCon = holder.keyPairListConMap.get(tag)
                    ?: return
                consecutiveJob?.cancel()
                consecutiveJob = CoroutineScope(Dispatchers.IO).launch {
                    var roopTimes = 0
                    while (true) {
                        execTouchJob = CoroutineScope(Dispatchers.Main).launch touch@ {
                            withContext(Dispatchers.Main) {
                                ListIndexEditConfig.handle(
                                    editFragment,
                                    true,
                                    selectedItemLineMap,
                                    jsAcCon,
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
        editFragment: EditFragment,
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
                    editFragment,
                    it
                )
                else -> TitleImageAndViewSetter.makeDefaultTitle(
                    editFragment,
                )
            }
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                    editFragment.fannelInfoMap,
                    editFragment.setReplaceVariableMap,
                    editComponentListAdapter.indexListMap,
                    editFragment.busyboxExecutor,
//                    ListIndexAdapter.listIndexTypeKey
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
                    editFragment,
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

