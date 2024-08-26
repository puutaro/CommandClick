package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object EditableListContentsMultiSeletctSpinnerViewProducer {

    fun make (
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters,
        currentComponentIndex: Int,
        weight: Float,
        isInsertTextViewVisible: Boolean = false
    ): Button {
        val context = editParameters.context
        val currentId = editParameters.currentId

        val linearParamsForSpinner = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight
        val elcbMap = ListContentsSelectSpinnerViewProducer.getElsbMap(
            editParameters,
            currentComponentIndex
        )
        val listContentsFilePath = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        ) ?: String()
        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        FileSystems.createDirs(parentDir)

        val insertButton = Button(context)
        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
        val buttonLabel = "LSLs"
        insertButton.text = buttonLabel
        insertTextView.isVisible = isInsertTextViewVisible
        ButtonSetter.set(
            context,
            insertButton,
            mapOf()
        )
        insertButton.setOnClickListener {
                innerButtonView ->
            val updatedEditableSpinnerList = ReadText(
                listContentsFilePath
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

        val linearParamsForButton = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        )
        linearParamsForButton.weight = weight
        insertButton.layoutParams = linearParamsForButton

        return insertButton
    }
}
