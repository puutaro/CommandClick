package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.ItemPathMaker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.dialog.DialogObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecItemCat {

    fun cat(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val context = editFragment.context
            ?: return
        val catPath = ItemPathMaker.make(
            editFragment,
            selectedItem,
            listIndexPosition,
        ) ?: return
        val scriptContents = ReadText(
            catPath
        ).readText()
        val displayContents = "\tpath: ${catPath}" +
                "\n---\n${scriptContents}"
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                DialogObject.simpleTextShow(
                    context,
                    "Show contents",
                    displayContents
                )
            }
        }
    }
}