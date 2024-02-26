package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.view_model.activity.EditViewModel

object EditTextSetter {

    fun set(
        editParameters: EditParameters,
        editTextPropertyMap: Map<String, String>?,
        editTextWeight: Float
    ): EditText {
        val currentFragment = editParameters.currentFragment
        val editViewModel: EditViewModel by currentFragment.activityViewModels()
        val context = editParameters.context
        val currentId = editParameters.currentId
        val currentVariableValue = editParameters.currentVariableValue
        val currentVariableName = editParameters.currentVariableName

        val insertEditText = EditText(context)
        insertEditText.clearFocus()
        insertEditText.tag = currentVariableName
        insertEditText.id = currentId
        insertEditText.backgroundTintList = context?.getColorStateList(R.color.gray_out)
        editViewModel.variableNameToEditTextIdMap.put(
            currentVariableName as String,
            currentId
        )

        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        val editTextCon = EditTextMaker.make(
            currentFragment,
            editTextPropertyMap,
            currentVariableValue,
        )
        insertEditText.setText(editTextCon)

        editTextPropertyMap?.get(
            EditTextPropertySettingKey.SIZE.key
        )?.let {
            val textSize = try{it.toFloat()}catch (e: Exception){return@let }
            insertEditText.textSize = textSize
        }
        editTextPropertyMap?.get(
            EditTextPropertySettingKey.ON_UNDER_LINE.key
        )?.let {
            if(it != UnderLineValue.OFF.name) return@let
            insertEditText.setBackgroundResource(android.R.color.transparent)
        }
//        editTextPropertyMap?.get(
//            EditTextPropertySettingKey.ELLIP_SIZE.key
//        )?.let {
//                ellipKey ->
//            val trancate = EllipSizeSettingKey.values().firstOrNull {
//                it.key == ellipKey
//            }?.trancate
//            insertEditText.ellipsize = trancate
//        }
        insertEditText.setSelectAllOnFocus(true)
//        insertEditText.setTextColor(Color.parseColor("#FFFFFF"))
        val layoutHeight = editTextPropertyMap?.get(
            EditTextPropertySettingKey.HEIGHT.key
        ).let {
            val defaultHeight = ViewGroup.LayoutParams.WRAP_CONTENT
            if(
                it.isNullOrEmpty()
            ) return@let defaultHeight
            try {it.toInt()}catch (e: Exception){
                defaultHeight
            }
        }
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            layoutHeight,
        )
        linearParamsForEditTextTest.weight = editTextWeight
        insertEditText.layoutParams = linearParamsForEditTextTest
//        addTextChangeListenerForEditText(
//            insertEditText,
//            currentId
//        )
        return insertEditText
    }

    enum class EditTextPropertySettingKey(
        val key: String
    ){
        SIZE("size"),
        HEIGHT("height"),
        ON_UNDER_LINE("onUnderLine"),
        SHELL_PATH("shellPath"),
        SHELL_CON("shellCon"),
        ARGS("args"),
//        ELLIP_SIZE("ellipSize"),
//        .setEllipsize(TextUtils.TruncateAt.END);
    }

    enum class UnderLineValue {
        OFF
    }

//    enum class EllipSizeSettingKey(
//        val key: String,
//        val trancate: TextUtils.TruncateAt?,
//    ){
////        end, marquee, middle, none, start
//        END("end", TextUtils.TruncateAt.END),
//        MARQUEE("marquee", TextUtils.TruncateAt.MARQUEE),
//        MIDDLE("middle", TextUtils.TruncateAt.MIDDLE),
//        START("start", TextUtils.TruncateAt.START),
//        NONE("none", null),
//    }
}

private object EditTextMaker {

    fun make(
        fragment: Fragment,
        editTextPropertyMap: Map<String, String>?,
        currentVariableValue: String?,
    ): String {
        val shellConText = makeByShellCon(
            fragment,
            editTextPropertyMap,
            currentVariableValue,
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "editTexxt.txt").absolutePath,
//            listOf(
//                "editTextPropertyMap: ${editTextPropertyMap}",
//                "shellConText: ${shellConText}",
//            ).joinToString("\n\n")
//        )
        return when(shellConText.isNullOrEmpty()) {
            false -> shellConText
            else -> SearchBoxSettingsForListIndex.makeCurrentVariableValueInEditText(
                fragment,
                currentVariableValue
            )
        }
    }
    private fun makeByShellCon(
        fragment: Fragment,
        editTextPropertyMap: Map<String, String>?,
        currentVariableValue: String?,
    ): String? {
        if(
            editTextPropertyMap.isNullOrEmpty()
        ) return null
        val context = fragment.context
            ?: return null
        val setReplaceVariableMap = when(fragment){
            is EditFragment -> fragment.setReplaceVariableMap
            is TerminalFragment -> fragment.setReplaceVariableMap
            else -> emptyMap()
        }
        val readSharePreferenceMap = when(fragment){
            is EditFragment -> fragment.readSharePreferenceMap
            is TerminalFragment -> fragment.readSharePreferenceMap
            else -> emptyMap()
        }
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val shellConSrc = editTextPropertyMap.get(
            EditTextSetter.EditTextPropertySettingKey.SHELL_CON.key
        )
        val shellCon = when (
            shellConSrc.isNullOrEmpty()
        ) {
            true -> {
                editTextPropertyMap.get(
                    EditTextSetter.EditTextPropertySettingKey.SHELL_PATH.key
                )?.let {
                    EditSettingExtraArgsTool
                        .makeShellCon(editTextPropertyMap)

                }
            }
            else -> shellConSrc
        }?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            ).replace(
                "\${currentVariableValue}",
                currentVariableValue ?: String(),
            ).let {
                SearchBoxSettingsForListIndex.backStackMarkReplace(
                    fragment,
                    it
                )
            }
        } ?: return null
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context),
        )
        val repValMap = editTextPropertyMap.get(
            EditTextSetter.EditTextPropertySettingKey.ARGS.key
        ).let {
            CmdClickMap.createMap(
                it,
                '&'
            )
        }.toMap()
        return busyboxExecutor.getCmdOutput(
            shellCon,
            repValMap
        )

    }
}