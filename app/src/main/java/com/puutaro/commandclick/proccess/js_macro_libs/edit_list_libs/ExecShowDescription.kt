package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.ItemPathMaker
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object ExecShowDescription {
    fun desc(
        fragment: Fragment,
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
        val showFilePath = ItemPathMaker.make(
            editComponentListAdapter,
            listIndexPosition,
        ) ?: return
        val showFilePathObj = File(showFilePath)
//        val showFileParentDirPath = showFilePathObj.parent
//            ?: return
        val showFileName = showFilePathObj.name
        ScriptFileDescription.show(
            fragment,
            ReadText(
                File(
                    showFilePath
                ).absolutePath
            ).textToList(),
//            showFileParentDirPath,
            showFileName
        )
    }
}