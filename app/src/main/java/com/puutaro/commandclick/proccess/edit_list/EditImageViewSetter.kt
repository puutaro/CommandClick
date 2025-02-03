package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.RequestBuilder

object EditImageViewSetter {
    suspend fun setForConstraint(
        context: Context?,
        tagIdMap: Map<String, Int>?,
        imageView: AppCompatImageView?,
        contentsKeyPairList: List<Pair<String, String>>?,
        width: Int,
        enableClick: Boolean,
        outValue: TypedValue?,
        requestBuilderSrc: RequestBuilder<Drawable>?,
        density: Float,
        whereForErr: String,
    ){
        if(
            imageView == null
        ) return
        imageView.layoutParams = ConstraintTool.setConstraintParam(
            imageView.layoutParams as ConstraintLayout.LayoutParams,
            tagIdMap,
            contentsKeyPairList,
            width,
            0,
            density,
        ).let {
                param ->
            LayoutSetterTool.setMargin(
                param,
                contentsKeyPairList?.toMap(),
                density,
            )
            param
        }
        val contentsMap =
            contentsKeyPairList?.toMap()
        ImageViewTool.setVisibility(
            imageView,
            contentsMap
        )
        ImageViewTool.set(
            imageView,
            contentsMap,
        null,
            ImageView.ScaleType.FIT_CENTER,
            enableClick,
            outValue,
            requestBuilderSrc,
            density,
            whereForErr
        )
    }
}