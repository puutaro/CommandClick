package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
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
    private val paddingTopKey = EditComponent.Template.EditComponentKey.PADDING_TOP.key
    private val paddingBottomKey = EditComponent.Template.EditComponentKey.PADDING_BOTTOM.key
    private val paddingStartKey = EditComponent.Template.EditComponentKey.PADDING_START.key
    private val paddingEndKey = EditComponent.Template.EditComponentKey.PADDING_END.key
    private val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
    private val visibleKey = EditComponent.Template.EditComponentKey.VISIBLE.key
    private val bkColorKey = EditComponent.Template.EditComponentKey.BK_COLOR.key
    private val layoutGravityKey = EditComponent.Template.EditComponentKey.LAYOUT_GRAVITY.key
    private val gravityKey = EditComponent.Template.EditComponentKey.GRAVITI.key
    private val elevationKey = EditComponent.Template.EditComponentKey.ELEVATION.key
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
//    private val imagePropertyKey = EditComponent.Template.EditComponentKey.IMAGE_PROPERTY.key
    private val textKey = EditComponent.Template.EditComponentKey.TEXT.key
//    private val textPropertyKey = EditComponent.Template.EditComponentKey.TEXT_PROPERTY.key
    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
    private val widthKey = EditComponent.Template.EditComponentKey.WIDTH.key


    private val imagePathsKey = EditComponent.Template.ImageManager.ImageKey.PATHS.key
    private val imageDelayKey = EditComponent.Template.ImageManager.ImageKey.DELAY.key
    private val imageFadeInMilliKey = EditComponent.Template.ImageManager.ImageKey.FADE_IN_MILLI.key
//    private val imageTagKey = EditComponent.Template.ImageManager.PropertyKey.TAG.key
    private val imageColorKey = EditComponent.Template.ImageManager.PropertyKey.COLOR.key
    private val imageBkColorKey = EditComponent.Template.ImageManager.PropertyKey.BK_COLOR.key
    private val imageBkTintColorKey = EditComponent.Template.ImageManager.PropertyKey.BK_COLOR.key
    private val imageAlphaKey = EditComponent.Template.ImageManager.PropertyKey.ALPHA.key
    private val imageScaleKey = EditComponent.Template.ImageManager.PropertyKey.SCALE.key
    private val imageLayoutGravity = EditComponent.Template.ImageManager.PropertyKey.LAYOUT_GRAVITY.key
    private val imageGravityKey = EditComponent.Template.ImageManager.PropertyKey.GRAVITI.key
    private val imageWidthKey = EditComponent.Template.ImageManager.PropertyKey.WIDTH.key
    private val imageHeightKey = EditComponent.Template.ImageManager.PropertyKey.HEIGHT.key
    private val imageMarginTopKey = EditComponent.Template.ImageManager.PropertyKey.MARGIN_TOP.key
    private val imageMarginStartKey = EditComponent.Template.ImageManager.PropertyKey.MARGIN_START.key
    private val imageMarginEndKey = EditComponent.Template.ImageManager.PropertyKey.MARGIN_END.key
    private val imageMarginBottomKey = EditComponent.Template.ImageManager.PropertyKey.MARGIN_BOTTOM.key
    private val imagePaddingTopKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_TOP.key
    private val imagePaddingStartKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_START.key
    private val imagePaddingEndKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_END.key
    private val imagePaddingBottomKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_BOTTOM.key
    private val imageVisibleKey = EditComponent.Template.ImageManager.PropertyKey.VISIBLE.key
    private val imageRotateKey = EditComponent.Template.ImageManager.PropertyKey.ROTATE.key
    private val imageScaleXKey = EditComponent.Template.ImageManager.PropertyKey.SCALE_X.key
    private val imageScaleYKey = EditComponent.Template.ImageManager.PropertyKey.SCALE_Y.key

    private val onUpdateKey = EditComponent.Template.TextManager.TextKey.ON_UPDATE.key
    private val textSizeKey = EditComponent.Template.TextManager.PropertyKey.SIZE.key
    private val textBkColorKey = EditComponent.Template.TextManager.PropertyKey.BK_COLOR.key
    private val textStyleKey = EditComponent.Template.TextManager.PropertyKey.STYLE.key
    private val textFontKey = EditComponent.Template.TextManager.PropertyKey.FONT.key
