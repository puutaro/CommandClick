package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.proccess.tool_bar_button.ToolbarButtonHandler
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator


object ToolbarButtonProducerForEdit {

//    var languageType = LanguageTypeSelects.JAVA_SCRIPT
//    var languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//            languageType
//        )
//    var settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//
//    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String

    fun make(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ) {
        val context = editFragment.context
//        val toolbarButtonHandler = ToolbarButtonHandler(
//            editFragment,
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
//        val imageView = makeImageView(
//            editFragment,
//            toolbarButtonBariantForEdit,
//            iconIdToCaption
//        )
        buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)?.let {
            captionTextView ->
            setCaption(
                captionTextView,
                iconIdToCaption,
            )
        }
//        val textView = makeTextView(
//            context,
//            iconIdToCaption
//        )
//        buttonLayout.addView(imageButtonView)
//        buttonLayout.addView(captionTextView)




//        insertImageButtonParam.weight = editFragment.buttonWeight
//        val makeButtonView = ImageButton(context)
//        ButtonColorSettingForToolbarButton.set(
//            editFragment,
//            toolbarButtonBariantForEdit,
//            makeButtonView
//        )
////        makeButtonView.imageTintList =
////            context?.getColorStateList(R.color.terminal_color)
//        makeButtonView.setBackgroundResource(android.R.drawable.list_selector_background)
//        makeButtonView.setImageResource(
//            editFragment.toolBarButtonIconMap[toolbarButtonBariantForEdit]
//                ?: R.drawable.icons8_ok
//        )
//        makeButtonView.backgroundTintList =
//            context?.getColorStateList(R.color.white)
////        it.getColorStateList(R.color.terminal_color)
//
//        ButtonStatusSettingsForToolbarButton.set(
//            editFragment,
//            toolbarButtonBariantForEdit,
//            makeButtonView
//        )
//        makeButtonView.layoutParams = insertImageButtonParam
//        makeButtonView.tag = toolbarButtonBariantForEdit.str
        buttonLayout.setOnLongClickListener {
                buttonInnerView ->
            onLongClickHandler(
                editFragment,
                buttonLayout,
//                makeButtonView,
                toolbarButtonBariantForEdit,
//                recordNumToMapNameValueInCommandHolder,
//                recordNumToMapNameValueInSettingHolder,
            )
            true
        }

        buttonLayout.setOnClickListener { view ->
            when (toolbarButtonBariantForEdit) {
                ToolbarButtonBariantForEdit.HISTORY -> {
                    FannelHistoryButtonEvent.invoke(editFragment)
//                    val editExecuteTerminalFragment = TargetFragmentInstance()
//                        .getFromFragment<TerminalFragment>(
//                            editFragment.activity,
//                            context.getString(R.string.edit_terminal_fragment)
//                        )
//                    if(
//                        editExecuteTerminalFragment != null
//                    ){
//                        val listener = context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
//                        listener?.onKeyBoardVisibleChangeForEditFragment(
//                            false,
//                            true
//                        )
//                    }
//                    HistoryButtonSwitcher.switch(
//                        editFragment,
//                        editFragment.context?.getString(
//                            R.string.edit_terminal_fragment
//                        ),
//                        editFragment.historySwitch,
//                        urlHistoryButtonEvent,
//                        CLICLTYPE.SHORT
//                    )
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
        editFragment.binding.editToolbarLinearLayout.addView(
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
//        val buttonLayout = FrameLayout(context as Context)
        buttonLayout.tag = toolbarButtonBariantForEdit.str
//        val outValue = TypedValue()
//        context.theme
//            .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
//        buttonLayout.setBackgroundResource(outValue.resourceId)
//        buttonLayout.setBackgroundResource(android.R.drawable.list_selector_background)
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
//        buttonLayout.orientation = LinearLayoutCompat.VERTICAL
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
        captionTextView.setFillColor(R.color.web_icon_color)
    }

//    private fun makeTextView(
//        context: Context,
//        iconIdToCaption: Pair<Int, String>
//    ): AppCompatTextView {
//        val textView = AppCompatTextView(context)
//        val paramForTextView = LinearLayoutCompat.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT,
//        )
//        val horizonMargin = ScreenSizeCalculator.toDp(context, 4)
//        paramForTextView.marginStart = horizonMargin
//        paramForTextView.marginEnd = horizonMargin
//        paramForTextView.gravity = Gravity.CENTER
//        textView.layoutParams = paramForTextView
//        textView.gravity = Gravity.CENTER
//        textView.text = iconIdToCaption.second.ifEmpty {
//            val iconId = iconIdToCaption.first
//            CmdClickIcons.values().firstOrNull {
//                it.id == iconId
//            }?.str ?: String()
//        }
//        textView.textSize = 12f
//        textView.inputType = InputType.TYPE_CLASS_TEXT
//        textView.maxLines = 1
//        textView.setTypeface(null, Typeface.BOLD)
//        textView.ellipsize = TextUtils.TruncateAt.END
//        return textView
//    }

    private fun setImageView(
        editFragment: EditFragment,
        imageView: AppCompatImageView,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        iconIdToCaption: Pair<Int, String>
    ): AppCompatImageView{
//        val dpSize = ScreenSizeCalculator.toDp(context, 40)
        val imageViewContext = imageView.context
        val iconId = iconIdToCaption.first
        imageView.tag = CmdClickIcons.values().firstOrNull {
            it.id == iconId
        }?.str ?: toolbarButtonBariantForEdit.str
//        imageView.imageTintList = null
//        imageView.backgroundTintList = imageViewContext.getColorStateList(R.color.white)
//        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
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