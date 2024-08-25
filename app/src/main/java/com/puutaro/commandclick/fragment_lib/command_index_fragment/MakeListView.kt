package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.FannelQrLogoClickListener
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.FannelNameClickListenerSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.FocusToGgleSearchBox
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.KeyListenerSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.TextChangedListenerAdder
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.ExecOnLongClickDo
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.ExecOnQrLongClickDo
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool


class MakeListView(
    private val binding: CommandIndexFragmentBinding,
    private val cmdIndexFragment: CommandIndexFragment,
//    fannelInfoMap: Map<String, String>,
) {
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    private val currentAppDirPathTermux = UsePath.makeTermuxPathByReplace()
//    private val cmdListView = binding.cmdList
    private val cmdSearchEditText = binding.cmdSearchEditText


    fun makeList(
        context: Context
    ): FannelIndexListAdapter {
        FileSystems.createDirs(cmdclickDefaultAppDirPath)
        val cmdList = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            cmdclickDefaultAppDirPath
        )
        val baskstackOrder =
            cmdIndexFragment.activity?.supportFragmentManager?.getBackStackEntryCount() ?: 0
        cmdSearchEditText.hint = "(${baskstackOrder}) ${UsePath.makeOmitPath(currentAppDirPathTermux)}"
        return FannelIndexListAdapter(
            cmdIndexFragment,
//            currentAppDirPath,
            cmdList.toMutableList()
        )
    }

    fun makeTextFilter(
        cmdListAdapter: FannelIndexListAdapter,
    ){

        FocusToGgleSearchBox.handle(cmdIndexFragment)

        TextChangedListenerAdder.add(
            cmdIndexFragment,
//            currentAppDirPath,
            cmdListAdapter
        )

        KeyListenerSetter.set(
            cmdIndexFragment,
//            currentAppDirPath,
        )
    }

    fun makeClickItemListener(
        fannelIndexListAdapter: FannelIndexListAdapter
    ){
        FannelNameClickListenerSetter.set(
            cmdIndexFragment,
//            currentAppDirPath,
            fannelIndexListAdapter
        )
        FannelQrLogoClickListener.set(
            cmdIndexFragment,
//            currentAppDirPath,
            fannelIndexListAdapter
        )
    }

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

    fun onLongClickQrDo(
        fannelIndexListAdapter: FannelIndexListAdapter
    ) {
        ExecOnQrLongClickDo.invoke(
            cmdIndexFragment,
//            currentAppDirPath,
            fannelIndexListAdapter
        )
    }

    fun onLongClickDo (
        fannelIndexListAdapter: FannelIndexListAdapter
    ) {
        ExecOnLongClickDo.invoke(
            cmdIndexFragment,
//            currentAppDirPath,
            fannelIndexListAdapter
        )
    }
}
