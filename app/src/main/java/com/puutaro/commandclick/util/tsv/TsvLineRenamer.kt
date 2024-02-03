package com.puutaro.commandclick.util.tsv

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
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
            getTitleFileNameAndPathConPair(tsvLine)
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
            execRenameProcess(
                editFragment,
                tsvPath,
                tsvLine,
                promptEditText,
            )
            dismissProcess(editFragment)
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
        val context = editFragment.context
            ?: return
        val inputEditable = promptEditText?.text
        if(
            inputEditable.isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                "No type item name",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val titleFileNameAndPathConPair =
            getTitleFileNameAndPathConPair(tsvLine)
                ?: return
        val fileNameOrTitle = titleFileNameAndPathConPair.first
        val compExtend = CcPathTool.subExtend(fileNameOrTitle)
        val filePathOrCon = titleFileNameAndPathConPair.second
        val renamedFileNameOrTitle = UsePath.compExtend(
            inputEditable.toString(),
            compExtend
        )
        if(
            fileNameOrTitle == renamedFileNameOrTitle
        ) return
        val filePathOrConObj = File(filePathOrCon)
        val isWithFileRename = filePathOrConObj.isFile
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
        ListIndexForEditAdapter.replaceListElementForTsv(
            editFragment,
            srcAndRepLinePairList
        )
    }

    fun getTitleFileNameAndPathConPair(
        tsvLine: String
    ): Pair<String, String>? {
        val titleConList = tsvLine.split("\t")
        val fileNameOrTitle = titleConList.firstOrNull()
            ?: return null
        val filePathOrCon = titleConList.getOrNull(1)
            ?: return null
        return fileNameOrTitle to filePathOrCon
    }

    private fun dismissProcess(
        editFragment: EditFragment
    ){
        renamePromptDialog?.dismiss()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(200)
                editFragment.disableKeyboardFragmentChange = false
            }
        }
    }

}