//    private val textTagKey = EditComponent.Template.TextManager.Property.TAG.key
    private val textLayoutGravityKey =
        EditComponent.Template.TextManager.PropertyKey.LAYOUT_GRAVITY.key
    private val textGravityKey = EditComponent.Template.TextManager.PropertyKey.GRAVITI.key
    private val textWidthKey = EditComponent.Template.TextManager.PropertyKey.WIDTH.key
    private val textHeightKey = EditComponent.Template.TextManager.PropertyKey.HEIGHT.key
    private val textColorKey = EditComponent.Template.TextManager.PropertyKey.COLOR.key
    private val textVisibleKey = EditComponent.Template.TextManager.PropertyKey.VISIBLE.key
    private val strokeColorKey = EditComponent.Template.TextManager.PropertyKey.STROKE_COLOR.key
    private val strokeWidthKey = EditComponent.Template.TextManager.PropertyKey.STROKE_WIDTH.key
    private val textAlphaKey = EditComponent.Template.TextManager.PropertyKey.ALPHA.key
    private val textMaxLinesKey = EditComponent.Template.TextManager.PropertyKey.MAX_LINES.key
    private val textMarginTopKey = EditComponent.Template.TextManager.PropertyKey.MARGIN_TOP.key
    private val textMarginStartKey = EditComponent.Template.TextManager.PropertyKey.MARGIN_START.key
    private val textMarginEndKey = EditComponent.Template.TextManager.PropertyKey.MARGIN_END.key
    private val textMarginBottomKey = EditComponent.Template.TextManager.PropertyKey.MARGIN_BOTTOM.key
    private val textPaddingTopKey = EditComponent.Template.TextManager.PropertyKey.PADDING_TOP.key
    private val textPaddingStartKey = EditComponent.Template.TextManager.PropertyKey.PADDING_START.key
    private val textPaddingEndKey = EditComponent.Template.TextManager.PropertyKey.PADDING_END.key
    private val textPaddingBottomKey = EditComponent.Template.TextManager.PropertyKey.PADDING_BOTTOM.key
    private val textShadowRadiusKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_RADIUS.key
    private val textShadowColorKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_COLOR.key
    private val textShadowXKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_X.key
    private val textShadowYKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_Y.key
    private val letterSpacingKey = EditComponent.Template.TextManager.PropertyKey.LETTER_SPACING.key
