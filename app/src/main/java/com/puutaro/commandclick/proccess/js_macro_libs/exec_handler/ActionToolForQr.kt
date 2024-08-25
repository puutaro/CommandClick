package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import android.content.Context
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object ActionToolForQr {

    fun getParentDirPath(
        editFragment: EditFragment
    ): String {
        return ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexAdapter.indexListMap,
            ListIndexAdapter.listIndexTypeKey
        )
    }

    fun getContents(
        context: Context?,
        parentDirPath: String,
        clickFileName: String,
    ): String? {
        val clickFilePathObj = File(parentDirPath, clickFileName)
        if(
            !clickFilePathObj.isFile
        ){
            return null
        }
        return ReadText(
            clickFilePathObj.absolutePath
        ).readText()
    }
}