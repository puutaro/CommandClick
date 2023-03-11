package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.SharedPreferences
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.AddScriptHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFromDownloadDir
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.EnableGoForwardForWebVeiw
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

private val mainMenuGroupId = 1
private val submenuTermSlectGroupId = 2
private val submenuSettingGroupId = 3

class ToolBarSettingButtonControl(
    private val binding: CommandIndexFragmentBinding,
    private val cmdIndexFragment: CommandIndexFragment,
    private val cmdListAdapter: ArrayAdapter<String>,
    private val sharedPref: SharedPreferences?,
    private val readSharePreffernceMap: Map<String, String>,
){
    private val context = cmdIndexFragment.context
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

    private val settingButtonView = binding.cmdindexSettingButton
    private val popup = PopupMenu(context, settingButtonView)
    private val installFromDownloadDir = InstallFromDownloadDir(
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
            val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
            ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                cmdIndexFragment,
                currentAppDirPath,
            )
        }
    }

    fun toolbarSettingButtonOnClick(){
        settingButtonView.setOnLongClickListener {
            _ ->
            popup.menu.clear()
            popup.menu.add(
                MenuEnums.TERM_REFRESH.groupId,
                MenuEnums.TERM_REFRESH.itemId,
                MenuEnums.TERM_REFRESH.order,
                MenuEnums.TERM_REFRESH.itemName
            )
            popup.menu.add(
                MenuEnums.FORWARD.groupId,
                MenuEnums.FORWARD.itemId,
                MenuEnums.FORWARD.order,
                MenuEnums.FORWARD.itemName
            ).setEnabled(
                EnableGoForwardForWebVeiw.check(cmdIndexFragment)
            )
            val sub = popup.menu.addSubMenu(
                MenuEnums.SELECTTERM.groupId,
                MenuEnums.SELECTTERM.itemId,
                MenuEnums.SELECTTERM.order,
                MenuEnums.SELECTTERM.itemName
            )
            val currentMonitorFileName = terminalViewModel.currentMonitorFileName
            (MenuEnums.values()).forEach{
                val groupId = it.groupId
                if( groupId != submenuTermSlectGroupId) return@forEach
                val itemId = it.itemId
                val checked = it.itemName == currentMonitorFileName
                sub.add(
                    groupId,
                    itemId,
                    it.order,
                    it.itemName
                ).setCheckable(true).setChecked(checked);
            }
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
                    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.CHDIR
                    )
                }
                MenuEnums.CONFIG.itemId -> {
                    configEdit()
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
                MenuEnums.INSTALL.itemId -> {
                    installFromDownloadDir.install()
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
        val configDirPath = UsePath.cmdclickConfigDirPath
        val configShellName = UsePath.cmdclickConfigFileName
        CommandClickShellScript.makeConfigJsFile(
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
        val listener = cmdIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            context?.getString(R.string.cmd_config_variable_edit_fragment)
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
    SHORTCUT(submenuSettingGroupId, 60302, 2, "create_short_cut"),
    INSTALL(submenuSettingGroupId, 60303, 3, "install"),
    TERMUX_SETUP(submenuSettingGroupId, 60304, 4, "termux_setup"),
    CONFIG(submenuSettingGroupId, 60305, 5, "config"),
    SELECTTERM(mainMenuGroupId, 60400, 4, "select_term"),
    TERM1(submenuTermSlectGroupId, 60401, 1, "term_1"),
    TERM2(submenuTermSlectGroupId, 60402, 2, "term_2"),
    TERM3(submenuTermSlectGroupId, 60403, 3, "term_3"),
    TERM4(submenuTermSlectGroupId, 60404, 4, "term_4"),
    TERM_REFRESH(mainMenuGroupId, 60500, 5, "term_refresh"),
    FORWARD(mainMenuGroupId, 60600, 6, "forward")
}


internal fun execAddSettingSubMenu(
    popup: PopupMenu,
    addMenuEnums: MenuEnums,
    submenuGroupId: Int
){
    val sub = popup.menu.addSubMenu(
        addMenuEnums.groupId,
        addMenuEnums.itemId,
        addMenuEnums.order,
        addMenuEnums.itemName
    );
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
