package com.puutaro.commandclick.proccess.edit_list

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object EditConstraintFrameMaker {

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
}