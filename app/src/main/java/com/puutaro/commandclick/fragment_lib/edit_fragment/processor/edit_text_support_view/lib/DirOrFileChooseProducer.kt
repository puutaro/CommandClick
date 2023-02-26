package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.fragment.EditFragment
import java.io.File

class DirOrFileChooseProducer {
    companion object {
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
            insertButtonViewParam.weight = weight
            insertButtonView.layoutParams = insertButtonViewParam
            return insertButtonView
        }
    }
}