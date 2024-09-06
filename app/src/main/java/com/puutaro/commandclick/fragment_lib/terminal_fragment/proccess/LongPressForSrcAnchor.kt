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
import com.puutaro.commandclick.component.adapter.HistoryListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.ReadText
import java.io.File
import java.lang.ref.WeakReference

class LongPressForSrcAnchor(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
    private val context: Context?,
    private val srcAnchorMenuFilePath: String,
)  {
//    private val currentAppDirPath = terminalFragment.currentAppDirPath
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
        if(
            srcAnchorMenuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        ){
            execJsFile(
                srcAnchorMenuFilePath,
                longPressLinkUrl,
                currentUrl,
            )
            return
        }
        val terminalFragment = terminalFragmentRef.get() ?: return
        val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            srcAnchorLongPressMenuDirPath,
            srcAnchorLongPressMenuFileName,
        ).joinToString("\n").let {
            val currentValidFannelName =
                ValidFannelNameGetterForTerm.get(
                    terminalFragment
                )
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                terminalFragment.setReplaceVariableMap,
                currentValidFannelName
            )
        }.split("\n")
        val longPressMenuMapList = LongPressMenuTool.LongPressInfoMapList.makeMenuMapList(
            context,
            longPressScriptList,
            LongPressMenuTool.LongPressType.SRC_ANCHOR,
            listOf(longPressLinkUrl)
        )
        val menuList = LongPressMenuTool.LongPressInfoMapList.extractTitleIconOathList(
            longPressMenuMapList
        )
        if(
            menuList.size == 1
        ){
            val firstMenuTitle = menuList.first().first
            val jsPath = longPressMenuMapList.firstOrNull {
                it.get(LongPressMenuTool.LongPressKey.TITLE) == firstMenuTitle
            }?.get(LongPressMenuTool.LongPressKey.JS_PATH) ?: return
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
            longPressSrcAnchorDialog = null
        }

        setListView(
            menuList,
            longPressScriptList,
            longPressLinkUrl,
            currentUrl,
            longPressMenuMapList,
        )
        longPressSrcAnchorDialog?.setOnCancelListener {
            longPressSrcAnchorDialog?.dismiss()
            longPressSrcAnchorDialog = null
        }
        longPressSrcAnchorDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressSrcAnchorDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressSrcAnchorDialog?.show()
    }

    private fun setListView(
        menuList: List<Pair<String, String>>,
        longPressScriptList: List<String>,
        longPressLinkUrl: String,
        currentUrl: String,
        longPressMenuMapList: List<Map<LongPressMenuTool.LongPressKey, String>>
    ) {
        val terminalFragment = terminalFragmentRef.get() ?: return
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressSrcAnchorDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = HistoryListAdapter(
            context,
            menuList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeListItemSetClickListenerForListDialog(
            subMenuListView,
            longPressMenuMapList,
//            longPressScriptList,
            longPressLinkUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        longPressMenuMapList: List<Map<LongPressMenuTool.LongPressKey, String>>,
//        longPressScriptList: List<String>,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressSrcAnchorDialog?.dismiss()
            longPressSrcAnchorDialog = null
            val menuListAdapter = dialogListView.adapter as HistoryListAdapter
            val title =  menuListAdapter.getItem(pos)
            val selectedJsPath = longPressMenuMapList.firstOrNull {
                it.get(LongPressMenuTool.LongPressKey.TITLE) == title
            }?.get(LongPressMenuTool.LongPressKey.JS_PATH) ?: return@setOnItemClickListener
            if(
                !File(selectedJsPath).isFile
            ) return@setOnItemClickListener

//            val selectedMenuName = menuListAdapter.getItem(pos)
//                ?: return@setOnItemClickListener
//            val selectedJsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
//                selectedMenuName,
//                longPressScriptList,
//            ) ?: return@setOnItemClickListener
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
        val terminalFragment = terminalFragmentRef.get() ?: return
        val selectedScriptNameOrPathObj = File(selectedJsPath)
        val execJsPath = LongPressMenuTool.makeExecJsPath(
            terminalFragment,
//            currentAppDirPath,
            selectedScriptNameOrPathObj,
        )
        val settingValList = LongPressMenuTool.extractSettingValList(
            context,
            execJsPath,
        )
        val srcAnchorLongPressJsPath =  SettingVariableReader.getStrValue(
            settingValList,
            CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_JS_PATH,
            execJsPath
        )
        val srcAnchorLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL
                    to longPressLinkUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                    to currentUrl
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            srcAnchorLongPressJsPath,
            ReadText(srcAnchorLongPressJsPath).textToList(),
            srcAnchorLongPressRepValMap
        )
    }
}
