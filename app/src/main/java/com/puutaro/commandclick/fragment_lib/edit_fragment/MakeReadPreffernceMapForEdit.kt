package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FragmentTagManager

object MakeReadPreffernceMapForEdit {
    fun make(
        editFragment: EditFragment
    ):  Map<String, String> {
        val prefixDirScriptSuffixList = FragmentTagManager.makeListFromTag(
            editFragment.tag as String,
        )
        return mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to prefixDirScriptSuffixList.get(
                FragmentTagManager.parentAppDirPathIndex
            ),
            SharePrefferenceSetting.current_script_file_name.name
                    to prefixDirScriptSuffixList.get(
                FragmentTagManager.scriptFileNameIndex
            ),
            SharePrefferenceSetting.on_shortcut.name
                    to prefixDirScriptSuffixList.get(
                FragmentTagManager.modeIndex
            ),
        )
    }
}