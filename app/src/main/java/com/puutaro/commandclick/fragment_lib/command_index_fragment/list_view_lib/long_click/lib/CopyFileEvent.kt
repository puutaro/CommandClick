package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class CopyFileEvent(
    cmdIndexCommandIndexFragment: CommandIndexFragment,
    private val sourceAppDirPath: String,
    private val sourceShellFileName:String,
    private val cmdListAdaptar: ArrayAdapter<String>,
) {

    private val context = cmdIndexCommandIndexFragment.context
    private val binding = cmdIndexCommandIndexFragment.binding
    private val cmdListView = binding.cmdList
    val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath

    fun invoke(){
        val appDirList = FileSystems.filterSuffixJsFiles(
            cmdclickAppDirAdminPath
        )
        val appDirListView = ListView(context)
        val appDirListAdapter = context?.let {
            ArrayAdapter(
                it,
                R.layout.simple_list_item_1,
                appDirList
            )
        } ?: return
        appDirListView.adapter = appDirListAdapter
        appDirListView.setSelection(appDirListAdapter.count);
        val alertDialogBuilder = AlertDialog.Builder(
            context,
        )

        val alertDialog = alertDialogBuilder
            .setTitle("Select app dirctory name")
            .setView(appDirListView)
        val alertdialog = alertDialog.create()
        alertdialog.getWindow()?.setGravity(Gravity.BOTTOM);
        alertdialog.show()


        invokeItemSetClickListnerForCopyFile(
            appDirListView,
            appDirList,
            alertdialog
        )


    }

    private fun invokeItemSetClickListnerForCopyFile(
        AppDirListView: ListView,
        AppDirList: List<String>,
        alertDialog: AlertDialog
    ) {
        AppDirListView.setOnItemClickListener { parent, View, pos, id
            ->
            val selectedShellFileName = AppDirList.get(pos)
            execInvokeItemSetClickListnerForCopyFile(
                sourceAppDirPath,
                sourceShellFileName,
                selectedShellFileName
            )
            alertDialog.dismiss()
            return@setOnItemClickListener
        }
    }

    private fun execInvokeItemSetClickListnerForCopyFile(
        sourceAppDirPath: String,
        sourceScriptFileName: String,
        selectedShellFileName: String,
    ) {

        val sourceScriptFilePath = "${sourceAppDirPath}/${sourceScriptFileName}"
        val selectedShellFilePath = makeSelectedShellFilePath(
            sourceAppDirPath,
            sourceScriptFileName,
            selectedShellFileName
        )

        FileSystems.copyFile(
            sourceScriptFilePath,
            selectedShellFilePath
        )
        val sourceFannelName =
            sourceScriptFileName
                .removeSuffix(UsePath.JS_FILE_SUFFIX)
                .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
        val selectedFannelName =
            File(selectedShellFilePath).name
                .removeSuffix(UsePath.JS_FILE_SUFFIX)
                .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
        val sourceFannelDir = sourceFannelName + UsePath.fannelDirSuffix
        val selectedFannelDir = selectedFannelName + UsePath.fannelDirSuffix
        FileSystems.copyDirectory(
            "${sourceAppDirPath}/${sourceFannelDir}",
            "${File(selectedShellFilePath).parent}/${selectedFannelDir}"
        )
        copyResultToast(
            context,
            selectedShellFilePath
        )

        CommandListManager.execListUpdate(
            sourceAppDirPath,
            cmdListAdaptar,
            cmdListView,
        )

    }
}

private fun makeSelectedShellFilePath(
    sourceAppDirPath: String,
    sourceShellFileName: String,
    selectedShellFileName: String
): String {
    val selectedAppDirPath = UsePath.cmdclickAppDirPath + '/' +
            selectedShellFileName.removeSuffix(
                UsePath.JS_FILE_SUFFIX
            )
    val selectedShellFilePathSource = if(sourceAppDirPath == selectedAppDirPath) {
        sourceAppDirPath +
                "/${CommandClickScriptVariable.makeCopyPrefix()}" +
                "_${sourceShellFileName}"
    } else {
        "${selectedAppDirPath}/${sourceShellFileName}"
    }
    return if(File(selectedShellFilePathSource).isFile){
        selectedAppDirPath +
                "/${CommandClickScriptVariable.makeCopyPrefix()}" +
                "_${sourceShellFileName}"
    } else {
        selectedShellFilePathSource
    }
}


private fun copyResultToast(
    context: Context?,
    selectedShellFilePath: String
){
    if(
        File(
            selectedShellFilePath
        ).isFile
    ){
        Toast.makeText(
            context,
            "copy, ok\n" +
                    "file: ${selectedShellFilePath}",
            Toast.LENGTH_SHORT
        ).show()
        return
    }
    Toast.makeText(
        context,
        "copy, failure\n" +
                "file: ${selectedShellFilePath}",
        Toast.LENGTH_LONG
    ).show()
}
