package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
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
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object EditFrameMaker {


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

    private val imageKey = EditComponent.Template.EditComponentKey.IMAGE.key
    private val imagePropertyKey = EditComponent.Template.EditComponentKey.IMAGE_PROPERTY.key
    private val textKey = EditComponent.Template.EditComponentKey.TEXT.key
    private val textPropertyKey = EditComponent.Template.EditComponentKey.TEXT_PROPERTY.key
    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
    private val widthKey = EditComponent.Template.EditComponentKey.WIDTH.key
    private val weightKey = EditComponent.Template.EditComponentKey.WEIGHT.key


    private val imagePathsKey = EditComponent.Template.ImageManager.ImageKey.PATHS.key
    private val imageDelayKey = EditComponent.Template.ImageManager.ImageKey.DELAY.key
    private val imageTagKey = EditComponent.Template.ImagePropertyManager.PropertyKey.TAG.key
    private val imageColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.COLOR.key
    private val imageBkColorKey = EditComponent.Template.ImagePropertyManager.PropertyKey.BK_COLOR.key
    private val imageAlphaKey = EditComponent.Template.ImagePropertyManager.PropertyKey.ALPHA.key
    private val imageScaleKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE.key
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

    private val textSizeKey = EditComponent.Template.TextPropertyManager.Property.SIZE.key
    private val textBkColorKey = EditComponent.Template.TextPropertyManager.Property.BK_COLOR.key
    private val textStyleKey = EditComponent.Template.TextPropertyManager.Property.STYLE.key
    private val textFontKey = EditComponent.Template.TextPropertyManager.Property.FONT.key
    private val textTagKey = EditComponent.Template.TextPropertyManager.Property.TAG.key
    private val textGravityKey = EditComponent.Template.TextPropertyManager.Property.GRAVITI.key
    private val textWidthKey = EditComponent.Template.TextPropertyManager.Property.WIDTH.key
    private val textHeightKey = EditComponent.Template.TextPropertyManager.Property.HEIGHT.key
    private val textColorKey = EditComponent.Template.TextPropertyManager.Property.COLOR.key
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

    suspend fun make(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        frameKeyPairList: List<Pair<String, String>>?,
        width: Int,
        firstWeight: Float?,
        overrideTag: String?,
//        isMarginZero: Boolean,
        totalSettingValMap: Map<String, String>?
    ): FrameLayout? {
        if(
            context == null
        ) return null
        val inflater = LayoutInflater.from(context)
        val buttonLayout = inflater.inflate(
            R.layout.icon_caption_layout_for_edit_list,
            null
        ) as FrameLayout
        val height = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                frameKeyPairList,
                heightKey,
            ).let {
                EditComponent.Template.LinearLayoutUpdater.convertHeight(
                    context,
                    it,
                    ViewGroup.LayoutParams.MATCH_PARENT,
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
                    context,
                    it,
                    width,
                )

            }
//                ,ScreenSizeCalculator.toDp(context, 50)
        }
        val param = LinearLayoutCompat.LayoutParams(
            overrideWidth,
            height,
        )
        val overrideWeight = PairListTool.getValue(
            frameKeyPairList,
            weightKey
        )?.let {
            try{
                it.toFloat()
            } catch (e: Exception){
                null
            }
        } ?: firstWeight ?: param.weight
