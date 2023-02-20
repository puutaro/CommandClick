package com.puutaro.commandclick.fragment_lib.command_index_fragment.common

import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.util.FileSystems

class CommandListManager {

    companion object {

        fun makeListSource(
            currentAppDirPath: String
        ): List<String> {
            FileSystems.createDirs(currentAppDirPath)
            return FileSystems.filterSuffixShellOrJsOrHtmlFiles(
                currentAppDirPath
            )
        }


        fun execListUpdate(
            currentAppDirPath: String,
            cmdListAdapter: ArrayAdapter<String>,
            cmdListView: ListView
        ){
            cmdListAdapter.clear()
            val updateList = makeListSource(currentAppDirPath)
            cmdListAdapter.addAll(updateList)
            cmdListView.adapter = cmdListAdapter
            cmdListAdapter.notifyDataSetChanged();
        }

        fun execListUpdateByEditText(
            cmdStrList: List<String>,
            cmdListAdapter: ArrayAdapter<String>,
            cmdListView: ListView
        ){
            cmdListAdapter.clear()
            cmdListAdapter.addAll(cmdStrList)
            cmdListView.adapter = cmdListAdapter
            cmdListAdapter.notifyDataSetChanged();
        }
    }
}