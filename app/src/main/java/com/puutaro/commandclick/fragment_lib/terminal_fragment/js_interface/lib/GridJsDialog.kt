package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.GridView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.ImageAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class GridJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()

    fun create(
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    message,
                    imagePathListTabSepaStr
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
        return returnValue

    }

    private fun createGridView(
        imagePathList: List<String>,
    ): GridView {

        val gridview = GridView(context)
        gridview.numColumns = 2;
        val myImageAdapter = ImageAdapter(context)
        myImageAdapter.addAll(imagePathList.toMutableList())
        gridview.adapter = myImageAdapter

//        val externalStorageDirectoryPath = Environment
//            .getExternalStorageDirectory()
//            .absolutePath
//
//        val targetPath = "$externalStorageDirectoryPath/DCIM/100ANDRO"
//
//        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show()
//        val targetDirector = File(targetPath)

//        val files = targetDirector.listFiles()
//        for (file in files) {
//            myImageAdapter.add(file.absolutePath)
//        }
        return gridview
    }

    private fun execCreate(
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ) {
        val imagePathList =
            imagePathListTabSepaStr
                .split("\t")
                .toMutableList()
        val context = context ?: return

        val gridview = createGridView(
            imagePathList
        )



        val titleString = if(title.isNotEmpty()){
            title
        } else "Select bellow list"
        val alertDialog = if(
            message.isNotEmpty()
        ) {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setMessage(message)
                .setView(gridview)
                .create()
        } else {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setView(gridview)
                .create()
        }
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.show()

        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })

        gridview.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog.dismiss()
            val selectedElement = imagePathList
                .get(pos)
                .split("\n")
                .firstOrNull()
                ?: return@setOnItemClickListener
            terminalViewModel.onDialog = false
            returnValue = selectedElement
            return@setOnItemClickListener
        }


    }
}