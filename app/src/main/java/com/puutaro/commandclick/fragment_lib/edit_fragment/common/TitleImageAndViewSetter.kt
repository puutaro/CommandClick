package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditConstraintListView
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import jp.wasabeef.blurry.Blurry
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.GrayscaleTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object TitleImageAndViewSetter {

    private const val backstackCountSeparator = " "
    private const val switchOff = "OFF"
    private const val switchOn = "ON"
    private const val keySeparator = '|'
    private const val valueSeparator = '&'
    private const val errWhere = "editTitle"

    private fun parseColors(
        context: Context?,
        titleTextMap: Map<String, String>?,
        colorsKey: String
    ): List<String?>? {
        if(
            titleTextMap.isNullOrEmpty()
        ) return null
        return titleTextMap.get(
            colorsKey
        )?.split(valueSeparator)?.map {
                colorStr ->
            ColorTool.parseColorStr(
                context,
                colorStr,
                colorsKey,
                ErrType.FANNEL_TITLE.str,
            )
//            ColorTool.parseColorStr(
//                context,
//                colorStr
//            )
        }
    }

    private enum class EditBoxTitleKey(
        val key: String,
    ){
        TEXT("text"),
        IMAGE("image"),
    }

    enum class TitleType(
        val str: String
    ){
        LINEAR("linear"),
        FRAG("frag"),
    }

    enum class TitleTextSettingKey(
        val key: String
    ){
        //    SIZE("size"),
        HEIGHT("height"),
        TYPE("type"),
        BK_HEX_ALPHA("bkHexAlpha"),
        STROKE_WIDTH("strokeWidth"),
        STROKE_COLORS("strokeColors"),
        BACKSTACK_COLORS("backstackColors"),
        FORE_FILL_COLORS("foreFillColors"),
        BK_FILL_COLORS("bkFillColors"),
        ON_FORE_BK_DIFF_COLOR("onForeBkDiffColor"),
        VISIBLE("visible"),
        SHELL_PATH("shellPath"),
        SHELL_CON("shellCon"),
        ARGS("args"),
        SHADOW_RADIUS("shadowRadius"),
        SHADOW_COLOR("shadowColor"),
        SHADOW_X("shadowX"),
        SHADOW_Y("shadowY"),
    }


    private enum class ErrType(
        val str: String
    ) {
        FANNEL_TITLE("[FANNEL TITLE]")
    }

    suspend fun set(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListTitleFrame: FrameLayout?,
        editListLinearAlignTitleLayout: FrameLayout?,
        editListFragAlignTitleLayout: FrameLayout?,
        titleSettingMap: Map<String, String>?,
        requestBuilder: RequestBuilder<Drawable>?,
    ) {
        return
        val titleTextMap = withContext(Dispatchers.IO) {
            titleSettingMap?.get(
                EditBoxTitleKey.TEXT.key
            ).let {
                CmdClickMap.createMap(
                    it,
                    keySeparator
                )
            }.toMap()
        }
        withContext(Dispatchers.IO) {
            titleTextMap.get(
                TitleTextSettingKey.VISIBLE.key
            )
        }.let {
            val onTitleSwitch =
                it != switchOff
            if (onTitleSwitch) return@let
            withContext(Dispatchers.Main) {
                editListTitleFrame?.apply {
                    visibility = View.VISIBLE
                }
            }
            return
        }
        val titleType = withContext(Dispatchers.IO) {
            titleTextMap.get(
                TitleTextSettingKey.TYPE.key
            )?.let { titleTypeSrc ->
                TitleType.entries.firstOrNull {
                    it.str == titleTypeSrc
                }
            }
        } ?: TitleType.LINEAR
        val titleLayoutElevation =
            WithEditConstraintListView.titleLayoutElevation
        when(titleType) {
            TitleType.LINEAR -> {
                withContext(Dispatchers.Main) {
                    editListLinearAlignTitleLayout?.apply {
                        visibility = View.VISIBLE
                        (layoutParams as ConstraintLayout.LayoutParams).apply {
                            elevation = titleLayoutElevation
                        }
                    }
                }
                val titleBkImageView =
                    editListLinearAlignTitleLayout
                        ?.findViewById<AppCompatImageView>(
                            R.id.edit_list_linear_align_title_bk_image
                        )
                val fragImageFrame = editListLinearAlignTitleLayout?.findViewById<FrameLayout>(
                    R.id.edit_list_linear_align_title_backstack_count_frame
                )
                val editBackstackCountView =
                    editListLinearAlignTitleLayout
                        ?.findViewById<ShapeableImageView>(
                            R.id.edit_list_linear_align_title_backstack_count
                        )
                val editBackstackCountShadowView =
                    editListLinearAlignTitleLayout
                        ?.findViewById<ShapeableImageView>(
                            R.id.edit_list_linear_align_title_backstack_count_shaddow
                        )
                val editTextView =
                    editListLinearAlignTitleLayout
                        ?.findViewById<OutlineTextView>(
                            R.id.edit_list_linear_align_title_text
                        )
                setLinearAlignTitle(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    fragImageFrame,
                    titleBkImageView,
                    editBackstackCountView,
                    editBackstackCountShadowView,
                    editTextView,
                    titleTextMap,
                    requestBuilder
                )
            }
            TitleType.FRAG -> {
                withContext(Dispatchers.Main) {
                    editListFragAlignTitleLayout?.apply {
                        visibility = View.VISIBLE
                        (layoutParams as ConstraintLayout.LayoutParams).apply {
                            elevation = titleLayoutElevation
                        }
                    }
                }
                val fragBkImageView = editListFragAlignTitleLayout?.findViewById<AppCompatImageView>(
                    R.id.edit_list_dialog_frag_bk_image
                )
                val titleView = editListFragAlignTitleLayout?.findViewById<OutlineTextView>(
                    R.id.edit_list_dialog_title_text
                )
                FragAlignBk.set(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    fragBkImageView,
                    titleView,
                    titleTextMap,
                    requestBuilder
                )
            }
        }
    }

    private object FragAlignBk {
        suspend fun set(
            fragment: Fragment,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            fragBkImageView: AppCompatImageView?,
            titleView: OutlineTextView?,
            titleTextMap: Map<String, String>?,
            requestBuilderSrc: RequestBuilder<Drawable>?
        ) {
            val context = fragment.context
                ?: return
            val bkCountAndOverrideText = withContext(Dispatchers.IO) {
                EditTextMaker.make(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    titleTextMap
                )
            }
            val bkCountAndOverrideTextList = withContext(Dispatchers.IO) {
                EditTextMaker.makeBkCountAndOverrideTextList(
                    bkCountAndOverrideText
                )
            }
            val overrideText = withContext(Dispatchers.IO) {
                EditTextMaker.extractText(
                    bkCountAndOverrideTextList
                )
            }
            val fillColorStr = withContext(Dispatchers.IO) {
                val foreFillColorsKey = TitleTextSettingKey.FORE_FILL_COLORS.key
                parseColors(
                    context,
                    titleTextMap,
                    foreFillColorsKey
                )?.random() ?: fillColorStrList.random()
            }
            val strokeWidthInt = let {
                val strokeWidthKey = TitleTextSettingKey.STROKE_WIDTH.key
                try {
                    titleTextMap?.get(
                        strokeWidthKey
                    )?.toInt()
                } catch (e: Exception){
                    null
                }
            }
            val strokeColorStr = let {
                val strokeColorKey = TitleTextSettingKey.STROKE_COLORS.key
                parseColors(
                    context,
                    titleTextMap,
                    strokeColorKey
                )?.random() ?: makeWhiteColor()
            }
            val backstackColorKey = TitleTextSettingKey.BACKSTACK_COLORS.key
            val backstackColorStrListSrc = parseColors(
                context,
                titleTextMap,
                backstackColorKey
            ) ?: listOf(backStackStrListForFragBackStack.random())
            if(
                backstackColorStrListSrc.isNullOrEmpty()
                || backstackColorStrListSrc.any {
                    it.isNullOrEmpty()
                }
            ) return
            val escapeColorStrList = listOf(
                fillColorStr,
                strokeColorStr,
            )
            val backstackColorStr =
                backstackColorStrListSrc.random()
                    ?: backStackStrListForFragBackStack.filter {
                        !escapeColorStrList.contains(it)
                    }.random()
            val escapeColorListForBkArgbColor = listOf(
                backstackColorStr,
            ) + escapeColorStrList
            val bkArgbColor = makeArgbBkColor(
                context,
                titleTextMap ?: emptyMap(),
                escapeColorListForBkArgbColor,
                fillColorStr,
            ) ?: return
            val shadowColorStr = let {
                val shadowColorKey = TitleTextSettingKey.SHADOW_COLOR.key
                parseColors(
                    context,
                    titleTextMap,
                    shadowColorKey
                )?.random() ?: makeWhiteColor()
            }
            val shadowRadiusFloat = let {
                val shadowRadiusKey = TitleTextSettingKey.SHADOW_RADIUS.key
                try {
                    titleTextMap?.get(shadowRadiusKey)?.toFloat()
                } catch (e: Exception){
                    null
                }
            } ?: 0f
            val shadowXFloat = let {
                val shadowXKey = TitleTextSettingKey.SHADOW_X.key
                try {
                    titleTextMap?.get(shadowXKey)?.toFloat()
                } catch (e: Exception){
                    null
                }
            } ?: 0f
            val shadowYFloat = let {
                val shadowYKey = TitleTextSettingKey.SHADOW_Y.key
                try {
                    titleTextMap?.get(shadowYKey)?.toFloat()
                } catch (e: Exception){
                    null
                }
            } ?: 0f
            CoroutineScope(Dispatchers.IO).launch {
                setTitleText(
                    titleView,
                    overrideText,
                    fillColorStr,
                    strokeWidthInt,
                    strokeColorStr,
                    shadowColorStr,
                    shadowRadiusFloat,
                    shadowXFloat,
                    shadowYFloat,
                )
            }
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "ldefault.txt").absolutePath,
                listOf(
                    "fillColorStr: ${fillColorStr}",
                    "strokeColorStr: ${strokeColorStr}",
                    "backstackColorStr: ${backstackColorStr}",
                    "bkArgbColor: ${bkArgbColor }",
                ).joinToString("\n")
            )
            CoroutineScope(Dispatchers.IO).launch {
                val bkCount = withContext(Dispatchers.IO) {
                    bkCountAndOverrideTextList.first()
                }
                setBackstackCountAndLogoImage(
                    context,
                    fannelInfoMap,
                    fragBkImageView,
                    bkCount,
                    overrideText,
                    backstackColorStr,
                    bkArgbColor,
                    requestBuilderSrc,
                )
            }
        }

        private suspend fun setTitleText(
            titleView: OutlineTextView?,
            overrideText: String,
            fillColorStr: String,
            strokeWidthInt: Int?,
            strokeColorStr: String,
            shadowColorStr: String,
            shadowRadiusFloat: Float,
            shadowXFloat: Float,
            shadowYFloat: Float,
        ){
            titleView?.apply {
                if (
                    overrideText.isEmpty()
                ) return@apply
                withContext(Dispatchers.Main) {
                    visibility = View.VISIBLE
                    letterSpacing = 0.2f
                    text = overrideText
                    setShadowLayer(
                        shadowRadiusFloat,
                        shadowXFloat,
                        shadowYFloat,
                        Color.parseColor(shadowColorStr)
                    )
                    strokeWidthSrc = strokeWidthInt ?: strokeWidthSrc
                    setFillColor(Color.parseColor(fillColorStr))
                    setStrokeColor(
                        Color.parseColor(
                            strokeColorStr
                        )
                    )
                }
            }
        }

        private enum class PutImageType {
            LOGO,
            BACKSTACK_COUNT
        }

        private suspend fun setBackstackCountAndLogoImage(
            context: Context?,
            fannelInfoMap: Map<String, String>,
            fragBkImageView: AppCompatImageView?,
            bkCount: String,
            overrideText: String,
            backstackColorStr: String,
            bkArgbColor: String,
            requestBuilderSrc: RequestBuilder<Drawable>?,
        ){
            if(
                context == null
            ) return
            fragBkImageView?.apply {
                withContext(Dispatchers.Main) {
                    scaleType = ImageView.ScaleType.FIT_XY
                }

                val logoPngPath = withContext(Dispatchers.IO) {
                    val fannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
                    listOf(
                        UsePath.fannelLogoPngPath,
                    ).joinToString("/").let {
                        ScriptPreWordReplacer.replace(
                            it,
                            fannelName
                        )
                    }
                }
                val logoBitmap = withContext(Dispatchers.IO) {
                    when (File(logoPngPath).isFile) {
                        true -> BitmapTool.convertFileToBitmap(logoPngPath)
                        else -> BitmapTool.convertFileToBitmap(
                            ExecSetToolbarButtonImage.getImageFile(
                                CmdClickIcons.CC.assetsPath
                            ).absolutePath
                        )
                    }
                } ?: return
                val shrinkRate = 0.5f  //0.5f
                val oneSideLength = withContext(Dispatchers.IO) {
                   (context.resources.getDimension(R.dimen.twenty_dp) * 2).let {
                       it * shrinkRate
                   }.toFloat()
                }
                val screenWidth = withContext(Dispatchers.Main) {
                    ScreenSizeCalculator.toDp(
                        context,
                        300
                    )
                }
                val rectWidth = withContext(Dispatchers.Main) {
                    val rectWidthSrc = let {
                        val baseWidth = 720f
                        val minSize = 200f
                        val maxSize = 250f
                        val incline = (maxSize - minSize) / (1080f - baseWidth)
                        val culcSize = incline * (screenWidth - baseWidth) + minSize
                        if (
                            culcSize <= minSize
                        ) return@let minSize
                        culcSize
                    }.toInt()
                    ScreenSizeCalculator.toDp(
                        context,
                        rectWidthSrc
                    ).let {
                        it * shrinkRate
                    }.toInt()
                }
                val rectHeight = withContext(Dispatchers.Main) {
                    ScreenSizeCalculator.toDp(
                        context,
                        55
                    ).let {
                        it * shrinkRate
                    }.toInt()
                }
                val backstackCountBitmap = withContext(Dispatchers.IO) {
                    val boldOfNormalList = listOf(
                        Typeface.BOLD,
                        Typeface.NORMAL,
                    )
                    val fontType = listOf(
                        Typeface.SANS_SERIF,
                        Typeface.SERIF,
                        Typeface.MONOSPACE,
                        Typeface.DEFAULT,
                    )
                    var bkRect = BitmapTool.ImageTransformer.makeRect(
                        bkArgbColor,
                        rectWidth,
                        rectHeight
                    )
                    val bkRectWidth = bkRect.width
                    val bkRectHeight = bkRect.height
//                    FileSystems.writeFromByteArray(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lbkRect.png").absolutePath,
//                        BitmapTool.convertBitmapToByteArray(bkRect)
//                    )
                    val fontSize = let {
                        val baseWidth = 720f
                        val minSize = 70f //60f
                            // (40..60).random().toFloat()
                        val maxSize = minSize + 10f  //70f
                        val incline = (maxSize - minSize) / (1080f - baseWidth)
                        val culcSize = incline * (screenWidth - baseWidth) + minSize
                        if (
                            culcSize <= minSize
                        ) return@let minSize
                        culcSize
                    }.let {
                        it * shrinkRate
                    }.toFloat()
                    val imagePutTimes = 100
                    val backstackCountRotateList =  listOf(90, 180, -90, 0)
                    val backstackCountBitmapListForOverlay = withContext(Dispatchers.IO) {
                        val imageTypeTobackstackCountBitmapListForOverlayJobList = (0..<imagePutTimes).map {
                            async {
                                val isLogoMaking =
                                    (1..6).random() == 1
                                val imageTypeToBackstackCountBitmap = when (isLogoMaking) {
                                    false -> {
                                        val backstackCountBitmapSrc =
                                            BitmapTool.DrawText.drawTextToBitmap(
                                                bkCount,
                                                oneSideLength,
                                                oneSideLength,
                                                null,
                                                fontSize,
                                                Color.parseColor(backstackColorStr),
                                                Color.parseColor(backstackColorStr),
                                                0f,
                                                null,
                                                null,
                                                font = Typeface.create(
                                                    fontType.random(),
                                                    boldOfNormalList.random()
                                                ),
                                                isAntiAlias = true,
                                            ).let {
                                                val shurinkOnesideLength =
                                                    (oneSideLength * 0.8).toInt()
                                                BitmapTool.ImageTransformer.cutCenter2(
                                                    it,
                                                    shurinkOnesideLength,
                                                    shurinkOnesideLength
                                                )
                                            }.let {
                                                BitmapTool.rotate(
                                                    it,
                                                    backstackCountRotateList.random().toFloat()
                                                )
                                            }
                                        PutImageType.BACKSTACK_COUNT to backstackCountBitmapSrc
                                    }

                                    else -> {
                                        val logoBitmapRate = (1..5).random() / 5f
                                        PutImageType.LOGO to Bitmap.createScaledBitmap(
                                            logoBitmap,
                                            (logoBitmap.width * logoBitmapRate).let {
                                                it * shrinkRate
                                            }.toInt(),
                                            (logoBitmap.height * logoBitmapRate).let {
                                                it * shrinkRate
                                            }.toInt(),
                                            false,
                                        ).let {
                                            BitmapTool.rotate(
                                                it,
                                                (0..180).random().toFloat()
                                            )
                                        }
                                    }
                                }
                                val hexOpacity = when (overrideText.isNotEmpty()) {
                                    true -> (50..100).random()
                                    //(10..60).random()
                                    else -> (100..255).random()
                                }

                                val backstackCountBitmapForOverlay =
                                    BitmapTool.ImageTransformer.ajustOpacity(
                                        imageTypeToBackstackCountBitmap.second,
                                        hexOpacity
                                        //(10..60).random()
                                    )
                                imageTypeToBackstackCountBitmap.first to backstackCountBitmapForOverlay
                            }
                        }
                        imageTypeTobackstackCountBitmapListForOverlayJobList.awaitAll()
                    }
                    withContext(Dispatchers.IO) {
                        val overrideTextLength = overrideText.length
                        val maxTextLength = 22
                        val marginDivideInt = let {
                            val maxRate = 10 //14
                            if(
                                overrideTextLength >= maxTextLength
                            ) return@let maxRate
                            ((maxRate * overrideTextLength) / maxTextLength.toFloat()).toInt()
                        }
//                        FileSystems.writeFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lrate.txt").absolutePath,
//                            listOf(
//                                "marginDivideInt: ${marginDivideInt}"
//                            ).joinToString("\n")
//                        )
                        backstackCountBitmapListForOverlay.forEachIndexed { index, imageTypeTobackstackCountBitmap ->
                            val imageType = imageTypeTobackstackCountBitmap.first
                            val backstackCountBitmap = imageTypeTobackstackCountBitmap.second
                            bkRect = when(imageType) {
                                PutImageType.LOGO -> BitmapTool.ImageTransformer.overlayOnBkBitmap(
                                    bkRect,
                                    backstackCountBitmap,
                                )
                                PutImageType.BACKSTACK_COUNT -> {
                                    val pivotX = let {
                                        val marginWidth =
                                            bkRectWidth - backstackCountBitmap.width
                                        val inFreqAreaWidth = marginWidth / marginDivideInt
                                            //marginWidth / 8
                                        when ((1..6).random() == 1) {
                                            true -> (0..marginWidth).random()
                                                .toFloat()

                                            else -> (inFreqAreaWidth..(marginWidth - inFreqAreaWidth)).random()
                                                .toFloat()
                                        }
                                    }
                                    val pivotY = let {
                                        val marginHeight =
                                            bkRectHeight - backstackCountBitmap.height
                                        val inFreqAreaHeight = marginHeight / 5
                                        when ((1..6).random() == 1) {
                                            true -> (0..marginHeight).random()
                                                .toFloat()

                                            else -> (inFreqAreaHeight..(marginHeight - inFreqAreaHeight)).random()
                                                .toFloat()
                                        }
                                    }
                                    BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                                        bkRect,
                                        backstackCountBitmap,
                                        pivotX,
                                        pivotY
                                    )
                                }
                            }
                        }
                    }
                    val trimWidth = let {
                        val baseWidth = 720f
                        val minSize = 100f
                        val maxSize = 200f //70f
                        val incline = (maxSize - minSize) / (1080f - baseWidth)
                        val culcSize = incline * (screenWidth - baseWidth) + minSize
                        if (
                            culcSize <= minSize
                        ) return@let minSize
                        culcSize
                    }.let {
                        it * shrinkRate
                    }.toInt()
                    val trimHeight = let {
                        val baseWidth = 720f
                        val minSize = 70f
                        val maxSize = 100f //70f
                        val incline = (maxSize - minSize) / (1080f - baseWidth)
                        val culcSize = incline * (screenWidth - baseWidth) + minSize
                        if (
                            culcSize <= minSize
                        ) return@let minSize
                        culcSize
                    }.let {
                        it * shrinkRate
                    }.toInt()
                    BitmapTool.ImageTransformer.cutCenter2(
                        bkRect,
                        bkRect.width - trimWidth,
                        bkRect.height - trimHeight
                    )
                }
                withContext(Dispatchers.Main) {
                    val requestBuilder: RequestBuilder<Drawable> =
                        requestBuilderSrc ?: Glide.with(context)
                            .asDrawable()
                            .sizeMultiplier(0.1f)
                    Glide
                        .with(context)
                        .load(backstackCountBitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate()
                        .thumbnail(requestBuilder)
                        .into(this@apply)
                }

            }
        }
    }

    private suspend fun setLinearAlignTitle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        fragImageFrame: FrameLayout?,
        titleBkImageView: AppCompatImageView?,
        editBackstackCountView: ShapeableImageView?,
        editBackstackCountShadowView: ShapeableImageView?,
        editTextView: OutlineTextView?,
        titleTextMap: Map<String, String>,
        requestBuilder: RequestBuilder<Drawable>?,

    ){
        val context = fragment.context
        val whiteColorStr =
            withContext(Dispatchers.IO) {
                makeWhiteColor()
            }
        val fillColorStr = withContext(Dispatchers.IO) {
            val foreFillColorsKey = TitleTextSettingKey.FORE_FILL_COLORS.key
            parseColors(
                context,
                titleTextMap,
                foreFillColorsKey
            )?.random() ?: fillColorStrList.random()
        }
        val strokeWidthInt = let {
            val strokeWidthKey = TitleTextSettingKey.STROKE_WIDTH.key
            try {
                titleTextMap.get(
                    strokeWidthKey
                )?.toInt()
            } catch (e: Exception){
                null
            }
        }
        val strokeColorStr = let {
            val strokeColorKey = TitleTextSettingKey.STROKE_COLORS.key
            parseColors(
                context,
                titleTextMap,
                strokeColorKey
            )?.random() ?: whiteColorStr
        }
        val shadowColorStr = let {
            val shadowColorKey = TitleTextSettingKey.SHADOW_COLOR.key
            parseColors(
                context,
                titleTextMap,
                shadowColorKey
            )?.random() ?: makeWhiteColor()
        }
        val shadowRadiusFloat = let {
            val shadowRadiusKey = TitleTextSettingKey.SHADOW_RADIUS.key
            try {
                titleTextMap.get(shadowRadiusKey)?.toFloat()
            } catch (e: Exception){
                null
            }
        } ?: 0f
        val shadowXFloat = let {
            val shadowXKey = TitleTextSettingKey.SHADOW_X.key
            try {
                titleTextMap.get(shadowXKey)?.toFloat()
            } catch (e: Exception){
                null
            }
        } ?: 0f
        val shadowYFloat = let {
            val shadowYKey = TitleTextSettingKey.SHADOW_Y.key
            try {
                titleTextMap.get(shadowYKey)?.toFloat()
            } catch (e: Exception){
                null
            }
        } ?: 0f
        val backstackColorStr = let {
            val backstackColorsKey = TitleTextSettingKey.BACKSTACK_COLORS.key
            parseColors(
                context,
                titleTextMap,
                backstackColorsKey
            )?.random() ?: whiteColorStr
        }
        setTitleText(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            fragImageFrame,
            editBackstackCountView,
            editBackstackCountShadowView,
            editTextView,
            titleTextMap,
            fillColorStr,
            backstackColorStr,
            strokeWidthInt,
            strokeColorStr,
            shadowColorStr,
            shadowRadiusFloat,
            shadowXFloat,
            shadowYFloat,
            requestBuilder,
        )
        val bkArgbColorStr = makeArgbBkColor(
            fragment.context,
            titleTextMap,
            listOf(
                fillColorStr,
                backstackColorStr,
                strokeColorStr
            ),
            fillColorStr,
        ) ?: return
        setTitleImage(
            fragment,
            titleBkImageView,
            fannelInfoMap,
            bkArgbColorStr,
            requestBuilder,
        )
    }

