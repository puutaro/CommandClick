package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.view_model.activity.EditViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object EditTextProducerForEdit {

    fun adds(
        editFragment: EditFragment,
    ) {
        val editViewModel: EditViewModel by editFragment.activityViewModels()
        editViewModel.variableNameToEditTextIdMap.clear()
        editFragment.listConSelectBoxMapList.clear()
        execAddEditComponent(
            editFragment,
        )
    }

    private fun execAddEditComponent(
        editFragment: EditFragment,
    ){
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap.txt").absolutePath,
//            listOf(
//                "setVariableTypeList: ${editFragment.setVariableTypeList}",
//                "recordNumToSetVariableMaps: ${recordNumToSetVariableMaps}",
//            ).joinToString("\n\n\n")
//        )
        execAdd(
            editFragment,
        )
    }

    private fun execAdd(
        editFragment: EditFragment,
    ) {
        setListIndexLayoutComponent(
            editFragment,
        )
    }

    private fun setListIndexLayoutComponent(
        editFragment: EditFragment,
    ){
        val binding = editFragment.binding
        val isOnlyCmdValEdit = !editFragment.enableEditExecute
        when(isOnlyCmdValEdit) {
            true -> {
                val toolbarLayoutMap = mapOf(
                    EditListConfig.EditListConfigKey.TOOLBAR_LAYOUT_PATH.key to
                            EditListConfig.ToolbarLayoutPath.ToolbarLayoutMacro.FOR_ONLY_CMD_VAL_EDIT.name,
                )
                editFragment.editListConfigMap = editFragment.editListConfigMap?.let {
                    it + toolbarLayoutMap
                } ?: toolbarLayoutMap
            }
            else -> {
                CoroutineScope(Dispatchers.Main).launch {
                    ExecSetToolbarButtonImage.setImageButton(
                        binding.editToolbarFannelCenterButtonImage,
                        CmdClickIcons.HISTORY
                    )
                    binding.editToolbarFannelCenterButton.setOnClickListener {
                        FannelHistoryButtonEvent.invoke(editFragment)
                    }
                }
            }
        }
        val editToolbarFannelCenterButton =
            binding.editToolbarFannelCenterButton.apply {
                isVisible = !isOnlyCmdValEdit
            }

//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "editList.txt").absolutePath,
//            listOf(
//                "editFragment.editListConfigMap: ${editFragment.editListConfigMap}"
//            ).joinToString("\n")
//        )
        val verticalLinearListForFooter =
            listOf(
                binding.verticalLinear1 as LinearLayoutCompat,
                binding.verticalLinear2 as LinearLayoutCompat,
            )
        WithEditComponentListView.create(
            editFragment,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.busyboxExecutor,
            editFragment.editListConfigMap,
            binding.editBackstackCountFrame,
            binding.editBackstackCount,
            binding.editTextView,
            binding.editTitleImage,
            binding.editListRecyclerView,
            binding.editListBkFrame,
            binding.editListSearchEditText,
            binding.editFooterHorizonLayout,
            verticalLinearListForFooter,
            binding.editToolBarHorizonLayout,
            editToolbarFannelCenterButton,
            editFragment.mainFannelConList,
        )
    }
}

