package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.view.View
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.checkAllMatched
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.KillConfirmDialogForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.*
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.*
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
        editExecuteValue :String = SettingVariableSelects.Companion.EditExecuteSelects.NO.name,
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
                    val existEditExecuteTerminalFragment = ExistTerminalFragment
                        .how(
                            editFragment,
                            editFragment.context?.getString(
                                R.string.edit_execute_terminal_fragment
                            )
                        )
                    if(
                        editFragment.editTerminalInitType
                        == EditInitType.TERMINAL_SHRINK
                        || existEditExecuteTerminalFragment == null
                    ) {
                        Toast.makeText(
                            view.context,
                            "no terminal",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
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
                if(existEditExecuteTerminalFragment == null){
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
                execForOkLongClick(
                    shellContentsList,
                    toolbarButtonBariantForEdit,
                    recordNumToMapNameValueInCommandHolder,
                    recordNumToMapNameValueInSettingHolder,
                )
            }
            else -> {}
        }
    }

    private fun execForOkLongClick(
        shellContentsList: List<String>,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ) {
        val onShortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.on_shortcut
        )
        if(
            editFragment.tag !=
            context?.getString(R.string.cmd_variable_edit_fragment)
            || onShortcut != ShortcutOnValueStr.ON.name
        ) return
        scriptFileSaver.save(
            shellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )

        val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
        listener?.onToolBarButtonClickForEditFragment(
            editFragment.tag,
            toolbarButtonBariantForEdit,
            readSharePreffernceMap,
            enableCmdEdit,
            isLongPress = true
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
        val startUpPref =  editFragment.activity?.getPreferences(
            Context.MODE_PRIVATE
        )
        val beforeUpdateShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        scriptFileSaver.save(
            shellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        val afterUpdateShellFileName = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_script_file_name
        )
        val curentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )

        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
        val onAppDirAdminMode = curentAppDirPath == cmdclickAppDirAdminPath
        execMoveDirWhenOk(
            onAppDirAdminMode,
            beforeUpdateShellFileName,
            afterUpdateShellFileName
        )
        if(onAppDirAdminMode){
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                toolbarButtonBariantForEdit,
                readSharePreffernceMap,
                enableCmdEdit
            )
            return
        }
        val EditExecuteAlways =
            SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
        val EditExecuteOnce =
            SettingVariableSelects.Companion.EditExecuteSelects.ONCE.name
        val on_shortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.on_shortcut
        ) != SharePrefferenceSetting.on_shortcut.defalutStr
        if(
            editExecuteValue == EditExecuteAlways
            && enableCmdEdit
            && on_shortcut
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
                curentAppDirPath,
                currentShellFileName,
            )
            return
        }
        if(
            (editExecuteValue == EditExecuteAlways)
            == !enableCmdEdit
            && on_shortcut
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

        val howConfigEdit = editFragment.tag == context?.getString(
            R.string.cmd_config_variable_edit_fragment
        )
        val howEditApi = editFragment.tag == context?.getString(
            R.string.api_cmd_variable_edit_api_fragment
        )
        if( howConfigEdit || howEditApi){
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.CANCEL,
                readSharePreffernceMap,
                enableCmdEdit
            )
            if(howEditApi) editFragment.activity?.finish()
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


    private fun execMoveDirWhenOk(
        onAppDirAdminMode: Boolean,
        beforeUpdateShellFileName: String,
        afterUpdateShellFileName: String
    ){
        if(
            onAppDirAdminMode
            && beforeUpdateShellFileName != afterUpdateShellFileName
        ){
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            val beforeMoveDirPath = cmdclickAppDirPath + '/' +
                    beforeUpdateShellFileName.removeSuffix(
                        CommandClickScriptVariable.JS_FILE_SUFFIX
                    )
            val afterMoveDirPath = cmdclickAppDirPath + '/' +
                    afterUpdateShellFileName.removeSuffix(
                        CommandClickScriptVariable.JS_FILE_SUFFIX
                    )
            FileSystems.moveDirectory(
                beforeMoveDirPath,
                afterMoveDirPath,
            )
        }
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
            popup.menu.add(
                MenuEnumsForEdit.TERM_REFRESH.groupId,
                MenuEnumsForEdit.TERM_REFRESH.itemId,
                MenuEnumsForEdit.TERM_REFRESH.order,
                MenuEnumsForEdit.TERM_REFRESH.itemName
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
            val sub = popup.menu.addSubMenu(
                MenuEnumsForEdit.SELECTTERM.groupId,
                MenuEnumsForEdit.SELECTTERM.itemId,
                MenuEnumsForEdit.SELECTTERM.order,
                MenuEnumsForEdit.SELECTTERM.itemName
            )
            val currentMonitorFileName = terminalViewModel.currentMonitorFileName
            (MenuEnumsForEdit.values()).forEach {
                val groupId = it.groupId
                if (groupId != submenuTermSlectGroupId) return@forEach
                val itemId = it.itemId
                val checked = if (it.itemName == currentMonitorFileName) {
                    true
                } else {
                    false
                }
                sub.add(
                    groupId,
                    itemId,
                    it.order,
                    it.itemName
                ).setCheckable(true).setChecked(checked);
            }
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
            MenuEnumsForEdit.CONFIG.itemId -> {
                val configDirPath = UsePath.cmdclickConfigDirPath
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
                    SharePreffrenceMethod.getReadSharePreffernceMap(
                        readSharePreffernceMap,
                        SharePrefferenceSetting.current_app_dir
                    ),
                    SharePreffrenceMethod.getReadSharePreffernceMap(
                        readSharePreffernceMap,
                        SharePrefferenceSetting.current_script_file_name
                    ),
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
    CONFIG(submenuSettingGroupId, 70402, 2, "config"),
    SELECTTERM(mainMenuGroupId, 70700, 7, "select_term"),
    TERM1(submenuTermSlectGroupId, 70701, 1, "term_1"),
    TERM2(submenuTermSlectGroupId, 70702, 2, "term_2"),
    TERM3(submenuTermSlectGroupId, 70703, 3, "term_3"),
    TERM4(submenuTermSlectGroupId, 70704, 4, "term_4"),
    TERM_REFRESH(mainMenuGroupId, 70800, 8, "term_refresh"),
    FORWARD(mainMenuGroupId, 70900, 9, "forward"),
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