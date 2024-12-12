package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecSimpleEditItem
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecWriteItem
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.lang.ref.WeakReference

class JsEditorItem(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun edit_S(
        listIndexPosition: Int,
    ){

        /*
        ## Description

        Edit file by edit text in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [SIMPLE_EDIT](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#simple_edit)

        ## selectedItem arg

        file name for desc

        ## listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)


        ## Example

        ```js.js
        var=runSimpleEdit
            ?func=jsEditorItem.edit_S
            ?args=
                &selectedItem=${item name}
                &listIndexPosition=NO_QUOTE:${item index}

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
        val editConstraintListAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditConstraintListAdapter
        ExecSimpleEditItem.edit(
            editConstraintListAdapter,
            listIndexPosition,
        )
    }

    @JavascriptInterface
    fun write_S(
        listIndexPosition: Int,
    ){
        /*
        ## Description

        Edit file by editor app in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [WRITE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#write)

        ## selectedItem arg

        file name for desc

        ## listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)


        ## Example

        ```js.js
        var=runEdit
            ?func=jsEditorItem.write_S
            ?args=
                &selectedItem=${item name}
                &listIndexPosition=NO_QUOTE:${item index}

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
        val editConstraintListAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditConstraintListAdapter
        val editContext = editFragment.context ?: return
        ExecWriteItem.write(
            editContext,
            editConstraintListAdapter,
            listIndexPosition
        )
    }
}