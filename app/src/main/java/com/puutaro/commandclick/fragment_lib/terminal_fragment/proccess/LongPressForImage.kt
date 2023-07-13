package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.util.ReadText
import java.io.File

class LongPressForImage(
    private val terminalFragment: TerminalFragment,
)  {
    private val context = terminalFragment.context
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val imageMenuFilePath = terminalFragment.imageLongPressMenuFilePath
    private val imageLongPressMenuFilePathObj = File(imageMenuFilePath)
    private val imageLongPressMenuDirPath = imageLongPressMenuFilePathObj.parent
    private val imageLongPressMenuFileName = imageLongPressMenuFilePathObj.name


    fun launch(
        title: String?,
        longPressImageUrl: String,
        currentUrl: String,
    ) {
        if(
            context == null
        ) return
        if(
            imageLongPressMenuDirPath.isNullOrEmpty()
        ) return
        if(
            !File(imageMenuFilePath).isFile
        ) return
        val menuList = ReadText(
            imageLongPressMenuDirPath,
            imageLongPressMenuFileName
        ).textToList()
        if(
            menuList.size == 1
        ){
            menuScriptHandler(
                menuList.first(),
                longPressImageUrl,
                currentUrl,
            )
            return
        }

        val dialogListView = ListView(context)
        val dialogListAdapter = ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            menuList.toMutableList(),
        )
        dialogListView.adapter = dialogListAdapter
        dialogListView.setSelection(
            dialogListAdapter.count
        )
        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        linearLayoutForTotal.addView(dialogListView)
        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle(title)
            .setView(linearLayoutForTotal)
            .create()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.show()

        alertDialog.setOnCancelListener(
            object : DialogInterface.OnCancelListener {
                override fun onCancel(
                    dialog: DialogInterface?
                ) {
                    alertDialog.dismiss()
                }
            })

        invokeListItemSetClickListenerForListDialog(
            dialogListView,
            menuList,
            alertDialog,
            longPressImageUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        dialogList: List<String>,
        alertDialog: AlertDialog,
        longPressImageUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog.dismiss()
            val selectedScript = dialogList
                .get(pos)
                .split("\n")
                .firstOrNull()
                ?: return@setOnItemClickListener
            menuScriptHandler(
                selectedScript,
                longPressImageUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun menuScriptHandler(
        selectedScript: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        execJsFile(
            selectedScript,
            longPressImageUrl,
            currentUrl,
        )
    }

    private fun execJsFile(
        selectedScript: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        val jsContentsListSource = ReadText(
            currentAppDirPath,
            selectedScript,
        ).readText()
            .replace(
                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL,
                longPressImageUrl
            )
            .replace(
                CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL,
                currentUrl
            )
            .split("\n")
        ExecJsLoad.execJsLoad(
            terminalFragment,
            currentAppDirPath,
            selectedScript,
            jsContentsListSource
        )
    }
}
