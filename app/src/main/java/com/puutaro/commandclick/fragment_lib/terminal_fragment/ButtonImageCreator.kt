package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File


object ButtonImageCreator {

    private
    var buttonImageCreateJob: Job? = null

    fun exit(){
        buttonImageCreateJob?.cancel()
    }

    fun create(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        val toolbarUrlImageDirPath = UrlHistoryPath.toolbarUrlImageDirPath
        buttonImageCreateJob = terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {

//                withContext(Dispatchers.IO){
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "image.txt").absolutePath,
//                        "start ${LocalDateTime.now()}"
//                    )
//                }
                val capturePartPngDirPathList = withContext(Dispatchers.IO) {
                    makeCapturePartPngDirPathList()
                }

                val defaultUrlCapBitmap = withContext(Dispatchers.IO){
                    AssetsFileManager.assetsByteArray(
                        context,
                        AssetsFileManager.firstUrlCapPngPath
                    )?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                }
                withContext(Dispatchers.IO) {
                    val jobList = CmdClickIcons.values().map {
                        it.assetsPath
                    }.map { assetsPath ->
                        async {
                            semaphore.withPermit {
                                val fileName = File(assetsPath).name
                                val toolbarButtonImageFile = File(toolbarUrlImageDirPath, fileName)

                                if (
                                    toolbarButtonImageFile.isFile
                                ) {
                                    if (
                                        (1..4).random() % 4 <= 2
                                    ) return@async
                                }
                                val byteArray = cropImage(
                                    context,
                                    assetsPath,
                                    capturePartPngDirPathList,
                                    defaultUrlCapBitmap,
                                ) ?: return@async

                                FileSystems.writeFromByteArray(
                                    toolbarButtonImageFile.absolutePath,
                                    byteArray
                                )
                            }
                        }
                    }
                    jobList.forEach { it.await() }
                }
                withContext(Dispatchers.Main) {
                    val listener = context as? TerminalFragment.OnSetToolbarButtonImageListener
                        ?: return@withContext
                    listener.onSetToolbarButtonImage()
                }
//                withContext(Dispatchers.IO){
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "image.txt").absolutePath,
//                        "end ${LocalDateTime.now()}"
//                    )
//                }
            }
        }
    }

    private suspend fun cropImage(
        context: Context?,
        maskAssetsPath: String,
        captureDirList: List<String>,
        defaultUrlCapBitmap: Bitmap?,
    ): ByteArray? {
        val originalImagePath = captureDirList.shuffled().firstOrNull()?.let {
                dirPath ->
            if(
                dirPath.isEmpty()
            ) return@let null
            FileSystems.sortedFiles(dirPath).firstOrNull()?.let fileList@ {
                if(
                    it.isEmpty()
                ) return@fileList null
                File(dirPath, it).absolutePath
            }
        }
        val original = withContext(Dispatchers.IO) {
            when(originalImagePath.isNullOrEmpty()){
                true -> defaultUrlCapBitmap
                else -> BitmapTool.convertFileToBitmap(
                    originalImagePath
                )
            }?.let  {
                cutOriginal(
                    BitmapTool.resizeByMaxHeight(
                        it,
                        300.0
                    )
                )
            }
        } ?: return null
        val maskByteArray = withContext(Dispatchers.IO) {
            AssetsFileManager.assetsByteArray(
                context,
                maskAssetsPath
            )
        }?: return null
        val src = withContext(Dispatchers.IO) {
            BitmapFactory.decodeByteArray(
                maskByteArray,
                0,
                maskByteArray.size
            )
        }
        val output = withContext(Dispatchers.IO) {
            Bitmap.createBitmap(
                src.width,
                src.height,
                Bitmap.Config.ARGB_8888
            )
        }
        val outBitmap = withContext(Dispatchers.IO) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            val canvas = Canvas(output)
            canvas.drawBitmap(original, 0f, 0f, null)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = -0x1000000
            canvas.drawBitmap(src, 0f, 0f, paint)
            return@withContext output
        }
        val cornerDips = (2..8).random()
//        val borderDips = (0..3).random()
//        val borderFrameBitmap = drawBorderFrame(
//            context,
//            original,
//            lightColorList.random(),
//            cornerDips,
//            borderDips,
//        )
        val bkBitmapSrc = makeGradientBitmap2(
            original.width,
            original.height,
        )
//            .let {
//            BitmapTool.convertBitmapToByteArray(it)
//        }
//        val bkBitmap = overlayBitmap(
//            borderFrameBitmap,
//            bkBitmapSrc
//        ) ?: return null
         val overBitmap = overlayBitmap(
             bkBitmapSrc,
//             BitmapFactory.decodeByteArray(bkByteArray, 0, bkByteArray.size),
             outBitmap,
        ) ?: return null
        val resultBitmap = getRoundedCornerBitmap(
            context,
            overBitmap,
            cornerDips,
        ) ?: return null
