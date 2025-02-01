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
                withContext(Dispatchers.Main) {
                    setImageView(
                        imageView,
                        imageMap,
//                        imageMap,
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
//                val textPropertyMap = withContext(Dispatchers.IO) {
//                    PairListTool.getPair(
//                        frameKeyPairList,
//                        textKey
////                        textPropertyKey,
//                    )?.let {
//                        EditComponent.Template.makeKeyMap(
//                            it.second,
//                        )
//                    }
//                }
            tagToTextViewList.forEach {
                (tagName, textView) ->
                val textMap = textTagToMap.get(tagName)
                    ?: return@forEach
//                val textMap = withContext(Dispatchers.IO) {
//                    PairListTool.getPair(
//                        frameKeyPairList,
//                        textKey,
//                    )?.let {
//                        EditComponent.Template.TextManager.createTextMap(
//                            it.second,
//                            totalSettingValMap?.get(
//                                overrideTag
//                            )
//                        )
//                    }
//                }
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
//        overrideTag: String?,
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
                else -> ConstraintView.makeConstraintParam(
                    tagIdMap,
                    frameKeyPairList,
                    overrideWidth,
                    height,
                )
            }
        }
        withContext(Dispatchers.IO) {
            LayoutSetterTool.setMargin(
                param,
                frameKeyPairList,
                density,
            )
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
//            tag = overrideTag
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
                    elevationKey,
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
                    elevationKey,
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
//        imageMap: Map<String, String>?,
        enableImageViewClick: Boolean,
        outValue: TypedValue?,
        where: String,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
    ) {
        if(
            imageMap.isNullOrEmpty()
//            && imageMap.isNullOrEmpty()
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
//                "imageMap: ${imageMap}"
//            ).joinToString("\n") + "\n\n==========\n\n"
//        )

//        when(
//            imagePathList.isNullOrEmpty()
//        ) {
//            true -> {
////                FileSystems.updateFile(
////                    File(UsePath.cmdclickDefaultAppDirPath, "lBk_image.txt").absolutePath,
////                    listOf(
////                        "bk: ${bk}",
////                        "imageMap: ${imageMap}",
////                        "imageMap: ${imageMap}"
////                    ).joinToString("\n") + "\n\n==========\n\n"
////                )
//                imageView.setImageDrawable(null)
//            }
//            else -> {
        imageView.apply {
            val visibilityValue = withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageVisibleKey,
                ).let { visibleStr ->
                    EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
            }
            visibility = visibilityValue
            CoroutineScope(Dispatchers.Main).launch {
                if(
                    imagePathList.isNullOrEmpty()
                ) return@launch
//                val matrixStormConfigMap = withContext(Dispatchers.IO){
//                    EditComponent.Template.ImageManager.MatrixStormManager.makeConfigMap(
//                        imageMap,
//                    )
//                }
//                val autoRndIconsConfigMap = withContext(Dispatchers.IO){
//                    EditComponent.Template.ImageManager.AutoRndIconsManager.makeConfigMap(
//                        imageMap,
//                    )
//                }
//                val autoRndStringsConfigMap = withContext(Dispatchers.IO){
//                    EditComponent.Template.ImageManager.AutoRndStringsManager.makeConfigMap(
//                        imageMap,
//                    )
//                }
                when (
                   imagePathList.size == 1
                ) {
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
//                            matrixStormConfigMap,
//                            autoRndIconsConfigMap,
//                            autoRndStringsConfigMap,
//                            where,
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
                            EditComponent.Template.ImageManager.BlurManager.getBlueRadiusToSampling(
                                imageMap
                            )
                        }
                        execSetSingleImage(
                            imageView,
                            imagePathList.firstOrNull(),
                            requestBuilderSrc,
                            fadeInMilli,
//                            matrixStormConfigMap,
//                            autoRndIconsConfigMap,
//                            autoRndStringsConfigMap,
                            blurRadiusToSampling,
//                            where,
                        )
                    }
                }
            }
            val overrideGravity = withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageGravityKey,
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }
            }?: Gravity.CENTER
            foregroundGravity = overrideGravity
            val imageColor = withContext(Dispatchers.IO) {
                imageMap?.get(
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
                imageMap?.get(
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
            isClickable = enableImageViewClick
            when(enableImageViewClick) {
                true -> outValue?.let {
                    setBackgroundResource(it.resourceId)
                }
                else -> {
                    setBackgroundResource(0)
                    background = imageBkColor?.let {
                        ColorDrawable(imageBkColor)
                    }
                }
            }
            val imageAlpha = withContext(Dispatchers.IO) {
                imageMap?.get(
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
                imageMap?.get(
                    imageScaleKey,
                ).let {
                        scale ->
                    EditComponent.Template.ImageManager.ImageScale.entries.firstOrNull {
                        it.str == scale
                    } ?: EditComponent.Template.ImageManager.ImageScale.FIT_CENTER
                }
            }
            scaleType = imageScale.scale
            val rotateFloat = withContext(Dispatchers.IO) {
                imageMap?.get(
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
                imageMap?.get(
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
                imageMap?.get(
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
                    imageMap?.get(
                        imagePaddingTopKey,
                    ),
                    imageMap?.get(
                        imagePaddingBottomKey,
                    ),
                    imageMap?.get(
                        imagePaddingStartKey,
                    ),
                    imageMap?.get(
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
//            }
//        }
        imageView.layoutParams = imageView.layoutParams.apply {
            val curLayoutParams = this as FrameLayout.LayoutParams
            curLayoutParams.apply setParam@ {
                val overrideWidth = withContext(Dispatchers.IO) {
                    imageMap?.get(
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
                    imageMap?.get(
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
                    imageMap?.get(
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
                        imageMap?.get(
                            imageMarginTopKey,
                        ),
                        imageMap?.get(
                            imageMarginBottomKey,
                        ),
                        imageMap?.get(
                            imageMarginStartKey,
                        ),
                        imageMap?.get(
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
//        imageMap: Map<String, String>?,
        density: Float,
    ) {
        val context = imageView.context
        val where = "EditConstraintFrameMaker.setImageViewForDynamic"
        imageView.layoutParams = imageView.layoutParams.apply {
            val curLayoutParams = this as FrameLayout.LayoutParams
            curLayoutParams.apply setParam@ {
                val overrideWidth = withContext(Dispatchers.IO) {
                    imageMap?.get(
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
                    imageMap?.get(
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
                    imageMap?.get(
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
//        val imagePathList = withContext(Dispatchers.IO) {
//            imageMap?.get(
//                imagePathsKey,
//            )?.split(valueSeparator)
//        }
        imageView.apply {
            withContext(Dispatchers.Main) {
                imageMap?.get(
                    imageVisibleKey,
                )?.let { visibleStr ->
                    EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
            }?.let {
                visibility = it
            }
            withContext(Dispatchers.IO) {
                imageMap?.get(
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
                imageMap?.get(
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
                imageTintList = ColorStateList.valueOf(it)
            }
            withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageBkColorKey,
                )?.let { colorStr ->
                    val parsedColorStr = ColorTool.parseColorStr(
                        context,
                        colorStr,
                        imageBkColorKey,
                        where,
                    )
                    Color.parseColor(parsedColorStr)
                }
            }?.let {
                background = ColorDrawable(it)
            }


            withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageAlphaKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
            }?.let {
                alpha = it
            }
            withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageScaleKey,
                ).let {
                        scale ->
                    EditComponent.Template.ImageManager.ImageScale.entries.firstOrNull {
                        it.str == scale
                    }
                }
            }?.let {
                scaleType = it.scale
            }

            withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageRotateKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
            }?.let {
                rotation = it
            }
           withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageScaleXKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
            }?.let {
               scaleX = it
           }
            withContext(Dispatchers.IO) {
                imageMap?.get(
                    imageScaleYKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch(e: Exception){
                        null
                    }
                }
            }?.let {
                scaleY = it
            }
        }
        val imagePathList = withContext(Dispatchers.IO) {
            imageMap?.get(
                imagePathsKey,
            )?.split(valueSeparator)
        }
        if(
            imagePathList.isNullOrEmpty()
            ) return
        CoroutineScope(Dispatchers.Main).launch {
//            val matrixStormConfigMap = withContext(Dispatchers.IO){
//                EditComponent.Template.ImageManager.MatrixStormManager.makeConfigMap(
//                    imageMap,
//                )
//            }
//            val autoRndIconsConfigMap = withContext(Dispatchers.IO){
//                EditComponent.Template.ImageManager.AutoRndIconsManager.makeConfigMap(
//                    imageMap,
//                )
//            }
//            val autoRndStringsConfigMap = withContext(Dispatchers.IO){
//                EditComponent.Template.ImageManager.AutoRndStringsManager.makeConfigMap(
//                    imageMap,
//                )
//            }
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
//                        matrixStormConfigMap,
//                        autoRndIconsConfigMap,
//                        autoRndStringsConfigMap,
//                        where,
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
                        EditComponent.Template.ImageManager.BlurManager.getBlueRadiusToSampling(
                            imageMap
                        )
                    }
                    execSetSingleImage(
                        imageView,
                        imagePathList.firstOrNull(),
                        null,
                        fadeInMilli,
//                        matrixStormConfigMap,
//                        autoRndIconsConfigMap,
//                        autoRndStringsConfigMap,
                        blurRadiusToSampling,
//                        where,
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
//        matrixStormConfigMap: Map<String, String>,
//        autoRndIconsConfigMap: Map<String, String>,
//        autoRndStringsConfigMap: Map<String, String>,
        blurRadiusAndSamplingPair: Pair<Int, Int>?,
//        where: String,
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
            ?: ImageCreator.byWallMacro(
            imagePathSrc
        ) ?: ImageCreator.byIconMacro(
            imageViewContext,
            imagePathToIconType
        )
//         ?: ImageCreator.byAutoCreateImage(
//                imageViewContext,
//                imagePathSrc,
//                matrixStormConfigMap,
//                autoRndIconsConfigMap,
//                autoRndStringsConfigMap,
//                where,
//            )
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
        suspend fun byWallMacro(
            imageMacro: String,
        ): Bitmap? {
            val cmdClickBkImageFilePath = BkWallPath.get(
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

        private object BkWallPath {

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
                    EditComponent.IconType.IMG -> {
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lstringspf.txt").absolutePath,
//                listOf(
//                    "string: $string",
//                    "pieceWidthFloat: $pieceWidth",
//                    "pieceHeightFloat: $pieceHeight",
//                    null,
//                    "fontSizeFloat: $fontSize",
//                    "fontColor: $color",
//                    "strokeColor: $strokeColor",
//                    "strokeWidthFloat: $strokeWidthInt",
//                    null,
//                    "letterSpacingFloat: $letterSpacingFloat",
//                    "fontType: $fontType",
//                    "fontStyle: $fontStyle",
//                    "baseWidth: $baseWidth",
//                    "baseHeight: $baseHeight",
//                    "bkColorStr: $bkColorStr",
//                    "times: $times",
//                    "layout: ${layout}",
//                ).joinToString("\n"),
//            )
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
//        matrixStormConfigMap: Map<String, String>,
//        autoRndIconsConfigMap: Map<String, String>,
//        autoRndStringsConfigMap: Map<String, String>,
//        where: String,
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
                        ?: ImageCreator.byWallMacro(
                            imagePathSrc
                        ) ?: ImageCreator.byIconMacro(
                            imageViewContext,
                            imagePathToIconType
                        )
//                    ?: ImageCreator.byAutoCreateImage(
//                            imageViewContext,
//                            imagePathSrc,
//                            matrixStormConfigMap,
//                            autoRndIconsConfigMap,
//                            autoRndStringsConfigMap,
//                            where,
//                        )
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
                textMap?.get(
                    textVisibleKey,
                ).let {
                        visibleStr ->
                    visibility = EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
                val lp = layoutParams as FrameLayout.LayoutParams
                lp.apply {
                    val overrideLayoutGravity = textMap?.get(
                        textLayoutGravityKey,
                    )?.let {
                            gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    } ?: Gravity.CENTER
                    gravity = overrideLayoutGravity
                    val overrideWidth = withContext(Dispatchers.IO) {
                        textMap?.get(
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
                        textMap?.get(
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
                        textMap?.get(
                            textMarginTopKey,
                        ),
                        textMap?.get(
                            textMarginBottomKey,
                        ),
                        textMap?.get(
                            textMarginStartKey,
                        ),
                        textMap?.get(
                            textMarginEndKey,
                        ),
                        density,
                    )
                    topMargin = marginData.marginTop ?: 0
                    bottomMargin = marginData.marginBottom ?: 0
                    marginStart = marginData.marginStart ?: 0
                    marginEnd = marginData.marginEnd ?: 0
                }
                val overrideGravity = textMap?.get(
                    textGravityKey,
                )?.let {
                        gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                } ?: Gravity.CENTER
                gravity = overrideGravity
                val paddingData = EditComponent.Template.PaddingData(
                    textMap?.get(
                        textPaddingTopKey,
                    ),
                    textMap?.get(
                        textPaddingBottomKey,
                    ),
                    textMap?.get(
                        textPaddingStartKey,
                    ),
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
                        strokeColorKey,
                    )
                }
                CmdClickColor.entries.firstOrNull {
                    it.str == strokeColorStr
                }.let {
                    setStrokeColor(it?.id ?: R.color.white)
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
                strokeWidthSrc = strokeWidth ?: 2
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
                } ?: let {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size_16))
                }
                val letterSpacingFloat = withContext(Dispatchers.IO) {
                    textMap?.get(
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
                alpha = textAlpha ?: 1f
                val textShadowXFloat = withContext(Dispatchers.IO) {
                    textMap?.get(
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
                    textMap?.get(
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
                    textMap?.get(
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
                    } ?: Color.TRANSPARENT
                }
                setShadowLayer(
                    textShadowRadiusFloat,
                    textShadowXFloat,
                    textShadowYFloat,
                    textShadowColor
                )
                val overrideTextStyle = withContext(Dispatchers.IO) {
                    textMap?.get(
                        textStyleKey,
                    )?.let {
                            textStyleStr ->
                        EditComponent.Template.TextManager.TextStyle.entries.firstOrNull {
                            it.key == textStyleStr
                        }
                    }
                } ?: EditComponent.Template.TextManager.TextStyle.NORMAL
                val overrideFont = withContext(Dispatchers.IO) {
                    textMap?.get(
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
//        textPropertyMap: Map<String, String>?,
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