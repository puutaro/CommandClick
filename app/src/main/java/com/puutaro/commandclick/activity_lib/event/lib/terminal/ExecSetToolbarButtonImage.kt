package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecSetToolbarButtonImage {

    private val cmdClickIconList = CmdClickIcons.values()
    fun set(activity: MainActivity){
        val cmdVariableEditFragmentTag =
            TargetFragmentInstance.getCmdEditFragmentTag(activity)
        val bottomFragment = TargetFragmentInstance.getCurrentBottomFragment(
            activity,
            cmdVariableEditFragmentTag
        ) ?: return
        when(bottomFragment) {
            is CommandIndexFragment -> {
                setForCmdIndex(bottomFragment)
                setForTerminalFragment(activity)
            }
            is EditFragment ->
                setForEditFragment(
                    bottomFragment,
                )
        }
    }

    fun setForCmdIndex(cmdIndexFragment: CommandIndexFragment){
        val binding = cmdIndexFragment.binding
        CoroutineScope(Dispatchers.IO).launch {
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.HISTORY.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.cmdindexHistoryButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.EXTRA.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.cmdindexSettingButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.BLACK_HISTORY.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.cmdindexUrlHistoryButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.GOOGLE.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.cmdindexSearchButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.TOP.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.cmdindexShowPinButtonImage,
                    icon
                )
            }
        }
    }

    private fun setForTerminalFragment(
        activity: MainActivity
    ){
        val terminalFragment =
            TargetFragmentInstance.getCurrentTerminalFragment(activity)
                ?: return
        val binding = terminalFragment.binding
        CoroutineScope(Dispatchers.IO).launch {
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.HISTORY.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termHistoryButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.EXTRA.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termSettingButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.BLACK_HISTORY.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termUrlHistoryButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.GOOGLE.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termSearchButtonImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.DOWN.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termHidePinButtonImage,
                    icon
                )
            }
            terminalFragment.ggleWebViewManager?.updateToolbarButton()
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.GOOGLE.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termGgleFocusImage,
                    icon
                )
            }
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.QR.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.termQrScanImage,
                    icon
                )
            }
        }

    }

    fun setForEditFragment(
        editFragment: EditFragment,
    ){
        CoroutineScope(Dispatchers.IO).launch {
            ToolbarButtonBariantForEdit.values().forEach {
                val frameLayout = withContext(Dispatchers.Main) {
                    editFragment.binding.editToolbarLinearLayout.findViewWithTag<FrameLayout>(
                        it.str
                    )
                } ?: return@forEach
                val imageView = withContext(Dispatchers.Main) {
                    frameLayout.children.forEach imageSet@{
                            view ->
                        if (
                            view !is AppCompatImageView
                        ) return@imageSet
                        return@withContext view
                    }
                    return@withContext null
                } ?: return@forEach
                val imageMacro = TagManager.getIconMacroFromTag(imageView.tag)
                val icon = cmdClickIconList.firstOrNull {
                    it.str == imageMacro
                } ?: return@forEach
                setImageButton(
                    imageView,
                    icon
                )
            }
        }
    }

    suspend fun setImageButton(
        imageView: AppCompatImageView?,
        icon: CmdClickIcons
    ){
        if(imageView == null) return
        val imageFile = getImageFile(icon.assetsPath)
        if (
            !imageFile.isFile
        ) return
        execSetImageButton(
            imageView,
            imageFile.absolutePath,
            icon.str
        )
    }

    fun isImageFile(assetsPath: String?): Boolean {
        if(
            assetsPath.isNullOrEmpty()
        ) return false
        return getImageFile(assetsPath).isFile
    }

    fun getImageFile(assetsPath: String): File {
        val toolbarUrlImageDirPath = UrlHistoryPath.toolbarUrlImageDirPath
        return File(
            toolbarUrlImageDirPath,
            File(assetsPath).name
        )
    }

    private suspend fun execSetImageButton(
        imageButton: AppCompatImageView?,
        imagePath: String,
        iconMacro: String
    ){
        if(imageButton == null) return
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
//            imageButton.background =
//                 AppCompatResources.getDrawable(context, R.color.terminal_color)
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