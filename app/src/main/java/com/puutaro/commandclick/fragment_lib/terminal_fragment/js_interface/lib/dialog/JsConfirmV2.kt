package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class JsConfirmV2(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var confirmDialogObj: Dialog? = null
    private var returnBool = false
    private var onDialog = false
//    private var leftWhiteTitleBitmap: Bitmap? = null
//    private var rightTitleBlackBitmap: Bitmap? = null

    fun create(
        title: String,
    ): Boolean {
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                    )
                } catch (e: Exception){
                    LogSystems.stdErr(
                        terminalFragmentRef.get()?.context,
                        e.toString()
                    )
                    dismissProcess(
                        null,
                        null,
                        false,
                    )
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return returnBool
    }

    private fun execCreate(
        title: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        if(
            context == null
        ) {
            dismissProcess(
                null,
                null,
                false,
            )
            return
        }
        if(
            title.isEmpty()
        ) {
            dismissProcess(
                null,
                null,
                false,
            )
            return
        }
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        val leftRightBitmapChannel = Channel<Pair<Int, Bitmap?>>(2)
        CoroutineScope(Dispatchers.IO).launch {
//            val screenHeight = ScreenSizeCalculator.pxHeight(terminalFragment)
            val screenWidth = withContext(Dispatchers.Main) {
                ScreenSizeCalculator.pxWidth(
                    terminalFragment
                )
            }
            val titleLength = title.length
            val baseTitleLength = let {
                val quotient = screenWidth / 90
                val threshold = 8
                if(
                    quotient <= threshold
                    ) return@let threshold
                quotient
            }
            val constraintWidth = let {
                if(titleLength >= baseTitleLength) return@let screenWidth
                (screenWidth * titleLength) / baseTitleLength
            }
            val fontSize = let {
                val baseWidth = 720f
                val minSize = 100f
                val incline = (150f - minSize) / (1080f - baseWidth)
                val culcSize = incline  * (screenWidth - baseWidth) + minSize
                if(
                    culcSize <= minSize
                ) return@let minSize
                culcSize
            }
            val centerConstraintX = constraintWidth / 2
            val constraintHeight = let {
                val quotient = titleLength / baseTitleLength
                val oneLineHeight = constraintWidth / 3
                return@let when(
                    quotient
                ) {
                    0 -> oneLineHeight
                    1, 2 -> oneLineHeight * (quotient + 1)
                    else -> screenWidth
                }
            }
            val srcTitleWhiteBitmap = withContext(Dispatchers.IO) {
                BitmapTool.DrawText.drawTextToBitmap(
                    title,
                    constraintWidth.toFloat(),
                    constraintHeight.toFloat(),
                    Color.TRANSPARENT,
                    fontSize,
                    Color.WHITE,
                    Color.WHITE,
                    2f,
                    1.5f,
                )
            }

//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "confirm.txt").absolutePath,
//                listOf(
//
//                    "beforeConstWidth: ${beforeConstWidth}",
//                    "beforeConstHeight: ${beforeConstHeight}",
//                    "constraintWidth: ${constraintWidth}",
//                    "constraintHeight: ${constraintHeight}",
//                    "screenHeight: ${screenHeight}",
//                    "screenWidth: ${screenWidth}",
//                ).joinToString("\n")
//            )
            val jobList = (1..2).map {
                order ->
                async {
                    val bitmap = when(order) {
                        1 -> BitmapTool.ImageTransformer.cutByTarget(
                            srcTitleWhiteBitmap,
                            centerConstraintX,
                            constraintHeight,
                            0,
                            0
                        )

                        else -> BitmapTool.ImageTransformer.cutByTarget(
                            srcTitleWhiteBitmap,
                            centerConstraintX,
                            constraintHeight,
                            centerConstraintX,
                            0
                        )
                    }
                    leftRightBitmapChannel.send(order to bitmap)
                }
            }
            jobList.forEach { it.await() }
            leftRightBitmapChannel.close()
        }
        confirmDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogThemeWithNoDimm,
        )
        confirmDialogObj?.setContentView(
            R.layout.confirm_dialog_v2_layout
        )
