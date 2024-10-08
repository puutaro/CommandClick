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

class LongPressForImage(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
    private val context: Context?,
    private val imageMenuFilePath: String,
)  {
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    private val imageLongPressMenuFilePathObj = File(imageMenuFilePath)
    private val imageLongPressMenuDirPath = imageLongPressMenuFilePathObj.parent
    private val imageLongPressMenuFileName = imageLongPressMenuFilePathObj.name
    private var longPressImageDialog: Dialog? = null


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
        if(
            imageMenuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        ){
            execJsFile(
                imageMenuFilePath,
                longPressImageUrl,
                currentUrl,
            )
            return
        }
       val terminalFragment = terminalFragmentRef.get()
           ?: return
//        val longPressScriptList =
//            LongPressMenuTool.makeLongPressScriptList(
//                terminalFragment,
//                imageLongPressMenuDirPath,
//                imageLongPressMenuFileName,
//            )
       val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            imageLongPressMenuDirPath,
            imageLongPressMenuFileName,
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
            LongPressMenuTool.LongPressType.IMAGE,
            listOf(longPressImageUrl)
//            longPressScriptList
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
//            val jsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
//                menuList.first().first,
//                longPressScriptList,
//            )?: return
            execJsFile(
                jsPath,
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
            longPressImageDialog = null
        }

        setListView(
            menuList,
            longPressImageUrl,
            currentUrl,
            longPressMenuMapList
        )
        longPressImageDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressImageDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressImageDialog?.show()

        longPressImageDialog?.setOnCancelListener{
            longPressImageDialog?.dismiss()
            longPressImageDialog = null
        }
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        longPressMenuMapList: List<Map<LongPressMenuTool.LongPressKey, String>>,
        longPressImageUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressImageDialog?.dismiss()
            longPressImageDialog = null
            val menuListAdapter = dialogListView.adapter as HistoryListAdapter
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
//                longPressScriptList,
//            ) ?: return@setOnItemClickListener
            execJsFile(
                selectedJsPath,
                longPressImageUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun setListView(
        menuList: List<Pair<String, String>>,
//        longPressScriptList: List<String>,
        longPressImageUrl: String,
        currentUrl: String,
        longPressMenuMapList: List<Map<LongPressMenuTool.LongPressKey, String>>

    ) {
        val terminalFragment = terminalFragmentRef.get() ?: return
        val context = terminalFragment.context
            ?: return
        val subMenuListView =
            longPressImageDialog?.findViewById<ListView>(
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
            longPressImageUrl,
            currentUrl,
        )
    }


    private fun execJsFile(
        selectedJsPath: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
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
        val imageLongPressJsPath =  SettingVariableReader.getStrValue(
            settingValList,
            CommandClickScriptVariable.IMAGE_LONG_PRESS_JS_PATH,
            execJsPath
        )
        val imageLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL
                to longPressImageUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                to currentUrl
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            imageLongPressJsPath,
            ReadText(imageLongPressJsPath).textToList(),
            imageLongPressRepValMap
        )
    }
}
