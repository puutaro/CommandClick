package com.puutaro.commandclick.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecBackstackHandle
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.GgleSerchSystemMaker
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.register.BroadcastRegisterForTerm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.GgleSchDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WebViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.InitCurrentMonitorFile
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.TerminalOnHandlerForEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ValidFannelNameGetterForTerm
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryCaptureTool
import com.puutaro.commandclick.proccess.pin.PinFannelHideShow
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Job
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.lang.ref.WeakReference


class TerminalFragment:
    Fragment() {


    private var _binding: TerminalFragmentBinding? = null
    val binding get() = _binding!!
    val terminalViewhandler: Handler = Handler(Looper.getMainLooper())
    var busyboxExecutor: BusyboxExecutor? = null
    var fannelInfoMap = mapOf<String, String>()
    var srcFannelInfoMap: Map<String, String>? = null
    var editType =
        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
//    var currentAppDirPath = String()
    var currentFannelName = String()
    var setReplaceVariableMap: Map<String, String>? = null
    var settingFannelPath: String = String()
    var displayUpdateCoroutineJob: Job? = null
    var loadAssetCoroutineJob: Job? = null
    var onPageFinishedCoroutineJob: Job? = null
    var onScrollPosiSaveJob: Job? = null
    var registerUrlHistoryTitleCoroutineJob: Job? = null
    var onWebHistoryUpdaterJob: Job? = null
    var onPocketWebHistoryUpdaterJob: Job? = null
    var onRegisterPocketWebViewUrl: Job? = null
    var previousTerminalTag: String? = null
    private var outputFileLength: Int = 0
    var terminalOn = CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
    var firstDisplayUpdate = true
    var onAdBlock = CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE
    var onLaunchUrlHistoryByBackstack =
        CommandClickScriptVariable.ON_LAUNCH_URL_HISTORY_BY_BACKSTACK_DEFAULT_VALUE
    var onTermBackendWhenStart = CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START
    var onTermShortWhenLoad =
        CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE
    var disableShowToolbarWhenHighlight =
        CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE
    var defaultMonitorFile = CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE
    var fontZoomPercent = CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalColor = CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
    var terminalFontColor = CommandClickScriptVariable.TERMINAL_FONT_COLOR_DEFAULT_VALUE
    var currentUrl: String? = null
    var onUrlHistoryRegister = CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE
    var ignoreHistoryPathList: List<String>? = null
    var onUrlLaunchIntent: Boolean = false
    var noScrollSaveUrls = emptyList<String>()
    var srcImageAnchorLongPressMenuFilePath: String = String()
    var srcAnchorLongPressMenuFilePath: String = String()
    var imageLongPressMenuFilePath: String = String()
    var rowsMap: MutableMap<String, List<List<String>>> = mutableMapOf()
    var headerMap: MutableMap<String, List<String>> = mutableMapOf()
    var alertDialogInstance: AlertDialog? = null
    var fannelIndexDialog: Dialog? = null
//    var webViewDialogInstance: Dialog? = null
    var goBackFlag = false
    var pocketWebViewManager: WebViewJsDialog? = null
    var ggleWebViewManager: GgleSchDialog? = null
    var selectionText = String()
//    var extraMapBitmapList: List<Bitmap?> = emptyList()
    var broadcastReceiverForTerm: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            BroadcastHandlerForTerm.handle(
                this@TerminalFragment,
                intent
            )
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.terminal_fragment,
            container,
        false
        )
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val terminalViewModel: TerminalViewModel by activityViewModels()
        outputFileLength = savedInstanceState?.getInt("outputFileLength")
            ?:arguments?.getInt("outputFileLength") ?: 0

        if(savedInstanceState != null) {
            binding.terminalWebView.restoreState(savedInstanceState)
        }
        context?.let {
            busyboxExecutor = BusyboxExecutor(
                it,
                UbuntuFiles(it)
            )
        }
        fannelInfoMap = EditFragmentArgs.getFannelInfoMap(arguments)
        srcFannelInfoMap = EditFragmentArgs.getSrcFannelInfoMap(arguments)
