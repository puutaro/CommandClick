package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.R
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
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonColorSettingForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonStatusSettingsForToolbarButton
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
) {
    private val fannelInfoMap = editFragment.fannelInfoMap
    private val context = editFragment.context
    private val insertImageButtonParam = LinearLayoutCompat.LayoutParams(
        0,
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
    )
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
        ) return
        insertImageButtonParam.weight = editFragment.buttonWeight
        val makeButtonView = ImageButton(context)
        ButtonColorSettingForToolbarButton.set(
            editFragment,
            toolbarButtonBariantForEdit,
            makeButtonView
        )
//        makeButtonView.imageTintList =
//            context?.getColorStateList(R.color.terminal_color)
        makeButtonView.setImageResource(
            editFragment.toolBarButtonIconMap[toolbarButtonBariantForEdit]
                ?: R.drawable.icons8_ok
        )
        makeButtonView.backgroundTintList =
            context?.getColorStateList(R.color.white)
//        it.getColorStateList(R.color.terminal_color)

        ButtonStatusSettingsForToolbarButton.set(
            editFragment,
            toolbarButtonBariantForEdit,
            makeButtonView
        )
        makeButtonView.layoutParams = insertImageButtonParam
        makeButtonView.tag = toolbarButtonBariantForEdit.str
        makeButtonView.setOnLongClickListener {
                buttonInnerView ->
            onLongClickHandler(
                editFragment,
                makeButtonView,
                toolbarButtonBariantForEdit,
//                recordNumToMapNameValueInCommandHolder,
//                recordNumToMapNameValueInSettingHolder,
            )
            true
        }

        makeButtonView.setOnClickListener { view ->
            when (toolbarButtonBariantForEdit) {
                ToolbarButtonBariantForEdit.HISTORY -> {
                    val editExecuteTerminalFragment = TargetFragmentInstance()
                        .getFromFragment<TerminalFragment>(
                            editFragment.activity,
                            context?.getString(R.string.edit_terminal_fragment)
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
                ToolbarButtonBariantForEdit.EDIT ,
                ToolbarButtonBariantForEdit.EXTRA, -> {
                    toolbarButtonHandler.handle(
                        false,
                        toolbarButtonBariantForEdit,
                        makeButtonView,
                    )
                }
                ToolbarButtonBariantForEdit.CANCEL -> {}
            }
        }
        binding.editToolbarLinearLayout.addView(makeButtonView)
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
        settingButtonView: ImageButton,
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
                    settingButtonView,
                )
            }
            ToolbarButtonBariantForEdit.CANCEL -> {}
        }
    }
}