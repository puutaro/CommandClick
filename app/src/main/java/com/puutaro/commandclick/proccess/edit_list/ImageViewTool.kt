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
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ImageViewTool {

    private val valueSeparator = EditComponent.Template.valueSeparator

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
    private val imagePaddingTopKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_TOP.key
    private val imagePaddingStartKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_START.key
    private val imagePaddingEndKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_END.key
    private val imagePaddingBottomKey = EditComponent.Template.ImageManager.PropertyKey.PADDING_BOTTOM.key
    private val imageVisibleKey = EditComponent.Template.ImageManager.PropertyKey.VISIBLE.key
    private val imageRotateKey = EditComponent.Template.ImageManager.PropertyKey.ROTATE.key
    private val imageScaleXKey = EditComponent.Template.ImageManager.PropertyKey.SCALE_X.key
    private val imageScaleYKey = EditComponent.Template.ImageManager.PropertyKey.SCALE_Y.key

    suspend fun setVisibility(
        imageView: AppCompatImageView?,
        imageMap: Map<String, String>?,
    ){
        imageView?.apply {
            withContext(Dispatchers.IO) {
                imageMap?.get(
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
        }
    }
    suspend fun set(
        imageView: AppCompatImageView?,
        imageMap: Map<String, String>?,
        defaultGravity: Int?,
        defaultScale: ImageView.ScaleType?,
        enableImageViewClick: Boolean?,
        outValue: TypedValue?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        where: String,
    ){
        if(
            imageMap.isNullOrEmpty()
        ) return
        val imageViewContext =
            imageView?.context
                ?: return
        val requestBuilder: RequestBuilder<Drawable> =
            requestBuilderSrc ?: Glide.with(imageViewContext)
                .asDrawable()
                .sizeMultiplier(0.1f)
        imageView.apply {
            CoroutineScope(Dispatchers.Main).launch {
                val imagePathList = withContext(Dispatchers.IO) {
                    imageMap.get(
                        imagePathsKey,
                    )?.split(valueSeparator)
                }
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
                            requestBuilder,
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
}