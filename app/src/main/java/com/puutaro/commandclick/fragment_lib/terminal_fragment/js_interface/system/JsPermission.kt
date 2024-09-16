package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.content.pm.PackageManager
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class JsPermission(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun isExist(
        permissionTypeStr: String,
    ): Boolean {
        val terminalFragment = terminalFragmentRef.get()
            ?: return false
        val context = terminalFragment.context ?: return false
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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val context = terminalFragment.context
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
                val jsConList =
                    ReadText(loadJsPath).textToList()
                val jsCon = JavaScriptLoadUrl.make(
                    context,
                    loadJsPath,
                    jsConList,
                ) ?: return@withContext
                if (jsCon.isEmpty()) return@withContext
                JsUrl(terminalFragmentRef).loadUrl(jsCon)
            }
        }
    }

}
