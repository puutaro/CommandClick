package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.button.JsPathForEditButton
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsIconSelectBox(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun launch(
        valName: String,
        listPath: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
//        val jsEdit = JsEdit(terminalFragmentRef)
//        val currentItem = jsEdit.getFromEditText(valName)
//        val listSrc = readListPath(listPath)
//        val sortedListSrc = sortByCurrentItem(
//            currentItem,
//            listSrc,
//        )
//        val selectItem = ListJsDialog(terminalFragmentRef).create(
//            "",
//            "",
//            sortedListSrc,
//        )
//        if(
//            selectItem.trim().isEmpty()
//        ) return
//        jsEdit.updateEditText(
//            valName,
//            selectItem
//        )
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
            val nameIconNameList = QuoteTool.splitBySurroundedIgnore(
                it,
                nameIconNameSeparator
            )
//                it.split(nameIconNameSeparator)
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
                ReadText(
                    listPath
                ).readText().replace("\n", "\t")
            }
        }
    }


}