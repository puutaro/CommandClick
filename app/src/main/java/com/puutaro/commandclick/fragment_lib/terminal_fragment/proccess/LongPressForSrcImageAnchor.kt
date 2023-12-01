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

class LongPressForSrcImageAnchor(
    private val terminalFragment: TerminalFragment,
    private val context: Context?,
    private val srcImageAnchorMenuFilePath: String,
)  {
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val srcImageAnchorLongPressMenuFilePathObj = File(srcImageAnchorMenuFilePath)
    private val srcImageAnchorLongPressMenuDirPath = srcImageAnchorLongPressMenuFilePathObj.parent
    private val srcImageAnchorLongPressMenuFileName = srcImageAnchorLongPressMenuFilePathObj.name
    private var longPressSrcImageAnchorDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel

    fun launch(
        title: String?,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ) {
        if(
            context == null
        ) return
        if(
            srcImageAnchorLongPressMenuDirPath.isNullOrEmpty()
        ) return
        if(
            !File(srcImageAnchorMenuFilePath).isFile
        ) return
        val menuList = ReadText(
            srcImageAnchorLongPressMenuDirPath,
            srcImageAnchorLongPressMenuFileName
        ).textToList().map {
            it to icons8Wheel
        }
        if(
            menuList.size == 1
        ){
            menuScriptHandler(
                menuList.first().first,
                longPressLinkUrl,
                longPressImageUrl,
                currentUrl,
            )
            return
        }

        longPressSrcImageAnchorDialog = Dialog(
            context
        )
        longPressSrcImageAnchorDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val listDialogTitle = longPressSrcImageAnchorDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        listDialogTitle?.text = title
        val listDialogMessage = longPressSrcImageAnchorDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = longPressSrcImageAnchorDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = longPressSrcImageAnchorDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            longPressSrcImageAnchorDialog?.dismiss()
        }
        setListView(
            menuList,
            longPressLinkUrl,
            longPressImageUrl,
            currentUrl,
        )
        longPressSrcImageAnchorDialog?.setOnCancelListener {
            longPressSrcImageAnchorDialog?.dismiss()
        }
        longPressSrcImageAnchorDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressSrcImageAnchorDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressSrcImageAnchorDialog?.show()
    }


    private fun setListView(
        menuList: List<Pair<String, Int>>,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ) {
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressSrcImageAnchorDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            menuList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeListItemSetClickListenerForListDialog(
            subMenuListView,
            longPressLinkUrl,
            longPressImageUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        subMenuListView: ListView,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ) {

        subMenuListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressSrcImageAnchorDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedScript = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            menuScriptHandler(
                selectedScript,
                longPressLinkUrl,
                longPressImageUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun menuScriptHandler(
        selectedScript: String,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        execJsFile(
            selectedScript,
            longPressLinkUrl,
            longPressImageUrl,
            currentUrl,
        )
    }

    private fun execJsFile(
        selectedScript: String,
        longPressLinkUrl: String,
        longPressImageUrl: String,
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

