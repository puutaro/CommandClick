package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ButtonImageCreator
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
                setForCmdIndex(
                    bottomFragment
                )
                setForTerminalFragment(activity)
            }
            is EditFragment -> {}
//                setForEditFragment(
//                    bottomFragment,
//                )
        }
    }

    fun setForCmdIndex(
        cmdIndexFragment: CommandIndexFragment
    ){
        val binding = cmdIndexFragment.binding
        CoroutineScope(Dispatchers.IO).launch {
            cmdClickIconList.firstOrNull {
                it.str == CmdClickIcons.HISTORY.str
            }?.let {
                    icon ->
                setImageButton(
                    binding.cmdindexFannelCenterButtonImage,
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

    object SelectionBarButton {

        val rectColorList = listOf(
            CmdClickColorStr.LIGHT_GREEN.str,
            CmdClickColorStr.WHITE_GREEN.str,
            CmdClickColorStr.YELLOW_GREEN.str,
            CmdClickColorStr.DARK_GREEN.str,
            CmdClickColorStr.ANDROID_GREEN.str,
            CmdClickColorStr.THICK_GREEN.str,
            CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.BLUE.str,
            CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.WATER_BLUE.str,
            CmdClickColorStr.WHITE_BLUE.str,
            CmdClickColorStr.WHITE_BLUE_PURPLE.str,
            CmdClickColorStr.DARK_BROWN.str,
            CmdClickColorStr.GOLD_YELLOW.str,
            CmdClickColorStr.NAVY.str,
        )
        fun updatePocketSearchImage(
            binding: CommandIndexFragmentBinding,
        ) {
            CoroutineScope(Dispatchers.IO).launch{
                setPocketSearchIconImage(
                    binding
                )
            }
        }

        private suspend fun setPocketSearchIconImage(
            binding: CommandIndexFragmentBinding
        ){
            val baseIconViewList = listOf(
                binding.cmdindexSelectionSearchIcon1,
            )
            withContext(Dispatchers.Main) {
                baseIconViewList.forEach {
                    setIconForSelectionBarActiveGBar(
                        it,
                    )
                }
            }
        }


        private fun execSetIconForSelectionBarActiveGBar(
            imageButton: AppCompatImageView?,
            bitmapList: List<Bitmap?>
        ){
            if(imageButton == null) return
            val context = imageButton.context
            val animation = AnimationDrawable()

            bitmapList.shuffled().forEach {
                animation.addFrame(
                    BitmapDrawable(context.resources, it),
                    600
                )
            }
            animation.isOneShot = false

            imageButton.imageTintList = null
            imageButton.setImageDrawable(animation)
            imageButton.scaleType = ImageView.ScaleType.FIT_XY
            animation.start()
                imageButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ff8800"))
        }

        private fun setIconForSelectionBarActiveGBar(
            imageButton: AppCompatImageView?,
        ) {
            val rectColor = rectColorList.random()
            val isAll = (1..10).random() < 3
            val rotateRndList = listOf(0f, 180f)
            val bitmapListSrc = (1..2).map {
                createGBarActiveBitmap(
                    isAll,
                    rectColor,
                )
            } +  listOf(
                createGBarActiveBitmap(
                    true,
                    rectColor
                )
            )
            val bitmapList = bitmapListSrc.map {
                if(
                    it == null
                ) return@map null
                BitmapTool.rotate(
                    it,
                    rotateRndList.random()
                )
            }
            execSetIconForSelectionBarActiveGBar(
                imageButton,
                bitmapList,
            )
        }

        private fun createGBarActiveBitmap(
            isAllG: Boolean,
            rectColor: String,
        ): Bitmap? {
            val gBitmap =
                ButtonImageCreator.SelectionBarButton.getSelectionBarBitmapList().shuffled().firstOrNull()
                    ?: return null
            val repeatTime = 5
            val gRepeatTimes = when(isAllG) {
                true -> repeatTime //(4..repeatTime).random()
                else -> (0..repeatTime).random()
            }
            val rotateAngleList = listOf(-90f, 0f, 90f)
            var gOutBitmap: Bitmap? = null
            if(gRepeatTimes > 0) {
                gOutBitmap = gBitmap
                (1 until gRepeatTimes).forEach { _ ->
                    val outBitmapEntry = gOutBitmap
                        ?: return@forEach
                    val rotateGBitmap = BitmapTool.rotate(
                        gBitmap,
                        rotateAngleList.random()
                    )
                    gOutBitmap = BitmapTool.concatByHorizon(
                        outBitmapEntry,
                        rotateGBitmap,
                    )
                }
            }
            val colorRepeatTimes = repeatTime - gRepeatTimes
            if(
                colorRepeatTimes == 0
            ) return gOutBitmap
            val colorRect = BitmapTool.ImageTransformer.makeRect(
                rectColor,
                gBitmap.width,
                gBitmap.height
            )
            var colorOutBitmap: Bitmap? = colorRect
            (1 until colorRepeatTimes).forEach { _ ->
                val outBitmapEntry = colorOutBitmap
                    ?: return@forEach
                colorOutBitmap = BitmapTool.concatByHorizon(
                    outBitmapEntry,
                    colorRect,
                )
            }
            val gOutBitmapResult = gOutBitmap
                ?: return colorOutBitmap
            val colorOutBitmapResult = colorOutBitmap
                ?: return gOutBitmapResult
//            FileSystems.writeFromByteArray(
//                "${UsePath.cmdclickDefaultAppDirPath}/button/white_${whiteOutBitmapResult.hashCode()}.png",
//                BitmapTool.convertBitmapToByteArray(whiteOutBitmapResult)
//            )
            return BitmapTool.concatByHorizon(
                gOutBitmapResult,
                colorOutBitmapResult,
            )
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

//    fun setForEditFragment(
//        editFragment: EditFragment,
//    ){
//        CoroutineScope(Dispatchers.IO).launch {
//            ToolbarButtonBariantForEdit.entries.forEach {
//                val frameLayout = withContext(Dispatchers.Main) {
//                    editFragment.binding.editToolBarLinearLayout.findViewWithTag<FrameLayout>(
//                        it.str
//                    )
//                } ?: return@forEach
//                val imageView = withContext(Dispatchers.Main) {
//                    frameLayout.children.forEach imageSet@{
//                            view ->
//                        if (
//                            view !is AppCompatImageView
//                        ) return@imageSet
//                        return@withContext view
//                    }
//                    return@withContext null
//                } ?: return@forEach
//                val imageMacro = TagManager.getIconMacroFromTag(imageView.tag)
//                val icon = cmdClickIconList.firstOrNull {
//                    it.str == imageMacro
//                } ?: return@forEach
//                setImageButton(
//                    imageView,
//                    icon
//                )
//            }
//        }
//    }

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
        val checksum = withContext(Dispatchers.IO){
            FileSystems.checkSum(imagePath)
        }
        withContext(Dispatchers.Main) {
            val beforeChecksum = TagManager.getChecksumFromTag(
                imageButton.autofillHints?.firstOrNull()
            )
            if(
                beforeChecksum == checksum
            ) return@withContext
            withContext(Dispatchers.Main) {
                imageButton.imageTintList = null
            }
            val iconSignal = TagManager.make(
                iconMacro,
                checksum
            )
            imageButton.setAutofillHints(iconSignal)
//            imageButton.tag = TagManager.make(
//                iconMacro,
//                checksum
//            )
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
                .dontAnimate()
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