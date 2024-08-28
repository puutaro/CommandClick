package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.GoogleSuggest


object TextChangedListenerAdder {
    fun add (
        fragment: Fragment,
        cmdSearchEditText: AutoCompleteTextView,
//        currentAppDirPath: String,
//        cmdListAdapter: FannelIndexListAdapter
    ){
//        val context = fragment.context
//        val binding = fragment.binding
//        val cmdSearchEditText = binding.cmdSearchEditText
//        val cmdListView = binding.cmdList
        val googleSuggest = GoogleSuggest(
            fragment,
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
                if(
                    !cmdSearchEditText.hasFocus()
                ) return
//                val linearLayoutParam =
//                    cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
//                val cmdIndexFragmentWeight = linearLayoutParam.weight
//                if(
//                    cmdIndexFragmentWeight == ReadLines.LONGTH
//                ) {
//                    cmdSearchEditText.threshold = 100000
//                    return
//                }
//                if(!cmdIndexFragment.WebSearchSwitch) return
                googleSuggest.set(
                    cmdSearchEditText.text
                )
            }

            override fun afterTextChanged(s: Editable?) {
//                if(!cmdSearchEditText.hasFocus()) return
//                if(
//                    !cmdIndexFragment.WebSearchSwitch
//                ) {
//                    val listener = context as? CommandIndexFragment.OnFilterWebViewListener
//                    listener?.onFilterWebView(
//                        s.toString(),
//                    )
//                    return
//                }
//                val linearLayoutParam =
//                    cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
//                val cmdIndexFragmentWeight = linearLayoutParam.weight
//                if(
//                    cmdIndexFragmentWeight != ReadLines.LONGTH
//                ) return
//                val filteredCmdStrList = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
//                    UsePath.cmdclickDefaultAppDirPath
//                ).filter {
//                    Regex(
//                        s.toString().lowercase()
//                    ).containsMatchIn(
//                        it.lowercase()
//                    )
//                }
////                CommandListManager.execListUpdateByEditTextForCmdIndex(
//                    filteredCmdStrList,
//                    cmdListAdapter,
//                    cmdListView,
//                )
            }
        })
    }
}