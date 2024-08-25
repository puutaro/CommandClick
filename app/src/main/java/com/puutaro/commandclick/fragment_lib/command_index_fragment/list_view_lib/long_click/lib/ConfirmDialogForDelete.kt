package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File


object ConfirmDialogForDelete {

    private var addConfirmDialog: Dialog? = null
    fun show(
        cmdIndexFragment: CommandIndexFragment,
//        currentAppDirPath: String,
        scriptFileName: String,
//        cmdListView: RecyclerView
    ){
        val context =
            cmdIndexFragment.context
                ?: return
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val currentAppDirPathTermux = UsePath.makeTermuxPathByReplace()
        val shellContents = ReadText(
            File(cmdclickDefaultAppDirPath, scriptFileName).absolutePath
        ).readText()
        val displayContents = "\tpath: ${currentAppDirPathTermux}/${scriptFileName}" +
                "\n---\n${shellContents}"
        addConfirmDialog = Dialog(
            context
        )
        addConfirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            addConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = "Delete bellow contents, ok?"
        val confirmContentTextView =
            addConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = displayContents
        val confirmCancelButton =
            addConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            addConfirmDialog?.dismiss()
            addConfirmDialog = null
        }
        deleteOkListener(
//            currentAppDirPath,
            scriptFileName,
//            cmdListView
        )
        addConfirmDialog?.setOnCancelListener {
            addConfirmDialog?.dismiss()
            addConfirmDialog = null
        }
        addConfirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addConfirmDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        addConfirmDialog?.show()
    }

    private fun deleteOkListener(
//        currentAppDirPath: String,
        scriptFileName: String,
//        cmdListView: RecyclerView
    ){
        val confirmOkButtonView =
            addConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        confirmOkButtonView?.setOnClickListener {
            addConfirmDialog?.dismiss()
            addConfirmDialog = null
            FileSystems.removeFiles(
                File(
                    cmdclickDefaultAppDirPath,
                    scriptFileName,
                ).absolutePath
            )
            val fannelDirName = scriptFileName
                .removeSuffix(
                    UsePath.SHELL_FILE_SUFFIX
                )
                .removeSuffix(
                    UsePath.JS_FILE_SUFFIX
                ) + UsePath.fannelDirSuffix
            FileSystems.removeDir(
                "${cmdclickDefaultAppDirPath}/${fannelDirName}"
            )

//            CommandListManager.execListUpdateForCmdIndex(
////                currentAppDirPath,
//                cmdListView,
//            )
        }
    }
}