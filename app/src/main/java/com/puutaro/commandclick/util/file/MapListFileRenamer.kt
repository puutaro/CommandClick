package com.puutaro.commandclick.util.file

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecReWriteForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexDuplicate
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.CcPathTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object MapListFileRenamer {
    private var renamePromptDialog: Dialog? = null

    fun rename(
        context: Context?,
        editListRecyclerView: RecyclerView,
        mapListPath: String,
        lineMap: Map<String, String>,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                execRename(
                    context,
                    editListRecyclerView,
                    mapListPath,
                    lineMap,
                )
            }
        }
    }

    private fun execRename(
        context: Context?,
        editListRecyclerView: RecyclerView,
        mapListPath: String,
        lineMap: Map<String, String>,
    ){
        if(
            context == null
        ) return
//        fragment.disableKeyboardFragmentChange = true
//        val context = fragment.context
//            ?: return

        renamePromptDialog = Dialog(
            context
        )
        renamePromptDialog?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            renamePromptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Rename title"
        val promptMessageTextView =
            renamePromptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            renamePromptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
//        val titleFileNameAndPathConPair =
//            TitleFileNameAndPathConPairForListIndexAdapter.get(lineMap)
//                ?: return
        val fileNameOrSrcTitle = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
        )
//            titleFileNameAndPathConPair.first

        promptEditText?.setText(
            fileNameOrSrcTitle
        )
        val promptCancelButton =
            renamePromptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            dismissProcess(
//                fragment
            )
        }
        val promptOkButtonView =
            renamePromptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    ExecRenameProcess.rename(
                        editListRecyclerView,
                        mapListPath,
                        lineMap,
                        promptEditText,
                    )
                }
                withContext(Dispatchers.Main) {
                    dismissProcess(
//                        fragment
                    )
                }
            }
        }
        renamePromptDialog?.setOnCancelListener {
            dismissProcess(
//                fragment
            )
        }
        renamePromptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        renamePromptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        renamePromptDialog?.show()
    }

    private object ExecRenameProcess {
        fun rename(
            editListRecyclerView: RecyclerView,
            mapListPath: String,
            lineMap: Map<String, String>,
            promptEditText: AutoCompleteTextView?,

            ) {
            val inputEditable = promptEditText?.text
            if (
                inputEditable.isNullOrEmpty()
            ) {
                ToastUtils.showShort("No type item name")
                return
            }
//            val titleFileNameAndPathConPair =
//                TitleFileNameAndPathConPairForListIndexAdapter.get(lineMap)
//                    ?: return
            val srcTitleKey = ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
            val fileNameOrTitle = lineMap.get(
                srcTitleKey
            ) ?: String()
//                titleFileNameAndPathConPair.first
            val compExtend = CcPathTool.subExtend(fileNameOrTitle)
            val renamedFileNameOrTitle = UsePath.compExtend(
                inputEditable.toString().trim(),
                compExtend
            )
            if (
                fileNameOrTitle == renamedFileNameOrTitle
            ) return
            val srcConKey = ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
            val filePathOrCon = lineMap.get(
                srcConKey
            ) ?: String()
            val filePathOrConObj = File(filePathOrCon)
            val isTitleEqualPathOrCon =
                fileNameOrTitle == filePathOrConObj.name
            val isWithFileRename =
                filePathOrConObj.isFile
                        && isTitleEqualPathOrCon
            val renameFilePathOrCon = renameConOrPath(
                renamedFileNameOrTitle,
                filePathOrCon,
                isWithFileRename,
            )
            ListIndexDuplicate.isTsvDetect(
                mapListPath,
                renamedFileNameOrTitle,
                renameFilePathOrCon
            ).let {
                    isDetect ->
                if(
                    isDetect
                ) return
            }
            val viewLayoutPathKey = ListSettingsForListIndex.MapListPathManager.Key.VIEW_LAYOUT_TAG.key
            val renameLineMap = mapOf(
                srcTitleKey to renamedFileNameOrTitle,
                srcConKey to renameFilePathOrCon,
                viewLayoutPathKey to (lineMap.get(viewLayoutPathKey) ?: String())
            )
            val srcAndRepLinePairMapList = listOf(
                lineMap to renameLineMap,
            )
            MapListFileTool.updateMapListFileByReplace(
                mapListPath,
                srcAndRepLinePairMapList
            )
            if (isWithFileRename) {
                FileSystems.moveFile(
                    filePathOrCon,
                    renameFilePathOrCon
                )
            }
            ExecReWriteForListIndexAdapter.replaceListElementForTsv(
//                fragment,
                editListRecyclerView,
                srcAndRepLinePairMapList
            )
        }

        private fun renameConOrPath(
            renamedFileNameOrTitle: String,
            filePathOrCon: String,
            isWithFileRename: Boolean,
        ): String {
            val filePathOrConObj = File(filePathOrCon)
            return when (isWithFileRename) {
                true -> {
                    val tsvParentDirPath = filePathOrConObj.parent
                        ?: return String()
                    File(tsvParentDirPath, renamedFileNameOrTitle).absolutePath
                }
                else -> filePathOrCon
            }
        }
    }

    private fun dismissProcess(
//        editFragment: EditFragment
    ){
        renamePromptDialog?.dismiss()
        renamePromptDialog = null
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.IO) {
//                delay(200)
//                editFragment.disableKeyboardFragmentChange = false
//            }
//        }
    }
}