//        currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentValidFannelName =
            ValidFannelNameGetterForTerm.get(
                this
            )
        val fannelContentsList = CommandClickVariables.makeMainFannelConList(
            currentValidFannelName
        )
        setReplaceVariableMap =
            JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                context,
                fannelContentsList,
//                currentAppDirPath,
                currentValidFannelName,
            )
        settingFannelPath = FannelStateRooterManager.getSettingFannelPath(
            fannelInfoMap,
            setReplaceVariableMap
        )
        editType = EditFragmentArgs.getEditType(arguments)
        ExecDownLoadManager.set(
            this,
            binding.terminalWebView
        )
        pocketWebViewManager = WebViewJsDialog(WeakReference(this))
        ggleWebViewManager = GgleSchDialog(WeakReference(this))
        TerminalToolbarHandler.handler(this)
        GgleSerchSystemMaker.makeSearchButtonFromActivity(this)
        GgleSchToolbar.set(this)
        val keyboardHandleListener = context as? OnKeyboardHandleListenerForTerm
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
                        view.height == 0
                    ) return@KeyboardVisibilityEventListener
                    if(
                        !view.hasWindowFocus()
                    ) return@KeyboardVisibilityEventListener
                    keyboardHandleListener?.onKeyboardHandleForTerm(isOpen)
                })
        }
        PinFannelBarManager.set(
            this,
            tag,
            binding.fannelPinRecyclerView
        )
        PinFannelHideShow.setHideListener(this)

        ToolbarHideShowWhenTermLongAndScrollSave.invoke(
            this,
        )

        ConfigFromPreferenceFileSetterForTerm.set(this)
        MonitorFileManager.switchCurMonitorFile(
            this,
            terminalViewModel
        )
        UrlHistoryBackUp.backup(this)
        ScrollYPosiBackUp.backup()
        AddBlockerHandler.handle(this)

        WebChromeClientSetter.set(
            this,
            binding.terminalWebView,
            binding.progressBar
        )
        FindListenerSetter.set(this)

        WebViewClientSetter.set(this@TerminalFragment)

        WebViewSettings.set(this)
        TermOnLongClickListener.set(this)
        MonitorFileManager.trim(terminalViewModel)
        BroadcastRegisterForTerm.register(this)
        FannelHistoryGifCreator.watch(this)
        ButtonImageCreator.create(this)

    }

    override fun onStart() {
        super.onStart()
        ExecBackstackHandle.initPrevBackTime()
        TerminalOnHandlerForEdit.handle(this)
        JsDebugger.stockLogSender(this)
        GifCreateMonitor.watch(this)
    }


    override fun onPause() {
        super.onPause()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        alertDialogInstance?.dismiss()
        alertDialogInstance = null
//        webViewDialogInstance?.findViewById<WebView>(
//            R.id.webview_dialog_webview
//        )?.onPause()
//        terminalViewModel.onDialog = false
        val terminalWebView = binding.terminalWebView
        terminalWebView.stopLoading()
        terminalWebView.removeAllViews()
        activity?.intent?.action = String()
        binding.terminalWebView.onPause()
        loadAssetCoroutineJob?.cancel()
        onPageFinishedCoroutineJob?.cancel()
        onScrollPosiSaveJob?.cancel()
        registerUrlHistoryTitleCoroutineJob?.cancel()
        onWebHistoryUpdaterJob?.cancel()
        onPocketWebHistoryUpdaterJob?.cancel()
        displayUpdateCoroutineJob?.cancel()
        UrlCaptureWatcher.exit()
        GifCreateMonitor.exit()
//        FannelHistoryGifCreator.exit()
//        GifCreateMonitor.exit()
//        ButtonImageCreator.exit()
    }

    override fun onResume() {
        super.onResume()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        val cmdVariableEditFragmentTag = TargetFragmentInstance.getCmdEditFragmentTag(activity)
        val bottomFragment = TargetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdVariableEditFragmentTag
        )
        val currentBottomFragmentWeight =
            TargetFragmentInstance.getCurrentBottomFragmentWeight(bottomFragment)
        firstDisplayUpdate = if(
            !firstDisplayUpdate
            && currentBottomFragmentWeight == ReadLines.LONGTH
        ){
            onTermBackendWhenStart == SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name
        } else firstDisplayUpdate
        InitCurrentMonitorFile.trim(this)
        alertDialogInstance?.dismiss()
        alertDialogInstance = null
