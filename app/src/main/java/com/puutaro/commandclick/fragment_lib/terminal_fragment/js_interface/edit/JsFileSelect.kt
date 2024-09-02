package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class JsFileSelect(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private val noSuffixMacroWord = FileSelectSpinnerViewProducer.noExtend
    private val totalExtendRegex = Regex("\\.[a-zA-Z0-9]*$")
    private var confirmDialog: Dialog? = null

    @JavascriptInterface
    fun execEditTargetFileName(
        targetVariable: String,
        renameVariable: String,
        targetDirPath: String,
        settingVariables: String,
        commandVariables: String,
        prefix: String,
        suffix: String,
        scriptFilePath: String,
        title: String,
    ){
        val replaceContents = JsDialog(terminalFragmentRef).formDialog(
            title,
            settingVariables,
            commandVariables,
        )

        val replaceContentsList = replaceContents.split("\n")
        val editFileNameForDialog = replaceContentsList.filter {
            it.contains(targetVariable)
        }
            .firstOrNull()
            ?.split('=')
            ?.lastOrNull()
            .let { QuoteTool.trimBothEdgeQuote(it) }
        val renameFileNameKeyValue = replaceContentsList.filter {
            it.contains(renameVariable)
        }.firstOrNull() ?: return
        if(renameFileNameKeyValue.isEmpty()) return
        val renameFileNameForDialog = renameFileNameKeyValue
            .split("=")
            .lastOrNull()
            .let { QuoteTool.trimBothEdgeQuote(it) }
        val scriptFileObj = File(scriptFilePath)
        val parentDirPath = scriptFileObj.parent ?: return
        val scriptFileName = scriptFileObj.name
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        when(
            renameFileNameForDialog
        ){
            String() -> {
                terminalViewModel.onDialog = true
                runBlocking {
                    withContext(Dispatchers.Main) {
                        execDeleteFilePath(
                            parentDirPath,
                            scriptFileName,
                            targetDirPath,
                            editFileNameForDialog,
                            targetVariable,
                            prefix,
                            suffix,
                        )
                    }
                    withContext(Dispatchers.IO){
                        for(i in 1..2000){
                            delay(100)
                            if(!terminalViewModel.onDialog) break
                        }
                    }
                }
            }
            else -> {
                renameFilePath(
                    parentDirPath,
                    scriptFileName,
                    targetDirPath,
                    editFileNameForDialog,
                    targetVariable,
                    renameFileNameForDialog,
                    prefix,
                    suffix
                )
            }
        }
    }

    private fun execDeleteFilePath(
        parentDirPath: String,
        scriptFileName: String,
        targetDirPath: String,
        editFileNameForDialog: String,
        targetVariable: String,
        prefix: String,
        suffix: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        if(
            context == null
        ) {
            terminalViewModel.onDialog = false
            return
        }

        confirmDialog = Dialog(
            context
        )
        confirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            confirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = "delete ok?"
        val confirmContentTextView =
            confirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = editFileNameForDialog
        val confirmCancelButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        setOkButton(
            targetVariable,
            targetDirPath,
            prefix,
            suffix,
            editFileNameForDialog,
            parentDirPath,
            scriptFileName
        )
        confirmCancelButton?.setOnClickListener {
            confirmDialog?.dismiss()
            confirmDialog = null
            terminalViewModel.onDialog = false
        }
        confirmDialog?.setOnCancelListener {
            confirmDialog?.dismiss()
            confirmDialog = null
            terminalViewModel.onDialog = false
        }
        confirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        confirmDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        confirmDialog?.show()
    }

    private fun setOkButton(
        targetVariable: String,
        targetDirPath: String,
        prefix: String,
        suffix: String,
        editFileNameForDialog: String,
        parentDirPath: String,
        scriptFileName: String,
    ){
        val confirmOkButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        val terminalFragment = terminalFragmentRef.get() ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        confirmOkButton?.setOnClickListener {
            confirmDialog?.dismiss()
            confirmDialog = null
            if(
                !File(
                    targetDirPath,
                    editFileNameForDialog
                ).isFile
            ){
                ToastUtils.showLong("not exist editFileNameForDialog ${editFileNameForDialog}")
                terminalViewModel.onDialog = false
                return@setOnClickListener
            }
            FileSystems.removeFiles(
                File(
                    targetDirPath,
                    editFileNameForDialog
                ).absolutePath
            )
            ToastUtils.showShort("delete ok")
            terminalViewModel.onDialog = false
            val recentLogFile = FileSystems.sortedFiles(
                targetDirPath
            ).filter {
                val onPrefix = it.startsWith(prefix)
                val onSuffix = if(
                    suffix == noSuffixMacroWord
                ){
                    !totalExtendRegex.containsMatchIn(it)
                } else it.endsWith(suffix)
                onPrefix && onSuffix
            }.lastOrNull() ?: return@setOnClickListener
            updateScriptFile(
                parentDirPath,
                scriptFileName,
                "${targetVariable}=\"${recentLogFile}\""
            )
            JsEdit(terminalFragmentRef).updateEditText(
                targetVariable,
                recentLogFile
            )

        }
    }

    private fun renameFilePath(
        parentDirPath: String,
        scriptFileName: String,
        targetDirPath: String,
        editFileNameForDialog: String,
        targetVariable: String,
        renameFileNameForDialog: String,
        prefix: String,
        suffix: String
    ){
        val renameFileNameOkForDialog = makeRenameFileNameOkForDialog(
            renameFileNameForDialog,
            prefix,
            suffix
        )
        if(
            editFileNameForDialog == renameFileNameOkForDialog
        ){
            ToastUtils.showLong("rename file is same current file")
            return
        }
        FileSystems.copyFile(
            "${targetDirPath}/${editFileNameForDialog}",
            "${targetDirPath}/${renameFileNameOkForDialog}",
        )
        FileSystems.removeFiles(
            File(
                targetDirPath,
                editFileNameForDialog
            ).absolutePath
        )
        updateScriptFile(
            parentDirPath,
            scriptFileName,
            "${targetVariable}=\"${renameFileNameOkForDialog}\""
        )
        JsEdit(terminalFragmentRef).updateEditText(
            targetVariable,
            renameFileNameOkForDialog
        )
    }

    private fun updateScriptFile(
        parentDirPath: String,
        scriptFileName: String,
        replaceString: String
    ){
        val scriptFilePathObj = File(
            parentDirPath,
            scriptFileName
        )
        if(
            !scriptFilePathObj.isFile
        ) {
            ToastUtils.showLong("no exist: ${scriptFileName}")
            return
        }
        val jsScript = JsScript(terminalFragmentRef)
        val scriptContents = ReadText(
            scriptFilePathObj.absolutePath
        ).readText()
        val replaceContents = jsScript.replaceCommandVariable(
            scriptContents,
            replaceString,
        )
        FileSystems.writeFile(
            scriptFilePathObj.absolutePath,
            replaceContents
        )
    }

    private fun makeRenameFileNameOkForDialog(
        renameFileNameForDialog: String,
        prefix: String,
        suffix: String
    ): String {
        val renameFileNameOkForDialogSource = UsePath.compPrefix(
            renameFileNameForDialog,
            prefix
        )
        if(
            suffix == noSuffixMacroWord
        ) return renameFileNameOkForDialogSource
            .replace(totalExtendRegex, "")
        return UsePath.compExtend(
            renameFileNameOkForDialogSource,
            suffix
        )
    }
}