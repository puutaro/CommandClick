package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object ListContentsSaverByTag {

    fun save(
        editFragment: EditFragment,
        currentButtonTagList: List<String?>
    ) {
        if (
            currentButtonTagList.isEmpty()
        ) return
        val saveTagsKey =
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveTags.name
        val listContentsMap = editFragment.listConSelectBoxMapList.firstOrNull {
            val saveTagName = it?.get(saveTagsKey)
                ?: return@firstOrNull false
            currentButtonTagList.contains(saveTagName)
        }
        if (
            listContentsMap.isNullOrEmpty()
        ) return
        val saveTargetListFilePath =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
            )
        if (
            saveTargetListFilePath.isNullOrEmpty()
        ) return
        val saveValName =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveValName.name
            )
        if (
            saveValName.isNullOrEmpty()
        ) return
        val saveFilterShellPath =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveFilterShellPath.name
            )
        val filterSaveValue = when (
            saveFilterShellPath.isNullOrEmpty()
        ) {
            true -> EditVariableName.getText(
                editFragment,
                saveValName.trim()
            )
            else -> makeShellConForListConSBFilter(
                editFragment,
                saveFilterShellPath,
                saveValName,
            )
        }
        if (
            filterSaveValue.isNullOrEmpty()
        ) return
        ListContentsSelectBoxTool.updateListFileCon(
            saveTargetListFilePath,
            filterSaveValue
        )
    }

    private fun makeShellConForListConSBFilter(
        editFragment: EditFragment,
        saveFilterShellPath: String,
        saveValName: String,
    ): String? {
        val saveTextCon = "\${CMDCLICK_TEXT_CONTENTS}"
        val saveFilterShellPathObj = File(saveFilterShellPath)
        val shellParentDirPath = saveFilterShellPathObj.parent
            ?: return null
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val saveValue = EditVariableName.getText(
            editFragment,
            saveValName
        )
        val shellCon = ReadText(
            File(
                shellParentDirPath,
                saveFilterShellPathObj.name
            ).absolutePath
        ).readText().replace(
            saveTextCon,
            saveValue
        ).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName
            )
        }
        return editFragment.busyboxExecutor?.getCmdOutput(shellCon)
    }
}