//    private fun colorChecker(
//        context: Context?,
//        colorStr: String,
//        colorKey: String,
//    ): String? {
//        return try {
//            Color.parseColor(
//                colorStr
//            )
//            colorStr
//        } catch (e: Exception){
//            val spanColorKey =
//                CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errBrown,
//                    colorKey
//                )
//            val spanSrcColorStr =CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                CheckTool.errRedCode,
//                colorStr
//            )
//            LogSystems.stdErr(
//                context,
//                "${ErrType.FANNEL_TITLE.str} ${spanColorKey} parse err: ${spanSrcColorStr}",
//            )
//            null
//        }
//    }

    private fun makeArgbBkColor(
        context: Context?,
        titleTextMap: Map<String, String>,
        escapeColorListWhenDiff: List<String>,
        fillColorStr: String,
    ): String? {
        val fillColorStrListForBk = listOf(
//        blackGreen,
            CmdClickColorStr.GREEN.str,
//        CmdClickColorStr.THICK_GREEN.str,
//            CmdClickColorStr.CARKI.str,
//            CmdClickColorStr.GOLD_YELLOW.str,
//        CmdClickColorStr.DARK_GREEN.str,
            CmdClickColorStr.THICK_AO.str,
//        CmdClickColorStr.BLACK_AO.str,
//        CmdClickColorStr.NAVY.str,
            CmdClickColorStr.BLUE.str,
//        CmdClickColorStr.BLUE_DARK_PURPLE.str,
            CmdClickColorStr.PURPLE.str,
            CmdClickColorStr.BROWN.str,
//        CmdClickColorStr.DARK_BROWN.str,
            CmdClickColorStr.SKERLET.str,
        )
        val onForeBkDiffColor = titleTextMap.get(
            TitleTextSettingKey.ON_FORE_BK_DIFF_COLOR.key
        ) == switchOn
        val bkFillColorsKey =
            TitleTextSettingKey.BK_FILL_COLORS.key
        val bkColorStr = parseColors(
            context,
            titleTextMap,
            bkFillColorsKey
        )?.random() ?: let {
            when (onForeBkDiffColor) {
                true -> {
                    fillColorStrListForBk.filter {
                        !escapeColorListWhenDiff.contains(it)
                    }.random()
                }

                else -> fillColorStr
            }
        }
//        val bkColorStr = when(bkColorStrListSrc.isNullOrEmpty()) {
//            false -> colorChecker(
//                context,
//                bkColorStrListSrc.random(),
//                bkFillColorsKey,
//                ) ?: return null
//            else -> when (onForeBkDiffColor) {
//                true -> {
//                    fillColorStrListForBk.filter {
//                        !escapeColorListWhenDiff.contains(it)
//                    }.random()
//                }
//
//                else -> fillColorStr
//            }
//        }
        val bkHexAlphaKey =
            TitleTextSettingKey.BK_HEX_ALPHA.key
        val bkAlphaIntPrefixStr = titleTextMap.get(
            bkHexAlphaKey
        )
        val bkAlphaInt = bkAlphaIntPrefixStr?.let {
            try {
                val bkAlphaIntSrc = it.toInt()
                val maxAlpha = 255
                when(bkAlphaIntSrc >= maxAlpha){
                    true -> maxAlpha
                    else -> bkAlphaIntSrc
                }
            } catch (e: Exception){
                null
            }
        } ?: (3..9).random()
        val bkArgbColorSrc = let {
            val bkAlphaIntStr = "#%02x".format(bkAlphaInt)
            bkColorStr.replace(
                "#",
                bkAlphaIntStr,
            )
        }

//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbkArgbColorSrc.txt").absolutePath,
//            listOf(
//                "bkArgbColorSrc: ${bkArgbColorSrc}",
//                "bkAlphaIntPrefixStr: ${bkAlphaIntPrefixStr}",
//                "titleTextMap: ${titleTextMap}",
//            ).joinToString("\n")
//        )
        ColorTool.parseColorStr(
            context,
            bkArgbColorSrc,
            bkHexAlphaKey,
            ErrType.FANNEL_TITLE.str,
        ) ?: return null
//        colorChecker(
//            context,
//            bkArgbColorSrc,
//            bkHexAlphaKey
//        ) ?: return null
        return bkArgbColorSrc
    }

    private val blackGreen = "#0f1419"
