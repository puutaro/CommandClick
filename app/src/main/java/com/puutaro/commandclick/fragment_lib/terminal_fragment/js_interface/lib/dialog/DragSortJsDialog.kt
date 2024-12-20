package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.component.adapter.DragSortRecyclerAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.list.ListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference


class DragSortJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    companion object {
        private var dragSortDialogObj: Dialog? = null
        private var dimAlphaJob: Job? = null
        private var setRecyclerJob: Job? = null
        private var setBkImageJob: Job? = null
    }

    fun create(
        title: String,
        dragSortFilePath: String,
    ){
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    dragSortFilePath,
                )
            }
        }
    }

    fun execCreate(
        titleSrc: String,
        dragSortFilePath: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return Unit.also {
                simpleExitDialog()
            }
        val context = terminalFragment.context
            ?: return Unit.also {
                simpleExitDialog()
            }

        val dragSortFileFileObj = File(dragSortFilePath)
        if(
            !dragSortFileFileObj.isFile
            ) return Unit.also {
            simpleExitDialog()
        }
        dragSortDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogThemeWithNoDimm
        )
        dragSortDialogObj?.setContentView(
            R.layout.drag_sort_dialog_layout
        )
        dragSortDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        val dragSortDialogConstraint = dragSortDialogObj?.findViewById<ConstraintLayout>(
            R.id.drag_sort_dialog_constraint
        )
        val dragSortRecyclerView =
            dragSortDialogObj?.findViewById<RecyclerView>(
                R.id.drag_sort_dialog_recycler_view
            )

        dragSortDialogObj?.setOnCancelListener {
            exitDialog(
                dragSortDialogConstraint,
                dragSortRecyclerView
            )
        }
        dragSortDialogObj?.window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT),
            )
        }
        dragSortDialogObj?.show()
        val title = titleSrc.ifEmpty {
            "Drag sort"
        }
        setBkImageJob?.cancel()
        setBkImageJob = CoroutineScope(Dispatchers.Main).launch {

//            withContext(Dispatchers.Main){
//                dragSortDialogObj?.findViewById<AppCompatImageView>(
//                    R.id.drag_sort_dialog_bk_image_bk_cut_in
//                )?.apply {
//                    YoYo.with(Techniques.FadeOut)
//                        .duration(500)
//                        .repeat(0)
//                        .playOn(this@apply)
//                }
//            }
            val imageSizePair =  withContext(Dispatchers.Main) {
                Pair(
                    200f,
                    400f
                )
            }
            val textBitmapList = withContext(Dispatchers.IO) {
                val bkTitleText = title.replace(
                    Regex("[ 　]"),
                    "C"
                )
                val repeatNum = bkTitleText.let {
                    1800 / it.length
                }

                (1..3).map {

                    BitmapTool.DrawText.drawTextToBitmapByRandom(
                        bkTitleText.repeat(repeatNum),
                        170.toFloat(),
                        340.toFloat(),
                        (10..15).random().toFloat(),
                        Color.BLACK,
                    ).let { textBitmapSrc ->
                        val scaledTextBitmapSrc = Bitmap.createScaledBitmap(
                            textBitmapSrc,
                            imageSizePair.first.toInt(),
                            imageSizePair.second.toInt(),
                            true
                        )
                        scaledTextBitmapSrc
                    }
                }
            }

            val allowBitmapList = withContext(Dispatchers.IO) {
                listOf(
                    R.drawable.brushi_allow_dot,
                    R.drawable.brushi_allow22,
                    R.drawable.brushi_allow3,
                ).map {
                    AppCompatResources.getDrawable(
                        context,
                        it,
                    )?.toBitmap()
                }
            }
            val rotateAllowBitmapList = withContext(Dispatchers.IO) {
                allowBitmapList.map { allow ->
                    if (allow == null) return@map null
                    BitmapTool.rotate(
                        allow,
                        180f
                    )
                }
            }
            val allColorUIdList = listOf(
                CmdClickColor.LIGTH_BLUE.id,
                R.color.true_blue_color,
                CmdClickColor.NABY.id,
                CmdClickColor.LIGHT_GREEN.id,
                CmdClickColor.DARK_GREEN.id,
                CmdClickColor.OLIVE_GREEN.id,
                CmdClickColor.RED.id,
                CmdClickColor.AO.id,
                CmdClickColor.LIGHT_AO.id,
                CmdClickColor.ORANGE.id,
                CmdClickColor.LIGHT_ORANGE.id,
                CmdClickColor.BROWN.id,
                CmdClickColor.WHITE.id,
                CmdClickColor.YELLOW.id,
                CmdClickColor.BLACK.id,
                R.color.purple_200,
            )
            val shuujiColorIdList = listOf(
                CmdClickColor.LIGTH_BLUE.id,
                R.color.true_blue_color,
                CmdClickColor.LIGHT_GREEN.id,
                CmdClickColor.RED.id,
                CmdClickColor.LIGHT_AO.id,
                CmdClickColor.ORANGE.id,
                CmdClickColor.LIGHT_ORANGE.id,
                CmdClickColor.WHITE.id,
                CmdClickColor.YELLOW.id,
                R.color.purple_200,
                CmdClickColor.BLACK.id,
                CmdClickColor.NABY.id,
                CmdClickColor.DARK_GREEN.id,
            )
            val darkColorIdList = listOf(
                R.color.true_blue_color,
                CmdClickColor.NABY.id,
                CmdClickColor.DARK_GREEN.id,
                CmdClickColor.OLIVE_GREEN.id,
                CmdClickColor.BROWN.id,
                CmdClickColor.BLACK.id,
            )
            val useColorIdList = mutableListOf<Int>()
            val shuujiColorId = withContext(Dispatchers.IO) {
                val shuujiColorStrSrc = shuujiColorIdList.filter {
                    !useColorIdList.contains(it)
                }.random()
                useColorIdList.add(shuujiColorStrSrc)
                shuujiColorStrSrc
            }
            val textColorId = withContext(Dispatchers.IO) {
                val textColorIdSrc = allColorUIdList.filter {
                    !useColorIdList.contains(it)
                }.random()
                useColorIdList.add(textColorIdSrc)
                textColorIdSrc
            }
            val goalAlpha = when(
                darkColorIdList.contains(shuujiColorId)
            ){
                true -> 0.4f
                else -> 0.8f
            }
            val strokeColorId = withContext(Dispatchers.IO) {
                val isLightColor = darkColorIdList.contains(textColorId)
                val strokeColorIdSrc = allColorUIdList.filter {
                    !useColorIdList.contains(it)
                }.let {
                    entryColorIdList ->
                    if(
                        !isLightColor
                        ) return@let entryColorIdList
                    entryColorIdList.filter {
                        !darkColorIdList.contains(it)
                    }
                }.random()
                useColorIdList.add(strokeColorIdSrc)
                strokeColorIdSrc
            }
            withContext(Dispatchers.Main){
                dragSortDialogObj?.findViewById<AppCompatImageView>(
                    R.id.drag_sort_dialog_bk_image_bk_allow1
                )?.apply {
                    imageTintList = AppCompatResources.getColorStateList(
                        context,
                        shuujiColorId
                    )
                    val animationDrawable = makeAllowDuration(
                        context,
                        allowBitmapList
                    )
//                    AnimationDrawable()
//                    allowBitmapList.forEachIndexed { index, allow ->
//                        if(allow == null) return@forEachIndexed
//                        val duration = makeAllowDuration(index)
//                        animationDrawable.addFrame(
//                            BitmapDrawable(context.resources, allow),
//                            duration
//                        )
//                    }
//                    animationDrawable.isOneShot = true
                    setImageDrawable(animationDrawable)
                    animationDrawable.start()

                }
                dragSortDialogObj?.findViewById<AppCompatImageView>(
                    R.id.drag_sort_dialog_bk_image_bk_allow2
                )?.apply {
                    imageTintList = AppCompatResources.getColorStateList(
                        context,
                        shuujiColorId
                    )
                    val animationDrawable = makeAllowDuration(
                        context,
                        rotateAllowBitmapList
                    )
//                    val animationDrawable = AnimationDrawable()
//                    rotateAllowBitmapList.forEachIndexed { index, allow ->
//                        if(allow == null) return@forEachIndexed
//                        val duration = makeAllowDuration(index)
//                        animationDrawable.addFrame(
//                            BitmapDrawable(context.resources, allow),
//                            duration
//                        )
//                    }
//                    animationDrawable.isOneShot = true
                    setImageDrawable(animationDrawable)
                    animationDrawable.start()
                }
            }
            withContext(Dispatchers.Main) {
                dragSortDialogObj?.findViewById<AppCompatImageView>(
                    R.id.drag_sort_dialog_title_image
                )?.apply {
                    val titleLimitLength = 20
                    val titleText = title.take(titleLimitLength).mapIndexed { index, c ->
                        if (index == 0) {
                            return@mapIndexed c.uppercase()
                        }
                        c
                    }.joinToString(" ")
                    val titleTextSize = sizeCalculator(
                        100f,
                        60f,
                        titleLimitLength,
                        titleText,
                    )
//                    ((80f * baseTextSize) / titleText.length).let {
//                        titleTextSizeSrc ->
//                        if(
//                            titleTextSizeSrc > maxSize
//                        ) return@let maxSize
//                        if(
//                            titleTextSizeSrc > titleTextSize
//                            ) return@let titleTextSizeSrc
//                        titleTextSize
//                    }
                    val minStrokeSize = 5f
                    val strokeWidth =
                        sizeCalculator(
                            15f,
                            minStrokeSize,
                            titleLimitLength,
                            titleText,
                        ).let {
                            if(it < minStrokeSize) return@let minStrokeSize
                            it
                        }
//                    let {
//                        val maxSize = 6f
//                        val minSize = 3f
//                        val maxSizeDelta = maxSize - minSize
//                        val maxLengthDelta = titleLimitLength.toFloat()
//                        val inclination = maxSizeDelta / maxLengthDelta
//                        val curLength = (titleText.length / 2).let curLength@ {
//                            if(it == 0) return@curLength 1
//                            it
//                        }
//                        val strokeWidth = minSize + inclination * curLength
//                        strokeWidth
//                    }
//                    (
//                            ((maxStrokeSize * baseTextSize) / titleText.length + 1)
//                            ).let {
//                                strokeWidthSrc ->
//                                if(
//                                    strokeWidthSrc > maxStrokeSize
//                                    ) return@let maxStrokeSize
//                            strokeWidthSrc
//                        }
                    val textColor = ContextCompat.getColor(
                        context,
                        textColorId
                    )
                    val strokeColor = ContextCompat.getColor(
                        context,
                        strokeColorId
                    )
                    val titleBitmap = BitmapTool.DrawText.drawTextToBitmap(
                        titleText,
                        imageSizePair.first,
                        imageSizePair.second,
                        Color.TRANSPARENT,
                        titleTextSize,
                        textColor,
                        strokeColor,
                        strokeWidth,
                        1.5f,
                        null,
                    )
                    val textBitmapListSrc = withContext(Dispatchers.IO) {
//                        FileSystems.writeFromByteArray(
//                            File(UsePath.cmdclickDefaultAppDirPath, "ltitleBitmap.png").absolutePath,
//                            BitmapTool.convertBitmapToByteArray(titleBitmap),
//                        )
                        textBitmapList.mapIndexed { index, textBitmap ->
//                            FileSystems.writeFromByteArray(
//                                File(UsePath.cmdclickDefaultAppDirPath, "ltextBitmap${index}.png").absolutePath,
//                                BitmapTool.convertBitmapToByteArray(textBitmap),
//                            )
                            return@mapIndexed BitmapTool.ImageTransformer.mask(
                                titleBitmap,
                                textBitmap,
                            )
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val animationDrawable = AnimationDrawable()
                        textBitmapListSrc.forEach {
                            animationDrawable.addFrame(
                                BitmapDrawable(context.resources, it),
                                500
                            )
                        }
                        animationDrawable.isOneShot = false
                        setImageDrawable(animationDrawable)
                        animationDrawable.start()
                        YoYo.with(Techniques.FadeIn)
                            .duration(700)
                            .repeat(0)
                            .playOn(this@apply)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                dragSortDialogObj?.findViewById<FrameLayout>(
                    R.id.drag_sort_dialog_bk_image_bk
                )?.apply {
                    dimAlphaJob?.cancel()
                    dimAlphaJob = CoroutineScope(Dispatchers.IO).launch{
                        withContext(Dispatchers.IO){
                            delay(200)
                        }
                        withContext(Dispatchers.IO) {
                            val changeTimes = 4
                            val plusAlpha = goalAlpha / changeTimes
                            val delayTime = 200L / changeTimes
                            for(i in 1..changeTimes) {
                                delay(delayTime)
                                if (
                                    dragSortDialogObj?.isShowing != true
                                    || alpha >= goalAlpha
                                ) {
                                    alpha = goalAlpha
                                    dimAlphaJob?.cancel()
                                    break
                                }
                                alpha += plusAlpha
                            }
                        }
                    }
                }
            }
        }
        setRecyclerJob?.cancel()
        setRecyclerJob = CoroutineScope(Dispatchers.Main).launch{
            dragSortRecyclerView?.apply {
                val preloadLayoutManager: RecyclerView.LayoutManager = PreLoadLayoutManager(
                    context,
                    true
                )
                layoutManager = preloadLayoutManager
            }
            val dragSortList = withContext(Dispatchers.IO) {
                makeDragSortList(
                    dragSortFilePath
                )
            }
            val dragSortRecyclerAdapter = withContext(Dispatchers.IO) {
                DragSortRecyclerAdapter(
                    context,
                    dragSortList.toMutableList(),
                )
            }
            withContext(Dispatchers.Main) {
//                val itemDecoration: ItemDecoration =
//                    DividerItemDecoration(
//                        context,
//                        DividerItemDecoration.VERTICAL
//                    )
//                dragSortRecyclerView?.addItemDecoration(itemDecoration)
                setItemTouchHelper(
                    dragSortRecyclerView,
                    dragSortRecyclerAdapter,
                )
            }
            withContext(Dispatchers.IO) {
                dragSortRecyclerView?.adapter = dragSortRecyclerAdapter
            }

            withContext(Dispatchers.Main) {
                dragSortDialogObj?.findViewById<AppCompatImageView>(
                    R.id.drag_sort_dialog_cancel
                )?.apply {
                    ExecSetToolbarButtonImage.setImageButton(
                        this,
                        CmdClickIcons.CANCEL
                    )
                    setOnClickListener {
                        exitDialog(
                            dragSortDialogConstraint,
                            dragSortRecyclerView,
                        )
                    }
                }
            }
            withContext(Dispatchers.Main) {
                dragSortDialogObj?.findViewById<AppCompatImageView>(
                    R.id.drag_sort_dialog_ok
                )?.apply {
                    ExecSetToolbarButtonImage.setImageButton(
                        this,
                        CmdClickIcons.OK
                    )
                    setOnClickListener {
                        exitDialog(
                            dragSortDialogConstraint,
                            dragSortRecyclerView
                        )
                        FileSystems.writeFile(
                            dragSortFilePath,
                            dragSortRecyclerAdapter.dragSortList
                                .reversed()
                                .joinToString("\n")
                        )
                    }
                }
            }
        }
    }

    private fun makeAllowDuration(
        context: Context,
        rotateAllowBitmapList: List<Bitmap?>
    ): AnimationDrawable {
        return AnimationDrawable().apply {
            rotateAllowBitmapList.forEachIndexed {
                                                 index, allow ->
                if(
                    allow == null
                ) return@forEachIndexed
                val duration = when(index){
                    0 -> 200
                    1 -> 150
                    else -> 100
                }
                addFrame(
                    BitmapDrawable(
                        context.resources,
                        allow
                    ),
                    duration
                )
            }
            isOneShot = true
        }
    }

    private fun sizeCalculator(
        maxSize: Float,
        minSize: Float,
        titleLimitLength: Int,
        titleText: String,
    ): Float {
//        val maxSize = 100f
//        val minSize = 60f
        val maxSizeDelta = maxSize - minSize
        val maxLengthDelta = titleLimitLength.toFloat()
        val inclination = maxSizeDelta / maxLengthDelta
        val marginLength = 1
        val curLength = (titleText.length / 2).let curLength@ {
            if(it == 0) return@curLength 1
            if(it % 2 == 1) return@curLength it + 1
            it
        } + marginLength
        val textSize = maxSize - inclination * curLength
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lsize.txt").absolutePath,
//            listOf(
//                "maxSizeDelta: ${maxSizeDelta}",
//                "maxLengthDelta: ${maxLengthDelta}",
//                "inclination: ${inclination}",
//                "curLength: ${curLength}",
//                "textSize: ${textSize}",
//            ).joinToString("\n")
//        )
        return textSize
    }

    private fun simpleExitDialog(){
        exitDialog(
            null,
            null,
        )
    }

    private fun exitDialog(
        dragSortDialogConstraint: ConstraintLayout?,
        dragSortRecyclerView: RecyclerView?,
    ){
        dimAlphaJob?.cancel()
        setRecyclerJob?.cancel()
        setBkImageJob?.cancel()
        dragSortRecyclerView?.layoutManager = null
        dragSortRecyclerView?.adapter = null
        dragSortRecyclerView?.recycledViewPool?.clear()
        dragSortRecyclerView?.removeAllViews()
        dragSortDialogConstraint?.removeAllViews()
        dragSortDialogObj?.dismiss()
        dragSortDialogObj = null
    }


    private fun makeDragSortList(
        listContentsFilePath: String,
    ): List<String> {
        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        FileSystems.createDirs(parentDir)
        return ReadText(
            listContentsFilePath
        ).textToList().filter {
            it.trim().isNotEmpty()
        }.reversed()
    }

    private fun setItemTouchHelper(
        recyclerView: RecyclerView?,
        dragSortRecyclerAdapter: DragSortRecyclerAdapter,
    ){
        val context = recyclerView?.context
            ?: return
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    val adapter = recyclerView.adapter as DragSortRecyclerAdapter
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    adapter.notifyItemMoved(from, to)
                    ListTool.switchList(
                        dragSortRecyclerAdapter.dragSortList,
                        from,
                        to,
                    )
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if(
                        direction != ItemTouchHelper.LEFT
                    ) return
                    val position = viewHolder.layoutPosition
                    dragSortRecyclerAdapter.notifyItemRemoved(position)
                    dragSortRecyclerAdapter.dragSortList.removeAt(position)
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                        viewHolder?.itemView?.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.light_ao
                            )
                        )
                    }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                    viewHolder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.trans
                        )
                    )
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }
}