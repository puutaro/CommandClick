package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.FannelLogoLongClickDoForListIndex
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForListIndexAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrDialogClickHandler
import com.puutaro.commandclick.util.Keyboard


object WithIndexListView{

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

//    var languageType = LanguageTypeSelects.JAVA_SCRIPT
//    var languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//            languageType
//        )
//    var settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//
//    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String
    val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

    fun create(
        editFragment: EditFragment,
        editParameters: EditParameters,
    ) {
        val context = editFragment.context ?: return
        val binding = editFragment.binding
        val editListSearchEditText = binding.editListSearchEditText

        binding.editListLinearLayout.isVisible = true
        binding.editTextScroll.isVisible = false
        val listIndexConfigMap = editFragment.listIndexConfigMap
        val listIndexTypeKey = ListIndexEditConfig.getListIndexType(
            editFragment
        )
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
            editFragment,
            indexListMap,
            listIndexTypeKey
        )

        val editListRecyclerView =
            binding.editListRecyclerView
        val listIndexForEditAdapter = ListIndexAdapter(
            editFragment,
            fileList,
        )
        ItemTouchHelperCallbackForListIndexAdapter.set(
            editFragment,
            editListRecyclerView,
            listIndexForEditAdapter,
        )

        editListRecyclerView.adapter = listIndexForEditAdapter


        val isReverseLayout = ListSettingsForListIndex.howReverseLayout(
            editFragment,
            indexListMap
        )
        LayoutSettingsForListIndex.setLayout(
            context,
            listIndexForEditAdapter.getLayoutConfigMap(),
            editListRecyclerView,
            isReverseLayout,
        )
        ListViewToolForListIndexAdapter.scrollToBottom(
            editListRecyclerView,
            listIndexForEditAdapter,
        )
        invokeItemSetClickListenerForFileList(
            editFragment,
            binding.editListRecyclerView
        )
        invokeQrLogoSetClickListenerForFileList(
            editFragment,
            binding.editListRecyclerView,
        )
        invokeQrLogoSetLongClickListenerForFileList(editFragment)
        invokeItemSetLongTimeClickListenerForIndexList(
            editFragment,
            editListRecyclerView,
        )
        makeSearchEditText(
            editFragment,
            editListSearchEditText,
            listIndexConfigMap
        )
    }

        private fun invokeItemSetClickListenerForFileList(
            editFragment: EditFragment,
            editListRecyclerView: RecyclerView
        ) {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexAdapter
        listIndexForEditAdapter.fileNameClickListener =
            object: ListIndexAdapter.OnFileNameItemClickListener {
                override fun onFileNameClick(
                    itemView: View,
                    holder: ListIndexAdapter.ListIndexListViewHolder,
                    listIndexPosition: Int,
                ) {
                    keyboardHide(
                        editFragment,
                    )
                    val selectedItem = holder.fileName
                    ListIndexEditConfig.handle(
                        editFragment,
                        false,
                        selectedItem,
                        holder,
                        listIndexPosition
                    )
                }
        }
    }

    private fun invokeQrLogoSetClickListenerForFileList(
        editFragment: EditFragment,
        editListRecyclerView: RecyclerView,
    ) {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexAdapter
        listIndexForEditAdapter.fileQrLogoClickListener = object: ListIndexAdapter.OnFileQrLogoItemClickListener {
            override fun onFileQrLogoClick(
                itemView: View,
                holder: ListIndexAdapter.ListIndexListViewHolder,
                listIndexPosition: Int,
            ) {
                keyboardHide(editFragment)
                QrDialogClickHandler.handle(
                    false,
                    editFragment,
                    holder.fileName,
                    holder.bindingAdapterPosition,
                )
            }
        }
    }

    private fun invokeQrLogoSetLongClickListenerForFileList(
        editFragment: EditFragment,
    ) {
        FannelLogoLongClickDoForListIndex.invoke(
            editFragment,
        )
    }

    private fun makeSearchEditText(
        editFragment: EditFragment,
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
                    editFragment,
                    ListIndexAdapter.indexListMap,
                    ListIndexAdapter.listIndexTypeKey
                ).filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                    editFragment,
                    filteredUrlHistoryList,
                )
            }
        })
    }

    private fun invokeItemSetLongTimeClickListenerForIndexList(
        editFragment: EditFragment,
        editListRecyclerView: RecyclerView,
    ){
        val indexForEditAdapter = editListRecyclerView.adapter as ListIndexAdapter
        indexForEditAdapter.itemLongClickListener = object : ListIndexAdapter.OnItemLongClickListener {
            override fun onItemLongClick(
                itemView: View,
                holder: ListIndexAdapter.ListIndexListViewHolder,
                listIndexPosition: Int
            ) {
                val selectedItem = holder.fileName
                ListIndexEditConfig.handle(
                    editFragment,
                    true,
                    selectedItem,
                    holder,
                    listIndexPosition,
                )
            }
        }
    }
}

