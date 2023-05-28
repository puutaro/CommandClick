package com.puutaro.commandclick.activity_lib.event

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.common.variable.PageSearchToolbarButtonVariant

class PageSearchToolbarHandler {
    companion object {
        fun handle(
            activity: MainActivity,
            pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant,
            tag: String?,
            searchText: String,
        ){
            val terminalFragment = try {
                activity.supportFragmentManager.findFragmentByTag(
                    tag
                ) as TerminalFragment
            } catch (e: Exception){
                return
            }
            val webView = terminalFragment.binding.terminalWebView
            if(!webView.isVisible) return
            when(pageSearchToolbarButtonVariant){
                PageSearchToolbarButtonVariant.SEARCH_TEXT -> {
                    webView.findAllAsync(searchText.trim())
                }
                PageSearchToolbarButtonVariant.TOP -> {
                    webView.findNext(false)
                }
                PageSearchToolbarButtonVariant.DOWN -> {
                    webView.findNext(true)
                }
                PageSearchToolbarButtonVariant.CANCEL -> {}
            }
        }
    }
}