package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.*
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.ReadText
import java.io.File


object ExecOnLongClickDo {

    private var contextMenuDialog: Dialog? = null

    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        fannelIndexListAdapter: FannelIndexListAdapter
    ) {
        val context = cmdIndexFragment.context
            ?: return

        fannelIndexListAdapter.itemLongClickListener =
            object : FannelIndexListAdapter.OnItemLongClickListener {
                override fun onItemLongClick(
                    itemView: View,
                    holder: FannelIndexListAdapter.FannelIndexListViewHolder,
                    position: Int
                ) {
                    val selectedFannelName = holder.fannelNameTextView.text.toString()
                    contextMenuDialog = Dialog(
                        context
                    )
                    contextMenuDialog?.setContentView(
                        R.layout.list_dialog_layout
                    )
                    setTitleImage(
                        contextMenuDialog?.findViewById<AppCompatImageView>(
                            R.id.list_dialog_title_image
                        ),
                        currentAppDirPath,
                        selectedFannelName,
                    )
                    val listDialogTitle = contextMenuDialog?.findViewById<AppCompatTextView>(
                        R.id.list_dialog_title
                    )
                    listDialogTitle?.text = selectedFannelName
                    val listDialogMessage = contextMenuDialog?.findViewById<AppCompatTextView>(
                        R.id.list_dialog_message
                    )
                    listDialogMessage?.isVisible = false
                    val listDialogSearchEditText = contextMenuDialog?.findViewById<AppCompatEditText>(
                        R.id.list_dialog_search_edit_text
                    )
                    listDialogSearchEditText?.isVisible = false
                    val cancelButton = contextMenuDialog?.findViewById<AppCompatImageButton>(
                        R.id.list_dialog_cancel
                    )
                    cancelButton?.setOnClickListener {
                        contextMenuDialog?.dismiss()
                    }

                    setContextMenuListView(
                        cmdIndexFragment,
                        currentAppDirPath,
                        selectedFannelName,
                    )
                    contextMenuDialog?.setOnCancelListener {
                        contextMenuDialog?.dismiss()
                    }
                    contextMenuDialog?.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    contextMenuDialog?.window?.setGravity(Gravity.BOTTOM)
                    contextMenuDialog?.show()

                }
        }
    }

    private fun setContextMenuListView(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        selectedScriptName: String
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val menePairList = ContextMenuEnums.values().map {
            it.itemName to it.imageId
        }
        val contextMenuListView =
            contextMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            menePairList.toMutableList()
        )
        contextMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForContextMenuList(
            cmdIndexFragment,
            currentAppDirPath,
            contextMenuListView,
            selectedScriptName
        )
    }

    private fun invokeItemSetClickListnerForContextMenuList(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        contextMenuListView: ListView,
        selectedScriptName: String,
    ) {
        val context = cmdIndexFragment.context
        contextMenuListView.setOnItemClickListener {
                parent, View, pos, id ->
            contextMenuDialog?.dismiss()
            val menuListAdapter = contextMenuListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener

            when(selectedMenuName){
                ContextMenuEnums.WRITE.itemName
                -> Editor(
                        currentAppDirPath,
                        selectedScriptName,
                        context
                    ).open()

                ContextMenuEnums.EDIT.itemName
                -> ScriptFileEdit.edit(
                        cmdIndexFragment,
                        currentAppDirPath,
                        selectedScriptName,
                    )

                ContextMenuEnums.COPY.itemName
                -> CopySubMenuDialog.launch(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedScriptName
                )
                ContextMenuEnums.UTILITY.itemName
                -> UtilitySubMenuDialog.launch(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedScriptName
                )

            }
            return@setOnItemClickListener
        }
    }
}


private object CopySubMenuDialog {

    private var copySubMenuDialog: Dialog? = null
    fun launch(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = cmdIndexFragment.context
            ?: return
        copySubMenuDialog = Dialog(
            context
        )
        copySubMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        setTitleImage(
            copySubMenuDialog?.findViewById<AppCompatImageView>(
                R.id.list_dialog_title_image
            ),
            currentAppDirPath,
            selectedScriptName,
        )
        val listDialogTitle = copySubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )
        listDialogTitle?.text = "Copy: $selectedScriptName"
        val listDialogMessage = copySubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = copySubMenuDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = copySubMenuDialog?.findViewById<AppCompatImageButton>(
            R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            copySubMenuDialog?.dismiss()
        }

