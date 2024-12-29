package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickBkImageInfo
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.command_index_fragment.UrlImageDownloader
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.CcDotArt
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.str.PairListTool
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val verticalWeightKey = EditComponent.Template.EditComponentKey.VERTICAL_WEIGHT.key
    private val percentageWidthKey = EditComponent.Template.EditComponentKey.PERCENTAGE_WIDTH.key
    private val percentageHeightKey = EditComponent.Template.EditComponentKey.PERCENTAGE_HEIGHT.key
    private val dimensionRatioKey = EditComponent.Template.EditComponentKey.DIMENSION_RATIO.key
    private val horizontalChainStyleKey =
        EditComponent.Template.EditComponentKey.HORIZONTAL_CHAIN_STYLE.key
    private val verticalChainStyleKey =
        EditComponent.Template.EditComponentKey.VERTICAL_CHAIN_STYLE.key


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
    private val imageRotateKey = EditComponent.Template.ImagePropertyManager.PropertyKey.ROTATE.key
    private val imageScaleXKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE_X.key
    private val imageScaleYKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE_Y.key

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
    private val textShadowRadiusKey = EditComponent.Template.TextPropertyManager.Property.SHADOW_RADIUS.key
    private val textShadowColorKey = EditComponent.Template.TextPropertyManager.Property.SHADOW_COLOR.key
    private val textShadowXKey = EditComponent.Template.TextPropertyManager.Property.SHADOW_X.key
    private val textShadowYKey = EditComponent.Template.TextPropertyManager.Property.SHADOW_Y.key
    private val letterSpacingKey = EditComponent.Template.TextPropertyManager.Property.LETTER_SPACING.key
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
        whereForErr: String,
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
            whereForErr,
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
//                    if(overrideTag == "rndRect1") {
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "limagemap.txt").absolutePath,
//                            listOf(
//                                "frameKeyPairList: ${frameKeyPairList}"
//                            ).joinToString("\n")
//                        )
//                    }
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
                        whereForErr,
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
                        whereForErr,
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
        whereForErr: String,
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
            val bkColorStr = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    bkColorKey,
                )
            }
            val bkColorDrawable = withContext(Dispatchers.IO) {
                if(
                    bkColorStr.isNullOrEmpty()
                ) return@withContext null
                ColorTool.parseColorStr(
                    context,
                    bkColorStr,
                    bkColorKey,
                    whereForErr,
                )?.let {
                    ColorDrawable(Color.parseColor(it))
                }
            }