//    private val fillGray = "#808080"

    private val backStackStrListForFragBackStack = listOf(
//        blackGreen,
//        fillGray,
        CmdClickColorStr.GREEN.str,
        CmdClickColorStr.WHITE_GREEN.str,
        CmdClickColorStr.LIGHT_GREEN.str,
        CmdClickColorStr.ANDROID_GREEN.str,
        CmdClickColorStr.YELLOW_GREEN.str,
        CmdClickColorStr.YELLOW.str,
//        CmdClickColorStr.THICK_GREEN.str,
//        CmdClickColorStr.CARKI.str,
        CmdClickColorStr.GOLD_YELLOW.str,
//        CmdClickColorStr.DARK_GREEN.str,
//        CmdClickColorStr.THICK_AO.str,
//        CmdClickColorStr.BLACK_AO.str,
//        CmdClickColorStr.NAVY.str,
        CmdClickColorStr.WHITE_BLUE.str,
        CmdClickColorStr.WHITE_BLUE_PURPLE.str,
        CmdClickColorStr.BLUE.str,
//        CmdClickColorStr.BLUE_DARK_PURPLE.str,
//        CmdClickColorStr.PURPLE.str,
//        CmdClickColorStr.BROWN.str,
//        CmdClickColorStr.DARK_BROWN.str,
        CmdClickColorStr.SKERLET.str,
    )

    private val fillColorStrList = listOf(
//        blackGreen,
//        fillGray,
        CmdClickColorStr.GREEN.str,
//        CmdClickColorStr.THICK_GREEN.str,
        CmdClickColorStr.CARKI.str,
        CmdClickColorStr.GOLD_YELLOW.str,
//        CmdClickColorStr.DARK_GREEN.str,
        CmdClickColorStr.THICK_AO.str,
//        CmdClickColorStr.BLACK_AO.str,
//        CmdClickColorStr.NAVY.str,
        CmdClickColorStr.BLUE.str,
//        CmdClickColorStr.BLUE_DARK_PURPLE.str,
        CmdClickColorStr.PURPLE.str,
        CmdClickColorStr.BROWN.str,
//        CmdClickColorStr.DARK_BROWN.str,
        CmdClickColorStr.SKERLET.str,
    )
    private suspend fun setTitleText(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        fragImageFrame: FrameLayout?,
        editBackstackCountView: ShapeableImageView?,
        editBackstackCountShadowView: ShapeableImageView?,
        editTextView: OutlineTextView   ?,
        titleTextMap: Map<String, String>?,
        fillColorStr: String,
        backstackColorStr: String,
        strokeWidthInt: Int?,
        strokeColorStr: String,
        shadowColorStr: String,
        shadowRadiusFloat: Float,
        shadowXFloat: Float,
        shadowYFloat: Float,
        requestBuilderSrc: RequestBuilder<Drawable>?,
    ){
        val context = fragment.context ?: return
        val bkCountAndOverrideText = withContext(Dispatchers.IO) {
            EditTextMaker.make(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                titleTextMap
            )
        }
        val bkCountAndOverrideTextList = withContext(Dispatchers.IO) {
            EditTextMaker.makeBkCountAndOverrideTextList(
                bkCountAndOverrideText
            )
        }
        val bkCount = withContext(Dispatchers.IO) { bkCountAndOverrideTextList.first() }
        val overrideText = withContext(Dispatchers.IO) {
            EditTextMaker.extractText(
                bkCountAndOverrideTextList
            )
        }
        editBackstackCountShadowView?.apply {
            withContext(Dispatchers.Main) {
                imageTintList =
                    ColorStateList.valueOf(Color.parseColor(shadowColorStr))
                val bitmap = AppCompatResources.getDrawable(
                    context,
                    R.drawable.rect
                )?.toBitmap()
                Blurry.with(context)
                    .radius(25)
                    .sampling(2)
                    .async()
                    .animate(500)
                    .from(bitmap)
                    .into( this@apply)
//                val requestBuilder: RequestBuilder<Drawable> =
//                    requestBuilderSrc ?: Glide.with(context)
//                        .asDrawable()
//                        .sizeMultiplier(0.1f)
//                Glide
//                    .with(context)
//                    .load(R.drawable.rect)
//                    .transform(
//                        GrayscaleTransformation(),
//                        BlurTransformation(50, )
//                    )
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .dontAnimate()
//                    .thumbnail(requestBuilder)
//                    .into(this@apply)
            }
        }
        editBackstackCountView?.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val oneSideLength = withContext(Dispatchers.IO){
                    context.resources.getDimension(R.dimen.twenty_dp) * 2
                }
                val screenWidth = withContext(Dispatchers.Main) {
                    ScreenSizeCalculator.pxWidth(
                        fragment.activity
                    )
                }
                val backstackCountBitmap = withContext(Dispatchers.IO) {
                    val fontSize = let {
                        val baseWidth = 720f
                        val minSize = 60f
                        val maxSize = 70f
                        val incline = (maxSize - minSize) / (1080f - baseWidth)
                        val culcSize = incline  * (screenWidth - baseWidth) + minSize
                        if(
                            culcSize <= minSize
                        ) return@let minSize
                        culcSize
                    }
                    val backstackCountBitmap = BitmapTool.DrawText.drawTextToBitmap(
                        bkCount,
                        oneSideLength,
                        oneSideLength,
                        null,
                        fontSize,
                        Color.parseColor(backstackColorStr),
                        Color.parseColor(backstackColorStr),
                        null,
                        null,
                        null,
                        font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
                        isAntiAlias = true,
                    )
                    backstackCountBitmap
                }
                withContext(Dispatchers.Main) {
                    setBackgroundColor(Color.parseColor(fillColorStr))
                    val requestBuilder: RequestBuilder<Drawable> =
                        requestBuilderSrc ?: Glide.with(context)
                            .asDrawable()
                            .sizeMultiplier(0.1f)
                    Glide
                        .with(context)
                        .load(backstackCountBitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate()
                        .thumbnail(requestBuilder)
                        .into(this@apply)
                }

            }
        }
        withContext(Dispatchers.Main){
            editTextView?.apply {
                letterSpacing = 0.2f
                strokeWidthSrc =
                    strokeWidthInt ?: strokeWidthSrc
                setFillColor(
                    Color.parseColor(
                        fillColorStr
                    )
                )
                setStrokeColor(
                    Color.parseColor(
                        strokeColorStr
                    )
                )
                setShadowLayer(
                    shadowRadiusFloat,
                    shadowXFloat,
                    shadowYFloat,
                    Color.parseColor(shadowColorStr)
                )
                text = overrideText
            }
        }
    }

    private fun makeWhiteColor(
    ): String {
        val pink = "#faf0f9"
        val whiteColorList = listOf(
            CmdClickColorStr.WHITE_GREEN.str,
            CmdClickColorStr.WHITE_BLUE.str,
            CmdClickColorStr.WHITE_BLUE_PURPLE.str,
            pink,
        )
        return whiteColorList.random()
    }

    fun makeDefaultTitle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        isBackstackEmoji: Boolean,
    ): String {
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val backstackOrder = makeBackstackCount(
            fragment,
            isBackstackEmoji,
        )
        return listOf(
            backstackOrder,
            makeCompressFannelPath(
                currentFannelName
            ),
        ).joinToString(backstackCountSeparator)
    }

    fun makeCompressFannelPath(
        currentScriptFileName: String
    ): String {
        return CcPathTool.trimAllExtend(
            UsePath.makeOmitPath(currentScriptFileName)
        )
    }

    fun makeBackstackCount(
        fragment: Fragment,
        isBackstackEmoji: Boolean
    ): String {
        return execMakeBackstackCount(
            fragment,
            isBackstackEmoji
        )
    }

    private suspend fun setTitleImage(
        fragment: Fragment,
        editTitleImageView: AppCompatImageView?,
        fannelInfoMap: Map<String, String>,
        bkArgbColorStr: String,
        requestBuilder: RequestBuilder<Drawable>?
    ){
        val currentFannelName = withContext(Dispatchers.IO) {
            FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
        }
        FannelLogoSetter.setTitleFannelLogo(
            fragment,
            editTitleImageView,
            currentFannelName,
            bkArgbColorStr,
            requestBuilder
        )
    }
}

