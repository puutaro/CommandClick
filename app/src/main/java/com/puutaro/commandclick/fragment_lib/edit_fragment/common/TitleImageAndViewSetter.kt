package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.graphics.Bitmap
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
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
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
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

    private const val keySeparator = '|'
    fun set(
        fragment: Fragment,
        editBackstackCountView: ShapeableImageView,
        editTextView: OutlineTextView,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editTitleImageView: AppCompatImageView,
        titleSettingMap: Map<String, String>?,
    ) {
        val titleTextMap = titleSettingMap?.get(
            EditBoxTitleKey.TEXT.key
        ).let {
            CmdClickMap.createMap(
                it,
                keySeparator
            )
        }.toMap()
        val titleImageMap = titleSettingMap?.get(
            EditBoxTitleKey.IMAGE.key
        ).let {
            CmdClickMap.createMap(
                it,
                keySeparator
            )
        }.toMap()
        titleTextMap.get(
            TitleTextSettingKey.VISIBLE.key
        ).let {
           val onTitleSwitch =
               it != switchOff
           if (onTitleSwitch) return@let
//           editFragment.binding.editTitleLinearlayout.isVisible = false
           return
       }
        val fillColorStr = fillColorStrList.random()
        setTitleText(
            fragment,
            editBackstackCountView,
            editTextView,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            titleTextMap,
            fillColorStr,
        )
        setTitleImage(
            fragment,
            editTitleImageView,
            fannelInfoMap,
            fillColorStr,
            titleImageMap,
        )
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
    private fun setTitleText(
        fragment: Fragment,
        editBackstackCountView: ShapeableImageView,
        editTextView: OutlineTextView,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        titleTextMap: Map<String, String>?,
        fillColorStr: String,
    ){
        val context = fragment.context ?: return
        val bkCountAndOverrideText = EditTextMaker.make(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            titleTextMap
        )
        val bkCountAndOverrideTextList = bkCountAndOverrideText.split(" ")
        val bkCount = bkCountAndOverrideTextList.first()
        val overrideText = bkCountAndOverrideTextList.filterIndexed { index, _ ->
            index > 0
        }.joinToString(String()).trim()
//        val fillColorStr = fillColorStrList.random()
        val whiteColorStr = convertWhiteColor()
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
                    BitmapTool.DrawText.drawTextToBitmap(
                        bkCount,
                        oneSideLength,
                        oneSideLength,
                        null,
                        fontSize,
                        Color.parseColor(whiteColorStr),
                        Color.parseColor(whiteColorStr),
                        null,
                        null,
                        null,
                        null,
                        font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
                    )
                }
                withContext(Dispatchers.Main) {
                    setBackgroundColor(Color.parseColor(fillColorStr))
//                     val hex = String.format("#%06X", 0xFFFFFF and rgb)
//                    background = AppCompatResources.getDrawable(context, R.color.ao)
                    val requestBuilder: RequestBuilder<Drawable> =
                        Glide.with(context)
                            .asDrawable()
                            .sizeMultiplier(0.1f)
                    Glide
                        .with(context)
                        .load(backstackCountBitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .thumbnail(requestBuilder)
                        .into(this@apply)
                }

            }
        }
        editTextView.apply {
            letterSpacing = 0.2f
            setFillColor(
                Color.parseColor(
                    fillColorStr
                )
            )
            setStrokeColor(
                Color.parseColor(
                    whiteColorStr
                )
            )
            text = overrideText
        }
    }

    private fun makeFirstTitleGradColorStrList(
        mustColor: String,
    ): List<String> {
//        val whiteColorStr = "#ffffff"
        val colorList = listOf(
            CmdClickColorStr.LIGHT_GREEN.str,
//            CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.BLUE.str,
            CmdClickColorStr.SKERLET.str,
            CmdClickColorStr.YELLOW.str,
            CmdClickColorStr.WHITE_GREEN.str,
            CmdClickColorStr.GREEN.str,
            CmdClickColorStr.YELLOW_GREEN.str,
            CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.WATER_BLUE.str,
            CmdClickColorStr.PURPLE.str,
            CmdClickColorStr.ORANGE.str,
            CmdClickColorStr.BROWN.str,
        )
        val color1 = colorList.random()
        val alreadyColorList: MutableList<String> = mutableListOf()
        alreadyColorList.add(color1)
        val color2 = let {
//            if(
//                !isWhiteBackground
//            ) return@let whiteColorStr
            colorList.filter{
                it != color1
            }.random()
        }
        alreadyColorList.add(color2)
        val color3 = let {
            colorList.filter {
                !alreadyColorList.contains(it)
            }.random()
        }
//        val aoColorStr = "#007F89"
        val useTitleColorListSrc = listOf(
            color1,
            color2,
            color3,
        )
        val useTitleColorList = useTitleColorListSrc.contains(mustColor).let {
                isUseAo ->
            if(
                isUseAo
            ) return@let useTitleColorListSrc
            val plusAoList =
                useTitleColorListSrc.shuffled().take(2) + listOf(mustColor)
            plusAoList.shuffled()
        }
        return listOf(
            useTitleColorList.get(0),
            useTitleColorList.get(1),
//            whiteColorStr,
            useTitleColorList.get(2),
        ) // Define your gradient colors
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

    private fun setTitleImage(
        fragment: Fragment,
        editTitleImageView: AppCompatImageView,
        fannelInfoMap: Map<String, String>,
        fillColorStr: String,
        titleImageMap: Map<String, String>,
    ){
//        val isNotSet = titleImageMap.get(
//            TitleImageSettingKey.VISIBLE.key
//        ) == switchOff
//        if(isNotSet) return
//        val fannelInfoMap =
//            fragment.fannelInfoMap

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
//        val binding = fragment.binding
//        val editTitleImageView = binding.editTitleImage
        FannelLogoSetter.setTitleFannelLogo(
            fragment,
            editTitleImageView,
//        currentAppDirPath: String,
            currentFannelName,
            fillColorStr,
        )
//        QrLogo(editFragment).setTitleFannelLogo(
//            editTitleImageView,
////            currentAppDirPath,
//            currentFannelName
//        )
    }
}

private object FannelLogoSetter {

    fun setTitleFannelLogo(
        fragment: Fragment,
        titleImageView: AppCompatImageView?,
//        currentAppDirPath: String,
        selectedScriptName: String,
        fillColorStr: String,
    ){
        val context = fragment.context
            ?: return
        if(
            titleImageView == null
        ) return
//        val fannelDirName = CcPathTool.makeFannelDirName(selectedScriptName)
        val logoPngPath = listOf(
            UsePath.fannelLogoPngPath,
        ).joinToString("/").let {
            ScriptPreWordReplacer.replace(
                it,
                selectedScriptName
            )
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
            val rectHeight = logoBitmap.height * 2
            val rectWidth = rectHeight * 5
            val transParentHex = (3..9).random().let {
                "#0${it}"
            }
            val updatedRectBitmap = withContext(Dispatchers.IO) {
                val rectBitmap = BitmapTool.ImageTransformer.makeRect(
//                    CmdClickColorStr.entries.random().str
                            fillColorStr.replace(
                        "#",
                        transParentHex,
                    ),
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
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(context)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(context)
                    .load(updatedRectBitmap)
                    .skipMemoryCache(true)
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