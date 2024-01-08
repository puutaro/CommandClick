package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class CopyFileEvent(
    cmdIndexFragment: CommandIndexFragment,
    private val sourceAppDirPath: String,
    private val sourceFannelName:String,
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
        listDialogTitle?.text = "Select app dir name"
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
        copyFileDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        copyFileDialog?.window?.setGravity(Gravity.BOTTOM)
        copyFileDialog?.show()
    }

    private fun setListView() {
        if(
            context == null
        ) return
        val subMenuListView =
            copyFileDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            makeAppDirNameList().toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForCopyFile(
            subMenuListView,
        )
    }

    private fun makeAppDirNameList(): List<Pair<String, Int>> {
        val jsSuffix = UsePath.JS_FILE_SUFFIX
        val systemAppDirName = UsePath.cmdclickSystemAppDirName
        return FileSystems.filterSuffixJsFiles(
            cmdclickAppDirAdminPath
        ).map {
            val appDirName = it.removeSuffix(
                jsSuffix
            )
            appDirName to icons8Wheel
        }.filter {
            it.first != systemAppDirName
        }
    }


    private fun invokeItemSetClickListnerForCopyFile(
        appDirListView: ListView,
    ) {
        appDirListView.setOnItemClickListener {
                parent, View, pos, id ->
            val menuListAdapter = appDirListView.adapter as SubMenuAdapter
            val selectedAppDirName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            execInvokeItemSetClickListnerForCopyFile(
                sourceAppDirPath,
                sourceFannelName,
                selectedAppDirName
            )
            copyFileDialog?.dismiss()
            return@setOnItemClickListener
        }
    }

    private fun execInvokeItemSetClickListnerForCopyFile(
        sourceAppDirPath: String,
        sourceFannelName: String,
        selectedAppDirName: String,
    ) {
        val sourceFannelPath =
            "${sourceAppDirPath}/${sourceFannelName}"
        val selectedFannelPath =
            "${UsePath.cmdclickAppDirPath}/${selectedAppDirName}/${sourceFannelName}"
        FileSystems.execCopyFileWithDir(
            File(sourceFannelPath),
            File(selectedFannelPath),
        )
        copyResultToast(
            context,
            selectedFannelPath
        )
        CommandListManager.execListUpdateForCmdIndex(
            sourceAppDirPath,
            cmdListView,
        )

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
