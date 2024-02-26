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
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelPrefGetter
import java.io.File

class LongPressForImage(
    private val terminalFragment: TerminalFragment,
    private val context: Context?,
    private val imageMenuFilePath: String,
)  {
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
        readSharePreferenceMap
    )
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
        val longPressScriptList =
            LongPressMenuTool.makeLongPressScriptList(
                terminalFragment,
                imageLongPressMenuDirPath,
                imageLongPressMenuFileName,
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
            longPressScriptList,
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
        longPressScriptList: List<String>,
        longPressImageUrl: String,
        currentUrl: String,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            longPressImageDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val selectedJsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
                selectedMenuName,
                longPressScriptList,
            ) ?: return@setOnItemClickListener
            execJsFile(
                selectedJsPath,
                longPressImageUrl,
                currentUrl,
            )
            return@setOnItemClickListener
        }
    }

    private fun setListView(
        menuList: List<Pair<String, Int>>,
        longPressScriptList: List<String>,
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
            longPressScriptList,
            longPressImageUrl,
            currentUrl,
        )
    }


    private fun execJsFile(
        selectedJsPath: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        val selectedScriptNameOrPathObj = File(selectedJsPath)
        val execJsPath = LongPressMenuTool.makeExecJsPath(
            terminalFragment,
            currentAppDirPath,
            selectedScriptNameOrPathObj,
        )
        val imageLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL
                to longPressImageUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                to currentUrl
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            execJsPath,
            ReadText(execJsPath).textToList(),
            imageLongPressRepValMap
        )
    }

}
