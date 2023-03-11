package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class CopyFileEvent(
    cmdIndexFragment: CommandIndexFragment,
    private val sourceAppDirPath: String,
    private val sourceShellFileName:String,
    private val cmdListAdaptar: ArrayAdapter<String>,
) {

    private val context = cmdIndexFragment.context
    private val binding = cmdIndexFragment.binding
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
        sourceShellFileName: String,
        selectedShellFileName: String,
    ) {

        val sourceShellFilePath = "${sourceAppDirPath}/${sourceShellFileName}"
        val selectedShellFilePath = makeSelectedShellFilePath(
            sourceAppDirPath,
            sourceShellFileName,
            selectedShellFileName
        )

        FileSystems.copyFile(
            sourceShellFilePath,
            selectedShellFilePath
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

internal fun makeSelectedShellFilePath(
    sourceAppDirPath: String,
    sourceShellFileName: String,
    selectedShellFileName: String
): String {
    val selectedAppDirPath = UsePath.cmdclickAppDirPath + '/' +
            selectedShellFileName.removeSuffix(
                CommandClickShellScript.JS_FILE_SUFFIX
            )
    val selectedShellFilePathSource = if(sourceAppDirPath == selectedAppDirPath) {
        sourceAppDirPath +
                "/${CommandClickShellScript.makeCopyPrefix()}" +
                "_${sourceShellFileName}"
    } else {
        "${selectedAppDirPath}/${sourceShellFileName}"
    }
    return if(File(selectedShellFilePathSource).isFile){
        selectedAppDirPath +
                "/${CommandClickShellScript.makeCopyPrefix()}" +
                "_${sourceShellFileName}"
    } else {
        selectedShellFilePathSource
    }
}


internal fun copyResultToast(
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
