package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object EditConstraintFrameMaker {


    private val valueSeparator = EditComponent.Template.valueSeparator

    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val marginTopKey = EditComponent.Template.EditComponentKey.MARGIN_TOP.key
    private val marginBottomKey = EditComponent.Template.EditComponentKey.MARGIN_BOTTOM.key
    private val marginStartKey = EditComponent.Template.EditComponentKey.MARGIN_START.key
    private val marginEndKey = EditComponent.Template.EditComponentKey.MARGIN_END.key
    private val paddingTopKey = EditComponent.Template.EditComponentKey.PADDING_TOP.key
    private val paddingBottomKey = EditComponent.Template.EditComponentKey.PADDING_BOTTOM.key
    private val paddingStartKey = EditComponent.Template.EditComponentKey.PADDING_START.key
    private val paddingEndKey = EditComponent.Template.EditComponentKey.PADDING_END.key
    private val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
    private val visibleKey = EditComponent.Template.EditComponentKey.VISIBLE.key
    private val bkColorKey = EditComponent.Template.EditComponentKey.BK_COLOR.key
    private val layoutGravityKey = EditComponent.Template.EditComponentKey.LAYOUT_GRAVITY.key
    private val gravityKey = EditComponent.Template.EditComponentKey.GRAVITI.key
    private val elevationkey = EditComponent.Template.EditComponentKey.ELEVATION.key
    private val alphaKey = EditComponent.Template.EditComponentKey.ALPHA.key

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
    private val dimensionRatioKey = EditComponent.Template.EditComponentKey.DIMENSION_RATIO.key


    private val imageKey = EditComponent.Template.EditComponentKey.IMAGE.key
    private val imagePropertyKey = EditComponent.Template.EditComponentKey.IMAGE_PROPERTY.key
    private val textKey = EditComponent.Template.EditComponentKey.TEXT.key
    private val textPropertyKey = EditComponent.Template.EditComponentKey.TEXT_PROPERTY.key
    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
    private val widthKey = EditComponent.Template.EditComponentKey.WIDTH.key


    private val imagePathsKey = EditComponent.Template.ImageManager.ImageKey.PATHS.key
    private val imageDelayKey = EditComponent.Template.ImageManager.ImageKey.DELAY.key
    private val imageFadeInMilliKey = EditComponent.Template.ImageManager.ImageKey.FADE_IN_MILLI.key
//    private val imageTagKey = EditComponent.Template.ImagePropertyManager.PropertyKey.TAG.key
    private val imageColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.COLOR.key
    private val imageBkColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.BK_COLOR.key
    private val imageBkTintColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.BK_COLOR.key
    private val imageAlphaKey = EditComponent.Template.ImagePropertyManager.PropertyKey.ALPHA.key
    private val imageScaleKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE.key
    private val imageLayoutGravity = EditComponent.Template.ImagePropertyManager.PropertyKey.LAYOUT_GRAVITY.key
    private val imageGravityKey = EditComponent.Template.ImagePropertyManager.PropertyKey.GRAVITI.key
    private val imageWidthKey = EditComponent.Template.ImagePropertyManager.PropertyKey.WIDTH.key
    private val imageHeightKey = EditComponent.Template.ImagePropertyManager.PropertyKey.HEIGHT.key
    private val imageMarginTopKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_TOP.key
    private val imageMarginStartKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_START.key
    private val imageMarginEndKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_END.key
    private val imageMarginBottomKey = EditComponent.Template.ImagePropertyManager.PropertyKey.MARGIN_BOTTOM.key
    private val imagePaddingTopKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_TOP.key
    private val imagePaddingStartKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_START.key
    private val imagePaddingEndKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_END.key
    private val imagePaddingBottomKey = EditComponent.Template.ImagePropertyManager.PropertyKey.PADDING_BOTTOM.key
    private val imageVisibleKey = EditComponent.Template.ImagePropertyManager.PropertyKey.VISIBLE.key

    private val onUpdateKey = EditComponent.Template.TextManager.TextKey.ON_UPDATE.key
    private val textSizeKey = EditComponent.Template.TextPropertyManager.Property.SIZE.key
    private val textBkColorKey = EditComponent.Template.TextPropertyManager.Property.BK_COLOR.key
    private val textStyleKey = EditComponent.Template.TextPropertyManager.Property.STYLE.key
    private val textFontKey = EditComponent.Template.TextPropertyManager.Property.FONT.key
//    private val textTagKey = EditComponent.Template.TextPropertyManager.Property.TAG.key
    private val textLayoutGravityKey =
        EditComponent.Template.TextPropertyManager.Property.LAYOUT_GRAVITY.key
    private val textGravityKey = EditComponent.Template.TextPropertyManager.Property.GRAVITI.key
    private val textWidthKey = EditComponent.Template.TextPropertyManager.Property.WIDTH.key
    private val textHeightKey = EditComponent.Template.TextPropertyManager.Property.HEIGHT.key
    private val textColorKey = EditComponent.Template.TextPropertyManager.Property.COLOR.key
    private val textVisibleKey = EditComponent.Template.TextPropertyManager.Property.VISIBLE.key
    private val strokeColorKey = EditComponent.Template.TextPropertyManager.Property.STROKE_COLOR.key
    private val strokeWidthKey = EditComponent.Template.TextPropertyManager.Property.STROKE_WIDTH.key
    private val textAlphaKey = EditComponent.Template.TextPropertyManager.Property.ALPHA.key
    private val textMaxLinesKey = EditComponent.Template.TextPropertyManager.Property.MAX_LINES.key
    private val textMarginTopKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_TOP.key
    private val textMarginStartKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_START.key
    private val textMarginEndKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_END.key
    private val textMarginBottomKey = EditComponent.Template.TextPropertyManager.Property.MARGIN_BOTTOM.key
    private val textPaddingTopKey = EditComponent.Template.TextPropertyManager.Property.PADDING_TOP.key
    private val textPaddingStartKey = EditComponent.Template.TextPropertyManager.Property.PADDING_START.key
    private val textPaddingEndKey = EditComponent.Template.TextPropertyManager.Property.PADDING_END.key
    private val textPaddingBottomKey = EditComponent.Template.TextPropertyManager.Property.PADDING_BOTTOM.key
//    private val disableTextSelectKey = EditComponent.Template.TextPropertyManager.Property.DISABLE_TEXT_SELECT.key

    private val switchOn = EditComponent.Template.switchOn
    private val switchOff = EditComponent.Template.switchOff

    object ParentReplace {

        fun makeReplaceParentInt(
            positionEntryStr: String?,
            tagIdMap: Map<String, Int>?,
        ): Int? {
            if(
                positionEntryStr.isNullOrEmpty()
            ) return null
            val unsetEnum = EditComponent.Template
                .ConstraintManager
                .ConstraintParameter.UNSET
            if(
                positionEntryStr == unsetEnum.str
            ) return unsetEnum.int
            val parentIdEnum = EditComponent.Template
                .ConstraintManager
                .ConstraintParameter.PARENT_ID
            if(
                positionEntryStr == parentIdEnum.str
            ) return parentIdEnum.int
            return tagIdMap?.get(positionEntryStr)
        }
    }

    suspend fun make(
        context: Context?,
        idInt: Int?,
        tagIdMap: Map<String, Int>?,
        buttonLayoutSrc: FrameLayout?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        frameKeyPairList: List<Pair<String, String>>?,
        width: Int,
        overrideTag: String?,
        totalSettingValMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ): FrameLayout? {
        if(
            context == null
        ) return null
        val buttonLayout = makeButtonFrameLayout(
            context,
            idInt,
            tagIdMap,
            buttonLayoutSrc,
            frameKeyPairList,
            width,
            overrideTag,
            density,
        ) ?: return null


        CoroutineScope(Dispatchers.Main).launch {
            val imageButtonView = withContext(Dispatchers.Main) {
                buttonLayout.children.firstOrNull {
                    it is AppCompatImageView
                } as? AppCompatImageView
            }?: return@launch
//            buttonLayout.findViewById<AppCompatImageView>(R.id.icon_caption_for_edit_image)
//                ?.let { imageButtonView ->
                val imageMap = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        imageKey,
                    )?.let {
                        EditComponent.Template.makeKeyMap(
                            it,
                        )
                    }
                }
                val imagePropertyMap = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        imagePropertyKey,
                    )?.let {
                        EditComponent.Template.makeKeyMap(
                            it,
                        )
                    }
                }
                withContext(Dispatchers.Main) {
                    setImageView(
                        imageButtonView,
                        imageMap,
                        imagePropertyMap,
                        requestBuilderSrc,
                        density,
                    )
                }
