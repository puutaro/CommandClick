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
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.ReadText
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
        if(
            srcImageAnchorMenuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        ){
            execJsFile(
                srcImageAnchorMenuFilePath,
                longPressLinkUrl,
                longPressImageUrl,
                currentUrl,
            )
            return
        }
        val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            srcImageAnchorLongPressMenuDirPath,
            srcImageAnchorLongPressMenuFileName,
        )
        val menuList = LongPressMenuTool.makeMenuList(longPressScriptList)
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
            longPressScriptList,
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
        longpressScriptList: List<String>,
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
            longpressScriptList,
            longPressLinkUrl,
            longPressImageUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        subMenuListView: ListView,
        longpressScriptList: List<String>,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ) {

        subMenuListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressSrcImageAnchorDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val selectedJsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
                selectedMenuName,
                longpressScriptList,
            ) ?: return@setOnItemClickListener
            execJsFile(
                selectedJsPath,
                longPressLinkUrl,
                longPressImageUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun execJsFile(
        selectedJsPath: String,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        val selectedScriptNameOrPathObj = File(selectedJsPath)
        val execJsPath = LongPressMenuTool.makeExecJsPath(
            terminalFragment,
            currentAppDirPath,
            selectedScriptNameOrPathObj,
        )
        val settingValList = LongPressMenuTool.extractSettingValList(
            context,
            execJsPath,
        )
        val srcImageAnchorLongPressJsPath =  SettingVariableReader.getStrValue(
            settingValList,
            CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_JS_PATH,
            execJsPath
        )
        val srcImageAnchorLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL
                    to longPressLinkUrl,
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL
                    to longPressImageUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                    to currentUrl,
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            srcImageAnchorLongPressJsPath,
            ReadText(srcImageAnchorLongPressJsPath).textToList(),
            srcImageAnchorLongPressRepValMap
        )
    }
}

