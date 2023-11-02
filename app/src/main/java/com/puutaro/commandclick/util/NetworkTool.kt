package com.puutaro.commandclick.util

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.Inet4Address

object NetworkTool {

    fun getIpv4Address(
        context: Context?
    ): String {
        var isBreakLoop = false
        var ipAd = String()
        runBlocking {
            val manager: ConnectivityManager =
                context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    super.onLinkPropertiesChanged(network, linkProperties)
                    ipAd = linkProperties.linkAddresses.filter {
                        it.address is Inet4Address
                    }[0].toString().split("/").firstOrNull() ?: String()
                    isBreakLoop = true
                }
            }
            manager.registerDefaultNetworkCallback(networkCallback)
            for(i in 1..100){
                if(
                    isBreakLoop
                ) break
                delay(100)
            }
        }
        return ipAd
    }

    fun isWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        if (
            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        ) return true
        return false
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        if (
            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            )
        ) return true
        else if (
            capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        ) return true
        else if (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        ) return true
        return false
    }
}
