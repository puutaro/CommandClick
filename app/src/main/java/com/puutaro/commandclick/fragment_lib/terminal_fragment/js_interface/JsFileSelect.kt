package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

class JsFileSelect(
    private val terminalFragment: TerminalFragment
) {

    private val jsScript = JsScript(terminalFragment)
    private val jsIntent = JsIntent(terminalFragment)
    private val context = terminalFragment.context

    @JavascriptInterface
    fun execEditTargetFileName(
        targetVariable: String,
        renameVariable: String,
        taergetDirPath: String,
        settingVariables: String,
        commandVariables: String,
        prefix: String,
        scriptFilePath: String
    ){
        val replaceContents = JsDialog(terminalFragment).formDialog(
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
                    taergetDirPath,
                    editFileNameForDialog,
                    targetVariable,
                )
            }
            else -> {
                renameFilePath(
                    parentDirPath,
                    scriptFileName,
                    taergetDirPath,
                    editFileNameForDialog,
                    targetVariable,
                    renameFileNameForDialog,
                    prefix
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
    ){
        val context = terminalFragment.context
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "delete ok?"
            )
            .setMessage(" : ${editFileNameForDialog}")
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
                    !Regex("\\.[a-zA-Z0-9]*$").containsMatchIn(it)
                }.firstOrNull() ?: return@OnClickListener
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
        prefix: String
    ){
        val renameFileNameOkForDialog = if(
            renameFileNameForDialog.startsWith(prefix)
        ) renameFileNameForDialog
        else "${prefix}${renameFileNameForDialog}"
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
        jsIntent.launchShortcut(
            parentDirPath,
            scriptFileName
        )
    }

}