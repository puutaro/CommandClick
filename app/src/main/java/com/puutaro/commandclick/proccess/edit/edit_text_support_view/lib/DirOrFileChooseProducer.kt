package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter

object DirOrFileChooseProducer {

    fun make(
        editFragment: EditFragment,
        onDirectoryPick: Boolean,
        insertEditText: EditText,
        weight: Float,
    ): Button {
        val context = editFragment.context
        val chooseButtonStr = if(onDirectoryPick) {
            "dir"
        } else {
            "file"
        }
        val insertButtonView = Button(context)
        insertButtonView.text = chooseButtonStr
        ButtonSetter.set(
            context,
            insertButtonView,
            mapOf()
        )

        insertButtonView.setOnClickListener { view ->
            val listener = context as? EditFragment.OnFileChooserListenerForEdit
            listener?.onFileChooserListenerForEdit(
                onDirectoryPick,
                insertEditText,
                String()
            )
        }
        val insertButtonViewParam = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        insertButtonViewParam.weight = weight
        insertButtonView.layoutParams = insertButtonViewParam
        return insertButtonView
    }
}