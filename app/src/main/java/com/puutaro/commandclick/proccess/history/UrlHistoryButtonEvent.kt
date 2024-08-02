package com.puutaro.commandclick.proccess.history

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.ScriptArgsMapList
import com.puutaro.commandclick.component.adapter.UrlHistoryAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.UrlTool
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

class UrlHistoryButtonEvent(
    private val fragment: androidx.fragment.app.Fragment,
    private val fannelInfoMap: Map<String, String>,
) {
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val fragmentTag = fragment.tag
    private val context = fragment.context
    private val tabReplaceStr = "\t"
    private val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private val takeUrlListNum = 400
    private var urlHistoryDialog: Dialog? = null


    fun invoke(
        historyButtonInnerView: View,
    ){
        when(fragment){
            is EditFragment -> {
                val isCmdValEdit =
                    fragment.editTypeSettingKey ==
                            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                if(
                    !isCmdValEdit
                ) return
                val onShortcut = FannelInfoTool.getOnShortcut(
                    fragment.fannelInfoMap
                ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
                if(!onShortcut) return
            }
        }
        terminalViewModel.onDialog = true
        val historyButtonInnerViewContext = historyButtonInnerView.context
        urlHistoryDialog = Dialog(
            historyButtonInnerViewContext,
        )
        urlHistoryDialog?.setContentView(
            R.layout.url_history_list_view_layout
        )
        val urlHistoryListView = urlHistoryDialog?.findViewById<ListView>(
            R.id.url_history_list_view
        )
        val urlHistoryListViewLinearParams =
            urlHistoryListView?.layoutParams as LinearLayout.LayoutParams
        urlHistoryListViewLinearParams.weight = listLinearWeight
        val urlHistoryList = makeUrlHistoryList()
        val searchText = urlHistoryDialog?.findViewById<EditText>(
            R.id.url_history_search_edit_text
        )
        val searchTextLinearParams =
            searchText?.layoutParams as LinearLayout.LayoutParams
        searchTextLinearParams.weight = searchTextLinearWeight

        val urlHistoryDisplayListAdapter = UrlHistoryAdapter(
            historyButtonInnerView.context,
            R.layout.url_history_list_view_adapter_layout,
            urlHistoryList.toMutableList()
        )
        urlHistoryListView.adapter = urlHistoryDisplayListAdapter
        urlHistoryListView.setSelection(
            urlHistoryDisplayListAdapter.count
        )
        makeSearchEditText(
            urlHistoryListView,
            urlHistoryDisplayListAdapter,
            searchText
        )
        urlHistoryDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                urlHistoryDialog?.dismiss()
                urlHistoryDialog = null
                terminalViewModel.onDialog = false
            }
        })
        urlHistoryDialog?.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        urlHistoryDialog?.window?.setGravity(Gravity.BOTTOM)
        urlHistoryDialog?.show()
        setUrlHistoryListViewOnItemClickListener(
            urlHistoryListView,
            urlHistoryList,
            searchText
        )

        setUrlHistoryListViewOnItemLongClickListener (
            urlHistoryListView,
            searchText,
        )
    }

    private fun makeSearchEditText(
        urlHistoryListView: ListView,
        urlHistoryListAdapter: UrlHistoryAdapter,
        searchText: EditText
    ) {
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = makeSearchFilteredUrlHistoryList(searchText)
                urlHistoryListAdapter.clear()
                urlHistoryListAdapter.addAll(filteredUrlHistoryList)
                urlHistoryListAdapter.notifyDataSetChanged()
                urlHistoryListView.setSelection(
                    urlHistoryListAdapter.count
                )
            }
        })
    }


    private fun setUrlHistoryListViewOnItemClickListener (
        urlHistoryListView: ListView,
        urlHistoryList: List<String>,
        searchText: EditText,
    ){
        urlHistoryListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            urlHistoryDialog?.dismiss()
            urlHistoryDialog = null
            terminalViewModel.onDialog = false
            val filteredUrlHistoryTitle =
                makeSearchFilteredUrlHistoryList(
                    searchText
                ).getOrNull(pos)
                        ?: return@setOnItemClickListener
            val selectedUrlSource =
                urlHistoryList.filter {
                    it.startsWith(filteredUrlHistoryTitle)
                }.firstOrNull()
                    ?: return@setOnItemClickListener
            val selectedUrl = selectedUrlSource.split(
                tabReplaceStr
            ).getOrNull(1)
                ?.let {
                    ScriptPreWordReplacer.settingValReplace(
                        it,
                        currentAppDirPath
                    )
                } ?: return@setOnItemClickListener
            if (
                selectedUrl.endsWith(
                    UsePath.SHELL_FILE_SUFFIX
                )
                || selectedUrl.endsWith(
                    UsePath.JS_FILE_SUFFIX
                )
                || selectedUrl.endsWith(
                    UsePath.JSX_FILE_SUFFIX,
                )
            ) {
                execScriptFile(selectedUrl)
                return@setOnItemClickListener
            }

            if (
                fragmentTag == context?.getString(
                    R.string.command_index_fragment
                )
            ) {
                val listener = context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
                listener?.onLaunchUrlByWebView(
                    selectedUrl,
                )
                return@setOnItemClickListener
            } else if (
                fragmentTag?.startsWith(
                    FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str
                ) == true
            ) {
                val listener = context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listener?.onLaunchUrlByWebViewForEdit(
                    selectedUrl,
                )
                return@setOnItemClickListener
            }
        }
    }

    private fun makeUrlHistoryList(): List<String> {
        val urlHistoryList =  makeCompleteListSourceNoJsExclude(
            currentAppDirPath,
        ).reversed()
        val iconHistoryList = UrlIconTool.makeUrlIconList(currentAppDirPath)
        return urlHistoryList.map {
            titleAndUrl ->
            val url = titleAndUrl
                .split("\t").getOrNull(1) ?: String()
            val base64Str = iconHistoryList.firstOrNull {
                val iconUrl = it.first
                if(
                    url != iconUrl
                ) return@firstOrNull false
                true
            }?.second ?: String()
            "${titleAndUrl}\t${base64Str}"
        }
    }

    private fun makeCompleteListSourceNoJsExclude(
        currentAppDirPath: String?,
    ):List<String> {
        if(
            currentAppDirPath.isNullOrEmpty()
        ) return emptyList()
        val usedTitle = mutableSetOf<String>()
        val usedUrl = mutableSetOf<String>()
        return makeBottomScriptUrlList() + ReadText(
            File(
                "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
                UsePath.cmdclickUrlHistoryFileName
            ).absolutePath
        ).textToList()
            .distinct()
            .take(takeUrlListNum)
            .filter {
                isNotDuplicate(
                    it,
                    usedTitle,
                    usedUrl,
                )
            }
    }

    private fun execScriptFile(
        selectedUrlSource: String
    ) {
        val shellFileObj = File(selectedUrlSource)
        if(!shellFileObj.isFile) return
        val parentDirPath =
            shellFileObj.parent ?: return
        ExecJsOrSellHandler.handle(
            fragment,
            parentDirPath,
            shellFileObj.name,
            args = ScriptArgsMapList.ScriptArgsName.URL_HISTORY_CLICK.str
        )
    }

    private fun setUrlHistoryListViewOnItemLongClickListener (
        urlHistoryListView: ListView,
        searchText: EditText,
    ){
        urlHistoryListView.setOnItemLongClickListener {
                parent, listSelectedView, position, id ->
            val popup = PopupMenu(
                context,
                listSelectedView,
            )
            val selectedTitleToUrl = makeTitleToUrl(
                searchText,
                position,
            )
            val selectedTitle = selectedTitleToUrl.first
            val selectedUrl =
                selectedTitleToUrl.second
                ?: return@setOnItemLongClickListener true
            val inflater = popup.menuInflater
            inflater.inflate(
                R.menu.history_admin_menu,
                popup.menu
            )
            popup.menu.add(
                UrlHistoryMenuEnums.COPY_URL.groupId,
                UrlHistoryMenuEnums.COPY_URL.itemId,
                UrlHistoryMenuEnums.COPY_URL.order,
                UrlHistoryMenuEnums.COPY_URL.itemName,

                )
            popup.menu.add(
                UrlHistoryMenuEnums.DELETE.groupId,
                UrlHistoryMenuEnums.DELETE.itemId,
                UrlHistoryMenuEnums.DELETE.order,
                UrlHistoryMenuEnums.DELETE.itemName,

                )
            popup.setOnMenuItemClickListener {
                    menuItem ->
                terminalViewModel.onDialog = false
                when(menuItem.itemId){
                    UrlHistoryMenuEnums.COPY_URL.itemId -> {
                        ToastUtils.showShort("copy")
                        execCopyUrl(
                            listSelectedView,
                            selectedUrl,
                        )
                    }
                    UrlHistoryMenuEnums.DELETE.itemId -> {
                        ToastUtils.showShort("delete")
                        execDeleteUrl(
                            selectedTitle,
                            selectedUrl,
                            urlHistoryListView

                        )
                    }
                }
                true
            }
            popup.show()
            true
        }
    }

    private fun makeTitleToUrl(
        searchText: EditText,
        position: Int,
    ): Pair<String?, String?> {
        return makeSearchFilteredUrlHistoryList(
            searchText
        ).getOrNull(position)?.let {
            val titleAndUrlList = it.split("\t")
            if (
                titleAndUrlList.size != 2
            ) return@let null to null
            val title = titleAndUrlList.first()
            val url = titleAndUrlList.last()
            title to url
        } ?: (null to null)
    }

    private fun makeBottomScriptUrlList(
    ): List<String> {
        val fannelName = when(fragment) {
            is CommandIndexFragment
            -> String()
            else
            -> FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
        }
        val replaceVariableMap = when(
            fannelName.isEmpty()
        ) {
            true -> null
            else -> SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                context,
                "${currentAppDirPath}/${fannelName}",
            )
        }
        val bottomScriptUrlList =
            when(fragment){
                is CommandIndexFragment
                -> fragment.bottomScriptUrlList
                is EditFragment
                -> fragment.bottomScriptUrlList
                else
                -> return emptyList()
            }
        return convertBottomScriptUrlListToUrlList(
            bottomScriptUrlList,
            replaceVariableMap,
            fannelName,
        )
    }

    private fun convertBottomScriptUrlListToUrlList(
        bottomScriptUrlList: List<String>,
        replaceVariableMap: Map<String, String>?,
        fannelName: String,
    ): List<String> {
        return execSetRepalceVariable(
            bottomScriptUrlList,
            replaceVariableMap,
            fannelName,
        ).map {
                url ->
            val replaceUrl = ScriptPreWordReplacer.replace(
                url,
                currentAppDirPath,
                String(),
            )
            val title = url.split("/")
                .lastOrNull()
                ?: String()
            "${title}\t${replaceUrl}"
        }.filter {
            it.trim().isNotEmpty()
        }.reversed()
    }

    private fun execSetRepalceVariable(
        bottomScriptUrlList: List<String>,
        replaceVariableMap: Map<String, String>?,
        fannelName: String,
    ): List<String> {
        return bottomScriptUrlList.joinToString("\n").let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                replaceVariableMap,
                currentAppDirPath,
                fannelName
            )
        }.split("\n")
    }

    private fun isNotDuplicate(
        historySourceRow: String,
        usedTitle: MutableSet<String>,
        usedUrl: MutableSet<String>,
    ): Boolean {
        val historySourceRowList = historySourceRow
            .split("\t")
        val duliEntryTitle = historySourceRowList
            .firstOrNull()
            ?: return false
        val duliEntryUrl = historySourceRowList
            .getOrNull(1)
            ?: return false
        return if(
            usedTitle.contains(duliEntryTitle)
            || usedUrl.contains(duliEntryUrl)
        ) {
            false
        } else {
            usedTitle.add(duliEntryTitle)
            usedUrl.add(duliEntryUrl)
            true
        }
    }

    private fun makeSearchFilteredUrlHistoryList(
        searchText: EditText
    ): List<String> {
        return makeUrlHistoryList().filter {
            val urlTitleSource =
                it.split(tabReplaceStr)
                    .firstOrNull() ?:String()
            val title = UrlTool.trimTitle(
                urlTitleSource
            )
            Regex(
                searchText.text.toString()
                    .lowercase()
                    .replace("\n", "")
            ).containsMatchIn(
                title.lowercase()
            )
        }
    }


    private fun execCopyUrl(
        listSelectedView: View,
        selectedLine: String?
    ){
        val clipboard = listSelectedView.context?.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(
            "url",
            selectedLine
        )
        clipboard.setPrimaryClip(clip)
    }
    private fun execDeleteUrl(
        selectedTitle: String?,
        selectedUrl: String?,
        urlHistoryListView: ListView,
    ){
        val bottomScriptUrlList = makeBottomScriptUrlList()
        val isBottomScript = bottomScriptUrlList.filter {
            val url = it.split("\t").getOrNull(1)
            url == selectedUrl
        }.isNotEmpty()
        if(isBottomScript) {
            ToastUtils.showShort(
                "Bottom script must be deleted bellow\n" +
                    "\tat ${CommandClickScriptVariable.HOME_SCRIPT_URLS_PATH}\n" +
                    "\t\tin start up script"
            )
            return
        }
        val urlHistoryDirPath = "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val cmdclickUrlHistoryFilePath = File(
            urlHistoryDirPath,
            cmdclickUrlHistoryFileName
        ).absolutePath
        val urlHistoryCon = makeDeletedUrlHistoryCon(
            cmdclickUrlHistoryFilePath,
            selectedTitle,
            selectedUrl,
        )
        FileSystems.writeFile(
            cmdclickUrlHistoryFilePath,
            urlHistoryCon
        )
        val cmdclickUrlHistoryBkFilePath = File(
            urlHistoryDirPath,
            UsePath.cmdclickUrlHistoryBackupFileName
        ).absolutePath
        FileSystems.writeFile(
            cmdclickUrlHistoryBkFilePath,
            urlHistoryCon
        )
        val urlHistoryList = makeUrlHistoryList()
        val urlHistoryAdapter = urlHistoryListView.adapter as UrlHistoryAdapter
        urlHistoryAdapter.clear()
        urlHistoryAdapter.addAll(urlHistoryList)
        urlHistoryAdapter.notifyDataSetChanged()
    }

    private fun makeDeletedUrlHistoryCon(
        cmdclickUrlHistoryFilePath: String,
        selectedTitle: String?,
        selectedUrl: String?,
    ): String {
        return ReadText(
            cmdclickUrlHistoryFilePath
        ).textToList().filter {
            val titleAndUrlList = it.split("\t")
            val title = titleAndUrlList.firstOrNull()
            val url = titleAndUrlList.getOrNull(1)
            val isNotEqualTitle =
                title != selectedTitle
                        && !title.isNullOrEmpty()
            val isNotEqualUrl =
                url != selectedUrl
            isNotEqualTitle
                    && isNotEqualUrl
        }.joinToString("\n")
    }
}