//                }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val captionTextView = withContext(Dispatchers.Main) {
                buttonLayout.children.firstOrNull {
                    it is OutlineTextView
                } as? OutlineTextView
            }?: return@launch
//            buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_for_edit_caption)
//                ?.let { captionTextView ->
                val textMap = withContext(Dispatchers.IO) {
                    PairListTool.getPair(
                        frameKeyPairList,
                        textKey,
                    )?.let {
                        EditComponent.Template.TextManager.createTextMap(
                            it.second,
                            totalSettingValMap?.get(
                                overrideTag
                            )
                        )
                    }
                }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "sGet_frameMaker.txt").absolutePath,
//                listOf(
//                    "totalSettingValMap: ${totalSettingValMap}",
//                    "textMap: ${textMap}",
//                ).joinToString("\n")
//            )
                val textPropertyMap = withContext(Dispatchers.IO) {
                    PairListTool.getPair(
                        frameKeyPairList,
                        textPropertyKey,
                    )?.let {
                        EditComponent.Template.makeKeyMap(
                            it.second,
                        )
                    }
                }
                withContext(Dispatchers.Main) {
                    setCaption(
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        captionTextView,
                        textMap,
                        textPropertyMap,
                        density,
                    )
                }
//                }
        }
        return buttonLayout
    }

    private suspend fun makeButtonFrameLayout(
        context: Context?,
        idInt: Int?,
        tagIdMap: Map<String, Int>?,
        buttonLayout: FrameLayout?,
        frameKeyPairList: List<Pair<String, String>>?,
        width: Int,
        overrideTag: String?,
        density: Float,
    ):  FrameLayout? {
        if(
            context == null
        ) return null
        val isEnable = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                enableKey,
           ) != switchOff
        }
        if(
            !isEnable
        ) return null
        val visibilityValue = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                visibleKey,
            ).let { visibleStr ->
                EditComponent.Template.VisibleManager.getVisible(
                    visibleStr
                )
            }
        }
        buttonLayout?.apply {
            visibility = visibilityValue
            id = idInt ?: id
        }

        val height = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                heightKey,
            ).let {
                EditComponent.Template.LinearLayoutUpdater.convertHeight(
                    it,
                    0,
                    density,
                )
            }
