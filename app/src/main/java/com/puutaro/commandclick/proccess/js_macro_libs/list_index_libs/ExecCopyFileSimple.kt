package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecCopyFileSimple {

    fun copy(
        editFragment: EditFragment,
        selectedItem: String,
        jsActionMap: Map<String, String>?,
    ){
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()
        val copyDirOrTsvPathToTypeCon = argsMap.get(
            CopyFileSimpleKey.COPY_DESTI_TSV_PATH_CON.key
        ) ?: return
        ExecSimpleCopy.execCopy(
            editFragment,
            copyDirOrTsvPathToTypeCon,
            selectedItem,
        )
    }
}

object ExecSimpleCopy {

    private enum class DirOrTsvType(
        var type: String
    ) {
        DIR("dir"),
        TSV("tsv"),
    }

    private val dirOrTsvTypePairList = listOf(
        DirOrTsvType.DIR.type to CmdClickIcons.FOLDA.id,
        DirOrTsvType.TSV.type to CmdClickIcons.FILE.id,
    )
    fun execCopy(
        editFragment: EditFragment,
        copyDirOrTsvPathToTypeCon: String,
        selectedItem: String,
    ) {
        val srcItem = getCurrentItem(
            editFragment,
            selectedItem,
        ) ?: return
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "copy_execCopy.txt").absolutePath,
            listOf(
                "selectedItem: ${selectedItem}",
                "srcItem: ${srcItem}",
                "copyDirOrTsvPathToTypeCon: ${copyDirOrTsvPathToTypeCon}",
            ).joinToString("\n\n\n")
        )
        val copyDirOrTsvList = makeCopyDirOrTsvList(
            editFragment,
            copyDirOrTsvPathToTypeCon,
        )
        if (
            copyDirOrTsvList.isEmpty()
        ) return
        if(copyDirOrTsvList.size == 1){
            val selectedDirOrTsvName =
                makeSelectedDirOrTsvName(copyDirOrTsvList)
            ExecCopyToOther.exec(
                editFragment,
                copyDirOrTsvList,
                selectedDirOrTsvName,
                srcItem,
            )
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                CopyListDialog.launch(
                    editFragment,
                    copyDirOrTsvList,
                    srcItem,
                )
            }
        }

    }

    private fun makeSelectedDirOrTsvName(
        copyDirOrTsvList:  List<Pair<String, Int>>
    ): String {
        val selectedDirOrTsvPath =
            copyDirOrTsvList.first().first
        return File(selectedDirOrTsvPath).name
    }

    private fun getCurrentItem(
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
        editFragment: EditFragment,
        copyDirOrTsvPathToTypeCon: String,
    ): List<Pair<String, Int>> {
        val defaultDirOrTsvType =
            makeDefaultDirOrTsvType(editFragment)
        return copyDirOrTsvPathToTypeCon.split("\n").map {
            val dirOrTsvPathAndTypeList = it.split("\t")
            val dirOrTsvPath = dirOrTsvPathAndTypeList.firstOrNull()
                ?: String()
            val dirOrTsvTypeName =
                dirOrTsvPathAndTypeList.getOrNull(1)
                    ?: defaultDirOrTsvType
            val dirOrTsvType = makeCurrentDirOrTsvType(
                dirOrTsvTypeName,
                defaultDirOrTsvType
            )
            val iconId = dirOrTsvTypePairList.firstOrNull {
                val type = it.first
                type == dirOrTsvType
            }?.second ?: -1
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, "copy.txt").absolutePath,
                listOf(
                    "it: ${it}",
                    "dirOrTsvPathAndTypeList:${dirOrTsvPathAndTypeList}",
                    "dirOrTsvPath: ${dirOrTsvPath}",
                    "defaultDirOrTsvType: ${defaultDirOrTsvType}",
                    "dirOrTsvTypeName: ${dirOrTsvTypeName}",
                    "dirOrTsvType: ${dirOrTsvType}",
                ).joinToString("\n\n\n")
            )
            dirOrTsvPath to iconId
        }.filter {
            val iconId = it.second
            iconId > 0
        }
    }

    private fun makeCurrentDirOrTsvType(
        dirOrTsvTypeName: String,
        defaultDirOrTsvType: String
    ): String {
        return DirOrTsvType.values().firstOrNull {
            it.type == dirOrTsvTypeName
        }?.type ?: defaultDirOrTsvType
    }

    private fun makeDefaultDirOrTsvType(
        editFragment: EditFragment
    ): String {
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        return when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> DirOrTsvType.DIR.type
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> DirOrTsvType.TSV.type
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> String()
        }
    }
}
object CopyListDialog {

