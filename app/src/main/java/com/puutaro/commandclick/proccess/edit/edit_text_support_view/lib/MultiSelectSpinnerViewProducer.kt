package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter


object MultiSelectSpinnerViewProducer {
//    fun make(
//        fragment: Fragment,
//        insertTextView: TextView,
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        currentComponentIndex: Int,
//        weight: Float,
//        isInsertTextViewVisible: Boolean = false
//    ): Button {
//        val context = fragment.context
//        val currentId = editParameters.currentId
//        val currentSetVariableMap = editParameters.setVariableMap
//
//        val linearParamsForButton = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        linearParamsForButton.weight = weight
//        val insertButton = Button(context)
//        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
//        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
//        ButtonSetter.set(
//            context,
//            insertButton,
//            mapOf()
//        )
//        val buttonLabel = "SLs"
//        insertButton.setText(buttonLabel)
//        insertTextView.isVisible = isInsertTextViewVisible
//
//        val sppinerList = currentSetVariableMap?.get(
//            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
//        )
//            ?.split('|')
//            ?.getOrNull(currentComponentIndex)
////            .let {
////                ReplaceVariableMapReflecter.reflect(
////                    QuoteTool.trimBothEdgeQuote(it),
////                    editParameters
////                )
////            }
//            ?.split('?')
//            ?: listOf()
//        val currentExistItem = insertEditText.text.toString()
//        val updatedSppinerList = if(
//            sppinerList.contains(
//                insertEditText.text.toString()
//            )
//        ){
//            listOf(
//                currentExistItem,
//            ) + sppinerList.filter { it != currentExistItem }
//        } else {
//            sppinerList
//        }
//        val updatedMultiModelArray = ArrayList(
//                (updatedSppinerList.indices).map {
//                MultiSelectModel(it, updatedSppinerList[it])
//            }
//        )
//        val currentSelectList = insertEditText.text.toString().split(',')
//        val preSelectedMultiModelIdArray = ArrayList(
//            (updatedSppinerList.indices).filter {
//                val currentItem = updatedSppinerList[it]
//                currentSelectList.contains(currentItem)
//            }
//        )
//        insertButton.setOnClickListener {
//                innerButtonView ->
//            val listener = context as? EditFragment.OnMultiSelectListenerForEdit
//            listener?.onMultiSelectForEdit(
//                insertTextView.text.toString(),
//                insertEditText.id,
//                updatedMultiModelArray,
//                preSelectedMultiModelIdArray
//            )
//        }
//
//        insertButton.layoutParams = linearParamsForButton
//        return insertButton
//    }
}