//            bkColorStr?.let {
//                    ColorStateList.valueOf(Color.parseColor(it))
//                }
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
        val where = "EditConstraintFrameMaker.setButtonFrameLayoutByDynamic"
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
                    EditComponent.Template.ConstraintManager.makeFloat(
                        PairListTool.getValue(
                            frameKeyPairList,
                            horizontalBiasKey
                        )
                    )?.let {
                        horizontalBias = it
                    }
                }
                withContext(Dispatchers.Main){
                    EditComponent.Template.ConstraintManager.makeFloat(
                        PairListTool.getValue(
                            frameKeyPairList,
                            percentageWidthKey
                        )
                    )?.let {
                        matchConstraintPercentWidth = it
                    }
                }
                withContext(Dispatchers.Main){
                    EditComponent.Template.ConstraintManager.makeFloat(
                        PairListTool.getValue(
                            frameKeyPairList,
                            percentageHeightKey
                        )
                    )?.let {
                        matchConstraintPercentHeight = it
                    }
                }
                withContext(Dispatchers.Main){
                    EditComponent.Template.ConstraintManager.makeFloat(
                        PairListTool.getValue(
                            frameKeyPairList,
                            horizontalWeightKey
                        )
                    )?.let {
                        horizontalWeight = it
                    }
                }
                withContext(Dispatchers.Main){
                    EditComponent.Template.ConstraintManager.makeFloat(
                        PairListTool.getValue(
                            frameKeyPairList,
                            verticalWeightKey
                        )
                    )?.let {
                        verticalWeight = it
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
                withContext(Dispatchers.Main){
                    PairListTool.getValue(
                        frameKeyPairList,
                        horizontalChainStyleKey
                    )?.let {
                        EditComponent.Template.ConstraintManager.getChainStyleInt(
                            it,
                        )
                    }?.let {
                        horizontalChainStyle = it
                    }
                }
                withContext(Dispatchers.Main){
                    PairListTool.getValue(
                        frameKeyPairList,
                        verticalChainStyleKey
                    )?.let {
                        EditComponent.Template.ConstraintManager.getChainStyleInt(
                            it,
                        )
                    }?.let {
                        verticalChainStyle = it
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
                    val parsedColorStr = ColorTool.parseColorStr(
                        context,
                        colorStr,
                        bkColorKey,
                        where,
                    )
                    Color.parseColor(parsedColorStr)
//                    CmdClickColor.entries.firstOrNull {
//                        it.str == colorStr
//                    }
                }?.let {
                    background = ColorDrawable(it)
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
        where: String,
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
                        val matrixStormConfigMap = withContext(Dispatchers.IO){
                            EditComponent.Template.ImageManager.MatrixStormManager.makeConfigMap(
                                imageMap,
                            )
                        }
                        val autoRndIconsConfigMap = withContext(Dispatchers.IO){
                            EditComponent.Template.ImageManager.AutoRndIconsManager.makeConfigMap(
                                imageMap,
                            )
                        }
                        val autoRndStringsConfigMap = withContext(Dispatchers.IO){
                            EditComponent.Template.ImageManager.AutoRndStringsManager.makeConfigMap(
                                imageMap,
                            )
                        }
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
                                    matrixStormConfigMap,
                                    autoRndIconsConfigMap,
                                    autoRndStringsConfigMap,
                                    where,
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
                                val blurRadiusToSampling = withContext(Dispatchers.IO){
                                    EditComponent.Template.ImagePropertyManager.BlurManager.getBlueRadiusToSampling(
                                        imagePropertyMap
                                    )
                                }
                                execSetSingleImage(
                                    imageView,
                                    imagePathList.firstOrNull(),
                                    requestBuilderSrc,
                                    fadeInMilli,
                                    matrixStormConfigMap,
                                    autoRndIconsConfigMap,
                                    autoRndStringsConfigMap,
                                    blurRadiusToSampling,
                                    where,
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
                        )?.let {
                                colorStr ->
                            val parsedColorStr = ColorTool.parseColorStr(
                                context,
                                colorStr,
                                imageColorKey,
                                where,
                            )
                            Color.parseColor(parsedColorStr)
                        }
                    }
                    imageTintList = imageColor?.let {
                        ColorStateList.valueOf(it)
                    }
                    val imageBkColor = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageBkColorKey,
                        )?.let {
                            colorStr ->
                            val parsedColorStr = ColorTool.parseColorStr(
                                context,
                                colorStr,
                                imageBkColorKey,
                                where,
                            )
                            Color.parseColor(parsedColorStr)
                        }
                    }
                    background = imageBkColor?.let {
                        ColorDrawable(imageBkColor)
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
                    val rotateFloat = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageRotateKey,
                        )?.let {
                            try {
                                it.toFloat()
                            } catch(e: Exception){
                                null
                            }
                        } ?: 0f
                    }
                    rotation = rotateFloat
                    val scaleXFloat = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageScaleXKey,
                        )?.let {
                            try {
                                it.toFloat()
                            } catch(e: Exception){
                                null
                            }
                        } ?: 1f
                    }
                    scaleX = scaleXFloat
                    val scaleYFloat = withContext(Dispatchers.IO) {
                        imagePropertyMap?.get(
                            imageScaleYKey,
                        )?.let {
                            try {
                                it.toFloat()
                            } catch(e: Exception){
                                null
                            }
                        } ?: scaleY
                    }
                    scaleY = scaleYFloat
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
        val where = "EditConstraintFrameMaker.setImageViewForDynamic"
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
            withContext(Dispatchers.Main) {
                imagePropertyMap?.get(
                    imageColorKey,
                )?.let {
                        colorStr ->
                    val parsedColorStr = ColorTool.parseColorStr(
                        context,
                        colorStr,
                        imageColorKey,
                        where,
                    )
                    Color.parseColor(parsedColorStr)
                }?.let {
                    imageTintList = ColorStateList.valueOf(it)
                }
            }
            withContext(Dispatchers.Main) {
                imagePropertyMap?.get(
                    imageBkColorKey,
                )?.let { colorStr ->
                    val parsedColorStr = ColorTool.parseColorStr(
                        context,
                        colorStr,
                        imageBkColorKey,
                        where,
                    )
                    Color.parseColor(parsedColorStr)
                }?.let {
                    background = ColorDrawable(it)
                }
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

            withContext(Dispatchers.Main) {
                imagePropertyMap?.get(
                    imageRotateKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }?.let {
                    rotation = it
                }
            }
            val scaleXFloat = withContext(Dispatchers.Main) {
                imagePropertyMap?.get(
                    imageScaleXKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }?.let {
                    scaleX = it
                }
            }
            val scaleYFloat = withContext(Dispatchers.Main) {
                imagePropertyMap?.get(
                    imageScaleYKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }?.let {
                    scaleY = it
                }
            }
        }

        if(
            imagePathList.isNullOrEmpty()
            ) return
        CoroutineScope(Dispatchers.Main).launch {
            val matrixStormConfigMap = withContext(Dispatchers.IO){
                EditComponent.Template.ImageManager.MatrixStormManager.makeConfigMap(
                    imageMap,
                )
            }
            val autoRndIconsConfigMap = withContext(Dispatchers.IO){
                EditComponent.Template.ImageManager.AutoRndIconsManager.makeConfigMap(
                    imageMap,
                )
            }
            val autoRndStringsConfigMap = withContext(Dispatchers.IO){
                EditComponent.Template.ImageManager.AutoRndStringsManager.makeConfigMap(
                    imageMap,
                )
            }
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
                        matrixStormConfigMap,
                        autoRndIconsConfigMap,
                        autoRndStringsConfigMap,
                        where,
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
                    val blurRadiusToSampling = withContext(Dispatchers.IO){
                        EditComponent.Template.ImagePropertyManager.BlurManager.getBlueRadiusToSampling(
                            imagePropertyMap
                        )
                    }
                    execSetSingleImage(
                        imageView,
                        imagePathList.firstOrNull(),
                        null,
                        fadeInMilli,
                        matrixStormConfigMap,
                        autoRndIconsConfigMap,
                        autoRndStringsConfigMap,
                        blurRadiusToSampling,
                        where,
                    )
                }
            }
        }
    }

    private enum class ImageMacro{
        WALL_DIR
    }

    private suspend fun execSetSingleImage(
        imageView: AppCompatImageView,
        imagePathSrc: String?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        fadeInMilli: Int?,
        matrixStormConfigMap: Map<String, String>,
        autoRndIconsConfigMap: Map<String, String>,
        autoRndStringsConfigMap: Map<String, String>,
        blurRadiusAndSamplingPair: Pair<Int, Int>?,
        where: String,
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
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "licon.txt").absolutePath,
//            listOf(
//                "imagePath: ${imagePathSrc}",
//                "icon: ${icon?.str}",
//            ).joinToString("\n\n")
//        )


        val bitmap = ImageCreator.byFilePath(imagePathSrc)
            ?: ImageCreator.byImageMacro(
            imagePathSrc
        ) ?: ImageCreator.byAutoCreateImage(
                imageViewContext,
                imagePathSrc,
                matrixStormConfigMap,
                autoRndIconsConfigMap,
                autoRndStringsConfigMap,
                where,
            ) ?: ImageCreator.byIconMacro(
            imageViewContext,
            imagePathToIconType
        )
        if(blurRadiusAndSamplingPair != null){
            val blurRadius = blurRadiusAndSamplingPair.first
            val blurSampling = blurRadiusAndSamplingPair.second
            when(fadeInMilli != null) {
                true -> Blurry.with(imageViewContext)
                    .radius(blurRadius)
                    .sampling(blurSampling)
                    .async()
                    .animate(fadeInMilli)
                    .from(bitmap)
                    .into(imageView)
                else ->  Blurry.with(imageViewContext)
                    .radius(blurRadius)
                    .sampling(blurSampling)
                    .async()
                    .from(bitmap)
                    .into( imageView)
            }
            return
        }
        when(fadeInMilli != null) {
            true -> Glide.with(imageViewContext)
                .load(bitmap)
                .transition(DrawableTransitionOptions.withCrossFade(fadeInMilli))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .thumbnail(requestBuilder)
                .into(imageView)
            else -> Glide.with(imageViewContext)
                .load(bitmap)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .thumbnail(requestBuilder)
                .into(imageView)
        }
        if(bitmap == null){
            imageView.isVisible = false
            return
        }
    }

    private object ImageCreator {

        fun byFilePath(
            imagePathSrc: String,
        ): Bitmap? {
            if(
                !File(imagePathSrc).isFile
            ) return null
            return BitmapTool.convertFileToBitmap(imagePathSrc)
        }
        suspend fun byImageMacro(
            imageMacro: String,
        ): Bitmap? {
            val cmdClickBkImageFilePath = BkFilePath.get(
                imageMacro
            ) ?: return null
            return withContext(Dispatchers.IO) {
                BitmapTool.convertFileToBitmap(cmdClickBkImageFilePath)?.let {
                    BitmapTool.ImageTransformer.cutCenter(
                        it,
                        400,
                        800
                    )
                }
            }
        }

        private object BkFilePath {

            fun get(
                wallRelativePath: String,
            ): String? {
                val fannelWallDirPath = UrlImageDownloader.fannelWallDirPath
                val fannelWallDirName = File(fannelWallDirPath).name
                if(
                    !wallRelativePath
                        .startsWith(fannelWallDirName)
                ) return null
                val wallPathObj = File(
                    UrlImageDownloader.imageDirObj.absolutePath,
                    wallRelativePath
                )
                val wallPathOrDirPath = wallPathObj.absolutePath
                return when(true){
                    wallPathObj.isFile -> {
                        if(
                            !isImageFile(wallPathOrDirPath)
                        ) return null
                        wallPathOrDirPath
                    }
                    else -> {
                        File(fannelWallDirPath).walk().filter {
                                wallImageFileEntry ->
                            if(
                                !wallImageFileEntry.isFile
                            ) return@filter false
                            val wallImageFilePath =
                                wallImageFileEntry.absolutePath
                            wallImageFilePath.startsWith(
                                wallPathOrDirPath
                            ) && isImageFile(
                                wallImageFilePath
                            )
                        }.shuffled().firstOrNull()?.absolutePath
                            ?: return null
                    }
                }
            }

            private fun isImageFile(
                wallImageFilePath: String
            ): Boolean {
                val imageFileExtendList = listOf(".jpeg", ".jpg", ".png")
                return imageFileExtendList.any {
                        imageFileExtend ->
                    wallImageFilePath.endsWith(imageFileExtend)
                }
            }

            private fun getBkImageFilePathFromDirPath(
                bkImageDirPath: String,
            ): String {
                return FileSystems.sortedFiles(
                    bkImageDirPath
                ).random().let {
                    File(bkImageDirPath, it).absolutePath
                }
            }
        }

        suspend fun byAutoCreateImage(
            context: Context,
            autoCreateMacroStr: String,
            matrixStormConfigMap: Map<String, String>,
            autoRndIconsConfigMap: Map<String, String>,
            autoRndStringsConfigMap: Map<String, String>,
            where: String,
        ): Bitmap? {
            val autoCreateMacro = CmdClickBkImageInfo.CmdClickAutoCreateImage.entries.firstOrNull {
                it.name == autoCreateMacroStr
            }
            if (
                autoCreateMacro == null
            ) return null
            val cmdClickAutoCreateBitmap = withContext(Dispatchers.IO) {
                when (autoCreateMacro) {
                    CmdClickBkImageInfo.CmdClickAutoCreateImage.AUTO_MATRIX_STORM
                        -> makeMatrixStorm(
                        context,
                        matrixStormConfigMap,
                        where
                        )
                    CmdClickBkImageInfo.CmdClickAutoCreateImage.AUTO_RND_ICONS
                        -> makeAutoRndIcons(
                            context,
                            autoRndIconsConfigMap,
                            where,
                        )
                    CmdClickBkImageInfo.CmdClickAutoCreateImage.AUTO_RND_STRINGS
                        ->  makeAutoRndStrings(
                        context,
                        autoRndStringsConfigMap,
                        where
                        )
                }
            }
//            FileSystems.writeFromByteArray(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
//                    "lCREATE_OVERLAY_RECT00.png"
//                ).absolutePath,
//                BitmapTool.convertBitmapToByteArray(cmdClickAutoCreateBitmap)
//            )
            return cmdClickAutoCreateBitmap
        }

        suspend fun makeMatrixStorm(
            context: Context,
            matrixStormConfigMap: Map<String, String>,
            where: String,
        ): Bitmap? {
            val goalWidth =
                EditComponent.Template.ImageManager.MatrixStormManager.getWidth(
                    matrixStormConfigMap
                ) ?: 300
            val widthMulti = EditComponent.Template.ImageManager.MatrixStormManager.getXMulti(
                matrixStormConfigMap
            ) ?: 60
            val pieceWidth = goalWidth / widthMulti
            val goalHeight =
                EditComponent.Template.ImageManager.MatrixStormManager.getHeight(
                    matrixStormConfigMap
                ) ?: 600
            val heightMulti = EditComponent.Template.ImageManager.MatrixStormManager.getYMulti(
                matrixStormConfigMap
            ) ?: 120
            val iconType = EditComponent.Template.ImageManager.MatrixStormManager.getIconType(
                matrixStormConfigMap
            )
            val iconColorStr = EditComponent.Template.ImageManager.MatrixStormManager.getColor(
                context,
                matrixStormConfigMap,
                where
            ) ?: ColorTool.convertColorToHex(
                Color.BLACK
            )
            val shapeStr = EditComponent.Template.ImageManager.MatrixStormManager.getShape(
                matrixStormConfigMap
            )
            //            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lRndRext.txt").absolutePath,
//                listOf(
//                    "leftRectsConfigMap: ${autoRndIconsConfigMap}",
//                    "baseWidth: ${baseWidth}",
//                    "baseHeight: ${baseHeight}",
//                    "pieceWidth: ${pieceWidth}",
//                    "pieceHeight: ${pieceHeight}",
//                    "times: ${times}",
//                    "shapeType: $shapeType",
//                ).joinToString("\n")
//            )
            val pieceHeight = goalHeight / heightMulti
            val pieceBitmap = makePieceBitmap(
                context,
                pieceWidth,
                pieceHeight,
                shapeStr,
                iconType,
                iconColorStr,
            )
            return CcDotArt.makeMatrixStorm(
                pieceBitmap,
                widthMulti,
                heightMulti,
            )
        }

        private fun makeAutoRndIcons(
            context: Context,
            autoRndIconsConfigMap: Map<String, String>,
            where: String,
        ): Bitmap {
            val baseWidth =
                EditComponent.Template.ImageManager.AutoRndIconsManager.getWidth(
                    autoRndIconsConfigMap
                ) ?: 300
            val baseHeight =
                EditComponent.Template.ImageManager.AutoRndIconsManager.getHeight(
                    autoRndIconsConfigMap
                ) ?: 600
            val pieceWidth = EditComponent.Template.ImageManager.AutoRndIconsManager.getPieceWidth(
                autoRndIconsConfigMap
            ) ?: 100
            val pieceHeight = EditComponent.Template.ImageManager.AutoRndIconsManager.getPieceHeight(
                autoRndIconsConfigMap
            ) ?: 100
            val times = EditComponent.Template.ImageManager.AutoRndIconsManager.getTimes(
                autoRndIconsConfigMap
            ) ?: 10
            val iconType = EditComponent.Template.ImageManager.AutoRndIconsManager.getIconType(
                autoRndIconsConfigMap
            )
            val blackHexStr = ColorTool.convertColorToHex(
                Color.BLACK
            )
            val iconColorStr = EditComponent.Template.ImageManager.AutoRndIconsManager.getColor(
                context,
                autoRndIconsConfigMap,
                where
            ) ?: blackHexStr
            val bkColorStr = EditComponent.Template.ImageManager.AutoRndIconsManager.getBkColor(
                context,
                autoRndIconsConfigMap,
                where
            ) ?: "#00000000"
            val shapeStr = EditComponent.Template.ImageManager.AutoRndIconsManager.getShape(
                autoRndIconsConfigMap
            )
            val pieceBitmap = makePieceBitmap(
                context,
                pieceWidth,
                pieceHeight,
                shapeStr,
                iconType,
                iconColorStr,
            )
            val layout = EditComponent.Template.ImageManager.AutoRndIconsManager.getLayout(
                autoRndIconsConfigMap
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lRndRext.txt").absolutePath,
//                listOf(
//                    "leftRectsConfigMap: ${autoRndIconsConfigMap}",
//                    "baseWidth: ${baseWidth}",
//                    "baseHeight: ${baseHeight}",
//                    "pieceWidth: ${pieceWidth}",
//                    "pieceHeight: ${pieceHeight}",
//                    "times: ${times}",
//                    "iconType: $iconType",
//                    "shapeStr: ${shapeStr}"
//                ).joinToString("\n")
//            )
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "lpieceBitmap.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(pieceBitmap)
//            )
            val autoRndIcons = when(layout){
                EditComponent.Template.ImageManager.AutoRndIconsManager.Layout.LEFT -> {
                   CcDotArt.MistMaker.makeLeftRndBitmaps(
                        baseWidth,
                        baseHeight,
                        pieceBitmap,
                        times
                    ).let {
                       val cutWidth = (baseWidth * 0.8).toInt()
                       val cutHeight = (baseHeight * 0.8).toInt()
                       BitmapTool.ImageTransformer.cutByTarget(
                           it,
                           cutWidth,
                           cutHeight,
                           it.width - cutWidth,
                           (it.height - cutHeight) / 2
                       )
                   }
                }
                EditComponent.Template.ImageManager.AutoRndIconsManager.Layout.RND -> {
                    CcDotArt.MistMaker.makeRndBitmap(
                        baseWidth,
                        baseHeight,
                        bkColorStr,
                        pieceBitmap,
                        times
                    )
//                        .let {
//                        val cutWidth = (baseWidth * 0.8).toInt()
//                        val cutHeight = (baseHeight * 0.8).toInt()
//                        BitmapTool.ImageTransformer.cutCenter2(
//                            it,
//                            cutWidth,
//                            cutHeight,
//                        )
//                    }
                }
            }

//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "lrndRect.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(autoRndBitmap)
//            )
            return autoRndIcons
        }

        private fun makePieceBitmap(
            context: Context,
            pieceWidth: Int,
            pieceHeight: Int,
            shapeStr: String,
            iconType: EditComponent.IconType,
            iconColorStr: String,
        ): Bitmap {
            return let {
                if(
                    File(shapeStr).isFile
                ) return@let BitmapTool.convertFileToBitmap(shapeStr)?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        pieceWidth,
                        pieceHeight,
                        true,
                    )
                }
                val shape = CmdClickIcons.entries.firstOrNull {
                    it.str == shapeStr
                } ?: CmdClickIcons.RECT
                return@let when(iconType){
                    EditComponent.IconType.IMAGE -> {
                        val iconFile = ExecSetToolbarButtonImage.getImageFile(
                            shape.assetsPath
                        )
                        BitmapTool.convertFileToBitmap(iconFile.absolutePath)?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                pieceWidth,
                                pieceHeight,
                                true,
                            )
                        }
                    }
                    EditComponent.IconType.SVG -> {
                        AppCompatResources.getDrawable(
                            context,
                            shape.id,
                        )?.toBitmap(
                            pieceWidth,
                            pieceHeight
                        )?.let convertBlack@ {
                            val bitmap = BitmapTool.ImageTransformer.convertBlackToColor(
                                it,
                                iconColorStr
                            )
//                            FileSystems.writeFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "lRndRext_shape.txt").absolutePath,
//                                listOf(
//                                    "shape: ${shape}",
//                                    "pieceWidth: ${pieceWidth}",
//                                    "pieceHeight: ${pieceHeight}",
//                                    "iconType: $iconType",
//                                    "shapeStr: ${shapeStr}",
//                                    "bitmap: ${bitmap == null}"
//                                ).joinToString("\n")
//                            )
//                            FileSystems.writeFromByteArray(
//                                File(UsePath.cmdclickDefaultAppDirPath, "lbitmap_it.png").absolutePath,
//                                BitmapTool.convertBitmapToByteArray(it)
//                            )
//                            FileSystems.writeFromByteArray(
//                                File(UsePath.cmdclickDefaultAppDirPath, "lbitmap.png").absolutePath,
//                                BitmapTool.convertBitmapToByteArray(bitmap)
//                            )
                            bitmap
                        }
                    }
                }
            } ?: BitmapTool.ImageTransformer.makeRect(
                iconColorStr,
                pieceWidth,
                pieceHeight
            )
        }

        private fun makeAutoRndStrings(
            context: Context,
            autoRndStringsConfigMap: Map<String, String>,
            where: String,
        ): Bitmap {
            val baseWidth =
                EditComponent.Template.ImageManager.AutoRndStringsManager.getWidth(
                    autoRndStringsConfigMap
                ) ?: 300
            val baseHeight =
                EditComponent.Template.ImageManager.AutoRndStringsManager.getHeight(
                    autoRndStringsConfigMap
                ) ?: 600
            val bkColorStr = EditComponent.Template.ImageManager.AutoRndStringsManager.getBkColor(
                context,
                autoRndStringsConfigMap,
                where
            ) ?: "#00000000"
            val pieceWidth = EditComponent.Template.ImageManager.AutoRndStringsManager.getPieceWidth(
                autoRndStringsConfigMap
            ) ?: 40f
            val pieceHeight = EditComponent.Template.ImageManager.AutoRndStringsManager.getPieceHeight(
                autoRndStringsConfigMap
            ) ?: 40f
            val times = EditComponent.Template.ImageManager.AutoRndStringsManager.getTimes(
                autoRndStringsConfigMap
            ) ?: 10
            val string = EditComponent.Template.ImageManager.AutoRndStringsManager.getString(
                autoRndStringsConfigMap
            ) ?: "C"
            val fontSize = EditComponent.Template.ImageManager.AutoRndStringsManager.getFontSize(
                autoRndStringsConfigMap
            ) ?: 20f
            val fontType = EditComponent.Template.ImageManager.AutoRndStringsManager.getFontType(
                autoRndStringsConfigMap
            )
            val fontStyle = EditComponent.Template.ImageManager.AutoRndStringsManager.getFontStyle(
                autoRndStringsConfigMap
            )
            val color = EditComponent.Template.ImageManager.AutoRndStringsManager.getColor(
                context,
                autoRndStringsConfigMap,
                where
            )?.let {
                Color.parseColor(it)
            } ?: Color.BLACK
            val strokeColor = EditComponent.Template.ImageManager.AutoRndStringsManager.getStrokeColor(
                context,
                autoRndStringsConfigMap,
                where
            )?.let {
                Color.parseColor(it)
            } ?: Color.BLACK
            val strokeWidthInt = EditComponent.Template.ImageManager.AutoRndStringsManager.getStrokeWidth(
                autoRndStringsConfigMap,
            ) ?: 0f
            val letterSpacingFloat = EditComponent.Template.ImageManager.AutoRndStringsManager.getLetterSpacing(
                autoRndStringsConfigMap,
            ) ?: 0f
            val layout = EditComponent.Template.ImageManager.AutoRndStringsManager.getLayout(
                autoRndStringsConfigMap
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lstringsBk.txt").absolutePath,
//                listOf(
//                    "baseWidth: ${baseWidth}",
//                    "baseHeight: ${baseHeight}",
//                    "pieceWidth: ${pieceWidth}",
//                    "pieceHeight: ${pieceHeight}",
//                    "times: ${times}",
//                    "string: ${string}",
//                    "fontSize: ${fontSize}",
//                    "fontType: ${fontType}",
//                    "fontStyle: ${fontStyle}",
//                ).joinToString("\n")
//            )
//            Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
            val stringBitmap = BitmapTool.DrawText.drawTextToBitmap(
                string,
                pieceWidth,
                pieceHeight,
                null,
                fontSize,
                color,
                strokeColor,
                strokeWidthInt,
                null,
                letterSpacingFloat,
                font = Typeface.create(
                    fontType,
                    fontStyle
                ),
                isAntiAlias = true,
            ).let {
                val cutWidth = (pieceWidth * 0.8).toInt()
                val cutHeight = (pieceHeight * 0.8).toInt()
                BitmapTool.ImageTransformer.cutCenter2(
                    it,
                    cutWidth,
                    cutHeight
                )
            }
            val autoRndStringsBitmap = when(layout){
                EditComponent.Template.ImageManager.AutoRndStringsManager.Layout.LEFT -> {
                    CcDotArt.MistMaker.makeLeftRndBitmaps(
                        baseWidth,
                        baseHeight,
                        stringBitmap,
                        times
                    ).let {
                        val cutWidth = (baseWidth * 0.8).toInt()
                        val cutHeight = (baseHeight * 0.8).toInt()
                        BitmapTool.ImageTransformer.cutByTarget(
                            it,
                            cutWidth,
                            cutHeight,
                            it.width - cutWidth,
                            (it.height - cutHeight) / 2
                        )
                    }
                }
                EditComponent.Template.ImageManager.AutoRndStringsManager.Layout.RND -> {
                    CcDotArt.MistMaker.makeRndBitmap(
                        baseWidth,
                        baseHeight,
                        bkColorStr,
                        stringBitmap,
                        times
                    )
//                        .let {
//                        val cutWidth = (baseWidth * 0.8).toInt()
//                        val cutHeight = (baseHeight * 0.8).toInt()
//                        BitmapTool.ImageTransformer.cutCenter2(
//                            it,
//                            cutWidth,
//                            cutHeight,
//                        )
//                    }
                }
            }

//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "lstringBitmap.png").absolutePath,
//               BitmapTool.convertBitmapToByteArray(stringBitmap)
//            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lRndRext.txt").absolutePath,
//                listOf(
//                    "leftRectsConfigMap: ${leftRectsConfigMap}",
//                    "baseWidth: ${baseWidth}",
//                    "baseHeight: ${baseHeight}",
//                    "pieceWidth: ${pieceWidth}",
//                    "pieceHeight: ${pieceHeight}",
//                    "times: ${times}",
//                ).joinToString("\n")
//            )
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "lstringsBitmap.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(stringsBitmap)
//            )
            return autoRndStringsBitmap
        }

        suspend fun byIconMacro(
            context: Context?,
            imagePathToIconType: Pair<String, String>,
        ): Bitmap? {
            val imagePath =
                imagePathToIconType.first
            val iconType = withContext(Dispatchers.IO) {
                imagePathToIconType.second.let { iconTypeStr ->
                    EditComponent.IconType.entries.firstOrNull {
                        it.name == iconTypeStr
                    } ?: EditComponent.IconType.IMAGE
                }
            }
            val assetsPath = withContext(Dispatchers.IO) {
                CmdClickIcons.entries.firstOrNull {
                    it.str == imagePath
                }?.assetsPath
            }?: return null
            return withContext(Dispatchers.IO) {
                when (iconType) {
                    EditComponent.IconType.IMAGE -> {
                        val iconFile = ExecSetToolbarButtonImage.getImageFile(
                            assetsPath
                        )
                        BitmapTool.convertFileToBitmap(iconFile.absolutePath)
                    }

                    EditComponent.IconType.SVG -> {
                        AssetsFileManager.assetsByteArray(
                            context,
                            assetsPath,
                        )?.let {
                            BitmapFactory.decodeByteArray(it, 0, it.size)
                        }
                    }
                }
            }
        }
    }


    private suspend fun execSetMultipleImage(
        imageView: AppCompatImageView,
        imagePathList: List<String>,
        delay: Int,
        matrixStormConfigMap: Map<String, String>,
        autoRndIconsConfigMap: Map<String, String>,
        autoRndStringsConfigMap: Map<String, String>,
        where: String,
    ){
        val imageViewContext = imageView.context
        val animationDrawable = AnimationDrawable()
        val bitmapList = withContext(Dispatchers.IO) {
            val bitmapIndexToListJob = imagePathList.mapIndexed { index, imagePathSrc ->
                async {
                    val imagePathToIconType =
                        EditComponent.Template.ImageManager.makeIconAndTypePair(
                            imagePathSrc
                        )
                    val bitmap = ImageCreator.byFilePath(imagePathSrc)
                        ?: ImageCreator.byImageMacro(
                            imagePathSrc
                        ) ?: ImageCreator.byAutoCreateImage(
                            imageViewContext,
                            imagePathSrc,
                            matrixStormConfigMap,
                            autoRndIconsConfigMap,
                            autoRndStringsConfigMap,
                            where,
                        ) ?: ImageCreator.byIconMacro(
                            imageViewContext,
                            imagePathToIconType
                        )
                    index to bitmap
                }
            }
            bitmapIndexToListJob.awaitAll().sortedBy { it.first }.map {
                it.second
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
        where: String,
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
                        val parsedColorStr = ColorTool.parseColorStr(
                            context,
                            colorStr,
                            textColorKey,
                            where,
                        )
                        Color.parseColor(parsedColorStr)
                    }
                }
                setFillColor(textColor ?: R.color.fill_gray)
                val textBkColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textBkColorKey,
                    )?.let {
                            colorStr ->
                        val parsedColorStr = ColorTool.parseColorStr(
                            context,
                            colorStr,
                            textBkColorKey,
                            where,
                        )
                        Color.parseColor(parsedColorStr)
//                        CmdClickColor.entries.firstOrNull {
//                            it.str == colorStr
//                        }
                    }
                }
                background =
                    textBkColor?.let {
                        ColorDrawable(it)
                    }
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
                strokeWidthSrc = strokeWidth ?: 2
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
                val letterSpacingFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        letterSpacingKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: 0f
                }
                letterSpacing = letterSpacingFloat
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
                val textShadowXFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowXKey,
                    )?.let {
                        try {
                            ScreenSizeCalculator.toDpByDensity(
                                it.toFloat(),
                                density
                            ).toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: 0f
                }
                val textShadowYFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowYKey,
                    )?.let {
                        try {
                            ScreenSizeCalculator.toDpByDensity(
                                it.toFloat(),
                                density
                            ).toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: 0f
                }
                val textShadowRadiusFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowRadiusKey,
                    )?.let {
                        try {
                            ScreenSizeCalculator.toDpByDensity(
                                it.toFloat(),
                                density
                            ).toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: 0f
                }
                val textShadowColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowColorKey,
                    )?.let {
                        val colorStr = ColorTool.parseColorStr(
                            context,
                            it,
                            textShadowColorKey,
                            where
                        )
                        Color.parseColor(colorStr)
                    } ?: Color.TRANSPARENT
                }
                setShadowLayer(
                    textShadowRadiusFloat,
                    textShadowXFloat,
                    textShadowYFloat,
                    textShadowColor
                )
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
                        EditComponent.Font.entries.firstOrNull {
                            it.key == textFontStr
                        }
                    }
                } ?: EditComponent.Font.SANS_SERIF
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
        val where = "EditConstraintFrameMaker.setCaptionByDynamic"
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
                withContext(Dispatchers.Main) {
                    textPropertyMap?.get(
                        textBkColorKey,
                    )?.let {
                            colorStr ->
                        val parsedColorStr = ColorTool.parseColorStr(
                            context,
                            colorStr,
                            textBkColorKey,
                            where,
                        )
                        Color.parseColor(parsedColorStr)
                    }?.let {
                        background =
                            ColorDrawable(it)
                    }
                }
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
                    strokeWidthSrc = it
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
                withContext(Dispatchers.Main) {
                    textPropertyMap?.get(
                        letterSpacingKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }?.let {
                        letterSpacing = it
                    }
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
                val textShadowXFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowXKey,
                    )?.let {
                        try {
                            ScreenSizeCalculator.toDp(
                                context,
                                it.toFloat(),
                            ).toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: shadowDx
                }
                val textShadowYFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowYKey,
                    )?.let {
                        try {
                            ScreenSizeCalculator.toDp(
                                context,
                                it.toFloat(),
                            ).toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: shadowDy
                }
                val textShadowRadiusFloat = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowRadiusKey,
                    )?.let {
                        try {
                            ScreenSizeCalculator.toDp(
                                context,
                                it.toFloat(),
                            ).toFloat()
                        } catch(e: Exception){
                            null
                        }
                    } ?: shadowRadius
                }
                val textShadowColor = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textShadowColorKey,
                    )?.let {
                        val colorStr = ColorTool.parseColorStr(
                            context,
                            it,
                            textShadowColorKey,
                            where
                        )
                        Color.parseColor(colorStr)
                    } ?: shadowColor
                }
                setShadowLayer(
                    textShadowRadiusFloat,
                    textShadowXFloat,
                    textShadowYFloat,
                    textShadowColor
                )
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
                        EditComponent.Font.entries.firstOrNull {
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
}