    private var copyListDialog: Dialog? = null

    fun launch(
        editFragment: EditFragment,
        copyDirOrTsvList: List<Pair<String, Int>>,
        srcItem: String,
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
            srcItem,
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
        srcItem: String,
    ) {
        val context = editFragment.context
            ?: return
        val copyListView =
            copyListDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val copyDirOrTsvNameList = copyDirOrTsvList.map {
            val dirOrTsvPath = it.first
            val iconId = it.second
            File(dirOrTsvPath).name to iconId
        }
        val copyListAdapter = SubMenuAdapter(
            context,
            copyDirOrTsvNameList.toMutableList()
        )
        copyListView?.adapter = copyListAdapter
        copyListItemClickListener(
            editFragment,
            copyListView,
            srcItem,
            copyDirOrTsvList
        )
    }

    private fun copyListItemClickListener(
        editFragment: EditFragment,
        copyListView: ListView?,
        srcItem: String,
        copyDirOrTsvList: List<Pair<String, Int>>
    ) {
        copyListView?.setOnItemClickListener { parent, view, position, id ->
            copyListDialog?.dismiss()
            val copyListAdapter = copyListView.adapter as SubMenuAdapter
            val selectedDirOrTsvName = copyListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            ExecCopyToOther.exec(
                editFragment,
                copyDirOrTsvList,
                selectedDirOrTsvName,
                srcItem,
            )
        }
    }
}

private object ExecCopyToOther {
    fun exec(
        editFragment: EditFragment,
        copyDirOrTsvList: List<Pair<String, Int>>,
        selectedDirOrTsvName: String,
        srcItem: String,
    ){
        val copyDirOrTsvPathToIconId = copyDirOrTsvList.firstOrNull {
            val selectedDirOrTsvPath = it.first
            val isHit =
                File(selectedDirOrTsvPath).name == selectedDirOrTsvName
            isHit
        }
        val selectedDirOrTsvPath = copyDirOrTsvPathToIconId?.first
            ?: return
        val iconId = copyDirOrTsvPathToIconId.second
        val foldaId = CmdClickIcons.FOLDA.id
        val tsvId = CmdClickIcons.FILE.id
        when(iconId){
            foldaId
            -> copyFileToDir(
                editFragment,
                srcItem,
                selectedDirOrTsvPath,
            )
            tsvId
            -> insertTsvInFirst(
                editFragment,
                srcItem,
                selectedDirOrTsvPath,
            )
        }
    }

    private fun getSrcItemPathForDir(
        srcItem: String
    ): String {
        val containsTab = srcItem.contains("\t")
        return when(
            containsTab
        ){
            true
            -> srcItem.split("\t")
                .getOrNull(1)
                ?: String()
            else -> srcItem
        }
    }

    private fun copyFileToDir(
        editFragment: EditFragment,
        srcItem: String,
        selectedDirPath: String,
    ){
        val srcItemPath =
            getSrcItemPathForDir(srcItem)
        if(
            !File(srcItemPath).isFile
        ) return
        if(
            !File(selectedDirPath).isDirectory
        ) FileSystems.createDirs(selectedDirPath)
        val destiFilePathObj = File(
            selectedDirPath,
            File(srcItemPath).name
        )
        if(
            destiFilePathObj.isFile
        ) return
        val destiFilePath = destiFilePathObj.absolutePath
        FileSystems.execCopyFileWithDir(
            File(srcItemPath),
            File(destiFilePath)
        )
        copyOkToast(
            editFragment.context
        )
    }

    private fun insertTsvInFirst(
        editFragment: EditFragment,
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
            makeInsertTsvLine(srcItemPath)
        TsvTool.insertTsvInFirst(
            selectedTsvPath,
            insertTsvLine,
        )
        copyOkToast(
            editFragment.context
        )
    }

    private fun makeInsertTsvLine(srcItem: String): String {
        val containsTab =
            srcItem.contains("\t")
        return when(containsTab){
            true -> srcItem
            else -> listOf(
                File(srcItem).name,
                srcItem,
            ).joinToString("\t")
        }
    }
    private fun copyOkToast(
        context: Context?
    ){
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "copy ok",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}

private enum class CopyFileSimpleKey(
    val key: String
){
    COPY_DESTI_TSV_PATH_CON("copyDestiTsvPathCon")
}