//        val marginDp = when(isMarginZero) {
//            true -> 0
//            else -> context.resources?.getDimension(R.dimen.toolbar_button_horizon_margin)?.toInt() ?: 0
////            ScreenSizeCalculator.toDp(
////                context,
////                context.resources?.getDimension(R.dimen.toolbar_button_horizon_margin) ?: 0
////            )
//        }
        val marginData = EditComponent.Template.MarginData(
            context,
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
        )
        val paddingData = EditComponent.Template.PaddingData(
            context,
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
        )
        param.apply {
            weight = overrideWeight
            topMargin = marginData.marginTop
            marginStart = marginData.marginStart
            marginEnd = marginData.marginEnd
            bottomMargin = marginData.marginBottom
            gravity = Gravity.CENTER
        }
        buttonLayout.apply {
            tag = overrideTag
            setPadding(
                paddingData.paddingStart,
                paddingData.paddingTop,
                paddingData.paddingEnd,
                paddingData.paddingBottom,
            )
            layoutParams = param
            foregroundGravity = Gravity.CENTER
        }

        buttonLayout.findViewById<AppCompatImageView>(R.id.icon_caption_for_edit_image)?.let {
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
        buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_for_edit_caption)?.let {
                captionTextView ->
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
        imageView.layoutParams = imageView.layoutParams.apply {
            val curLayoutParams = this as FrameLayout.LayoutParams
            curLayoutParams.apply setParam@ {
                val overrideWidth = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageWidthKey,
                    ).let {
                        EditComponent.Template.LinearLayoutUpdater.convertWidth(
                            context,
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        )
                    }
                }
                width = overrideWidth
                val overrideHeight = withContext(Dispatchers.IO) {
                    imagePropertyMap?.get(
                        imageHeightKey,
                    ).let {
                        EditComponent.Template.LinearLayoutUpdater.convertHeight(
                            context,
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        )
                    }
                }
                height = overrideHeight
                val overrideGravity = imagePropertyMap?.get(
                    imageGravityKey,
                )?.let {
                        gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                } ?: Gravity.CENTER
                gravity = overrideGravity
                val marginData = EditComponent.Template.MarginData(
                    context,
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
                )
                topMargin = marginData.marginTop
                bottomMargin = marginData.marginBottom
                marginStart = marginData.marginStart
                marginEnd = marginData.marginEnd
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
            val paddingData = EditComponent.Template.PaddingData(
                context,
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
            )
            setPadding(
                paddingData.paddingStart,
                paddingData.paddingTop,
                paddingData.paddingEnd,
                paddingData.paddingBottom,
            )
            isVisible = !imagePathList.isNullOrEmpty()
        }
        if(
            imagePathList.isNullOrEmpty()
        ) {
            imageView.setImageDrawable(null)
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "licon0.txt").absolutePath,
//                    listOf(
//                        "imageTag: ${imageTag}",
//                    ).joinToString("\n")
//                )
            return
        }
        imageView.apply {
            val imageColor = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageColorKey,
                )?.let {
                        colorStr ->
                    CmdClickColor.entries.firstOrNull {
                        it.str == colorStr
                    }
                }
            }
            imageTintList = imageColor?.let {
                AppCompatResources.getColorStateList(
                    context,
                    it.id
                )
            }
            val imageBkColor = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageBkColorKey,
                )?.let {
                        colorStr ->
                    CmdClickColor.entries.firstOrNull {
                        it.str == colorStr
                    }
                }
            }
            background = imageBkColor?.let {
                AppCompatResources.getDrawable(
                    context,
                    it.id
                )
            }
            val imageTag = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageTagKey,
                )
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
            tag = imageTag
            alpha = imageAlpha ?: 1f
            scaleType = imageScale.scale
        }

        when(imagePathList.size == 1){
            false -> {
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

    suspend fun setImageViewForDynamic(
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        imagePropertyMap: Map<String, String>?,
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
                            context,
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
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
                            context,
                            it,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        )
                    }
                }
                overrideHeight?.let {
                    height = it
                }
                val overrideGravity = imagePropertyMap?.get(
                    imageGravityKey,
                )?.let {
                        gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }
                overrideGravity?.let {
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
            val imageColor = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageColorKey,
                )?.let {
                        colorStr ->
                    CmdClickColor.entries.firstOrNull {
                        it.str == colorStr
                    }
                }
            }
            imageColor?.let {
                imageTintList = AppCompatResources.getColorStateList(
                    context,
                    it.id
                )
            }
            val imageBkColor = withContext(Dispatchers.IO) {
                imagePropertyMap?.get(
                    imageBkColorKey,
                )?.let {
                        colorStr ->
                    CmdClickColor.entries.firstOrNull {
                        it.str == colorStr
                    }
                }
            }
            imageBkColor?.let {
                background =
                    AppCompatResources.getDrawable(
                        context,
                        it.id
                    )
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
        imageView.isVisible = true
        when(imagePathList.size == 1){
            false -> {
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
            EditComponent.Template.ImageManager.IconType.entries.firstOrNull {
                it.type == iconTypeStr
            } ?: EditComponent.Template.ImageManager.IconType.IMAGE
        }
//        val iconId = icon?.id
//        imageView.setAutofillHints(CmdClickIcons.values().firstOrNull {
//            it.id == iconId
//        }?.str)
        val assetsPath = CmdClickIcons.entries.firstOrNull {
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
                EditComponent.Template.ImageManager.IconType.entries.firstOrNull {
                    it.type == iconTypeStr
                } ?: EditComponent.Template.ImageManager.IconType.IMAGE
            }
            val assetsPath = CmdClickIcons.entries.firstOrNull {
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
                    val overrideGravity = textPropertyMap?.get(
                        textGravityKey,
                    )?.let {
                            gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    } ?: Gravity.CENTER
                    gravity = overrideGravity
                    val overrideWidth = withContext(Dispatchers.IO) {
                        textPropertyMap?.get(
                            textWidthKey,
                        ).let {
                            EditComponent.Template.LinearLayoutUpdater.convertWidth(
                                textViewContext,
                                it,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                            )
                        }
                    }
                    width = overrideWidth
                    val overrideHeight = withContext(Dispatchers.IO) {
                        textPropertyMap?.get(
                            textHeightKey,
                        ).let {
                            EditComponent.Template.LinearLayoutUpdater.convertHeight(
                                textViewContext,
                                it,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                            )
                        }
                    }
                    height = overrideHeight
                    val marginData = EditComponent.Template.MarginData(
                        textViewContext,
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
                    )
                    topMargin = marginData.marginTop
                    bottomMargin = marginData.marginBottom
                    marginStart = marginData.marginStart
                    marginEnd = marginData.marginEnd
                }
                val paddingData = EditComponent.Template.PaddingData(
                    textViewContext,
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
                )
                setPadding(
                    paddingData.paddingStart,
                    paddingData.paddingTop,
                    paddingData.paddingEnd,
                    paddingData.paddingBottom,
                )
                val textTag = withContext(Dispatchers.IO) {
                    textPropertyMap?.get(
                        textTagKey,
                    )
                }
                tag = textTag
                val settingValue = textMap?.get(
                    EditComponent.Template.TextManager.TextKey.SETTING_VALUE.key
                )
                setAutofillHints(settingValue)
//        captionTextView.autofillHints?.firstOrNull(0)
//        captionTextView.hint = settingValue
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
                } ?: EditComponent.Template.TextPropertyManager.Font.DEFAULT
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
                    val overrideGravity = textPropertyMap?.get(
                        textGravityKey,
                    )?.let {
                            gravityStr ->
                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                            it.key == gravityStr
                        }?.gravity
                    }
                    overrideGravity?.let {
                        gravity = it
                    }
                }
//        captionTextView.autofillHints?.firstOrNull(0)
//        captionTextView.hint = settingValue
                overrideText?.let {
                    text = it
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
}