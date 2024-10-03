package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.ItemPathMaker
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object ExecShowDescription {
    fun desc(
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
        val showFilePath = ItemPathMaker.make(
            editFragment,
            listIndexPosition,
        ) ?: return
        val showFilePathObj = File(showFilePath)
//        val showFileParentDirPath = showFilePathObj.parent
//            ?: return
        val showFileName = showFilePathObj.name
        ScriptFileDescription.show(
            editFragment,
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