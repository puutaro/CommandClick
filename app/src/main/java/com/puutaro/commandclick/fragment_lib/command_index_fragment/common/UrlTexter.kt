package com.puutaro.commandclick.fragment_lib.command_index_fragment.common

import android.content.Context
import android.widget.AutoCompleteTextView
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment

class UrlTexter {
    companion object {
        fun launch(
            context: Context?,
            cmdSearchEditText: AutoCompleteTextView,
            inputUrl: String?
        ){
            if(inputUrl.isNullOrEmpty()) return
            val queryUrl = WebUrlVariables.queryUrl
            val enableStartHttp = inputUrl.startsWith("http") == true
            val searchUrl = if(
                enableStartHttp
            ) inputUrl
            else queryUrl + inputUrl
            if(
                enableStartHttp
            ) cmdSearchEditText.setText(inputUrl)
            else cmdSearchEditText.setText(inputUrl)
            val listener = context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
            listener?.onLaunchUrlByWebView(
                searchUrl,
            )
        }
    }
}