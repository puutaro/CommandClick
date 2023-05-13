package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

class JsListSelect(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val escapeCharHyphen = "-"


    @JavascriptInterface
    fun updateListFileCon(
        targetListFilePath: String,
        itemText: String
    ) {
        val trimSearchText = itemText.trim()
        if(
            trimSearchText == escapeCharHyphen
            || trimSearchText.isEmpty()
        ) return
        val listFileObj = File(targetListFilePath)
        val searchListDirPath = listFileObj.parent
            ?: return
        val searchListFileName = listFileObj.name
        FileSystems.createDirs(searchListDirPath)
        val listContentsList = ReadText(
            searchListDirPath,
            searchListFileName
        ).textToList()
        val findSearchText = listContentsList.find {
            it.trim() == trimSearchText
        }
        if(
            !findSearchText.isNullOrEmpty()
        ) return
        val lastListContentsSourceList =
            listOf(trimSearchText) + listContentsList
        val lastListContents = lastListContentsSourceList.filter {
            it.isNotEmpty()
                    || it != escapeCharHyphen
        }.joinToString("\n")
        FileSystems.writeFile(
            searchListDirPath,
            searchListFileName,
            lastListContents
        )

    }

    @JavascriptInterface
    fun removeItemInListFileCon(
        targetListFilePath: String,
        itemText: String
    ){
        val trimSearchText = itemText.trim()
        if(
            trimSearchText == escapeCharHyphen
            || itemText.isEmpty()
        ) return
        val listFileObj = File(targetListFilePath)
        val searchListDirPath = listFileObj.parent
            ?: return
        val searchListFileName = listFileObj.name
        FileSystems.createDirs(searchListDirPath)
        val listContentsList = ReadText(
            searchListDirPath,
            searchListFileName
        ).textToList()
        val findSearchText = listContentsList.find {
            it.trim() == trimSearchText
        }
        if(
            findSearchText.isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                "no exist itemText: $itemText",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val updateListContents = ReadText(
            searchListDirPath,
            searchListFileName,
        ).textToList().filter {
            val trimItem = it.trim()
            trimItem.isNotEmpty()
                    && trimItem != escapeCharHyphen
                    && trimItem != itemText
        }.joinToString("\n")
        FileSystems.writeFile(
            searchListDirPath,
            searchListFileName,
            updateListContents
        )
    }

    @JavascriptInterface
    fun wrapRemoveItemInListFileCon(
        targetListFilePath: String,
        removeTargetItem: String,
        currentScriptPath: String,
        replaceTargetVariable: String = String(),
        defaultVariable: String = String()
    ){
        val searchListFileObj = File(
            targetListFilePath
        )
        if(
            !searchListFileObj.isFile
        ) {
            Toast.makeText(
                context,
                "no exist targetListFilePath ${targetListFilePath}",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if(
            removeTargetItem.isEmpty()
        ) {
            Toast.makeText(
                context,
                "blank removeTargetItem",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val currentScriptObj = File(
            currentScriptPath
        )
        if(
            !currentScriptObj.isFile
        ) {
            Toast.makeText(
                context,
                "no exist currentScriptPath $currentScriptPath",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        removeItemInListFileCon(
            targetListFilePath,
            removeTargetItem
        )
        val searchListDirPath = searchListFileObj.parent
            ?: return
        val searchListFileName = searchListFileObj.name
        val recentItem =  ReadText(
            searchListDirPath,
            searchListFileName
        ).textToList().firstOrNull() ?: String()

        val jsScript = JsScript(terminalFragment)
        val currentAppDirPath = currentScriptObj.parent
            ?: return
        val scriptName = currentScriptObj.name
        val scriptContents = ReadText(
            currentAppDirPath,
            scriptName
        ).readText()
        val replacedScriptContentsTargetVariable = if(
            replaceTargetVariable.isEmpty()
        ) scriptContents
        else jsScript.replaceComamndVariable(
                scriptContents,
                "${replaceTargetVariable}=\"${recentItem}\"",
            )
        val replacedScriptContents = if(
            defaultVariable.isEmpty()
        ) replacedScriptContentsTargetVariable
        else jsScript.replaceComamndVariable(
            replacedScriptContentsTargetVariable,
                "${defaultVariable}=",
            )
        FileSystems.writeFile(
            currentAppDirPath,
            scriptName,
            replacedScriptContents
        )
        val jsEdit = JsEdit(terminalFragment)
        if(
            defaultVariable.isNotEmpty()
        ) jsEdit.updateEditText(
            defaultVariable,
            String(),
        )
        if(
            replaceTargetVariable.isNotEmpty()
        ) jsEdit.updateEditText(
            replaceTargetVariable,
            recentItem,
        )
    }
}