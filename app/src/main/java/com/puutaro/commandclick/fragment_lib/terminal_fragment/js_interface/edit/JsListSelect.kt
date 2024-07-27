package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

class JsListSelect(
    private val terminalFragment: TerminalFragment
) {
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
        /*
        Complete list contents select box with item test list con (item separated by newline)

        ### targetListFilePath arg
        list (item separated by newline) file list contents select box

        ### itemTextListCon

        item test list con (item separated by newline)

        */

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
            ToastUtils.showShort("no exist itemText: $itemText")
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
        replaceTargetVariable: String,
        defaultVariable: String
    ){
        /*
        Wrap remove item from list contents in select box and set recent item to one

        ### targetListFilePath arg
        Remove target list file path in list contents select box

        ### removeTargetItem
        Remove target item

        ### currentScriptPath
        current fannel path

        ### replaceTargetVariable
        Set recent item to here variable name's edit text

        ### replaceTargetVariable
        Set blank value to here variable name's edit text
        */

        val searchListFileObj = File(
            targetListFilePath
        )
        if(
            !searchListFileObj.isFile
        ) {
            ToastUtils.showLong("no exist targetListFilePath ${targetListFilePath}")
            return
        }
        if(
            removeTargetItem.isEmpty()
        ) {
            ToastUtils.showShort("blank removeTargetItem")
            return
        }
        val currentScriptObj = File(
            currentScriptPath
        )
        if(
            !currentScriptObj.isFile
        ) {
            ToastUtils.showLong("no exist currentScriptPath $currentScriptPath")
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