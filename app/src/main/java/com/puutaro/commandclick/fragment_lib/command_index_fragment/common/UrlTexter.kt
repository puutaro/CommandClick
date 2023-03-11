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
            val queryUrl = WebUrlVariables.queryUrl
            val searchUrl = if(
                inputUrl?.startsWith("http") == true
            ) inputUrl
            else {
                queryUrl + inputUrl
            }
            cmdSearchEditText.setText(searchUrl)
            val listener = context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
            listener?.onLaunchUrlByWebView(
                searchUrl,
            )
        }
    }
}