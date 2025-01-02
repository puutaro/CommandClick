package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.view_model.activity.EditViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
//                CoroutineScope(Dispatchers.Main).launch {
//                    ExecSetToolbarButtonImage.setImageButton(
//                        binding.editToolbarFannelCenterButtonImage,
//                        CmdClickIcons.HISTORY
//                    )
//                    binding.editListToolbarFannelCenterButton.setOnClickListener {
//                        FannelHistoryButtonEvent.invoke(editFragment)
//                    }
//                }
            }
        }
        val editListToolbarConstraintLayout =
            binding.editListToolbarConstraintLayout

//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "editList.txt").absolutePath,
//            listOf(
//                "editFragment.editListConfigMap: ${editFragment.editListConfigMap}"
//            ).joinToString("\n")
//        )
        val editListFooterConstraintLayout = binding.editListFooterConstraintLayout

        CoroutineScope(Dispatchers.IO).launch {
            val context = editFragment.context
            val density = withContext(Dispatchers.Main) {
                ScreenSizeCalculator.getDensity(editFragment.context)
            }
            val requestBuilderSrc: RequestBuilder<Drawable>? = withContext(Dispatchers.IO){
                context?.let {
                    Glide.with(it)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                }
            }
            val eachLayoutIdMap = EditComponent.Template.LayoutIdMap.makeMap(
                binding.editListTitleConstraintLayout.id,
                binding.editListRecyclerView.id,
                binding.editListSearchEditText.id,
                binding.editListFooterConstraintLayout.id,
                binding.editListToolbarConstraintLayout.id,
            )
            WithEditConstraintListView.create(
                editFragment,
                editFragment.fannelInfoMap,
                editFragment.setReplaceVariableMap,
                editFragment.busyboxExecutor,
                editFragment.imageActionAsyncCoroutine,
                editFragment.editListConfigMap,
                binding.editListTitleConstraintLayout,
                binding.editListRecyclerView,
                binding.editListSearchEditText,
                binding.editFragment,
                editListFooterConstraintLayout,
                editListToolbarConstraintLayout,
                eachLayoutIdMap,
                editFragment.mainFannelConList,
                density,
                requestBuilderSrc,
            )
        }
    }
}

