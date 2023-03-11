package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.R
import android.app.AlertDialog
import android.os.Environment
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems

class InstallFromDownloadDir(
    private val cmdIndexFragment: CommandIndexFragment,
    private val currentAppDirPath: String,
    private val cmdListAdapter: ArrayAdapter<String>,
) {

    val context = cmdIndexFragment.context
    val binding = cmdIndexFragment.binding
    val cmdListView = binding.cmdList
    val downloadDirPath =  Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    ).toString()

    fun install(){
        val downloadShellFileListView = ListView(
            context
        )
        val downloadShellFileList = mekeDownloadShellFileList()

        setDownloadShellFileListView(
            downloadShellFileListView,
            downloadShellFileList,
        )

        val alertDialogBuilder = AlertDialog.Builder(
            context
        )
            .setTitle("Select from download shell files")
            .setView(downloadShellFileListView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog
            .getWindow()?.setGravity(Gravity.BOTTOM);
        alertDialog.show()

        setDownloadShellFileListViewOnItemClickListener(
            downloadShellFileListView,
            downloadShellFileList,
            alertDialog
        )


    }

    private fun setDownloadShellFileListView(
        downloadShellFileListView: ListView,
        downloadShellFileList: List<String>

    ){
        context?.let {
            val urlHistoryDisplayListAdapter =
                ArrayAdapter(
                    context,
                    R.layout.simple_list_item_1,
                    downloadShellFileList
                )
            downloadShellFileListView.adapter = urlHistoryDisplayListAdapter
            downloadShellFileListView.setSelection(urlHistoryDisplayListAdapter.count)
        }
    }


    private fun setDownloadShellFileListViewOnItemClickListener (
        urlHistoryListView: ListView,
        urlHistoryList: List<String>,
        alertDialog: AlertDialog
    ){
        urlHistoryListView.setOnItemClickListener { parent, View, pos, id
            ->
            val downloadShellFileNameSource =
                urlHistoryList.getOrNull(
                    pos
                ) ?: return@setOnItemClickListener
            alertDialog.dismiss()
            val installedShellFilePath = "${currentAppDirPath}/${downloadShellFileNameSource}"
            FileSystems.moveFile(
                "${downloadDirPath}/${downloadShellFileNameSource}",
                installedShellFilePath
            )
            FileSystems.updateLastModified(
                currentAppDirPath,
                downloadShellFileNameSource
            )

            CommandListManager.execListUpdate(
                currentAppDirPath,
                cmdListAdapter,
                cmdListView,
            )
        }
    }

    private fun mekeDownloadShellFileList(): List<String> {
        return FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            downloadDirPath,
        )
    }
}