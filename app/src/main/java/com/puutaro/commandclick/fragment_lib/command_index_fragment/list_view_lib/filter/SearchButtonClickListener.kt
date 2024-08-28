package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object SearchButtonClickListener {

    fun handle (
//        fragment: Fragment,
        activity: MainActivity,
        isFromTerminal: Boolean,
//        searchLinearButton: LinearLayoutCompat,
//        searchEditText: AutoCompleteTextView,
//
        ) {
        val targetFragmentInstance = TargetFragmentInstance()
        val cmdIndexFragment = targetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
        val searchEditText = cmdIndexFragment.binding.cmdSearchEditText
        val searchLinearButton = when(isFromTerminal){
            true -> targetFragmentInstance
                .getCurrentTerminalFragment(activity)
                ?.binding
                ?.termSearchButton
            else -> cmdIndexFragment.binding.cmdindexSearchButton
        } ?: return
        searchLinearButton.setOnClickListener{
            val terminalFragment = targetFragmentInstance.getCurrentTerminalFragment(
                activity
            ) ?: return@setOnClickListener
            val isGgleSearchUrl =
                terminalFragment.binding.terminalWebView.url?.startsWith(
                    WebUrlVariables.queryUrlBase
                ) == true
            if (!isGgleSearchUrl) {
                searchEditText.isVisible = true
                searchEditText.requestFocus()
//                searchEditText.isVisible = false
                Keyboard.showKeyboardForCmdIndexFromActivity(
                    activity,
                    searchEditText,
                )
                return@setOnClickListener
            }
            execSetFocus(
                terminalFragment,
                searchEditText,
            )
        }

        searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if(
                !hasFocus
            ) return@setOnFocusChangeListener

            val terminalFragment = targetFragmentInstance.getCurrentTerminalFragment(
                activity
            ) ?: return@setOnFocusChangeListener
            val isGgleSearchUrl =
                terminalFragment.binding.terminalWebView.url?.startsWith(
                    WebUrlVariables.queryUrlBase
                ) == true
            if (!isGgleSearchUrl) {
                return@setOnFocusChangeListener
            }
            execSetFocus(
                terminalFragment,
                searchEditText,
            )
        }
    }

    private fun execSetFocus(
        fragment: Fragment,
        searchEditText: AutoCompleteTextView,
    ){
//            cmdSearchEditText.isVisible = false
        searchEditText.setSelection(0)
        searchEditText.clearFocus()
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