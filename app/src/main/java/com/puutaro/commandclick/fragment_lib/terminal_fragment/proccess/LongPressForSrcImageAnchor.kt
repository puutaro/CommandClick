package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListAdapter
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
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime

class LongPressForSrcImageAnchor(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
    private val context: Context?,
    private val srcImageAnchorMenuFilePath: String,
)  {
//    private val currentAppDirPath = terminalFragment.currentAppDirPath
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
        val terminalFragment = terminalFragmentRef.get() ?: return
        val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            srcImageAnchorLongPressMenuDirPath,
            srcImageAnchorLongPressMenuFileName,
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
            LongPressMenuTool.LongPressType.SRC_IMAGE_ANCHOR,
            listOf(longPressLinkUrl, longPressImageUrl)
        )
        val menuList = LongPressMenuTool.LongPressInfoMapList.extractTitleIconOathList(
            longPressMenuMapList
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "longpress.txt").absolutePath,
//            listOf(
//                "longPressScriptList: ${longPressScriptList}",
//                "longPressMenuMapList: ${longPressMenuMapList}",
//                "menuList: ${menuList}",
//            ).joinToString("\n\n")
//        )
        val menuListSize = menuList.size
        if(
            menuListSize == 0
        ) return
        if(
            menuListSize == 1
        ){
            val firstMenuTitle = menuList.first().first
            val jsPath = longPressMenuMapList.firstOrNull {
                it.get(LongPressMenuTool.LongPressKey.TITLE) == firstMenuTitle
            }?.get(LongPressMenuTool.LongPressKey.JS_PATH) ?: return
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
            longPressSrcImageAnchorDialog = null
        }
        setListView(
            menuList,
//            longPressScriptList,
            longPressLinkUrl,
            longPressImageUrl,
            currentUrl,
            longPressMenuMapList
        )
        longPressSrcImageAnchorDialog?.setOnCancelListener {
            longPressSrcImageAnchorDialog?.dismiss()
            longPressSrcImageAnchorDialog = null
        }
        longPressSrcImageAnchorDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressSrcImageAnchorDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressSrcImageAnchorDialog?.show()
    }

    private fun setListView(
        menuList: List<Pair<String, String>>,
//        longpressScriptList: List<String>,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
        longPressMenuMapList: List<Map<LongPressMenuTool.LongPressKey, String>>,
    ) {
        val terminalFragment = terminalFragmentRef.get() ?: return
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressSrcImageAnchorDialog?.findViewById<ListView>(
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
//            longpressScriptList,
            longPressLinkUrl,
            longPressImageUrl,
            currentUrl,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        subMenuListView: ListView,
        longPressMenuMapList: List<Map<LongPressMenuTool.LongPressKey, String>>,
//        longpressScriptList: List<String>,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,

    ) {

        subMenuListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressSrcImageAnchorDialog?.dismiss()
            longPressSrcImageAnchorDialog = null
            val menuListAdapter = subMenuListView.adapter as HistoryListAdapter
            val title =  menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            HistoryListAdapter.saveItemToList(
                context,
                title
            )
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
//                longpressScriptList,
//            ) ?: return@setOnItemClickListener
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

