package com.puutaro.commandclick.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import android.webkit.WebStorage
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment_lib.terminal_fragment.*
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Job


class TerminalFragment: Fragment() {


    private var _binding: TerminalFragmentBinding? = null
    val binding get() = _binding!!
    val terminalViewhandler : Handler = Handler(Looper.getMainLooper())
    var jobCoroutine: Job? = null
    var firstDisplayUpdateRunner: Runnable? = null
    var lastDisplayUpdateRunner: Runnable? = null
    private var outputFileLength: Int = 0
    var firstDisplayUpdate = true
    var onHistoryUrlTitle = CommandClickShellScript.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
    var fontZoomPercent = CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalColor = CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
    var terminalFontColor = CommandClickShellScript.TERMINAL_FONT_COLOR_DEFAULT_VALUE
    var currentUrl: String? = null


    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val urlStr = intent.getStringExtra(
                BroadCastIntentScheme.ULR_LAUNCH.scheme
            )
            urlStr?.let {
                binding.terminalWebView.loadUrl(it)
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        WebStorage.getInstance().deleteAllData()
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
            IntentAction.judge(this)
        ){
            val urlString = activity?.intent?.dataString
            urlString?.let {
                firstDisplayUpdate = false
                val webView = this.binding.terminalWebView
                webView.loadUrl(it);
            }
        }

        jobCoroutine = DisplaySwich.update(
            this,
            terminalViewModel
        )

        return binding.root
    }



    override fun onPause() {
        super.onPause()
        BroadcastManager.unregisterBloadcastReciever(this)
        binding.terminalWebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.terminalWebView.onResume()
        activity?.setVolumeControlStream(AudioManager.STREAM_MUSIC)
        InitCurrentMonitorFile.trim(this)
        BroadcastManager.registerBloadcastReciever(this)
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
        WebStorage.getInstance().deleteAllData()
        _binding = null
    }


}