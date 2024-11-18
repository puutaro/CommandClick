package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import android.webkit.ValueCallback
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ListJsDialogV2Script
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptWithListDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ValidFannelNameGetterForTerm
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecCopyFileSimple {

    const val extraMapSeparator = '|'
    const val nameToIconSeparator =
        PromptWithListDialog.Companion.PromptMapList.promptListNameToIconSeparator.toString()

    fun copy(
        fragment: Fragment,
        selectedItem: String,
        jsActionMap: Map<String, String>?,
    ){
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()
        val copyDirOrTsvPathToTypeCon = argsMap.get(
            CopyFileSimpleKey.COPY_DESTI_TSV_PATH_CON.key
        ) ?: return
        val extraMap = CmdClickMap.createMap(
            argsMap.get(CopyFileSimpleKey.EXTRA.key),
            extraMapSeparator
        ).toMap()
        val onWithFile = WithCpFile.howWithFile(extraMap)
        ExecSimpleCopy.execCopy(
            fragment,
            copyDirOrTsvPathToTypeCon,
            selectedItem,
            onWithFile,
        )
    }

    object WithCpFile {
        fun howWithFile(
            extraMap: Map<String, String>?
        ): Boolean {
            if(
                extraMap.isNullOrEmpty()
            ) return false
            val withFileOnValue = "ON"
            return extraMap.get(
                ExtraMapKey.WITH_FILE.key
            ) == withFileOnValue
        }
    }

    private enum class ExtraMapKey(
        val key: String
    ){
        WITH_FILE("withFile"),
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
        DirOrTsvType.DIR.type to CmdClickIcons.FOLDA.str,
        DirOrTsvType.TSV.type to CmdClickIcons.FILE.str,
    )

    fun execCopy(
        fragment: Fragment,
        copyDirOrTsvPathToTypeCon: String,
        selectedItem: String,
        onWithFile: Boolean,
    ) {
        val srcItem = selectedItem
//        getCurrentItem(
//            editFragment,
//            selectedItem,
//        ) ?: return
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "copy_execCopy.txt").absolutePath,
//            listOf(
//                "selectedItem: ${selectedItem}",
//                "srcItem: ${srcItem}",
//                "copyDirOrTsvPathToTypeCon: ${copyDirOrTsvPathToTypeCon}",
//            ).joinToString("\n\n\n")
//        )
        val copyDirOrTsvList = makeCopyDirOrTsvList(
//            fragment,
            copyDirOrTsvPathToTypeCon,
        )
        if (
            copyDirOrTsvList.isEmpty()
        ) return
        if(copyDirOrTsvList.size == 1){
            val selectedDirOrTsvName =
                makeSelectedDirOrTsvName(copyDirOrTsvList)
                    ?: return
            ExecCopyToOther.exec(
                copyDirOrTsvList,
                selectedDirOrTsvName,
                srcItem,
                onWithFile,
            )
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                CopyListDialog.launch(
                    fragment,
                    copyDirOrTsvList,
                    srcItem,
                    onWithFile,
                )
            }
        }

    }

    private fun makeSelectedDirOrTsvName(
        copyDirOrTsvList:  List<String>
    ): String? {
        val selectedDirOrTsvPath =
            copyDirOrTsvList.first()
                .split(ExecCopyFileSimple.nameToIconSeparator)
                .firstOrNull()
                ?: return null
        return File(selectedDirOrTsvPath).name
    }

