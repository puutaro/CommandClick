package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.content.Intent
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.fragment.EditFragment
import java.io.File


class WithDirOrFileChooseView(
    private val editFragment: EditFragment,
) {

    private val context = editFragment.context

    fun create(
        insertEditText: EditText,
        currentVariableValue: String?,
        onDirectoryPick: Boolean = true
    ): LinearLayout {
        val chooseButtonStr = if(onDirectoryPick) {
            "dir"
        } else {
            "file"
        }
        val innerLayout = LinearLayout(context)
        innerLayout.orientation = LinearLayout.HORIZONTAL
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        linearParamsForEditTextTest.weight = 0.8F
        insertEditText.layoutParams = linearParamsForEditTextTest
        insertEditText.setFocusableInTouchMode(true);
        innerLayout.addView(insertEditText)

        val insertButtonView = Button(context)
        insertButtonView.setText(chooseButtonStr)


        val prefixRegex = Regex("^content.*fileprovider/root/storage")
        val getFile = editFragment.registerForActivityResult(
            ActivityResultContracts.OpenDocument()) { uri ->
            if (
                uri == null
                || uri.toString() == String()
            ) return@registerForActivityResult
            val pathSource = File(
                uri.toString().replace(prefixRegex, "/storage")
            )
            val setPath = if(onDirectoryPick) {
                pathSource.parent
            } else {
                pathSource.absolutePath
            }
            insertEditText.setText(setPath)
        }

        insertButtonView.setOnClickListener { view ->
            val contentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            contentIntent.type = "*/*"
            contentIntent.addCategory(Intent.CATEGORY_OPENABLE)
            getFile.launch(arrayOf(Intent.CATEGORY_OPENABLE))
        }
        val insertButtonViewParam = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        insertButtonViewParam.weight = 0.2F
        insertButtonView.layoutParams = insertButtonViewParam
        innerLayout.addView(insertButtonView)
        return innerLayout
    }
}