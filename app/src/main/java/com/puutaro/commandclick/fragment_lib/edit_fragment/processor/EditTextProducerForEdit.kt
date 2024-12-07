package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.view_model.activity.EditViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val verticalLinear1 = binding.verticalLinear1
        val verticalLinear2 = binding.verticalLinear2
        val verticalLinearListForFooter =
            listOf(
                verticalLinear1.editComponentAdapterVerticalBind as LinearLayoutCompat,
                verticalLinear2.editComponentAdapterVerticalBind as LinearLayoutCompat,
            )
        val varticalIndexAndHorizonLinearListForFooter =
            listOf (
                listOf(
                    verticalLinear1.editComponentAdapterHorizonBind1,
                    verticalLinear1.editComponentAdapterHorizonBind2,
                ),
                listOf(
                    verticalLinear2.editComponentAdapterHorizonBind1,
                    verticalLinear2.editComponentAdapterHorizonBind2,
                )
            )
//        val contentsLayoutIdListList = listOf(
//            listOf(
//                R.id.button_frame_layout11,
//                R.id.button_frame_layout12,
//                R.id.button_frame_layout13,
//            ),
//            listOf(
//                R.id.button_frame_layout21,
//                R.id.button_frame_layout22,
//                R.id.button_frame_layout23,
//            ),
//        )
        val verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter =
            listOf(
                listOf(
                    listOf(
                        verticalLinear1.buttonFrameLayout11,
                        verticalLinear1.buttonFrameLayout12,
                        verticalLinear1.buttonFrameLayout13,
                    ),
                    listOf(
                        verticalLinear1.buttonFrameLayout21,
                        verticalLinear1.buttonFrameLayout22,
                        verticalLinear1.buttonFrameLayout23,
                    ),
                ),
                listOf(
                    listOf(
                        verticalLinear2.buttonFrameLayout11,
                        verticalLinear2.buttonFrameLayout12,
                        verticalLinear2.buttonFrameLayout13,
                    ),
                    listOf(
                        verticalLinear2.buttonFrameLayout21,
                        verticalLinear2.buttonFrameLayout22,
                        verticalLinear2.buttonFrameLayout23,
                    ),
                )
            ) as List<List<List<FrameLayout?>>>
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "editList.txt").absolutePath,
//            verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter.mapIndexed {
//                varticalIndex, horizonIndexAndReadyContentsLayoutList ->
//                horizonIndexAndReadyContentsLayoutList.mapIndexed {
//                    horizonIndex, readyContentsLayoutList ->
//                    readyContentsLayoutList.map {
//                        contentsLayout ->
//                        "varticalIndex: ${varticalIndex}, horizonIndex: ${horizonIndex}, bool: ${contentsLayout is FrameLayout}"
//                    }.joinToString("\n")
//                }.joinToString("\n")
//            }.joinToString("\n")
//        )
//            varticalIndexAndHorizonLinearListForFooter.mapIndexed {
//                    _, readyHorizonLayoutList ->
//                readyHorizonLayoutList.mapIndexed {
//                        horizonIndex, horizon ->
//                    val curLayoutIdListForHorizon =
//                        contentsLayoutIdListList.get(horizonIndex)
//                    curLayoutIdListForHorizon.map {
//                            layoutId ->
//                        horizon.findViewById<FrameLayout>(layoutId)
//                    }
//                }
//            }

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
                varticalIndexAndHorizonLinearListForFooter,
                verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter,
                binding.editToolBarHorizonLayout,
                editToolbarFannelCenterButton,
                editFragment.mainFannelConList,
                density,
                requestBuilderSrc,
            )
        }
    }
}

