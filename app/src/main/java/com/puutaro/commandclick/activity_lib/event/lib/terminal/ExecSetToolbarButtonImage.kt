package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecSetToolbarButtonImage {
    fun set(activity: MainActivity){
        val targetFragmentInstance = TargetFragmentInstance()
        val cmdIndexFragment = targetFragmentInstance.getCmdIndexFragment(activity)
            ?: return
        val binding = cmdIndexFragment.binding
        CoroutineScope(Dispatchers.IO).launch {
            setImageButton(
                binding.cmdindexHistoryButtonImage,
                AssetsFileManager.fannelManagerPath
            )
            setImageButton(
                binding.cmdindexSettingButtonImage,
                AssetsFileManager.settingPingPath
            )
//            File(
//                toolbarUrlImageDirPath,
//                CcPathTool.trimAllExtend(
//                    File(AssetsFileManager.fannelManagerPath).name
//                )
//            ).let {
//                dirFile ->
//                val imageName = FileSystems.sortedFiles(dirFile.absolutePath).firstOrNull()
//                    ?: return@launch
//
//                if (
//                    FileSystems.sortedFiles(dirFile.absolutePath).isEmpty()
//                ) return@let
//                execSetImageButton(
//                    binding.cmdindexHistoryButtonImage,
//                    File(dirFile.absolutePath, imageName).absolutePath
//                )
//            }
//            File(
//                toolbarUrlImageDirPath,
//                File(AssetsFileManager.settingPingPath).name
//            ).let {
//                if (!it.isFile) return@let
//                execSetImageButton(
//                    binding.cmdindexSettingButtonImage,
//                    it.absolutePath
//                )
//            }
        }
    }

    private suspend fun setImageButton(
        imageView: AppCompatImageView,
        assetsPath: String,
    ){
        val toolbarUrlImageDirPath = UrlHistoryPath.toolbarUrlImageDirPath
        val imageFile = File(
            toolbarUrlImageDirPath,
            File(assetsPath).name

        )
        if (
            !imageFile.isFile
        ) return
        execSetImageButton(
            imageView,
            imageFile.absolutePath
        )
    }

    private suspend fun execSetImageButton(
        imageButton: AppCompatImageView,
        imagePath: String,
    ){
        val context = imageButton.context
        withContext(Dispatchers.Main) {
            imageButton.imageTintList = null
        }
//        val bitmap = withContext(Dispatchers.IO) {
//            BitmapTool.convertFileToBitmap(
//                File(imagePath).absolutePath
//            )
//        }
//        val isEqualBitmap = withContext(Dispatchers.Main) {
//            imageButton.drawable.toBitmap()
//        } == bitmap
//        if(isEqualBitmap) return
        val checksum = withContext(Dispatchers.IO){
            FileSystems.checkSum(imagePath)
        }
        withContext(Dispatchers.Main) {
            if(
                imageButton.tag == checksum
            ) return@withContext
            imageButton.tag = checksum
            imageButton.background =
                 AppCompatResources.getDrawable(context, R.color.terminal_color)
            val requestBuilder: RequestBuilder<Drawable> =
                Glide.with(context)
                    .asDrawable()
                    .sizeMultiplier(0.1f)
            Glide
                .with(context)
                .load(imagePath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageButton)
        }
    }
}