package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class JsDirSelect(
    private val terminalFragment: TerminalFragment
) {
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var confirmDialog: Dialog? = null

    @JavascriptInterface
    fun execEditDirName(
        targetVariable: String,
        renameVariable: String,
        targetDirPath: String,
        settingVariables: String,
        commandVariables: String,
        scriptFilePath: String,
        title: String,
    ){
        val replaceContents = JsDialog(terminalFragment).formDialog(
            title,
            settingVariables,
            commandVariables,
        )
        val replaceContentsList = replaceContents.split("\n")
        val editDirNameForDialog = replaceContentsList.filter {
            it.contains(targetVariable)
        }
            .firstOrNull()
            ?.split('=')
            ?.lastOrNull()
            .let { QuoteTool.trimBothEdgeQuote(it) }
        val renameDirNameKeyValue = replaceContentsList.filter {
            it.contains(renameVariable)
        }.firstOrNull() ?: return
        if(renameDirNameKeyValue.isEmpty()) return
        val renameDirNameForDialog = renameDirNameKeyValue
            .split("=")
            .lastOrNull()
            .let { QuoteTool.trimBothEdgeQuote(it) }
        val scriptFileObj = File(scriptFilePath)
        val parentDirPath = scriptFileObj.parent ?: return
        val scriptFileName = scriptFileObj.name
        when(
            renameDirNameForDialog
        ){
            String() -> {
                terminalViewModel.onDialog = true
                runBlocking {
                    withContext(Dispatchers.Main){
                        execDeleteFilePath(
                            parentDirPath,
                            scriptFileName,
                            targetDirPath,
                            editDirNameForDialog,
                            targetVariable,
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
                    editDirNameForDialog,
                    targetVariable,
                    renameDirNameForDialog,
                )
            }
        }
    }

    private fun execDeleteFilePath(
        parentDirPath: String,
        scriptFileName: String,
        targetDirPath: String,
        editDirNameForDialog: String,
        targetVariable: String,
    ){
        val context = terminalFragment.context
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
        confirmContentTextView?.text = editDirNameForDialog
        val confirmCancelButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        setOkButton(
            targetVariable,
            targetDirPath,
            editDirNameForDialog,
            parentDirPath,
            scriptFileName,
        )
        confirmCancelButton?.setOnClickListener {
            confirmDialog?.dismiss()
            terminalViewModel.onDialog = false
        }
        confirmDialog?.setOnCancelListener {
            confirmDialog?.dismiss()
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
        editDirNameForDialog: String,
        parentDirPath: String,
        scriptFileName: String,
    ){
        val confirmOkButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            confirmDialog?.dismiss()
            val removeDirPath = "${targetDirPath}/${editDirNameForDialog}"
            val removeDirPathObj = File(removeDirPath)
            if(
                !removeDirPathObj.isDirectory
            ){
                ToastUtils.showLong("not exist editDirNameForDialog ${removeDirPath}")
                terminalViewModel.onDialog = false
                return@setOnClickListener
            }
            FileSystems.removeDir(
                removeDirPath
            )
            ToastUtils.showShort("delete ok")
            terminalViewModel.onDialog = false
            val recentDirName = FileSystems.showDirList(targetDirPath).filter {
                File(it).isDirectory
            }.lastOrNull() ?: return@setOnClickListener
            updateScriptFile(
                parentDirPath,
                scriptFileName,
                "${targetVariable}=\"${recentDirName}\""
            )
            JsEdit(terminalFragment).updateEditText(
                targetVariable,
                recentDirName
            )
            terminalViewModel.onDialog = false
        }
    }

    private fun renameFilePath(
        parentDirPath: String,
        scriptFileName: String,
        targetDirPath: String,
        editDirNameForDialog: String,
        targetVariable: String,
        renameDirNameForDialog: String,
    ) {
        if (
            editDirNameForDialog == renameDirNameForDialog
        ) {
            ToastUtils.showLong("rename dir is same current dir")
            return
        }
        val srcDirPath = "${targetDirPath}/${editDirNameForDialog}"
        val destiDirPath = "${targetDirPath}/${renameDirNameForDialog}"
        FileSystems.copyDirectory(
            srcDirPath,
            destiDirPath
        )
        FileSystems.removeDir(
            srcDirPath
        )
        updateScriptFile(
            parentDirPath,
            scriptFileName,
            "${targetVariable}=\"${renameDirNameForDialog}\""
        )
        JsEdit(terminalFragment).updateEditText(
            targetVariable,
            renameDirNameForDialog
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
            ToastUtils.showLong("no exist : ${scriptFileName}")
            return
        }
        val jsScript = JsScript(terminalFragment)
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
}