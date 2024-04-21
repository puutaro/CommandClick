package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsDeleteItem(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = SharePrefTool.getCurrentStateName(
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
            ToastUtils.showShort(
                "Invalid Index: ${listIndexListPosition} / ${listIndexListLastIndex}"
            )
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