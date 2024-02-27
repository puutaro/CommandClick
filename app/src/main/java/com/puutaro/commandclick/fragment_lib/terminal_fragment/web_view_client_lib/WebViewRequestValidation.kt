package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.content.Intent
import android.net.Uri


class WebViewRequestValidation {
    fun isTransitionToOtherApp(url: Uri): TransitionToOtherApp {
        return when (url.scheme) {
            AppIntentType.TEL.str ->
                TransitionToOtherApp.Yes(
                    Intent(Intent.ACTION_DIAL, url)
                )
            AppIntentType.MAILTO.str ->
                TransitionToOtherApp.Yes(
                    Intent(Intent.ACTION_SENDTO, url)
                )
            AppIntentType.INTENT.str,
            AppIntentType.MARKET.str ->
                TransitionToOtherApp.Yes(
                    Intent.parseUri(
                        url.toString(),
                        Intent.URI_INTENT_SCHEME
                    )
                )
            else -> TransitionToOtherApp.No()
        }
    }
    sealed class TransitionToOtherApp {
        class Yes(val intent: Intent):TransitionToOtherApp()
        class No():TransitionToOtherApp()
    }
}

enum class AppIntentType(
    val str: String
){
    TEL("tel"),
    MAILTO("mailto"),
    INTENT("intent"),
    MARKET("market")
}