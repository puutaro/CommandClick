package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object TitleImageAndViewSetter {

    private const val backstackCountSeparator = " "
    private const val switchOff = "OFF"
    private const val switchOn = "ON"
    private const val keySeparator = '|'


    private enum class ErrType(
        val str: String
    ) {
        FANNEL_TITLE("[FANNEL TITLE]")
    }

    suspend fun set(
        fragment: Fragment,
        editBackstackCountFrame: FrameLayout,
        editBackstackCountView: ShapeableImageView,
        editTextView: OutlineTextView,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editTitleImageView: AppCompatImageView,
        titleSettingMap: Map<String, String>?,
        requestBuilder: RequestBuilder<Drawable>?,
    ) {
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
                editBackstackCountFrame.isVisible = false
                editTextView.isVisible = false
            }
//           editFragment.binding.editTitleLinearlayout.isVisible = false
            return
        }
        val whiteColorStr =
            withContext(Dispatchers.IO) {
                convertWhiteColor()
            }
        val fillColorStr = withContext(Dispatchers.IO) {
            val foreFillColorKey = TitleTextSettingKey.FORE_FILL_COLOR.key
            val fillColorStrSrc = titleTextMap.get(
                foreFillColorKey
            )
            when(fillColorStrSrc.isNullOrEmpty()) {
                false -> {
                    colorChecker(
                        fragment.context,
                        fillColorStrSrc,
                        foreFillColorKey,
                    ) ?: return@withContext null
                }
                else -> fillColorStrList.random()
            }
        } ?: return
        val strokeColorStr = let {
            val strokeColorKey = TitleTextSettingKey.STROKE_COLOR.key
            val strokeColorStrSrc = titleTextMap.get(
                strokeColorKey
            )
            when (strokeColorStrSrc.isNullOrEmpty()) {
                false -> {
                    colorChecker(
                        fragment.context,
                        strokeColorStrSrc,
                        strokeColorKey,
                    ) ?: return
                }
                else -> whiteColorStr
            }
        }

        val backstackColorStr = let {
            val backstackColorKey = TitleTextSettingKey.BACKSTACK_COLOR.key
            val backstackColorStrSrc = titleTextMap.get(
                backstackColorKey
            )
            when (backstackColorStrSrc.isNullOrEmpty()) {
                false -> colorChecker(
                    fragment.context,
                    backstackColorStrSrc,
                    backstackColorKey,
                    ) ?: return
                else -> whiteColorStr
            }
        }

        setTitleText(
            fragment,
            editBackstackCountView,
            editTextView,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            titleTextMap,
            fillColorStr,
            backstackColorStr,
            strokeColorStr,
            requestBuilder,
        )
        val bkArgbColor = makeArgbBkColor(
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
            editTitleImageView,
            fannelInfoMap,
            bkArgbColor,
            requestBuilder,
        )
    }

    private fun colorChecker(
        context: Context?,
        colorStr: String,
        colorKey: String,
    ): String? {
        return try {
            Color.parseColor(
                colorStr
            )
            colorStr
        } catch (e: Exception){
            val spanColorKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    colorKey
                )
            val spanSrcColorStr =CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                colorStr
            )
            LogSystems.stdErr(
                context,
                "${ErrType.FANNEL_TITLE.str} ${spanColorKey} parse err: ${spanSrcColorStr}",
            )
            null
        }
    }

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
        val bkFillColorKey =
            TitleTextSettingKey.BK_FILL_COLOR.key
        val bkColorStrSrc = titleTextMap.get(
            bkFillColorKey
        )
        val bkColorStr = when(bkColorStrSrc.isNullOrEmpty()) {
            false -> colorChecker(
                context,
                bkColorStrSrc,
                bkFillColorKey,
                ) ?: return null
            else -> when (onForeBkDiffColor) {
                true -> {
                    fillColorStrListForBk.filter {
                        !escapeColorListWhenDiff.contains(it)
                    }.random()
                }

                else -> fillColorStr
            }
        }
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
        colorChecker(
            context,
            bkArgbColorSrc,
            bkHexAlphaKey
        ) ?: return null
        return bkArgbColorSrc
    }

    private val blackGreen = "#0f1419"
