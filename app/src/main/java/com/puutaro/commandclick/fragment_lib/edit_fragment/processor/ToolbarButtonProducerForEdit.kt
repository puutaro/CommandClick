package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.history.CLICLTYPE
import com.puutaro.commandclick.proccess.history.HistoryButtonSwitcher
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import com.puutaro.commandclick.proccess.tool_bar_button.ToolbarButtonHandler
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
) {
    private val fannelInfoMap = editFragment.fannelInfoMap
    private val context = editFragment.context
    private val toolbarButtonHandler = ToolbarButtonHandler(
        editFragment,
    )
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        editFragment,
        fannelInfoMap,
    )


    var languageType = LanguageTypeSelects.JAVA_SCRIPT
    var languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    var settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    fun make(
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ) {
        if(
            !howSetButton(toolbarButtonBariantForEdit)
            || context == null
        ) return


        val buttonLayout = makeButtonLayout(
            context,
            toolbarButtonBariantForEdit,
        )
        val iconIdToCaption = editFragment.toolBarButtonIconMap[toolbarButtonBariantForEdit]
            ?: Pair(R.drawable.icons8_ok, String())

        val imageView = makeImageView(
            context,
            toolbarButtonBariantForEdit,
            iconIdToCaption
        )

        val textView = makeTextView(
            context,
            iconIdToCaption
        )
        buttonLayout.addView(imageView)
        buttonLayout.addView(textView)




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
                    val editExecuteTerminalFragment = TargetFragmentInstance()
                        .getFromFragment<TerminalFragment>(
                            editFragment.activity,
                            context.getString(R.string.edit_terminal_fragment)
                        )
                    if(
                        editExecuteTerminalFragment != null
                    ){
                        val listener = context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
                        listener?.onKeyBoardVisibleChangeForEditFragment(
                            false,
                            true
                        )
                    }
                    HistoryButtonSwitcher.switch(
                        editFragment,
                        editFragment.context?.getString(
                            R.string.edit_terminal_fragment
                        ),
                        editFragment.historySwitch,
                        urlHistoryButtonEvent,
                        CLICLTYPE.SHORT
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.SETTING,
                ToolbarButtonBariantForEdit.OK,
                ToolbarButtonBariantForEdit.EDIT,
                ToolbarButtonBariantForEdit.EXTRA,
                -> {
                    toolbarButtonHandler.handle(
                        false,
                        toolbarButtonBariantForEdit,
                        buttonLayout
//                        makeButtonView,
                    )
                }
                ToolbarButtonBariantForEdit.CANCEL -> {}
            }
        }
        binding.editToolbarLinearLayout.addView(
            buttonLayout
//            makeButtonView
        )
    }

    private fun makeButtonLayout(
        context: Context,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ): LinearLayoutCompat {
        val buttonLayout = LinearLayoutCompat(context)
        buttonLayout.tag = toolbarButtonBariantForEdit.str
        val outValue = TypedValue()
        context.theme
            .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        buttonLayout.setBackgroundResource(outValue.resourceId)
//        buttonLayout.setBackgroundResource(android.R.drawable.list_selector_background)
        val param = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        )
        param.gravity = Gravity.CENTER
        param.weight = editFragment.buttonWeight
        buttonLayout.layoutParams = param
        buttonLayout.orientation = LinearLayoutCompat.VERTICAL
        return buttonLayout
    }

    private fun makeTextView(
        context: Context,
        iconIdToCaption: Pair<Int, String>
    ): AppCompatTextView {
        val textView = AppCompatTextView(context)
        val paramForTextView = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        val horizonMargin = ScreenSizeCalculator.toDp(context, 4)
        paramForTextView.marginStart = horizonMargin
        paramForTextView.marginEnd = horizonMargin
        paramForTextView.gravity = Gravity.CENTER
        textView.layoutParams = paramForTextView
        textView.gravity = Gravity.CENTER
        textView.text = iconIdToCaption.second.ifEmpty {
            val iconId = iconIdToCaption.first
            CmdClickIcons.values().firstOrNull {
                it.id == iconId
            }?.str ?: String()
        }
        textView.textSize = 12f
        textView.inputType = InputType.TYPE_CLASS_TEXT
        textView.maxLines = 1
        textView.ellipsize = TextUtils.TruncateAt.END
        return textView
    }

    private fun makeImageView(
        context: Context,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        iconIdToCaption: Pair<Int, String>
    ): AppCompatImageView{
        val imageView = AppCompatImageView(context)
        val dpSize = ScreenSizeCalculator.toDp(context, 40)
        val paramForImageView = LinearLayoutCompat.LayoutParams(
            dpSize,
            dpSize,
        )
        paramForImageView.gravity = Gravity.CENTER
        imageView.layoutParams = paramForImageView
        val imageViewContext = imageView.context
        val iconId = iconIdToCaption.first
        imageView.tag = CmdClickIcons.values().firstOrNull {
            it.id == iconId
        }?.str ?: toolbarButtonBariantForEdit.str
        imageView.imageTintList = null
        imageView.backgroundTintList = imageViewContext.getColorStateList(R.color.white)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
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
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit
    ): Boolean {
        return editFragment.toolBarButtonVisibleMap.filter {
            it.key == toolbarButtonBariantForEdit
        }.values.firstOrNull() ?: true
    }

    private fun onLongClickHandler(
        editFragment: EditFragment,
        buttonLayout: LinearLayoutCompat,
//        settingButtonView: ImageButton,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    ){
        when (toolbarButtonBariantForEdit) {
            ToolbarButtonBariantForEdit.HISTORY -> {
                HistoryButtonSwitcher.switch(
                    editFragment,
                    editFragment.context?.getString(
                        R.string.edit_terminal_fragment
                    ),
                    editFragment.historySwitch,
                    urlHistoryButtonEvent,
                    CLICLTYPE.LONG
                )
            }
            ToolbarButtonBariantForEdit.SETTING,
            ToolbarButtonBariantForEdit.OK,
            ToolbarButtonBariantForEdit.EDIT,
            ToolbarButtonBariantForEdit.EXTRA -> {
                toolbarButtonHandler.handle(
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