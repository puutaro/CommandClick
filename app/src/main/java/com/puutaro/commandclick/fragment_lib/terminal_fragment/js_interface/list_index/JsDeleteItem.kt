package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsDeleteItem(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
        fannelInfoMap
    )

    @JavascriptInterface
    fun delete_S(
        parentDirPath: String,
        selectedItem: String
    ){

        /*
        ## Description


        Delete item from list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [DELETE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#delete)

        ## parentDirPath arg

        parent dir path of file name for delete

        ## selectedItem arg

        file name for delete

        ## Example

        ```js.js
        var=runDelete
            ?func=jsDeleteItem.delete_S
            ?args=
                &parentDirPath=${parent dir path}
                &selectedItem=${file name}

        ```

        */

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as ListIndexAdapter
        val listIndexPosition =
            listIndexForEditAdapter.listIndexList.indexOf(selectedItem)
        ExecItemDelete.execItemDelete(
            editFragment,
            parentDirPath,
            selectedItem,
            listIndexPosition,
        )
    }

    @JavascriptInterface
    fun simpleDelete_S(
        selectedItem: String,
        listIndexListPosition: Int,
    ){
        /*
        ## Description

        Delete item from list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [SIMPLE_DELETE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#simple_delete)

        ## selectedItem arg

        file name for delete

        ## listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Example

        ```js.js
        var=runDelete
            ?func=jsDeleteItem.simpleDelete_S
            ?args=
                &selectedItem=${item name}
                &listIndexListPosition=NO_QUOTE:${item index}

        ```

        */

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexForEditAdapter = editListRecyclerView.adapter as ListIndexAdapter
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