//        webViewDialogInstance?.findViewById<WebView>(
//            R.id.webview_dialog_webview
//        )?.onResume()
//        terminalViewModel.onDialog = false
        binding.terminalWebView.onResume()
        activity?.volumeControlStream = AudioManager.STREAM_MUSIC
//        UrlLaunchIntentAction.handle(this)
        StartupHandler.invoke(this)
        displayUpdateCoroutineJob?.cancel()
        displayUpdateCoroutineJob = DisplaySwitch.update(
            this,
            terminalViewModel,
        )
        UrlCaptureWatcher.watch(this)
        previousTerminalTag = tag
    }


    override fun onSaveInstanceState(outState:Bundle){
        outState.putInt("outputFileLength", outputFileLength)
    }

    interface onBackstackWhenTermLongInRestartListener {
        fun onBackstackWhenTermLongInRestart()
    }

    interface OnToolBarVisibleChangeListener {
        fun onToolBarVisibleChange(
            toolBarVisible: Boolean,
            bottomFragment: Fragment?,
        )
    }

//    interface OnSearchTextChangeListener {
//        fun onSearchTextChange(
//            text: String
//        )
//    }

//    interface OnAutoCompUpdateListener {
//        fun onAutoCompUpdate (
////            currentAppDirPath: String
//        )
//    }

    interface OnTermLongChangeListenerForTerminalFragment {
        fun onTermLongChangeForTerminalFragment(
            bottomFragment: Fragment?
        )
    }

    interface OnTermShortSizeListenerForTerminalFragment {
        fun onTermNormalSizeForTerminalFragment(
            terminalFragment: TerminalFragment
        )
    }

    interface OnPageLoadPageSearchDisableListener {
        fun onPageLoadPageSearchDisable()
    }

    interface OnFindPageSearchResultListener {
        fun onFindPageSearchResultListner(
            activeMatchOrdinal: Int,
            numberOfMatches: Int,
        )
    }

    interface OnFileChooseListener {
        fun onFileCoose(
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: WebChromeClient.FileChooserParams?
        )
    }

//    interface OnEditTextUpdateListenerForTermFragment {
//        fun onEditTextUpdateForTermFragment(
//            editTextId: Int?,
//            variableValue: String
//        )
//    }

