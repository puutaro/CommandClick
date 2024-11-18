package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object EditFrameMaker {


    private val valueSeparator = EditComponent.Template.valueSeparator

    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val imageKey = EditComponent.Template.EditComponentKey.IMAGE.key
    private val imagePropertyKey = EditComponent.Template.EditComponentKey.IMAGE_PROPERTY.key
    private val textKey = EditComponent.Template.EditComponentKey.TEXT.key
    private val textPropertyKey = EditComponent.Template.EditComponentKey.TEXT_PROPERTY.key
    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key


    private val imagePathsKey = EditComponent.Template.ImageManager.ImageKey.PATHS.key
    private val imageDelayKey = EditComponent.Template.ImageManager.ImageKey.DELAY.key
    private val imageTagKey = EditComponent.Template.ImagePropertyManager.PropertyKey.TAG.key
    private val imageColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.COLOR.key
    private val imageAlphaKey = EditComponent.Template.ImagePropertyManager.PropertyKey.ALPHA.key
    private val imageScaleKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE.key

    private val textSizeKey = EditComponent.Template.TextPropertyManager.Property.SIZE.key
    private val textTagKey = EditComponent.Template.TextPropertyManager.Property.TAG.key
    private val textColorKey = EditComponent.Template.TextPropertyManager.Property.COLOR.key
    private val strokeColorKey = EditComponent.Template.TextPropertyManager.Property.STROKE_COLOR.key
    private val strokeWidthKey = EditComponent.Template.TextPropertyManager.Property.STROKE_WIDTH.key
    private val textAlphaKey = EditComponent.Template.TextPropertyManager.Property.ALPHA.key
    private val textMaxLinesKey = EditComponent.Template.TextPropertyManager.Property.MAX_LINES.key
//    private val disableTextSelectKey = EditComponent.Template.TextPropertyManager.Property.DISABLE_TEXT_SELECT.key

    private val switchOn = EditComponent.Template.switchOn

    suspend fun make(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        frameKeyPairList: List<Pair<String, String>>?,
        width: Int,
        weight: Float?,
        tag: String?,
        isMarginZero: Boolean,
        totalSettingValMap: Map<String, String>?
    ): FrameLayout? {
        if(
            context == null
        ) return null
        val inflater = LayoutInflater.from(context)
        val buttonLayout = inflater.inflate(
            R.layout.icon_caption_layout,
            null
        ) as FrameLayout
        val height = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                heightKey,
            )?.let {
                try {
                    ScreenSizeCalculator.toDp(context, it.toInt())
                } catch(e: Exception){
                    null
                }
            } ?: ViewGroup.LayoutParams.MATCH_PARENT
//                ,ScreenSizeCalculator.toDp(context, 50)
        }
        tag?.let {
            buttonLayout.tag = it
        }
        val param = LinearLayoutCompat.LayoutParams(
            width,
            height,
        )
        weight?.let {
            param.weight = it
        }
        val marginDp = when(isMarginZero) {
            true -> 0
            else -> ScreenSizeCalculator.toDp(
                context,
                context.resources?.getDimension(R.dimen.toolbar_button_horizon_margin) ?: 0
            )
        }
        param.marginStart = marginDp
        param.marginEnd = marginDp
        param.gravity = Gravity.CENTER
        buttonLayout.layoutParams = param
        buttonLayout.foregroundGravity = Gravity.CENTER

        buttonLayout.findViewById<AppCompatImageView>(R.id.icon_caption_layout_image)?.let {
                imageButtonView ->
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
            setImageView(
                imageButtonView,
                imageMap,
                imagePropertyMap,
            )
        }
        buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)?.let {
                captionTextView ->
            val textMap = withContext(Dispatchers.IO) {
                PairListTool.getPair(
                    frameKeyPairList,
                    textKey,
                )?.let {
                    EditComponent.Template.TextManager.createTextMap(
                        it.second,
                        totalSettingValMap?.get(
                            tag
                        )
                    )
                }
            }
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
            setCaption(
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                captionTextView,
                textMap,
                textPropertyMap,
            )
        }
        return buttonLayout
    }

    private suspend fun setImageView(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        imagePropertyMap: Map<String, String>?,
    ) {
        val context = imageView.context
        val imagePathList = withContext(Dispatchers.IO) {
            imageMap?.get(
                imagePathsKey,
            )?.split(valueSeparator)
        }
        val delay = withContext(Dispatchers.IO) {
            imageMap?.get(
                imageDelayKey,
            )?.let {
                try {
                    it.toInt()
                } catch(e: Exception){
                    null
                }
            } ?: 800
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
        val imageTag = withContext(Dispatchers.IO) {
            imagePropertyMap?.get(
                imageTagKey,
            )
        }
        val imageColor = withContext(Dispatchers.IO) {
            imagePropertyMap?.get(
                imageColorKey,
            )?.let {
                colorStr ->
                CmdClickColor.values().firstOrNull {
                    it.str == colorStr
                }
            }
        }
        val imageScale = withContext(Dispatchers.IO) {
            imagePropertyMap?.get(
                imageScaleKey,
            ).let {
                    scale ->
                EditComponent.Template.ImagePropertyManager.ImageScale.values().firstOrNull {
                    it.str == scale
                } ?: EditComponent.Template.ImagePropertyManager.ImageScale.FIT_CENTER
            }
        }
        imageView.isVisible = !imagePathList.isNullOrEmpty()
        if(
            imagePathList.isNullOrEmpty()
        ) {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "licon0.txt").absolutePath,
//                    listOf(
//                        "imageTag: ${imageTag}",
//                    ).joinToString("\n")
//                )
            return
        }
        when(imageColor == null){
            true -> imageView.imageTintList = null
            else -> imageView.imageTintList = AppCompatResources.getColorStateList(
                context,
                imageColor.id
            )
        }
        imageTag?.let {
            imageView.tag = imageTag
        }
        imageAlpha?.let {
            imageView.alpha = imageAlpha
        }
        imageView.scaleType = imageScale.scale

        when(imagePathList.size == 1){
            false -> {
                execSetMultipleImage(
                    imageView,
                    imagePathList,
                    delay,
                )
            }
            else -> {
                execSetSingleImage(
                    imageView,
                    imagePathList.firstOrNull()
                )
            }
        }
    }

    private suspend fun execSetSingleImage(
        imageView: AppCompatImageView,
        imagePathSrc: String?,
    ){
        if(
            imagePathSrc.isNullOrEmpty()
        ) return
        val imageViewContext = imageView.context
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(imageViewContext)
                .asDrawable()
                .sizeMultiplier(0.1f)
        val imagePathToIconType = EditComponent.Template.ImageManager.makeIconAndTypePair(
            imagePathSrc
        )
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
            Glide.with(imageViewContext)
                .load(imagePathSrc)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageView)
            return
        }
        val iconType = imagePathToIconType.second.let {
                iconTypeStr ->
            EditComponent.Template.ImageManager.IconType.values().firstOrNull {
                it.type == iconTypeStr
            } ?: EditComponent.Template.ImageManager.IconType.IMAGE
        }
