package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleEditItem
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecWriteItem
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsEditorItem(
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
    fun edit_S(
        selectedItem: String,
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

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecSimpleEditItem.edit(
            editFragment,
            selectedItem,
            listIndexPosition,
        )
    }

    @JavascriptInterface
    fun write_S(
        selectedItem: String,
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
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecWriteItem.write(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }
}