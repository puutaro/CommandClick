package com.puutaro.commandclick.util

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
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


    fun getWifiWpaSsidAndPinPair(
        scanStr: String
    ): Pair<String?, String?> {
        val wifiNetowrkRegex = "T:[^;]+;S:([^;]+);P:([^;]+).*".toRegex()
        return scanStr.split(":").filterIndexed {
            index, _ -> index > 0
        }.joinToString(":").replace(
            wifiNetowrkRegex,
            "$1\t$2"
        ).split("\t").let {
            it.firstOrNull() to it.lastOrNull()
        }
    }

    fun getSmsNumAndBody(
        scanStr: String
    ): Pair<String?, String?> {
        return scanStr.split(":").filterIndexed{
                index, _ -> index > 0
        }.let {
            it.firstOrNull() to it.getOrNull(1)
        }
    }

    fun makeGmailMap(
        scanStr: String
    ): Map<String, String?>? {
        val excludeMailPrefixStr =
            scanStr
                .replaceFirst(Regex(";(SUB):|;(BODY):"), "?$1=")
                .replaceFirst("?SUB=", "?${GmailKey.SUBJECT.key}=")
                .replaceFirst("?BODY=", "&${GmailKey.BODY.key}=")
                .replaceFirst(";SUB:", "&${GmailKey.SUBJECT.key}=")
                .replaceFirst(";BODY:", "&${GmailKey.BODY.key}=")
                .trim()
                .replaceFirst(Regex(";+$"), "")
                .split(":")
                .filterIndexed { index, _ ->
            index > 0
        }.joinToString(":")
        val addAndExtraList = excludeMailPrefixStr.split("?")
        val mailAd = addAndExtraList.firstOrNull()?.trim()?.removePrefix("TO:")
            ?: return null
        val mailAdMap = mapOf(
            GmailKey.MAIL_AD.key to mailAd
        )
        val subjectBodyMap = addAndExtraList.getOrNull(1)?.split("&")?.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }?.toMap()
        if (
            subjectBodyMap.isNullOrEmpty()
        ) return mailAdMap
        return mailAdMap + subjectBodyMap
    }

    fun extractCopyPath(
        cpQrString: String
    ): Pair<String, String>? {
        val httpPrefix = WebUrlVariables.httpPrefix
        val cpFilePrefix = QrLaunchType.CpFile.prefix
        val urlAndFilePath =
            cpQrString
                .trim()
                .removePrefix(cpFilePrefix)
                .trim()
                .split(";")
        val url =
            urlAndFilePath.firstOrNull()?.trim()?.let {
                if(
                    it.startsWith(WebUrlVariables.httpPrefix)
                    || it.startsWith(WebUrlVariables.httpsPrefix)
                ) return@let it
                "${httpPrefix}${it}"
            } ?: return null
        val filePath =
            urlAndFilePath.getOrNull(1)?.trim()
            ?: return null
        return url to filePath
    }
}

enum class GmailKey(
    val key: String
){
    MAIL_AD("mail_ad"),
    SUBJECT("subject"),
    BODY("body"),
}
