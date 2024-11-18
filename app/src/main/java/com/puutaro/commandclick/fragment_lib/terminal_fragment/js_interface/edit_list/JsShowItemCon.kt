package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecItemCat
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.lang.ref.WeakReference

class JsShowItemCon(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun show_S(
        listIndexPosition: Int,
    ){


        /*
        ## Description

        Show item contents

        ## Corresponding macro

        -> [CAT](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#cat)

        ## selectedItem arg

        file name for delete

        ## listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Example

        ```js.js
        var=runCat
           ?func=jsDeleteItem.show_S
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
        val editComponentListAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val editContext = editFragment.context ?: return
        ExecItemCat.cat(
            editContext,
            editComponentListAdapter,
            listIndexPosition
        )
    }
}