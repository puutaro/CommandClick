package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.*
import androidx.core.view.isVisible
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.util.*
import java.io.File

object EditableListContentsMultiSeletctSpinnerViewProducer {

    fun make (
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
        isInsertTextViewVisible: Boolean = false
    ): Button {
        val context = editParameters.context
        val currentId = editParameters.currentId

        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight
        val elcbMap = ListContentsSelectSpinnerViewProducer.getElcbMap(
            editParameters
        )
        val listContentsFilePath = ListContentsSelectSpinnerViewProducer.getListPath(
            elcbMap,
        )
        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name
        FileSystems.createDirs(parentDir)


        val insertButton = Button(context)
        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
        val buttonLabel = "LSLs"
        insertButton.setText(buttonLabel)
        insertTextView.isVisible = isInsertTextViewVisible

        insertButton.setOnClickListener {
                innerButtonView ->
            val updatedEditableSpinnerList = ReadText(
                parentDir,
                listFileName
            ).textToList().filter {
                it.trim().isNotEmpty()
            }
            val updatedMultiModelArray = ArrayList(
                (updatedEditableSpinnerList.indices).map {
                    MultiSelectModel(it, updatedEditableSpinnerList[it])
                }
            )
            val currentSelectList = insertEditText.text.toString().split(',')
            val preSelectedMultiModelIdArray = ArrayList(
                (updatedEditableSpinnerList.indices).filter {
                    val currentItem = updatedEditableSpinnerList[it]
                    currentSelectList.contains(currentItem)
                }
            )
            val listener = context as? EditFragment.OnMultiSelectListenerForEdit
            listener?.onMultiSelectForEdit(
                insertTextView.text.toString(),
                insertEditText.id,
                updatedMultiModelArray,
                preSelectedMultiModelIdArray
            )
        }

        val linearParamsForButton = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForButton.weight = weight
        insertButton.layoutParams = linearParamsForButton

        return insertButton
    }
}
