package com.puutaro.commandclick.proccess.list_index_for_edit

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object EditFrameMaker {

    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val imageTagKey = EditComponent.Template.EditComponentKey.IMAGE_TAG.key
    private val textKey = EditComponent.Template.EditComponentKey.TEXT.key
    private val textPropertyKey = EditComponent.Template.EditComponentKey.TEXT_PROPERTY.key
    private val imagePathKey = EditComponent.Template.EditComponentKey.IMAGE_PATH.key
    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
    private val imageAlphaKey = EditComponent.Template.EditComponentKey.IMAGE_ALPHA.key
    private val imageScaleKey = EditComponent.Template.EditComponentKey.IMAGE_SCALE.key

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
            val imageTag = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    imageTagKey,
                )
            }
            val imagePath = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    imagePathKey,
                )
            }
            val imageAlpha = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
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
                PairListTool.getValue(
                    frameKeyPairList,
                    imageScaleKey,
                ).let {
                        scale ->
                    EditComponent.Template.ImageScale.values().firstOrNull {
                        it.str == scale
                    } ?: EditComponent.Template.ImageScale.FIT_CENTER
                }
            }
            setImageView(
                imageButtonView,
                imageTag,
                imagePath,
                imageAlpha,
                imageScale,
            )
        }
        buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)?.let {
                captionTextView ->
//            val labelTag = withContext(Dispatchers.IO) {
//                PairListTool.getValue(
//                    frameKeyPairList,
//                    textTagKey,
//                )
//            }
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
                    EditComponent.Template.TextPropertyManager.makeTextPropertyMap(
                        it.second,
                    )
                }
            }
            setCaption(
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                captionTextView,
//                labelTag,
                textMap,
                textPropertyMap,
//                textSize,
//                textColorStr,
//                strokeColorStr,
//                strokeWidth,
//                textAlpha,
//                maxLines,
            )
        }
        return buttonLayout
    }

    suspend fun setImageView(
        imageView: AppCompatImageView,
        imageTag: String?,
        imagePath: String?,
        imageAlpha: Float?,
        imageScale: EditComponent.Template.ImageScale
    ) {
        imageView.isVisible = !imagePath.isNullOrEmpty()
        if(
            imagePath.isNullOrEmpty()
        ) {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "licon0.txt").absolutePath,
//                    listOf(
//                        "imageTag: ${imageTag}",
//                    ).joinToString("\n")
//                )
            return
        }
        imageView.imageTintList = null
        imageTag?.let {
            imageView.tag = imageTag
        }
        imageAlpha?.let {
            imageView.alpha = imageAlpha
        }
        imageView.scaleType = imageScale.scale
        val imageViewContext = imageView.context

        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(imageViewContext)
                .asDrawable()
                .sizeMultiplier(0.1f)
        val icon = CmdClickIcons.values().firstOrNull {
            it.str == imagePath
        }
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, "licon.txt").absolutePath,
                listOf(
                    "imagePath: ${imagePath}",
                    "icon: ${icon?.str}",
                ).joinToString("\n\n")
            )
        if(icon == null && File(imagePath).isFile){
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, "liconImagePath.txt").absolutePath,
                listOf(
                    "imagePath: ${imagePath}",
                    "icon: ${icon?.str}",
                ).joinToString("\n\n")
            )
            Glide.with(imageViewContext)
                .load(imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageView)
            return
        }
        val iconId = icon?.id
        imageView.tag = CmdClickIcons.values().firstOrNull {
            it.id == iconId
        }?.str

        val isImageFile =
            ExecSetToolbarButtonImage.isImageFile(icon?.assetsPath)
        when(true) {
            (isImageFile && icon != null) ->
                ExecSetToolbarButtonImage.setImageButton(
                    imageView,
                    icon
                )
            else -> {
                imageView.isVisible = false
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "licon1.txt").absolutePath,
//                        listOf(
//                            "imageTag: ${imageTag}",
//                            "imagePath: ${imagePath}",
//                        ).joinToString("\n")
//                    )
            }
//                Glide.with(imageViewContext)
//                    .load(iconId)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .thumbnail(requestBuilder)
//                    .into(imageView)
        }
    }

    private suspend fun setCaption(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        captionTextView: OutlineTextView,
//        textTag: String?,
        textMap: Map<String, String>?,
        textPropertyMap: Map<String, String>?,
//        textSize: Float?,
//        textColorStr: String?,
//        strokeColorStr: String?,
//        strokeWidth: Int?,
//        textAlpha: Float?,
//        maxLines: Int,
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
            textTag?.let {
                captionTextView.tag = textTag
            }
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
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, "lcapt.txt").absolutePath,
            listOf(
                "textMap: ${textMap}",
                "textPropertyMap: ${textPropertyMap}",
                "strokeWidth: ${strokeWidth}",
                "captionTextView.outlineWidthSrc: ${captionTextView.outlineWidthSrc}",

            ).joinToString("\n")
        )
    }
}