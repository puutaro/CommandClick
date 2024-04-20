package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.edit.lib.GetFileEditTool
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
            insertButtonView,
            mapOf()
        )

//        val prefixRegex = Regex("^content.*fileprovider/root/storage")

        val getFile = editFragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (
                result.resultCode != Activity.RESULT_OK
            ) return@registerForActivityResult
            result.data?.data?.let { uri ->
                if (
                    uri.toString() == String()
                ) return@registerForActivityResult
                val pathSource = runBlocking {
                    GetFileEditTool.makeGetName(
                        uri
                    )
                }
                val setPath = if(onDirectoryPick) {
                    pathSource.parent
                } else {
                    pathSource.absolutePath
                }
                insertEditText.setText(setPath)
            }
        }

        insertButtonView.setOnClickListener { view ->
            when(Build.VERSION.SDK_INT < 30){
                true -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"  // Set the MIME type to filter files
//            val uri = Uri.parse(
//                "content://com.android.externalstorage.documents/document/primary:$folderName")
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
                    }
                    getFile.launch(intent)
//                    getFile.launch(arrayOf(Intent.CATEGORY_OPENABLE))
                }
                else -> {
                    val listener = context as? EditFragment.OnFileChooserListenerForEdit
                    listener?.onFileChooserListenerForEdit(
                        onDirectoryPick,
                        insertEditText
                    )
                }
            }
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