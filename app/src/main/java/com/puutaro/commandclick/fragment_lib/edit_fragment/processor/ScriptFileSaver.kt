package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents

class ScriptFileSaver(
    private val editFragment: EditFragment,
) {
    fun save(
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>? = null,
    ){
        if(
            editFragment.existIndexList
        ) return
        val editedTextContents = EditedTextContents(
            editFragment,
        )
        val editedShellContentsList = editedTextContents.updateByCommandVariables(
            editFragment.currentScriptContentsList,
            recordNumToMapNameValueInCommandHolder,
        )
        editedTextContents.save(
            editedShellContentsList,
        )
    }
}