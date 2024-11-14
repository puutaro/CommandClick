package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
import com.puutaro.commandclick.util.file.ConfirmDialogAssets
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

    fun create(
        title: String,
        message: String,
    ): Boolean {
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        message,
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
        message: String,
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
        val screenWidth = ScreenSizeCalculator.pxWidth(
                terminalFragment
            )
        val xOPairToMakeType = ConfirmDialogAssets.xOPairPngToMakeTypeList.random()
        val xOPair = xOPairToMakeType.first
        val makeType = xOPairToMakeType.second
        CoroutineScope(Dispatchers.IO).launch {
            val constraintWidth = screenWidth
            val fontSize = let {
                val baseWidth = 720f
                val minSize = 110f
                val maxSize = 160f
                val incline = (maxSize - minSize) / (1080f - baseWidth)
                val culcSize = incline  * (screenWidth - baseWidth) + minSize
                if(
                    culcSize <= minSize
                ) return@let minSize
                culcSize
            }
            val centerConstraintX = constraintWidth / 2
            val constraintHeight = (14 * screenWidth) / 10
            val strokeSize = let {
                if(
                    makeType == ConfirmDialogAssets.ImageMakingType.OPACITY
                ) return@let 0f
                val baseWidth = 720f
                val minSize = 2f //7f
                val maxSize = 5f //10f
                val incline = (maxSize - minSize) / (1080f - baseWidth)
                val culcSize = incline  * (screenWidth - baseWidth) + minSize
                if(
                    culcSize <= minSize
                ) return@let minSize
                culcSize
            }
            val messageMarginTop = let {
                val baseWidth = 720f
                val minMargin = 30f
                val incline = (100f - minMargin) / (1080f - baseWidth)
                val culcMargin = incline  * (screenWidth - baseWidth) + minMargin
                if(
                    culcMargin <= minMargin
                ) return@let minMargin
                culcMargin
            }

            val srcTitleWhiteBitmap = withContext(Dispatchers.IO) {
                val titleFont = when(makeType){
                    ConfirmDialogAssets.ImageMakingType.PLANE -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC)
                    ConfirmDialogAssets.ImageMakingType.OPACITY -> Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC)
                }
                val messageFont = when(makeType){
                    ConfirmDialogAssets.ImageMakingType.PLANE -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC)
                    ConfirmDialogAssets.ImageMakingType.OPACITY -> Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC)
                }
                BitmapTool.DrawText.drawTextToBitmapWithMessage(
                    title,
                    message,
                    constraintWidth.toFloat(),
                    constraintHeight.toFloat(),
                    Color.TRANSPARENT,
                    fontSize,
                    Color.WHITE,
                    Color.WHITE,
                    strokeSize,
                    messageStrokeSize = strokeSize * 0.47f,
                    1.5f,
                    0f, //0.142f,
                    null,
                    titleSpacingMulti = 1f, //1f,
                    innerWidthRate = 0.95f,
                    titleFont = titleFont, //Typeface.SANS_SERIF
                    messageFont = messageFont,
                    messageMarginTop = messageMarginTop,
                    messageWidthRate = 0.75f,
                    isAntiAlias = true,
                    maxLines = 8
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
        val leftForeImageFrame =
            confirmDialogObj?.findViewById<FrameLayout>(
                R.id.confirm_dialog_v2_left_fore_image_frame
            )
        val rightForeImageFrame =
            confirmDialogObj?.findViewById<FrameLayout>(
                R.id.confirm_dialog_v2_right_fore_image_frame
            )
        val leftForeImage1 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_left_fore_image1
            )
        val leftForeImage2 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_left_fore_image2
            )
        val rightForeImage1 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_right_fore_image1
            )
        val rightForeImage2 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_right_fore_image2
            )
        val cancelClickFrameLayout = confirmDialogObj?.findViewById<FrameLayout>(
            R.id.confirm_dialog_v2_click_left_frame,
        )
        val okClickFrameLayout = confirmDialogObj?.findViewById<FrameLayout>(
            R.id.confirm_dialog_v2_click_right_frame,
        )
        listOf(
            cancelClickFrameLayout to false,
            okClickFrameLayout to true,
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
                setOnLongClickListener {
                    true
                }
            }
        }
        val confirmLeftTitleView1 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_cancel1
            )
        val confirmLeftTitleView2 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_cancel2
            )
        val confirmRightTitleView1 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_ok1
            )
        val confirmRightTitleView2 =
            confirmDialogObj?.findViewById<AppCompatImageView>(
                R.id.confirm_dialog_v2_ok2
            )
        CoroutineScope(Dispatchers.IO).launch {
            for (orderToBitmap in leftRightBitmapChannel) {
                val order = orderToBitmap.first
                val bitmap = orderToBitmap.second
                val imageView1 = withContext(Dispatchers.IO) {
                    when (order) {
                        1 -> confirmLeftTitleView1
                        else ->confirmRightTitleView1
                    }
                } ?: return@launch
                val imageView2 = withContext(Dispatchers.IO) {
                    when (order) {
                        1 -> confirmLeftTitleView2
                        else ->confirmRightTitleView2
                    }
                } ?: return@launch
                withContext(Dispatchers.Main) {
                    imageView1.setImageBitmap(bitmap)
                    imageView2.setImageBitmap(bitmap)
                }
            }
        }
        val moveDirection = MoveDirection.entries.random()
        val aoColorStr = "#007F89"
        val redPurple = "#BB86FC"
        val redAiIro = "#6c0096"
        val enji = "#78013d"
        //"#b305f7"
        val blueGreen = "#298072"
        //""#2f9c8b"
        //"#32b39f"
        //""#25cfb5"
        val littlrDarkRed = "#ab0000"
        val red = "#fc3232"
        //"#FF0000"
        val cancelImageColorStr = listOf(
            littlrDarkRed,
//            redPurple,
            enji,
            redAiIro,
            CmdClickColorStr.BROWN.str,
//            red,
        ).random()
        val waterBlue2 = "#48ecf7"
        val lightGreen2 = "#2dfa82"
        val yellowGreen2 = "#9fff5e"
        val blueGreen2 = "#32fab7"
        val okImageColorStr = listOf(
            yellowGreen2,
            lightGreen2,
            waterBlue2,
            blueGreen2,
        ).random()
        CoroutineScope(Dispatchers.Main).launch {
            val byteArrayPair = withContext(Dispatchers.IO) {
                listOf(
                    xOPair.first,
                    xOPair.second
                ).map {
                    AssetsFileManager.assetsByteArray(
                        context,
                        it
                    )?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        val opacity = when(
                            makeType
                        ) {
                            ConfirmDialogAssets.ImageMakingType.PLANE -> return@let bitmap //215 //200 kusumu
                            ConfirmDialogAssets.ImageMakingType.OPACITY -> 50
                        }
                        BitmapTool.ImageTransformer.adjustOpacity(
                            bitmap,
                            opacity //130 //50
                        )
                    }
                //                    ?.let {
//                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
//                        BitmapTool.ImageTransformer.adjustOpacity(
//                            bitmap,
//                            opacity //130 //50
//                        )
//                    }
                }.let {
                    it.first() to it.last()
                }
            }
            val slideMultiplePair = listOf(-1, 1).shuffled().let {
                Pair(
                    it.first(),
                    it.last()
                )
            }
            withContext(Dispatchers.Main) {
                val jobList = (1..2).map {
                    async {
                        val byteArray = when (it) {
                            1 -> byteArrayPair.first
                            else -> byteArrayPair.second
                        }
                        val imageViewFrame = when (it) {
                            1 -> leftForeImageFrame
                            else -> rightForeImageFrame
                        } ?: return@async
                        val imageView1 = when (it) {
                            1 -> leftForeImage1
                            else -> rightForeImage1
                        } ?: return@async
                        val imageView2 = when (it) {
                            1 -> leftForeImage2
                            else -> rightForeImage2
                        } ?: return@async
                        val titleImageView2 = withContext(Dispatchers.IO) {
                            when (it) {
                                1 -> confirmLeftTitleView2
                                else ->confirmRightTitleView2
                            }
                        } ?: return@async
                        val colorStr = when (it) {
                            1 -> cancelImageColorStr
                            else -> okImageColorStr
                        }
                        val techniquesPair = moveDirection.directionPair
                        val techniques = when (it) {
                            1 -> techniquesPair.first
                            else -> techniquesPair.second
                        }
                        val firstMultiple = slideMultiplePair.first
                        val secondMultiple = slideMultiplePair.second
                        val absImageSlideX = when(
                            moveDirection
                        ){
                            MoveDirection.HORIZON -> let {
                                val baseWidth = 720f
                                val minDistance = 7f
                                val incline = (10f - minDistance) / (1080f - baseWidth)
                                val culcDistance = incline  * (screenWidth - baseWidth) + minDistance
                                if(
                                    culcDistance <= minDistance
                                ) return@let minDistance
                                culcDistance
                            }.toInt()
                            MoveDirection.VERTICAL -> 0
                        }
                        val absImageSlideY = when(
                            moveDirection
                        ){
                            MoveDirection.HORIZON -> 0
                            MoveDirection.VERTICAL -> let {
                                val baseWidth = 720f
                                val minDistance = 25f
                                val incline = (30f - minDistance) / (1080f - baseWidth)
                                val culcDistance = incline  * (screenWidth - baseWidth) + minDistance
                                if(
                                    culcDistance <= minDistance
                                ) return@let minDistance
                                culcDistance
                            }.toInt()
                        }
                        val slideXyPair = when (it) {
                            1 -> Pair(absImageSlideX * firstMultiple, absImageSlideY * firstMultiple)
                            else -> Pair(absImageSlideX * secondMultiple, absImageSlideY * secondMultiple)
                        }
                        val absTitleSlideX = when(
                            moveDirection
                        ){
                            MoveDirection.HORIZON -> 2
                            MoveDirection.VERTICAL -> 0
                        }
                        val absTitleSlideY = when(
                            moveDirection
                        ){
                            MoveDirection.HORIZON -> 0
                            MoveDirection.VERTICAL -> 2
                        }
                        val titleSlideXyPair = when (it) {
                            1 -> Pair(absTitleSlideX * firstMultiple, absTitleSlideY * firstMultiple) // -5
                            else -> Pair(absTitleSlideX * secondMultiple, absTitleSlideY * secondMultiple) // 5
                        }
                        imageView1.setColorFilter(Color.parseColor(colorStr))
                        Glide
                            .with(context)
                            .load(byteArray)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .thumbnail(requestBuilder)
                            .into(imageView1)
                        Glide
                            .with(context)
                            .load(byteArray)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .thumbnail(requestBuilder)
                            .into(imageView2)
                        YoYo.with(techniques)
                            .duration(100)
                            .interpolate(AccelerateDecelerateInterpolator()).withListener(
                                object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {}

                                    override fun onAnimationEnd(animation: Animator) {
                                        CoroutineScope(Dispatchers.Main).launch{
                                            withContext(Dispatchers.Main) {
                                                val jobList = (1..2).map {
                                                    async {
                                                        when (it) {
                                                            1 -> imageView2.animate()
                                                                .translationX(imageView2.x + slideXyPair.first)
                                                                .translationY(imageView2.y + slideXyPair.second)
                                                                .setDuration(300)

                                                            else -> titleImageView2.animate()
                                                                .translationX(imageView2.x + titleSlideXyPair.first)
                                                                .translationY(imageView2.y + titleSlideXyPair.second)
                                                                .setDuration(300)
                                                        }
                                                    }
                                                }
                                                jobList.forEach { it.await() }
                                            }
                                        }
                                    }
                                    override fun onAnimationCancel(animation: Animator) {}
                                    override fun onAnimationRepeat(animation: Animator) {}
                            }).playOn(imageViewFrame)
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

    private fun setAnimationDrawable(
        context: Context,
        imageView: AppCompatImageView,
        bitmapList1: List<Bitmap?>,
        duration: Int,
    ){
        val animationDrawable = AnimationDrawable()
        bitmapList1.forEach {
            if(it == null) return@forEach
            animationDrawable.addFrame(
                BitmapDrawable(context.resources, it),
                duration
            )
        }
        animationDrawable.isOneShot = false
        imageView.setImageDrawable(animationDrawable)
        animationDrawable.start()
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

    private enum class MoveDirection(
        val directionPair: Pair<Techniques, Techniques>
    ) {
        HORIZON(
            Pair(
                Techniques.SlideInRight,
                Techniques.SlideInLeft
            )
        ),
        VERTICAL(
            Pair(
                Techniques.SlideInDown,
                Techniques.SlideInUp
            )
        ),
    }
}