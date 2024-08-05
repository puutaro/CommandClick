package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy


object GlideTool {

    fun setImageOrGif(
        context: Context?,
        capturePngPathOrMacro: String,
//        byteArray: ByteArray?,
//        bitmapList: List<Bitmap?>?,
        imageView: AppCompatImageView
    ){
        if(
            context == null
//            || byteArray == null
        ) return
//        if(bitmapList.size == 1){
//            Glide
//                .with(context)
//                .load(bitmapList[0])
////                        .centerCrop()
//                .into(imageView)
//            return
//        }
//        val byteArray = BitmapTool.generateGIF(
//            bitmapList,
//        )
        val requestBuilder: RequestBuilder<Drawable> = Glide.with(imageView.context)
            .asDrawable().sizeMultiplier(0.1f)
        Glide
            .with(imageView.context)
            .load(capturePngPathOrMacro)
            .skipMemoryCache( true )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail( requestBuilder )
            .into(imageView)
    }
}