package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
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
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
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

            setImageButton(
                binding.cmdindexSelectionSearchImage,
                CmdClickIcons.values().random()
            )
            SelectionBarButton.setPocketSearch(
                binding,
            )
        }
    }


    object SelectionBarButton {

        fun updatePocketSearchImage(
            binding: CommandIndexFragmentBinding,
        ) {
            CoroutineScope(Dispatchers.IO).launch{
                withContext(Dispatchers.IO){
                    setPocketSearch(
                        binding,
                    )
                }
            }
        }

        suspend fun setPocketSearch(
            binding: CommandIndexFragmentBinding,
        ) {
            val degreeRndList = listOf(90f, -90f)
            val originalImagePath = withContext(Dispatchers.IO) {
                (1..3).map {
                    makeCapturePartPngDirPathList().shuffled().firstOrNull()?.let { dirPath ->
                        if (
                            dirPath.isEmpty()
                        ) return@let null
                        FileSystems.sortedFiles(dirPath).shuffled().firstOrNull()?.let fileList@{
                            if (
                                it.isEmpty()
                            ) return@fileList String()
                            File(dirPath, it).absolutePath
                        }
                    }?.let {
                        val bitmap = BitmapTool.convertFileToBitmap(it)
                            ?: return@let null
                        val rotateBitmap = BitmapTool.rotate(
                            bitmap,
                            degreeRndList.random()
                        )
                        val bitmapWidth = rotateBitmap.width
                        val limitWidth = (bitmapWidth - 100).let remake@ {
                            if(it > 0) return@remake it
                            bitmapWidth
                        }
                        val limitHeightSrc = 200
                        val bitmapHeight = rotateBitmap.height
                        val limitHeight =
                            if(bitmapHeight - limitHeightSrc > 0) limitHeightSrc
                            else bitmapHeight
                        BitmapTool.ImageRemaker.cut(
                            rotateBitmap,
                            limitWidth,
                            limitHeight
                        )
                    }
                }
            }
            withContext(Dispatchers.Main) {
                execSetImageButtonForSelectinBar(
                    binding.cmdindexSelectionSearchImage,
                    originalImagePath,
                )
            }
        }

        private fun execSetImageButtonForSelectinBar(
            imageButton: AppCompatImageView?,
            bitmapList: List<Bitmap?>
        ){
            if(imageButton == null) return
            val context = imageButton.context
            val animation = AnimationDrawable()

            val rndList = (14..20)
            bitmapList.forEach {
                animation.addFrame(
                    BitmapDrawable(context.resources, it),
                    800
                )
            }
            animation.isOneShot = false

            imageButton.imageTintList = null
//                context.getColorStateList(colorId)
            imageButton.setImageDrawable(animation)
            animation.start()
        }

        private fun execSetCaptionIconTint(
            imageButton: AppCompatImageView?,
        ){
            if(imageButton == null) return
            val tintBitmapSrc = BitmapTool.GradientBitmap.makeGradientBitmap2(
                500,
                500,
                BitmapTool.colorList.random(),
                BitmapTool.colorList.random(),
            )
            val context = imageButton.context
            imageButton.setImageDrawable(
                BitmapDrawable(
                    context.resources,
                    tintBitmapSrc
                )
            )
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
        iconMacro: String,
    ){
        if(imageButton == null) return
        val context = imageButton.context
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
            withContext(Dispatchers.Main) {
                imageButton.imageTintList = null
            }
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