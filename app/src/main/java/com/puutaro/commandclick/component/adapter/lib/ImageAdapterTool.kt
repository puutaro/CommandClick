package com.puutaro.commandclick.component.adapter.lib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.puutaro.commandclick.common.variable.UsePath
import java.io.File
import kotlin.math.roundToInt

object ImageAdapterTool {

    private val pdfExtend = UsePath.pdfExtend


    fun makeFileMarkBitMap(
        context: Context?,
        assetsRelativePath: String
    ): Bitmap {
        val assetManager = context?.assets
        val fileMarkbitmap = BitmapFactory.decodeStream(
            assetManager?.open(
                assetsRelativePath
            )
        )
        return fileMarkbitmap
    }

    fun decodeSampledBitmapFromUri(
        path: String?,
        reqWidth: Int,
        reqHeight: Int,
        pdfImagePngBitMap: Bitmap,
        textImagePngBitMap: Bitmap,
    ): Bitmap? {
        if (
            path.isNullOrEmpty()
        ) return null
        if (
            !File(path).isFile
        ) return null
        var bm: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(path, options)
        return when (bm) {
            null -> judgePdfOrOther(
                path,
                pdfImagePngBitMap,
                textImagePngBitMap,
            )
            else -> bm
        }
    }

    private fun judgePdfOrOther(
        path: String,
        pdfImagePngBitMap: Bitmap,
        textImagePngBitMap: Bitmap,
    ): Bitmap {
        val onPdf = path.endsWith(pdfExtend)
        return when (onPdf) {
            true -> pdfImagePngBitMap
            else -> textImagePngBitMap
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                (height.toFloat() / reqHeight.toFloat()).roundToInt()
            } else {
                (width.toFloat() / reqWidth.toFloat()).roundToInt()
            }
        }
        return inSampleSize
    }
}