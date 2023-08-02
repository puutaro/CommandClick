package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Intent
import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder


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
            insertButtonView
        )

        val prefixRegex = Regex("^content.*fileprovider/root/storage")

        val getFile = editFragment.registerForActivityResult(
            ActivityResultContracts.OpenDocument()) { uri ->
            if (
                uri == null
                || uri.toString() == String()
            ) return@registerForActivityResult

            val pathSource = runBlocking {
                File(
                    withContext(Dispatchers.IO) {
                        URLDecoder.decode(
                            uri.toString(), Charsets.UTF_8.name()
                        )
                    }.replace(prefixRegex, "/storage")
                )
            }
            val setPath = if(onDirectoryPick) {
                pathSource.parent
            } else {
                pathSource.absolutePath
            }
            insertEditText.setText(setPath)
        }

        insertButtonView.setOnClickListener { view ->
            if(Build.VERSION.SDK_INT < 30){
                getFile.launch(arrayOf(Intent.CATEGORY_OPENABLE))
                return@setOnClickListener
            }
            val listener = context as? EditFragment.OnFileChooserListenerForEdit
            listener?.onFileChooserListenerForEdit(
                onDirectoryPick,
                insertEditText
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