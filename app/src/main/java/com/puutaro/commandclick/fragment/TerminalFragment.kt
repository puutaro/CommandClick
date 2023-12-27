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
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.InitCurrentMonitorFile
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.TerminalOnHandlerForEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.ChangeTargetFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.TargetFragmentInstance
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
    var goBackFlag = false


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
        BroadcastRegister.unregisterBroadcastReceiverForTerm(
            this,
            broadcastReceiverForTerm
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
        BroadcastRegister.registerBroadcastReceiverMultiActions(
            this,
            broadcastReceiverForTerm,
            listOf(
                BroadCastIntentSchemeTerm.HTML_LAUNCH.action,
                BroadCastIntentSchemeTerm.ULR_LAUNCH.action,
                BroadCastIntentSchemeTerm.MONITOR_TEXT_PATH.action,
                BroadCastIntentSchemeTerm.MONITOR_MANAGER.action,
            )
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

    interface OnAdBlockListener {
        fun exeOnAdblock()
    }

    interface OnGetPermissionListenerForTerm {
        fun onGetPermission(
            permissionStr: String
        )
    }
}