package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.view_model.activity.EditViewModel

object EditTextSetter {

    private const val backstackCountMarkForInsertEditText = "\${BACKSTACK_COUNT}"
    private const val fannelTitleMarkForInsertEditText = "\${FANNEL_TITLE}"

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

        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        val insertEditText = EditText(context)
        insertEditText.clearFocus()
        insertEditText.tag = currentVariableName
        insertEditText.id = currentId
//        insertEditText.setTextColor(context?.getColor(R.color.terminal_color) as Int)
        insertEditText.backgroundTintList = context?.getColorStateList(R.color.gray_out)
        editViewModel.variableNameToEditTextIdMap.put(
            currentVariableName as String,
            currentId
        )

        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(
            makeCurrentVariableValueInEditText(
                editParameters,
                currentVariableValue
            )
        )

        editTextPropertyMap?.get(
            EditTextPropertySettingKey.SIZE.key
        )?.let {
            val textSize = try{it.toFloat()}catch (e: Exception){return@let }
            insertEditText.textSize = textSize
        }
        editTextPropertyMap?.get(
            EditTextPropertySettingKey.ON_UNDER_LINE.key
        )?.let {
            if(it != "OFF") return@let
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
        linearParamsForEditTextTest.weight = editTextWeight
        insertEditText.layoutParams = linearParamsForEditTextTest
//        addTextChangeListenerForEditText(
//            insertEditText,
//            currentId
//        )
        return insertEditText
    }

    private fun makeCurrentVariableValueInEditText(
        editParameters: EditParameters,
        currentVariableValue: String?
    ): String {
        if(
            currentVariableValue.isNullOrEmpty()
        ) return String()
        val currentFragment = editParameters.currentFragment
        if(
            currentFragment !is EditFragment
        ) return currentVariableValue
        val readSharePreffernceMap = currentFragment.readSharePreffernceMap

        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val replaceBackstackCurrentVariableValue =
            when(
                currentVariableValue.contains(backstackCountMarkForInsertEditText)
            ){
                true -> {
                    val backstackNum = TitleImageAndViewSetter.makeBackstackCount(currentFragment)
                    currentVariableValue.replace(
                        backstackCountMarkForInsertEditText,
                        backstackNum.toString(),
                    )
                }
                else -> currentVariableValue
            }
        return when(
            replaceBackstackCurrentVariableValue.contains(fannelTitleMarkForInsertEditText)
        ){
            true -> {
                val fannelTitleStr = TitleImageAndViewSetter.makeCompressFannelPath(
                    currentAppDirPath,
                    currentFannelName,
                )
                replaceBackstackCurrentVariableValue.replace(
                    fannelTitleMarkForInsertEditText,
                    fannelTitleStr,
                )
            }
            else -> replaceBackstackCurrentVariableValue
        }
    }

    enum class EditTextPropertySettingKey(
        val key: String
    ){
        SIZE("size"),
        ON_UNDER_LINE("onUnderLine"),
//        ELLIP_SIZE("ellipSize"),
//        .setEllipsize(TextUtils.TruncateAt.END);
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