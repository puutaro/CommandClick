package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class JsIntent(
    private val terminalFragment: TerminalFragment
) {
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()


    @JavascriptInterface
    fun launchEditSite(
        editPath: String,
        srcPath: String? = null,
        onClickSort: String = "true",
        filterCode: String? = null
    ) {
        val jsIntent = Intent()
        jsIntent.action = BroadCastIntentScheme.HTML_LAUNCH.action
        jsIntent.putExtra(
            BroadCastIntentScheme.HTML_LAUNCH.scheme,
            editPath
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForHtml.SCR_PATH.scheme,
            srcPath
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForHtml.ON_CLICK_SORT.scheme,
            onClickSort
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForHtml.FILTER_CODE.scheme,
            filterCode
        )
        terminalFragment.activity?.sendBroadcast(jsIntent)
    }


    @JavascriptInterface
    fun launchUrl(
        currentPageUrl: String
    ){
        val openUrlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(currentPageUrl)
        )
        terminalFragment.startActivity(openUrlIntent)
    }
}