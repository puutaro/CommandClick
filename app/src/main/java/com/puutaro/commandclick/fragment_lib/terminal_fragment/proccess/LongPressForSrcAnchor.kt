package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.subMenuAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.ReadText
import java.io.File

class LongPressForSrcAnchor(
    private val terminalFragment: TerminalFragment,
    private val context: Context?,
    private val srcAnchorMenuFilePath: String,
)  {
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val srcAnchorLongPressMenuFilePathObj = File(srcAnchorMenuFilePath)
    private val srcAnchorLongPressMenuDirPath = srcAnchorLongPressMenuFilePathObj.parent
    private val srcAnchorLongPressMenuFileName = srcAnchorLongPressMenuFilePathObj.name
    private var longPressSrcAnchorDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel


    fun launch(
        title: String?,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {
        if(
            context == null
        ) return
        if(
            srcAnchorLongPressMenuDirPath.isNullOrEmpty()
        ) return
        if(
            !File(srcAnchorMenuFilePath).isFile
        ) return
        val menuList = ReadText(
            srcAnchorLongPressMenuDirPath,
            srcAnchorLongPressMenuFileName
        ).textToList().map {
            it to icons8Wheel
        }
        if(
            menuList.size == 1
        ){
            menuScriptHandler(
                menuList.first().first,
                longPressLinkUrl,
                currentUrl,
            )
            return
        }

        longPressSrcAnchorDialog = Dialog(
            context
        )
        longPressSrcAnchorDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val listDialogTitle = longPressSrcAnchorDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        listDialogTitle?.text = title
        val listDialogMessage = longPressSrcAnchorDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = longPressSrcAnchorDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = longPressSrcAnchorDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            longPressSrcAnchorDialog?.dismiss()
        }

        setListView(
            menuList,
            longPressLinkUrl,
            currentUrl,
        )
        longPressSrcAnchorDialog?.setOnCancelListener {
            longPressSrcAnchorDialog?.dismiss()
        }
        longPressSrcAnchorDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressSrcAnchorDialog?.show()
    }

    private fun setListView(
        menuList: List<Pair<String, Int>>,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressSrcAnchorDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = subMenuAdapter(
            context,
            menuList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeListItemSetClickListenerForListDialog(
            subMenuListView,
            longPressLinkUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressSrcAnchorDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as subMenuAdapter
            val selectedScript = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            menuScriptHandler(
                selectedScript,
                longPressLinkUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun menuScriptHandler(
        selectedScript: String,
        longPressLinkUrl: String,
        currentUrl: String,
    ){
        execJsFile(
            selectedScript,
            longPressLinkUrl,
            currentUrl,
        )
    }

    private fun execJsFile(
        selectedScript: String,
        longPressLinkUrl: String,
        currentUrl: String,
    ){
        val jsContentsListSource = ReadText(
            currentAppDirPath,
            selectedScript,
        ).readText()
            .replace(
                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL,
                longPressLinkUrl
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