        copySubMenuListView(
            cmdIndexFragment,
            currentAppDirPath,
            selectedScriptName,
        )
        copySubMenuDialog?.setOnCancelListener {
            copySubMenuDialog?.dismiss()
        }
        copySubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        copySubMenuDialog?.window?.setGravity(Gravity.BOTTOM)
        copySubMenuDialog?.show()
    }

    private fun copySubMenuListView(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        selectedScriptName: String,
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val copyMenePairList = CopySubMenuEnums.values().map {
            it.itemName to it.imageId
        }
        val copyMenuListView =
            copySubMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            copyMenePairList.toMutableList()
        )
        copyMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForCopy(
            cmdIndexFragment,
            copyMenuListView,
            currentAppDirPath,
            selectedScriptName
        )
    }

    private fun invokeItemSetClickListnerForCopy(
        cmdIndexFragment: CommandIndexFragment,
        copyMenuListView: ListView,
        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = cmdIndexFragment.context
        copyMenuListView.setOnItemClickListener {
                parent, View, pos, id ->
            copySubMenuDialog?.dismiss()
            val menuListAdapter = copyMenuListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener

            when(selectedMenuName){
                CopySubMenuEnums.COPY_PATH.itemName
                -> {
                    val shellFilePathByTermux = "${currentAppDirPath}/${selectedScriptName}"
                    val clipboard = context?.getSystemService(
                        Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText(
                        "cmdclick path",
                        shellFilePathByTermux
                    )
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        "copy ok",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                CopySubMenuEnums.COPY_FILE.itemName
                -> CopyFileEvent(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedScriptName,
                ).invoke()
            }
            return@setOnItemClickListener
        }
    }
}

private object UtilitySubMenuDialog {

    private var utilitySubMenuDialog: Dialog? = null
    fun launch(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = cmdIndexFragment.context
            ?: return
        utilitySubMenuDialog = Dialog(
            context
        )
        utilitySubMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        setTitleImage(
            utilitySubMenuDialog?.findViewById<AppCompatImageView>(
                R.id.list_dialog_title_image
            ),
            currentAppDirPath,
            selectedScriptName,
        )
        val listDialogTitle = utilitySubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )
        listDialogTitle?.text = "Utility: $selectedScriptName"
        val listDialogMessage = utilitySubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = utilitySubMenuDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = utilitySubMenuDialog?.findViewById<AppCompatImageButton>(
            R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            utilitySubMenuDialog?.dismiss()
        }

        utilitySubMenuListView(
            cmdIndexFragment,
            currentAppDirPath,
            selectedScriptName,
        )
        utilitySubMenuDialog?.setOnCancelListener {
            utilitySubMenuDialog?.dismiss()
        }
        utilitySubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        utilitySubMenuDialog?.window?.setGravity(Gravity.BOTTOM)
        utilitySubMenuDialog?.show()
    }

    private fun utilitySubMenuListView(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        selectedScriptName: String,
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val utilityMenePairList = UtilitySubMenuEnums.values().map {
            it.itemName to it.imageId
        }
        val utilityMenuListView =
            utilitySubMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            utilityMenePairList.toMutableList()
        )
        utilityMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForUtility(
            cmdIndexFragment,
            utilityMenuListView,
            currentAppDirPath,
            selectedScriptName
        )
    }

    private fun invokeItemSetClickListnerForUtility(
        cmdIndexFragment: CommandIndexFragment,
        copyMenuListView: ListView,
        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        copyMenuListView.setOnItemClickListener {
                parent, View, pos, id ->
            utilitySubMenuDialog?.dismiss()
            val menuListAdapter = copyMenuListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener

            when(selectedMenuName){
                UtilitySubMenuEnums.DELETE.itemName
                -> ConfirmDialogForDelete.show(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedScriptName,
                    cmdIndexFragment.binding.cmdList
                )
                UtilitySubMenuEnums.DESCRIPTION.itemName
                -> ScriptFileDescription.show(
                    cmdIndexFragment,
                    ReadText(
                        currentAppDirPath,
                        selectedScriptName
                    ).textToList(),
                    currentAppDirPath,
                    selectedScriptName
                )
                UtilitySubMenuEnums.KILL.itemName
                -> AppProcessManager.killDialog(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedScriptName
                )
            }
            return@setOnItemClickListener
        }
    }

}

private enum class ContextMenuEnums(
    val itemName: String,
    val imageId: Int
){
    WRITE("Write", R.drawable.icons8_support),
    EDIT("Edit", R.drawable.icons8_edit),
    COPY("Copy", com.termux.shared.R.drawable.ic_copy),
    UTILITY("Utility", R.drawable.icons8_support),
}

private enum class CopySubMenuEnums(
    val itemName: String,
    val imageId: Int
){
    COPY_PATH("Copy path", com.termux.shared.R.drawable.ic_copy),
    COPY_FILE("Copy file", R.drawable.icons8_folda)
}

private enum class UtilitySubMenuEnums(
    val itemName: String,
    val imageId: Int
){
    DELETE("Delete", R.drawable.icons8_refresh),
    DESCRIPTION("Description", R.drawable.icons8_info),
    KILL("Kill", R.drawable.icons8_cancel)
}

private fun setTitleImage(
    titleImageView: AppCompatImageView?,
    currentAppDirPath: String,
    selectedScriptName: String,
){
    val fannelDirName = CcPathTool.makeFannelDirName(selectedScriptName)
    val qrLogoPath = "$currentAppDirPath/$fannelDirName/${UsePath.qrPngRelativePath}"
    if(!File(qrLogoPath).isFile) return
    titleImageView?.load(qrLogoPath){
        crossfade(true)
        transformations(
            CircleCropTransformation()
        )
    }
}