//        val titleRight = confirmDialogObj?.findViewById<AppCompatTextView>(
//            R.id.confirm_dialog_v2_title_right
//        )

        val confirmConstraint =
            confirmDialogObj?.findViewById<ConstraintLayout>(
                R.id.confirm_dialog_v2_constraint
            )
        confirmDialogObj?.setOnCancelListener {
            dismissProcess(
                confirmConstraint,
                leftRightBitmapChannel,
                false,
            )
        }
        val leftForeImage =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_left_fore_image
            )
        val rightForeImage =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_right_fore_image
            )
        val confirmLeftTitleView =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_cancel
            )
        val confirmRightTitleView =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_ok
            )
        val leftFrameLayout = confirmDialogObj?.findViewById<FrameLayout>(
            R.id.confirm_dialog_v2_left_frame,
        )
        val rightFrameLayout = confirmDialogObj?.findViewById<FrameLayout>(
            R.id.confirm_dialog_v2_right_frame,
        )
        listOf(
            leftFrameLayout to false,
            confirmLeftTitleView to false,
            rightFrameLayout to true,
            confirmRightTitleView to true,
        ).forEach {
            viewToBool ->
            val view = viewToBool.first
            val bool = viewToBool.second
            view?.apply {
                setOnClickListener {
                    dismissProcess(
                        confirmConstraint,
                        leftRightBitmapChannel,
                        bool,
                    )
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            for (orderToBitmap in leftRightBitmapChannel) {
                val order = orderToBitmap.first
                val bitmap = orderToBitmap.second
                val imageView = withContext(Dispatchers.IO) {
                    when (order) {
                        1 -> confirmLeftTitleView
                        else ->confirmRightTitleView
                    }
                } ?: return@launch
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
//                withContext(Dispatchers.Main) {
//                    Glide
//                        .with(context)
//                        .load(bitmap)
//                        .transition(DrawableTransitionOptions.withCrossFade())
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .thumbnail(requestBuilder)
//                        .into(imageView)
//                }
            }
        }
        val animationPair = listOf(
            Techniques.SlideInRight to Techniques.SlideInLeft,
//            Techniques.SlideInDown to Techniques.SlideInUp,
//            null to null,
        ).random()
//        val rippleDelayPair = listOf(
//            0L,
//            100L
//        ).shuffled().let {
//            it.first() to it.last()
//        }
        val aoColorStr = "#007F89"
        val redPurple = "#BB86FC"
        val redAiIro = "#b305f7"
        val blueGreen = "#298072"
        //""#2f9c8b"
        //"#32b39f"
        //""#25cfb5"
        val red = "#FF0000"
        val cancelImageColorStr = listOf(
            CmdClickColorStr.SKERLET.str,
            redPurple,
            redAiIro,
            red,
        ).random()
        val okImageColorStr = listOf(
            aoColorStr,
            blueGreen,
            CmdClickColorStr.GREEN.str,
            CmdClickColorStr.BLUE.str,
            CmdClickColorStr.PURPLE.str,
        ).random()
        CoroutineScope(Dispatchers.Main).launch {
            val byteArrayPair = withContext(Dispatchers.IO) {
                AssetsFileManager.assetsByteArray(
                    context,
                    AssetsFileManager.xPngPath
                ) to AssetsFileManager.assetsByteArray(
                    context,
                    AssetsFileManager.oPngPath
                )
            }

            withContext(Dispatchers.Main) {
                val jobList = (1..2).map {
                    async {
                        val byteArray = when (it) {
                            1 -> byteArrayPair.first
                            else -> byteArrayPair.second
                        }
                        val imageView = when (it) {
                            1 -> leftForeImage
                            else -> rightForeImage
                        } ?: return@async
                        val colorStr = when (it) {
                            1 -> cancelImageColorStr
                            else -> okImageColorStr
                        }
                        imageView.setColorFilter(Color.parseColor(colorStr))
                        Glide
                            .with(context)
                            .load(byteArray)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .thumbnail(requestBuilder)
                            .into(imageView)
                        val techniques = when (it) {
                            1 -> animationPair.first
                            else -> animationPair.second
                        }
                        YoYo.with(techniques)
                            .duration(100)
                            .playOn(imageView)
                    }
                }
                jobList.forEach { it.await() }
            }
        }


        confirmDialogObj?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            setGravity(
                Gravity.BOTTOM
            )
        }
        confirmDialogObj?.show()
    }

    private fun dismissProcess(
        confirmConstraint: ConstraintLayout?,
        leftRightBitmapChannel: Channel<Pair<Int, Bitmap?>>?,
        returnBoolSrc: Boolean,
    ){
        try {
            leftRightBitmapChannel?.close()
        } catch (e: Exception){
            null
        }
        confirmConstraint?.removeAllViews()
        returnBool = returnBoolSrc
        confirmDialogObj?.dismiss()
        confirmDialogObj = null
        onDialog = false
    }
}