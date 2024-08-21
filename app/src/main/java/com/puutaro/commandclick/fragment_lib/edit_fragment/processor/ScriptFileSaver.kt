package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SettingFannelConHandlerForEdit
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

class ScriptFileSaver(
    private val editFragment: EditFragment,
) {
    private val fannelInfoMap = editFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    fun save(){
        val editedTextContents = EditedTextContents(
            editFragment,
        )
        val isSettingEdit = !IsCmdEdit.judge(editFragment)
        val editedShellContentsList = when(
            isSettingEdit
        ) {
            true -> {
                val settingFannelConList = SettingFannelConHandlerForEdit.handle(
                    editFragment
                )
                editedTextContents.updateBySettingVariables(
                    settingFannelConList,
                    editFragment.recordNumToMapNameValueInSettingHolder,
                )
            }

            else -> {
                val currentFannelConList = ReadText(
                    File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath
                ).textToList()
                editedTextContents.updateByCommandVariables(
                    currentFannelConList,
                    editFragment.recordNumToMapNameValueInCommandHolder,
                )
            }
        }
        editedTextContents.save(
            editedShellContentsList,
            isSettingEdit
        )
    }
}