//                ,ScreenSizeCalculator.toDp(context, 50)
        }
        val overrideWidth = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                widthKey,
            ).let {
                EditComponent.Template.LinearLayoutUpdater.convertWidth(
                    it,
                    width,
                    density,
                )
            }
        }

        val param = withContext(Dispatchers.IO) {
            val overrideLayoutGravity = PairListTool.getValue(
                frameKeyPairList,
                layoutGravityKey
            )?.let { gravityStr ->
                EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                    it.key == gravityStr
                }?.gravity
            } ?: Gravity.CENTER
            val isFrameLayoutParam = tagIdMap.isNullOrEmpty()
            when (isFrameLayoutParam) {
                true -> FrameLayout.LayoutParams(
                    overrideWidth,
                    height,
                ).apply {
                    gravity = overrideLayoutGravity
                }
                else -> ConstraintLayout.LayoutParams(
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
//                            overrideTag == "ok"
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
//                                ).joinToString("\n") + "\n\n============\n\n\n"
//                            )
//                        }
                        bottomToTopId
                    }
                    bottomToTop = bottomToTopInt
                    val horizontalBiasFloat = withContext(Dispatchers.IO){
                        EditComponent.Template.ConstraintManager.makeBias(
                            PairListTool.getValue(
                                frameKeyPairList,
                                horizontalBiasKey
                            )
                        ) ?: horizontalBias
                    }
                    horizontalBias = horizontalBiasFloat
                    val horizontalWeightFloat = withContext(Dispatchers.IO){
                        EditComponent.Template.ConstraintManager.makeBias(
                            PairListTool.getValue(
                                frameKeyPairList,
                                horizontalWeightKey
                            )
                        ) ?: horizontalWeight
                    }
                    horizontalWeight = horizontalWeightFloat
                    val dimensionRatioStr = withContext(Dispatchers.IO){
                        PairListTool.getValue(
                            frameKeyPairList,
                            dimensionRatioKey
                        )
                    }
                    dimensionRatio = dimensionRatioStr
                        ?: dimensionRatio

                }
            }
        }
        withContext(Dispatchers.IO) {
            param.apply {
                val marginData = EditComponent.Template.MarginData(
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginTopKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginBottomKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginStartKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginEndKey
                    ),
                    density,
                )
                topMargin = marginData.marginTop ?: 0
                marginStart = marginData.marginStart ?: 0
                marginEnd = marginData.marginEnd ?: 0
                bottomMargin = marginData.marginBottom ?: 0
            }
        }
        buttonLayout?.apply {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "soverrideTag.txt").absolutePath,
//                listOf(
//                    "overrideTag: ${overrideTag}"
//                ).joinToString("\n")
//            )
//            val idInt = withContext(Dispatchers.IO){
//                EditComponent.Template.ConstraintManager.makeId(
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        idKey
//                    )
//                ) ?: id
//            }
            layoutParams = param
            tag = overrideTag
            val overrideGravity = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    gravityKey
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }
            }?: Gravity.CENTER
            foregroundGravity = overrideGravity
            val paddingData = withContext(Dispatchers.IO) {
                EditComponent.Template.PaddingData(
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingTopKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingBottomKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingStartKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingEndKey
                    ),
                    density,
                )
            }
            setPadding(
                paddingData.paddingStart ?: 0,
                paddingData.paddingTop ?: 0,
                paddingData.paddingEnd ?: 0,
                paddingData.paddingBottom ?: 0,
            )
            val bkColorStr =  withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    bkColorKey,
                )
            }
            val bkColorDrawable = withContext(Dispatchers.IO) {
                    CmdClickColor.entries.firstOrNull {
                        it.str == bkColorStr
                    }?.let {
                    AppCompatResources.getDrawable(
                        context,
                        it.id
                    )
                }
            }
            if(bkColorDrawable == null) {
                val bkTintColor =
                    bkColorStr?.let {
                        try {
                            Color.parseColor(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
            }

            bkColorStr?.let {
                    ColorStateList.valueOf(Color.parseColor(it))
                }
            background = bkColorDrawable
            val elevationFloat = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    elevationkey,
                )?.let {
                    try {
                        it.toFloat()
                    }catch (e: Exception){
                        null
                    }
                }
            } ?: elevation
            elevation = elevationFloat
            val alphaFloat = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    alphaKey,
                )?.let {
                    try {
                        it.toFloat()
                    }catch (e: Exception){
                        null
                    }
                }
            } ?: alpha
            alpha = alphaFloat
