package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.view.View
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.*
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.setting_button.SettingButtonHandler
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    currentScriptContentsList: List<String>,
    readSharePreffernceMap: Map<String, String>,
    private val enableCmdEdit: Boolean,
) {

    private val context = editFragment.context
    private val insertImageButtonParam = LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )
    private val settingButtonHandler = SettingButtonHandler(
        editFragment,
        readSharePreffernceMap
    )
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
    private val sharedPref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        editFragment,
        readSharePreffernceMap,
    )

    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    private val currentScriptFileName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_fannel_name
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

    private val recordNumToMapNameValueInSettingHolder =
        RecordNumToMapNameValueInHolder.parse(
            currentScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
            true,
            currentScriptFileName
        )
    private val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder,
        currentAppDirPath,
        currentScriptFileName,
    )

    private val scriptFileSaver = ScriptFileSaver(
        binding,
        editFragment,
        readSharePreffernceMap,
    )

    private val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.on_shortcut
    ) == FragmentTagManager.OnShortcutSuffix.ON.name

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentScriptContentsList,
        editFragment.languageType
    )
    private val enableEditExecute =
        (
                editExecuteValue ==
                        SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                        && onShortcut
                )

    fun make(
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        buttonWeight: Float,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        editExecuteValue :String = SettingVariableSelects.EditExecuteSelects.NO.name,
        setDrawble: Int? = null
    ) {
        if(
            !howSetButton(toolbarButtonBariantForEdit)
        ) return
        insertImageButtonParam.weight = buttonWeight
//              1F
        val makeButtonView = ImageButton(context)
        makeButtonView.imageTintList =
            context?.getColorStateList(R.color.terminal_color)
        when (setDrawble == null) {
            true->
                makeButtonView.setImageResource(
                    toolbarButtonBariantForEdit.drawbleIconInt
                )

            else ->
                makeButtonView.setImageResource(
                    setDrawble
                )
        }
        makeButtonView.backgroundTintList =
            context?.getColorStateList(R.color.white)
        makeButtonView.layoutParams = insertImageButtonParam
        makeButtonView.tag = toolbarButtonBariantForEdit.str
        if(
            makeButtonView.tag ==
            ToolbarButtonBariantForEdit.SETTING.str
        ) {
            val isCmdEditInEditExecute = enableCmdEdit && enableEditExecute
            SettingButtonHandler.setIcon(
                editFragment,
                readSharePreffernceMap,
                makeButtonView,
                isCmdEditInEditExecute
            )
        }

        makeButtonView.setOnLongClickListener {
                buttonInnerView ->
            onLongClickHandler(
                buttonInnerView,
                makeButtonView,
                toolbarButtonBariantForEdit,
                shellContentsList,
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
                        readSharePreffernceMap,
                        editFragment.historySwitch,
                        urlHistoryButtonEvent,
                        sharedPref,
                        CLICLTYPE.SHORT
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.OK -> {
                    execForOk(
                        shellContentsList,
                        recordNumToMapNameValueInCommandHolder,
                        recordNumToMapNameValueInSettingHolder,
                        toolbarButtonBariantForEdit,
                        editExecuteValue,
                        enableCmdEdit
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.SETTING -> {
                    settingButtonHandler.handle(
                        false,
                        makeButtonView,
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.EDIT -> {}
                else -> {
                    println("pass")
                }
            }
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                toolbarButtonBariantForEdit,
                readSharePreffernceMap,
                enableCmdEdit
            )
        }

        binding.editToolbarLinearLayout.addView(makeButtonView)
    }

    private fun howSetButton(
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit
    ): Boolean {
        if(!enableCmdEdit || !enableEditExecute){
            return true
        }
        val onMark = SettingVariableSelects.disableEditButtonSelects.ON.name
        return when(toolbarButtonBariantForEdit){
            ToolbarButtonBariantForEdit.CANCEL,
            ToolbarButtonBariantForEdit.HISTORY -> {
                true
            }
            ToolbarButtonBariantForEdit.OK -> {
                editFragment.disablePlayButton != onMark
            }
            ToolbarButtonBariantForEdit.EDIT -> {
                editFragment.disableEditButton != onMark
            }
            ToolbarButtonBariantForEdit.SETTING -> {
                editFragment.disableSettingButton != onMark
            }
        }
    }

    private fun onLongClickHandler(
        buttonInnerView: View,
        settingButtonView: ImageButton,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        shellContentsList: List<String>,
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
                    readSharePreffernceMap,
                    editFragment.historySwitch,
                    urlHistoryButtonEvent,
                    sharedPref,
                    CLICLTYPE.LONG
                )
            }
            ToolbarButtonBariantForEdit.SETTING -> {
                settingButtonHandler.handle(
                    true,
                    settingButtonView,
                )
            }
            ToolbarButtonBariantForEdit.OK -> {
                try {
                    execForOkLongClick(
                        shellContentsList,
                        recordNumToMapNameValueInCommandHolder,
                        recordNumToMapNameValueInSettingHolder,
                        editFragment.existIndexList
                    )
                } catch(e: Exception){
                    Toast.makeText(
                        context,
                        e.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            ToolbarButtonBariantForEdit.EDIT -> {
                execForEditLongClick(
                    shellContentsList,
                    recordNumToMapNameValueInCommandHolder,
                    recordNumToMapNameValueInSettingHolder,
                    editFragment.existIndexList
                )
            }
            else -> {}
        }
    }

    private fun execForOkLongClick(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        existIndexList: Boolean
    ) {
        if(!existIndexList) {
            execScriptSave(
                shellContentsList,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
            )
        }

        editFragment.execPlayBtnLongPress = replaceLongPressMacroInToolBar(
            editFragment.execPlayBtnLongPress,
        )

        EditToolbarSwitcher.switch(
            editFragment,
            editFragment.execPlayBtnLongPress
        )
    }

    private fun execForEditLongClick(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
        existIndexList: Boolean
    ) {
        if(!existIndexList) {
            execScriptSave(
                shellContentsList,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
            )
        }
        editFragment.execEditBtnLongPress = replaceLongPressMacroInToolBar(
            editFragment.execEditBtnLongPress,
        )
        EditToolbarSwitcher.switch(
            editFragment,
            editFragment.execEditBtnLongPress
        )
    }

    private fun replaceLongPressMacroInToolBar(
        execLongPressMacro: String,
    ): String {
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            execLongPressMacro,
            setReplaceVariableMap,
            currentAppDirPath,
            currentScriptFileName
        )
    }
    private fun execScriptSave(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        if(
            editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.cmdEditPrefix.str
            ) != true
            || editFragment.tag?.endsWith(
                FragmentTagManager.OnShortcutSuffix.ON.name
            ) != true
        ) return
        scriptFileSaver.save(
            shellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun execForOk(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        editExecuteValue: String,
        enableCmdEdit: Boolean
    ){
        val onPassCmdVariableEdit =
            editFragment.passCmdVariableEdit ==
                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE

        scriptFileSaver.save(
            shellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )


        val EditExecuteAlways =
            SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        val EditExecuteOnce =
            SettingVariableSelects.EditExecuteSelects.ONCE.name
        val shortcutValue = SharePreferenceMethod.getReadSharePreffernceMap(
            editFragment.readSharePreffernceMap,
            SharePrefferenceSetting.on_shortcut
        )
        val onShortcut =
            shortcutValue != SharePrefferenceSetting.on_shortcut.defalutStr
                && shortcutValue.isNotEmpty()
        if(
            editExecuteValue == EditExecuteAlways
            && enableCmdEdit
            && onShortcut
            && !onPassCmdVariableEdit
        ) {
            Keyboard.hiddenKeyboardForFragment(
                editFragment
            )
            TerminalShowByTerminalDo.show(
                editFragment,
                shellContentsList
            )
            ExecJsOrSellHandler.handle(
                editFragment,
                currentAppDirPath,
                currentScriptFileName,
            )
            return
        }
        if(
            (editExecuteValue == EditExecuteAlways)
            == !enableCmdEdit
            && onShortcut
        ){
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.HISTORY,
                readSharePreffernceMap,
                enableCmdEdit
            )
            return
        }
        if(
            editExecuteValue == EditExecuteOnce
        ) {
            terminalViewModel.editExecuteOnceCurrentShellFileName = currentScriptFileName
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                toolbarButtonBariantForEdit,
                readSharePreffernceMap,
                enableCmdEdit,
            )
            return
        }
        if(onPassCmdVariableEdit){
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.CANCEL,
                readSharePreffernceMap,
                enableCmdEdit
            )
            return
        }
        val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
        listener?.onToolBarButtonClickForEditFragment(
            editFragment.tag,
            toolbarButtonBariantForEdit,
            readSharePreffernceMap,
            enableCmdEdit
        )
    }
}