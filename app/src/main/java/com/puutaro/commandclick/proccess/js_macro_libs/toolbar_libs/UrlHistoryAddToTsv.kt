package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.ShellTool
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File


object UrlHistoryAddToTsv {

    private var urlHistoryToTsvDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel

    fun invoke(
         editFragment: EditFragment,
         jsActionMap: Map<String, String>
    ){
        val context = editFragment.context ?: return
        urlHistoryToTsvDialog = Dialog(
            context
        )
        urlHistoryToTsvDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val urlHistoryToTsvDialogTitle = urlHistoryToTsvDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        urlHistoryToTsvDialogTitle?.text = "Select url"
        val urlHistoryToTsvDialogMessage = urlHistoryToTsvDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        urlHistoryToTsvDialogMessage?.isVisible = false
        val urlHistoryToTsvDialogSearchEditText = urlHistoryToTsvDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        urlHistoryToTsvDialogSearchEditText?.isVisible = false
        val cancelButton = urlHistoryToTsvDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            urlHistoryToTsvDialog?.dismiss()
            urlHistoryToTsvDialog = null
        }

        setListView(
            editFragment,
            jsActionMap,
        )
        urlHistoryToTsvDialog?.setOnCancelListener {
            urlHistoryToTsvDialog?.dismiss()
            urlHistoryToTsvDialog = null
        }
        urlHistoryToTsvDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        urlHistoryToTsvDialog?.window?.setGravity(Gravity.BOTTOM)
        urlHistoryToTsvDialog?.show()
    }

    private fun setListView(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>
    ) {
        val context = editFragment.context ?: return
        val urlHistoryToTsvListView =
            urlHistoryToTsvDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            makeUrlHistoryList(
                editFragment,
                jsActionMap,
            ).toMutableList()
        )
        urlHistoryToTsvListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForUrlToTsv(
            editFragment,
            jsActionMap,
            urlHistoryToTsvListView,
        )
        invokeItemSetLongClickListenerForUrlToTsv(
            editFragment,
            jsActionMap,
            urlHistoryToTsvListView,
        )
    }

    private fun makeUrlHistoryList(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>
    ): List<Pair<String, Int>> {
        return takeFromUrlHistoryList(
            editFragment,
            jsActionMap
        ).map {
            val titleUrlList = it.split("\t")
            val title = titleUrlList.first()
            title to icons8Wheel
        }
    }

    private fun takeFromUrlHistoryList(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>
    ): List<String> {
        val historyFirstExtractNum = 50
        val busyboxExecutor = editFragment.busyboxExecutor
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()

        val shellCon = EditSettingExtraArgsTool.makeShellCon(
            argsMap,
        ).let {
            if(
                it.isNotEmpty()
            ) return@let it
            defaultShellCon
        }
        val takeLines = 5
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )

        val urlHistoryParentDirPath = File(
            UsePath.cmdclickDefaultAppDirPath,
            UsePath.cmdclickUrlSystemDirRelativePath
        ).absolutePath
        val srcTsvCon = ReadText(
            File(
                urlHistoryParentDirPath,
                UsePath.cmdclickUrlHistoryFileName,
            ).absolutePath
        ).textToList().let {
            TsvTool.uniqByTitle(it)
        }.take(historyFirstExtractNum).joinToString("\n")
        return ShellTool.filter(
            srcTsvCon,
            busyboxExecutor,
            shellCon,
            argsMap
        ).split("\n").filter {
            it.split("\t").size == 2
        }.take(takeLines).reversed()
    }


    private fun invokeItemSetClickListnerForUrlToTsv(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
        urlHistoryToTsvListView: ListView,
    ) {
        urlHistoryToTsvListView.setOnItemClickListener {
                parent, View, pos, id ->
            val menuListAdapter = urlHistoryToTsvListView.adapter as SubMenuAdapter
            val selectedTitle = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val selectedUrlHistoryLine = takeFromUrlHistoryList(
                editFragment,
                jsActionMap,
            ).find {
                it.startsWith(selectedTitle)
            } ?: return@setOnItemClickListener
            ExecAddForListIndexAdapter.execAddForTsv(
                editFragment,
                selectedUrlHistoryLine
            )
            urlHistoryToTsvDialog?.dismiss()
            urlHistoryToTsvDialog = null
            return@setOnItemClickListener
        }
    }

    private fun invokeItemSetLongClickListenerForUrlToTsv(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
        urlHistoryToTsvListView: ListView,
    ) {
        urlHistoryToTsvListView.setOnItemLongClickListener {
                arg0, arg1, pos, id ->
            val menuListAdapter = urlHistoryToTsvListView.adapter as SubMenuAdapter
            val selectedTitle = menuListAdapter.getItem(pos)
                ?: return@setOnItemLongClickListener false
            val selectedUrlHistoryLine = takeFromUrlHistoryList(
                editFragment,
                jsActionMap,
            ).find {
                it.startsWith(selectedTitle)
            } ?: return@setOnItemLongClickListener false
            val selectedUrl = selectedUrlHistoryLine.split("\t").lastOrNull()
                ?: return@setOnItemLongClickListener false
            val webSearcherName = SystemFannel.webSearcher
//            val fannelInfoMap = editFragment.fannelInfoMap
//            val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//                fannelInfoMap
//            )
            ExecJsLoad.execExternalJs(
                editFragment,
//                currentAppDirPath,
                webSearcherName,
                listOf(selectedUrl),
            )
           true
        }
    }

    private val defaultShellCon = """
        echo "${ShellTool.shellConReplaceMark}" | \
            ${'$'}{b} awk '{
            	if(\
            		${'$'}0 !~ /\thttp:\/\// \
            		&& ${'$'}0 !~ /\thttps:\/\// \
            	) next
            	print ${'$'}0
            }'
        """.trimIndent()
}