//            val paramConst = try {
//                param as ConstraintLayout.LayoutParams
//            } catch (e: Exception){
//                return@apply
//            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "ledtConstarintFramel.txt").absolutePath,
//                listOf(
//                    "unsetInt: ${EditComponent.Template.ConstraintManager.ConstraintParameter.UNSET.int}",
//                    "PARENT_ID: ${EditComponent.Template.ConstraintManager.ConstraintParameter.PARENT_ID.int}",
//                    "id: ${id}",
//                    "tag: ${tag}",
//                    "this.topToTop: ${paramConst.topToTop}",
//                    "this.topToBottom: ${paramConst.topToBottom}",
//                    "this.startToEnd: ${paramConst.startToEnd}",
//                    "this.startToStart: ${paramConst.startToStart}",
//                    "this.endToEnd: ${paramConst.endToEnd}",
//                    "this.endToStart: ${paramConst.endToStart}",
//                    "this.bottomToTop: ${paramConst.bottomToTop}",
//                    "this.bottomToBottom: ${paramConst.bottomToBottom}",
//                    "this.horizontalBias: ${paramConst.horizontalBias}",
//                ).joinToString("\n")
//            )
        }
        return buttonLayout
    }

    suspend fun setButtonFrameLayoutByDynamic(
        context: Context?,
        buttonFrameLayout: FrameLayout?,
        frameKeyPairList: List<Pair<String, String>>?,
        density: Float,
    ) {
        if(
            context == null
        ) return
        val isEnable = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                enableKey,
            ) != switchOff
        }
        if(
            !isEnable
        ) return
        buttonFrameLayout?.apply {
           withContext(Dispatchers.Main) {
                PairListTool.getValue(
                    frameKeyPairList,
                    visibleKey,
                )?.let { visibleStr ->
                    visibility = EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
            }
        }

        val param = withContext(Dispatchers.IO) {
            (buttonFrameLayout?.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
                withContext(Dispatchers.Main){
                    EditComponent.Template.ConstraintManager.makeBias(
                        PairListTool.getValue(
                            frameKeyPairList,
                            horizontalBiasKey
                        )
                    )?.let {
                        horizontalBias = it
                    }
                }
                withContext(Dispatchers.Main){
                    EditComponent.Template.ConstraintManager.makeBias(
                        PairListTool.getValue(
                            frameKeyPairList,
                            horizontalWeightKey
                        )
                    )?.let {
                        horizontalWeight = it
                    }
                }
                withContext(Dispatchers.Main){
                    PairListTool.getValue(
                        frameKeyPairList,
                        dimensionRatioKey
                    )?.let {
                        dimensionRatio = it
                    }
                }
                val marginData = EditComponent.Template.MarginData(
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginTopKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginBottomKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginStartKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        marginEndKey
                    ),
                    density,
                )
                withContext(Dispatchers.Main) {
                    marginData.marginTop?.let {
                        topMargin = it
                    }
                    marginData.marginStart?.let {
                        marginStart = 0
                    }
                    marginData.marginEnd?.let {
                        marginEnd = it
                    }
                    marginData.marginBottom?.let {
                        bottomMargin = it
                    }
                }
            }
        }

        buttonFrameLayout?.apply {
            layoutParams = param
            withContext(Dispatchers.Main) {
                PairListTool.getValue(
                    frameKeyPairList,
                    gravityKey
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }?.let {
                    foregroundGravity = it
                }
            }
            val paddingData = withContext(Dispatchers.IO) {
                EditComponent.Template.PaddingData(
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingTopKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingBottomKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingStartKey
                    ),
                    PairListTool.getValue(
                        frameKeyPairList,
                        paddingEndKey
                    ),
                    density,
                )
            }
            withContext(Dispatchers.Main) {
                setPadding(
                    paddingData.paddingStart ?: paddingStart,
                    paddingData.paddingTop ?: paddingStart,
                    paddingData.paddingEnd ?: paddingEnd,
                    paddingData.paddingBottom ?: paddingBottom,
                )
            }
            withContext(Dispatchers.Main) {
                PairListTool.getValue(
                    frameKeyPairList,
                    bkColorKey,
                )?.let {
                        colorStr ->
                    CmdClickColor.entries.firstOrNull {
                        it.str == colorStr
                    }
                }?.let {
                    AppCompatResources.getDrawable(
                        context,
                        it.id
                    )
                }?.let {
                    background = it
                }
            }
            withContext(Dispatchers.Main) {
                PairListTool.getValue(
                    frameKeyPairList,
                    elevationkey,
                )?.let {
                    try {
                        it.toFloat()
                    }catch (e: Exception){
                        null
                    }
                }
            }?.let {
                elevation = it
            }
            withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    alphaKey,
                )?.let {
                    try {
                        it.toFloat()
                    }catch (e: Exception){
                        null
                    }
                }
            }?.let {
                alpha = it
            }
        }
    }

    private suspend fun setImageView(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        imagePropertyMap: Map<String, String>?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ) {
        if(
            imageMap.isNullOrEmpty()
            && imagePropertyMap.isNullOrEmpty()
        ){
            imageView.isVisible = false
            return
        }
        val context = imageView.context
        val imagePathList = withContext(Dispatchers.IO) {
            imageMap?.get(
                imagePathsKey,
            )?.split(valueSeparator)
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lBk_image.txt").absolutePath,
//            listOf(
//                "imageMap: ${imageMap}",
//                "imagePropertyMap: ${imagePropertyMap}"
//            ).joinToString("\n") + "\n\n==========\n\n"
//        )
        when(
            imagePathList.isNullOrEmpty()
        ) {
            true -> {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lBk_image.txt").absolutePath,
//                    listOf(
//                        "bk: ${bk}",
//                        "imageMap: ${imageMap}",
//                        "imagePropertyMap: ${imagePropertyMap}"
//                    ).joinToString("\n") + "\n\n==========\n\n"
//                )
                imageView.setImageDrawable(null)
            }
            else -> {
                imageView.apply {
                    val visibilityValue = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageVisibleKey,
                        ).let { visibleStr ->
                            EditComponent.Template.VisibleManager.getVisible(
                                visibleStr
                            )
                        }
                    }
                    visibility = visibilityValue
                    CoroutineScope(Dispatchers.Main).launch {
                        when (imagePathList.size == 1) {
                            false -> {
                                val delay = withContext(Dispatchers.IO) {
                                    imageMap?.get(
                                        imageDelayKey,
                                    )?.let {
                                        try {
                                            it.toInt()
                                        } catch (e: Exception) {
                                            null
                                        }
                                    } ?: 800
                                }
                                execSetMultipleImage(
                                    imageView,
                                    imagePathList,
                                    delay,
                                )
                            }

                            else -> {
                                val fadeInMilli = withContext(Dispatchers.IO) {
                                    imageMap?.get(
                                        imageFadeInMilliKey,
                                    )?.let {
                                        try {
                                            it.toInt()
                                        } catch (e: Exception){
                                            null
                                        }
                                    }
                                }
                                execSetSingleImage(
                                    imageView,
                                    imagePathList.firstOrNull(),
                                    requestBuilderSrc,
                                    fadeInMilli,
                                )
                            }
                        }
                    }
                    val overrideGravity = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageGravityKey,
                        )?.let { gravityStr ->
                            EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                it.key == gravityStr
                            }?.gravity
                        }
                    }?: Gravity.CENTER
                    foregroundGravity = overrideGravity
                    val imageColor = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageColorKey,
                        ).let {
                                colorStr ->
                            makeColor(
                                context,
                                colorStr,
                            )
                        }
                    }
                    imageTintList = imageColor?.let {
                        ColorStateList.valueOf(it)
                    }
                    val imageBkColor = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageBkColorKey,
                        ).let {
                            colorStr ->
                            makeColor(
                                context,
                                colorStr,
                            )
                        }
                    }
                    background = imageBkColor?.let {
                        ColorDrawable(imageBkColor)
                    }

