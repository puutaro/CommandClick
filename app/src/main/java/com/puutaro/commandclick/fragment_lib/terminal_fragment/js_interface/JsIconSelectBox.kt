package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ListJsDialog
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.button.JsPathForEditButton
import com.puutaro.commandclick.util.ReadText
import java.io.File

class JsIconSelectBox(
    private val terminalFragment: TerminalFragment
) {
    private val listJsDialog = ListJsDialog(terminalFragment)

    @JavascriptInterface
    fun launch(
        valName: String,
        listPath: String,
    ){
        val jsEdit = JsEdit(terminalFragment)
        val currentItem = jsEdit.getFromEditText(valName)
        val listSrc = readListPath(listPath)
        val sortedListSrc = sortByCurrentItem(
            currentItem,
            listSrc,
        )
        val selectItem = listJsDialog.create(
            "",
            "",
            sortedListSrc,
        )
        if(
            selectItem.trim().isEmpty()
        ) return
        jsEdit.updateEditText(
            valName,
            selectItem
        )
    }

    private fun sortByCurrentItem(
        currentItem: String,
        listSrc: String,
    ): String {

        val nameIconNameSeparator = ListJsDialog.nameIconNameSeparator
        val targetList = listSrc.split("\t").filter {
            it.trim().isNotEmpty()
        }
        val currentSelectedElement = targetList.filter {
            it.trim().isNotEmpty()
        }.firstOrNull {
            val nameIconNameList = it.split(nameIconNameSeparator)
            val itemName = nameIconNameList.first().trim()
            itemName == currentItem
        }?.trim() ?: return listSrc
        val sortedList =
            targetList.filter {
                it.trim() != currentSelectedElement
            } + listOf(currentSelectedElement)
        return sortedList.joinToString("\t")
    }

    private fun readListPath(listPath: String?): String {
        if(listPath.isNullOrEmpty()) return String()
        return when(listPath){
            JsPathForEditButton.ListPathMacroForEditButton.ICON_LIST.name -> {
                CmdClickIcons.values().map {
                    listOf(
                        it.str,
                        it.str
                    ).joinToString(JsPathForEditButton.buttonIconNameIdSeparator)
                }.joinToString(JsPathForEditButton.buttonIconSeparator)
            }
            else -> {
                val listPathObj = File(listPath)
                val parentDirPath = listPathObj.parent
                    ?: return String()
                ReadText(
                    parentDirPath,
                    listPathObj.name
                ).readText().replace("\n", "\t")
            }
        }
    }


}