package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

class JsFileSelect(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val suffixMacroWord = FileSelectSpinnerViewProducer.noExtend

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
        val replaceContents = JsDialog(terminalFragment).formDialog(
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
            .let { BothEdgeQuote.trim(it) }
        val renameFileNameKeyValue = replaceContentsList.filter {
            it.contains(renameVariable)
        }.firstOrNull() ?: return
        if(renameFileNameKeyValue.isEmpty()) return
        val renameFileNameForDialog = renameFileNameKeyValue
            .split("=")
            .lastOrNull()
            .let { BothEdgeQuote.trim(it) }
        val scriptFileObj = File(scriptFilePath)
        val parentDirPath = scriptFileObj.parent ?: return
        val scriptFileName = scriptFileObj.name
        when(
            renameFileNameForDialog
        ){
            String() -> {
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
        taergetDirPath: String,
        editFileNameForDialog: String,
        targetVariable: String,
        prefix: String,
        suffix: String
    ){
        val context = terminalFragment.context
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "delete ok?"
            )
            .setMessage(" ${editFileNameForDialog}")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                if(
                    !File(
                        taergetDirPath,
                        editFileNameForDialog
                    ).isFile
                ){
                    Toast.makeText(
                        context,
                        "not exist ${editFileNameForDialog}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@OnClickListener
                }
                FileSystems.removeFiles(
                    taergetDirPath,
                    editFileNameForDialog
                )
                val recentLogFile = FileSystems.sortedFiles(
                    taergetDirPath
                ).filter {
                    val onPrefix = it.startsWith(prefix)
                    val onSuffix = if(
                        suffix == suffixMacroWord
                    ){
                        !Regex("\\.[a-zA-Z0-9]*$").containsMatchIn(it)
                    } else it.endsWith(suffix)
                    onPrefix && onSuffix
                }.lastOrNull() ?: return@OnClickListener
                updateScriptFile(
                    parentDirPath,
                    scriptFileName,
                    "${targetVariable}=\"${recentLogFile}\""
                )
            })
            .setNegativeButton("NO", null)
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(R.color.black)
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun renameFilePath(
        parentDirPath: String,
        scriptFileName: String,
        taergetDirPath: String,
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
            Toast.makeText(
                context,
                "rename file is same current file",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        FileSystems.copyFile(
            "${taergetDirPath}/${editFileNameForDialog}",
            "${taergetDirPath}/${renameFileNameOkForDialog}",
        )
        FileSystems.removeFiles(
            taergetDirPath,
            editFileNameForDialog
        )
        updateScriptFile(
            parentDirPath,
            scriptFileName,
            "${targetVariable}=\"${renameFileNameOkForDialog}\""
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
        val jsIntent = JsIntent(terminalFragment)
        jsIntent.launchShortcut(
            parentDirPath,
            scriptFileName
        )
    }

    private fun makeRenameFileNameOkForDialog(
        renameFileNameForDialog: String,
        prefix: String,
        suffix: String
    ): String {
        val renameFileNameOkForDialogSource = if(
            renameFileNameForDialog.startsWith(prefix)
        ) renameFileNameForDialog
        else "${prefix}${renameFileNameForDialog}"
        return if(
            suffix == suffixMacroWord
        ) renameFileNameOkForDialogSource
        else if(
            renameFileNameOkForDialogSource.endsWith(suffix)
        ) renameFileNameOkForDialogSource
        else "${renameFileNameOkForDialogSource}${suffix}"
    }

}