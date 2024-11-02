package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.component.adapter.GridListAdapter
import com.puutaro.commandclick.custom_manager.PreLoadGridLayoutManager
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.BitmapTool.ImageTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class GridJsDialogV2(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var gridDialogObj: Dialog? = null
    private var onDialog = false
    private var dimAlphaJob: Job? = null

    fun create(
        title: String,
        imagePathListTabSepaStr: String,
        configMapCon: String,
    ): String {
        onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    imagePathListTabSepaStr,
                    configMapCon,
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!onDialog) break
                }
            }
        }
        return returnValue
    }

    private fun execCreate(
        title: String,
        imagePathListNewlineSepaStr: String,
        configMapCon: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return
        val colorPairList = listOf(
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.ORANGE.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.SKERLET.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.DARK_BROWN.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.BLUE_DARK_PURPLE.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.NAVY.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.DARK_GREEN.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.GREEN.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.CARKI.str,
            CmdClickColorStr.YELLOW.str to CmdClickColorStr.PURPLE.str,

            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.NAVY.str,
            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.DARK_GREEN.str,
            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.BLUE_DARK_PURPLE.str,
            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.GREEN.str,
            CmdClickColorStr.WATER_BLUE.str to CmdClickColorStr.CARKI.str,

            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.DARK_GREEN.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.NAVY.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.BLUE_DARK_PURPLE.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.ORANGE.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.SKERLET.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.DARK_BROWN.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.GREEN.str,
            CmdClickColorStr.WHITE_BLUE.str to CmdClickColorStr.PURPLE.str,
        )
        val usrColorPair = colorPairList.random()
        val waterColor = usrColorPair.first
        val titleAndDimColor = usrColorPair.second
        gridDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogTheme
        )
        gridDialogObj?.setContentView(
           R.layout.grid_dialog_v2_layout
        )
        gridDialogObj?.findViewById<OutlineTextView>(
            R.id.grid_dialog_v2_title
        )?.apply {
            val titleString = if(
                title.isNotEmpty()
            ){
                title
            } else "Select"
            text = titleString
        }
        gridDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        gridDialogObj?.window?.apply {
            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            setGravity(Gravity.BOTTOM)
        }
        gridDialogObj?.findViewById<FrameLayout>(
            R.id.grid_dialog_v2_dimm_image_frame
        ).apply{
            originalDimmEffect(
                this,
                titleAndDimColor
            )
        }
        gridDialogObj?.show()
        CoroutineScope(Dispatchers.Main).launch {
            val titleBkImage = withContext(Dispatchers.Main){
                gridDialogObj?.findViewById<AppCompatImageView>(
                    R.id.grid_dialog_v2_title_bk_image
                )
//                    ?.apply {
//                    setBackgroundColor(
//                        Color.parseColor(titleAndDimColor)
//                    )
//                }
            }
            gridDialogObj?.findViewById<AppCompatImageView>(
                R.id.grid_dialog_v2_pour_image1
            )?.apply {
                val imageViewContext = this.context
                val pngSizePair = Pair(200, 300)
//                val gradTopColorStr = CmdClickColorStr.entries.random().str
                val gradientRect = BitmapTool.GradientBitmap.makeGradientBitmap2(
                    pngSizePair.first,
                    pngSizePair.second,
                    listOf(
                        "#00000000",
                        waterColor,
//                        gradTopColorStr,
                    ).map {
                        Color.parseColor(it)
                    }.toIntArray(),
                    BitmapTool.GradientBitmap.GradOrient.VERTICAL_BOTTOM_TO_TOP,
                )
                val revGradientRect = BitmapTool.rotate(
                    gradientRect,
                    180f
                ).let {
                    BitmapTool.ImageTransformer.adjustOpacity(
                        it,
                        200
                    )
                }
//                val titleBkRect = BitmapTool.ImageTransformer.makeRect(
//                        gradTopColorStr,
//                    pngSizePair.first,
//                    pngSizePair.second,
//                )
//                FileSystems.writeFromByteArray(
//                    File("${UsePath.cmdclickDefaultAppDirPath}/pour", "gradientRect.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(gradientRect)
//                )
//                val skullMaskBitmap = ImageTransformer.mask(
//                    gradientRect,
//                    AppCompatResources.getDrawable(
//                        context,
//                        R.drawable.fannel_skull
//                    )?.toBitmap(
//                        pngSizePair.first,
//                        pngSizePair.second
//                    ) as Bitmap,
//                )

//                AppCompatResources.getDrawable(
//                    context,
//                    R.drawable.sibuki4
//                )?.toBitmap(
//                    pngSizePair.first,
//                    pngSizePair.second
//                ) as Bitmap,
                val shibukiBitmapList = (1..2).map {
                    AssetsFileManager.assetsByteArray(
                        context,
                        AssetsFileManager.sibukiPngPath
                    )?.let {
                        BitmapFactory.decodeByteArray(
                            it,
                            0,
                            it.size
                        ).let {
                            val srcWidth = it.width
                            val srcHeight = it.height
                            val cutHeight = srcHeight / 3
                            val cutSrcBitmap = BitmapTool.ImageTransformer.cutByTarget(
                                it,
                                srcWidth,
                                cutHeight,
                                0,
                                0
                            ).let {
                                when(
                                    (1..2).random()
                                ){
                                    1 -> BitmapTool.ImageTransformer.flipHorizontally(it)
                                    else -> it
                                }
                            }
                            val cutBitmap = BitmapTool.ImageTransformer.cut(
                                cutSrcBitmap,
                                (srcWidth / 3),
                                cutHeight,
                            )
                            val gradientRectCutForShibuki = Bitmap.createScaledBitmap(
                                gradientRect,
                                cutBitmap.width,
                                cutBitmap.height,
                                true
                            )
                            ImageTransformer.mask(
                                gradientRectCutForShibuki,
                                cutBitmap,
                            )
                        }
                    }
                }

//                val shibukiBitmapRight = ImageTransformer.mask(
//                    gradientRectCutForShibuki,
//                    shibukiSrcBitmapList.first() as Bitmap,
//                )
//                val shibukiBitmapLeft = BitmapTool.ImageTransformer.flipHorizontally(
//                    shibukiSrcBitmapList.last() as Bitmap,
//                )
//                val shibukiBitmapList = listOf(
//                    shibukiBitmapRight,
//                    shibukiBitmapLeft,
//                )
//                val shibukiBitmap = shibukiBitmapRight.let {
//                    shibukiSrc ->
                val shibukiBitmap = BitmapTool.ImageTransformer.overlayBitmap(
                        shibukiBitmapList.last() as Bitmap,
                        shibukiBitmapList.first() as Bitmap,
                    )
//                }
//                FileSystems.writeFromByteArray(
//                    File(UsePath.cmdclickDefaultAppDirPath, "shibukiBitmap.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(shibukiBitmap)
//                )
//                FileSystems.writeFromByteArray(
//                    File(UsePath.cmdclickDefaultAppDirPath, "shibuki180Bitmap.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(shibukiOverlayBitmap)
//                )

//                FileSystems.writeFromByteArray(
//                    File("${UsePath.cmdclickDefaultAppDirPath}/pour", "skullMaskBitmap.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(skullMaskBitmap)
//                )
//                val pour1Bitmap = ImageTransformer.mask(
//                    gradientRect,
//                    AppCompatResources.getDrawable(
//                        context,
//                        R.drawable.pour1
//                    )?.toBitmap(
//                        pngSizePair.first,
//                        pngSizePair.second
//                    ) as Bitmap,
//                )

//                FileSystems.writeFromByteArray(
//                    File("${UsePath.cmdclickDefaultAppDirPath}/pour", "pour1Bitmap.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(pour1Bitmap)
//                )
                val animationDrawable = AnimationDrawable()
                val bitmapList = listOf(
                    R.drawable.pour1,
                    R.drawable.pour2,
                    R.drawable.pour3,
                ).mapIndexed { index, drawableId ->
                    AppCompatResources.getDrawable(
                        context,
                        drawableId,
                    )?.toBitmap(
                        pngSizePair.first,
                        pngSizePair.second,
                    )?.let {
                        maskBitmap ->
                        val maskedBitmap = BitmapTool.ImageTransformer.mask(
                            gradientRect,
                            maskBitmap,
//                            maskBitmap.width,
//                            maskBitmap.height,
//                            false
                        )
//                        FileSystems.writeFromByteArray(
//                            File("${UsePath.cmdclickDefaultAppDirPath}/pour", "maskBitmap${index}.png").absolutePath,
//                            BitmapTool.convertBitmapToByteArray(maskBitmap)
//                        )
//                        FileSystems.writeFromByteArray(
//                            File("${UsePath.cmdclickDefaultAppDirPath}/pour", "maskedBitmap${index}.png").absolutePath,
//                            BitmapTool.convertBitmapToByteArray(maskedBitmap)
//                        )
                        maskedBitmap
                    }
                }
//                val requestBuilder: RequestBuilder<Drawable> =
//                    Glide.with(context)
//                        .asDrawable()
//                        .sizeMultiplier(0.1f)
                bitmapList.forEachIndexed { index, bitmap ->
//                    delay(300)
//                    Glide
//                        .with(context)
//                        .load(bitmap)
//                        .skipMemoryCache( true )
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .thumbnail( requestBuilder )
//                        .into(this)
//                    bitmap ->
                    if(bitmap == null) return@forEachIndexed
                    val duration = when(index){
                        0 -> 150
                        1 -> 100
                        2 -> 10
                        else -> 70
                    }
                    animationDrawable.addFrame(
                        BitmapDrawable(imageViewContext.resources, bitmap),
                        duration
                    )
                }
                animationDrawable.isOneShot = true
                setImageDrawable(animationDrawable)
                animationDrawable.start()
                YoYo.with(Techniques.FadeOut)
                    .duration(600) // 400
                    .repeat(0)
                    .playOn(this)
                CoroutineScope(Dispatchers.Main).launch {
                    gridDialogObj?.findViewById<AppCompatImageView>(
                        R.id.grid_dialog_v2_dimm_image_view
                    )?.apply setBkGridImage@ {
                        imageTintList = ColorStateList.valueOf(
                            Color.parseColor(waterColor)
                        )
//                        val maskBitmap = BitmapTool.ImageTransformer.exchangeColorToBlack(
//                            it,
//                        )
//                        val maskBkBitmap = BitmapTool.ImageTransformer.makeRect(
//                            waterColor,
//                            maskBitmap.width,
//                            maskBitmap.height,
//                        )
//                        val maskedBitmap = BitmapTool.ImageTransformer.maskImageByTransparent(
//                            maskBkBitmap,
//                            maskBitmap,
//                        )
//                        BitmapTool.ImageTransformer.adjustOpacity(
//                            maskedBitmap,
//                            170,
//                        )
                        val bkShibukiBitmap =  shibukiBitmapList.random()
                            ?: return@setBkGridImage
                        BitmapTool.ImageTransformer.cutByTarget(
                            bkShibukiBitmap,
                            bkShibukiBitmap.width,
                            (bkShibukiBitmap.height / 3),
                            0,
                            0

                        ).let {
                            setImageBitmap(it)
                        }
//                        setImageBitmap(shibukiBitmapList.random())
//                        setImageBitmap(shibukiBitmapList.random())
//                        BitmapTool.ImageTransformer.adjustOpacity(
//                            shibukiBitmapList.random(),
//                            250
//                        ).let {
//                            setImageBitmap(shibukiBitmapList.random(),)
//                        }
                        isVisible = true
                        YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(this@setBkGridImage)

                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(250)
                    }
                    withContext(Dispatchers.Main){
                        titleBkImage?.apply setTitleBk@ {
                            setImageBitmap(revGradientRect)
                            CoroutineScope(Dispatchers.Main).launch {
                                YoYo.with(Techniques.FadeIn)
                                    .duration(300)
                                    .repeat(0)
                                    .playOn(this@setTitleBk)
                            }
                        }
                        gridDialogObj?.findViewById<AppCompatImageView>(
                            R.id.grid_dialog_v2_title_effect_image1
                        )?.apply setEffectImage@ {
                            val requestBuilder: RequestBuilder<Drawable> =
                                Glide.with(context)
                                    .asDrawable()
                                    .sizeMultiplier(0.1f)
                            Glide
                                .with(context)
                                .load(shibukiBitmap)
//                                .transition(DrawableTransitionOptions.withCrossFade())
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .thumbnail(requestBuilder)
                                .into(this@setEffectImage)
//                            setImageBitmap(shibukiBitmap)
                            imageTintList =  ColorStateList.valueOf(
                                Color.parseColor(waterColor)
                            )
//                            CoroutineScope(Dispatchers.Main).launch{
//                                withContext(Dispatchers.Main){
//                                    val constraintLayoutParam =
//                                        this@setTitleBk.layoutParams as ConstraintLayout.LayoutParams
//                                    val minusRatio = 0.075
//                                    var widthRelative = 1.5
//                                    for(i in 1..12){
//                                        widthRelative -= minusRatio
//                                        constraintLayoutParam.dimensionRatio = "H,1:${widthRelative}"
//                                        this@setTitleBk.layoutParams = constraintLayoutParam
//                                        delay(20)
//                                    }
//                                }
//                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                YoYo.with(Techniques.FadeOut)
                                    .duration(500)
                                    .repeat(0)
                                    .playOn(this@setEffectImage)
                            }
                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            val imagePathList = withContext(Dispatchers.IO) {
                makeImageList(
                    imagePathListNewlineSepaStr
                )
            }
            val gridListAdapter = withContext(Dispatchers.Main) {
                GridListAdapter(
                    imagePathList
                )
            }
            val holderConstraint = withContext(Dispatchers.Main) {
                gridDialogObj?.findViewById<ConstraintLayout>(
                    R.id.grid_dialog_v2_constraint
                )
            }
            val gridListRecyclerView = withContext(Dispatchers.Main) {
                gridDialogObj?.findViewById<RecyclerView>(
                    R.id.grid_dialog_v2_grid_view
                )
            }
            gridDialogObj?.setOnCancelListener {
                exitDialog(
                    holderConstraint,
                    gridListRecyclerView,
                    String()
                )
            }
            withContext(Dispatchers.Main) {
                gridListRecyclerView?.apply {
                    adapter = gridListAdapter
                    layoutManager = PreLoadGridLayoutManager(
                        context,
                        3,
                        true,
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        YoYo.with(Techniques.FadeIn)
                            .duration(300) // 1000
                            .repeat(0)
                            .playOn(this@apply)
                    }
                }
            }
            withContext(Dispatchers.Main){
                invokeListItemSetClickListenerForListDialog(
                    holderConstraint,
                    gridListRecyclerView,
                    gridListAdapter,
                )
            }
        }
    }

    private fun originalDimmEffect(
        imageFrame: FrameLayout?,
        titleAndDimColor: String,
    ){
        val goalAlpha = 0.6f
        imageFrame?.apply {
            dimAlphaJob?.cancel()
            dimAlphaJob = CoroutineScope(Dispatchers.IO).launch{
                withContext(Dispatchers.Main) {
                    setBackgroundColor(Color.parseColor(titleAndDimColor))
                    alpha = 1f
                    isVisible = true
                }
                withContext(Dispatchers.IO){
                    delay(500)
                }
                withContext(Dispatchers.Main){
                    val animation1 = AlphaAnimation(1f, goalAlpha)
                    animation1.duration = 200
//                    animation1.startOffset = 5000
                    animation1.fillAfter = true
                    startAnimation(animation1)
                }
            }
//            alpha = 0f
//            CoroutineScope(Dispatchers.IO).launch {
//                for (i in 1..loopTimes) {
//                    withContext(Dispatchers.IO) {
//                        delay(200)
//                    }
//                    withContext(Dispatchers.Main) {
//                        alpha += plusAlpha
//                    }
//                }
//            }
        }
    }

    private fun invokeListItemSetClickListenerForListDialog(
        holderConstraint: ConstraintLayout?,
        gridListView: RecyclerView?,
        gridListAdapter: GridListAdapter,
    ) {
        gridListAdapter.itemClickListener = object: GridListAdapter.OnItemClickListener {
            override fun onItemClick(holder: GridListAdapter.GridListViewHolder) {
                val selectedImagePath =
                    gridListAdapter.imagePathList.getOrNull(holder.bindingAdapterPosition)
                        ?: return
                exitDialog(
                    holderConstraint,
                    gridListView,
//                    gridListBkRecyclerView,
                    selectedImagePath,
                )
            }
        }
    }


    private fun makeImageList(
        listCon: String
    ): List<String> {
        return listCon
            .split("\n")
            .filter {
                it.trim().isNotEmpty()
            }
    }

    private fun exitDialog(
        holderConstraint: ConstraintLayout?,
        gridListView: RecyclerView?,
//        gridListBkView: RecyclerView?,
        returnStr: String,
    ){
        gridListView?.apply {
            layoutManager = null
            adapter = null
            recycledViewPool.clear()
            removeAllViews()
        }

//        gridListBkView?.apply {
//            layoutManager = null
//            adapter = null
//            recycledViewPool.clear()
//            removeAllViews()
//        }

        holderConstraint?.removeAllViews()
        returnValue = returnStr
        gridDialogObj?.dismiss()
        gridDialogObj = null
        onDialog = false
    }
}