//                    backgroundTintList = ColorStateList.valueOf(imageBkColor)
//                    background = imageBkColorId?.let {
//                        AppCompatResources.getDrawable(
//                            context,
//                            it.id
//                        )
//                    }
                    val imageAlpha = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageAlphaKey,
                        )?.let {
                            try {
                                it.toFloat()
                            } catch(e: Exception){
                                null
                            }
                        }
                    }
                    alpha = imageAlpha ?: 1f
                    val imageScale = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageScaleKey,
                        ).let {
                                scale ->
                            EditComponent.Template.ImagePropertyManager.ImageScale.entries.firstOrNull {
                                it.str == scale
                            } ?: EditComponent.Template.ImagePropertyManager.ImageScale.FIT_CENTER
                        }
                    }
                    scaleType = imageScale.scale
                    val paddingData = withContext(Dispatchers.IO) {
                        EditComponent.Template.PaddingData(
                            imagePropertyMap?.get(
                                imagePaddingTopKey,
                            ),
                            imagePropertyMap?.get(
                                imagePaddingBottomKey,
                            ),
                            imagePropertyMap?.get(
                                imagePaddingStartKey,
                            ),
                            imagePropertyMap?.get(
                                imagePaddingEndKey,
                            ),
                            density,
                        )
                    }
                    setPadding(
                        paddingData.paddingStart ?: 0,
                        paddingData.paddingTop ?: 0,
                        paddingData.paddingEnd ?: 0,
                        paddingData.paddingBottom ?: 0,
                    )
                }
            }
        }
        imageView.layoutParams = imageView.layoutParams.apply {
            val curLayoutParams = this as FrameLayout.LayoutParams
            curLayoutParams.apply setParam@ {
                val overrideWidth = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageWidthKey,
                    ).let {
                        EditComponent.Template.LinearLayoutUpdater.convertWidth(
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            density,
                        )
                    }
                }
                width = overrideWidth
                val overrideHeight = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageHeightKey,
                    ).let {
                        EditComponent.Template.LinearLayoutUpdater.convertHeight(
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            density,
                        )
                    }
                }
                height = overrideHeight
                val overrideLayoutGravity = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageLayoutGravity,
                    )?.let { gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    }
                }?: Gravity.CENTER
                gravity = overrideLayoutGravity
                val marginData = withContext(Dispatchers.IO) {
                    EditComponent.Template.MarginData(
                        imagePropertyMap?.get(
                            imageMarginTopKey,
                        ),
                        imagePropertyMap?.get(
                            imageMarginBottomKey,
                        ),
                        imagePropertyMap?.get(
                            imageMarginStartKey,
                        ),
                        imagePropertyMap?.get(
                            imageMarginEndKey,
                        ),
                        density,
                    )
                }
                marginData.marginTop?.let {
                    topMargin = it
                }
                marginData.marginBottom?.let {
                    bottomMargin = it
                }
                marginData.marginStart?.let {
                    marginStart = it
                }
                marginData.marginEnd?.let {
                    marginEnd = it
                }
            }
        }

//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lImage.txt").absolutePath,
//            listOf(
//                "width: ${width}",
//                "height: ${height}",
//            ).joinToString("\n")
//        )

    }

    suspend fun setImageViewForDynamic(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        imagePropertyMap: Map<String, String>?,
        density: Float,
    ) {
        val context = imageView.context
        imageView.layoutParams = imageView.layoutParams.apply {
            val curLayoutParams = this as FrameLayout.LayoutParams
            curLayoutParams.apply setParam@ {
                val overrideWidth = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageWidthKey,
                    )?.let {
                        EditComponent.Template.LinearLayoutUpdater.convertWidth(
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            density,
                        )
                    }
                }
                overrideWidth?.let {
                    width = it
                }
                val overrideHeight = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageHeightKey,
                    )?.let {
                        EditComponent.Template.LinearLayoutUpdater.convertHeight(
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            density,
                        )
                    }
                }
                overrideHeight?.let {
                    height = it
                }
                withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageLayoutGravity,
                    )?.let { gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    }
                }?.let {
                    gravity = it
                }
            }
        }

