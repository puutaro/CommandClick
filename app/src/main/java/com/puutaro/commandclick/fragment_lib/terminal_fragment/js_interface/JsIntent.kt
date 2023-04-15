package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForFzHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.IntentExtra
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
        onSortableJs: String = "true",
        onClickUrl: String = "true",
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
            BroadCastIntentExtraForHtml.ON_SORTABLE_JS.scheme,
            onSortableJs
        )
        jsIntent.putExtra(
            BroadCastIntentExtraForHtml.ON_CLICK_URL.scheme,
            onClickUrl
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
        extraFloat: String,
    ){

        val intent = Intent()
        if(
            action.isNotEmpty()
        ) intent.action = Intent.ACTION_INSERT

        val eventUri = Uri.parse(uriString)
        if(
            uriString.isNotEmpty()
        ) intent.data = eventUri
        IntentExtra.add(
            intent,
            extraString,
            IntentExtra.ConvertNumberType.String
        )
        IntentExtra.add(
            intent,
            extraInt,
            IntentExtra.ConvertNumberType.Int
        )
        IntentExtra.add(
            intent,
            extraLong,
            IntentExtra.ConvertNumberType.Long
        )
        IntentExtra.add(
            intent,
            extraFloat,
            IntentExtra.ConvertNumberType.Float
        )
        terminalFragment.activity?.startActivity(intent)
    }

    @JavascriptInterface
    fun launchShortcut(
        currentAppDirPath: String,
        currentShellFileName: String
    ){
        val execIntent = Intent(terminalFragment.activity, MainActivity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        execIntent.putExtra(
            SharePrefferenceSetting.current_app_dir.name,
            currentAppDirPath
        )
        execIntent.putExtra(
            SharePrefferenceSetting.current_script_file_name.name,
            currentShellFileName
        )
        terminalFragment.activity?.startActivity(execIntent)
    }
}
