package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.lang.ref.WeakReference


class ImplicitIntentStarter(private val wContext: Context?) {

    fun startActivity(intent: Intent, uri: Uri) {
        val context = wContext ?: return
        val packageManager = context.packageManager

        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent)
            return
        }

        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
        if (fallbackUrl != null) {
            // ブラウザ起動など
            return
        }
        startGooglePlayStoreActivity(uri.toString())
    }

    private fun startGooglePlayStoreActivity(uriSource: String) {
        val context = wContext?: return
        val packageManager = context.packageManager
        val marketPrefix = "market://details?id="
        val uri = if(uriSource.startsWith(marketPrefix)){
            uriSource
        } else {
            "${marketPrefix}${uriSource}"
        }
        val marketIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri))
        if (marketIntent.resolveActivity(packageManager) != null) {
            context.startActivity(marketIntent)
            return
        }
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${uri}")))
    }
}

// WeakReferenceから取得する値を null safe に扱うための拡張です。
fun <T> WeakReference<T>.getNullable(): T? = get()