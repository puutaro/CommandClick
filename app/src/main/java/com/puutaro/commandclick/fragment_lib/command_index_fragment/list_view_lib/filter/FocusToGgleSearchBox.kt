package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.view.View
import android.widget.LinearLayout
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object FocusToGgleSearchBox {

    fun handle (
        cmdIndexFragment: CommandIndexFragment,
    ) {
        val context = cmdIndexFragment.context
        val binding = cmdIndexFragment.binding
        val cmdSearchEditText = binding.cmdSearchEditText

        cmdSearchEditText.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (
                !hasFocus
            ) return@OnFocusChangeListener
            val linearLayoutParam =
                cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayout.LayoutParams
            val cmdIndexFragmentWeight = linearLayoutParam.weight
            if (
                cmdIndexFragmentWeight == ReadLines.LONGTH
            ) return@OnFocusChangeListener
            val jsContents = AssetsFileManager.readFromAssets(
                context,
                AssetsFileManager.ggleSchBoxFocus
            )
            val terminalFragment = TargetFragmentInstance().getCurrentTerminalFragmentFromFrag(
                cmdIndexFragment.activity
            ) ?: return@OnFocusChangeListener
            val isGgleSearchUrl =
                terminalFragment.binding.terminalWebView.url?.startsWith(
                    WebUrlVariables.queryUrlBase
                ) == true
            if (!isGgleSearchUrl) return@OnFocusChangeListener
            cmdSearchEditText.setSelection(0)
            cmdSearchEditText.clearFocus()
            ExecJsLoad.jsConLaunchHandler(
                cmdIndexFragment,
                jsContents
            )
        })
    }
}