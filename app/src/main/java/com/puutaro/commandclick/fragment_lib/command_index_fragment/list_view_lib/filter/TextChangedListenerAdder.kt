package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.GoogleSuggest
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object TextChangedListenerAdder {
    fun add (
        cmdIndexCommandIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        cmdListAdapter: FannelIndexListAdapter
    ){
        val context = cmdIndexCommandIndexFragment.context
        val terminalViewModel: TerminalViewModel by cmdIndexCommandIndexFragment.activityViewModels()
        val binding = cmdIndexCommandIndexFragment.binding
        val cmdSearchEditText = binding.cmdSearchEditText
        val cmdListView = binding.cmdList
        val googleSuggest = GoogleSuggest(
            cmdIndexCommandIndexFragment,
            cmdSearchEditText
        )

        val filter = InputFilter { source, _, _, _, _, _ ->
            if (
                source.contains("　")
            ) " "
            else source
        }

        if (
            filter !in cmdSearchEditText.filters
        ) cmdSearchEditText.filters =
            arrayOf(
                *cmdSearchEditText.filters,
                filter
            )

        cmdSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!cmdSearchEditText.hasFocus()) return
                if(
                    terminalViewModel.readlinesNum == ReadLines.SHORTH
                ) {
                    cmdSearchEditText.threshold = 100000;
                    return
                }
                if(!cmdIndexCommandIndexFragment.WebSearchSwitch) return
                googleSuggest.set(
                    cmdSearchEditText.text
                )
            }

            override fun afterTextChanged(s: Editable?) {
                if(!cmdSearchEditText.hasFocus()) return
                if(
                    !cmdIndexCommandIndexFragment.WebSearchSwitch
                ) {
                    val listener = context as? CommandIndexFragment.OnFilterWebViewListener
                    listener?.onFilterWebView(
                        s.toString(),
                    )
                    return
                }
                if(
                    terminalViewModel.readlinesNum != ReadLines.SHORTH
                ) return
                val filteredCmdStrList = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
                    currentAppDirPath
                ).filter {
                    Regex(
                        s.toString().lowercase()
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                CommandListManager.execListUpdateByEditTextForCmdIndex(
                    filteredCmdStrList,
                    cmdListAdapter,
                    cmdListView,
                )
            }
        })
    }
}