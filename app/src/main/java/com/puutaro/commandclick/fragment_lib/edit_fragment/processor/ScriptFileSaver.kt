package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SettingFannelConHandlerForEdit
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object ScriptFileSaver{
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    fun save(
        editFragment: EditFragment
    ){
        val fannelInfoMap = editFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
//        val editedTextContents = EditedTextContents(
//            editFragment,
//        )
        val isSettingEdit = !IsCmdEdit.judge(editFragment)
        val editedShellContentsList = when(
            isSettingEdit
        ) {
            true -> {
                val settingFannelConList = SettingFannelConHandlerForEdit.handle(
                    editFragment
                )
                EditedTextContents.updateBySettingVariables(
                    editFragment,
                    settingFannelConList,
                    editFragment.recordNumToMapNameValueInSettingHolder,
                )
            }

            else -> {
                val currentFannelConList = ReadText(
                    File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath
                ).textToList()
                EditedTextContents.updateByCommandVariables(
                    editFragment,
                    currentFannelConList,
                    editFragment.recordNumToMapNameValueInCommandHolder,
                )
            }
        }
        EditedTextContents.save(
            editFragment,
            currentFannelName,
            editedShellContentsList,
            isSettingEdit
        )
    }
}
