package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsRenameItem(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun rename_S(
        listIndexPosition: Int,
        srcFragmentStr: String
    ){
        /*
        ## Description

        Rename item in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [RENAME](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#rename)

        ## selectedItem arg

        file name for rename

        ## listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Example

        ```js.js
        var=runCopyFile
           ?func=jsRenameItem.rename_S
           ?args=
               &selectedItem=${item name}
               &listIndexListPosition=NO_QUOTE:${item index}

        ```

        */
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context ?: return
        val lisetener = context as TerminalFragment.OnTextViewAndMapListUpdateListenerForTerm
        lisetener.onTextViewAndMapListUpdateForTerm(
            listIndexPosition,
            srcFragmentStr
        )
//        val activity = terminalFragment.activity
//        val fannelInfoMap = terminalFragment.fannelInfoMap
//        val currentFannelName = FannelInfoTool.getCurrentFannelName(
//            fannelInfoMap
//        )
//        val currentFannelState = FannelInfoTool.getCurrentStateName(
//            fannelInfoMap
//        )
//        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
//            activity,
////            currentAppDirPath,
//            currentFannelName,
//            currentFannelState
//        ) ?: return
//        ExecRenameFile.rename(
//            editFragment,
//            editFragment.binding.editListRecyclerView,
//            listIndexPosition,
//        )
    }
}