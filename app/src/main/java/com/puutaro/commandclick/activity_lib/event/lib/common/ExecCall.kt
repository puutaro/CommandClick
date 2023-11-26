package com.puutaro.commandclick.activity_lib.event.lib.common

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.CallSetter
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecCall {
    fun call(
        activity: MainActivity,
        telString: String,
    ){
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)
        terminalViewModel.onPermDialog = true
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                CallSetter.getPermissionAndSet(activity)
            }
            withContext(Dispatchers.IO) {
                for (i in 1..100) {
                    if (!terminalViewModel.onPermDialog) break
                    delay(100)
                }
            }
            val isCallPermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
            if(!isCallPermission) return@launch
            withContext(Dispatchers.IO){
                try {
                    val intent = Intent(
                        Intent.ACTION_CALL,
                        Uri.parse(telString)
                    )
                    activity.startActivity(intent)
                }catch (e: Exception){
                    LogSystems.stdErr(e.toString())
                }
            }
        }
    }
}