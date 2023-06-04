package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.R
import android.content.Context
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.ItemClickListenerSetter
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
    ):  ArrayAdapter<String> {
        FileSystems.createDirs(currentAppDirPath)
        val cmdList = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            currentAppDirPath
        )
        val baskstackOrder =
            cmdIndexFragment.activity?.supportFragmentManager?.getBackStackEntryCount() ?: 0
        cmdSearchEditText.hint = "(${baskstackOrder}) ${UsePath.makeOmitPath(currentAppDirPathTermux)}"
        return ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            cmdList
        )
    }

    fun makeTextFilter(
        cmdListAdapter: ArrayAdapter<String>,
    ){
        cmdListView.setTextFilterEnabled(false)
        cmdListView.isTextFilterEnabled = false
        val filteringCmdStrList = (0..cmdListAdapter.getCount()-1).map{
            cmdListAdapter.getItem(it) ?: String()
        }

        TextChangedListenerAdder.add(
            cmdIndexFragment,
            filteringCmdStrList,
            cmdListAdapter
        )

        KeyListenerSetter.set(
            cmdIndexFragment,
            currentAppDirPath,
            cmdListAdapter,
        )
    }

    fun makeClickItemListener(
        cmdListAdapter: ArrayAdapter<String>
    ){
        ItemClickListenerSetter.set(
            cmdIndexFragment,
            currentAppDirPath,
            cmdListAdapter
        )
    }

    fun cmdListSwipeToRefresh(
        cmdListAdapter: ArrayAdapter<String>,
    ){
        val cmdListSwipeToRefresh = binding.cmdListSwipeToRefresh
        cmdListSwipeToRefresh.setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener {
                direction ->
            CommandListManager.execListUpdate(
                currentAppDirPath,
                cmdListAdapter,
                cmdListView,
            )
            cmdListSwipeToRefresh.isRefreshing = false
        })
    }

    fun onLongClickDo (
        item: MenuItem,
        contextItemSelected: Boolean,
        cmdListAdapter: ArrayAdapter<String>,
    ): Boolean {
        return ExecOnLongClickDo.invoke(
            cmdIndexFragment,
            currentAppDirPath,
            item,
            contextItemSelected,
            cmdListAdapter,
        )
    }
}
