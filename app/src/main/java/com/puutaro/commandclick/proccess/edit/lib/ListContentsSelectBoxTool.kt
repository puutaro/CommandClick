package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

object ListContentsSelectBoxTool {

    private const val escapeCharHyphen = "-"
    private const val buttonTagSeparator = "&"

    fun saveListContents(
        editFragment: EditFragment,
        currentButtonTag: String?
    ){
        val saveTextCon = "\${CMDCLICK_TEXT_CONTENTS}"
        if(
            currentButtonTag.isNullOrEmpty()
        ) return
        val saveTagsKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveTags.name
        val listContentsMap = editFragment.listConSelectBoxMapList.firstOrNull {
            if(
                it.isNullOrEmpty()
            ) return@firstOrNull false
            val saveTagKeyList = it.get(saveTagsKey)?.split(buttonTagSeparator)
            if(
                saveTagKeyList.isNullOrEmpty()
            ) return@firstOrNull false
            saveTagKeyList.contains(currentButtonTag)
        }
        if(
            listContentsMap.isNullOrEmpty()
        ) return
        val saveTargetListFilePath =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
            )
        if(
            saveTargetListFilePath.isNullOrEmpty()
        ) return
        val saveValName =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveValName.name
            )
        if(
            saveValName.isNullOrEmpty()
        ) return
        val saveFilterShellPath =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveFilterShellPath.name
            )
        val saveValue = EditVariableName.getText(
            editFragment,
            saveValName
        )
        val filterSaveValue = when(saveFilterShellPath.isNullOrEmpty()) {
            true -> return
            else -> {
                val saveFilterShellPathObj = File(saveFilterShellPath)
                val shellParentDirPath = saveFilterShellPathObj.parent
                    ?: return
                editFragment.busyboxExecutor?.getCmdOutput(
                    ReadText(
                        shellParentDirPath,
                        saveFilterShellPathObj.name
                    ).readText().replace(
                        saveTextCon,
                        saveValue
                    )
                )
            }
        }
        if(
            filterSaveValue.isNullOrEmpty()
        ) return
        updateListFileCon(
            saveTargetListFilePath,
            filterSaveValue
        )
    }

    fun updateListFileCon(
        targetListFilePath: String,
        itemText: String
    ) {
        if(
            itemText == escapeCharHyphen
            || itemText.isEmpty()
        ) return
        val listFileObj = File(targetListFilePath)
        val searchListDirPath = listFileObj.parent
            ?: return
        val searchListFileName = listFileObj.name
        FileSystems.createDirs(searchListDirPath)
        val listContentsList = ReadText(
            searchListDirPath,
            searchListFileName
        ).textToList()
        val findSearchText = listContentsList.find {
            it == itemText
        }
        val lastListContentsSourceList = makeUpdatedListCon(
            findSearchText,
            listContentsList,
            itemText
        )
        val lastListContents = lastListContentsSourceList.filter {
            it.isNotEmpty()
                    || it != escapeCharHyphen
        }.joinToString("\n")
        FileSystems.writeFile(
            searchListDirPath,
            searchListFileName,
            lastListContents
        )
    }


    private fun makeUpdatedListCon(
        findSearchText: String?,
        listContentsList: List<String>,
        itemText: String
    ): List<String> {
        if(
            !findSearchText.isNullOrEmpty()
        ) {
            return listOf(itemText) + listContentsList.filter {
                it != itemText
            }
        }
        return listOf(itemText) + listContentsList
    }

    fun compListFile(
        targetListFilePath: String,
        itemTextListCon: String
    ){
        if(
            itemTextListCon.isEmpty()
        ) return
        val itemTextList = itemTextListCon.split("\n")
        val targetListFilePathObj = File(targetListFilePath)
        val targetListParentDirPath = targetListFilePathObj.parent
            ?: return
        val targetListFileName = targetListFilePathObj.name
        val currentListConList = ReadText(
            targetListParentDirPath,
            targetListFileName
        ).textToList()
        val registerItemList = itemTextList.filter {
            !currentListConList.contains(it)
        }
        val registerListConList = registerItemList + currentListConList
        FileSystems.writeFile(
            targetListParentDirPath,
            targetListFileName,
            registerListConList.joinToString("\n")
        )
    }

}

enum class SaveTagForListContents(
    val tag: String
) {
    OK("ok")
}