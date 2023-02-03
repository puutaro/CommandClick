package com.puutaro.commandclick.fragment

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.*
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.AutoCompleteEditTexter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.*
import com.puutaro.commandclick.fragment_lib.command_index_fragment.AutoShellExecManager
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class CommandIndexFragment: Fragment() {

    private var isKeyboardShowing: Boolean = false
    private var _binding: CommandIndexFragmentBinding? = null
    val binding get() = _binding!!
    var mParentContextMenuListIndex: Int = 0
    var runShell = CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
    var SpecialSearchSwitch = WebSearchSwich.OFF.bool
    var historySwitch = SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name
    var urlHistoryOrButtonExec = CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE
    var shiban = CommandClickShellScript.CMDCLICK_SHIBAN_DEFAULT_VALUE
    var readSharePreffernceMap: Map<String, String> = mapOf()
    var terminalColor = CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
    var statusBarIconColorMode = CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.command_index_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val terminalViewModel: TerminalViewModel by activityViewModels()
        val startUpPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val thisFragmentTag = this.tag
        val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
        FileSystems.createDirs(
            UsePath.cmdclickDefaultAppDirPath
        )
        FileSystems.createDirs(
            UsePath.cmdclickAppDirAdminPath
        )
        FileSystems.createDirs(
            cmdclickAppHistoryDirAdminPath
        )
        FileSystems.createFiles(
            cmdclickAppHistoryDirAdminPath,
            AppHistoryManager.makeAppHistoryFileNameForInit(
                UsePath.cmdclickDefaultAppDirName,
            )
        )
        CommandClickShellScript.makeAppDirAdminFile(
            UsePath.cmdclickAppDirAdminPath,
            UsePath.cmdclickDefaultAppDirName +
                    CommandClickShellScript.SHELL_FILE_SUFFIX
        )

        CommandClickShellScript.makeConfigShellFile(
            UsePath.cmdclickConfigDirPath,
            UsePath.cmdclickConfigFileName
        )

        ConfigFromConfigFileSetter.set(this)
        val appDirAdminTag = getString(R.string.app_dir_admin)
        if(
            thisFragmentTag == appDirAdminTag
        ){
            SharePreffrenceMethod.putSharePreffrence(
                startUpPref,
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name
                            to UsePath.cmdclickAppDirAdminPath,
                )
            )
        } else {
//            val listener = this.context as? CommandIndexFragment.OnBackstackDeleteListner
//            listener?.onBackstackDelete()
            val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath

            if(
                IntentAction.judge(this)
            ){
                FileSystems.updateLastModified(
                    cmdclickAppDirAdminPath,
                    UsePath.cmdclickDefaultAppDirName +
                            CommandClickShellScript.SHELL_FILE_SUFFIX
                )
            } else {
                UpdateLastModifyFromSharePrefDir.update(startUpPref)
            }

            val currentDirName = FileSystems.filterSuffixShellFiles(
                cmdclickAppDirAdminPath,
                "on"
            ).firstOrNull()?.removeSuffix(
                CommandClickShellScript.SHELL_FILE_SUFFIX
            ) ?: UsePath.cmdclickDefaultAppDirName
            val currentAppDirPath = "${cmdclickAppDirPath}/${currentDirName}"

            SharePreffrenceMethod.putSharePreffrence(
                startUpPref,
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name
                            to currentAppDirPath,
                    SharePrefferenceSetting.current_shell_file_name.name
                            to SharePrefferenceSetting.current_shell_file_name.defalutStr,
                    SharePrefferenceSetting.on_shortcut.name to SharePrefferenceSetting.on_shortcut.defalutStr,
                )
            )

            FileSystems.updateLastModified(
                UsePath.cmdclickAppHistoryDirAdminPath,
                AppHistoryManager.makeAppHistoryFileNameForInit(
                    currentAppDirPath,
                )
            )
            CommandClickShellScript.makeButtonExecShell(
                shiban,
                currentAppDirPath,
                UsePath.cmdclickButtonExecShellFileName
            )
            CommandClickShellScript.makeAutoShellFile(
                CommandClickShellScript.CMDCLICK_SHIBAN_DEFAULT_VALUE,
                currentAppDirPath,
                UsePath.cmdclickStartupShellName
            )
            CommandClickShellScript.makeAutoShellFile(
                CommandClickShellScript.CMDCLICK_SHIBAN_DEFAULT_VALUE,
                currentAppDirPath,
                UsePath.cmdclickEndShellName
            )
            ConfigFromStartUpFileSetter.set(
                this,
                currentAppDirPath,
            )

            val pageSearchToolbarManager =
                PageSearchToolbarManager(this)

            pageSearchToolbarManager.cancleButtonClickListner()
            pageSearchToolbarManager.pageSearchTextChangeListner()
            pageSearchToolbarManager.onKeyListner()
            pageSearchToolbarManager.searchTopClickLisnter()
            pageSearchToolbarManager.searchDownClickLisnter()
        }

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context?.let {
            window?.statusBarColor = Color.parseColor(terminalColor)
//            window?.decorView?.systemUiVisibility = if(
//                statusBarIconColorMode ==
//                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE
//            ) 0
//            else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }


        readSharePreffernceMap = SharePreffrenceMethod.makeReadSharePreffernceMap(
            startUpPref
        )

        StartupOrEditExecuteOnceShell.invoke(
            this,
            readSharePreffernceMap
        )


        val autoCompleteEditTexter = AutoCompleteEditTexter(
            this,
        )
        autoCompleteEditTexter.setItemClickListner()
        val cmdListView = binding.cmdList
        val makeListView = MakeListView(
            binding,
            this,
            readSharePreffernceMap
        )
        val cmdListAdapter = makeListView.makeList(
            requireContext()
        )
        makeListView.makeClickItemListner(
            cmdListAdapter
        )
        makeListView.cmdListSwipeToRefresh(
            cmdListAdapter,
        )
        cmdListView.adapter = cmdListAdapter
        makeListView.makeTextFilter(
            cmdListAdapter,
        )
        registerForContextMenu(cmdListView)
        if(
            thisFragmentTag == appDirAdminTag
        ) {
            val makeChangeDirView = MakeChangeDirView(
                this,
                binding
            )
            makeChangeDirView.addHeaderPrompt(
                "select app dir"
            )
            makeChangeDirView.hideToolbar()
            return
        }
        val cmdindexInternetButton = binding.cmdindexInternetButton
        KeyboardVisibilityEvent.setEventListener(activity) {
                isOpen ->
            if(!this.isVisible) return@setEventListener
            val enableInternetButton = (
                    !isOpen
                    || terminalViewModel.readlinesNum != ReadLines.SHORTH
                    )
            cmdindexInternetButton.isEnabled = enableInternetButton
            if(enableInternetButton){
                cmdindexInternetButton.imageTintList = context?.getColorStateList(R.color.black)
            } else {
                cmdindexInternetButton.imageTintList = context?.getColorStateList(android.R.color.darker_gray)
            }
            val isLongth = terminalViewModel.readlinesNum != ReadLines.SHORTH
            if(isLongth) {
                KeyboardForCmdIndex.ajustCmdIndexFragmentWhenTermLong(
                        isOpen,
                        this,
                )
                return@setEventListener
            }
            KeyboardForCmdIndex.historyAndSearchHideShow(
                isOpen,
                this,
            )
            val listener = context as? OnKeyboardVisibleListener
            listener?.onKeyBoardVisibleChange(
                isOpen,
                this.isVisible,
                this.SpecialSearchSwitch
            )
        }

        val toolBarSettingButtonControl = ToolBarSettingButtonControl(
            binding,
            this,
            cmdListAdapter,
            startUpPref,
            readSharePreffernceMap
        )
        toolBarSettingButtonControl.inflate()
        toolBarSettingButtonControl.toolbarSettingButtonOnClick()
        toolBarSettingButtonControl.popupMenuItemSelected(this)
        toolBarSettingButtonControl.toolbarSettingButtonOnLongClick()


        val toolBarHistoryButtonControl = ToolBarHistoryButtonControl(
            this,
            readSharePreffernceMap,
        )
        toolBarHistoryButtonControl.historyButtonClick()

        val toolBarInternetButtonControl = ToolBarInternetButtonControl(
            this,
            readSharePreffernceMap
        )
        toolBarInternetButtonControl.interneButtontSetOnClickListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        AutoShellExecManager.fire(
            this,
            UsePath.cmdclickEndShellName
        )
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        activity?.setVolumeControlStream(AudioManager.STREAM_MUSIC)
    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        view: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val thisFragmentTag = this.tag
        super.onCreateContextMenu(menu, view, menuInfo)
        val inflater = this.getActivity()?.getMenuInflater();
        if(
            thisFragmentTag ==  getString(R.string.app_dir_admin)
        ) {
            inflater?.inflate(R.menu.app_dir_admin_list_menu, menu)
        } else {
            inflater?.inflate(R.menu.cmd_index_list_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val startUpPref =  activity?.getPreferences(Context.MODE_PRIVATE)
        val readSharePreffernceMap = SharePreffrenceMethod.makeReadSharePreffernceMap(
            startUpPref
        )
        val makeListView = MakeListView(
            binding,
            this,
            readSharePreffernceMap
        )

        val cmdListAdapter = makeListView.makeList(
            requireContext()
        )
        return makeListView.onLongClickDo (
            item,
            super.onContextItemSelected(item),
            cmdListAdapter
        )
    }

    interface OnLongClickMenuItemsForCmdIndexListener {
        fun onLongClickMenuItemsforCmdIndex(
            longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
            editFragmentTag: String? = String(),
            onOpenTerminal: Boolean = false,
            terminalFragmentTag: String? = null
        )
    }


    interface OnToolbarMenuCategoriesListener {
        fun onToolbarMenuCategories(
            toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex
        )
    }


    interface OnKeyboardVisibleListener {
        fun onKeyBoardVisibleChange(
            isKeyboardShowing: Boolean,
            isVisible: Boolean,
            SpecialSearchSwitch: Boolean
        )
    }

    interface OnListItemClickListener {
        fun onListItemClicked(
            curentFragmentTag: String
        )
    }

    override fun onSaveInstanceState(outState:Bundle){
        outState.putBoolean("isKeyboardShowing", isKeyboardShowing)
    }

    interface OnBackstackDeleteListner {
        fun onBackstackDelete()
    }

    interface OnQueryTextChangedListener {
        fun onQueryTextChanged(searchUrl: String)
    }

    interface OnFilterWebViewListener {
        fun onFilterWebView(filterText: String)
    }


    interface OnPageSearchToolbarClickListener {
        fun onPageSearchToolbarClick(
            pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant,
            searchText: String = String(),
        )
    }

    interface OnTerminalWebViewInitListener {
        fun onTerminalWebViewInit(
            fontZoomPercent: Int
        )
    }
}
