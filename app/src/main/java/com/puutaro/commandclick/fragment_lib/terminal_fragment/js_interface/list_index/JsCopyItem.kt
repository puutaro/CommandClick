package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFile
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileHere
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyPath
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsCopyItem(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
        fannelInfoMap
    )
    @JavascriptInterface
    fun copyPath(
        selectedItem: String,
        listIndexPosition: Int
    ){
        /*
        Copy path or contents from list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### Corresponding macro

        -> [COPY_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#copy_path)

        ### selectedItem arg

        file name for copy

        ### listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### Example

        ```js.js
        var=runCopyPath
           ?func=jsCopyItem.copyPath
           ?args=
               &selectedItem=${item name}
               &listIndexListPosition=NO_QUOTE:${item index}

        ```

        */

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecCopyPath.copyPath(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }

    @JavascriptInterface
    fun copyFile_S(
        selectedItem: String,
        listIndexPosition: Int,
        initialPath: String,
    ){
        /*
        Copy file to list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### Corresponding macro

        -> [COPY_FILE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#copy_file)

        ### selectedItem arg

        file name for copy

        ### listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### initialPath arg

        File picker initial dir path

        ### Example

        ```js.js
        var=runCopyFile
           ?func=jsCopyItem.copyFile_S
           ?args=
               &selectedItem=${item name}
               &listIndexListPosition=NO_QUOTE:${item index}
               &initialPath="/storage/emulated/0/Music"

        ```

        */

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val filterMap = mapOf(
            EditSettingExtraArgsTool.ExtraKey.INITIAL_PATH.key to initialPath,
        )
        ExecCopyFile.copyFile(
            editFragment,
            selectedItem,
            listIndexPosition,
            filterMap,
        )
    }

    @JavascriptInterface
    fun copyFileHere_S(
        selectedItem: String,
        listIndexPosition: Int,
    ){
        /*
        Copy file in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### Corresponding macro

        -> [COPY_FILE_HERE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#copy_file_here)

        ### selectedItem arg

        file name for copy

        ### listIndexListPosition arg

        list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### initialPath arg

        File picker initial dir path

        ### Example

        ```js.js
        var=runCopyFile
           ?func=jsCopyItem.copyFileHere_S
           ?args=
               &selectedItem=${item name}
               &listIndexListPosition=NO_QUOTE:${item index}

        ```

        */

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecCopyFileHere.copyFileHere(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }
}