package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import android.view.MenuItem
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.FannelContentsClickListener
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.FannelNameClickListenerSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.KeyListenerSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.TextChangedListenerAdder
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.ExecOnLongClickDo
import com.puutaro.commandclick.util.*


class MakeListView(
    private val binding: CommandIndexFragmentBinding,
    private val cmdIndexFragment: CommandIndexFragment,
    readSharePreffernceMap: Map<String, String>,
) {
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentAppDirPathTermux = UsePath.makeTermuxPathByReplace(currentAppDirPath)
    private val cmdListView = binding.cmdList
    private val cmdSearchEditText = binding.cmdSearchEditText


    fun makeList(
        context: Context
    ): FannelIndexListAdapter {
        FileSystems.createDirs(currentAppDirPath)
        val cmdList = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            currentAppDirPath
        )
        val baskstackOrder =
            cmdIndexFragment.activity?.supportFragmentManager?.getBackStackEntryCount() ?: 0
        cmdSearchEditText.hint = "(${baskstackOrder}) ${UsePath.makeOmitPath(currentAppDirPathTermux)}"
        return FannelIndexListAdapter(
            cmdIndexFragment,
            currentAppDirPath,
            cmdList.toMutableList()
        )
    }

    fun makeTextFilter(
        cmdListAdapter: FannelIndexListAdapter,
    ){
//        cmdListView.setTextFilterEnabled(false)
//        cmdListView.isTextFilterEnabled = false
//        val filteringCmdStrList = (0..cmdListAdapter.getCount()-1).map{
//            cmdListAdapter.getItem(it) ?: String()
//        }

        TextChangedListenerAdder.add(
            cmdIndexFragment,
            currentAppDirPath,
            cmdListAdapter
        )

        KeyListenerSetter.set(
            cmdIndexFragment,
            currentAppDirPath,
        )
    }

    fun makeClickItemListener(
        fannelIndexListAdapter: FannelIndexListAdapter
    ){
        FannelNameClickListenerSetter.set(
            cmdIndexFragment,
            currentAppDirPath,
            fannelIndexListAdapter
        )
        FannelContentsClickListener.set(
            cmdIndexFragment,
            currentAppDirPath,
            fannelIndexListAdapter
        )
    }

    fun cmdListSwipeToRefresh(
    ){
        val cmdListSwipeToRefresh = binding.cmdListSwipeToRefresh
        cmdListSwipeToRefresh.setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener {
                direction ->
            CommandListManager.execListUpdateForCmdIndex(
                currentAppDirPath,
                cmdListView,
            )
            cmdListSwipeToRefresh.isRefreshing = false
        })
    }

    fun onLongClickDo (
        item: MenuItem,
        contextItemSelected: Boolean,
    ): Boolean {
        return ExecOnLongClickDo.invoke(
            cmdIndexFragment,
            currentAppDirPath,
            item,
            contextItemSelected,
        )
    }
}
