package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileSimple
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleCopy
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class JsCopySItem(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun copy_S(
        copyDirOrTsvPathToTypeCon: String,
        selectedItem: String,
        extraMapCon: String,
    ){
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
            currentFannelName,
            currentFannelState
        ) ?: return
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            ExecCopyFileSimple.extraMapSeparator
        ).toMap()
        val onWithFile = ExecCopyFileSimple.WithCpFile.howWithFile(extraMap)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "copy.txt").absolutePath,
//            listOf(
//                "extraMapCon: ${extraMapCon}",
//                "extraMap: ${extraMap}",
//                "onWithFile: ${onWithFile}",
//            ).joinToString("\n\n")
//        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                ExecSimpleCopy.execCopy(
                    editFragment,
                    copyDirOrTsvPathToTypeCon,
                    selectedItem,
                    onWithFile,
                )
            }
        }
    }
}
