package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.FannelLogoLongClickDoForListIndex
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrDialogClickHandler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.state.SharePreferenceMethod


class WithIndexListView(
    private val editFragment: EditFragment
) {
    private val context = editFragment.context
    private val binding = editFragment.binding
    private val busyboxExecutor = editFragment.busyboxExecutor
    private var currentSetVariableMap: Map<String, String>? = mapOf()
    private var currentAppDirPath = String()
    private var currentScriptName = String()
    private val editListRecyclerView = binding.editListRecyclerView
    private val editListSearchEditText = binding.editListSearchEditText

    companion object {

        private var fannelDirName = String()
        private var fannelDirPath = String()
        private var fannelMenuDirPath = String()

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
        val context = editParameters.context
        currentSetVariableMap = editParameters.setVariableMap
        currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        currentScriptName = SharePreferenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        fannelDirName = CcPathTool.makeFannelDirName(
            currentScriptName
        )
        fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
        fannelMenuDirPath = "${fannelDirPath}/menu"

        val listIndexConfigMap = editFragment.listIndexConfigMap
        val listIndexTypeKey = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        ListIndexForEditAdapter.listIndexTypeKey = listIndexTypeKey

        val indexListMap = ListIndexForEditAdapter.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LIST.key
        )

        ListIndexForEditAdapter.filterDir = ListIndexForEditAdapter.getFilterListDir(
            indexListMap,
            listIndexTypeKey,
            currentAppDirPath,
            currentScriptName
        )
        ListIndexForEditAdapter.filterPrefix = ListIndexForEditAdapter.getFilterPrefix(
            indexListMap,
        )
        ListIndexForEditAdapter.filterSuffix = ListIndexForEditAdapter.getFilterSuffix(
            indexListMap,
        )

        ListIndexForEditAdapter.filterShellCon = ListIndexForEditAdapter.getFilterShellCon(
            indexListMap,
            editParameters
        )

        FileSystems.createDirs(ListIndexForEditAdapter.filterDir)

        val fileList = ListIndexForEditAdapter.makeFileListHandler(
            busyboxExecutor,
            listIndexTypeKey
        )

        val editListRecyclerView =
            binding.editListRecyclerView
        val listIndexForEditAdapter = ListIndexForEditAdapter(
            editFragment,
            fileList,
        )
        editListRecyclerView.adapter = listIndexForEditAdapter
        val preLoadLayoutManager = PreLoadLayoutManager(
            context,
        )
        preLoadLayoutManager.stackFromEnd = true
        editListRecyclerView.layoutManager = preLoadLayoutManager

        invokeItemSetClickListenerForFileList()
        invokeQrLogoSetClickListenerForFileList()
        invokeQrLogoSetLongClickListenerForFileList()
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
                    holder: ListIndexForEditAdapter.ListIndexListViewHolder
                ) {
                    keyboardHide(
                        editFragment,
                    )
                    val selectedItem = holder.fileName
                    ListIndexForEditAdapter.clickUpdateFileList(
                        editFragment,
                        selectedItem,
                    )
                    ListIndexEditConfig.handle(
                        editFragment,
                        false,
                        selectedItem,
                    )
                }
        }
    }

    private fun invokeQrLogoSetClickListenerForFileList() {

        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fileQrLogoClickListener = object: ListIndexForEditAdapter.OnFileQrLogoItemClickListener {
            override fun onFileQrLogoClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder
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
    ) {
        FannelLogoLongClickDoForListIndex.invoke(
            editFragment,
            currentAppDirPath,
        )
    }

    private fun makeSearchEditText(
        searchText: AppCompatEditText,
        listIndexConfigMap: Map<String, String>?
    ) {
        val searchBoxMap = ListIndexForEditAdapter.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.SEARCH_BOX.key
        )
        val inVisible =
            searchBoxMap.get(ListIndexEditConfig.SearchBoxSettingKey.VISIBLE.key) ==
                    ListIndexEditConfig.SearchBoxVisibleKey.OFF.name
        if(inVisible){
            searchText.isVisible = false
            return
        }
        searchText.hint = searchBoxMap.get(ListIndexEditConfig.SearchBoxSettingKey.HINT.key).let {
            when(it.isNullOrEmpty()) {
                false -> it
                else -> editFragment.editBoxTitle
            }
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = ListIndexForEditAdapter.makeFileListHandler(
                    busyboxExecutor,
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
                ListIndexForEditAdapter.listIndexListUpdateFileList(
                    editFragment,
                    filteredUrlHistoryList
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
                position: Int
            ) {
                val selectedItem = holder.fileName
                ListIndexEditConfig.handle(
                    editFragment,
                    true,
                    selectedItem,
                )
            }
        }
    }
}
