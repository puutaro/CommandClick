package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.view.View
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.checkAllMatched
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.KillConfirmDialogForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.*
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    private val enableCmdEdit: Boolean,
) {

    private val context = editFragment.context
    private val insertImageButtonParam = LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
    private val sharedPref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        editFragment,
        readSharePreffernceMap,
    )

    private val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_script_file_name
    )

    private val scriptFileSaver = ScriptFileSaver(
        binding,
        editFragment,
        readSharePreffernceMap,
    )

    fun make(
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        editExecuteValue :String = SettingVariableSelects.EditExecuteSelects.NO.name,
        setDrawble: Int? = null
    ) {
        insertImageButtonParam.weight = 1F
        val makeButtonView = ImageButton(context)

        if (setDrawble == null) {
            makeButtonView.setImageResource(
                toolbarButtonBariantForEdit.drawbleIconInt
            )
        } else {
            makeButtonView.setImageResource(
                setDrawble
            )
        }
        makeButtonView.setBackgroundTintList(context?.getColorStateList(R.color.white))
        makeButtonView.layoutParams = insertImageButtonParam
        makeButtonView.tag = toolbarButtonBariantForEdit.str

        makeButtonView.setOnLongClickListener {
                view ->
            onLongClickHandler(
                view,
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
                            context?.getString(R.string.edit_execute_terminal_fragment)
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
                            R.string.edit_execute_terminal_fragment
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
                    if(
                        editFragment.terminalOn
                        == SettingVariableSelects.TerminalDoSelects.OFF.name
                    ) return@setOnClickListener
                    val existEditExecuteTerminalFragment = ExistTerminalFragment
                        .how(
                            editFragment,
                            editFragment.context?.getString(
                                R.string.edit_execute_terminal_fragment
                            )
                        )
                    if(
                        existEditExecuteTerminalFragment?.isVisible != true
                    ) {
                        Toast.makeText(
                            view.context,
                            "no terminal",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    val onShorth = terminalViewModel.readlinesNum == ReadLines.SHORTH
                    EditLayoutViewHideShow.exec(
                        editFragment,
                        !onShorth
                    )
                    val listener =
                        context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX
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

    private fun onLongClickHandler(
        view: View,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        when (toolbarButtonBariantForEdit) {
            ToolbarButtonBariantForEdit.HISTORY -> {
                HistoryBottunSwitcher.switch(
                    editFragment,
                    view,
                    editFragment.context?.getString(
                        R.string.edit_execute_terminal_fragment
                    ),
                    readSharePreffernceMap,
                    editFragment.historySwitch,
                    urlHistoryButtonEvent,
                    sharedPref,
                    CLICLTYPE.LONG
                )
            }
            ToolbarButtonBariantForEdit.SETTING -> {
                val existEditExecuteTerminalFragment = ExistTerminalFragment
                    .how(
                        editFragment,
                        editFragment.context?.getString(
                            R.string.edit_execute_terminal_fragment
                        )
                    )
                if(
                    existEditExecuteTerminalFragment == null
                ){
                    Toast.makeText(
                        view.context,
                        "no working",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                createPopUpForSetting(
                    editFragment,
                    context,
                    view
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
        EditToolbarSwitcher.switch(
            editFragment,
            editFragment.execEditBtnLongPress
        )
    }
    private fun execScriptSave(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        val onShortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.on_shortcut
        )
        if(
            editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.cmdEditPrefix.str
            ) != true
            || editFragment.tag?.endsWith(
                FragmentTagManager.Suffix.ON.name
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

        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )


        val EditExecuteAlways =
            SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        val EditExecuteOnce =
            SettingVariableSelects.EditExecuteSelects.ONCE.name
        val shortcutValue = FragmentTagManager.makeListFromTag(
            editFragment.tag
                ?: String()
        ).getOrNull(FragmentTagManager.modeIndex)
            ?: String()
        val onShortcut = shortcutValue != SharePrefferenceSetting.on_shortcut.defalutStr
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
                currentShellFileName,
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
            terminalViewModel.editExecuteOnceCurrentShellFileName = currentShellFileName
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

    private fun createPopUpForSetting(
        editFragment: EditFragment,
        context: Context?,
        view: View
    ){
        val popup = PopupMenu(context, view)
        val terminalFragment = editFragment.activity?.supportFragmentManager?.findFragmentByTag(
            context?.getString(R.string.edit_execute_terminal_fragment)
        )
        val enableTerminalFragment =
            terminalFragment != null && terminalFragment.isVisible
        if(enableTerminalFragment) {
//            popup.menu.add(
//                MenuEnumsForEdit.TERM_REFRESH.groupId,
//                MenuEnumsForEdit.TERM_REFRESH.itemId,
//                MenuEnumsForEdit.TERM_REFRESH.order,
//                MenuEnumsForEdit.TERM_REFRESH.itemName
//            )
            popup.menu.add(
                MenuEnumsForEdit.NO_SCROLL_SAVE_URL.groupId,
                MenuEnumsForEdit.NO_SCROLL_SAVE_URL.itemId,
                MenuEnumsForEdit.NO_SCROLL_SAVE_URL.order,
                MenuEnumsForEdit.NO_SCROLL_SAVE_URL.itemName
            ).setEnabled(
                editFragment.onNoUrlSaveMenu
            )
            popup.menu.add(
                MenuEnumsForEdit.FORWARD.groupId,
                MenuEnumsForEdit.FORWARD.itemId,
                MenuEnumsForEdit.FORWARD.order,
                MenuEnumsForEdit.FORWARD.itemName
            ).setEnabled(
                EnableGoForwardForWebVeiw.check(editFragment)
            )
            popup.menu.add(
                MenuEnumsForEdit.KILL.groupId,
                MenuEnumsForEdit.KILL.itemId,
                MenuEnumsForEdit.KILL.order,
                MenuEnumsForEdit.KILL.itemName
            );
//            val sub = popup.menu.addSubMenu(
//                MenuEnumsForEdit.SELECTTERM.groupId,
//                MenuEnumsForEdit.SELECTTERM.itemId,
//                MenuEnumsForEdit.SELECTTERM.order,
//                MenuEnumsForEdit.SELECTTERM.itemName
//            )
//            val currentMonitorFileName = terminalViewModel.currentMonitorFileName
//            (MenuEnumsForEdit.values()).forEach {
//                val groupId = it.groupId
//                if (groupId != submenuTermSlectGroupId) return@forEach
//                val itemId = it.itemId
//                val checked = if (it.itemName == currentMonitorFileName) {
//                    true
//                } else {
//                    false
//                }
//                sub.add(
//                    groupId,
//                    itemId,
//                    it.order,
//                    it.itemName
//                ).setCheckable(true).setChecked(checked);
//            }
        }
        execAddSettingSubMenu(
            popup,
            MenuEnumsForEdit.SETTING,
            submenuSettingGroupId
        )
        popup.show()
        popupMenuItemSelectedForEdit(
            popup,
            editFragment,
            readSharePreffernceMap,
            terminalViewModel,
        )
    }
}


internal fun popupMenuItemSelectedForEdit(
    popup: PopupMenu,
    editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>,
    terminalViewModel: TerminalViewModel,
){
    val context = editFragment.context
    val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    val fannelName = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_script_file_name
    )

    popup.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            MenuEnumsForEdit.SELECTTERM.itemId  -> {
                println("pass")
            }
            MenuEnumsForEdit.TERM1.itemId  -> {
                FileSystems.updateLastModified(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1
                )
                terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_1
            }
            MenuEnumsForEdit.TERM2.itemId  -> {
                FileSystems.updateLastModified(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_2
                )
                terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_2
            }
            MenuEnumsForEdit.TERM3.itemId  -> {
                FileSystems.updateLastModified(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_3
                )
                terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_3
            }
            MenuEnumsForEdit.TERM4.itemId  -> {
                FileSystems.updateLastModified(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_4
                )
                terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_4
            }
            MenuEnumsForEdit.SETTING.itemId  -> {
                println("pass")
            }
            MenuEnumsForEdit.SHORTCUT.itemId -> {
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT
                )
            }
            MenuEnumsForEdit.TERMUX_SETUP.itemId -> {
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP
                )
            }
            MenuEnumsForEdit.CONFIG.itemId -> {
                val configDirPath = UsePath.cmdclickSystemAppDirPath
                val configShellName = UsePath.cmdclickConfigFileName
                CommandClickScriptVariable.makeConfigJsFile(
                    configDirPath,
                    configShellName
                )
                val shellContentsList = ReadText(
                    configDirPath,
                    configShellName
                ).textToList()
                val validateErrMessage = ValidateShell.correct(
                    editFragment,
                    shellContentsList,
                    configShellName
                )
                if(validateErrMessage.isNotEmpty()){
                    val shellScriptPath = "${configDirPath}/${configShellName}"
                    VaridateionErrDialog.show(
                        editFragment,
                        shellScriptPath,
                        validateErrMessage
                    )
                } else {
                    val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        ToolbarMenuCategoriesVariantForCmdIndex.CONFIG
                    )
                }
            }
            MenuEnumsForEdit.NO_SCROLL_SAVE_URL.itemId -> {
                NoScrollUrlSaver.save(
                    editFragment,
                    currentAppDirPath,
                    fannelName,
                )
            }
            MenuEnumsForEdit.FORWARD.itemId -> {
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    ToolbarMenuCategoriesVariantForCmdIndex.FORWARD
                )
            }
            MenuEnumsForEdit.TERM_REFRESH.itemId -> {
                TermRefresh.refresh(
                    terminalViewModel.currentMonitorFileName
                )
            }
            MenuEnumsForEdit.KILL.itemId -> {
                KillConfirmDialogForEdit.show(
                    editFragment,
                    currentAppDirPath,
                    fannelName,
                    terminalViewModel.currentMonitorFileName,
                )


            }
            else -> {}
        }.checkAllMatched
        true
    }
}

private val mainMenuGroupId = 100
private val submenuTermSlectGroupId = 200
private val submenuSettingGroupId = 300


internal enum class MenuEnumsForEdit(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    KILL(mainMenuGroupId, 70100, 1, "kill"),
    SETTING(mainMenuGroupId, 70400, 4, "setting"),
    SHORTCUT(submenuSettingGroupId, 70401, 1, "create_short_cut"),
    TERMUX_SETUP(submenuSettingGroupId, 70402, 2, "termux_setup"),
    CONFIG(submenuSettingGroupId, 70403, 3, "config"),
    SELECTTERM(mainMenuGroupId, 70700, 7, "select_term"),
    TERM1(submenuTermSlectGroupId, 70701, 1, "term_1"),
    TERM2(submenuTermSlectGroupId, 70702, 2, "term_2"),
    TERM3(submenuTermSlectGroupId, 70703, 3, "term_3"),
    TERM4(submenuTermSlectGroupId, 70704, 4, "term_4"),
    TERM_REFRESH(mainMenuGroupId, 70800, 8, "term_refresh"),
    NO_SCROLL_SAVE_URL(mainMenuGroupId, 70900, 9, "no_scroll_save_url"),
    FORWARD(mainMenuGroupId, 71000, 10, "forward"),
}


internal fun execAddSettingSubMenu(
    popup: PopupMenu,
    addMenuEnums: MenuEnumsForEdit,
    submenuGroupId: Int
){
    val sub = popup.menu.addSubMenu(
        addMenuEnums.groupId,
        addMenuEnums.itemId,
        addMenuEnums.order,
        addMenuEnums.itemName
    );
    (MenuEnumsForEdit.values()).forEach{
        val groupId = it.groupId
        if( groupId != submenuGroupId) return@forEach
        sub.add(
            groupId,
            it.itemId,
            it.order,
            it.itemName
        )
    }
}