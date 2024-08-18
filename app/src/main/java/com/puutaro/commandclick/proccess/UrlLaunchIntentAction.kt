package com.puutaro.commandclick.proccess

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.StartupHandler
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object UrlLaunchIntentAction {

//    fun handle(
//        terminalFragment: TerminalFragment,
//    ) {
//        val activity = terminalFragment.activity
//        val webView = terminalFragment.binding.terminalWebView
//        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
//        val intent = activity?.intent
//        val intentAction =
//            activity?.intent?.action
//        when (intentAction) {
//            Intent.ACTION_VIEW,
//            Intent.ACTION_MAIN -> {
//                if (
//                    !intent?.dataString.isNullOrEmpty()
//                ) {
//                    val urlString = activity.intent?.dataString
//                    urlString?.let {
////                        ToastUtils.showShort("urlString: ${urlString}")
//                        terminalFragment.firstDisplayUpdate = false
//                        terminalViewModel.launchUrl = urlString
////                    webView.loadUrl(it)
//                    }
//                    return
//                }
//            }
//            Intent.ACTION_WEB_SEARCH -> {
//                val query = intent
//                    ?.extras
//                    ?.getString(
//                        SearchManager.QUERY,
//                        null
//                    ) ?: return
//                terminalFragment.firstDisplayUpdate = false
////                ToastUtils.showShort("query: ${query}")
//                terminalViewModel.launchUrl = "${WebUrlVariables.queryUrl}${query}"
////                webView.loadUrl("${WebUrlVariables.queryUrl}${query}")
//                return
//            }
//            else -> {}
//        }
//        if(
//            !webView.url.isNullOrEmpty()
//        ) return
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.IO) {
//                for (i in 1..100) {
//                    if(
//                        !terminalFragment.firstDisplayUpdate
//                    ) break
//                    delay(50)
//                }
//            }
//            withContext(Dispatchers.Main) {
//                StartupHandler.invoke(
//                    terminalFragment,
//                )
//            }
//        }
//    }

    fun judge(
        activity: Activity?,
    ): Boolean {
        val intent = activity?.intent
        val intentAction =
            activity?.intent?.action
        when (
            intentAction
        ) {
            Intent.ACTION_VIEW,
            Intent.ACTION_MAIN -> {
                return !intent?.dataString.isNullOrEmpty()
            }

            Intent.ACTION_WEB_SEARCH -> {
                val query = intent
                    ?.extras
                    ?: return false
                return !query.isEmpty
            }
            else -> {
                return false
            }
        }
    }
}