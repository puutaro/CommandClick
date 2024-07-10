package com.puutaro.commandclick.util.tsv

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecReWriteForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.TitleFileNameAndPathConPairForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object TsvLineRenamer {

    private var renamePromptDialog: Dialog? = null

    fun rename(
        editFragment: EditFragment,
        tsvPath: String,
        tsvLine: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                execRename(
                    editFragment,
                    tsvPath,
                    tsvLine,
                )
            }
        }
    }
    
    fun execRename(
        editFragment: EditFragment,
        tsvPath: String,
        tsvLine: String,
    ){
        editFragment.disableKeyboardFragmentChange = true
        val context = editFragment.context
            ?: return

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
        val titleFileNameAndPathConPair =
            TitleFileNameAndPathConPairForListIndexAdapter.get(tsvLine)
                ?: return
        val fileNameOrTitle = titleFileNameAndPathConPair.first

        promptEditText?.setText(
            fileNameOrTitle
        )
        val promptCancelButton =
            renamePromptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            dismissProcess(editFragment)
        }
        val promptOkButtonView =
            renamePromptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execRenameProcess(
                        editFragment,
                        tsvPath,
                        tsvLine,
                        promptEditText,
                    )
                }
                withContext(Dispatchers.Main) {
                    dismissProcess(editFragment)
                }
            }
        }
        renamePromptDialog?.setOnCancelListener {
            dismissProcess(editFragment)
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

    private fun execRenameProcess(
        editFragment: EditFragment,
        tsvPath: String,
        tsvLine: String,
        promptEditText: AutoCompleteTextView?,

    ){
        val inputEditable = promptEditText?.text
        if(
            inputEditable.isNullOrEmpty()
        ) {
            ToastUtils.showShort("No type item name")
            return
        }
        val titleFileNameAndPathConPair =
            TitleFileNameAndPathConPairForListIndexAdapter.get(tsvLine)
                ?: return
        val fileNameOrTitle = titleFileNameAndPathConPair.first
        val compExtend = CcPathTool.subExtend(fileNameOrTitle)
        val renamedFileNameOrTitle = UsePath.compExtend(
            inputEditable.toString(),
            compExtend
        )
        if(
            fileNameOrTitle == renamedFileNameOrTitle
        ) return
        val filePathOrCon = titleFileNameAndPathConPair.second
        val filePathOrConObj = File(filePathOrCon)
        val isTitleEqualPathOrCon =
            fileNameOrTitle == filePathOrConObj.name
        val isWithFileRename =
            filePathOrConObj.isFile
                    && isTitleEqualPathOrCon
        val renameFilePathOrCon = when(isWithFileRename){
            true -> {
                val tsvParentDirPath = filePathOrConObj.parent
                    ?: return
                File(tsvParentDirPath, renamedFileNameOrTitle).absolutePath
            }
            else -> filePathOrCon
        }
        val renameTsvLine = "${renamedFileNameOrTitle}\t${renameFilePathOrCon}"
        val srcAndRepLinePairList = listOf(
            tsvLine to renameTsvLine,
        )
        TsvTool.updateTsvByReplace(
            tsvPath,
            srcAndRepLinePairList
        )
        if(isWithFileRename){
            FileSystems.moveFile(
                filePathOrCon,
                renameFilePathOrCon
            )
        }
        ExecReWriteForListIndexAdapter.replaceListElementForTsv(
            editFragment,
            srcAndRepLinePairList
        )
    }

    private fun dismissProcess(
        editFragment: EditFragment
    ){
        renamePromptDialog?.dismiss()
        renamePromptDialog = null
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(200)
                editFragment.disableKeyboardFragmentChange = false
            }
        }
    }

}