//    private fun getCurrentItem(
//        editFragment: EditFragment,
//        selectedItem: String,
//    ): String? {
//        return selectedItem
////        val type = ListIndexEditConfig.getListIndexType(
////            editFragment
////        )
////        return when(type){
//////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//////            -> null
////            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
////            -> selectedItem
////            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
////            -> {
////                val listDirPath = FilePrefixGetter.get(
////                    editFragment,
////                    ListIndexAdapter.indexListMap,
////                    ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
////                )
////                File(listDirPath, selectedItem).absolutePath
////            }
////        }
//    }
    private fun makeCopyDirOrTsvList(
        copyDirOrTsvPathToTypeCon: String,
    ): List<String> {
        val defaultDirOrTsvType = makeDefaultDirOrTsvType()
        val nameToIconSeparator = ExecCopyFileSimple.nameToIconSeparator
        return copyDirOrTsvPathToTypeCon.split("\n").map {
            val dirOrTsvPathAndTypeList = it.split(nameToIconSeparator)
            val dirOrTsvPath = dirOrTsvPathAndTypeList.firstOrNull()
                ?: String()
            val dirOrTsvTypeName =
                dirOrTsvPathAndTypeList.getOrNull(1)
                    ?: defaultDirOrTsvType
            val dirOrTsvType = makeCurrentDirOrTsvType(
                dirOrTsvTypeName,
                defaultDirOrTsvType
            )
            val iconStr = dirOrTsvTypePairList.firstOrNull {
                val type = it.first
                type == dirOrTsvType
            }?.second ?: String()
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "copy.txt").absolutePath,
//                listOf(
//                    "it: ${it}",
//                    "dirOrTsvPathAndTypeList:${dirOrTsvPathAndTypeList}",
//                    "dirOrTsvPath: ${dirOrTsvPath}",
//                    "defaultDirOrTsvType: ${defaultDirOrTsvType}",
//                    "dirOrTsvTypeName: ${dirOrTsvTypeName}",
//                    "dirOrTsvType: ${dirOrTsvType}",
//                ).joinToString("\n\n\n")
//            )
            dirOrTsvPath to iconStr
        }.filter {
            val iconStr = it.second
            iconStr.isNotEmpty()
        }.map {
            "${it.first}${nameToIconSeparator}${it.second}"
        }
    }

    private fun makeCurrentDirOrTsvType(
        dirOrTsvTypeName: String,
        defaultDirOrTsvType: String
    ): String {
        return DirOrTsvType.entries.firstOrNull {
            it.type == dirOrTsvTypeName
        }?.type ?: defaultDirOrTsvType
    }

    private fun makeDefaultDirOrTsvType(
//        editFragment: EditFragment
    ): String {
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        return DirOrTsvType.TSV.type
//        return when(type){
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> DirOrTsvType.DIR.type
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> DirOrTsvType.TSV.type
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////            -> String()
//        }
    }
}
private object CopyListDialog {

//    private var copyListDialog: Dialog? = null

    fun launch(
        fragment: Fragment,
        copyDirOrTsvList: List<String>,
        srcItem: String,
        onWithFile: Boolean,
    ) {
        val terminalFragment = when(fragment){
            is TerminalFragment -> fragment
            else -> TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment.activity,
            )
        } ?: return
        val currentValidFannelName =
            ValidFannelNameGetterForTerm.get(
                terminalFragment
            )
        val selectLongPressJs = ListJsDialogV2Script.make(
            currentValidFannelName,
            "Select copy path",
            copyDirOrTsvList,
            saveTag = null,
        )

        terminalFragment.binding.terminalWebView.evaluateJavascript(
            selectLongPressJs,
            ValueCallback<String> { selectedDirOrTsvNameSrc ->
                val selectedDirOrTsvName = QuoteTool.trimBothEdgeQuote(
                    selectedDirOrTsvNameSrc
                )
                if(
                    selectedDirOrTsvName.isEmpty()
                ) return@ValueCallback
                ExecCopyToOther.exec(
                    copyDirOrTsvList,
                    selectedDirOrTsvName,
                    srcItem,
                    onWithFile,
                )
            })

//        copyListDialog = Dialog(
//            context
//        )
//        copyListDialog?.setContentView(
//            R.layout.list_dialog_layout
//        )
//        val title = "Select copy path"
//        copyListDialog?.findViewById<AppCompatTextView>(
//            R.id.list_dialog_title
//        )?.text = title
//        copyListDialog?.findViewById<AppCompatTextView>(
//            R.id.list_dialog_message
//        )?.isVisible = false
//        copyListDialog?.findViewById<AppCompatEditText>(
//            R.id.list_dialog_search_edit_text
//        )?.isVisible = false
//        setListView(
//            fragment,
//            copyDirOrTsvList,
//            srcItem,
//            onWithFile,
//        )
//        setCancelListener()
//        copyListDialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        copyListDialog
//            ?.window
//            ?.setGravity(Gravity.BOTTOM)
//        copyListDialog?.show()

    }

