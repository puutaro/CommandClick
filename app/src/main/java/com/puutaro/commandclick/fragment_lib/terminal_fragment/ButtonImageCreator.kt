package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
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
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
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

    private var buttonImageCreateJob: Job? = null

//    private var selectionBarBitmapList = listOf<Bitmap?>()

    fun exit(){
        buttonImageCreateJob?.cancel()
    }

    fun create(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
        val toolbarUrlImageDirPath = UrlHistoryPath.toolbarUrlImageDirPath
        buttonImageCreateJob = terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {

//                withContext(Dispatchers.IO){
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "image.txt").absolutePath,
//                        "start ${LocalDateTime.now()}"
//                    )
//                }
                val defaultUrlCapBitmap = withContext(Dispatchers.IO){
                    AssetsFileManager.assetsByteArray(
                        context,
                        AssetsFileManager.firstUrlCapPngPath
                    )?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                }
                val assetsPathList = CmdClickIcons.entries.map {
                    it.assetsPath
                }
                execCreate(
                    context,
                    assetsPathList,
                    toolbarUrlImageDirPath,
                    defaultUrlCapBitmap,
                )
                withContext(Dispatchers.Main) {
                    val listener = context as? TerminalFragment.OnSetToolbarButtonImageListener
                        ?: return@withContext
                    listener.onSetToolbarButtonImage()
                }
                SelectionBarButton.create(
                    context,
                    listOf(CmdClickIcons.GOOGLE.assetsPath),
                    defaultUrlCapBitmap,
                    null,
                )
//                withContext(Dispatchers.IO){
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "image.txt").absolutePath,
//                        "end ${LocalDateTime.now()}"
//                    )
//                }
            }
        }
    }

    private suspend fun execCreate(
        context: Context?,
        assetsPathList: List<String>,
        imageDirPath: String,
        defaultUrlCapBitmap: Bitmap?,
    ) {
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        val capturePartPngDirPathList = makeCapturePartPngDirPathList()
        withContext(Dispatchers.IO) {
            val jobList = assetsPathList.map { assetsPath ->
                async {
                    semaphore.withPermit {
                        val fileName = File(assetsPath).name
                        val toolbarButtonImageFile = File(imageDirPath, fileName)

                        if (
                            toolbarButtonImageFile.isFile
                        ) {
                            if (
                                (1..4).random() % 4 <= 2
                            ) return@async
                        }
                        val byteArray = ExecButtonImageCreator.create(
                            context,
                            assetsPath,
                            capturePartPngDirPathList,
                            defaultUrlCapBitmap,
                            null,
                            BitmapTool.GradientBitmap.GradOrient.BOTH,
                        ) ?: return@async
//                        val originalImagePath = capturePartPngDirPathList.shuffled().firstOrNull()?.let {
//                                dirPath ->
//                            if(
//                                dirPath.isEmpty()
//                            ) return@let null
//                            FileSystems.sortedFiles(dirPath).shuffled().firstOrNull()?.let fileList@ {
//                                if(
//                                    it.isEmpty()
//                                ) return@fileList null
//                                File(dirPath, it).absolutePath
//                            }
//                        }
//                        val byteArray = cropImage(
//                            context,
//                            assetsPath,
//                            originalImagePath,
//                            defaultUrlCapBitmap,
//                            null,
//                            null,
//                        ) ?: return@async

                        FileSystems.writeFromByteArray(
                            toolbarButtonImageFile.absolutePath,
                            byteArray
                        )
                    }
                }
            }
            jobList.forEach { it.await() }
        }
    }

    object ExecButtonImageCreator{
        fun create(
            context: Context?,
            assetsPath: String,
            capturePartPngDirPathList: List<String>,
            defaultUrlCapBitmap: Bitmap?,
            centerColorStr: String?,
            gradOrient: BitmapTool.GradientBitmap.GradOrient,
        ): ByteArray? {
            val originalImagePath = capturePartPngDirPathList.shuffled().firstOrNull()?.let {
                    dirPath ->
                if(
                    dirPath.isEmpty()
                ) return@let null
                FileSystems.sortedFiles(dirPath).shuffled().firstOrNull()?.let fileList@ {
                    if(
                        it.isEmpty()
                    ) return@fileList null
                    File(dirPath, it).absolutePath
                }
            }
            return cropImage(
                context,
                assetsPath,
                originalImagePath,
                defaultUrlCapBitmap,
                centerColorStr,
                gradOrient,
            )
        }
    }


    object SelectionBarButton {

        private const val selectionImageFileNumSeparator = "_"

        private val ccGradColorList = listOf(
            CmdClickColorStr.LIGHT_GREEN.str,
            CmdClickColorStr.WHITE_GREEN.str,
            CmdClickColorStr.DARK_GREEN.str,
            CmdClickColorStr.GREEN.str,
            CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.BLUE.str,
            CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.WATER_BLUE.str,
            CmdClickColorStr.WHITE_BLUE.str,
            CmdClickColorStr.PURPLE.str,
            CmdClickColorStr.NAVY.str,
            CmdClickColorStr.CARKI.str,
        )
        suspend fun create(
            context: Context?,
            assetsPathList: List<String>,
            defaultUrlCapBitmap: Bitmap?,
            centerColorStr: String?,
        ) {
            val capturePartPngDirPathList = makeCapturePartPngDirPathList()
            val concurrentLimit = 5
            val semaphore = Semaphore(concurrentLimit)
            val imageToAssetsPathList = assetsPathList.map { assetsPath ->
                (1..5).map { index ->
                    makeSelectionBarImagePathFromAssetsPath(
                        assetsPath,
                        index,
                    ) to assetsPath
                }
            }.flatten()
            //BitmapTool.ccGradColorList //+ BitmapTool.ccDeepColorList
//        val ccDeepColorList = BitmapTool.ccDeepColorList
            withContext(Dispatchers.IO) {
                val jobList = imageToAssetsPathList.map { imageToAssetsPath ->
                    val selectionBarImageFile = imageToAssetsPath.first
                    val assetsPath = imageToAssetsPath.second
                    async {
                        semaphore.withPermit {
                            if (
                                selectionBarImageFile.isFile
                            ) {
                                if (
                                    (1..4).random() % 4 <= 2
                                ) return@async
                            }
                            val originalImagePath =
                                capturePartPngDirPathList.shuffled().firstOrNull()?.let { dirPath ->
                                    if (
                                        dirPath.isEmpty()
                                    ) return@let null
                                    FileSystems.sortedFiles(dirPath).shuffled().firstOrNull()
                                        ?.let fileList@{
                                            if (
                                                it.isEmpty()
                                            ) return@fileList null
                                            File(dirPath, it).absolutePath
                                        }
                                }
                            val byteArray = cropImage(
                                context,
                                assetsPath,
                                originalImagePath,
                                defaultUrlCapBitmap,
                                centerColorStr,
                                BitmapTool.GradientBitmap.GradOrient.BOTH,
                            ) ?: return@async
                            FileSystems.writeFromByteArray(
                                selectionBarImageFile.absolutePath,
                                byteArray
                            )
                        }
                    }
                }
                jobList.forEach { it.await() }
            }
        }

        fun getSelectionBarBitmapList(): List<Bitmap?> {
            return getSelectionBarImagePathFromAssetsPath(
                CmdClickIcons.GOOGLE.assetsPath
            ).map {
                BitmapTool.convertFileToBitmap(it)
            }
        }

        private fun makeSelectionBarImagePathFromAssetsPath(
            assetsPath: String,
            num: Int,
        ): File {
            return File(
                UrlHistoryPath.selectionTextBarImageDirPath,
                listOf(
                    num,
                    File(assetsPath).name
                ).joinToString(selectionImageFileNumSeparator)
            )
        }


        private fun getSelectionBarImagePathFromAssetsPath(
            assetsPath: String,
        ): List<String> {
            val selectionTextBarImageDirPath =
                UrlHistoryPath.selectionTextBarImageDirPath
            val fileName = File(assetsPath).name
            return FileSystems.sortedFiles(
                selectionTextBarImageDirPath,
            ).filter {
                it.endsWith("${selectionImageFileNumSeparator}${fileName}")
            }.map {
                File(selectionTextBarImageDirPath, it).absolutePath
            }

        }
    }

    private fun cropImage(
        context: Context?,
        maskAssetsPath: String,
        originalImagePath: String?,
        defaultUrlCapBitmap: Bitmap?,
        centerColorStr: String?,
        gradOrient: BitmapTool.GradientBitmap.GradOrient,
    ): ByteArray? {
        val original = when(originalImagePath.isNullOrEmpty()){
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
            } ?: return null
        val maskByteArray = AssetsFileManager.assetsByteArray(
                context,
                maskAssetsPath
            ) ?: return null
        val src = BitmapFactory.decodeByteArray(
                maskByteArray,
                0,
                maskByteArray.size
            )
        val output = Bitmap.createBitmap(
                src.width,
                src.height,
                Bitmap.Config.ARGB_8888
            )
        val outBitmap = let {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            val canvas = Canvas(output)
            canvas.drawBitmap(original, 0f, 0f, null)
            paint.color = -0x1000000
            canvas.drawBitmap(src, 0f, 0f, paint)
            return@let output
        }
        val cornerDips = (2..8).random()
        val colorIntArray = listOf(
            colorList.random(),
            centerColorStr ?: colorList.random(),
            colorList.random()
        ).map {
            Color.parseColor(it)
        }.toIntArray()
        val bkBitmapSrc = BitmapTool.GradientBitmap.makeGradientBitmap2(
            original.width,
            original.height,
            colorIntArray,
            gradOrient,
        )
         val overBitmap = BitmapTool.ImageTransformer.overlayBitmap(
             bkBitmapSrc,
             outBitmap,
        )
        val resultBitmap = BitmapTool.ImageTransformer.roundCorner(
            context,
            overBitmap,
            cornerDips,
        ) ?: return null
//        getRoundedCornerBitmap(
//            context,
//            overBitmap,
//            cornerDips,
//        ) ?: return null
        return BitmapTool.convertBitmapToByteArray(resultBitmap)
    }

    fun makeCapturePartPngDirPathList(): List<String> {
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

    private val colorList = listOf(
        CmdClickColorStr.LIGHT_GREEN.str,
        CmdClickColorStr.THICK_AO.str,
        CmdClickColorStr.BLUE.str,
        CmdClickColorStr.SKERLET.str,
        CmdClickColorStr.YELLOW.str,
        CmdClickColorStr.WHITE_GREEN.str,
        CmdClickColorStr.GREEN.str,
        CmdClickColorStr.YELLOW_GREEN.str,
        CmdClickColorStr.BLACK_AO.str,
        CmdClickColorStr.WATER_BLUE.str,
        CmdClickColorStr.PURPLE.str,
        CmdClickColorStr.ORANGE.str,
        CmdClickColorStr.BROWN.str,
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