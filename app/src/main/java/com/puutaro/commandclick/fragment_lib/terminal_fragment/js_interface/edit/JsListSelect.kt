package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
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
        ListContentsSelectBoxTool.updateListFileCon(
            targetListFilePath,
            itemText
        )
    }

    @JavascriptInterface
    fun initListFile(
        targetListFilePath: String,
        itemTextListCon: String
    ){
        ListContentsSelectBoxTool.compListFile(
            targetListFilePath,
            itemTextListCon
        )
    }

    @JavascriptInterface
    fun removeItemInListFileCon(
        targetListFilePath: String,
        itemText: String
    ){
        if(
            itemText == escapeCharHyphen
            || itemText.isEmpty()
        ) return
        val listFileObj = File(targetListFilePath)
        val searchListDirPath = listFileObj.parent
            ?: return
        FileSystems.createDirs(searchListDirPath)
        val listContentsList = ReadText(
            targetListFilePath
        ).textToList()
        val findSearchText = listContentsList.find {
            it == itemText
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
            targetListFilePath
        ).textToList().filter {
            it.isNotEmpty()
                    && it != escapeCharHyphen
                    && it != itemText
        }.joinToString("\n")
        FileSystems.writeFile(
            targetListFilePath,
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
        val recentItem =  ReadText(
            targetListFilePath
        ).textToList().firstOrNull() ?: String()

        val jsScript = JsScript(terminalFragment)
        val scriptContents = ReadText(
            currentScriptPath
        ).readText()
        val replacedScriptContentsTargetVariable = if(
            replaceTargetVariable.isEmpty()
        ) scriptContents
        else jsScript.replaceCommandVariable(
                scriptContents,
                "${replaceTargetVariable}=\"${recentItem}\"",
            )
        val replacedScriptContents = if(
            defaultVariable.isEmpty()
        ) replacedScriptContentsTargetVariable
        else jsScript.replaceCommandVariable(
            replacedScriptContentsTargetVariable,
                "${defaultVariable}=",
            )
        FileSystems.writeFile(
            currentScriptPath,
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