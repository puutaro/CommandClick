package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
import com.puutaro.commandclick.util.ReadText


class ConfirmDialogForDelete {
    companion object {
        fun show(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
            shellScriptName: String,
            cmdListAdapter: ArrayAdapter<String>,
            cmdListView: ListView
        ){
            val context = cmdIndexFragment.context

            val currentAppDirPathTermux = UsePath.makeTermuxPathByReplace(currentAppDirPath)
            val shellContents = ReadText(
                currentAppDirPath,
                shellScriptName
            ).readText()
            val displayContents = "\tpath: ${currentAppDirPathTermux}/${shellScriptName}" +
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
                        shellScriptName,
                    )
                    CommandListManager.execListUpdate(
                        currentAppDirPath,
                        cmdListAdapter,
                        cmdListView,
                    )
                    if(currentAppDirPath == UsePath.cmdclickAppDirAdminPath){
                        val deleteAppDirName = shellScriptName.removeSuffix(
                            CommandClickShellScript.JS_FILE_SUFFIX
                        )
                        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
                        val displayDeleteAppDirPath =
                            "${
                                UsePath.makeTermuxPathByReplace(
                                cmdclickAppDirPath
                            )}/${deleteAppDirName}"
                        val alertDialogForAppDirAdmin = AlertDialog.Builder(context)
                            .setTitle(
                                "Delete bellow App dir, ok?"
                            )
                            .setMessage(
                                "\tpath: ${displayDeleteAppDirPath}"
                            )
                            .setPositiveButton("OK", DialogInterface.OnClickListener {
                                    dialogForAppDirAdmin, whichForAppDirAdmin ->
                                val deleteAppDirPath = "${cmdclickAppDirPath}/${deleteAppDirName}"
                                FileSystems.removeDir(
                                    deleteAppDirPath
                                )
                            })
                            .setNegativeButton("NO", null)
                            .show()
                        alertDialogForAppDirAdmin.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                            context?.getColor(R.color.black) as Int
                        )
                        alertDialogForAppDirAdmin.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                            context.getColor(R.color.black) as Int
                        )

                    }
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
}