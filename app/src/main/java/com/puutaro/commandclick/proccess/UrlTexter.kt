package com.puutaro.commandclick.proccess

import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment

object UrlTexter {
    fun launch(
        fragment: Fragment,
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
}