//    private fun setCancelListener(
//    ) {
//        val cancelImageButton =
//            copyListDialog?.findViewById<ImageButton>(
//                R.id.list_dialog_cancel
//            )
//        cancelImageButton?.setOnClickListener {
//            copyListDialog?.dismiss()
//            copyListDialog = null
//        }
//        copyListDialog?.setOnCancelListener {
//            copyListDialog?.dismiss()
//            copyListDialog = null
//        }
//    }

//    private fun setListView(
//        fragment: Fragment,
//        copyDirOrTsvList: List<Pair<String, Int>>,
//        srcItem: String,
//        onWithFile: Boolean,
//    ) {
//        val context = fragment.context
//            ?: return
//        val copyListView =
//            copyListDialog?.findViewById<ListView>(
//                R.id.list_dialog_list_view
//            )
//        val copyDirOrTsvNameList = copyDirOrTsvList.map {
//            val dirOrTsvPath = it.first
//            val iconId = it.second
//            File(dirOrTsvPath).name to iconId
//        }
//        val copyListAdapter = SubMenuAdapter(
//            context,
//            copyDirOrTsvNameList.toMutableList()
//        )
//        copyListView?.adapter = copyListAdapter
//        copyListItemClickListener(
//            copyListView,
//            srcItem,
//            copyDirOrTsvList,
//            onWithFile,
//        )
//    }

//    private fun copyListItemClickListener(
//        copyListView: ListView?,
//        srcItem: String,
//        copyDirOrTsvList: List<Pair<String, Int>>,
//        onWithFile: Boolean,
//    ) {
//        copyListView?.setOnItemClickListener { parent, view, position, id ->
//            copyListDialog?.dismiss()
//            copyListDialog = null
//            val copyListAdapter = copyListView.adapter as SubMenuAdapter
//            val selectedDirOrTsvName = copyListAdapter.getItem(position)
//                ?: return@setOnItemClickListener
//            ExecCopyToOther.exec(
//                copyDirOrTsvList,
//                selectedDirOrTsvName,
//                srcItem,
//                onWithFile,
//            )
//        }
//    }
}

