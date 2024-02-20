package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.ItemPathMaker
import java.io.File

object ExecCopyPath {
    fun copyPath(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val context = editFragment.context
            ?: return
        val copyPath = ItemPathMaker.make(
            editFragment,
            selectedItem,
            listIndexPosition,
        ) ?: return
        if(
            !File(copyPath).isFile
        ) return
        val clipboard = context.getSystemService(
            Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(
            "cmdclick path",
            copyPath
        )
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            "copy ok ${copyPath}",
            Toast.LENGTH_SHORT
        ).show()
    }
}