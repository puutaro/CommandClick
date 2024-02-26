package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.util.state.FannelPrefGetter
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsDeleteItem(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = FannelPrefGetter.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = FannelPrefGetter.getCurrentStateName(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun delete_S(
        parentDirPath: String,
        selectedItem: String
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecItemDelete.execItemDelete(
            editFragment,
            parentDirPath,
            selectedItem,
        )
    }

    @JavascriptInterface
    fun simpleDelete_S(
        selectedItem: String,
        listIndexListPosition: Int,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexForEditAdapter = editListRecyclerView.adapter as ListIndexForEditAdapter
        val listIndexListLastIndex = listIndexForEditAdapter.listIndexList.lastIndex
        val isInValidIndex = listIndexListPosition < 0
                && listIndexListPosition > listIndexListLastIndex
        if(
            isInValidIndex
        ) {
            Toast.makeText(
                context,
                "Invalid Index: ${listIndexListPosition} / ${listIndexListLastIndex}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        ExecSimpleDelete.removeController(
            editFragment,
            editListRecyclerView,
            listIndexForEditAdapter,
            selectedItem,
            listIndexListPosition,
        )
    }
}