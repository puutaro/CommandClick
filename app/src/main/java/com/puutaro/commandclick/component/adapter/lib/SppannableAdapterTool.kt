package com.puutaro.commandclick.component.adapter.lib

import android.graphics.Bitmap
import android.text.Spannable
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bachors.img2ascii.Img2Ascii
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator

object SppannableAdapterTool {

    fun setSpannableView(
        fragment: Fragment,
        spannableView: TextView?,
        imagePath: String,
        textImagePngBitMap: Bitmap,
        pdfImagePngBitMap: Bitmap,
    ): TextView? {
        val beforeResizeBitMap = ImageAdapterTool.decodeSampledBitmapFromUri(
            imagePath,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            pdfImagePngBitMap,
            textImagePngBitMap,
        ) as Bitmap
        val baseWidth = (ScreenSizeCalculator.dpWidth(fragment) * 50) / 100
        val resizeScale: Double =
            (baseWidth / beforeResizeBitMap.width).toDouble()
        val bitMap = Bitmap.createScaledBitmap(
            beforeResizeBitMap,
            (beforeResizeBitMap.width * resizeScale).toInt(),
            (beforeResizeBitMap.height * resizeScale).toInt(),
            true
        )
        Img2Ascii()
            .bitmap(bitMap)
            .quality(5) // 1 - 5
            .color(true)
            .convert(object : Img2Ascii.Listener {
                override fun onProgress(percentage: Int) {
//                                    textView.setText("$percentage %")
                }

                override fun onResponse(text: Spannable) {
                    spannableView?.text = text
                }
            })
        return spannableView
    }
}