private object FannelLogoSetter {

    suspend fun setTitleFannelLogo(
        fragment: Fragment,
        titleImageView: AppCompatImageView?,
        selectedScriptName: String,
        bkArgbColorStr: String,
        requestBuilderSrc: RequestBuilder<Drawable>?,
    ){
        val context = fragment.context
            ?: return
        if(
            titleImageView == null
        ) return
        val logoPngPath =  withContext(Dispatchers.IO) {
            listOf(
                UsePath.fannelLogoPngPath,
            ).joinToString("/").let {
                ScriptPreWordReplacer.replace(
                    it,
                    selectedScriptName
                )
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val logoBitmap = withContext(Dispatchers.IO) {
                when (File(logoPngPath).isFile) {
                    true -> BitmapTool.convertFileToBitmap(logoPngPath)
                    else -> BitmapTool.convertFileToBitmap(
                        ExecSetToolbarButtonImage.getImageFile(
                            CmdClickIcons.CC.assetsPath
                        ).absolutePath
                    )
                }
            }?: return@launch
            val rectHeight = withContext(Dispatchers.IO) { logoBitmap.height * 2 }
            val rectWidth = withContext(Dispatchers.IO) { rectHeight * 5 }
            val updatedRectBitmap = withContext(Dispatchers.IO) {
                val rectBitmap = BitmapTool.ImageTransformer.makeRect(
                    bkArgbColorStr,
//            "#bbedc9", //"#0000000000"
                    rectWidth,
                    rectHeight,
                )
                var updatedRectBitmap = rectBitmap
                val addTimes = (4..7).random()
                for (i in 1..addTimes) {
                    val logoBitmapRate = (1..5).random() / 5f
                    val rateLogoBitmap = Bitmap.createScaledBitmap(
                        logoBitmap,
                        (logoBitmap.width * logoBitmapRate).toInt(),
                        (logoBitmap.height * logoBitmapRate).toInt(),
                        false,
                    ).let {
                        BitmapTool.rotate(
                            it,
                            (0..180).random().toFloat()
                        )
                    }.let {
                        BitmapTool.ImageTransformer.ajustOpacity(
                            it,
                            (10..60).random()
                        )
                    }
                    updatedRectBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmap(
                        updatedRectBitmap,
                        rateLogoBitmap
                    )
                }
                updatedRectBitmap
            }

            withContext(Dispatchers.Main) {
                val requestBuilder =
                    requestBuilderSrc
                        ?: Glide.with(context).asDrawable().sizeMultiplier(0.1f)
                Glide
                    .with(context)
                    .load(updatedRectBitmap)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(requestBuilder)
                    .into(titleImageView)
            }
        }
    }
}

private object EditTextMaker {

