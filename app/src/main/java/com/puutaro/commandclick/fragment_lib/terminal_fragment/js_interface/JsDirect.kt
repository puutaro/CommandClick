package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.common.variable.intent.extra.FileDownloadExtra
import com.puutaro.commandclick.common.variable.intent.extra.FileUploadExtra
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.FileDownloadService
import com.puutaro.commandclick.service.FileUploadService

class JsDirect (
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val fileDownloadService = FileDownloadService::class.java
    private val fileUploadService = FileUploadService::class.java
    @JavascriptInterface
    fun launchCopyFannelServer(){
        val intent = Intent(
            context,
            fileUploadService
        )
        intent.putExtra(
            FileUploadExtra.CURRENT_APP_DIR_PATH_FOR_FILE_UPLOAD.schema,
            terminalFragment.currentAppDirPath
        )
        context?.let {
            ContextCompat.startForegroundService(context, intent)
        }
    }

    @JavascriptInterface
    fun exitCopyFannelServer(){
        val intent = Intent(
            context,
            fileUploadService
        )
        context?.stopService(intent)
    }

    @JavascriptInterface
    fun get(
        mainUrl: String,
        fullPathOrFannelRawName: String,
    ){
        val intent = Intent(
            context,
            fileDownloadService
        )
        intent.putExtra(
            FileDownloadExtra.MAIN_URL.schema,
            mainUrl
        )
        intent.putExtra(
            FileDownloadExtra.FULL_PATH_OR_FANNEL_RAW_NAME.schema,
            fullPathOrFannelRawName
        )
        intent.putExtra(
            FileDownloadExtra.CURRENT_APP_DIR_PATH_FOR_TRANSFER.schema,
            terminalFragment.currentAppDirPath
        )
        context?.let {
            ContextCompat.startForegroundService(context, intent)
        }
    }
}