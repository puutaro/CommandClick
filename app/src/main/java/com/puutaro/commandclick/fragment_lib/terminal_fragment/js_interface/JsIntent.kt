package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.EditSiteBroadCast
import com.puutaro.commandclick.util.Intent.IntentLauncher
import com.puutaro.commandclick.util.Intent.IntentVariant
import java.io.File


class JsIntent(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
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
        currentScriptFileName: String,
        currentFannelState: String,
    ){
        val execIntent = Intent(terminalFragment.activity, MainActivity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

//        execIntent.putExtra(
//            FannelInfoSetting.current_app_dir.name,
//            currentAppDirPath
//        )
        execIntent.putExtra(
            FannelInfoSetting.current_fannel_name.name,
            currentScriptFileName
        )
        execIntent.putExtra(
            FannelInfoSetting.current_fannel_state.name,
            currentFannelState
        )
        terminalFragment.activity?.startActivity(execIntent)
    }

    @JavascriptInterface
    fun shareImage(
        imageFilePath: String,
    ){
        /*
        Launch share image intent
        */
        val imageFilePathObj = File(imageFilePath)
        if(
            !imageFilePathObj.isFile
        ) {
            ToastUtils.showLong("no exist\n ${imageFilePath}")
        }
        IntentVariant.sharePngImage(
            imageFilePathObj,
            context,
        )
    }
}
