package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.CommandListManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class TextChangedListenerAdder {
    companion object {
        fun add (
            cmdIndexFragment: CommandIndexFragment,
            filteringCmdStrList: List<String>,
            cmdListAdapter: ArrayAdapter<String>
        ){
            val context = cmdIndexFragment.context
            val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
            val binding = cmdIndexFragment.binding
            val cmdSearchEditText = binding.cmdSearchEditText
            val cmdListView = binding.cmdList


            cmdSearchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if(!cmdSearchEditText.hasFocus()) return
                    if(
                        !cmdIndexFragment.SpecialSearchSwitch
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
                    val filteredCmdStrList = filteringCmdStrList.filter {
                        Regex(s.toString()).containsMatchIn(it)
                    }
                    CommandListManager.execListUpdateByEditText(
                        filteredCmdStrList,
                        cmdListAdapter,
                        cmdListView
                    )
                }
            })
        }
    }
}