//        getRoundedBitmap(overBitmap)
//            ?: return null
        return BitmapTool.convertBitmapToByteArray(resultBitmap)
    }

    private fun makeCapturePartPngDirPathList(): List<String> {
        val lastModifyExtend = UrlHistoryPath.lastModifyExtend
        val partPngDirName = UrlHistoryPath.partPngDirName
        val captureDirPath = UrlHistoryPath.makeCaptureHistoryDirPath()
        return FileSystems.sortedFiles(
            captureDirPath
        ).map {
            val dirName = it.removeSuffix(lastModifyExtend)
            listOf(
                captureDirPath,
                dirName,
                partPngDirName
            ).joinToString("/")
        }
    }

    private fun cutOriginal(
        bitmap: Bitmap
    ): Bitmap {
        // Set some constants
        val srcWidth = bitmap.width
        val srcHeight = bitmap.height
        val widthPx = 150
        val heightPx = 150
        val startX = (0..(srcWidth - widthPx)).random()
        val startY = (0..(srcHeight - heightPx)).random()

// Crop bitmap
       return Bitmap.createBitmap(bitmap, startX, startY, widthPx, heightPx, null, false)
    }


    private fun overlayBitmap(bitmapBackground: Bitmap, bitmapImage: Bitmap): Bitmap? {
        val bitmap2Width = bitmapImage.width
        val bitmap2Height = bitmapImage.height
        val marginLeft = (bitmapBackground.width * 0.5 - bitmap2Width * 0.5).toFloat()
        val marginTop = (bitmapBackground.height * 0.5 - bitmap2Height * 0.5).toFloat()
        val overlayBitmap =
            Bitmap.createBitmap(bitmap2Width, bitmap2Height, bitmapBackground.config)
        val canvas = Canvas(overlayBitmap)
        canvas.drawBitmap(bitmapBackground, Matrix(), null)
        canvas.drawBitmap(bitmapImage, marginLeft, marginTop, null)
        return overlayBitmap
    }

//    private fun getRoundedBitmap(bitmap: Bitmap): Bitmap? {
//        val resultBitmap: Bitmap
//        val originalWidth = bitmap.width
//        val originalHeight = bitmap.height
//        val r: Float
//        if (originalWidth > originalHeight) {
//            resultBitmap = Bitmap.createBitmap(
//                originalHeight, originalHeight,
//                Bitmap.Config.ARGB_8888
//            )
//            r = (originalHeight / 2).toFloat()
//        } else {
//            resultBitmap = Bitmap.createBitmap(
//                originalWidth, originalWidth,
//                Bitmap.Config.ARGB_8888
//            )
//            r = (originalWidth / 2).toFloat()
//        }
//        val canvas = Canvas(resultBitmap)
//        val paint = Paint()
//        val rect = Rect(
//            0,
//            0, originalWidth, originalHeight
//        )
//        paint.isAntiAlias = true
//        canvas.drawARGB(
//            0, 0,
//            0, 0
//        )
//        val radias = (r * 12) / 10
//        canvas.drawCircle(r, r, radias, paint)
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawBitmap(bitmap, rect, rect, paint)
//        return resultBitmap
//    }

    private fun getRoundedCornerBitmap(
        context: Context?,
        bitmap: Bitmap,
        cornerDips: Int,
    ): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val cornerSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, cornerDips.toFloat(),
            context?.resources?.displayMetrics
        ).toInt()
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        // prepare canvas for transfer
        paint.isAntiAlias = true
        paint.color = -0x1
        paint.style = Paint.Style.FILL
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, cornerSizePx.toFloat(), cornerSizePx.toFloat(), paint)

        // draw bitmap
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }


//    private fun drawBorderFrame(
//        context: Context?,
//        bitmap: Bitmap,
//        color: String,
//        cornerDips: Int,
//        borderDips: Int,
//    ): Bitmap {
//        val rectRndList = (1..5)
//        val rectPlusWidth = rectRndList.random() - rectRndList.random()
//        val rectPlusHeight = rectRndList.random() - rectRndList.random()
//        val bWidth = bitmap.width + rectPlusWidth
//        val bHeight = bitmap.height + rectPlusHeight
//        val outputimage = Bitmap.createBitmap(
//            bWidth,
//            bHeight,
//            Bitmap.Config.ARGB_8888
//        )
//        val cornerSizePx = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, cornerDips.toFloat(),
//            context?.resources?.displayMetrics
//        ).toInt()
//
//        val rectB = Rect(0, 0, bWidth, bHeight)
//        val rectBF = RectF(rectB)
//        val paint = Paint()
////        val rect = Rect(0, 0, bWidth, bHeight)
////        val rectF = RectF(rect)
//        val canvas = Canvas(outputimage)
//        paint.color = Color.parseColor(color)
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = borderDips.toFloat()
//        canvas.drawRoundRect(rectBF, cornerSizePx.toFloat(), cornerSizePx.toFloat(), paint)
//        return outputimage
//    }

    private val colorList = listOf(
        "#67ebdb",
        "#175759",
        "#1926e3",
        "#e0094a",
        "#e8e51a",
        "#c5f0eb",
        "#1a9618",
        "#8cf59f",
        "#075769",
        "#2bccf0",
        "#4332c7",
        "#e36517",
        "#573824"
    )

//    private val lightColorList = listOf(
//        "#c7f0d2",
//        "#cbf5f1",
//        "#f5e4e1",
//        "#f5f2d7",
//        "#f7e4f7"
//    )

    private val gradientOrientationList = listOf(
        GradientDrawable.Orientation.TOP_BOTTOM,
        GradientDrawable.Orientation.BOTTOM_TOP,
        GradientDrawable.Orientation.LEFT_RIGHT,
        GradientDrawable.Orientation.RIGHT_LEFT,
        GradientDrawable.Orientation.BL_TR,
        GradientDrawable.Orientation.TR_BL,
        GradientDrawable.Orientation.TL_BR,
        GradientDrawable.Orientation.BR_TL,
    )

    private fun makeGradientBitmap2(width: Int, height: Int): Bitmap {
        val color = intArrayOf(
            Color.parseColor(colorList.random()),
            Color.parseColor(colorList.random()),
        )

        val gradient = GradientDrawable(gradientOrientationList.random(), color)
        gradient.cornerRadius = 0f
        return gradient.toBitmap(width, height)
    }

}