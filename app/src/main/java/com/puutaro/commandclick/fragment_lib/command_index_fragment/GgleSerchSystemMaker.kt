package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.KeyListenerSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.SearchButtonClickListener
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.TextChangedListenerAdder
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.file.AssetsFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object GgleSerchSystemMaker
//    private val binding: CommandIndexFragmentBinding,
//    private val cmdIndexFragment: CommandIndexFragment,
//    fannelInfoMap: HashMap<String, String>,
 {
//    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

//    private val currentAppDirPathTermux = UsePath.makeTermuxPathByReplace()
//    private val cmdListView = binding.cmdList
//    private val cmdSearchEditText = binding.cmdSearchEditText


//    fun makeList(
//        context: Context
//    ): FannelIndexListAdapter {
//        FileSystems.createDirs(cmdclickDefaultAppDirPath)
//        val cmdList = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
//            cmdclickDefaultAppDirPath
//        )
////        val baskstackOrder =
////            cmdIndexFragment.activity?.supportFragmentManager?.getBackStackEntryCount() ?: 0
////        cmdSearchEditText.hint = "(${baskstackOrder}) ${UsePath.makeOmitPath(currentAppDirPathTermux)}"
//        return FannelIndexListAdapter(
//            cmdIndexFragment,
////            currentAppDirPath,
//            cmdList.toMutableList()
//        )
//    }

     fun makeSearchButtonFromActivity(
         fragment: Fragment
     ){
         CoroutineScope(Dispatchers.Main).launch {
             when(fragment){
                 is CommandIndexFragment -> {
                     val listener = fragment.context as? CommandIndexFragment.OnSearchButtonMakeListenerForCmdIndex
                         ?: return@launch
                     listener.onSearchButtonMakeForCmdIndex()
                 }
                 is TerminalFragment -> {
                     val listener = fragment.context as? TerminalFragment.OnSearchButtonMakeListenerForTerm
                         ?: return@launch
                     listener.onSearchButtonMakeForTerm()
                 }
             }
         }

     }

    fun makeSearchEditTextOnlyCmdIndexFragment(
        fragment: Fragment,
//        cmdindexSearchButton: LinearLayoutCompat,

        cmdSearchEditText: AutoCompleteTextView,
//        cmdListAdapter: FannelIndexListAdapter,
    ){
//        val binding = cmdIndexFragment.binding
//        val cmdSearchEditText = binding.cmdSearchEditText

        TextChangedListenerAdder.add(
            fragment,
            cmdSearchEditText
//            currentAppDirPath,
//            cmdListAdapter
        )

        KeyListenerSetter.set(
            fragment,
            cmdSearchEditText
//            currentAppDirPath,
        )
    }

//    fun makeClickItemListener(
//        fannelIndexListAdapter: FannelIndexListAdapter
//    ){
////        FannelNameClickListenerSetter.set(
////            cmdIndexFragment,
//////            currentAppDirPath,
////            fannelIndexListAdapter
////        )
//        FannelQrLogoClickListener.set(
//            cmdIndexFragment,
////            currentAppDirPath,
//            fannelIndexListAdapter
//        )
//    }

//    fun cmdListSwipeToRefresh(
//    ){
//        val cmdListSwipeToRefresh = binding.cmdListSwipeToRefresh
//        cmdListSwipeToRefresh.setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener {
//                direction ->
//            CommandListManager.execListUpdateForCmdIndex(
////                currentAppDirPath,
//                cmdListView,
//            )
//            cmdListSwipeToRefresh.isRefreshing = false
//        })
//    }

//    fun onLongClickQrDo(
//        fannelIndexListAdapter: FannelIndexListAdapter
//    ) {
//        ExecOnQrLongClickDo.invoke(
//            cmdIndexFragment,
////            currentAppDirPath,
//            fannelIndexListAdapter
//        )
//    }

//    fun onLongClickDo (
//        fannelIndexListAdapter: FannelIndexListAdapter
//    ) {
//        ExecOnLongClickDo.invoke(
//            cmdIndexFragment,
////            currentAppDirPath,
//            fannelIndexListAdapter
//        )
//    }
}
