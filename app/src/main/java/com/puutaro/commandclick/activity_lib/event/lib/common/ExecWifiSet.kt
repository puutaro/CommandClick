package com.puutaro.commandclick.activity_lib.event.lib.common

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.LocationSetter
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object ExecWifiSet {

    fun set(
        activity: MainActivity,
        ssid: String,
        pin: String,
    ){
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)
        terminalViewModel.onPermDialog = true
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                LocationSetter.getPermissionAndSet(activity)
            }
            withContext(Dispatchers.IO){
                for(i in 1..100){
                    if(!terminalViewModel.onPermDialog) break
                    delay(100)
                }
            }
            withContext(Dispatchers.Main){
                WifiUtils.withContext(activity).enableWifi()
            }
            val isLocationPermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if(!isLocationPermission) return@launch
            withContext(Dispatchers.Main){
                WifiUtils.withContext(activity)
                    .connectWithScanResult(
                        pin
                    ) { scanResults: List<ScanResult?> ->
                        scanResults[0]
                    }
                    .onConnectionResult(object : ConnectionSuccessListener {
                        override fun success() {
                            ToastUtils.showShort("Connect ok")
                        }

                        override fun failed(errorCode: ConnectionErrorCode) {
                            ToastUtils.showShort("Fail! location permission ok? $errorCode")
                        }
                    })
                    .start()
                WifiUtils.withContext(activity)
                    .connectWith(
                        ssid,
                        pin
                    )
                    .setTimeout(50000)
                    .onConnectionResult(object : ConnectionSuccessListener {
                        override fun success() {
                            ToastUtils.showShort("Connect ok")
                        }

                        override fun failed(errorCode: ConnectionErrorCode) {
                            ToastUtils.showShort("Fail! location permission ok? $errorCode")
                        }
                    })
                    .start()
            }

        }
    }

}