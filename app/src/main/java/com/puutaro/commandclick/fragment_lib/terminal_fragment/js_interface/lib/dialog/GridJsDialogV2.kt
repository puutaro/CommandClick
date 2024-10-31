package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.component.adapter.GridListAdapter
import com.puutaro.commandclick.custom_manager.PreLoadGridLayoutManager
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.BitmapTool.ImageTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        gridDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogTheme
        )
        gridDialogObj?.setContentView(
           R.layout.grid_dialog_v2_layout
        )
        val titleTextView = gridDialogObj?.findViewById<OutlineTextView>(
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
        gridDialogObj?.show()
        CoroutineScope(Dispatchers.Main).launch {
            gridDialogObj?.findViewById<AppCompatImageView>(
                R.id.grid_dialog_v2_pour_image1
            )?.apply {
                val imageViewContext = this.context
                val pngSizePair = Pair(200, 300)
                val gradTopColorStr = CmdClickColorStr.entries.random().str
                val gradientRect = BitmapTool.GradientBitmap.makeGradientBitmap2(
                    pngSizePair.first,
                    pngSizePair.second,
                    listOf(
                        "#00000000",
                        gradTopColorStr,
                    ).map {
                        Color.parseColor(it)
                    }.toIntArray(),
                    BitmapTool.GradientBitmap.GradOrient.VERTICAL_BOTTOM_TO_TOP,
                )
                val revGradientRect = BitmapTool.rotate(
                    gradientRect,
                    180f
                )
//                val titleBkRect = BitmapTool.ImageTransformer.makeRect(
//                        gradTopColorStr,
//                    pngSizePair.first,
//                    pngSizePair.second,
//                )
//                FileSystems.writeFromByteArray(
//                    File("${UsePath.cmdclickDefaultAppDirPath}/pour", "gradientRect.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(gradientRect)
//                )
                val skullMaskBitmap = ImageTransformer.mask(
                    gradientRect,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.fannel_skull
                    )?.toBitmap(
                        pngSizePair.first,
                        pngSizePair.second
                    ) as Bitmap,
                )
                val shibukiBitmap = ImageTransformer.mask(
                    gradientRect,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.sibuki
                    )?.toBitmap(
                        pngSizePair.first,
                        pngSizePair.second
                    ) as Bitmap,
                )

//                FileSystems.writeFromByteArray(
//                    File("${UsePath.cmdclickDefaultAppDirPath}/pour", "skullMaskBitmap.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(skullMaskBitmap)
//                )
                val pour1Bitmap = ImageTransformer.mask(
                    gradientRect,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.pour1
                    )?.toBitmap(
                        pngSizePair.first,
                        pngSizePair.second
                    ) as Bitmap,
                )

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
                        0 -> 70
                        1 -> 70
                        2 -> 200
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
                    .duration(700) // 400
                    .repeat(0)
                    .playOn(this)
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(250)
                    }
                    withContext(Dispatchers.Main){
                        gridDialogObj?.findViewById<AppCompatImageView>(
                            R.id.grid_dialog_v2_title_bk_image
                        )?.apply setTitleBk@ {
                            setImageBitmap(revGradientRect)
                            CoroutineScope(Dispatchers.Main).launch {
                                YoYo.with(Techniques.FadeIn)
                                    .duration(1200)
                                    .repeat(0)
                                    .playOn(this@setTitleBk)
                            }
                        }
                        gridDialogObj?.findViewById<AppCompatImageView>(
                            R.id.grid_dialog_v2_title_effect_image1
                        )?.apply setTitleBk@ {
                            setImageBitmap(shibukiBitmap)
                            imageTintList =  ColorStateList.valueOf(
                                Color.parseColor(gradTopColorStr)
                            )
                            CoroutineScope(Dispatchers.Main).launch {
                                YoYo.with(Techniques.FadeOut)
                                    .duration(1400)
                                    .repeat(0)
                                    .playOn(this@setTitleBk)
                            }
                        }
                        gridDialogObj?.findViewById<FrameLayout>(
                            R.id.grid_dialog_v2_title_bk_frame
                        )?.apply setTitleFrame@ {

//                            setColorFilter(
//                                Color.parseColor(gradTopColorStr)
//                            )
//                            backgroundTintList = ColorStateList.valueOf(
//                                Color.parseColor(gradTopColorStr)
//                            )
//                            backgroundTintList = ColorStateList.valueOf(
//                                Color.parseColor(gradTopColorStr)
//                            )
//                            background =  BitmapDrawable(imageViewContext.resources, titleBkRect)
                            YoYo.with(Techniques.Pulse)
                                .duration(1000)
                                .repeat(0)
                                .playOn(this@setTitleFrame)
//                            setImageBitmap(titleBkRect)
//                            setColorFilter(
//                                Color.parseColor(gradTopColorStr)
//                            )
//                            Glide
//                                .with(context)
//                                .load(gradientRect)
////                                .transition(DrawableTransitionOptions.withCrossFade())
//                                .skipMemoryCache( true )
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .thumbnail( requestBuilder )
//                                .into(this@setTitleBk)
////                            setImageBitmap(gradientRect)
//                            YoYo.with(Techniques.FadeIn)
//                                .duration(1000)
//                                .repeat(0)
//                                .playOn(this@setTitleBk)
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
//            withContext(Dispatchers.Main) {
//                gridDialogObj?.findViewById<AppCompatImageView>(
//                    R.id.grid_dialog_v2_cancel
//                )?.apply {
//                    ExecSetToolbarButtonImage.setImageButton(
//                        this,
//                        CmdClickIcons.CANCEL
//                    )
//                    setOnClickListener {
//                        exitDialog(
//                            holderConstraint,
//                            gridListRecyclerView,
//                            String()
//                        )
//                    }
//                }
//            }
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
                            .duration(1000) // 1000
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
        returnStr: String,
    ){
        gridListView?.layoutManager = null
        gridListView?.adapter = null
        gridListView?.recycledViewPool?.clear()
        gridListView?.removeAllViews()
        holderConstraint?.removeAllViews()
        returnValue = returnStr
        gridDialogObj?.dismiss()
        gridDialogObj = null
        onDialog = false
    }
}
