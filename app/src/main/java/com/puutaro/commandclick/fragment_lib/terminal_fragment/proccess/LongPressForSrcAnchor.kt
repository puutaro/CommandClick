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
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
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
        val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            srcAnchorLongPressMenuDirPath,
            srcAnchorLongPressMenuFileName,
        )
        val menuList = LongPressMenuTool.makeMenuList(
            longPressScriptList
        )

        if(
            menuList.size == 1
        ){
            val jsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
                menuList.first().first,
                longPressScriptList,
            )?: return
            execJsFile(
                jsPath,
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
            longPressScriptList,
            longPressLinkUrl,
            currentUrl,
        )
        longPressSrcAnchorDialog?.setOnCancelListener {
            longPressSrcAnchorDialog?.dismiss()
        }
        longPressSrcAnchorDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressSrcAnchorDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressSrcAnchorDialog?.show()
    }

    private fun setListView(
        menuList: List<Pair<String, Int>>,
        longPressScriptList: List<String>,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressSrcAnchorDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            menuList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeListItemSetClickListenerForListDialog(
            subMenuListView,
            longPressScriptList,
            longPressLinkUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        longPressScriptList: List<String>,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressSrcAnchorDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val selectedJsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
                selectedMenuName,
                longPressScriptList,
            ) ?: return@setOnItemClickListener
            execJsFile(
                selectedJsPath,
                longPressLinkUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun execJsFile(
        selectedJsPath: String,
        longPressLinkUrl: String,
        currentUrl: String,
    ){
        val selectedScriptNameOrPathObj = File(selectedJsPath)
        val execJsPath = LongPressMenuTool.makeExecJsPath(
            terminalFragment,
            currentAppDirPath,
            selectedScriptNameOrPathObj,
        )
        val srcAnchorLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL
                    to longPressLinkUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                    to currentUrl
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            execJsPath,
            ReadText(execJsPath).textToList(),
            srcAnchorLongPressRepValMap
        )
    }
}