//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lImage.txt").absolutePath,
//            listOf(
//                "width: ${width}",
//                "height: ${height}",
//            ).joinToString("\n")
//        )
        val imagePathList = withContext(Dispatchers.IO) {
            imageMap?.get(
                imagePathsKey,
            )?.split(valueSeparator)
        }
        imageView.apply {
            withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageVisibleKey,
                )?.let { visibleStr ->
                    EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }?.let {
                    visibility = it
                }
            }
            withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageGravityKey,
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }
            }?.let {
                foregroundGravity = it
            }
            withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageColorKey,
                ).let {
                        colorStr ->
                    makeColor(
                        context,
                        colorStr,
                    )
                }
            }?.let {
                imageTintList = ColorStateList.valueOf(it)
            }
            withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageBkColorKey,
                ).let {
                        colorStr ->
                    makeColor(
                        context,
                        colorStr,
                    )
                }
            }?.let {
                background = ColorDrawable(it)
            }

            val imageAlpha = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageAlphaKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
            }
            imageAlpha?.let {
                alpha = it
            }
            val imageScale = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageScaleKey,
                ).let {
                        scale ->
                    EditComponent.Template.ImagePropertyManager.ImageScale.entries.firstOrNull {
                        it.str == scale
                    }
                }
            }
            imageScale?.let {
                scaleType = it.scale
            }
        }

        if(
            imagePathList.isNullOrEmpty()
            ) return
//        imageView.isVisible = true
        CoroutineScope(Dispatchers.Main).launch {
            when (imagePathList.size == 1) {
                false -> {
                    val delay = withContext(Dispatchers.IO) {
                        imageMap?.get(
                            imageDelayKey,
                        )?.let {
                            try {
                                it.toInt()
                            } catch (e: Exception) {
                                null
                            }
                        } ?: 800
                    }
                    execSetMultipleImage(
                        imageView,
                        imagePathList,
                        delay,
                    )
                }

                else -> {
                    val fadeInMilli = withContext(Dispatchers.IO) {
                        imageMap?.get(
                            imageFadeInMilliKey,
                        )?.let {
                            try {
                                it.toInt()
                            } catch (e: Exception){
                                null
                            }
                        }
                    }
                    execSetSingleImage(
                        imageView,
                        imagePathList.firstOrNull(),
                        null,
                        fadeInMilli,
                    )
                }
            }
        }
    }

    private suspend fun execSetSingleImage(
        imageView: AppCompatImageView,
        imagePathSrc: String?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        fadeInMilli: Int?,
    ){
        if(
            imagePathSrc.isNullOrEmpty()
        ) return
        val imageViewContext = imageView.context
        val requestBuilder: RequestBuilder<Drawable> =
            requestBuilderSrc ?: Glide.with(imageViewContext)
                .asDrawable()
                .sizeMultiplier(0.1f)
        val imagePathToIconType = withContext(Dispatchers.IO) {
            EditComponent.Template.ImageManager.makeIconAndTypePair(
                imagePathSrc
            )
        }
        val imagePath =
            imagePathToIconType.first
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "licon.txt").absolutePath,
//            listOf(
//                "imagePath: ${imagePathSrc}",
//                "icon: ${icon?.str}",
//            ).joinToString("\n\n")
//        )
        if(File(imagePathSrc).isFile){
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "liconImagePath.txt").absolutePath,
//                listOf(
//                    "imagePath: ${imagePathSrc}",
//                    "icon: ${icon?.str}",
//                ).joinToString("\n\n")
//            )
            when(fadeInMilli != null) {
                true -> Glide.with(imageViewContext)
                    .load(imagePathSrc)
                    .transition(DrawableTransitionOptions.withCrossFade(fadeInMilli))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .thumbnail(requestBuilder)
                    .into(imageView)
                else -> Glide.with(imageViewContext)
                    .load(imagePathSrc)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .thumbnail(requestBuilder)
                    .into(imageView)
            }
            return
        }
        val iconType = withContext(Dispatchers.IO) {
            imagePathToIconType.second.let { iconTypeStr ->
                EditComponent.Template.ImageManager.IconType.entries.firstOrNull {
                    it.type == iconTypeStr
                } ?: EditComponent.Template.ImageManager.IconType.IMAGE
            }
        }
//        val iconId = icon?.id
//        imageView.setAutofillHints(CmdClickIcons.values().firstOrNull {
//            it.id == iconId
//        }?.str)
        val assetsPath = withContext(Dispatchers.IO) {
            CmdClickIcons.entries.firstOrNull {
                it.str == imagePath
            }?.assetsPath
        }?: return
        val bitmap = withContext(Dispatchers.IO) {
            when (iconType) {
                EditComponent.Template.ImageManager.IconType.IMAGE -> {
                    val iconFile = ExecSetToolbarButtonImage.getImageFile(
                        assetsPath
                    )
                    BitmapTool.convertFileToBitmap(iconFile.absolutePath)
                }

                EditComponent.Template.ImageManager.IconType.ICON -> {
                    AssetsFileManager.assetsByteArray(
                        imageViewContext,
                        assetsPath,
                    )?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                }
            }
        }
