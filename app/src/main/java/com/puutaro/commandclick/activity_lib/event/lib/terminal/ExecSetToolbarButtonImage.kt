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
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
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
                CmdClickIcons.HISTORY.str
            )
            setImageButton(
                binding.cmdindexSettingButtonImage,
                CmdClickIcons.SETTING.str
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
        iconMacroStr: String
    ){
        val icon = CmdClickIcons.values().firstOrNull {
            it.str == iconMacroStr
        } ?: return
        val toolbarUrlImageDirPath = UrlHistoryPath.toolbarUrlImageDirPath
        val imageFile = File(
            toolbarUrlImageDirPath,
            File(icon.assetsPath).name

        )
        if (
            !imageFile.isFile
        ) return
        execSetImageButton(
            imageView,
            imageFile.absolutePath,
            iconMacroStr
        )
    }

    private suspend fun execSetImageButton(
        imageButton: AppCompatImageView,
        imagePath: String,
        iconMacro: String
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
            val beforeChecksum = TagManager.getChecksumFromTag(imageButton.tag)
            if(
                beforeChecksum == checksum
            ) return@withContext
            imageButton.tag = TagManager.make(
                iconMacro,
                checksum
            )
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

    object TagManager {

        private const val separator = "___"
        fun make(
            iconMacro: String,
            checksum: String
        ): String {
            return listOf(
                iconMacro,
                checksum
            ).joinToString(separator)
        }

        fun getChecksumFromTag(
            tag: Any?
        ): String? {
            if(
                tag == null
            ) return null
            val tagString = try {
                tag.toString()
            } catch (e: Exception){
                return null
            }
            return tagString.split(separator).getOrNull(1)
        }

        fun getIconMacroFromTag(
            tag: Any?
        ): String? {
            if(
                tag == null
            ) return null
            val tagString = try {
                tag.toString()
            } catch (e: Exception){
                return null
            }
            return tagString.split(separator).firstOrNull()
        }
    }
}