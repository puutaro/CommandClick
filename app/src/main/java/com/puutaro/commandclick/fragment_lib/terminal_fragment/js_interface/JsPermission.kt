package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.pm.PackageManager
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsPermission(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()


    @JavascriptInterface
    fun isExist(
        permissionTypeStr: String,
    ): Boolean {
        if(
            context == null
        ) return false
        val postNotifications = permissionTypeStr
        return ContextCompat.checkSelfPermission(
            context,
            postNotifications
        ) == PackageManager.PERMISSION_GRANTED
    }

    @JavascriptInterface
    fun get(
        permissionTypeStr: String,
        loadJsPath: String,
    ){
        terminalViewModel.onPermDialog = true
        val listener =
            context as? TerminalFragment.OnGetPermissionListenerForTerm
        listener?.onGetPermission(permissionTypeStr)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                for (i in 1..100) {
                    if (!terminalViewModel.onPermDialog) break
                    delay(100)
                }
            }
            withContext(Dispatchers.IO) {
                val jsCon = JavaScriptLoadUrl.make(
                    context,
                    loadJsPath
                ) ?: return@withContext
                if (jsCon.isEmpty()) return@withContext
                JsUrl(terminalFragment).loadUrl(jsCon)
            }
        }
    }

}