//    interface OnSpinnerUpdateListenerForTermFragment {
//        fun onSpinnerUpdateForTermFragment(
//            spinnerId: Int?,
//            variableValue: String
//        )
//    }

    interface OnEditableSpinnerUpdateListenerForTermFragment {
        fun onEditableSpinnerUpdateForTermFragment(
            spinnerId: Int?,
            variableValue: String
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        BroadcastRegister.unregisterBroadcastReceiverForTerm(
            this,
            broadcastReceiverForTerm
        )
        UrlCaptureWatcher.exit()
        PinFannelBarManager.clear(binding.fannelPinRecyclerView)
        FannelHistoryGifCreator.exit()
        GifCreateMonitor.exit()
        ButtonImageCreator.exit()
        FannelHistoryCaptureTool.exit()
        this.onPageFinishedCoroutineJob?.cancel()
        onScrollPosiSaveJob?.cancel()
        this.registerUrlHistoryTitleCoroutineJob?.cancel()
        this.displayUpdateCoroutineJob?.cancel()
        this.onWebHistoryUpdaterJob?.cancel()
        onPocketWebHistoryUpdaterJob?.cancel()
        onRegisterPocketWebViewUrl?.cancel()
        pocketWebViewManager?.destroyWebView()
        ggleWebViewManager?.destroyWebView()
        val terminalWebView = binding.terminalWebView
        terminalWebView.onPause()
        terminalWebView.webChromeClient = null
        terminalWebView.clearHistory()
        terminalWebView.clearCache(true)
        terminalWebView.removeAllViews()
        terminalWebView.destroy()
        binding.terminalFragment.removeAllViews()
        _binding = null
        pocketWebViewManager = null
//        webViewDialogInstance?.dismiss()
//        webViewDialogInstance = null
        alertDialogInstance?.dismiss()
        alertDialogInstance = null
        firstDisplayUpdate = true
    }

    interface OnMultiSelectListenerForTerm {
        fun onMultiSelectForTerm(
            title: String,
            updatedMultiModelArray: ArrayList<MultiSelectModel>,
            preSelectedMultiModelArray: ArrayList<Int>
        )
    }

    interface OnTermSizeMinimumListenerForTerm {
        fun onTermSizeMinimumForTerm()
    }

    interface OnAdBlockListener {
        fun exeOnAdblock()
    }

    interface OnGetPermissionListenerForTerm {
        fun onGetPermission(
            permissionStr: String
        )
    }

    interface OnChangeEditFragmentListenerForTerm {
        fun onChangeEditFragment(
            editFragmentArgs: EditFragmentArgs,
            cmdEditFragmentTag: String,
            editTerminalFragmentTag: String,
            disableAddToBackStack: Boolean = false,
        )
    }
    interface OnEditFannelContentsListUpdateListenerForTerm {
        fun onEditFannelContentsListUpdateForTerm(
            fannelInfoMap: Map<String, String>,
            updateScriptContents: List<String>,
        )
    }

    interface OnMonitorSizeChangingForTerm {
        fun onMonitorSizeChangingForTerm(
            fannelInfoMap: Map<String, String>,
        )
    }

    interface OnPopStackImmediateListenerForTerm {
        fun onPopStackImmediateForTerm()
    }

    interface OnCmdValSaveAndBackListenerForTerm {
        fun onSettingOkButtonForTerm()
    }

    interface OnGetFileListenerForTerm {
        fun onGetFileForTerm(
//            parentDirPathSrc: String,
            onDirectoryPickSrc: Boolean = false,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            filterShellCon: String,
            initialPath: String,
            pickerMacro: FilePickerTool.PickerMacro?,
            currentFannelName: String,
            tag: String,
        )
    }

    interface OnGetFileListListenerForTerm {
        fun onGetFileListForTerm(
            onDirectoryPickSrc: Boolean = false,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            filterShellPathCon: String,
            initialPath: String,
            pickerMacro: FilePickerTool.PickerMacro?,
            currentFannelName: String,
            tag: String,
        )
    }

    interface GetSdcardDirListenerForTerm {
        fun getSdcardDirForTerm(
            isCreate: Boolean
        )
    }

    interface OnRestartListenerForTerm {
        fun onRestartForTerm()
    }

    interface OnPinClickForTermListener {
        fun onPinClickForTerm(
            longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
            editFragmentArgs: EditFragmentArgs,
            editFragmentTag: String,
            terminalFragmentTag: String
        )
    }

    interface OnSetToolbarButtonImageListener {
        fun onSetToolbarButtonImage()
    }

    interface OnSearchButtonMakeListenerForTerm {
        fun onSearchButtonMakeForTerm()
    }

    interface OnKeyboardHandleListenerForTerm {
        fun onKeyboardHandleForTerm(isOpen: Boolean)
    }
    interface OnPinFannelHideListener {
        fun onPinFannelHide(fannelManagerPinImageView: AppCompatImageView? = null)
    }

    interface OnPageSearchSwitchListenerForTerm{
        fun onPageSearchSwitchForTerm()
    }


    interface OnSelectionSearchBarSwitchListenerForTerm {
        fun onSelectionSearchBarSwitchForTerm(
            isShow: Boolean
        )
    }

    interface OnUpdateSelectionTextViewListenerForTerm {
        fun onUpdateSelectionTextViewForTerm(
            updateText: String
        )
    }

    interface OnCaptureActivityListenerForTerm {
        fun onCaptureActivityForTerm()
    }
}