package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import android.content.SharedPreferences
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.SystemFannelLauncher
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ScriptFileEdit
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.AddScriptHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFannelHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFromFannelRepo
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.EnableGoForwardForWebVeiw
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

private val mainMenuGroupId = 1
private val submenuTermSlectGroupId = 2
private val submenuSettingGroupId = 3

class ToolBarSettingButtonControl(
    binding: CommandIndexFragmentBinding,
    private val cmdIndexFragment: CommandIndexFragment,
    private val cmdListAdapter: ArrayAdapter<String>,
    private val sharedPref: SharedPreferences?,
    readSharePreffernceMap: Map<String, String>,
){
    private val context = cmdIndexFragment.context
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

    private val settingButtonView = binding.cmdindexSettingButton
    private val popup = PopupMenu(context, settingButtonView)
    private val installFromFannelRepo = InstallFromFannelRepo(
        cmdIndexFragment,
        currentAppDirPath,
        cmdListAdapter,
    )


    fun inflate(){
        popup.menuInflater.inflate(
            R.menu.cmd_index_menu,
            popup.menu
        )
    }

    fun toolbarSettingButtonOnLongClick() {
        settingButtonView.setOnClickListener {
            ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                cmdIndexFragment,
            )
        }
    }

    fun toolbarSettingButtonOnClick(){
        settingButtonView.setOnLongClickListener {
            _ ->
            popup.menu.clear()
            popup.menu.add(
                MenuEnums.INSTALL_FANNEL.groupId,
                MenuEnums.INSTALL_FANNEL.itemId,
                MenuEnums.INSTALL_FANNEL.order,
                MenuEnums.INSTALL_FANNEL.itemName
            )
            popup.menu.add(
                MenuEnums.NO_SCROLL_SAVE_URL.groupId,
                MenuEnums.NO_SCROLL_SAVE_URL.itemId,
                MenuEnums.NO_SCROLL_SAVE_URL.order,
                MenuEnums.NO_SCROLL_SAVE_URL.itemName
            )
            popup.menu.add(
                MenuEnums.EDIT_STARTUP.groupId,
                MenuEnums.EDIT_STARTUP.itemId,
                MenuEnums.EDIT_STARTUP.order,
                MenuEnums.EDIT_STARTUP.itemName
            )
            popup.menu.add(
                MenuEnums.FORWARD.groupId,
                MenuEnums.FORWARD.itemId,
                MenuEnums.FORWARD.order,
                MenuEnums.FORWARD.itemName
            ).setEnabled(
                EnableGoForwardForWebVeiw.check(cmdIndexFragment)
            )
//            val sub = popup.menu.addSubMenu(
//                MenuEnums.SELECTTERM.groupId,
//                MenuEnums.SELECTTERM.itemId,
//                MenuEnums.SELECTTERM.order,
//                MenuEnums.SELECTTERM.itemName
//            )
//            val currentMonitorFileName = terminalViewModel.currentMonitorFileName
//            (MenuEnums.values()).forEach{
//                val groupId = it.groupId
//                if( groupId != submenuTermSlectGroupId) return@forEach
//                val itemId = it.itemId
//                val checked = it.itemName == currentMonitorFileName
//                sub.add(
//                    groupId,
//                    itemId,
//                    it.order,
//                    it.itemName
//                ).setCheckable(true).setChecked(checked)
//            }
            popup.menu.add(
                MenuEnums.ADD.groupId,
                MenuEnums.ADD.itemId,
                MenuEnums.ADD.order,
                MenuEnums.ADD.itemName
            )
            execAddSettingSubMenu(
                popup,
                MenuEnums.SETTING,
                submenuSettingGroupId
            )
            popup.show()
            true
        }
    }


    fun popupMenuItemSelected(
        cmdIndexFragment: CommandIndexFragment
    ){
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                MenuEnums.ADD.itemId -> {
                    AddScriptHandler(
                        cmdIndexFragment,
                        sharedPref,
                        currentAppDirPath,
                        cmdListAdapter,
                    ).handle()
                }
                MenuEnums.SETTING.itemId -> {
                    print("pass")
                }
                MenuEnums.CHDIR.itemId -> {
                    SystemFannelLauncher.launch(
                        cmdIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        UsePath.appDirManagerFannelName
                    )
                }
                MenuEnums.CONFIG.itemId -> {
                    configEdit()
                }
                MenuEnums.CC_IMPORT.itemId -> {
                    SystemFannelLauncher.launch(
                        cmdIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        UsePath.ccImportManagerFannelName
                    )
                }
                MenuEnums.TERMUX_SETUP.itemId -> {
                    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP
                    )
                }
                MenuEnums.SHORTCUT.itemId -> {
                    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT
                    )
                }
                MenuEnums.INSTALL_FANNEL.itemId -> {
                    InstallFannelHandler.handle(
                        cmdIndexFragment,
                        installFromFannelRepo
                    )
                }
                MenuEnums.NO_SCROLL_SAVE_URL.itemId -> {
                    NoScrollUrlSaver.save(
                        cmdIndexFragment,
                        currentAppDirPath,
                        String()
                    )
                }
                MenuEnums.SELECTTERM.itemId  -> {
                    println("pass")
                }
                MenuEnums.TERM1.itemId  -> {
                    FileSystems.updateLastModified(
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_1
                    )
                    terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_1
                }
                MenuEnums.TERM2.itemId  -> {
                    FileSystems.updateLastModified(
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_2
                    )
                    terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_2
                }
                MenuEnums.TERM3.itemId  -> {
                    FileSystems.updateLastModified(
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_3
                    )
                    terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_3
                }
                MenuEnums.TERM4.itemId  -> {
                    FileSystems.updateLastModified(
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_4
                    )
                    terminalViewModel.currentMonitorFileName = UsePath.cmdClickMonitorFileName_4
                }
                MenuEnums.EDIT_STARTUP.itemId -> {
                    ScriptFileEdit.edit(
                        cmdIndexFragment,
                        currentAppDirPath,
                        UsePath.cmdclickStartupJsName,
                    )
                }
                MenuEnums.TERM_REFRESH.itemId -> {
                    TermRefresh.refresh(
                        terminalViewModel.currentMonitorFileName
                    )
                }
                MenuEnums.FORWARD.itemId -> {
                    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.FORWARD
                    )
                }
                else -> {
                    print("pass")
                }
            }.checkAllMatched
            true
        }
    }


    private fun configEdit(){
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
            cmdIndexFragment,
            shellContentsList,
            configShellName
        )
        if(validateErrMessage.isNotEmpty()){
            val shellScriptPath = "${configDirPath}/${configShellName}"
            VaridateionErrDialog.show(
                cmdIndexFragment,
                shellScriptPath,
                validateErrMessage
            )
            return
        }
        val cmdclickConfigFileName = UsePath.cmdclickConfigFileName
        val sharedPref = cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to UsePath.cmdclickSystemAppDirPath,
                SharePrefferenceSetting.current_script_file_name.name
                        to cmdclickConfigFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to FragmentTagManager.Suffix.ON.name
            )
        )
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            UsePath.cmdclickSystemAppDirPath,
            cmdclickConfigFileName,
            FragmentTagManager.Suffix.ON.name
        )
        val listener = cmdIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            cmdEditFragmentTag
        )
    }
}

