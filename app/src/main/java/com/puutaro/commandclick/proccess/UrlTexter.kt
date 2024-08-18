package com.puutaro.commandclick.proccess

import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import java.net.URLEncoder

object UrlTexter {
    fun launch(
        fragment: Fragment,
        cmdSearchEditText: AutoCompleteTextView? = null,
        inputUrl: String?
    ){
        if(
            inputUrl.isNullOrEmpty()
        ) return
        val enableStartHttp = inputUrl.startsWith("http")
        val searchUrl = makeSearchUrl(
            inputUrl,
            enableStartHttp,
        )
        if(
            enableStartHttp
            && cmdSearchEditText != null
        ) cmdSearchEditText.setText(inputUrl)
        when(fragment) {
            is CommandIndexFragment -> {
                val listener = fragment.context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
                listener?.onLaunchUrlByWebView(
                    searchUrl,
                )
            }
            is EditFragment -> {
                val listener = fragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listener?.onLaunchUrlByWebViewForEdit(
                    searchUrl,
                )
            }
        }
    }

    private fun makeSearchUrl(
        inputUrl: String,
        enableStartHttp: Boolean,
    ): String {
        val queryUrl = WebUrlVariables.queryUrl
        return when(enableStartHttp) {
            true -> inputUrl
            else -> queryUrl+URLEncoder.encode(inputUrl, "UTF-8")
        }
    }
}