//        if(bitmap == null){
//            imageView.isVisible = false
//            return
//        }
        when(fadeInMilli != null) {
            true ->
                Glide.with(imageViewContext)
                .load(bitmap)
                .transition(DrawableTransitionOptions.withCrossFade(fadeInMilli))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageView)
            else ->  Glide.with(imageViewContext)
                .load(bitmap)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageView)
        }
    }


    private suspend fun execSetMultipleImage(
        imageView: AppCompatImageView,
        imagePathList: List<String>,
        delay: Int,
    ){
        val imageViewContext = imageView.context
        val animationDrawable = AnimationDrawable()
        val bitmapList = withContext(Dispatchers.IO) {
            imagePathList.map { imagePathSrc ->
                if (File(imagePathSrc).isFile) {
                    return@map BitmapTool.convertFileToBitmap(imagePathSrc)
                }
                val imagePathToIconType = EditComponent.Template.ImageManager.makeIconAndTypePair(
                    imagePathSrc
                )
                val imagePath =
                    imagePathToIconType.first
                val iconType = imagePathToIconType.second.let { iconTypeStr ->
                    EditComponent.Template.ImageManager.IconType.entries.firstOrNull {
                        it.type == iconTypeStr
                    } ?: EditComponent.Template.ImageManager.IconType.IMAGE
                }
                val assetsPath = CmdClickIcons.entries.firstOrNull {
                    it.str == imagePath
                }?.assetsPath ?: return@map null
                when (iconType) {
                    EditComponent.Template.ImageManager.IconType.IMAGE -> {
                        val iconFile = ExecSetToolbarButtonImage.getImageFile(
                            assetsPath
                        )
                        BitmapTool.convertFileToBitmap(iconFile.absolutePath)
                    }

                    EditComponent.Template.ImageManager.IconType.ICON -> {
                        AssetsFileManager.assetsByteArray(
                            imageViewContext,
                            assetsPath,
                        )?.let {
                            BitmapFactory.decodeByteArray(it, 0, it.size)
                        }
                    }
                }
            }
        }
        bitmapList.forEach {
            if(it == null) return@forEach
            animationDrawable.addFrame(
                BitmapDrawable(imageViewContext.resources, it),
                delay
            )
        }
        animationDrawable.isOneShot = false
        imageView.setImageDrawable(animationDrawable)
        animationDrawable.start()
    }


    private suspend fun setCaption(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
        textPropertyMap: Map<String, String>?,
        density: Float,
    ) {
//        val enableTextSelect = withContext(Dispatchers.IO){
//            textPropertyMap?.get(
//                disableTextSelectKey
//            ) != switchOn
//        }
        val textViewContext = captionTextView.context
        withContext(Dispatchers.Main) {
            captionTextView.apply {
                textPropertyMap?.get(
                    textVisibleKey,
                ).let {
                        visibleStr ->
                    visibility = EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
                val lp = layoutParams as FrameLayout.LayoutParams
                lp.apply {
                    val overrideLayoutGravity = textPropertyMap?.get(
                        textLayoutGravityKey,
                    )?.let {
                            gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    } ?: Gravity.CENTER
                    gravity = overrideLayoutGravity
                    val overrideWidth = withContext(Dispatchers.IO) {
                        textPropertyMap?.get(
                            textWidthKey,
                        ).let {
                            EditComponent.Template.LinearLayoutUpdater.convertWidth(
                                it,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                density,
                            )
                        }
                    }
                    width = overrideWidth
                    val overrideHeight = withContext(Dispatchers.IO) {
                        textPropertyMap?.get(
                            textHeightKey,
                        ).let {
                            EditComponent.Template.LinearLayoutUpdater.convertHeight(
                                it,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                density,
                            )
                        }
                    }
                    height = overrideHeight
                    val marginData = EditComponent.Template.MarginData(
                        textPropertyMap?.get(
                            textMarginTopKey,
                        ),
                        textPropertyMap?.get(
                            textMarginBottomKey,
                        ),
                        textPropertyMap?.get(
                            textMarginStartKey,
                        ),
                        textPropertyMap?.get(
                            textMarginEndKey,
                        ),
                        density,
                    )
                    topMargin = marginData.marginTop ?: 0
                    bottomMargin = marginData.marginBottom ?: 0
                    marginStart = marginData.marginStart ?: 0
                    marginEnd = marginData.marginEnd ?: 0
                }
                val overrideGravity = textPropertyMap?.get(
                    textGravityKey,
                )?.let {
                        gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                } ?: Gravity.CENTER
                gravity = overrideGravity
                val paddingData = EditComponent.Template.PaddingData(
                    textPropertyMap?.get(
                        textPaddingTopKey,
                    ),
                    textPropertyMap?.get(
                        textPaddingBottomKey,
                    ),
                    textPropertyMap?.get(
                        textPaddingStartKey,
                    ),
                    textPropertyMap?.get(
                        textPaddingEndKey,
                    ),
                    density,
                )
                setPadding(
                    paddingData.paddingStart ?: 0,
                    paddingData.paddingTop ?: 0,
                    paddingData.paddingEnd ?: 0,
                    paddingData.paddingBottom ?: 0,
                )
//                val textTag = withContext(Dispatchers.IO) {
//                    textPropertyMap?.get(
//                        textTagKey,
//                    )
//                }
//                tag = textTag
                val settingValue = textMap?.get(
                    EditComponent.Template.TextManager.TextKey.SETTING_VALUE.key
                )
                setAutofillHints(settingValue)
                val overrideText = withContext(Dispatchers.IO) {
                    EditComponent.Template.TextManager.makeText(
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        textMap,
                        settingValue
                    )
                }
                text = overrideText
//        captionTextView.autofillHints?.firstOrNull(0)
//        captionTextView.hint = settingValue
                val overrideMaxLines = withContext(Dispatchers.IO){
                    textPropertyMap?.get(
                        textMaxLinesKey
                    )?.let {
                        try {
                            it.toInt()
                        }catch (e: Exception){
                            null
                        }
                    } ?: 1
                }
                maxLines = overrideMaxLines
                val textColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textColorKey,
                    )?.let {
                            colorStr ->
                        CmdClickColor.entries.firstOrNull {
                            it.str == colorStr
                        }
                    }
                }
                setFillColor(textColor?.id ?: R.color.fill_gray)
                val textBkColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textBkColorKey,
                    )?.let {
                            colorStr ->
                        CmdClickColor.entries.firstOrNull {
                            it.str == colorStr
                        }
                    }
                }
                background =
                    textBkColor?.let {
                        AppCompatResources.getDrawable(
                            textViewContext,
                            it.id
                        )
                    }
                //            captionTextView.setTextIsSelectable(enableTextSelect)
                val strokeColorStr = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        strokeColorKey,
                    )
                }
                CmdClickColor.entries.firstOrNull {
                    it.str == strokeColorStr
                }.let {
                    setStrokeColor(it?.id ?: R.color.white)
                }
                val strokeWidth = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        strokeWidthKey,
                    )?.let {
                        try {
                            it.toInt()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                outlineWidthSrc = strokeWidth ?: 2
                val overrideTextSize = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textSizeKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                overrideTextSize?.let {
                    textSize = it
                } ?: let {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size_16))
                }
                val textAlpha = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textAlphaKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                alpha = textAlpha ?: 1f
                val overrideTextStyle = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textStyleKey,
                    )?.let {
                            textStyleStr ->
                        EditComponent.Template.TextPropertyManager.TextStyle.entries.firstOrNull {
                            it.key == textStyleStr
                        }
                    }
                } ?: EditComponent.Template.TextPropertyManager.TextStyle.NORMAL
                val overrideFont = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textFontKey,
                    )?.let {
                            textFontStr ->
                        EditComponent.Template.TextPropertyManager.Font.entries.firstOrNull {
                            it.key == textFontStr
                        }
                    }
                } ?: EditComponent.Template.TextPropertyManager.Font.SANS_SERIF
                setTypeface(overrideFont.typeface, overrideTextStyle.style)
            }
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lcapt.txt").absolutePath,
//            listOf(
//                "textMap: ${textMap}",
//                "textPropertyMap: ${textPropertyMap}",
//                "strokeWidth: ${strokeWidth}",
//                "captionTextView.outlineWidthSrc: ${captionTextView.outlineWidthSrc}",
//
//            ).joinToString("\n")
//        )
    }

    suspend fun setCaptionByDynamic(
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
        textPropertyMap: Map<String, String>?,
        overrideText: String?,
    ) {
//        val enableTextSelect = withContext(Dispatchers.IO){
//            textPropertyMap?.get(
//                disableTextSelectKey
//            ) != switchOn
//        }
        val textViewContext = captionTextView.context
        withContext(Dispatchers.Main) {
            captionTextView.apply {
                val lp = layoutParams as FrameLayout.LayoutParams
                lp.apply {
                    val overrideLayoutGravity = textPropertyMap?.get(
                        textLayoutGravityKey,
                    )?.let {
                            gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    }
                    overrideLayoutGravity?.let {
                        gravity = it
                    }
                }
                val overrideGravity = textPropertyMap?.get(
                    textGravityKey,
                )?.let {
                        gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity

                }
                overrideGravity?.let {
                    gravity
                }
//        captionTextView.autofillHints?.firstOrNull(0)
//        captionTextView.hint = settingValue
                val isUpdate = textMap?.get(
                    onUpdateKey
                ) != switchOff
                if(isUpdate) {
                    overrideText?.let {
                        text = it
                    }
                }
                val overrideMaxLines = withContext(Dispatchers.IO){
                    textPropertyMap?.get(
                        textMaxLinesKey
                    )?.let {
                        try {
                            it.toInt()
                        }catch (e: Exception){
                            null
                        }
                    }
                }
                overrideMaxLines?.let {
                    maxLines = it
                }
                val textColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textColorKey,
                    )?.let {
                            colorStr ->
                        CmdClickColor.entries.firstOrNull {
                            it.str == colorStr
                        }
                    }
                }
                textColor?.let {
                    setFillColor(it.id)
                }
                val textBkColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textBkColorKey,
                    )?.let {
                            colorStr ->
                        CmdClickColor.entries.firstOrNull {
                            it.str == colorStr
                        }
                    }
                }
                textBkColor?.let {
                    background =
                        AppCompatResources.getDrawable(
                            textViewContext,
                            it.id
                        )

                }
                //            captionTextView.setTextIsSelectable(enableTextSelect)
                val strokeColorStr = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        strokeColorKey,
                    )
                }
                CmdClickColor.entries.firstOrNull {
                    it.str == strokeColorStr
                }?.let {
                    setStrokeColor(it.id)
                }
                val strokeWidth = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        strokeWidthKey,
                    )?.let {
                        try {
                            it.toInt()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                strokeWidth?.let {
                    outlineWidthSrc = it
                }
                val overrideTextSize = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textSizeKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                overrideTextSize?.let {
                    textSize = it
                }
                val textAlpha = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textAlphaKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                textAlpha?.let {
                    alpha = it
                }
                val overrideTextStyle = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textStyleKey,
                    )?.let {
                            textStyleStr ->
                        EditComponent.Template.TextPropertyManager.TextStyle.entries.firstOrNull {
                            it.key == textStyleStr
                        }?.style
                    }
                } ?: typeface.style
                val overrideFont = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textFontKey,
                    )?.let {
                            textFontStr ->
                        EditComponent.Template.TextPropertyManager.Font.entries.firstOrNull {
                            it.key == textFontStr
                        }?.typeface
                    }
                } ?: typeface
                setTypeface(overrideFont, overrideTextStyle)
            }
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lcapt.txt").absolutePath,
//            listOf(
//                "textMap: ${textMap}",
//                "textPropertyMap: ${textPropertyMap}",
//                "strokeWidth: ${strokeWidth}",
//                "captionTextView.outlineWidthSrc: ${captionTextView.outlineWidthSrc}",
//
//            ).joinToString("\n")
//        )
    }

    private fun makeColor(
        context: Context,
        colorStr: String?,
    ): Int? {
        return CmdClickColor.entries.firstOrNull {
            it.str == colorStr
        }?.let {
            ContextCompat.getColor(context, it.id)
        } ?: let {
            try {
                Color.parseColor(colorStr)
            } catch (e: Exception) {
                null
            }
        }
    }
}