package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import android.content.Context
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.ItemPathMaker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.dialog.DialogObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecItemCat {

    fun cat(
        context: Context?,
        editConstraintListAdapter: EditConstraintListAdapter,
        listIndexPosition: Int,
    ){
        if(
            context == null
        ) return
        val catPath = ItemPathMaker.make(
            editConstraintListAdapter,
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