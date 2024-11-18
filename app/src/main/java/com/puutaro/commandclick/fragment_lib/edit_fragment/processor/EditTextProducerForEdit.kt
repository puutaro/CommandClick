package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.view_model.activity.EditViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        CoroutineScope(Dispatchers.Main).launch {
            ExecSetToolbarButtonImage.setImageButton(
                binding.editToolbarFannelCenterButtonImage,
                CmdClickIcons.HISTORY
            )
            binding.editToolbarFannelCenterButton.setOnClickListener {
                FannelHistoryButtonEvent.invoke(editFragment)
            }
        }

        WithEditComponentListView.create(
            editFragment,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.busyboxExecutor,
            editFragment.listIndexConfigMap,
            binding.editTextView,
            binding.editTitleImage,
            binding.editListRecyclerView,
            binding.editListBkFrame,
            binding.editListSearchEditText,
            binding.editFooterLinearlayout,
            binding.editToolBarLinearLayout,
            binding.editToolbarFannelCenterButton,
            editFragment.mainFannelConList,
        )
    }
}

