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
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.manager.BroadcastManager
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
    var firstDisplayUpdateRunner: Runnable? = null
    var lastDisplayUpdateRunner: Runnable? = null
    private var outputFileLength: Int = 0
    var firstDisplayUpdate = true
    var onHistoryUrlTitle = CommandClickShellScript.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
    var onAdBlock = CommandClickShellScript.ON_ADBLOCK_DEFAULT_VALUE
    var fontZoomPercent = CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalColor = CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
    var terminalFontColor = CommandClickShellScript.TERMINAL_FONT_COLOR_DEFAULT_VALUE
    var currentUrl: String? = null
    var currentAppDirPath = UsePath.cmdclickDefaultAppDirPath
    var blocklist = hashSetOf<String>()
    var onWebHistoryUpdaterJob: Job? = null
    var runShell = "bash"


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
                currentAppDirPath
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
        AdBlocker.init(this)

        if(savedInstanceState!=null) {
            binding.terminalWebView.restoreState(savedInstanceState)
        }


        ExecDownLoadManager.set(this)
        ImageOnLongClickListner.set(this)
        ToolbarHideShowWhenTermLong.invoke(
            this,
            terminalViewModel,
        )

        ConfigFromStartUpFileSetterForTerm.set(this)

        WebChromeClientSetter.set(this)
        FindListenerSetter.set(this)

        WebViewClientSetter.set(this@TerminalFragment)

        WebViewSettings.set(this)

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
    }

    override fun onResume() {
        super.onResume()
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
        fun onFileCooose(
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: WebChromeClient.FileChooserParams?
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        this.loadAssetCoroutineJob?.cancel()
        this.onPageFinishedCoroutineJob?.cancel()
        this.registerUrlHistoryTitleCoroutineJob?.cancel()
        this.displayUpdateCoroutineJob?.cancel()
        this.onWebHistoryUpdaterJob?.cancel()
        _binding = null
    }


}