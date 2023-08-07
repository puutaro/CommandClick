package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.component.adapter.MenuListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class CopyFileEvent(
    cmdIndexFragment: CommandIndexFragment,
    private val sourceAppDirPath: String,
    private val sourceShellFileName:String,
) {

    private val context = cmdIndexFragment.context
    private val binding = cmdIndexFragment.binding
    private val cmdListView = binding.cmdList
    val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
    private var copyFileDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel

    fun invoke(){
        if(
            context == null
        ) return


        copyFileDialog = Dialog(
            context
        )
        copyFileDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val listDialogTitle = copyFileDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        listDialogTitle?.text = "Select app dirctory name"
        val listDialogMessage = copyFileDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = copyFileDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = copyFileDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            copyFileDialog?.dismiss()
        }

        setListView()
        copyFileDialog?.setOnCancelListener {
            copyFileDialog?.dismiss()
        }
        copyFileDialog?.window?.setGravity(Gravity.BOTTOM)
        copyFileDialog?.show()
    }

    private fun setListView() {
        if(
            context == null
        ) return
        val appDirList = FileSystems.filterSuffixJsFiles(
            cmdclickAppDirAdminPath
        ).map {
            it to icons8Wheel
        }
        val subMenuListView =
            copyFileDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = MenuListAdapter(
            context,
            appDirList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForCopyFile(
            subMenuListView,
        )
    }


    private fun invokeItemSetClickListnerForCopyFile(
        appDirListView: ListView,
    ) {
        appDirListView.setOnItemClickListener {
                parent, View, pos, id ->
            val menuListAdapter = appDirListView.adapter as MenuListAdapter
            val selectedScript = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            execInvokeItemSetClickListnerForCopyFile(
                sourceAppDirPath,
                sourceShellFileName,
                selectedScript
            )
            copyFileDialog?.dismiss()
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

        CommandListManager.execListUpdateForCmdIndex(
            sourceAppDirPath,
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
