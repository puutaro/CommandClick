package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.ReadText
import java.io.File

class LongPressForImage(
    private val terminalFragment: TerminalFragment,
    private val context: Context?,
    private val imageMenuFilePath: String,
)  {
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val imageLongPressMenuFilePathObj = File(imageMenuFilePath)
    private val imageLongPressMenuDirPath = imageLongPressMenuFilePathObj.parent
    private val imageLongPressMenuFileName = imageLongPressMenuFilePathObj.name
    private var longPressImageDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel


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
        ).textToList().map {
            it to icons8Wheel
        }
        if(
            menuList.size == 1
        ){
            menuScriptHandler(
                menuList.first().first,
                longPressImageUrl,
                currentUrl,
            )
            return
        }

        longPressImageDialog = Dialog(
            context
        )
        longPressImageDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val listDialogTitle = longPressImageDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        listDialogTitle?.text = title
        val listDialogMessage = longPressImageDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = longPressImageDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = longPressImageDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            longPressImageDialog?.dismiss()
        }

        setListView(
            menuList,
            longPressImageUrl,
            currentUrl,
        )
        longPressImageDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressImageDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressImageDialog?.show()

        longPressImageDialog?.setOnCancelListener{
            longPressImageDialog?.dismiss()
        }
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        longPressImageUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressImageDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
            val selectedScript = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            menuScriptHandler(
                selectedScript,
                longPressImageUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun setListView(
        menuList: List<Pair<String, Int>>,
        longPressImageUrl: String,
        currentUrl: String,
    ) {
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressImageDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            menuList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeListItemSetClickListenerForListDialog(
            subMenuListView,
            longPressImageUrl,
            currentUrl,
        )
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
        val loadLongPressJsCon = JavaScriptLoadUrl.make(
            context,
            "${currentAppDirPath}/${selectedScript}",
            jsContentsListSource
        ) ?: return
        ExecJsLoad.jsUrlLaunchHandler(
            terminalFragment,
            loadLongPressJsCon
        )
    }

}
