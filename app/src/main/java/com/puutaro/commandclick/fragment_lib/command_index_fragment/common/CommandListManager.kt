package com.puutaro.commandclick.fragment_lib.command_index_fragment.common

import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.util.file.FileSystems

object CommandListManager {

    fun makeListSource(
        currentAppDirPath: String
    ): List<String> {
        FileSystems.createDirs(currentAppDirPath)
        return FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            currentAppDirPath
        )
    }

    fun execListUpdateForCmdIndex(
        currentAppDirPath: String,
        cmdListView: RecyclerView,
    ){
        val fannelIndexListAdapter = cmdListView.adapter as FannelIndexListAdapter
        val updateList = makeListSource(currentAppDirPath)
        if(
            fannelIndexListAdapter.fannelIndexList == updateList
        ) return
        fannelIndexListAdapter.fannelIndexList.clear()
        fannelIndexListAdapter.fannelIndexList.addAll(updateList)
        fannelIndexListAdapter.notifyDataSetChanged()
        cmdListView.scrollToPosition(
            fannelIndexListAdapter.itemCount - 1
        )
    }

    fun execListUpdateByEditText(
        cmdStrList: List<String>,
        cmdListAdapter: ArrayAdapter<String>,
        cmdListView: ListView
    ){
        cmdListAdapter.clear()
        cmdListAdapter.addAll(cmdStrList)
        cmdListView.adapter = cmdListAdapter
        cmdListAdapter.notifyDataSetChanged()
    }

    fun execListUpdateByEditTextForCmdIndex(
        cmdStrList: List<String>,
        fannelIndexListAdapter: FannelIndexListAdapter,
        cmdListView: RecyclerView
    ){
        fannelIndexListAdapter.fannelIndexList.clear()
        fannelIndexListAdapter.fannelIndexList.addAll(cmdStrList)
        fannelIndexListAdapter.notifyDataSetChanged()
        cmdListView.scrollToPosition(
            fannelIndexListAdapter.itemCount - 1
        )
    }
}