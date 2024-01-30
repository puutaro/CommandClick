package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
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
        insertEditText.setText(
            SearchBoxSettingsForListIndex.makeCurrentVariableValueInEditText(
                editParameters.currentFragment,
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