//    private val fillGray = "#808080"
    val fillColorStrList = listOf(
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
        editBackstackCountView: ShapeableImageView,
        editTextView: OutlineTextView,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        titleTextMap: Map<String, String>?,
        fillColorStr: String,
        backstackColorStr: String,
        strokeColorStr: String,
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
        val space = " "
        val bkCountAndOverrideTextList = withContext(Dispatchers.IO) {
            bkCountAndOverrideText.split(
                space
            )
        }
        val bkCount = withContext(Dispatchers.IO) { bkCountAndOverrideTextList.first() }
        val overrideText = withContext(Dispatchers.IO) {
            bkCountAndOverrideTextList.filterIndexed { index, _ ->
                index > 0
            }.joinToString(space).trim()
        }
//        val fillColorStr = fillColorStrList.random()
        editBackstackCountView.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val oneSideLength = withContext(Dispatchers.IO){
                    context.resources.getDimension(R.dimen.twenty_dp) * 2
                }
                val screenWidth = withContext(Dispatchers.Main) {
                    ScreenSizeCalculator.pxWidth(
                        fragment
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
                        null,
                        font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
                    )
                    backstackCountBitmap
//                    val baseCornerDips = ScreenSizeCalculator.toDp(context, 4)
//                    val extendDp = ScreenSizeCalculator.toDp(context, 4)
//                    val backstackCountBitmapWidth = backstackCountBitmap.width
//                    val backstackCountBitmapHeight = backstackCountBitmap.height
//                    val bkBaseRect = BitmapTool.ImageTransformer.makeRect(
//                        whiteColorStr,
//                        backstackCountBitmapWidth + extendDp,
//                        backstackCountBitmapHeight + extendDp
//                    ).let {
//                        BitmapTool.ImageTransformer.roundCorner(
//                            context,
//                            it,
//                            baseCornerDips,
//                        )
//                    }
//
//                    val bkCornerDip = baseCornerDips + ScreenSizeCalculator.toDp(context, 2)
//                    val roundCornerBackkstackBitmap = BitmapTool.ImageTransformer.roundCorner(
//                        context,
//                        backstackCountBitmap,
//                        bkCornerDip,
//                    )
//                    val displayRoundCornerBackkstackBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapCenter(
//                            bkBaseRect as Bitmap,
//                            roundCornerBackkstackBitmap as Bitmap
//                        )
//                    displayRoundCornerBackkstackBitmap?.let {
//                        FileSystems.writeFromByteArray(
//                            File(
//                                UsePath.cmdclickDefaultAppDirPath,
//                                "ldisplayRoundCornerBackkstackBitmap.png"
//                            ).absolutePath,
//                            BitmapTool.convertBitmapToByteArray(
//                                it
//                            )
//                        )
//                    }
//                    displayRoundCornerBackkstackBitmap
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
            editTextView.apply {
                letterSpacing = 0.2f
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
                text = overrideText
            }
        }
    }

    private fun convertWhiteColor(
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
//        val fannelInfoMap =
//            fragment.fannelInfoMap

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
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
//                currentAppDirPath,
                currentFannelName
            ),
        ).joinToString(backstackCountSeparator)
    }

    fun makeCompressFannelPath(
//        currentAppDirPath: String,
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
        editTitleImageView: AppCompatImageView,
        fannelInfoMap: Map<String, String>,
        bkArgbColor: String,
        requestBuilder: RequestBuilder<Drawable>?
    ){
        val currentFannelName = withContext(Dispatchers.IO) {
            FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
        }
//        val binding = fragment.binding
//        val editTitleImageView = binding.editTitleImage
        FannelLogoSetter.setTitleFannelLogo(
            fragment,
            editTitleImageView,
//        currentAppDirPath: String,
            currentFannelName,
            bkArgbColor,
            requestBuilder
        )
//        QrLogo(editFragment).setTitleFannelLogo(
//            editTitleImageView,
////            currentAppDirPath,
//            currentFannelName
//        )
    }
}

private object FannelLogoSetter {

    suspend fun setTitleFannelLogo(
        fragment: Fragment,
        titleImageView: AppCompatImageView?,
//        currentAppDirPath: String,
        selectedScriptName: String,
        bkArgbColor: String,
        requestBuilderSrc: RequestBuilder<Drawable>?,
    ){
        val context = fragment.context
            ?: return
        if(
            titleImageView == null
        ) return
//        val fannelDirName = CcPathTool.makeFannelDirName(selectedScriptName)
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
//            "${UsePath.cmdclickDefaultAppDirPath}/$fannelDirName/${UsePath.qrPngRelativePath}"
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
                    bkArgbColor,
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
                        BitmapTool.ImageTransformer.adjustOpacity(
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

//        val isEditExecute = checkEditExecute(
////            currentAppDirPath,
//            selectedScriptName,
//        )
//        titleImageView.setPadding(2, 2,2,2)
//        titleImageView.background = if(isEditExecute) {
//            AppCompatResources.getDrawable(context, R.color.terminal_color)
//        } else AppCompatResources.getDrawable(context, R.color.fannel_icon_color)
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
//        titleImageView.load(logoPngPath)
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
//        val busyboxExecutor =
//            editFragment.busyboxExecutor
//                ?: return null
//        val setReplaceVariableMap =
//            editFragment.setReplaceVariableMap
//
//        val fannelInfoMap =
//            editFragment.fannelInfoMap

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val shellConSrc = editTextPropertyMap.get(
            TitleTextSettingKey.SHELL_CON.key
        )
        val repValMap = editTextPropertyMap.get(
            TitleTextSettingKey.ARGS.key
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
                TitleTextSettingKey.SHELL_PATH.key
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
//        currentAppDirPath: String,
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
//        .map {
//        c ->
//        numberStrMap.get(c.toString()) ?: "0"
//    }.joinToString(String()) + " "
}

private enum class EditBoxTitleKey(
    val key: String,
){
    TEXT("text"),
    IMAGE("image"),
}

enum class TitleTextSettingKey(
    val key: String
){
//    SIZE("size"),
    HEIGHT("height"),
    BK_HEX_ALPHA("bkHexAlpha"),
    STROKE_COLOR("strokeColor"),
    BACKSTACK_COLOR("backstackColor"),
    FORE_FILL_COLOR("foreFillColor"),
    BK_FILL_COLOR("bkFillColor"),
    ON_FORE_BK_DIFF_COLOR("onForeBkDiffColor"),
    VISIBLE("visible"),
    SHELL_PATH("shellPath"),
    SHELL_CON("shellCon"),
    ARGS("args"),
}

//enum class TitleImageSettingKey(
//    val key: String
//){
//    VISIBLE("visible"),
//}