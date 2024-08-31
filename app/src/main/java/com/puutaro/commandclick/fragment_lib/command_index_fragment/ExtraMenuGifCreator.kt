package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


private const val imageWidth = 500
private const val imageHeight = 1000

object ExtraMenuGifCreator {

    val extraMenuDirPath = File(UsePath.cmdclickDefaultAppDirPath, "extraMenuDir").absolutePath
    val fg1GifDirPath = File(extraMenuDirPath, "fg1").absolutePath
    var extraMapBitmapList: List<Bitmap?> = emptyList()
//    val fg2GifDirPath = File(extraMenuDirPath, "fg2").absolutePath
//    val fg3GifDirPath = File(extraMenuDirPath, "fg3").absolutePath
//    val bkGifDirPath = File(extraMenuDirPath, "bk").absolutePath

    fun create(
        cmdIndexFragment: CommandIndexFragment
    ){
        val context = cmdIndexFragment.context
        cmdIndexFragment.lifecycleScope.launch {
            cmdIndexFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            withContext(Dispatchers.IO) {
//                BkCreator.create(context)
//            }
                withContext(Dispatchers.IO) {
                    extraMapBitmapList = FgCreator.create(
                        context,
                        "#043333",
                        fg1GifDirPath
                    )
//                FgCreator.create(
//                    context,
//                    "#289c7f",
//                    fg2GifDirPath
//                )
//                FgCreator.create(
//                    context,
//                    "#289c7f",
//                    fg3GifDirPath
//                )
//                FgCreator.create(
//                    context,
//                    "#a4f5e1",
//                    bkGifDirPath
//                )
                }
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(
                        "ok"
                    )
                }

            }
        }
    }

    private object BkCreator {

        fun create(
            context: Context?
        ){
            (1..3).forEachIndexed {
                    index, _ ->
                val bitmap = execCreate(context)
                    ?: return@forEachIndexed
                val byteArray = BitmapTool.convertBitmapToByteArray(bitmap)
                    ?: return@forEachIndexed
//                FileSystems.writeFromByteArray(
//                    File(bkGifDirPath, "${index}.png").absolutePath,
//                    byteArray,
//                )
//                execCreate(context)
            }
//            val gifByteArray = BitmapTool.generateGIF(bitmapList)
//                ?: return
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagebkgif.gif").absolutePath,
//                gifByteArray
//            )

        }
        fun execCreate(
            context: Context?
        ): Bitmap? {
            val maskBitmap = createMask(
                context,
            ) ?: return null
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagebkfg.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(maskBitmap)
//            )
            val bkBitmap = createBk()
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagebkbk.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(bkBitmap)
//            )
            val bk = BitmapTool.ImageRemaker.mask(
                bkBitmap,
                maskBitmap,
            )

//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagebk.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(bk)
//            )
            return bk

        }

        private fun createBk(): Bitmap {
            return BitmapTool.ImageRemaker.makeRect(
                "#5ae0dc",
                imageWidth,
                imageHeight
            )
        }

        private fun createMask(
            context: Context?,
        ): Bitmap? {
            val baseColorRect = BitmapTool.ImageRemaker.makeRect(
                "#000000",
                imageWidth,
                imageHeight
            )
            val bitmap = AssetsFileManager.assetsByteArray(
                context,
                AssetsFileManager.cHoleMaskPingPath
            )?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            } ?: return null
            val maskBitmapSrc = BitmapTool.ImageRemaker.cut(
                bitmap,
                imageWidth,
                imageHeight,
            ).let {
                rndFlip(it)
            }
            return BitmapTool.ImageRemaker.mask(
                baseColorRect,
                maskBitmapSrc
            )
//            return BitmapTool.ImageRemaker.masking(
//                baseColorRect,
//                maskBitmapSrc,
//            )
//            return masking(
//                maskBitmap,
//                baseColorRect
//            )
        }
    }

    private object FgCreator {

        fun create(
            context: Context?,
            color: String,
            outDirPath: String,
        ): List<Bitmap?> {
            val createRndList = (1..4)
            return (1..10).mapIndexed {
                index, _ ->
                val createPngFile = File(outDirPath, "${index}.png")
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
                    context,
                    color,
                ) ?: return@mapIndexed null
                val byteArray = BitmapTool.convertBitmapToByteArray(bitmap)
                    ?: return@mapIndexed null
                FileSystems.writeFromByteArray(
                    File(outDirPath, "${index}.png").absolutePath,
                    byteArray,
                )
                bitmap
            }
//            val gifByteArray = BitmapTool.generateGIF(
//                bitMapList = bitmapList,
////                dispose = 2,
//                transparentColor = 0x0000ffff
//            ) ?: return
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imageFgif.gif").absolutePath,
//                gifByteArray
//            )

        }
        fun execCreate(
            context: Context?,
            color: String
        ): Bitmap? {
            val maskBitmap = createMask(
                context,
            ) ?: return null
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagefgfg.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(maskBitmap)
//            )
            val bkBitmap = createBk(color)
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagefgbk.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(bkBitmap)
//            )
            val fg = BitmapTool.ImageRemaker.mask(
                bkBitmap,
                maskBitmap,
            )

//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "imagefg.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(fg)
//            )
            return fg

        }

        private fun createBk(
            color: String
        ): Bitmap {
            return BitmapTool.ImageRemaker.makeRect(
                "#0a6161",
                imageWidth,
                imageHeight
            )
        }

        private fun createMask(
            context: Context?,
        ): Bitmap? {
            val baseColorRect = BitmapTool.ImageRemaker.makeRect(
                "#000000",
                imageWidth,
                imageHeight
            )
            val bitmap = AssetsFileManager.assetsByteArray(
                context,
                AssetsFileManager.cHoleMaskPingPath
            )?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            } ?: return null
            val maskBitmapSrc = BitmapTool.ImageRemaker.cut(
                bitmap,
                imageWidth,
                imageHeight,
            ).let {
                rndFlip(it)
            }
            val maskSrcBitmap = BitmapTool.ImageRemaker.mask(
                baseColorRect,
                maskBitmapSrc,
            )
            FileSystems.writeFromByteArray(
                File(UsePath.cmdclickDefaultAppDirPath, "imageFgMaskSrc.png").absolutePath,
                BitmapTool.convertBitmapToByteArray(maskSrcBitmap)
            )
            return maskBitmapSrc
            val maskBitmap = BitmapTool.ImageRemaker.mask(
                baseColorRect,
                maskSrcBitmap,
            )
            FileSystems.writeFromByteArray(
                File(UsePath.cmdclickDefaultAppDirPath, "imageFgMask.png").absolutePath,
                BitmapTool.convertBitmapToByteArray(maskBitmap)
            )
            return maskBitmap
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

//    private fun createAnimatinGif(
//        context: Context,
//        bitmap1: Bitmap,
//        bitmap2: Bitmap,
//        bitmap3: Bitmap,
//    ){
//        val animation = AnimationDrawable()
//        animation.addFrame(BitmapDrawable(context.resources, bitmap1), 10)
//        animation.addFrame(BitmapDrawable(context.resources, bitmap2), 50)
//        animation.addFrame(BitmapDrawable(context.resources, bitmap3), 30)
//        animation.isOneShot = false
//        GifDrawable()
//        val imageAnim = findViewById(R.id.imageView) as ImageView
//        imageAnim.setImageDrawable(animation)
//    }

    private fun gifDrawableToFile(gifDrawable: GifDrawable, gifFile: File) {
        val byteBuffer = gifDrawable.buffer
        val output = FileOutputStream(gifFile)
        val bytes = ByteArray(byteBuffer.capacity())
        (byteBuffer.duplicate().clear() as ByteBuffer).get(bytes)
        output.write(bytes, 0, bytes.size)
        output.close()
    }
}

