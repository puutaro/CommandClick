package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class JsDirSelect(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

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
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "delete ok?"
            )
            .setMessage(" ${editDirNameForDialog}")
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->
                val removeDirPath = "${targetDirPath}/${editDirNameForDialog}"
                val removeDirPathObj = File(removeDirPath)
                if(
                    !removeDirPathObj.isDirectory
                ){
                    Toast.makeText(
                        context,
                        "not exist editDirNameForDialog ${removeDirPath}",
                        Toast.LENGTH_LONG
                    ).show()
                    terminalViewModel.onDialog = false
                    return@OnClickListener
                }
                FileSystems.removeDir(
                    removeDirPath
                )
                terminalViewModel.onDialog = false
                val recentDirName = FileSystems.showDirList(targetDirPath).filter {
                    File(it).isDirectory
                }.lastOrNull() ?: return@OnClickListener
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
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener {
                    dialog, which ->
                terminalViewModel.onDialog = false
            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(R.color.black)
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
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
            Toast.makeText(
                context,
                "rename dir is same current dir",
                Toast.LENGTH_LONG
            ).show()
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
        if(
            !File(
                parentDirPath,
                scriptFileName
            ).isFile
        ) {
            Toast.makeText(
                context,
                "no exist: ${scriptFileName}",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val jsScript = JsScript(terminalFragment)
        val scriptContents = ReadText(
            parentDirPath,
            scriptFileName
        ).readText()
        val replaceContents = jsScript.replaceComamndVariable(
            scriptContents,
            replaceString,
        )
        FileSystems.writeFile(
            parentDirPath,
            scriptFileName,
            replaceContents
        )
    }
}