internal val <T> T.checkAllMatched: T
    get() = this


internal enum class MenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    ADD(mainMenuGroupId, 60100, 1, "add"),
    SETTING(mainMenuGroupId, 60300, 3, "setting"),
    CHDIR(submenuSettingGroupId, 60301, 1, "change_app_dir"),
    CC_IMPORT(submenuSettingGroupId, 60302, 2, "cc_import_manager"),
    SHORTCUT(submenuSettingGroupId, 60303, 3, "create_short_cut"),
    TERM_REFRESH(submenuSettingGroupId, 60304, 4, "term_refresh"),
    TERMUX_SETUP(submenuSettingGroupId, 60305, 5, "termux_setup"),
    CONFIG(submenuSettingGroupId, 60306, 6, "config"),
    SELECTTERM(mainMenuGroupId, 60400, 4, "select_term"),
    TERM1(submenuTermSlectGroupId, 60401, 1, "term_1"),
    TERM2(submenuTermSlectGroupId, 60402, 2, "term_2"),
    TERM3(submenuTermSlectGroupId, 60403, 3, "term_3"),
    TERM4(submenuTermSlectGroupId, 60404, 4, "term_4"),
    EDIT_STARTUP(mainMenuGroupId, 60600, 6, "edit_startup"),
    NO_SCROLL_SAVE_URL(mainMenuGroupId, 60700, 7, "no_scroll_save_url"),
    INSTALL_FANNEL(mainMenuGroupId, 60800, 8, "install_fannel"),
    FORWARD(mainMenuGroupId, 60900, 9, "forward")
}


private fun execAddSettingSubMenu(
    popup: PopupMenu,
    addMenuEnums: MenuEnums,
    submenuGroupId: Int
){
    val sub = popup.menu.addSubMenu(
        addMenuEnums.groupId,
        addMenuEnums.itemId,
        addMenuEnums.order,
        addMenuEnums.itemName
    )
    (MenuEnums.values()).forEach{
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
