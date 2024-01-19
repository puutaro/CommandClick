package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.*
import com.puutaro.commandclick.proccess.setting_button.SettingButtonHandler
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
) {
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val context = editFragment.context
    private val insertImageButtonParam = LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )
    private val settingButtonHandler = SettingButtonHandler(
        editFragment,
    )
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        editFragment,
        readSharePreffernceMap,
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
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
    ) {
        if(
            !howSetButton(toolbarButtonBariantForEdit)
        ) return
        insertImageButtonParam.weight = editFragment.buttonWeight
        val makeButtonView = ImageButton(context)
        makeButtonView.imageTintList =
            context?.getColorStateList(R.color.terminal_color)
        makeButtonView.setImageResource(
            editFragment.toolBarButtonIconMap[toolbarButtonBariantForEdit] ?: R.drawable.icons8_ok
        )
        makeButtonView.backgroundTintList =
            context?.getColorStateList(R.color.white)
        makeButtonView.layoutParams = insertImageButtonParam
        makeButtonView.tag = toolbarButtonBariantForEdit.str
        makeButtonView.setOnLongClickListener {
                buttonInnerView ->
            onLongClickHandler(
                editFragment,
                buttonInnerView,
                makeButtonView,
                toolbarButtonBariantForEdit,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
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
                    HistoryBottunSwitcher.switch(
                        editFragment,
                        view,
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
                ToolbarButtonBariantForEdit.EDIT -> {
                    settingButtonHandler.handle(
                        false,
                        toolbarButtonBariantForEdit,
                        makeButtonView,
                        recordNumToMapNameValueInCommandHolder,
                        recordNumToMapNameValueInSettingHolder,
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
        return editFragment.toolBarButtonDisableMap.filter {
            it.key == toolbarButtonBariantForEdit
        }.values.firstOrNull()?.let { !it } ?: true
    }

    private fun onLongClickHandler(
        editFragment: EditFragment,
        buttonInnerView: View,
        settingButtonView: ImageButton,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        when (toolbarButtonBariantForEdit) {
            ToolbarButtonBariantForEdit.HISTORY -> {
                HistoryBottunSwitcher.switch(
                    editFragment,
                    buttonInnerView,
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
            ToolbarButtonBariantForEdit.EDIT -> {
                settingButtonHandler.handle(
                    true,
                    toolbarButtonBariantForEdit,
                    settingButtonView,
                    recordNumToMapNameValueInCommandHolder,
                    recordNumToMapNameValueInSettingHolder,
                )
            }
            ToolbarButtonBariantForEdit.CANCEL -> {}
        }
    }
}