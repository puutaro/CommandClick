package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object ActionToolForQr {

    fun getCurrentAppDirPath(
        readSharePreferenceMap: Map<String, String>
    ): String {
        return SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
    }

    fun getParentDirPath(
        editFragment: EditFragment
    ): String {
        return ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
    }

    fun getContents(
        context: Context?,
        parentDirPath: String,
        clickFileName: String,
    ): String? {
        val clicFilePathObj = File(parentDirPath, clickFileName)
        if(
            !clicFilePathObj.isFile
        ){
            Toast.makeText(
                context,
                "no File con",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        return ReadText(
            clicFilePathObj.absolutePath
        ).readText()
    }
}