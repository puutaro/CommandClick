package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForFzHtml
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
    fun launchFzSite(
        editPath: String,
        promptMessage: String,
        itemClickJs: String,
        itemLongClickJs: String,
        realTimeListSetJs: String
    ) {
        val jsIntent = Intent()
        jsIntent.action = BroadCastIntentScheme.FZHTML_LAUNCH.action
        jsIntent.putExtra(
            BroadCastIntentScheme.FZHTML_LAUNCH.scheme,
            editPath
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForFzHtml.TEMPLATE_PROMPT_MESSAGE.scheme,
            promptMessage
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForFzHtml.ITEM_CLICK_JAVASCRIPT.scheme,
            itemClickJs
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForFzHtml.ITEM_LONG_CLICK_JAVASCRIPT.scheme,
            itemLongClickJs
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForFzHtml.REAL_TIME_LIST_SET_JAVASCRIPT.scheme,
            realTimeListSetJs
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

    @JavascriptInterface
    fun launchApp(
        action: String,
        uriString: String,
        extraString: String,
        extraInt: String,
        extraLong: String,
    ){

        val intent = Intent()
        if(
            action.isNotEmpty()
        ) intent.action = Intent.ACTION_INSERT

        val eventUri = Uri.parse(uriString)
        if(
            uriString.isNotEmpty()
        ) intent.data = eventUri
        val extraStringList = extraString.split("\t")
        extraStringList.forEach {
            if(!extraString.contains("\t")) return@forEach
            val currentKeyValue = it.split("=")
            val key = currentKeyValue.firstOrNull() ?: return@forEach
            val value = currentKeyValue.lastOrNull() ?: return@forEach
            intent.putExtra(key, value)
        }
        val extraIntList = extraInt.split("\t")
        extraIntList.forEach {
            if(!extraInt.contains("\t")) return@forEach
            val currentKeyValue = it.split("=")
            val key = currentKeyValue.firstOrNull() ?: return@forEach
            val value = currentKeyValue.lastOrNull()?.toInt() ?: return@forEach
            intent.putExtra(key, value)
        }
        val extraLongList = extraLong.split("\t")
        extraLongList.forEach {
            if(!extraLong.contains("\t")) return@forEach
            val currentKeyValue = it.split("=")
            val key = currentKeyValue.firstOrNull() ?: return@forEach
            val value = currentKeyValue.lastOrNull()?.toLong() ?: return@forEach
            intent.putExtra(key, value)
        }
        terminalFragment.activity?.startActivity(intent)
    }
}