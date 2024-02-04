package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecClickUpdate
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.FannelLogoLongClickDoForListIndex
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.ItemTouchHelperCallbackForListIndexAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrDialogClickHandler
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.state.SharePreferenceMethod


class WithIndexListView(
    private val editFragment: EditFragment
) {
    private val context = editFragment.context
    private val binding = editFragment.binding
    private val editListRecyclerView = binding.editListRecyclerView
    private val editListSearchEditText = binding.editListSearchEditText

    companion object {
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
    }

    var languageType = LanguageTypeSelects.JAVA_SCRIPT
    var languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    var settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    fun create(
        editParameters: EditParameters,
    ) {
        binding.editListLinearLayout.isVisible = true
        binding.editTextScroll.isVisible = false
        val context = editParameters.context ?: return
        val listIndexConfigMap = editFragment.listIndexConfigMap
        val listIndexTypeKey = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        val indexListMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LIST.key
        )
        val fileList = ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
            editFragment,
            indexListMap,
            listIndexTypeKey
        )

        val editListRecyclerView =
            binding.editListRecyclerView
        val listIndexForEditAdapter = ListIndexForEditAdapter(
            editFragment,
            fileList,
        )
        ItemTouchHelperCallbackForListIndexAdapter.set(
            editFragment,
            editListRecyclerView,
            listIndexForEditAdapter,
        )

        editListRecyclerView.adapter = listIndexForEditAdapter
        val preLoadLayoutManager = PreLoadLayoutManager(
            context,
        )
        preLoadLayoutManager.stackFromEnd = true
        editListRecyclerView.layoutManager = preLoadLayoutManager
        ListViewToolForListIndexAdapter.scrollToBottom(
            editListRecyclerView,
            listIndexForEditAdapter,
        )
        invokeItemSetClickListenerForFileList()
        invokeQrLogoSetClickListenerForFileList(editFragment)
        invokeQrLogoSetLongClickListenerForFileList(editFragment)
        invokeItemSetLongTimeClickListenerForIndexList()
        makeSearchEditText(
            editListSearchEditText,
            listIndexConfigMap
        )
    }

        private fun invokeItemSetClickListenerForFileList() {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fileNameClickListener =
            object: ListIndexForEditAdapter.OnFileNameItemClickListener {
                override fun onFileNameClick(
                    itemView: View,
                    holder: ListIndexForEditAdapter.ListIndexListViewHolder,
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
    ) {
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            editFragment.readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fileQrLogoClickListener = object: ListIndexForEditAdapter.OnFileQrLogoItemClickListener {
            override fun onFileQrLogoClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder,
                listIndexPosition: Int,
            ) {
                keyboardHide(editFragment)
                QrDialogClickHandler.handle(
                    false,
                    editFragment,
                    currentAppDirPath,
                    holder.fileName,
                    listIndexForEditAdapter.qrDialogConfigMap
                )
            }
        }
    }

    private fun invokeQrLogoSetLongClickListenerForFileList(
        editFragment: EditFragment,
    ) {
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            editFragment.readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        FannelLogoLongClickDoForListIndex.invoke(
            editFragment,
            currentAppDirPath,
        )
    }

    private fun makeSearchEditText(
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
                else -> editFragment.editBoxTitle
            }
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
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
    ){
        if(
            context == null
        ) return
        val indexForEditAdapter = editListRecyclerView.adapter as ListIndexForEditAdapter
        indexForEditAdapter.itemLongClickListener = object : ListIndexForEditAdapter.OnItemLongClickListener {
            override fun onItemLongClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder,
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

