package com.puutaro.commandclick.proccess.edit_list

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ConstraintView {

    private val topToTopKey = EditComponent.Template.EditComponentKey.TOP_TO_TOP.key
    private val topToBottomKey = EditComponent.Template.EditComponentKey.TOP_TO_BOTTOM.key
    private val startToStartKey = EditComponent.Template.EditComponentKey.START_TO_START.key
    private val startToEndKey = EditComponent.Template.EditComponentKey.START_TO_END.key
    private val endToEndKey = EditComponent.Template.EditComponentKey.END_TO_END.key
    private val endToStartKey = EditComponent.Template.EditComponentKey.END_TO_START.key
    private val bottomToBottomKey = EditComponent.Template.EditComponentKey.BOTTOM_TO_BOTTOM.key
    private val bottomToTopKey = EditComponent.Template.EditComponentKey.BOTTOM_TO_TOP.key
    private val horizontalBiasKey = EditComponent.Template.EditComponentKey.HORIZONTAL_BIAS.key
    private val horizontalWeightKey = EditComponent.Template.EditComponentKey.HORIZONTAL_WEIGHT.key
    private val verticalWeightKey = EditComponent.Template.EditComponentKey.VERTICAL_WEIGHT.key
    private val percentageWidthKey = EditComponent.Template.EditComponentKey.PERCENTAGE_WIDTH.key
    private val percentageHeightKey = EditComponent.Template.EditComponentKey.PERCENTAGE_HEIGHT.key
    private val dimensionRatioKey = EditComponent.Template.EditComponentKey.DIMENSION_RATIO.key
    private val horizontalChainStyleKey =
        EditComponent.Template.EditComponentKey.HORIZONTAL_CHAIN_STYLE.key
    private val verticalChainStyleKey =
        EditComponent.Template.EditComponentKey.VERTICAL_CHAIN_STYLE.key

    suspend fun set(
        view: View,
        tagIdMap: Map<String, Int>?,
        frameKeyPairList: List<Pair<String, String>>?,
        overrideWidth: Int,
        height: Int,
        density: Float,
    ){
        val param = makeConstraintParam(
            tagIdMap,
            frameKeyPairList,
            overrideWidth,
            height,
        ).let {
            paramSrc ->
            LayoutSetterTool.setMargin(
                paramSrc,
                frameKeyPairList,
                density,
            )
        }

    }

    suspend fun makeConstraintParam(
        tagIdMap: Map<String, Int>?,
        frameKeyPairList: List<Pair<String, String>>?,
        overrideWidth: Int,
        height: Int,
    ): ConstraintLayout. LayoutParams{
        return ConstraintLayout.LayoutParams(
            overrideWidth,
            height,
        ).apply {
            val unsetInt =
                EditComponent.Template.ConstraintManager.ConstraintParameter.UNSET.int
            val topToTopInt = withContext(Dispatchers.IO){
                val topToTopStr = PairListTool.getValue(
                    frameKeyPairList,
                    topToTopKey
                )
                ParentReplace.makeReplaceParentInt(
                    topToTopStr,
                    tagIdMap,
                ) ?: unsetInt
//                        EditComponent.Template.ConstraintManager.makePosition(
//                            topToTopStr
//                        ) ?: unsetInt
            }
            topToTop = topToTopInt
            val topToBottomInt = withContext(Dispatchers.IO){
                val topToBottomStr = PairListTool.getValue(
                    frameKeyPairList,
                    topToBottomKey
                )
                val topToBottomId = ParentReplace.makeReplaceParentInt(
                    topToBottomStr,
                    tagIdMap,
                ) ?: unsetInt
//                        if(
//                            overrideTag == "ok"
//                            || overrideTag == "firstTag"
//                            ) {
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultAppDirPath,
//                                    "ltagIdMap_topToBottomInt.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "overrideTag: ${overrideTag}",
//                                    "idInt: ${idInt}",
//                                    "tagIdMap: ${tagIdMap}",
//                                    "topToBottomStr: ${topToBottomStr}",
//                                    "topToBottomId: ${topToBottomId}",
//                                    "edit_list_button_frame_layout1: ${R.id.edit_list_button_frame_layout1}",
//                                    "edit_list_dialog_search_edit_text: ${R.id.edit_list_dialog_search_edit_text}",
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
//                        }
                topToBottomId
            }
//                    if(scene == ParentReplace.Scene.EDIT_LIST_DIALOG) {
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultAppDirPath,
//                                "lEditFrameParam.txt"
//                            ).absolutePath,
//                            listOf(
//                                "tag: ${overrideTag}",
//                                "topToBottomStr: ${
//                                    PairListTool.getValue(
//                                        frameKeyPairList,
//                                        topToBottomKey
//                                    )
//                                }",
//                                "frameKeyPairList: ${frameKeyPairList}",
//                                "topToBottomInt: ${topToBottomInt}",
//                                "R.id.edit_list_dialog_search_edit_text: ${R.id.edit_list_dialog_search_edit_text}",
//                            ).joinToString("\n") + "\n\n============\n\n\n"
//                        )
//                    }
            topToBottom = topToBottomInt

            val startToStartInt = withContext(Dispatchers.IO){
                val startToStartStr = PairListTool.getValue(
                    frameKeyPairList,
                    startToStartKey
                )
                ParentReplace.makeReplaceParentInt(
                    startToStartStr,
                    tagIdMap,
                ) ?: unsetInt
//                        ?: EditComponent.Template.ConstraintManager.makePosition(
//                            startToStartStr
//                        ) ?: unsetInt
            }
            startToStart = startToStartInt
            val startToEndInt = withContext(Dispatchers.IO){
                val startToEndStr = PairListTool.getValue(
                    frameKeyPairList,
                    startToEndKey
                )
                val startToEndId = ParentReplace.makeReplaceParentInt(
                    startToEndStr,
                    tagIdMap,
                ) ?: unsetInt
//                        if(
//                            overrideTag == "ok"
//                            || overrideTag == "firstTag"
//                            ) {
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultAppDirPath,
//                                    "ltagIdMap_startToEndInt.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "overrideTag: ${overrideTag}",
//                                    "idInt: ${idInt}",
//                                    "tagIdMap: ${tagIdMap}",
//                                    "startToEndStr: ${startToEndStr}",
//                                    "startToEndId: ${startToEndId}",
//                                    "edit_list_toolbar_fannel_center_button: ${R.id.edit_list_toolbar_fannel_center_button}",
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
//                        }
                startToEndId
            }
            startToEnd = startToEndInt
            val endToEndInt = withContext(Dispatchers.IO){
                val endToEndStr = PairListTool.getValue(
                    frameKeyPairList,
                    endToEndKey
                )
                val endToEndId = ParentReplace.makeReplaceParentInt(
                    endToEndStr,
                    tagIdMap,
                ) ?: unsetInt
//                        if(
//                            overrideTag == "ok"
//                            || overrideTag == "firstTag"
//                            ) {
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultAppDirPath,
//                                    "ltagIdMap_endToEndInt.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "overrideTag: ${overrideTag}",
//                                    "idInt: ${idInt}",
//                                    "tagIdMap: ${tagIdMap}",
//                                    "endToEndStr: ${endToEndStr}",
//                                    "endToEndId: ${endToEndId}",
//                                    "edit_list_toolbar_fannel_center_button: ${R.id.edit_list_toolbar_fannel_center_button}",
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
//                        }
                endToEndId
//                        ?: EditComponent.Template.ConstraintManager.makePosition(
//                            endToEndStr
//                        ) ?: unsetInt
            }
            endToEnd = endToEndInt
            val endToStartInt = withContext(Dispatchers.IO){
                val endToStartStr = PairListTool.getValue(
                    frameKeyPairList,
                    endToStartKey
                )
                ParentReplace.makeReplaceParentInt(
                    endToStartStr,
                    tagIdMap,
                ) ?: unsetInt
//                        ?: EditComponent.Template.ConstraintManager.makePosition(
//                            endToStartStr
//                        ) ?: unsetInt
            }
            endToStart = endToStartInt
            val bottomToBottomInt = withContext(Dispatchers.IO){
                val bottomToBottomStr = PairListTool.getValue(
                    frameKeyPairList,
                    bottomToBottomKey
                )
                val bottomToBottomId = ParentReplace.makeReplaceParentInt(
                    bottomToBottomStr,
                    tagIdMap,
                ) ?: unsetInt
//                        if(
//                            overrideTag == "ok"
//                            || overrideTag == "firstTag"
//                            ) {
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultAppDirPath,
//                                    "ltagIdMap_bottomToBottomInt.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "overrideTag: ${overrideTag}",
//                                    "idInt: ${idInt}",
//                                    "tagIdMap: ${tagIdMap}",
//                                    "bottomToBottomStr: ${bottomToBottomStr}",
//                                    "bottomToBottomId: ${bottomToBottomId}",
//                                    "edit_list_dialog_search_edit_text: ${R.id.edit_list_dialog_search_edit_text}",
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
//                        }
                bottomToBottomId
            }
            bottomToBottom = bottomToBottomInt
            val bottomToTopInt = withContext(Dispatchers.IO){
                val bottomToTopStr = PairListTool.getValue(
                    frameKeyPairList,
                    bottomToTopKey
                )
                val bottomToTopId = ParentReplace.makeReplaceParentInt(
                    bottomToTopStr,
                    tagIdMap,
                ) ?: unsetInt
//                        if(
//                            overrideTag == "bk2"
//                            || overrideTag == "firstTag"
//                            ) {
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultAppDirPath,
//                                    "ltagIdMap_bottomToTopInt.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "overrideTag: ${overrideTag}",
//                                    "idInt: ${idInt}",
//                                    "tagIdMap: ${tagIdMap}",
//                                    "bottomToTopStr: ${bottomToTopStr}",
//                                    "bottomToTopId: ${bottomToTopId}",
//                                    "edit_list_dialog_search_edit_text: ${R.id.edit_list_dialog_search_edit_text}",
//                                    "edit_list_dialog_footer_constraint_layout: ${R.id.edit_list_dialog_footer_constraint_layout}"
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
//                        }
                bottomToTopId
            }
            bottomToTop = bottomToTopInt
            val horizontalBiasFloat = withContext(Dispatchers.IO){
                EditComponent.Template.ConstraintManager.makeFloat(
                    PairListTool.getValue(
                        frameKeyPairList,
                        horizontalBiasKey
                    )
                ) ?: horizontalBias
            }
            horizontalBias = horizontalBiasFloat
            val horizontalWeightFloat = withContext(Dispatchers.IO){
                EditComponent.Template.ConstraintManager.makeFloat(
                    PairListTool.getValue(
                        frameKeyPairList,
                        horizontalWeightKey
                    )
                ) ?: horizontalWeight
            }
            horizontalWeight = horizontalWeightFloat
            val verticalWeightFloat = withContext(Dispatchers.IO){
                EditComponent.Template.ConstraintManager.makeFloat(
                    PairListTool.getValue(
                        frameKeyPairList,
                        verticalWeightKey
                    )
                ) ?: verticalWeight
            }
            verticalWeight = verticalWeightFloat
            val percentageWidthFloat = withContext(Dispatchers.IO){
                EditComponent.Template.ConstraintManager.makeFloat(
                    PairListTool.getValue(
                        frameKeyPairList,
                        percentageWidthKey
                    )
                ) ?: matchConstraintPercentWidth
            }
            matchConstraintPercentWidth = percentageWidthFloat
            val percentageHeightFloat = withContext(Dispatchers.IO){
                EditComponent.Template.ConstraintManager.makeFloat(
                    PairListTool.getValue(
                        frameKeyPairList,
                        percentageHeightKey
                    )
                ) ?: matchConstraintPercentHeight
            }
            matchConstraintPercentHeight = percentageHeightFloat
            val dimensionRatioStr = withContext(Dispatchers.IO){
                PairListTool.getValue(
                    frameKeyPairList,
                    dimensionRatioKey
                )
            }
            dimensionRatio = dimensionRatioStr
                ?: dimensionRatio
            val horizontalChainStyleInt = withContext(Dispatchers.IO){
                PairListTool.getValue(
                    frameKeyPairList,
                    horizontalChainStyleKey
                )?.let {
                    EditComponent.Template.ConstraintManager.getChainStyleInt(
                        it,
                    )
                } ?: ConstraintLayout.LayoutParams.UNSET
            }
            horizontalChainStyle = horizontalChainStyleInt
//                    if(overrideTag == "backstackCountRect"){
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lhorizontalChainStyle.txt").absolutePath,
//                            listOf(
//                                "horizontalChainStyleInt: ${horizontalChainStyleInt}",
//                                "horizontalChainStyle: ${horizontalChainStyle}",
//                                "ConstraintLayout.LayoutParams.CHAIN_PACKED: ${ConstraintLayout.LayoutParams.CHAIN_PACKED}",
//                            ).joinToString("\n")
//                        )
//                    }
            val verticalChainStyleInt = withContext(Dispatchers.IO){
                PairListTool.getValue(
                    frameKeyPairList,
                    verticalChainStyleKey
                )?.let {
                    EditComponent.Template.ConstraintManager.getChainStyleInt(
                        it,
                    )
                } ?: ConstraintLayout.LayoutParams.UNSET
            }
            verticalChainStyle = verticalChainStyleInt

        }
    }


}