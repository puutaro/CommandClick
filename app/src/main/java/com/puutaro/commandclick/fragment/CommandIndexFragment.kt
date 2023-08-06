package com.puutaro.commandclick.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.*
import com.puutaro.commandclick.fragment_lib.command_index_fragment.broadcast.receiver.BroadcastReceiveHandlerForCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.CmdClickSystemAppDir
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.*
import com.puutaro.commandclick.proccess.broadcast.BroadcastManager
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.CommandIndexViewModel
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class CommandIndexFragment: Fragment() {

    private var isKeyboardShowing: Boolean = false
    private var _binding: CommandIndexFragmentBinding? = null
    val binding get() = _binding!!
    var mParentContextMenuListIndex: Int = 0
    var recyclerViewIndex = 0
    var runShell = CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
    var WebSearchSwitch = WebSearchSwich.ON.bool
    var historySwitch = SettingVariableSelects.HistorySwitchSelects.OFF.name
    var urlHistoryOrButtonExec = CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE
    var shiban = CommandClickScriptVariable.CMDCLICK_SHIBAN_DEFAULT_VALUE
    var readSharePreffernceMap: Map<String, String> = mapOf()
    var terminalColor = CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
    var statusBarIconColorMode = CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE
    var onUrlLaunchIntent = false
    var jsExecuteJob: Job? = null
    var suggestJob: Job? = null
    var enableFannel = true
    var showTerminalJobWhenReuse: Job? = null
    var installFannelDialog: Dialog? = null
    var savedEditTextContents = String()
    var homeFannelHistoryNameList: List<String>? = null
    var bottomScriptUrlList = emptyList<String>()

    private var broadcastReceiverForCmdIndex: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            BroadcastReceiveHandlerForCmdIndex.handle(
                this@CommandIndexFragment,
                intent,
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
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
        val cmdIndexViewModel: CommandIndexViewModel by activityViewModels()
        cmdIndexViewModel.onFocusSearchText = false
        val startUpPref = activity?.getPreferences(Context.MODE_PRIVATE)
        binding.pageSearch.cmdclickPageSearchToolBar.isVisible = false
        val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
        val defaultSystemPath =
            "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        FileSystems.createDirs(
            defaultSystemPath
        )
        FileSystems.createDirs(
            cmdclickAppHistoryDirAdminPath
        )
        FileSystems.createDirs(
            UsePath.cmdclickCcimportDirPath
        )
        FileSystems.createFiles(
            cmdclickAppHistoryDirAdminPath,
            AppHistoryManager.makeAppHistoryFileNameForInit(
                UsePath.cmdclickDefaultAppDirName,
            )
        )
        CmdClickSystemAppDir.create(
            this
        )

        CommandClickScriptVariable.makeAppDirAdminFile(
            UsePath.cmdclickAppDirAdminPath,
            UsePath.cmdclickDefaultAppDirName +
                    UsePath.JS_FILE_SUFFIX
        )

        CommandClickScriptVariable.makeConfigJsFile(
            UsePath.cmdclickSystemAppDirPath,
            UsePath.cmdclickConfigFileName
        )

        ConfigFromConfigFileSetter.set(this)

        IndexInitHandler.handle(this)

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context?.let {
            window?.statusBarColor = try {
                Color.parseColor(terminalColor)
            } catch (e: Exception){
                Color.parseColor(
                    CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
                )
            }
        }

        readSharePreffernceMap = SharePreffrenceMethod.makeReadSharePreffernceMap(
            startUpPref
        )

        StartupOrEditExecuteOnceShell.invoke(
            this,
            readSharePreffernceMap
        )

        val cmdListView = binding.cmdList
        cmdListView.setHasFixedSize(true)
        cmdListView.setItemViewCacheSize(100)
        val makeListView = MakeListView(
            binding,
            this,
            readSharePreffernceMap
        )
        val fannelIndexListAdapter = makeListView.makeList(
            requireContext()
        )
        makeListView.makeClickItemListener(
            fannelIndexListAdapter
        )
        makeListView.cmdListSwipeToRefresh()
        cmdListView.adapter = fannelIndexListAdapter
        cmdListView.layoutManager = PreLoadLayoutManager(
            context,
        )
        cmdListView.layoutManager?.scrollToPosition(
            fannelIndexListAdapter.itemCount - 1
        )
        makeListView.makeTextFilter(
            fannelIndexListAdapter,
        )
        registerForContextMenu(cmdListView)

        val cmdindexInternetButton = binding.cmdindexInternetButton
        KeyboardVisibilityEvent.setEventListener(activity) {
                isOpen ->
            if(!this.isVisible) return@setEventListener
            if(terminalViewModel.onDialog) return@setEventListener
            val enableInternetButton = (
                    !isOpen
                    || terminalViewModel.readlinesNum != ReadLines.SHORTH
                    )
            cmdindexInternetButton.isEnabled = enableInternetButton
            if(enableInternetButton){
                cmdindexInternetButton.imageTintList = context?.getColorStateList(R.color.terminal_color)
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
                this.WebSearchSwitch
            )
            if(!isOpen
                && binding.cmdListSwipeToRefresh.isVisible
            ){
                CoroutineScope(Dispatchers.Main).launch {
                    delay(100)
                    cmdListView.scrollToPosition(
                        fannelIndexListAdapter.itemCount - 1
                    )
                }
            }
        }

        installFannelDialog = FannelInstallDialog.create(this)

        val toolBarSettingButtonControl = ToolBarSettingButtonControl(
            binding,
            this,
            startUpPref,
            readSharePreffernceMap
        )
//        toolBarSettingButtonControl.inflate()
        toolBarSettingButtonControl.toolbarSettingButtonOnClick()
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
        val cmdIndexViewModel: CommandIndexViewModel by activityViewModels()
        cmdIndexViewModel.onFocusSearchText = false
        jsExecuteJob?.cancel()
        _binding = null
    }

    override fun onPause() {
        savedEditTextContents = binding.cmdSearchEditText.text.toString()
        val cmdIndexViewModel: CommandIndexViewModel by activityViewModels()
        cmdIndexViewModel.onFocusSearchText = binding.cmdSearchEditText.hasFocus()
        installFannelDialog?.dismiss()
        showTerminalJobWhenReuse?.cancel()
        BroadcastManager.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForCmdIndex,
        )
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        TerminalShower.show(this)
        EditTextWhenReuse.focus(this)
        installFannelDialog?.dismiss()
        activity?.volumeControlStream = AudioManager.STREAM_MUSIC
        BroadcastManager.registerBroadcastReceiver(
            this,
            broadcastReceiverForCmdIndex,
            BroadCastIntentSchemeForCmdIndex.UPDATE_FANNEL_LIST.action
        )
    }


    override fun onStart() {
        super.onStart()
        ListViewUpdaterOnStart.update(this)
    }


