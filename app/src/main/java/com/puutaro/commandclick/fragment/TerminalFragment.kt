package com.puutaro.commandclick.fragment

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
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.proccess.broadcast.BroadcastManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.AdBlocker
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.HtmlLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Job


class TerminalFragment: Fragment() {


    private var _binding: TerminalFragmentBinding? = null
    val binding get() = _binding!!
    val terminalViewhandler : Handler = Handler(Looper.getMainLooper())
    var displayUpdateCoroutineJob: Job? = null
    var loadAssetCoroutineJob: Job? = null
    var onPageFinishedCoroutineJob: Job? = null
    var registerUrlHistoryTitleCoroutineJob: Job? = null
    var onWebHistoryUpdaterJob: Job? = null
    var firstDisplayUpdateRunner: Runnable? = null
    var lastDisplayUpdateRunner: Runnable? = null
    private var outputFileLength: Int = 0
    var firstDisplayUpdate = true
    var onHistoryUrlTitle = CommandClickScriptVariable.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
    var onAdBlock = CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE
    var fontZoomPercent = CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalColor = CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
    var terminalFontColor = CommandClickScriptVariable.TERMINAL_FONT_COLOR_DEFAULT_VALUE
    var currentUrl: String? = null
    var currentAppDirPath = UsePath.cmdclickDefaultAppDirPath
    var runShell = "bash"
    var onUrlHistoryRegister = CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE
    val trimLastLine = 500
    var rowsMap: MutableMap<String, List<List<String>>> = mutableMapOf()
    var headerMap: MutableMap<String, List<String>> = mutableMapOf()


    var broadcastReceiverForUrl: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val urlStr = intent.getStringExtra(
                BroadCastIntentScheme.ULR_LAUNCH.scheme
            ) ?: return
            if(
                !LoadUrlPrefixSuffix.judge(urlStr)
            ) return
            binding.terminalWebView.loadUrl(urlStr)
        }
    }

    var broadcastReceiverForHtml: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            HtmlLauncher.launch(
                intent,
                context,
                binding,
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


        ExecDownLoadManager.set(this)
        ToolbarHideShowWhenTermLong.invoke(
            this,
            terminalViewModel,
        )

        ConfigFromStartUpFileSetterForTerm.set(this)
        UrlHistoryBackUp.backup(this)
        AdBlocker.init(this)

        WebChromeClientSetter.set(this)
        FindListenerSetter.set(this)

        WebViewClientSetter.set(this@TerminalFragment)

        WebViewSettings.set(this)
        LongClickJsMaker.make(this)
        ImageOnLongClickListener.set(this)
        if(
            IntentAction.judge(this.activity)
        ){
            val urlString = activity?.intent?.dataString
            urlString?.let {
                firstDisplayUpdate = false
                val webView = this.binding.terminalWebView
                webView.loadUrl(it);
            }
        }
        DisplaySwich.update(
            this,
            terminalViewModel,
        )
        return binding.root
    }


    override fun onPause() {
        super.onPause()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        terminalViewModel.isStop = true
        val terminalWebView = binding.terminalWebView
        terminalWebView.stopLoading()
        terminalWebView.removeAllViews()
        activity?.intent?.action = String()
        BroadcastManager.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForUrl
        )
        BroadcastManager.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForHtml
        )
        binding.terminalWebView.onPause()
        loadAssetCoroutineJob?.cancel()
        onPageFinishedCoroutineJob?.cancel()
        registerUrlHistoryTitleCoroutineJob?.cancel()
        onWebHistoryUpdaterJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        terminalViewModel.isStop = false
        binding.terminalWebView.onResume()
        activity?.setVolumeControlStream(AudioManager.STREAM_MUSIC)
        InitCurrentMonitorFile.trim(this)
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
    }

    interface OnMultiSelectListenerForTerm {
        fun onMultiSelectForTerm(
            title: String,
            updatedMultiModelArray: ArrayList<MultiSelectModel>,
            preSelectedMultiModelArray: ArrayList<Int>
        )
    }


}