//    private val disableTextSelectKey = EditComponent.Template.TextManager.Property.DISABLE_TEXT_SELECT.key

    private val switchOn = EditComponent.Template.switchOn
    private val switchOff = EditComponent.Template.switchOff

    suspend fun make(
        context: Context?,
        idInt: Int?,
        tagIdMap: Map<String, Int>?,
        frameLayoutSrc: FrameLayout?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        frameKeyPairList: List<Pair<String, String>>?,
        textTagToMap: Map<String, Map<String, String>?>,
        tagToTextViewList: List<Pair<String, OutlineTextView>>,
        imageTagToMap: Map<String, Map<String, String>>,
        tagToImageViewListForContents: List<Pair<String, AppCompatImageView>>,
        width: Int,
//        overrideTag: String?,
//        totalSettingValMap: Map<String, String>?,
        whereForErr: String,
        enableClick: Boolean,
        clickViewStrList: List<String>?,
        outValue: TypedValue?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ): FrameLayout? {
        if(
            context == null
        ) return null
        val frameLayout = makeFrameLayout(
            context,
            idInt,
            tagIdMap,
            frameLayoutSrc,
            frameKeyPairList,
            whereForErr,
            width,
//            overrideTag,
            density,
        ) ?: return null


        CoroutineScope(Dispatchers.Main).launch {
            val enableImageViewClick = withContext(Dispatchers.IO) {
                if(
                    !enableClick
                    || clickViewStrList.isNullOrEmpty()
                ) return@withContext false
                EditComponent.Template
                    .ClickViewManager
                    .containClickImageView(clickViewStrList)
            }
//            val imageButtonView = withContext(Dispatchers.Main) {
//                frameLayout.children.firstOrNull {
//                    it is AppCompatImageView
//                } as? AppCompatImageView
//            }?: return@launch

            tagToImageViewListForContents.forEach {
                    (tagName, imageView) ->
                val imageMap = imageTagToMap.get(tagName)
                    ?: return@forEach
//                if(frameLayout.tag == "initButtonBottomShaddow") {
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lImage.txt").absolutePath,
//                        listOf(
//                            "frameTag: ${frameLayout.tag}",
//                            "tag: ${imageView.tag}",
//                            "imageMap: ${imageMap}",
//                        ).joinToString("\n")
//                    )
//                }
                withContext(Dispatchers.Main) {
                    setImageView(
                        imageView,
                        imageMap,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER,
                        Gravity.CENTER,
                        EditComponent.Template.ImageManager.ImageScale.FIT_CENTER.scale,
                        enableImageViewClick,
                        outValue,
                        whereForErr,
                        requestBuilderSrc,
                        density,
                    )
                }
            }
//                val imageMap = withContext(Dispatchers.IO) {
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        imageKey,
//                    )?.let {
//                        EditComponent.Template.makeKeyMap(
//                            it,
//                        )
//                    }
//                }
//                val imageMap = withContext(Dispatchers.IO) {
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        imagePropertyKey,
//                    )?.let {
//                        EditComponent.Template.makeKeyMap(
//                            it,
//                        )
//                    }
//                }

//                }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val enableTextViewClick = withContext(Dispatchers.IO) {
                if(
                    !enableClick
                    || clickViewStrList.isNullOrEmpty()
                ) return@withContext false
                EditComponent.Template
                    .ClickViewManager
                    .containClickTextView(clickViewStrList)
            }
//            val captionTextView = withContext(Dispatchers.Main) {
//                frameLayout.children.firstOrNull {
//                    it is OutlineTextView
//                } as? OutlineTextView
//            }?: return@launch
//            buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_for_edit_caption)
//                ?.let { captionTextView ->

//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "sGet_frameMaker.txt").absolutePath,
//                listOf(
//                    "totalSettingValMap: ${totalSettingValMap}",
//                    "textMap: ${textMap}",
//                ).joinToString("\n")
//            )
            tagToTextViewList.forEach {
                (tagName, textView) ->
                val textMap = textTagToMap.get(tagName)
                    ?: return@forEach
                withContext(Dispatchers.Main) {
                    setTextView(
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        textView,
                        textMap,
//                        textPropertyMap,
                        enableTextViewClick,
                        outValue,
                        whereForErr,
                        density,
                    )
                }
            }
        }
        return frameLayout
    }

    private suspend fun makeFrameLayout(
        context: Context?,
        idInt: Int?,
        tagIdMap: Map<String, Int>?,
        buttonLayout: FrameLayout?,
        frameKeyPairList: List<Pair<String, String>>?,
        whereForErr: String,
        width: Int,
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
                ) ?: 0
            }
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
                ) ?: width
            }
        }

        val param = withContext(Dispatchers.IO) {
            val isFrameLayoutParam = tagIdMap.isNullOrEmpty()
            when (isFrameLayoutParam) {
                true -> FrameLayout.LayoutParams(
                    overrideWidth,
                    height,
                ).apply {
                    val overrideLayoutGravity = PairListTool.getValue(
                        frameKeyPairList,
                        layoutGravityKey
                    )?.let { gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    } ?: Gravity.CENTER
                    gravity = overrideLayoutGravity
                }
                else -> {
                    val constraintParam = ConstraintLayout.LayoutParams(
                        overrideWidth,
                        height,
                    )
//                    if(buttonLayout?.tag == "initButtonBottomShaddow") {
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lFrame.txt").absolutePath,
//                            listOf(
//                                "frameTag: ${buttonLayout?.tag}",
//                                "frameKeyPairList: ${frameKeyPairList}",
//                            ).joinToString("\n")
//                        )
//                    }
                    withContext(Dispatchers.Main) {
                        ConstraintTool.setConstraintParam(
                            constraintParam,
                            tagIdMap,
                            frameKeyPairList,
                            null,
                            null,
                        )
                    }
                    constraintParam
                }
            }.let {
                param ->
                LayoutSetterTool.setMargin(
                    param,
                    frameKeyPairList?.toMap(),
                    density,
                )
                param
            }
        }
        buttonLayout?.apply {
            layoutParams = param
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "ledtConstarintFramel.txt").absolutePath,
////                listOf(
////                    "unsetInt: ${EditComponent.Template.ConstraintManager.ConstraintParameter.UNSET.int}",
////                    "PARENT_ID: ${EditComponent.Template.ConstraintManager.ConstraintParameter.PARENT_ID.int}",
////                    "id: ${id}",
////                    "tag: ${tag}",
////                    "this.topToTop: ${paramConst.topToTop}",
////                    "this.topToBottom: ${paramConst.topToBottom}",
////                    "this.startToEnd: ${paramConst.startToEnd}",
////                    "this.startToStart: ${paramConst.startToStart}",
////                    "this.endToEnd: ${paramConst.endToEnd}",
////                    "this.endToStart: ${paramConst.endToStart}",
////                    "this.bottomToTop: ${paramConst.bottomToTop}",
////                    "this.bottomToBottom: ${paramConst.bottomToBottom}",
////                    "this.horizontalBias: ${paramConst.horizontalBias}",
////                ).joinToString("\n")
////            )
        }
        setFrameLayout(
            buttonLayout,
            frameKeyPairList,
            density,
            whereForErr,
        )
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
           withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    visibleKey,
                )
            }?.let { visibleStr ->
                withContext(Dispatchers.Main) {
                    visibility = EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
           }
        }

        val param = withContext(Dispatchers.IO) {
            if(
                buttonFrameLayout == null
            ) return@withContext null
            ConstraintTool.setConstraintParam(
                buttonFrameLayout.layoutParams as ConstraintLayout.LayoutParams,
                null,
                frameKeyPairList,
                null,
                null,
            ).let {
                    param ->
                LayoutSetterTool.setMargin(
                    param,
                    frameKeyPairList?.toMap(),
                    density,
                )
                param
            }
        }
        buttonFrameLayout?.apply {
            withContext(Dispatchers.Main) {
                layoutParams = param
            }
        }
        setFrameLayout(
            buttonFrameLayout,
            frameKeyPairList,
            density,
            where,
        )
    }

    private suspend fun setFrameLayout(
        frameLayout: FrameLayout?,
        frameKeyPairList: List<Pair<String, String>>?,
        density: Float,
        where: String,
    ){
        frameLayout?.apply {
            withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    gravityKey
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }
            }?.let {
                withContext(Dispatchers.Main) {
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
            withContext(Dispatchers.IO) {
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
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    background = ColorDrawable(it)
                }
            }
            withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    elevationKey,
                )?.let {
                    try {
                        it.toFloat()
                    }catch (e: Exception){
                        null
                    }
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    elevation = it
                }
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
                withContext(Dispatchers.Main) {
                    alpha = it
                }
            }
        }

    }

    private suspend fun setImageView(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        defaultWidth: Int?,
        defaultHeight: Int?,
        defaultLayoutGravity: Int?,
        defaultGravity: Int?,
        defaultScale: ImageView.ScaleType?,
        enableImageViewClick: Boolean?,
        outValue: TypedValue?,
        where: String,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ) {
        if(
            imageMap.isNullOrEmpty()
        ){
            imageView.isVisible = false
            return
        }
        execSetImageView(
            imageView,
            imageMap,
            defaultWidth,
            defaultHeight,
            defaultLayoutGravity,
            defaultGravity,
            defaultScale,
            enableImageViewClick,
            outValue,
            where,
            requestBuilderSrc,
            density,
        )
    }

    suspend fun setImageViewForDynamic(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        density: Float,
    ) {
        val context = imageView.context
        val where = "EditConstraintFrameMaker.setImageViewForDynamic"
        val outValue = withContext(Dispatchers.IO) {
            val outValueSrc = TypedValue()
            context.theme?.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValueSrc,
                true
            )
            outValueSrc
        }
        val requestBuilder = Glide.with(context)
            .asDrawable()
            .sizeMultiplier(0.1f)
        execSetImageView(
            imageView,
            imageMap,
            null,
            null,
            null,
            null,
            null,
            null,
            outValue,
            where,
            requestBuilder,
            density
        )
    }

    private suspend fun execSetImageView(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        defaultWidth: Int?,
        defaultHeight: Int?,
        defaultLayoutGravity: Int?,
        defaultGravity: Int?,
        defaultScale: ImageView.ScaleType?,
        enableImageViewClick: Boolean?,
        outValue: TypedValue?,
        where: String,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ) {
        if(
            imageMap.isNullOrEmpty()
        ) return
        val context = imageView.context
        val imagePathList = withContext(Dispatchers.IO) {
            imageMap.get(
                imagePathsKey,
            )?.split(valueSeparator)
        }
        imageView.apply {
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageVisibleKey,
                )?.let { visibleStr ->
                    EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    visibility = it
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                if(
                    imagePathList.isNullOrEmpty()
                ) return@launch
                when (
                    imagePathList.size == 1
                ) {
                    false -> {
                        val delay = withContext(Dispatchers.IO) {
                            imageMap.get(
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
                            imageMap.get(
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
                            EditComponent.Template.ImageManager.BlurManager.getBlueRadiusToSampling(
                                imageMap
                            )
                        }
                        execSetSingleImage(
                            imageView,
                            imagePathList.firstOrNull(),
                            requestBuilderSrc,
                            fadeInMilli,
                            blurRadiusToSampling,
                        )
                    }
                }
            }
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageGravityKey,
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                } ?: defaultGravity
            }?.let {
                withContext(Dispatchers.Main) {
                    foregroundGravity = it
                }
            }
            withContext(Dispatchers.IO) {
                imageMap.get(
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
            }?.let {
                withContext(Dispatchers.Main) {
                    imageTintList = ColorStateList.valueOf(it)
                }
            }
            val imageBkColor = withContext(Dispatchers.IO) {
                imageMap.get(
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
            enableImageViewClick?.let {
                withContext(Dispatchers.Main) {
                    isClickable = it
                }
            }
            withContext(Dispatchers.Main) {
                when (enableImageViewClick) {
                    true -> outValue?.let {
                        setBackgroundResource(it.resourceId)
                    }

                    else -> {
                        imageBkColor?.let {
                            setBackgroundResource(0)
                            background = ColorDrawable(imageBkColor)
                        }
                    }
                }
            }
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageAlphaKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    alpha = it
                }
//                    imageAlpha ?: 1f
            }
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageScaleKey,
                ).let {
                        scale ->
                    EditComponent.Template.ImageManager.ImageScale.entries.firstOrNull {
                        it.str == scale
                    }?.scale ?: defaultScale
//                        ?: EditComponent.Template.ImageManager.ImageScale.FIT_CENTER
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    scaleType = it
                }
            }
