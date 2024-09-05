package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SearchButtonClickListener {

    fun handle (
        activity: MainActivity,
        isFromTerminal: Boolean,
        ) {
        val cmdIndexFragment = TargetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
//        val cmdindexSearchLinearLayout = cmdIndexFragment.binding.cmdindexSearchLinearLayout
        val searchLinearButton = when(isFromTerminal){
            true -> TargetFragmentInstance
                .getCurrentTerminalFragment(activity)
                ?.binding
                ?.termSearchButton
            else -> cmdIndexFragment.binding.cmdindexSearchButton
        } ?: return
        searchLinearButton.setOnClickListener{
            val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
                activity
            ) ?: return@setOnClickListener
            val terminalWebView =
                terminalFragment.binding.terminalWebView
            val isGgleSearchUrl =
                terminalWebView.url?.startsWith(
                    WebUrlVariables.queryUrlBase
                ) == true
            if (isGgleSearchUrl) {
                execSetGgleFocus(
                    terminalFragment,
                )
                terminalWebView.requestFocus()
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO){
                        delay(200)
                    }
                    withContext(Dispatchers.Main) {
                        Keyboard.showKeyboard(
                            activity,
                            terminalWebView,
                        )
                    }
                }
                return@setOnClickListener
            }
            terminalFragment.ggleWebViewManager?.show(
//                "GGLE_SEARCH",
//                String(),
//                menuMapStrListCon,
//                String(),
//                extraMapCon,

                )
//            val jsConSrc = """
//            jsDialog.webView_S(
//                "GGLE_SEARCH",
//                "",
//                "${menuMapStrListCon}",
//                "",
//                "${extraMapCon}",
//            );
//        """.trimIndent()
//            val jsCon = JavaScriptLoadUrl.makeFromContents(
//                cmdIndexFragment.context,
//                jsConSrc.split("\n"),
//            ) ?: return@setOnClickListener
//            terminalFragment.binding.terminalWebView.loadUrl(jsCon)
//            return@setOnClickListener
//            val isGgleSearchUrl =
//                terminalFragment.binding.terminalWebView.url?.startsWith(
//                    WebUrlVariables.queryUrlBase
//                ) == true
////            cmdindexSearchLinearLayout.isVisible = !isGgleSearchUrl
//            if (!isGgleSearchUrl) {
////                searchEditText.requestFocus()
//                val isFocus = terminalFragment.binding.terminalWebView.requestFocus()
//                Keyboard.showKeyboard(
//                    activity,
//                    terminalFragment.binding.terminalWebView,
//                )
////                Keyboard.showKeyboardForCmdIndexFromActivity(
////                    activity,
////                    searchEditText,
////                )
//                return@setOnClickListener
//            }
//            execSetGgleFocus(
//                terminalFragment,
////                searchEditText,
//            )
        }
//        cmdIndexFragment.binding.cmdSearchEditText.setOnFocusChangeListener { v, hasFocus ->
//            if(
//                !hasFocus
//            ) return@setOnFocusChangeListener
//
//            val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
//                activity
//            ) ?: return@setOnFocusChangeListener
//            val isGgleSearchUrl =
//                terminalFragment.binding.terminalWebView.url?.startsWith(
//                    WebUrlVariables.queryUrlBase
//                ) == true
//            if (!isGgleSearchUrl) {
//                return@setOnFocusChangeListener
//            }
//            execSetGgleFocus(
//                terminalFragment,
////                searchEditText,
//            )
//        }
    }


    private fun execSetGgleFocus(
        fragment: Fragment,
//        searchEditText: AutoCompleteTextView,
    ){
//            cmdSearchEditText.isVisible = false
//        searchEditText.setSelection(0)
//        searchEditText.clearFocus()
        val jsContents = AssetsFileManager.readFromAssets(
            fragment.context,
            AssetsFileManager.ggleSchBoxFocus
        )
        ExecJsLoad.jsConLaunchHandler(
            fragment,
            jsContents
        )
    }
}