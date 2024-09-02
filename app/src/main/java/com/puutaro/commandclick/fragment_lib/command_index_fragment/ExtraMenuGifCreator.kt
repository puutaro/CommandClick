package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


//private const val imageWidth = 500
private const val imageHeight = 1000

object ExtraMenuGifCreator {



    private val extraButtonImageDirPath = File(UsePath.cmdclickFannelSystemDirPath, "extraButtonImage").absolutePath
    private val extraMenuUrlDirPath = "${UrlFileSystems.cmdClickAssetsRepoPrefix}/master/extra_menu/wall"
    private const val srcWallPngName = "srcWall.png"
    private val srcWallUrlPngPath = "${extraMenuUrlDirPath}/${srcWallPngName}"
    private val srcWallDirPath = File(extraButtonImageDirPath, "wall").absolutePath
    private val srcWallSrcDirPath = File(srcWallDirPath, "src").absolutePath
    val srcWallPartDirPath = File(srcWallDirPath, "part").absolutePath
    private val srcWallPngFile = File(srcWallSrcDirPath, srcWallPngName)
    var extraMapBitmapList: List<Bitmap?> = emptyList()
    private var extraMenuGifCreateJob: Job? = null

    fun exit(){
        extraMenuGifCreateJob?.cancel()
    }
    fun create(
        cmdIndexFragment: CommandIndexFragment
    ){

        val context = cmdIndexFragment.context
        exit()
        extraMenuGifCreateJob = cmdIndexFragment.lifecycleScope.launch {
            cmdIndexFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val imageWidth = withContext(Dispatchers.IO) {
                    val screenHeight =
                        ScreenSizeCalculator.dpHeight(cmdIndexFragment.activity)
                    val screenWeight =
                        ScreenSizeCalculator.dpWidth(cmdIndexFragment.activity)
                    (imageHeight * screenWeight) / screenHeight
                }.toInt()
                val srcWallByteArray = withContext(Dispatchers.IO){
                    SrcWall.makeByteArray(context)
                } ?: return@repeatOnLifecycle
                withContext(Dispatchers.IO) {
                    extraMapBitmapList = FgCreator.create(
                        BitmapFactory.decodeByteArray(srcWallByteArray, 0, srcWallByteArray.size),
                        srcWallPartDirPath,
                        imageWidth
                    )
                }
            }
        }
    }

    private object SrcWall {

        suspend fun makeByteArray(
            context: Context?,
        ): ByteArray? {
            val urlSrcWallLength = withContext(Dispatchers.IO){
                CurlManager.getLength(
                    context,
                    srcWallUrlPngPath
                )
            }
            if(
                srcWallPngFile.isFile
                && srcWallPngFile.length().toInt() == urlSrcWallLength
            ) {
                return BitmapTool.convertFileToByteArray(srcWallPngFile.absolutePath)
            }
            val byteArray = CurlManager.get(
                context,
                srcWallUrlPngPath,
                String(),
                String(),
                5_000,
            ).let {
                val isConnOk = CurlManager.isConnOk(it)
                if(!isConnOk) return@let null
                it
            } ?: return null
            FileSystems.writeFromByteArray(
                srcWallPngFile.absolutePath,
                byteArray
            )
            return byteArray
        }
    }

    private object FgCreator {

        fun create(
            srcWallBitmap: Bitmap,
            outDirPath: String,
            imageWidth: Int
        ): List<Bitmap?> {
            val createRndList = (1..7)
            return (1..10).mapIndexed {
                    index, _ ->
                val partPngName = "${index}.png"
                val createPngFile = File(outDirPath, partPngName)
                val isCreate = when(createPngFile.isFile) {
                    false -> true
                    true -> createRndList.random() % 4 == 1
                }
                if(
                    !isCreate
                ) return@mapIndexed BitmapTool.convertFileToBitmap(
                    createPngFile.absolutePath
                )
                val bitmap = execCreate(
                    srcWallBitmap,
                    imageWidth,
                )
                val byteArray = BitmapTool.convertBitmapToByteArray(bitmap)
                FileSystems.writeFromByteArray(
                    File(outDirPath, partPngName).absolutePath,
                    byteArray,
                )
                bitmap
            }
        }
        fun execCreate(
            srcWallBitmap: Bitmap,
            imageWidth: Int
        ): Bitmap {
            val maskBitmap = createMask(
                srcWallBitmap,
                imageWidth,
            )
            val bkBitmap = createBk(
                imageWidth
            )
            val fg = BitmapTool.ImageRemaker.mask(
                bkBitmap,
                maskBitmap,
            )
            return fg

        }

        private fun createBk(
            imageWidth: Int
        ): Bitmap {
            return BitmapTool.ImageRemaker.makeRect(
                "#0a6161",
                imageWidth,
                imageHeight
            )
        }

        private fun createMask(
            srcWallBitmap: Bitmap,
            imageWidth: Int,
        ): Bitmap {
            val maskBitmapSrc = cutWall(
                srcWallBitmap,
                imageWidth,
                imageHeight,
            ).let {
                rndFlip(it)
            }
            return maskBitmapSrc
        }

        fun cutWall(
            bitmap: Bitmap,
            limitWidthPx: Int,
            limitHeightPx: Int,
        ): Bitmap {
            // Set some constants
            val srcWidth = bitmap.width
            val srcHeight = bitmap.height
            val startX = (0..(srcWidth - limitWidthPx)).random()
            val startY = (0..(srcHeight - limitHeightPx)).random()

            return Bitmap.createBitmap(bitmap, startX, startY, limitWidthPx, limitHeightPx, null, false)
        }
    }

    private fun rndFlip(bitmap: Bitmap): Bitmap {
        val rndInt = (1..4).random()
        return when(rndInt){
            1 -> {
                BitmapTool.ImageRemaker.flipHorizontally(bitmap)
            }
            2 -> {
                BitmapTool.ImageRemaker.flipVertically(bitmap)
            }
            3 -> {
                BitmapTool.ImageRemaker.flipVertically(bitmap).let {
                    BitmapTool.ImageRemaker.flipHorizontally(it)
                }
            }
            else -> bitmap
        }

    }
}

