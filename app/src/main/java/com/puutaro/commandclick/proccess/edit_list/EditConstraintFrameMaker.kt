package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object EditConstraintFrameMaker {

    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val paddingKey = EditComponent.Template.EditComponentKey.PADDING.key
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
        whereForErr: String,
        clickViewTagList: List<String>?,
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
//            val enableImageViewClick = withContext(Dispatchers.IO) {
//                if(
//                    clickViewTagList.contains()
////                    !enableClick
////                    || clickViewStrList.isNullOrEmpty()
//                ) return@withContext false
//                EditComponent.Template
//                    .ClickViewManager
//                    .containClickImageView(clickViewStrList)
//            }
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
                        clickViewTagList?.contains(imageView.tag),
//                        enableImageViewClick,
                        outValue,
                        whereForErr,
                        requestBuilderSrc,
                        density,
                    )
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
//            val enableTextViewClick = withContext(Dispatchers.IO) {
//                if(
//                    !enableClick
//                    || clickViewStrList.isNullOrEmpty()
//                ) return@withContext false
//                EditComponent.Template
//                    .ClickViewManager
//                    .containClickTextView(clickViewStrList)
//            }
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
                        Gravity.CENTER,
                        Gravity.CENTER,
                        1,
                        R.color.fill_gray,
                        R.color.white,
                        2,
                        context.resources.getDimension(R.dimen.text_size_16),
                        0f,
                        EditComponent.Template.TextManager.TextStyle.NORMAL,
                        EditComponent.Font.SANS_SERIF,
                        clickViewTagList?.contains(textView.tag),
