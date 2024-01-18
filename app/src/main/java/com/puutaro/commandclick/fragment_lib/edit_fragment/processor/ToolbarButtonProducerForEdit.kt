package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.*
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.setting_button.SettingButtonHandler
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file_tool.FDialogTempFile
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>,
) {
    private val context = editFragment.context
    private val currentScriptContentsList = editFragment.currentScriptContentsList
    private val insertImageButtonParam = LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )
    private val settingButtonHandler = SettingButtonHandler(
        editFragment,
        readSharePreffernceMap
    )
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
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
        editFragment,
    )

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
                ToolbarButtonBariantForEdit.OK -> {
                    execForOk(
                        editFragment,
                        recordNumToMapNameValueInCommandHolder,
                        recordNumToMapNameValueInSettingHolder,

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
                ToolbarButtonBariantForEdit.EDIT,
                ToolbarButtonBariantForEdit.CANCEL -> {
                    val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
                    listener?.onToolBarButtonClickForEditFragment(
                        editFragment.tag,
                        toolbarButtonBariantForEdit,
                        readSharePreffernceMap,
                        editFragment.enableCmdEdit
                    )
                }
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
            ToolbarButtonBariantForEdit.SETTING -> {
                settingButtonHandler.handle(
                    true,
                    settingButtonView,
                )
            }
            ToolbarButtonBariantForEdit.OK -> {
                try {
                    execForOkLongClick(
                        editFragment,
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
                    editFragment,
                    recordNumToMapNameValueInCommandHolder,
                    recordNumToMapNameValueInSettingHolder,
                    editFragment.existIndexList
                )
            }
            else -> {}
        }
    }

    private fun execForOkLongClick(
        editFragment: EditFragment,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        existIndexList: Boolean
    ) {
        if(!existIndexList) {
            execScriptSave(
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
        editFragment: EditFragment,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
        existIndexList: Boolean
    ) {
        if(!existIndexList) {
            execScriptSave(
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
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        if(
            editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.CMD_EDIT_PREFIX.str
            ) != true
            || editFragment.tag?.endsWith(
                EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
            ) != true
        ) return
        scriptFileSaver.save(
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun execForOk(
        editFragment: EditFragment,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        val enableCmdEdit = editFragment.enableCmdEdit
        val onPassCmdVariableEdit =
            editFragment.passCmdVariableEdit ==
                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE

        scriptFileSaver.save(
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        val isCmdEditExecute = enableCmdEdit
                && editFragment.enableEditExecute
                && !onPassCmdVariableEdit
        val isSettingEditByPass = enableCmdEdit
                && editFragment.enableEditExecute
                && onPassCmdVariableEdit
        val isSettingEdit = !enableCmdEdit

        val isFdialogFannel = FDialogTempFile.howFDialogFile(currentScriptFileName)
        val isOnlyCmdEditNoFdialog = enableCmdEdit
                && !editFragment.enableEditExecute
                && !isFdialogFannel
        val isOnlyCmdEditWithFdialog = enableCmdEdit
                && !editFragment.enableEditExecute
                && isFdialogFannel
        when(true) {
            isCmdEditExecute -> {
                Keyboard.hiddenKeyboardForFragment(
                    editFragment
                )
                TerminalShowByTerminalDo.show(
                    editFragment,
                )
                ExecJsOrSellHandler.handle(
                    editFragment,
                    currentAppDirPath,
                    currentScriptFileName,
                )
            }
            isOnlyCmdEditWithFdialog ->
                fDialalogOkButtonProcess()
            isSettingEditByPass,
            isOnlyCmdEditNoFdialog,
            isSettingEdit -> {
                val listener =
                    this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
                listener?.onToolBarButtonClickForEditFragment(
                    String(),
                    ToolbarButtonBariantForEdit.CANCEL,
                    mapOf(),
                    false
                )
            }
            else -> {}
        }
    }

    private fun fDialalogOkButtonProcess(
    ){
        val srcReadSharePreffernceMap = editFragment.srcReadSharePreffernceMap
            ?: return
        val srcAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            srcReadSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val srcFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            srcReadSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val srcFannelCon = ReadText(
            srcAppDirPath,
            srcFannelName
        ).readText()
        val fDialogConList = ReadText(
            currentAppDirPath,
            currentScriptFileName
        ).textToList()
        val fDialogCommandValCon = CommandClickVariables.substituteVariableListFromHolder(
            fDialogConList,
            editFragment.commandSectionStart,
            editFragment.commandSectionEnd
        )?.filter {
            !it.startsWith(FDialogTempFile.jsDescPrefix)
                    && it.isNotEmpty()
        }?.joinToString("\t") ?: String()
        val replaceSrcFanneCon = CommandClickVariables.replaceVariableInHolder(
            srcFannelCon,
            fDialogCommandValCon,
            editFragment.commandSectionStart,
            editFragment.commandSectionEnd,
        )
        if(
            replaceSrcFanneCon != srcFannelCon
        ){
            FileSystems.writeFile(
                srcAppDirPath,
                srcFannelName,
                replaceSrcFanneCon
            )
        }
        val currentFannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)
        val srcFannelDirName = CcPathTool.makeFannelDirName(srcFannelName)
        FileSystems.copyDirectory(
            File(currentAppDirPath, currentFannelDirName).absolutePath,
            File(srcAppDirPath, srcFannelDirName).absolutePath
        )
        val listener =
            this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
        listener?.onToolBarButtonClickForEditFragment(
            String(),
            ToolbarButtonBariantForEdit.CANCEL,
            mapOf(),
            false
        )
    }
}