package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.lang.ref.WeakReference

class JsDeleteItem(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as EditComponentListAdapter
//        val listIndexPosition =
//            listIndexForEditAdapter.lineMapList.indexOf(selectedItem)

//        ExecItemDelete.execItemDelete(
//            editFragment,
//            parentDirPath,
//            selectedItem,
//            listIndexPosition,
//        )
    }

    @JavascriptInterface
    fun simpleDelete_S(
        selectedItemMapCon: String,
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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexForEditAdapter = editListRecyclerView.adapter as EditComponentListAdapter
        val listIndexListLastIndex = listIndexForEditAdapter.lineMapList.lastIndex
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
        val selectedItemMap = CmdClickMap.createMap(
            selectedItemMapCon,
            ListSettingsForListIndex.MapListPathManager.mapListSeparator
        ).toMap()
        ExecSimpleDelete.removeController(
            editFragment,
            editListRecyclerView,
            listIndexForEditAdapter,
            selectedItemMap,
            listIndexListPosition,
        )
    }
}