//                        enableTextViewClick,
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
                            density,
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
                density,
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
//            val paddingData = withContext(Dispatchers.IO) {
//                val padding = PairListTool.getValue(
//                    frameKeyPairList,
//                    paddingKey
//                )
//                EditComponent.Template.PaddingData(
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        paddingTopKey
//                    ) ?: padding,
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        paddingBottomKey
//                    ) ?: padding,
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        paddingStartKey
//                    ) ?: padding,
//                    PairListTool.getValue(
//                        frameKeyPairList,
//                        paddingEndKey
//                    )?: padding,
//                    density,
//                )
//            }
            withContext(Dispatchers.IO) {
                LayoutSetterTool.setPadding(
                    this@apply,
                    frameKeyPairList?.toMap(),
                    density,
                )
//                setPadding(
//                    paddingData.paddingStart ?: paddingStart,
//                    paddingData.paddingTop ?: paddingStart,
//                    paddingData.paddingEnd ?: paddingEnd,
//                    paddingData.paddingBottom ?: paddingBottom,
//                )
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

    suspend fun setImageView(
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
        ImageViewTool.setVisibility(
            imageView,
            imageMap,
        )
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
//        if(imageView.tag == "speechModeFrame") {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lframe.txt").absolutePath,
//                listOf(
//                    "imageMap,: ${imageMap}",
//                    "isVisible,: ${imageView.isVisible}",
//                    "visibility: ${imageView.visibility}",
//                    "width: ${imageView.layoutParams.width}",
//                    "height: ${imageView.layoutParams.height}",
//                    "imageTintList: ${imageView.imageTintList}",
//                    "matchParent: ${FrameLayout.LayoutParams.MATCH_PARENT}"
//                ).joinToString("\n\n")
//            )
//        }
    }

    suspend fun setImageViewForDynamic(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        density: Float,
    ) {
        val context = imageView.context
        ImageViewTool.setVisibility(
            imageView,
            imageMap,
        )
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
        CoroutineScope(Dispatchers.IO).launch {
            FrameLayoutTool.setParam(
                imageView.layoutParams as FrameLayout.LayoutParams,
                imageMap,
                density,
                defaultWidth,
                defaultHeight,
                defaultLayoutGravity,
            ).let { param ->
                LayoutSetterTool.setMargin(
                    param,
                    imageMap,
                    density
                )
                param
            }.let {
                withContext(Dispatchers.Main) {
                    imageView.layoutParams = it
                }
            }
        }
        ImageViewTool.set(
            imageView,
            imageMap,
            defaultGravity,
            defaultScale,
            enableImageViewClick,
            outValue,
            requestBuilderSrc,
            density,
            where,
        )
    }

    private suspend fun setTextView(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
        defaultLayoutGravity: Int?,
        defaultGravity: Int?,
        defaultMaxLen: Int?,
        defaultTextColorResId: Int?,
        defaultStrokeColorResId: Int?,
        defaultStrokeWidth: Int?,
        defaultTextSize: Float?,
        defaultLetterSpacing: Float?,
        defaultTextStyle: EditComponent.Template.TextManager.TextStyle?,
        defaultFont: EditComponent.Font?,
        enableTextViewClick: Boolean?,
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
            ){
            withContext(Dispatchers.Main){
                captionTextView.isVisible = false
            }
            return
        }
        val settingValue = withContext(Dispatchers.IO) {
            textMap.get(
                EditComponent.Template.TextManager.TextKey.SETTING_VALUE.key
            )
        }
        val overrideText = withContext(Dispatchers.IO) {
            EditComponent.Template.TextManager.makeText(
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                textMap,
                settingValue
            )
        }
        withContext(Dispatchers.IO) {
            TextViewTool.setVisibility(
                captionTextView,
                textMap,
            )
        }
        execSetTextView(
            captionTextView,
            textMap,
//            settingValue,
            overrideText,
            defaultLayoutGravity,
            defaultGravity,
            defaultMaxLen,
            defaultTextColorResId,
            defaultStrokeColorResId,
            defaultStrokeWidth,
            defaultTextSize,
            defaultLetterSpacing,
            defaultTextStyle,
            defaultFont,
            enableTextViewClick,
            outValue,
            where,
            density,
        )
    }

    suspend fun setTextViewByDynamic(
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
//        settingValue: String?,
        overrideText: String?,
    ) {
        val where = "EditConstraintFrameMaker.setCaptionByDynamic"
//        val enableTextSelect = withContext(Dispatchers.IO){
//            textMap?.get(
//                disableTextSelectKey
//            ) != switchOn
//        }
        val textViewContext = captionTextView.context
        TextViewTool.setVisibility(
            captionTextView,
            textMap,
        )
        val outValue = withContext(Dispatchers.IO) {
            val outValueSrc = TypedValue()
            textViewContext.theme?.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValueSrc,
                true
            )
            outValueSrc
        }
        val density = withContext(Dispatchers.Main) {
            ScreenSizeCalculator.getDensity(
                textViewContext
            )
        }
        execSetTextView(
            captionTextView,
            textMap,
//            settingValue,
            overrideText,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            outValue,
            where,
            density,
        )
    }

    private suspend fun execSetTextView(
        captionTextView: OutlineTextView,
        textMap: Map<String, String>?,
//        settingValue: String?,
        overrideText: String?,
        defaultLayoutGravity: Int?,
        defaultGravity: Int?,
        defaultMaxLen: Int?,
        defaultTextColorResId: Int?,
        defaultStrokeColorResId: Int?,
        defaultStrokeWidth: Int?,
        defaultTextSize: Float?,
        defaultLetterSpacing: Float?,
        defaultTextStyle: EditComponent.Template.TextManager.TextStyle?,
        defaultFont: EditComponent.Font?,
        enableTextViewClick: Boolean?,
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
            && overrideText.isNullOrEmpty()
        ){
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            FrameLayoutTool.setParam(
                captionTextView.layoutParams as FrameLayout.LayoutParams,
                textMap,
                density,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                defaultLayoutGravity,
            ).let { param ->
                LayoutSetterTool.setMargin(
                    param,
                    textMap,
                    density
                )
                param
            }.let {
                withContext(Dispatchers.Main) {
                    captionTextView.layoutParams = it
                }
            }
        }
        TextViewTool.set(
            captionTextView,
            textMap,
//            settingValue,
            overrideText,
            defaultGravity,
            defaultMaxLen,
            defaultTextColorResId,
            defaultStrokeColorResId,
            defaultStrokeWidth,
            defaultTextSize,
            defaultLetterSpacing,
            defaultTextStyle,
            defaultFont,
            enableTextViewClick,
            outValue,
            where,
            density,
        )
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