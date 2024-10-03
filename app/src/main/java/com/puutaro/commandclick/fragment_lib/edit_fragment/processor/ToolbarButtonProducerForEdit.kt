package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.proccess.tool_bar_button.ToolbarButtonHandler
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import java.io.File


object ToolbarButtonProducerForEdit {

    fun make(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ) {
        val context = editFragment.context
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "toolbar.txt").absolutePath,
//            listOf(
//                "toolbarButtonBariantForEdit: ${toolbarButtonBariantForEdit.str}",
//                "howSetButton: ${howSetButton(
//                    editFragment,
//                    toolbarButtonBariantForEdit
//                )}",
//                "toolBarButtonVisibleMap: ${editFragment.toolBarButtonVisibleMap}",
//                "toolbarButtonConfigMap: ${editFragment.toolbarButtonConfigMap}",
//            ).joinToString("\n")
//        )
        if(
            !howSetButton(
                editFragment,
                toolbarButtonBariantForEdit
            )
            || context == null
        ) return


        val buttonLayout = makeButtonLayout(
            editFragment,
            toolbarButtonBariantForEdit,
        )
        val iconIdToCaption = editFragment.toolBarButtonIconMap[toolbarButtonBariantForEdit]
            ?: Pair(R.drawable.icons8_ok, String())


        buttonLayout.findViewById<AppCompatImageView>(R.id.icon_caption_layout_image)?.let {
            imageButtonView ->
            setImageView(
                editFragment,
                imageButtonView,
                toolbarButtonBariantForEdit,
                iconIdToCaption
            )
        }
        buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)?.let {
            captionTextView ->
            setCaption(
                captionTextView,
                iconIdToCaption,
            )
        }
        buttonLayout.setOnLongClickListener {
                buttonInnerView ->
            onLongClickHandler(
                editFragment,
                buttonLayout,
                toolbarButtonBariantForEdit,
            )
            true
        }

        buttonLayout.setOnClickListener { view ->
            when (toolbarButtonBariantForEdit) {
                ToolbarButtonBariantForEdit.HISTORY -> {
                    FannelHistoryButtonEvent.invoke(editFragment)
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.SETTING,
                ToolbarButtonBariantForEdit.OK,
                ToolbarButtonBariantForEdit.EDIT,
                ToolbarButtonBariantForEdit.EXTRA,
                -> {
                    ToolbarButtonHandler.handle(
                        editFragment,
                        false,
                        toolbarButtonBariantForEdit,
                        buttonLayout
//                        makeButtonView,
                    )
                }
                ToolbarButtonBariantForEdit.CANCEL -> {}
            }
        }
        editFragment.binding.editToolBarLinearLayout.addView(
            buttonLayout
//            makeButtonView
        )
    }

    private fun makeButtonLayout(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ): FrameLayout {
        val context = editFragment.context
        val inflater = LayoutInflater.from(context)
        val buttonLayout = inflater.inflate(
            R.layout.icon_caption_layout,
            null
        ) as FrameLayout
        buttonLayout.tag = toolbarButtonBariantForEdit.str
        val param = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        )
        val marginDp = ScreenSizeCalculator.toDp(
            context,
            context?.resources?.getDimension(R.dimen.toolbar_button_horizon_margin) ?: 0
        )
        param.marginStart = marginDp
        param.marginEnd = marginDp
        param.gravity = Gravity.CENTER
        param.weight = editFragment.buttonWeight
        buttonLayout.layoutParams = param
        return buttonLayout
    }

    private fun setCaption(
        captionTextView: OutlineTextView,
        iconIdToCaption: Pair<Int, String>
    ) {
        captionTextView.text =iconIdToCaption.second.ifEmpty {
            val iconId = iconIdToCaption.first
            CmdClickIcons.values().firstOrNull {
                it.id == iconId
            }?.str ?: String()
        }
        captionTextView.setStrokeColor(R.color.white)
        captionTextView.setFillColor(R.color.fill_gray)
    }

    private fun setImageView(
        editFragment: EditFragment,
        imageView: AppCompatImageView,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        iconIdToCaption: Pair<Int, String>
    ): AppCompatImageView{
        val imageViewContext = imageView.context
        val iconId = iconIdToCaption.first
        imageView.tag = CmdClickIcons.values().firstOrNull {
            it.id == iconId
        }?.str ?: toolbarButtonBariantForEdit.str
        val icon = CmdClickIcons.values().firstOrNull {
            it.id == iconId
        }
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(imageViewContext)
                .asDrawable()
                .sizeMultiplier(0.1f)
        val isImageFile =
            ExecSetToolbarButtonImage.isImageFile(icon?.assetsPath)
        when(isImageFile && icon != null) {
            true -> ExecSetToolbarButtonImage.setForEditFragment(
                editFragment,
            )
            else -> Glide.with(imageViewContext)
                .load(iconId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageView)
        }
        return imageView
    }

    private fun howSetButton(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit
    ): Boolean {
        return editFragment.toolBarButtonVisibleMap.filter {
            it.key == toolbarButtonBariantForEdit
        }.values.firstOrNull() ?: true
    }

    private fun onLongClickHandler(
        editFragment: EditFragment,
        buttonLayout: FrameLayout,
//        settingButtonView: ImageButton,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ){
        when (toolbarButtonBariantForEdit) {
            ToolbarButtonBariantForEdit.HISTORY -> {
//                HistoryButtonSwitcher.switch(
//                    editFragment,
//                    editFragment.context?.getString(
//                        R.string.edit_terminal_fragment
//                    ),
//                    editFragment.historySwitch,
//                    urlHistoryButtonEvent,
//                    CLICLTYPE.LONG
//                )
            }
            ToolbarButtonBariantForEdit.SETTING,
            ToolbarButtonBariantForEdit.OK,
            ToolbarButtonBariantForEdit.EDIT,
            ToolbarButtonBariantForEdit.EXTRA -> {
                ToolbarButtonHandler.handle(
                    editFragment,
                    true,
                    toolbarButtonBariantForEdit,
                    buttonLayout
//                    settingButtonView,
                )
            }
            ToolbarButtonBariantForEdit.CANCEL -> {}
        }
    }
}