//    override fun onCreateContextMenu(
//        menu: ContextMenu,
//        view: View,
//        menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//        super.onCreateContextMenu(menu, view, menuInfo)
//        val inflater = this.activity?.menuInflater;
//        inflater?.inflate(R.menu.cmd_index_list_menu, menu)
//    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)

        val startUpPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val readSharePreffernceMap = SharePreffrenceMethod.makeReadSharePreffernceMap(
            startUpPref
        )
        val makeListView = MakeListView(
            binding,
            this,
            readSharePreffernceMap
        )

        val fannelIndexListAdapter = makeListView.makeList(
            requireContext()
        )
        return makeListView.onLongClickDo (
            item,
            super.onContextItemSelected(item),
            fannelIndexListAdapter
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
            currentFragmentTag: String
        )
    }

    override fun onSaveInstanceState(outState:Bundle){
        outState.putBoolean("isKeyboardShowing", isKeyboardShowing)
    }

    interface OnBackstackDeleteListener {
        fun onBackstackDelete()
    }

    interface OnLaunchUrlByWebViewListener {
        fun onLaunchUrlByWebView(searchUrl: String)
    }

    interface OnFilterWebViewListener {
        fun onFilterWebView(filterText: String)
    }


    interface OnPageSearchToolbarClickListener {
        fun onPageSearchToolbarClick(
            pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant,
            tag: String?,
            searchText: String = String(),
        )
    }

    interface OnUpdateNoSaveUrlPathsListener {
        fun onUpdateNoSaveUrlPaths(
            currentAppDirPath: String,
            fannelName: String,
        )
    }
}
