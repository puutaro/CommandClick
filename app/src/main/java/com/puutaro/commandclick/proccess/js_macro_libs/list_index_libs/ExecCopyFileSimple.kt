package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object ExecCopyFileSimple {

    private val dirOrTsvTypePairList = listOf(
        "dir" to CmdClickIcons.FOLDA.id,
        "tsv" to CmdClickIcons.FILE.id,
    )

    fun copy(
        editFragment: EditFragment,
        selectedItem: String,
        jsActionMap: Map<String, String>?,
    ){
        val srcItemPath = getCurrentItemPath(
            editFragment,
            selectedItem,
        ) ?: return
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()
        val copyDirOrTsvPathToTypeCon = argsMap.get(
            CopyFileSimpleKey.COPY_DESTI_TSV_PATH.key
        ) ?: String()
        val copyDirOrTsvList = makeCopyDirOrTsvList(
            copyDirOrTsvPathToTypeCon,
        )
        if(
            copyDirOrTsvList.isEmpty()
        ) return
        CopyListDialog.launch(
            editFragment,
            copyDirOrTsvList,
            srcItemPath,
        )
    }

    private fun getCurrentItemPath(
        editFragment: EditFragment,
        selectedItem: String,
    ): String? {
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        return when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            -> null
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> selectedItem
                .split("\t")
                .getOrNull(1)
                ?: String()
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {
                val listDirPath = FilePrefixGetter.get(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
                )
                File(listDirPath, selectedItem).absolutePath
            }
        }
    }

    private fun makeCopyDirOrTsvList(
        copyDirOrTsvPathToTypeCon: String,
    ): List<Pair<String, Int>> {
        return copyDirOrTsvPathToTypeCon.split("\n").map {
            val dirOrTsvPathAndTypeList = it.split("\t")
            val dirOrTsvPath = dirOrTsvPathAndTypeList.firstOrNull()
                ?: String()
            val dirOrTsvTypeName = dirOrTsvPathAndTypeList.getOrNull(1)
                ?: String()
            val iconId = dirOrTsvTypePairList.firstOrNull {
                val type = it.first
                type == dirOrTsvTypeName
            }?.second ?: -1
            dirOrTsvPath to iconId
        }.filter {
            val iconId = it.second
            iconId > 0
        }
    }
}

private object CopyListDialog {

    private var copyListDialog: Dialog? = null

    fun launch(
        editFragment: EditFragment,
        copyDirOrTsvList: List<Pair<String, Int>>,
        srcItemPath: String,
    ) {
        val context = editFragment.context
            ?: return
        copyListDialog = Dialog(
            context
        )
        copyListDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val title = "Select copy path"
        copyListDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )?.text = title
        copyListDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )?.isVisible = false
        copyListDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )?.isVisible = false
        setListView(
            editFragment,
            copyDirOrTsvList,
            srcItemPath,
        )
        setCancelListener()
        copyListDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        copyListDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        copyListDialog?.show()

    }

    private fun setCancelListener(
    ) {
        val cancelImageButton =
            copyListDialog?.findViewById<ImageButton>(
                R.id.list_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            copyListDialog?.dismiss()
        }
        copyListDialog?.setOnCancelListener {
            copyListDialog?.dismiss()
        }
    }

    private fun setListView(
        editFragment: EditFragment,
        copyDirOrTsvList: List<Pair<String, Int>>,
        srcItemPath: String,
    ) {
        val context = editFragment.context
            ?: return
        val copyListView =
            copyListDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val copyListAdapter = SubMenuAdapter(
            context,
            copyDirOrTsvList.toMutableList()
        )
        copyListView?.adapter = copyListAdapter
        copyListItemClickListener(
            copyListView,
            srcItemPath,
            copyDirOrTsvList
        )
    }

    private fun copyListItemClickListener(
        copyListView: ListView?,
        srcItemPath: String,
        copyDirOrTsvList: List<Pair<String, Int>>
    ) {
        copyListView?.setOnItemClickListener { parent, view, position, id ->
            copyListDialog?.dismiss()
            val copyListAdapter = copyListView.adapter as SubMenuAdapter
            val selectedDirOrTsvPath = copyListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val iconId = copyDirOrTsvList.firstOrNull {
                val menuName = it.first
                val isHit =
                    menuName == selectedDirOrTsvPath
                isHit
            }?.second ?: -1
            val foldaId = CmdClickIcons.FOLDA.id
            val tsvId = CmdClickIcons.FILE.id
            when(iconId){
                foldaId
                -> copyFileToDir(
                    srcItemPath,
                    selectedDirOrTsvPath,
                )
                tsvId
                -> insertTsvInFirst(
                    srcItemPath,
                    selectedDirOrTsvPath,
                )
            }
        }
    }

    private fun copyFileToDir(
        srcItemPath: String,
        selectedDirPath: String,
    ){
        if(
            !File(srcItemPath).isFile
        ) return
        if(
            !File(selectedDirPath).isDirectory
        ) FileSystems.createDirs(selectedDirPath)
        val destiFilePath = File(
            selectedDirPath,
            File(srcItemPath).name
        ).absolutePath
        FileSystems.copyFile(
            srcItemPath,
            destiFilePath
        )
    }

    private fun insertTsvInFirst(
        srcItemPath: String,
        selectedTsvPath: String,
    ){
        if(
            !File(selectedTsvPath).isFile
        ) FileSystems.writeFile(
            selectedTsvPath,
            String()
        )
        val insertTsvLine =
            listOf(
                File(srcItemPath).name,
                srcItemPath,
            ).joinToString("\t")
        TsvTool.inseartTsvInFirst(
            selectedTsvPath,
            insertTsvLine,
        )
    }
}

private enum class CopyFileSimpleKey(
    val key: String
){
    COPY_DESTI_TSV_PATH("copyDestiTsvPath")
}