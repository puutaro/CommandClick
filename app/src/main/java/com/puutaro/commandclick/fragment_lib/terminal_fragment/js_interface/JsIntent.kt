package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForFzHtml
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.EditSiteBroadCast
import com.puutaro.commandclick.util.Intent.IntentLauncher
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


class JsIntent(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private val editSiteBroadCast = EditSiteBroadCast(terminalFragment)


    @JavascriptInterface
    fun launchEditSite(
        editPath: String,
        extraMapStr: String,
        filterCode: String,
    ) {
        editSiteBroadCast.send(
            editPath,
            extraMapStr,
            filterCode
        )
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
        jsIntent.action = BroadCastIntentSchemeTerm.FZHTML_LAUNCH.action
        jsIntent.putExtra(
            BroadCastIntentSchemeTerm.FZHTML_LAUNCH.scheme,
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
        extraListStrTabSepa: String,
        extraListIntTabSepa: String,
        extraListLongTabSepa: String,
        extraListFloatTabSepa: String,
    ){
        IntentLauncher.send(
            activity,
            action,
            uriString,
            extraListStrTabSepa,
            extraListIntTabSepa,
            extraListLongTabSepa,
            extraListFloatTabSepa,
        )
    }

    @JavascriptInterface
    fun launchShortcut(
        currentAppDirPath: String,
        currentScriptFileName: String
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
            SharePrefferenceSetting.current_fannel_name.name,
            currentScriptFileName
        )
        terminalFragment.activity?.startActivity(execIntent)
    }

    @JavascriptInterface
    fun shareImage(
        imageFilePath: String,
    ){
        val imageFilePathObj = File(imageFilePath)
        if(
            !imageFilePathObj.isFile
        ) {
            Toast.makeText(
                context,
                "no exist\n ${imageFilePath}",
                Toast.LENGTH_LONG
            ).show()
        }
        IntentVariant.sharePngImage(
            imageFilePathObj,
            activity
        )
    }
}