private object ExecCopyToOther {
    fun exec(
        copyDirOrTsvList: List<String>,
        selectedDirOrTsvName: String,
        srcItem: String,
        onWithFile: Boolean,
    ){
        val nameToIconSeparator = ExecCopyFileSimple.nameToIconSeparator
        val copyDirOrTsvPathToIconId = copyDirOrTsvList.firstOrNull {
            val selectedDirOrTsvPathToIconStr = it.split(nameToIconSeparator)
            val selectedDirOrTsvPath = selectedDirOrTsvPathToIconStr.first()
            val isHit =
                File(selectedDirOrTsvPath).name == selectedDirOrTsvName
            isHit
        }
        val copyDirOrTsvPathToIconIdToIconStr = copyDirOrTsvPathToIconId?.split(nameToIconSeparator)
        val selectedDirOrTsvPath =
            copyDirOrTsvPathToIconIdToIconStr
                ?.firstOrNull()
                ?: return
        val iconStr = copyDirOrTsvPathToIconIdToIconStr.getOrNull(1)
            ?: return
        val foldaStr = CmdClickIcons.FOLDA.str
        val tsvId = CmdClickIcons.FILE.str
        when(iconStr){
            foldaStr
            -> copyFileToDir(
                srcItem,
                selectedDirOrTsvPath,
            )
            tsvId
            -> insertTsvInFirst(
                srcItem,
                selectedDirOrTsvPath,
                onWithFile,
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
        srcItem: String,
        selectedDirPath: String,
    ){
        val srcItemPath =
            getSrcItemPathForDir(srcItem)
        if(
            !File(srcItemPath).isFile
        ) {
            return
        }
        if(
            !File(selectedDirPath).isDirectory
        ) FileSystems.createDirs(selectedDirPath)
        val destiFilePathObj = File(
            selectedDirPath,
            File(srcItemPath).name
        )
        val isUpdateOk = destiFilePathObj.isFile
        val destiFilePath = destiFilePathObj.absolutePath
        FileSystems.execCopyFileWithDir(
            File(srcItemPath),
            File(destiFilePath),
            true,
        )
        when(isUpdateOk) {
            true -> updateOkToast()
            else -> copyOkToast()
        }
    }

    private fun insertTsvInFirst(
        srcItemPath: String,
        selectedTsvPath: String,
        onWithFile: Boolean,
    ){
        if(
            !File(selectedTsvPath).isFile
        ) FileSystems.writeFile(
            selectedTsvPath,
            String()
        )
        val insertTsvLine =
            makeInsertTsvLine(srcItemPath)
        val tsvConList = ReadText(selectedTsvPath)
            .textToList()
        val updateInsertTsvLine = saveByWithFile(
            onWithFile,
            insertTsvLine,
            selectedTsvPath,
        )
        val isUpdateOk =
            tsvConList
                .contains(updateInsertTsvLine)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "copy_update.txt").absolutePath,
//            listOf(
//                "selectedTsvPath: ${selectedTsvPath}",
//                "selectedTsvPath.isfile: ${File(selectedTsvPath).isFile}"
//            ).joinToString("\n\n")
//        )
        TsvTool.insertTsvInFirst(
            selectedTsvPath,
            updateInsertTsvLine,
            tsvConList,
        )
        when(isUpdateOk){
            true -> updateOkToast()
            else -> copyOkToast()
        }
    }

    private fun saveByWithFile(
        onWithFile: Boolean,
        insertTsvLine: String,
        selectedTsvPath: String,
    ): String {
        if(
            !onWithFile
        ) return insertTsvLine
        val titleAndSrcPathList = insertTsvLine
            .split("\t")
        val title = titleAndSrcPathList.first()
        val srcItemPath =
            titleAndSrcPathList
                .lastOrNull()
                ?: return insertTsvLine
        val srcFilePathObj = File(srcItemPath)
        if(
            !srcFilePathObj.isFile
        ) return insertTsvLine
        val grandParentDirPath = srcFilePathObj.parent
            ?: return insertTsvLine
        val saveDirName = CcPathTool.trimAllExtend(File(selectedTsvPath).name)
        val parentDirPath = File(grandParentDirPath, saveDirName).absolutePath
        FileSystems.createDirs(parentDirPath)
        val destiFilePathObj = File(parentDirPath, srcFilePathObj.name)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "copy_saveByWithFile.txt").absolutePath,
//            listOf(
//                "onWithFile: ${onWithFile}",
//                "srcItemPath: ${srcFilePathObj.absolutePath}",
//                "srcFilePathObj.isFile: ${srcFilePathObj.isFile}",
//                "File(srcItemPath).parent: ${File(insertTsvLine).parent}",
//                "parentDirPath ${parentDirPath}",
//                "destiFilePathObj.ab ${destiFilePathObj.absolutePath}",
//            ).joinToString("\n\n")
//        )
        FileSystems.execCopyFileWithDir(
            srcFilePathObj,
            destiFilePathObj,
            true,
        )
        return listOf(
            title,
            destiFilePathObj.absolutePath,
        ).joinToString("\t")
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
    private fun copyOkToast(){
        toastMsg(
            "copy ok",
        )
    }

    private fun updateOkToast(){
        toastMsg("Update ok",)
    }

    private fun toastMsg(
        msg: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            ToastUtils.showShort(msg)
        }
    }
}

private enum class CopyFileSimpleKey(
    val key: String
){
    COPY_DESTI_TSV_PATH_CON("copyDestiTsvPathCon"),
    EXTRA("extra"),
}