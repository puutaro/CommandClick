package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.*


class ShellFileInitManager {
    companion object {

        fun initDialog(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
            shellScriptName: String,
            cmdListAdapter: ArrayAdapter<String>,
            cmdListView: ListView
        ){

            val context = cmdIndexFragment.context

            val alertDialog = AlertDialog.Builder(context)
                .setTitle("init ok?")
                .setMessage("\tpath: ${shellScriptName}")
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, which ->
                    excInit(
                        cmdIndexFragment,
                        currentAppDirPath,
                        shellScriptName,
                        cmdListAdapter,
                        cmdListView
                    )
                })
                .setNegativeButton("NO", null)
                .show()
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context?.getColor(R.color.black) as Int
            )
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                context.getColor(R.color.black)
            )
            alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        }


        private fun excInit(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
            shellScriptName: String,
            cmdListAdapter: ArrayAdapter<String>,
            cmdListView: ListView
        ){

            val languageType =
                JsOrShellFromSuffix.judge(shellScriptName)
            val languageTypeToSectionHolderMap =
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
            val settingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
            ) as String
            val settingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
            ) as String

            val shellContentsList = if(
                shellScriptName == UsePath.cmdclickStartupJsName
            ) {
                CommandClickShellScript.makeAutoJsContents(
                    shellScriptName
                ).split("\n")
            } else {
                CommandClickShellScript.makeShellContents(
                    cmdIndexFragment.shiban,
                    shellScriptName,
                    CommandClickShellScript.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE,
                    languageType
                ).split("\n")
            }
            val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                settingSectionStart,
                settingSectionEnd
            )?.filter {
                val existSettingStartHolder =  (
                        it.startsWith(settingSectionStart)
                                && it.endsWith(settingSectionStart)
                        )
                val existSettingEndHolder =  (
                        it.startsWith(settingSectionEnd)
                                && it.endsWith(settingSectionEnd)
                        )
                !(existSettingStartHolder || existSettingEndHolder)
            } ?: return
            val settingVariableDefaultMap = settingVariableList.map {
                val equalIndex = it.indexOf("=")
                val variableName = if(equalIndex <= 0) {
                    it
                } else {
                    MakeSettingVariableNameOrValue.returnValidVariableName(
                        it,
                        equalIndex,
                        settingVariableList,
                    ) ?: String()
                }
                val variableValueStr = makeValueStrForMap(
                    it,
                    variableName,
                    equalIndex
                )
                variableName to variableValueStr
            }.toMap().filterKeys {
                it != CommandClickShellScript.SET_VARIABLE_TYPE
                        && it != CommandClickShellScript.AFTER_COMMAND
                        && it != CommandClickShellScript.BEFORE_COMMAND
            }

            var count_setting_section_start = 0
            var count_setting_section_end = 0
            val initedShellcontents = ReadText(
                currentAppDirPath,
                shellScriptName
            ).textToList().map {
                if(
                    it.startsWith(settingSectionStart)
                    && it.endsWith(settingSectionStart)
                ) count_setting_section_start++
               if(
                   it.startsWith(settingSectionEnd)
                   && it.endsWith(settingSectionEnd)
               ) count_setting_section_end++
               makeSettingVariableRow(
                   count_setting_section_start,
                   count_setting_section_end,
                   it,
                   settingVariableDefaultMap
               )
            }

            FileSystems.writeFile(
                currentAppDirPath,
                shellScriptName,
                initedShellcontents.joinToString("\n")
            )
            CommandListManager.execListUpdate(
                currentAppDirPath,
                cmdListAdapter,
                cmdListView,
            )
        }
    }
}


internal fun makeValueStrForMap(
    rowStr: String,
    variableName: String,
    equalIndex: Int
): String {
    if(equalIndex <= 0) return rowStr
    if(rowStr.length == equalIndex) return rowStr
    return if(
        variableName != CommandClickShellScript.SET_VARIABLE_TYPE
    ) {
        rowStr.substring(
            equalIndex + 1, rowStr.length
        )
    } else String()
}


internal fun makeSettingVariableRow(
    count_setting_section_start: Int,
    count_setting_section_end: Int,
    rowStr: String,
    settingVariableDefaultMap: Map<String, String>
): String {

    if(
        count_setting_section_start == 0
        || count_setting_section_start > 1
        || count_setting_section_end > 0
    ) return rowStr
    val equalIndex = rowStr.indexOf("=")
    if(equalIndex <= 0) return rowStr
    val variableName = rowStr.substring(
        0, equalIndex
    )

    if(
        !CommandClickShellScript.SETTING_VARIABLE_NAMES_LIST
            .contains(variableName)
    ) return rowStr

    val replaceValueStr =
        settingVariableDefaultMap.get(variableName)
    if(
        replaceValueStr == null
    ) return rowStr

    return listOf(
        variableName,
        replaceValueStr
    ).joinToString("=")
}