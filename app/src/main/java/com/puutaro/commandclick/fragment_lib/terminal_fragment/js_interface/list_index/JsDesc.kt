package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecShowDescription
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsDesc(
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
    fun show_S(
        selectedItem: String,
        listIndexPosition: Int,
    ){
        /*
        ## Description

        Show description for [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

        ## Corresponding macro

        -> [DESC](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#descdesc)

        ## selectedItem arg

        file name for desc

        ## listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)


        ## Example

        ```js.js
        var=runDesc
            ?func=JsDesc.show_S
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
        ExecShowDescription.desc(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }
}