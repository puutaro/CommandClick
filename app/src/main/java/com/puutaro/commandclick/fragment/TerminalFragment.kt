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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.proccess.broadcast.BroadcastManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.HtmlLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.BroadcastHtmlReceiveHandler
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorTextLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.InitCurrentMonitorFile
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.TerminalOnHandlerForEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.ChangeTargetFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Job


class   TerminalFragment: Fragment() {


    private var _binding: TerminalFragmentBinding? = null
    val binding get() = _binding!!
    val terminalViewhandler : Handler = Handler(Looper.getMainLooper())
    var displayUpdateCoroutineJob: Job? = null
    var loadAssetCoroutineJob: Job? = null
    var onPageFinishedCoroutineJob: Job? = null
    var registerUrlHistoryTitleCoroutineJob: Job? = null
    var onWebHistoryUpdaterJob: Job? = null
    var previousTerminalTag: String? = null
    private var outputFileLength: Int = 0
    var terminalOn = CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
    var firstDisplayUpdate = true
    var onHistoryUrlTitle = CommandClickScriptVariable.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
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
    var currentAppDirPath = UsePath.cmdclickDefaultAppDirPath
    var currentScriptName = String()
    var runShell = "bash"
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
    var dialogInstance: Dialog? = null


    private var broadcastReceiverForUrl: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            BroadcastHtmlReceiveHandler.handle(
                this@TerminalFragment,
                intent,
            )
        }
    }

    private var broadcastReceiverForMonitorText: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            MonitorTextLauncher.handle(
                this@TerminalFragment,
                intent,
            )
        }
    }

    var broadcastReceiverForHtml: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            HtmlLauncher.launch(
                intent,
                context,
                this@TerminalFragment,
            )
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val terminalViewModel: TerminalViewModel by activityViewModels()
        terminalViewModel.readlinesNum = ReadLines.SHORTH

        outputFileLength = savedInstanceState?.getInt("outputFileLength")
            ?:arguments?.getInt("outputFileLength") ?: 0
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.terminal_fragment,
            container,
        false
        )

        if(savedInstanceState!=null) {
            binding.terminalWebView.restoreState(savedInstanceState)
        }

        ExecDownLoadManager.set(
            this,
            binding.terminalWebView
        )
        ToolbarHideShowWhenTermLongAndScrollSave.invoke(
            this,
            terminalViewModel,
        )

        ConfigFromStartUpFileSetterForTerm.set(this)
        MonitorFileManager.switchCurMonitorFile(
            this,
            terminalViewModel
        )
        UrlHistoryBackUp.backup(this)
        ScrollYPosiBackUp.backup(this)
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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        TerminalOnHandlerForEdit.handle(this)
    }


    override fun onPause() {
        super.onPause()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        terminalViewModel.isStop = true
        alertDialogInstance?.dismiss()
        dialogInstance?.dismiss()
        terminalViewModel.onDialog = false
        val terminalWebView = binding.terminalWebView
        terminalWebView.stopLoading()
        terminalWebView.removeAllViews()
        activity?.intent?.action = String()
        BroadcastManager.unregisterBroadcastReceiverForTerm(
            this,
            broadcastReceiverForUrl
        )
        BroadcastManager.unregisterBroadcastReceiverForTerm(
            this,
            broadcastReceiverForHtml
        )
        BroadcastManager.unregisterBroadcastReceiverForTerm(
            this,
            broadcastReceiverForMonitorText
        )
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
        firstDisplayUpdate = if(
            !firstDisplayUpdate
            && terminalViewModel.readlinesNum != ReadLines.LONGTH
        ){
            onTermBackendWhenStart == SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name
        } else firstDisplayUpdate
        InitCurrentMonitorFile.trim(this)
        terminalViewModel.isStop = false
        alertDialogInstance?.dismiss()
        dialogInstance?.dismiss()
        terminalViewModel.onDialog = false
        binding.terminalWebView.onResume()
        activity?.setVolumeControlStream(AudioManager.STREAM_MUSIC)
        IntentAction.handle(this)
        displayUpdateCoroutineJob?.cancel()
        displayUpdateCoroutineJob = DisplaySwitch.update(
            this,
            terminalViewModel,
        )
        BroadcastManager.registerBroadcastReceiver(
            this,
            broadcastReceiverForUrl,
            BroadCastIntentScheme.ULR_LAUNCH.action
        )
        BroadcastManager.registerBroadcastReceiver(
            this,
            broadcastReceiverForHtml,
            BroadCastIntentScheme.HTML_LAUNCH.action
        )
        BroadcastManager.registerBroadcastReceiver(
            this,
            broadcastReceiverForMonitorText,
            BroadCastIntentScheme.MONITOR_TEXT_PATH.action
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
            changeTargetFragmentSelects: ChangeTargetFragment?,
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
            changeTargetFragment: ChangeTargetFragment?
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
}