//            scaleType = imageScale.scale
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageRotateKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
//                    ?: 0f
            }?.let {
                withContext(Dispatchers.Main) {
                    rotation = it
                }
            }
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageScaleXKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
//                    ?: 1f
            }?.let {
                withContext(Dispatchers.Main) {
                    scaleX = it
                }
            }
            withContext(Dispatchers.IO) {
                imageMap.get(
                    imageScaleYKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
//                    ?: scaleY
            }?.let {
                withContext(Dispatchers.Main) {
                    scaleY = it
                }
            }
            val paddingData = withContext(Dispatchers.IO) {
                EditComponent.Template.PaddingData(
                    imageMap.get(
                        imagePaddingTopKey,
                    ),
                    imageMap.get(
                        imagePaddingBottomKey,
                    ),
                    imageMap.get(
                        imagePaddingStartKey,
                    ),
                    imageMap.get(
                        imagePaddingEndKey,
                    ),
                    density,
                )
            }
            withContext(Dispatchers.Main) {
                setPadding(
                    paddingData.paddingStart ?: paddingStart,
                    paddingData.paddingTop ?: paddingTop,
                    paddingData.paddingEnd ?: paddingEnd,
                    paddingData.paddingBottom ?: paddingBottom,
                )
            }
        }
        imageView.layoutParams = FrameLayoutTool.setParam(
            imageView.layoutParams as FrameLayout.LayoutParams,
            imageMap,
            density,
            defaultWidth,
            defaultHeight,
            defaultLayoutGravity,
        ).let {
                param ->
            LayoutSetterTool.setMargin(
                param,
                imageMap,
                density
            )
            param
        }
    }

    private suspend fun execSetSingleImage(
        imageView: AppCompatImageView,
        imagePathSrc: String?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        fadeInMilli: Int?,
        blurRadiusAndSamplingPair: Pair<Int, Int>?,
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
            ?: ImageCreator.byIconMacro(
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
//        if(bitmap == null){
//            imageView.isVisible = false
//            return
//        }
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
                    } ?: EditComponent.IconType.IMG
                }
            }
            val assetsPath = withContext(Dispatchers.IO) {
                CmdClickIcons.entries.firstOrNull {
                    it.str == imagePath
                }?.assetsPath
            }?: return null
            return withContext(Dispatchers.IO) {
                when (iconType) {
                    EditComponent.IconType.IMG -> {
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
                        ?: ImageCreator.byIconMacro(
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


    private suspend fun setTextView(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
//        textPropertyMap: Map<String, String>?,
        enableTextViewClick: Boolean,
        outValue: TypedValue?,
        where: String,
        density: Float,
    ) {
//        val enableTextSelect = withContext(Dispatchers.IO){
//            textMap?.get(
//                disableTextSelectKey
//            ) != switchOn
//        }
        val textViewContext = captionTextView.context
        if(
            textMap.isNullOrEmpty()
//            && textPropertyMap.isNullOrEmpty()
            ){
            withContext(Dispatchers.Main){
                captionTextView.isVisible = false
            }
            return
        }
        withContext(Dispatchers.Main) {
            captionTextView.apply {
                textMap.get(
                    textVisibleKey,
                ).let {
                        visibleStr ->
                    visibility = EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
                val lp = layoutParams as FrameLayout.LayoutParams
                layoutParams = FrameLayoutTool.setParam(
                    lp,
                    textMap,
                    density,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ).let {
                        param ->
                    LayoutSetterTool.setMargin(
                        param,
                        textMap,
                        density
                    )
                    param
                }
                val overrideGravity = textMap.get(
                    textGravityKey,
                )?.let {
                        gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                } ?: Gravity.CENTER
                gravity = overrideGravity
                val paddingData = EditComponent.Template.PaddingData(
                    textMap.get(
                        textPaddingTopKey,
                    ),
                    textMap.get(
                        textPaddingBottomKey,
                    ),
                    textMap.get(
                        textPaddingStartKey,
                    ),
                    textMap.get(
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
//                    textMap?.get(
//                        textTagKey,
//                    )
//                }
//                tag = textTag
                val settingValue = textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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

                isClickable = enableTextViewClick
                when(enableTextViewClick) {
                    true -> outValue?.let {
                        setBackgroundResource(it.resourceId)
                    }
                    else -> {
                        setBackgroundResource(0)
                        background =
                            textBkColor?.let {
                                ColorDrawable(it)
                            }
                    }
                }
                val strokeColorStr = withContext(Dispatchers.IO) {
                    textMap.get(
                        strokeColorKey,
                    )
                }
                CmdClickColor.entries.firstOrNull {
                    it.str == strokeColorStr
                }.let {
                    setStrokeColor(it?.id ?: R.color.white)
                }
                val strokeWidth = withContext(Dispatchers.IO) {
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
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
                    textMap.get(
                        textStyleKey,
                    )?.let {
                            textStyleStr ->
                        EditComponent.Template.TextManager.TextStyle.entries.firstOrNull {
                            it.key == textStyleStr
                        }
                    }
                } ?: EditComponent.Template.TextManager.TextStyle.NORMAL
                val overrideFont = withContext(Dispatchers.IO) {
                    textMap.get(
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

    suspend fun setTextViewByDynamic(
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
        overrideText: String?,
    ) {
        val where = "EditConstraintFrameMaker.setCaptionByDynamic"
//        val enableTextSelect = withContext(Dispatchers.IO){
//            textMap?.get(
//                disableTextSelectKey
//            ) != switchOn
//        }
        val textViewContext = captionTextView.context
        withContext(Dispatchers.Main) {
            captionTextView.apply {
                val lp = layoutParams as FrameLayout.LayoutParams
                lp.apply {
                    val overrideLayoutGravity = textMap?.get(
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
                val overrideGravity = textMap?.get(
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
                val isUpdate = textMap?.get(
                    onUpdateKey
                ) != switchOff
                if(isUpdate) {
                    overrideText?.let {
                        text = it
                    }
                }
                val overrideMaxLines = withContext(Dispatchers.IO){
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
                        strokeColorKey,
                    )
                }
                CmdClickColor.entries.firstOrNull {
                    it.str == strokeColorStr
                }?.let {
                    setStrokeColor(it.id)
                }
                val strokeWidth = withContext(Dispatchers.IO) {
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
                        textStyleKey,
                    )?.let {
                            textStyleStr ->
                        EditComponent.Template.TextManager.TextStyle.entries.firstOrNull {
                            it.key == textStyleStr
                        }?.style
                    }
                } ?: typeface.style
                val overrideFont = withContext(Dispatchers.IO) {
                    textMap?.get(
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