package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
import com.puutaro.commandclick.util.ReadText


object ConfirmDialogForDelete {
    fun show(
        cmdIndexCommandIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        scriptFileName: String,
        cmdListAdapter: ArrayAdapter<String>,
        cmdListView: ListView
    ){
        val context = cmdIndexCommandIndexFragment.context

        val currentAppDirPathTermux = UsePath.makeTermuxPathByReplace(currentAppDirPath)
        val shellContents = ReadText(
            currentAppDirPath,
            scriptFileName
        ).readText()
        val displayContents = "\tpath: ${currentAppDirPathTermux}/${scriptFileName}" +
                "\n---\n${shellContents}"
        val linearLayoutForDialog = LinearLayoutAdderForDialog.add(
            context,
            displayContents
        )
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Delete bellow contents, ok?"
            )
            .setView(linearLayoutForDialog)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                FileSystems.removeFiles(
                    currentAppDirPath,
                    scriptFileName,
                )
                val fannelDirName = scriptFileName
                    .removeSuffix(
                        UsePath.SHELL_FILE_SUFFIX
                    )
                    .removeSuffix(
                        UsePath.JS_FILE_SUFFIX
                    ) + UsePath.fannelDirSuffix
                FileSystems.removeDir(
                    "${currentAppDirPath}/${fannelDirName}"
                )
                CommandListManager.execListUpdate(
                    currentAppDirPath,
                    cmdListAdapter,
                    cmdListView,
                )
            })
            .setNegativeButton("NO", null)
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(R.color.black)
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
    }
}