//        val iconId = icon?.id
//        imageView.setAutofillHints(CmdClickIcons.values().firstOrNull {
//            it.id == iconId
//        }?.str)
        val assetsPath = CmdClickIcons.values().firstOrNull {
            it.str == imagePath
        }?.assetsPath ?: return
        val bitmap = when(iconType) {
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
        if(bitmap == null){
            imageView.isVisible = false
            return
        }
        Glide.with(imageViewContext)
            .load(bitmap)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail(requestBuilder)
            .into(imageView)
    }

    private fun execSetMultipleImage(
        imageView: AppCompatImageView,
        imagePathList: List<String>,
        delay: Int,
    ){
        val imageViewContext = imageView.context
        val animationDrawable = AnimationDrawable()
        val bitmapList = imagePathList.map {
            imagePathSrc ->
            if(File(imagePathSrc).isFile) {
                return@map BitmapTool.convertFileToBitmap(imagePathSrc)
            }
            val imagePathToIconType = EditComponent.Template.ImageManager.makeIconAndTypePair(
                imagePathSrc
            )
            val imagePath =
                imagePathToIconType.first
            val iconType = imagePathToIconType.second.let {
                    iconTypeStr ->
                EditComponent.Template.ImageManager.IconType.values().firstOrNull {
                    it.type == iconTypeStr
                } ?: EditComponent.Template.ImageManager.IconType.IMAGE
            }
            val assetsPath = CmdClickIcons.values().firstOrNull {
                it.str == imagePath
            }?.assetsPath ?: return@map null
            when(iconType) {
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
    ) {
        val settingValue = textMap?.get(
            EditComponent.Template.TextManager.TextKey.SETTING_VALUE.key
        )
        val text = withContext(Dispatchers.IO) {
            EditComponent.Template.TextManager.makeText(
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                textMap,
                settingValue
            )
        }
        val textTag = withContext(Dispatchers.IO) {
            textPropertyMap?.get(
                textTagKey,
            )
        }
        val textSize = withContext(Dispatchers.IO) {
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
        val textColorStr = withContext(Dispatchers.IO) {
            textPropertyMap?.get(
                textColorKey,
            )
        }
        val strokeColorStr = withContext(Dispatchers.IO) {
            textPropertyMap?.get(
                strokeColorKey,
            )
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
        val maxLines = withContext(Dispatchers.IO){
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
//        val enableTextSelect = withContext(Dispatchers.IO){
//            textPropertyMap?.get(
//                disableTextSelectKey
//            ) != switchOn
//        }
        withContext(Dispatchers.Main) {
//            textTag?.let {
                captionTextView.tag = textTag
//            }
            captionTextView.setAutofillHints(settingValue)
//        captionTextView.autofillHints?.firstOrNull(0)
//        captionTextView.hint = settingValue
            captionTextView.text = text
            captionTextView.maxLines = maxLines
//            captionTextView.setTextIsSelectable(enableTextSelect)
            CmdClickColor.values().firstOrNull {
                it.str == textColorStr
            }?.let {
                captionTextView.setFillColor(it.id)
            } ?: let {
                captionTextView.setFillColor(R.color.fill_gray)
            }
            CmdClickColor.values().firstOrNull {
                it.str == strokeColorStr
            }?.let {
                captionTextView.setStrokeColor(it.id)
            } ?: let {
                captionTextView.setStrokeColor(R.color.white)
            }
            strokeWidth?.let {
                captionTextView.outlineWidthSrc = it
            } ?: let {
                captionTextView.outlineWidthSrc = 2
            }
            textSize?.let {
                captionTextView.textSize = textSize
            }
            textAlpha?.let {
                captionTextView.alpha = textAlpha
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