    fun make(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        titleTextMap: Map<String, String>?,
    ): String {
        val defaultEditBoxTitle = TitleImageAndViewSetter.makeDefaultTitle(
            fragment,
            fannelInfoMap,
            true,
        )
        val shellConText = makeByShellCon(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            titleTextMap,
            busyboxExecutor,
            defaultEditBoxTitle,
        )
        return when(shellConText.isNullOrEmpty()) {
            false -> shellConText
            else -> SearchBoxSettingsForEditList.makeCurrentVariableValueInEditText(
                fragment,
                fannelInfoMap,
                defaultEditBoxTitle
            )
        }
    }
    private fun makeByShellCon(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editTextPropertyMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        currentVariableValue: String?,
    ): String? {
        if(
            editTextPropertyMap.isNullOrEmpty()
            || busyboxExecutor == null
        ) return null
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val shellConSrc = editTextPropertyMap.get(
            TitleImageAndViewSetter.TitleTextSettingKey.SHELL_CON.key
        )
        val repValMap = editTextPropertyMap.get(
            TitleImageAndViewSetter.TitleTextSettingKey.ARGS.key
        ).let {
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
        if(
            !shellConSrc.isNullOrEmpty()
        ) return getOutputByShellCon(
            fragment,
            setReplaceVariableMap,
            busyboxExecutor,
            repValMap,
            shellConSrc,
//            currentAppDirPath,
            currentFannelName,
            currentVariableValue
        )
        val backstackCountKey =
            SearchBoxSettingsForEditList.backstackCountMarkForInsertEditText
        val backstackCountMap = mapOf(
            backstackCountKey to execMakeBackstackCount(
                fragment,
                true,
            )
        )
        val updateRepValMap = repValMap + backstackCountMap
        return ShellMacroHandler.handle(
            fragment.context,
            busyboxExecutor,
            editTextPropertyMap.get(
                TitleImageAndViewSetter.TitleTextSettingKey.SHELL_PATH.key
            ) ?: String(),
            setReplaceVariableMap,
            updateRepValMap
        )
//                EditSettingExtraArgsTool.makeShellCon(editTextPropertyMap)
    }

    private fun getOutputByShellCon(
        fragment: Fragment,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        repValMap: Map<String, String>?,
        shellConSrc: String,
        currentFannelName: String,
        currentVariableValue: String?
    ): String? {
        val shellCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            shellConSrc,
            setReplaceVariableMap,
//            currentAppDirPath,
            currentFannelName
        ).replace(
            "\${defaultEditBoxTitle}",
            currentVariableValue ?: String(),
        ).let {
            SearchBoxSettingsForEditList.backStackMarkReplace(
                fragment,
                it
            )
        }
        if(
            shellCon.isEmpty()
        ) return null
        return busyboxExecutor?.getCmdOutput(
            shellCon,
            repValMap
        )
    }

