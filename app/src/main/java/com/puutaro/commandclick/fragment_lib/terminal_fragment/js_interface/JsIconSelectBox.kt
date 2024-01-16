package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ListJsDialog

class JsIconSelectBox(
    private val terminalFragment: TerminalFragment
) {
    private val listJsDialog = ListJsDialog(terminalFragment)

    @JavascriptInterface
    fun launch(
        valName: String,
        listSrc: String,
    ){
        val jsEdit = JsEdit(terminalFragment)
        val currentItem = jsEdit.getFromEditText(valName)
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
}