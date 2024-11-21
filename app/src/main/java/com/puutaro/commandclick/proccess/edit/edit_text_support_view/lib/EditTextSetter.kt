package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.text.InputType
import android.util.TypedValue
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.view_model.activity.EditViewModel

object EditTextSetter {

    fun set(
        fragment: Fragment,
        editParameters: EditParameters,
        editTextPropertyMap: Map<String, String>?,
        editTextWeight: Float
    ): EditText {
        val editViewModel: EditViewModel by fragment.activityViewModels()
        val context = fragment.context
//        val currentId = editParameters.currentId
//        val currentVariableValue = editParameters.currentVariableValue
//        val currentVariableName = editParameters.currentVariableName

        val insertEditText = EditText(context)
        insertEditText.clearFocus()
//        insertEditText.tag = currentVariableName
//        insertEditText.id = currentId
        insertEditText.backgroundTintList = context?.getColorStateList(R.color.gray_out)
//        editViewModel.variableNameToEditTextIdMap.put(
//            currentVariableName as String,
//            currentId
//        )

        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
//        val editTextCon = EditTextMaker.make(
//            fragment,
//            editTextPropertyMap,
//            currentVariableValue,
//        )
//        insertEditText.setText(editTextCon)

        editTextPropertyMap?.get(
            EditTextPropertySettingKey.SIZE.key
        )?.let {
            val textSize = try{
                ScreenSizeCalculator.toDp(
                    context,
                    it.toFloat()
                ).toFloat()
            }catch (e: Exception){
                return@let
            }
            insertEditText.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                textSize
            )
        }
        editTextPropertyMap?.get(
            EditTextPropertySettingKey.ON_UNDER_LINE.key
        )?.let {
            if(it != UnderLineValue.OFF.name) return@let
            insertEditText.setBackgroundResource(android.R.color.transparent)
        }
        editTextPropertyMap?.get(
            EditTextPropertySettingKey.HINT.key
        )?.let {
            insertEditText.hint = it
        }
        insertEditText.setSelectAllOnFocus(true)
        val layoutHeight = editTextPropertyMap?.get(
            EditTextPropertySettingKey.HEIGHT.key
        ).let {
            val defaultHeight = ScreenSizeCalculator.toDp(
                context,
                50
            )
            if(
                it.isNullOrEmpty()
            ) return@let defaultHeight
            try {
                ScreenSizeCalculator.toDp(
                    context,
                    it.toInt()
                )

            }catch (e: Exception){
                defaultHeight
            }
        }
        val linearParamsForEditTextTest = LinearLayoutCompat.LayoutParams(
            0,
            layoutHeight,
        )
        linearParamsForEditTextTest.weight = editTextWeight
        insertEditText.layoutParams = linearParamsForEditTextTest
        return insertEditText
    }

    enum class EditTextPropertySettingKey(
        val key: String
    ){
        SIZE("size"),
        HEIGHT("height"),
        ON_UNDER_LINE("onUnderLine"),
        HINT("hint"),
        SHELL_PATH("shellPath"),
        SHELL_CON("shellCon"),
        ARGS("args"),
    }

    enum class UnderLineValue {
        OFF
    }
}

private object EditTextMaker {

    private const val argsSeparator = '&'

//    fun make(
//        fragment: Fragment,
//        editTextPropertyMap: Map<String, String>?,
//        currentVariableValue: String?,
//    ): String {
//        val shellConText = makeByShellCon(
//            fragment,
//            editTextPropertyMap,
//            currentVariableValue,
//        )
//        return when(shellConText.isNullOrEmpty()) {
//            false -> shellConText
//            else -> SearchBoxSettingsForListIndex.makeCurrentVariableValueInEditText(
//                fragment,
//                currentVariableValue
//            )
//        }
//    }
    private fun makeByShellCon(
        fragment: Fragment,
        editTextPropertyMap: Map<String, String>?,
        currentVariableValue: String?,
    ): String? {
        if(
            editTextPropertyMap.isNullOrEmpty()
        ) return null
        val setReplaceVariableMap = when(fragment){
            is EditFragment -> fragment.setReplaceVariableMap
            is TerminalFragment -> fragment.setReplaceVariableMap
            else -> emptyMap()
        }
        val shellConSrc = editTextPropertyMap.get(
            EditTextSetter.EditTextPropertySettingKey.SHELL_CON.key
        )
        val repValMap = editTextPropertyMap.get(
            EditTextSetter.EditTextPropertySettingKey.ARGS.key
        ).let {
            CmdClickMap.createMap(
                it,
                argsSeparator
            )
        }.toMap()
        val extraRepValMap = repValMap + mapOf(
            "\${currentVariableValue}"
                    to (currentVariableValue ?: String()),
            SearchBoxSettingsForEditList.backstackCountMarkForInsertEditText
                    to TitleImageAndViewSetter.makeBackstackCount(fragment)
        )
        val busyboxExecutor = when(fragment){
            is EditFragment -> fragment.busyboxExecutor
            is TerminalFragment -> fragment.busyboxExecutor
            else -> return String()
        } ?: return String()
        return when (
            shellConSrc.isNullOrEmpty()
        ) {
            true -> {
                ShellMacroHandler.handle(
                    fragment.context,
                    busyboxExecutor,
                    editTextPropertyMap.get(
                        EditTextSetter.EditTextPropertySettingKey.SHELL_PATH.key
                    ) ?: String(),
                    setReplaceVariableMap,
                    extraRepValMap
                )
            }
            else -> {
                val envMap = extraRepValMap +
                        (setReplaceVariableMap ?: mapOf())
                busyboxExecutor.getCmdOutput(
                    shellConSrc,
                    envMap
                )
            }
            //        shellConSrc
//        }.let {
//            SetReplaceVariabler.execReplaceByReplaceVariables(
//                it,
//                setReplaceVariableMap,
//                currentAppDirPath,
//                currentFannelName
//            ).replace(
//                "\${currentVariableValue}",
//                currentVariableValue ?: String(),
//            ).let {
//                SearchBoxSettingsForListIndex.backStackMarkReplace(
//                    fragment,
//                    it
//                )
//            }
//        }
//        val repValMap = editTextPropertyMap.get(
//            EditTextSetter.EditTextPropertySettingKey.ARGS.key
//        ).let {
//            CmdClickMap.createMap(
//                it,
//                argsSeparator
//            )
//        }.toMap()
//        return busyboxExecutor.getCmdOutput(
//            shellCon,
//            repValMap
//        )
        }
    }

}