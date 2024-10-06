package com.puutaro.commandclick.fragment

import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.*
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.*
import com.puutaro.commandclick.proccess.pin.PinFannelHideShow
import com.puutaro.commandclick.proccess.setting_menu_for_cmdindex.ExtraMenuForCmdIndex
import com.puutaro.commandclick.proccess.setting_menu_for_cmdindex.page_search.PageSearchManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.CommandIndexViewModel
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class CommandIndexFragment: Fragment() {

    private var isKeyboardShowing: Boolean = false
    private var _binding: CommandIndexFragmentBinding? = null
    val binding get() = _binding!!
    var WebSearchSwitch = WebSearchSwich.ON.bool
//    var historySwitch = SettingVariableSelects.HistorySwitchSelects.OFF.name
    var onTermVisibleWhenKeyboard =
        CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE
//    var urlHistoryOrButtonExec = CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE
    var fannelInfoMap: Map<String, String> = mapOf()
    var terminalColor = CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
    var jsExecuteJob: Job? = null
    var suggestJob: Job? = null
    var showTerminalJobWhenReuse: Job? = null

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
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val terminalViewModel: TerminalViewModel by activityViewModels()
        terminalViewModel.onDialog = false
        val startUpPref = FannelInfoTool.getSharePref(context)
        binding.pageSearch.cmdclickPageSearchToolBar.isVisible = false
        val defaultSystemPath =
            "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        FileSystems.createDirs(
            defaultSystemPath
        )

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

        fannelInfoMap = FannelInfoTool.makeFannelInfoMapByShare(
            startUpPref
        )
        GgleSerchSystemMaker.makeSearchButtonFromActivity(this)
        val keyboardHandleListener = context as? OnKeyboardHandleListenerForCmdIndex
        activity?.let {
            KeyboardVisibilityEvent.setEventListener(
                it,
                this.viewLifecycleOwner,
                KeyboardVisibilityEventListener {
                    isOpen ->
                    // some code depending on keyboard visiblity status
                    if(
                        !this.isVisible
                    ) return@KeyboardVisibilityEventListener
                    if(
                        !view.hasWindowFocus()
                    ) return@KeyboardVisibilityEventListener
                    keyboardHandleListener?.onKeyboardHandleForCmdIndex(isOpen)
                })
        }

        ExecSetToolbarButtonImage.setForCmdIndex(this)
        ExtraMenuForCmdIndex.launch(this)
        PageSearchManager.set(this)
        ToolBarHistoryButtonControl.historyButtonClick(this)
        PinFannelHideShow.setShowListener(this)
        TextSelectionBarCtrl.setOnClickListener(this)
        PreInstallFannel.install(this)
        ExtraMenuGifCreator.create(this)
        UrlImageDownloader.save(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        ExtraMenuGifCreator.exit()
        UrlImageDownloader.exit()
        PreInstallFannel.exit()
        jsExecuteJob?.cancel()
        IndexInitHandler.exit()
        _binding = null
    }

    override fun onPause() {
//        savedEditTextContents = binding.s.text.toString()
//        val cmdIndexViewModel: CommandIndexViewModel by activityViewModels()
//        cmdIndexViewModel.onFocusSearchText = binding.cmdSearchEditText.hasFocus()
        showTerminalJobWhenReuse?.cancel()
//        ExtraMenuGifCreator.exit()
//        PreInstallFannel.exit()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        TerminalShower.show(this)
//        EditTextWhenReuse.focus(this)
        activity?.volumeControlStream = AudioManager.STREAM_MUSIC
//        PreInstallFannel.install(this)
//        ExtraMenuGifCreator.create(this)
    }


//    override fun onStart() {
//        super.onStart()
////        ListViewUpdaterOnStart.update(this)
//    }

    interface OnLongClickMenuItemsForCmdIndexListener {
        fun onLongClickMenuItemsforCmdIndex(
            longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
            editFragmentArgs: EditFragmentArgs,
            editFragmentTag: String,
            terminalFragmentTag: String
        )
    }


    interface OnToolbarMenuCategoriesListener {
        fun onToolbarMenuCategories(
            toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
            editFragmentArgs: EditFragmentArgs,
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
//            currentAppDirPath: String,
            fannelName: String,
        )
    }

    interface OnGetPermissionListenerForCmdIndex {
        fun onGetPermissionForCmdIndex(
            permissionStr: String
        )
    }

    interface OnConnectWifiListenerForCmdIndex {
        fun onConnectWifiForCmdIndex(
            ssid: String,
            pin: String
        )
    }

    interface OnCaptureActivityListenerForIndex {
        fun onCaptureActivityForIndex()
    }

    private fun exitDialog(
        cmdList: RecyclerView
    ){
        cmdList.layoutManager = null
        cmdList.adapter = null
        cmdList.recycledViewPool.clear()
        cmdList.removeAllViews()
    }

    interface OnZeroSizingListener {
        fun onZeroSizing()
    }

    interface OnSearchButtonMakeListenerForCmdIndex {
        fun onSearchButtonMakeForCmdIndex()
    }


    interface OnKeyboardHandleListenerForCmdIndex {
        fun onKeyboardHandleForCmdIndex(isOpen: Boolean)
    }

    interface OnPinFannelShowListener {
        fun onPinFannelShow(
            fannelManagerPinImageView: AppCompatImageView? = null
        )
    }

    interface OnPageSearchSwitchListener {
        fun onPageSearchSwitch()
    }
}
