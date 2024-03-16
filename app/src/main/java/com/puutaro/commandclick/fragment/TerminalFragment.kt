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
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.register.BroadcastRegisterForTerm
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.InitCurrentMonitorFile
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ValidFannelNameGetterForTerm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.TerminalOnHandlerForEdit
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.FileOrDirGetterForSettingButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Job


class TerminalFragment: Fragment() {


    private var _binding: TerminalFragmentBinding? = null
    val binding get() = _binding!!
    val terminalViewhandler: Handler = Handler(Looper.getMainLooper())
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

    var commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    var commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String
    var busyboxExecutor: BusyboxExecutor? = null
    var readSharePreferenceMap = mapOf<String, String>()
    var srcReadSharedPreferences: Map<String, String>? = null
    var editType =
        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
    var currentAppDirPath = String()
    var currentFannelName = String()
    var setReplaceVariableMap: Map<String, String>? = null
    var settingFannelPath: String = String()
    var displayUpdateCoroutineJob: Job? = null
    var loadAssetCoroutineJob: Job? = null
    var onPageFinishedCoroutineJob: Job? = null
    var registerUrlHistoryTitleCoroutineJob: Job? = null
    var onWebHistoryUpdaterJob: Job? = null
    var previousTerminalTag: String? = null
    private var outputFileLength: Int = 0
    var terminalOn = CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
    var firstDisplayUpdate = true
    var onAdBlock = CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE
    var onTermBackendWhenStart = CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START
    var onTermShortWhenLoad =
        CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE
    var disableShowToolbarWhenHighlight = CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE
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
    var webViewDialogInstance: Dialog? = null
    var goBackFlag = false
//    var fileOrDirGetterForSettingButton: FileOrDirGetterForSettingButton? = null
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
        val terminalViewModel: TerminalViewModel by activityViewModels()

        outputFileLength = savedInstanceState?.getInt("outputFileLength")
            ?:arguments?.getInt("outputFileLength") ?: 0
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.terminal_fragment,
            container,
        false
        )

        if(savedInstanceState != null) {
            binding.terminalWebView.restoreState(savedInstanceState)
        }
        context?.let {
            busyboxExecutor = BusyboxExecutor(
                it,
                UbuntuFiles(it)
            )
        }
        readSharePreferenceMap = EditFragmentArgs.getReadSharePreference(arguments)
        srcReadSharedPreferences = EditFragmentArgs.getSrcReadSharePreference(arguments)
        currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val currentValidFannelName =
            ValidFannelNameGetterForTerm.get(
                this
            )
        val fannelContentsList = CommandClickVariables.makeMainFannelConList(
            currentAppDirPath,
            currentValidFannelName
        )
        setReplaceVariableMap =
            JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                context,
                fannelContentsList,
                currentAppDirPath,
                currentValidFannelName,
            )
        settingFannelPath = FannelStateRooterManager.getSettingFannelPath(
            readSharePreferenceMap,
            setReplaceVariableMap
        )
        editType = EditFragmentArgs.getEditType(arguments)
//        fileOrDirGetterForSettingButton = FileOrDirGetterForSettingButton(this)

        ExecDownLoadManager.set(
            this,
            binding.terminalWebView
        )
        ToolbarHideShowWhenTermLongAndScrollSave.invoke(
            this,
        )

        ConfigFromStartUpFileSetterForTerm.set(this)
        MonitorFileManager.switchCurMonitorFile(
            this,
            terminalViewModel
        )
        UrlHistoryBackUp.backup(this)
        ScrollYPosiBackUp.backup(this)
        AddBlockerHandler.handle(this)
//        AdBlocker.init(this)

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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        TerminalOnHandlerForEdit.handle(this)
        JsDebugger.stockLogSender(this)
    }


    override fun onPause() {
        super.onPause()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        terminalViewModel.isStop = true
        alertDialogInstance?.dismiss()
        webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        )?.onPause()
        terminalViewModel.onDialog = false
        val terminalWebView = binding.terminalWebView
        terminalWebView.stopLoading()
        terminalWebView.removeAllViews()
        activity?.intent?.action = String()
        binding.terminalWebView.onPause()
        loadAssetCoroutineJob?.cancel()
        onPageFinishedCoroutineJob?.cancel()
        registerUrlHistoryTitleCoroutineJob?.cancel()
        onWebHistoryUpdaterJob?.cancel()
        displayUpdateCoroutineJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        val targetFragmentInstance = TargetFragmentInstance()
        val cmdVariableEditFragmentTag = targetFragmentInstance.getCmdEditFragmentTag(activity)
        val bottomFragment = targetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdVariableEditFragmentTag
        )
        val currentBottomFragmentWeight =
            targetFragmentInstance.getCurrentBottomFragmentWeight(bottomFragment)
        firstDisplayUpdate = if(
            !firstDisplayUpdate
            && currentBottomFragmentWeight == ReadLines.LONGTH
        ){
            onTermBackendWhenStart == SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name
        } else firstDisplayUpdate
        InitCurrentMonitorFile.trim(this)
        terminalViewModel.isStop = false
        alertDialogInstance?.dismiss()
        webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        )?.onResume()
        terminalViewModel.onDialog = false
        binding.terminalWebView.onResume()
        activity?.setVolumeControlStream(AudioManager.STREAM_MUSIC)
        IntentAction.handle(this)
        displayUpdateCoroutineJob?.cancel()
        displayUpdateCoroutineJob = DisplaySwitch.update(
            this,
            terminalViewModel,
        )
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

        interface OnSearchTextChangeListener {
        fun onSearchTextChange(
            text: String
        )
    }

    interface OnAutoCompUpdateListener {
        fun onAutoCompUpdate (
            currentAppDirPath: String
        )
    }

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

    interface OnEditTextUpdateListenerForTermFragment {
        fun onEditTextUpdateForTermFragment(
            editTextId: Int?,
            variableValue: String
        )
    }

    interface OnSpinnerUpdateListenerForTermFragment {
        fun onSpinnerUpdateForTermFragment(
            spinnerId: Int?,
            variableValue: String
        )
    }

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
        this.onPageFinishedCoroutineJob?.cancel()
        this.registerUrlHistoryTitleCoroutineJob?.cancel()
        this.displayUpdateCoroutineJob?.cancel()
        this.onWebHistoryUpdaterJob?.cancel()
        _binding = null
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
            readSharePreffernceMap: Map<String, String>,
            updateScriptContents: List<String>,
        )
    }

    interface OnMonitorSizeChangeingForTerm {
        fun onMonitorSizeChangeingForTerm(
            readSharePreffernceMap: Map<String, String>,
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
            parentDirPathSrc: String,
            onDirectoryPickSrc: Boolean = false
        )
    }

    interface OnGetFileListListenerForTerm {
        fun onGetFileListForTerm(
            parentDirPathSrc: String,
            onDirectoryPickSrc: Boolean = false,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            filterShellPathCon: String,
        )
    }
}