package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object CopyFileEvent{

//    private val context = cmdIndexFragment.context
//    private val binding = cmdIndexFragment.binding
//    private val cmdListView = binding.cmdList
//    private val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
//    private var copyFileDialog: Dialog? = null
//    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel

//    fun invoke(){
//        if(
//            context == null
//        ) return
//        copyFileDialog = Dialog(
//            context
//        )
//        copyFileDialog?.setContentView(
//            com.puutaro.commandclick.R.layout.list_dialog_layout
//        )
//        val listDialogTitle = copyFileDialog?.findViewById<AppCompatTextView>(
//            com.puutaro.commandclick.R.id.list_dialog_title
//        )
//        listDialogTitle?.text = "Select app dir name"
//        val listDialogMessage = copyFileDialog?.findViewById<AppCompatTextView>(
//            com.puutaro.commandclick.R.id.list_dialog_message
//        )
//        listDialogMessage?.isVisible = false
//        val listDialogSearchEditText = copyFileDialog?.findViewById<AppCompatEditText>(
//            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
//        )
//        listDialogSearchEditText?.isVisible = false
//        val cancelButton = copyFileDialog?.findViewById<AppCompatImageButton>(
//            com.puutaro.commandclick.R.id.list_dialog_cancel
//        )
//        cancelButton?.setOnClickListener {
//            copyFileDialog?.dismiss()
//            copyFileDialog = null
//        }
//
//        setListView()
//        copyFileDialog?.setOnCancelListener {
//            copyFileDialog?.dismiss()
//            copyFileDialog = null
//        }
//        copyFileDialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        copyFileDialog?.window?.setGravity(Gravity.BOTTOM)
//        copyFileDialog?.show()
//    }
//
//    private fun setListView() {
//        if(
//            context == null
//        ) return
//        val subMenuListView =
//            copyFileDialog?.findViewById<ListView>(
//                com.puutaro.commandclick.R.id.list_dialog_list_view
//            ) ?: return
//        val subMenuAdapter = SubMenuAdapter(
//            context,
//            makeAppDirNameList().toMutableList()
//        )
//        subMenuListView.adapter = subMenuAdapter
//        invokeItemSetClickListnerForCopyFile(
//            subMenuListView,
//        )
//    }
//
//    private fun makeAppDirNameList(): List<Pair<String, Int>> {
//        val jsSuffix = UsePath.JS_FILE_SUFFIX
//        val systemAppDirName = UsePath.cmdclickDefaultAppDirPath
//        return FileSystems.filterSuffixJsFiles(
//            cmdclickAppDirAdminPath
//        ).map {
//            val appDirName = it.removeSuffix(
//                jsSuffix
//            )
//            appDirName to icons8Wheel
//        }.filter {
//            it.first != systemAppDirName
//        }
//    }
//
//
//    private fun invokeItemSetClickListnerForCopyFile(
//        appDirListView: ListView,
//    ) {
//        appDirListView.setOnItemClickListener {
//                parent, View, pos, id ->
//            val menuListAdapter = appDirListView.adapter as SubMenuAdapter
//            val selectedAppDirName = menuListAdapter.getItem(pos)
//                ?: return@setOnItemClickListener
//            execInvokeItemSetClickListnerForCopyFile(
//                sourceAppDirPath,
//                sourceFannelName,
//                UsePath.cmdclickDefaultAppDirName
//            )
//            copyFileDialog?.dismiss()
//            copyFileDialog = null
//            return@setOnItemClickListener
//        }
//    }

    fun execInvokeItemSetClickListnerForCopyFile(
//        cmdListView: RecyclerView,
        sourceFannelName: String,
    ) {
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val sourceFannelPath =
            "${cmdclickDefaultAppDirPath}/${sourceFannelName}"
        FileSystems.execCopyFileWithDir(
            File(sourceFannelPath),
            File(sourceFannelPath),
        )
        copyResultToast(
            sourceFannelPath
        )
//        CommandListManager.execListUpdateForCmdIndex(
////            cmdclickDefaultAppDirPath,
//            cmdListView,
//        )

    }
}

private fun copyResultToast(
    selectedShellFilePath: String
){
    if(
        File(
            selectedShellFilePath
        ).isFile
    ){
        ToastUtils.showShort(
            "copy, ok\n" +
                "file: ${selectedShellFilePath}"
        )
        return
    }
    ToastUtils.showLong(
        "copy, failure\n" +
                "file: ${selectedShellFilePath}"
    )
}
