package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.extra_args.ShellTool
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.tsv.TsvTool


class UrlHistoryAddToTsv (
    private val toolbarButtonArgsMaker: ToolbarButtonArgsMaker
){

    private val editFragment = toolbarButtonArgsMaker.fragment
    private val context = editFragment.context
    private var urlHistoryToTsvDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel
    private val toolbarButtonConfigMap = toolbarButtonArgsMaker.toolbarButtonConfigMap

    fun invoke(){
        if(
            editFragment !is EditFragment
        ) return
        editFragment.busyboxExecutor
        if(
            context == null
        ) return
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
        }

        setListView()
        urlHistoryToTsvDialog?.setOnCancelListener {
            urlHistoryToTsvDialog?.dismiss()
        }
        urlHistoryToTsvDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        urlHistoryToTsvDialog?.window?.setGravity(Gravity.BOTTOM)
        urlHistoryToTsvDialog?.show()
    }

    private fun setListView() {
        if(
            editFragment !is EditFragment
        ) return
        if(
            context == null
        ) return
        val urlHistoryToTsvListView =
            urlHistoryToTsvDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            makeUrlHistoryList().toMutableList()
        )
        urlHistoryToTsvListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForUrlToTsv(
            editFragment,
            urlHistoryToTsvListView,
        )
        invokeItemSetLongClickListenerForUrlToTsv(
            editFragment,
            urlHistoryToTsvListView,
        )
    }

    private fun makeUrlHistoryList(): List<Pair<String, Int>> {
        return takeFromUrlHistoryList().map {
            val titleUrlList = it.split("\t")
            val title = titleUrlList.first()
            title to icons8Wheel
        }
    }

    private fun takeFromUrlHistoryList(): List<String> {
        if(
            editFragment !is EditFragment
        ) return emptyList()
        val historyFirstExtractNum = 50
        val busyboxExecutor = editFragment.busyboxExecutor
        val clickConfigMap = toolbarButtonArgsMaker.createClickConfigMap()
        val extraMap = ExtraArgsTool.createExtraMapFromMap(
            clickConfigMap,
            "!",
        )
        val shellCon = ExtraArgsTool.makeShellCon(
            extraMap,
        ).let {
            if(
                it.isNotEmpty()
            ) return@let it
            defaultShellCon
        }
        val extraArgsMap = ExtraArgsTool.createExtraMapFromMap(
            extraMap,
            "&",
        )
        val takeLines = 5
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )

        val urlHistoryParentDirPath = listOf(
            currentAppDirPath,
            UsePath.cmdclickUrlSystemDirRelativePath
        ).joinToString("/")
        val srcTsvCon = ReadText(
            urlHistoryParentDirPath,
            UsePath.cmdclickUrlHistoryFileName,
        ).textToList().let {
            TsvTool.uniqByTitle(it)
        }.take(historyFirstExtractNum).joinToString("\n")
        return ShellTool.filter(
            srcTsvCon,
            busyboxExecutor,
            shellCon,
            extraArgsMap
        ).split("\n").filter {
            it.split("\t").size == 2
        }.take(takeLines).reversed()
    }


    private fun invokeItemSetClickListnerForUrlToTsv(
        editFragment: EditFragment,
        urlHistoryToTsvListView: ListView,
    ) {
        urlHistoryToTsvListView.setOnItemClickListener {
                parent, View, pos, id ->
            val menuListAdapter = urlHistoryToTsvListView.adapter as SubMenuAdapter
            val selectedTitle = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val selectedUrlHistoryLine = takeFromUrlHistoryList().find {
                it.startsWith(selectedTitle)
            } ?: return@setOnItemClickListener
            ExecAddForListIndexAdapter.execAddForTsv(
                editFragment,
                selectedUrlHistoryLine
            )
            urlHistoryToTsvDialog?.dismiss()
            return@setOnItemClickListener
        }
    }

    private fun invokeItemSetLongClickListenerForUrlToTsv(
        editFragment: EditFragment,
        urlHistoryToTsvListView: ListView,
    ) {
        urlHistoryToTsvListView.setOnItemLongClickListener {
                arg0, arg1, pos, id ->
            val menuListAdapter = urlHistoryToTsvListView.adapter as SubMenuAdapter
            val selectedTitle = menuListAdapter.getItem(pos)
                ?: return@setOnItemLongClickListener false
            val selectedUrlHistoryLine = takeFromUrlHistoryList().find {
                it.startsWith(selectedTitle)
            } ?: return@setOnItemLongClickListener false
            val selectedUrl = selectedUrlHistoryLine.split("\t").lastOrNull()
                ?: return@setOnItemLongClickListener false
            val webSearcherName = UrlFileSystems.Companion.FirstCreateFannels.WebSearcher.str +
                    UsePath.JS_FILE_SUFFIX
            val readSharePreferenceMap = editFragment.readSharePreferenceMap
            val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreferenceMap,
                SharePrefferenceSetting.current_app_dir
            )
            ExecJsLoad.execExternalJs(
                editFragment,
                currentAppDirPath,
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