    private val space = " "
    fun makeBkCountAndOverrideTextList(
        bkCountAndOverrideText: String
    ): List<String> {
        return bkCountAndOverrideText.split(
            space
        )
    }

    fun extractText(
        bkCountAndOverrideTextList: List<String>
    ): String {
        return bkCountAndOverrideTextList.filterIndexed { index, _ ->
            index > 0
        }.joinToString(space).trim()
    }
}


private fun execMakeBackstackCount(
    fragment: Fragment,
    isBackstackEmoji: Boolean
): String {
    val backstackCount = fragment
        .activity
        ?.supportFragmentManager
        ?.backStackEntryCount
        ?: 0
    if(
        !isBackstackEmoji
    ) return "(${backstackCount})"
    val numberStrMap = mapOf(
        0.toString() to "0\uFE0F⃣",
        1.toString() to "1\uFE0F⃣",
        2.toString() to "2\uFE0F⃣",
        3.toString() to "3\uFE0F⃣",
        4.toString() to "4\uFE0F⃣",
        5.toString() to "5\uFE0F⃣",
        6.toString() to "6\uFE0F⃣",
        7.toString() to "7\uFE0F⃣",
        8.toString() to "8\uFE0F⃣",
        9.toString() to "9\uFE0F⃣",
    )
    return backstackCount.toString()
}

