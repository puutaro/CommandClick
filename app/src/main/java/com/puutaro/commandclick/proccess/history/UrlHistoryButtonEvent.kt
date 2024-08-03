package com.puutaro.commandclick.proccess.history

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private val takeUrlListNum = 400
    private var urlHistoryDialog: Dialog? = null
    private val urlHistoryMapKeys = UrlHistoryAdapter.Companion.UrlHistoryMapKey.values().map {
        it.key
    }
    private val titleKey = UrlHistoryAdapter.Companion.UrlHistoryMapKey.TITLE.key
    private val urlKey = UrlHistoryAdapter.Companion.UrlHistoryMapKey.URL.key
    private val iconBase64Key = UrlHistoryAdapter.Companion.UrlHistoryMapKey.ICON_BASE64_STR.key
    private val captureBase64Key = UrlHistoryAdapter.Companion.UrlHistoryMapKey.CAPTURE_BASE64_STR.key
    private val bottomFannelFileType = UrlHistoryAdapter.Companion.FileType.BOTTOM_FANNEL


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
        val urlHistoryListView = urlHistoryDialog?.findViewById<RecyclerView>(
            R.id.url_history_list_view
        )
        val catchSize = 50 * (context?.resources?.displayMetrics?.heightPixels ?: 0)
        urlHistoryListView?.setItemViewCacheSize(catchSize)
        val urlHistoryListViewLinearParams =
            urlHistoryListView?.layoutParams as LinearLayout.LayoutParams
        urlHistoryListViewLinearParams.weight = listLinearWeight
        val urlHistoryList = makeUrlHistoryList()
        val searchText = urlHistoryDialog?.findViewById<AppCompatEditText>(
            R.id.url_history_search_edit_text
        )
        val searchTextLinearParams =
            searchText?.layoutParams as LinearLayout.LayoutParams
        searchTextLinearParams.weight = searchTextLinearWeight
        urlHistoryListView.layoutManager =  GridLayoutManager(
            context,
            2,
            LinearLayoutManager.VERTICAL,
            false
        )
        val urlHistoryDisplayListAdapter = UrlHistoryAdapter(
            historyButtonInnerView.context,
            urlHistoryList.toMutableList()
        )
        urlHistoryListView.adapter = urlHistoryDisplayListAdapter
        urlHistoryListView.layoutManager?.scrollToPosition(
            urlHistoryDisplayListAdapter.itemCount - 1
        )
        makeSearchEditText(
            urlHistoryDisplayListAdapter,
            urlHistoryListView,
            searchText
        )
        urlHistoryDialog?.setOnCancelListener {
            editDialog()
        }
        urlHistoryDialog?.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        urlHistoryDialog?.window?.setGravity(Gravity.BOTTOM)
        urlHistoryDialog?.show()
        setUrlHistoryListViewOnItemClickListener(
            urlHistoryDisplayListAdapter,
            searchText
        )
        setUrlHistoryListViewOnCopyItemClickListener (
            urlHistoryDisplayListAdapter,
            searchText,
        )
        setUrlHistoryListViewOnDeleteItemClickListener(
            urlHistoryDisplayListAdapter,
            searchText
        )
    }

    private fun makeSearchEditText(
        urlHistoryListAdapter: UrlHistoryAdapter,
        urlHistoryListView: RecyclerView?,
        searchText: EditText
    ) {
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(
                    !searchText.hasFocus()
                ) return
                val updateHistoryList = makeSearchFilteredUrlHistoryList(searchText)
                val filteredCmdStrList = updateHistoryList.filter {
                    val title =  it.get(titleKey)?.lowercase()
                        ?: return@filter false
                    Regex(
                        s.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        title.lowercase()
                    )
                }
                urlHistoryListAdapter.urlHistoryMapList.clear()
                urlHistoryListAdapter.urlHistoryMapList.addAll(filteredCmdStrList)
                urlHistoryListAdapter.notifyDataSetChanged()
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(300)
                    }
                    urlHistoryListView?.layoutManager?.scrollToPosition(
                        urlHistoryListAdapter.itemCount - 1
                    )
                }
            }
        })
    }

    private fun setUrlHistoryListViewOnItemClickListener (
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        searchText: EditText,
    ){
        urlHistoryDisplayListAdapter.itemClickListener = object: UrlHistoryAdapter.OnItemClickListener {
            override fun onItemClick(holder: UrlHistoryAdapter.UrlHistoryViewHolder) {
                editDialog()
                val bindingAdapterPosition = holder.bindingAdapterPosition
                val filteredUrlHistoryMap =
                    makeSearchFilteredUrlHistoryList(
                        searchText
                    ).getOrNull(bindingAdapterPosition)
                        ?: return
                val selectedUrlHistoryMap =
                    urlHistoryDisplayListAdapter.urlHistoryMapList.filter {
                        urlHistoryMap ->
                        equalUrlHistoryMapKeys(
                            urlHistoryMap,
                            filteredUrlHistoryMap
                        )
                    }.firstOrNull()
                        ?: return
                val selectedUrl = selectedUrlHistoryMap.get(urlKey)
                    ?.let {
                        ScriptPreWordReplacer.settingValReplace(
                            it,
                            currentAppDirPath
                        )
                    } ?: return
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
                    return
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
                    return
                } else if (
                    fragmentTag?.startsWith(
                        FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str
                    ) == true
                ) {
                    val listener = context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                    listener?.onLaunchUrlByWebViewForEdit(
                        selectedUrl,
                    )
                    return
                }
            }
        }
    }

    private fun setUrlHistoryListViewOnCopyItemClickListener (
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        searchText: EditText,
    ){
        urlHistoryDisplayListAdapter.copyItemClickListener = object: UrlHistoryAdapter.OnCopyItemClickListener {
            override fun onItemClick(holder: UrlHistoryAdapter.UrlHistoryViewHolder) {
                val bindingAdapterPosition = holder.bindingAdapterPosition
                val filteredUrlHistoryMap =
                    makeSearchFilteredUrlHistoryList(
                        searchText
                    ).getOrNull(bindingAdapterPosition)
                        ?: return
                val selectedUrlHistoryMap =
                    urlHistoryDisplayListAdapter.urlHistoryMapList.filter {
                        urlHistoryMap ->
                        equalUrlHistoryMapKeys(
                            urlHistoryMap,
                            filteredUrlHistoryMap
                        )
                    }.firstOrNull()
                        ?: return
                val selectedUrl = selectedUrlHistoryMap.get(urlKey)
                    ?.let {
                        ScriptPreWordReplacer.settingValReplace(
                            it,
                            currentAppDirPath
                        )
                    } ?: return
                ToastUtils.showShort("copy")
                val clipboard = context?.getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(
                    "url",
                    selectedUrl
                )
                clipboard.setPrimaryClip(clip)
            }
        }
    }


    private fun setUrlHistoryListViewOnDeleteItemClickListener (
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        searchText: EditText,
    ){
        urlHistoryDisplayListAdapter.deleteItemClickListener = object: UrlHistoryAdapter.OnDeleteItemClickListener {
            override fun onItemClick(holder: UrlHistoryAdapter.UrlHistoryViewHolder) {
                val bindingAdapterPosition = holder.bindingAdapterPosition
                val filteredUrlHistoryMap =
                    makeSearchFilteredUrlHistoryList(
                        searchText
                    ).getOrNull(bindingAdapterPosition)
                        ?: return
                val selectedUrlHistoryMap =
                    urlHistoryDisplayListAdapter.urlHistoryMapList.filter {
                        urlHistoryMap ->
                        equalUrlHistoryMapKeys(
                            urlHistoryMap,
                            filteredUrlHistoryMap
                        )
                    }.firstOrNull()
                        ?: return

                val selectedTitle = selectedUrlHistoryMap.get(titleKey)
                    ?.let {
                        ScriptPreWordReplacer.settingValReplace(
                            it,
                            currentAppDirPath
                        )
                    } ?: return
                val selectedUrl = selectedUrlHistoryMap.get(urlKey)
                    ?.let {
                        ScriptPreWordReplacer.settingValReplace(
                            it,
                            currentAppDirPath
                        )
                    } ?: return
                execDeleteUrl(
                    selectedTitle,
                    selectedUrl,
                    urlHistoryDisplayListAdapter,
                    bindingAdapterPosition
                )
            }
        }
    }

    private fun makeUrlHistoryList(): List<Map<String, String>> {
        val urlHistoryList = makeCompleteListSourceNoJsExclude(
            currentAppDirPath,
        ).reversed()
        val iconHistoryList = UrlHistoryIconTool.makeUrlIconList(currentAppDirPath)
        val urlCaptureList = UrlCaptureHistoryTool.makeUrlCaptureList(currentAppDirPath)
        return urlHistoryList.map {
            map ->
            val url = map.get(urlKey) ?: String()
            val base64IconStr =
                map.get(iconBase64Key) ?: iconHistoryList.firstOrNull {
                    val iconUrl = it.get(urlKey)
                    if (
                        url != iconUrl
                    ) return@firstOrNull false
                    true
                }?.get(iconBase64Key) ?: String()
            val base64UrlCaptureStr = urlCaptureList.firstOrNull {
                val iconUrl = it.get(urlKey)
                if(
                    url != iconUrl
                ) return@firstOrNull false
                true
            }?.get(captureBase64Key) ?: String()
            val title = map.get(titleKey)
                ?: String()
            mapOf(
                titleKey to title,
                urlKey to url,
                iconBase64Key to base64IconStr,
                captureBase64Key to base64UrlCaptureStr,
            )
        }
    }

    private fun makeCompleteListSourceNoJsExclude(
        currentAppDirPath: String?,
    ):List<Map<String, String>> {
        if(
            currentAppDirPath.isNullOrEmpty()
        ) return emptyList()
        return makeBottomScriptUrlList() + makeUrlListFromHistory()
    }

    private fun makeUrlListFromHistory(): List<Map<String, String>>{
        val usedTitle = mutableSetOf<String>()
        val usedUrl = mutableSetOf<String>()
        return ReadText(
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
            }.map {
                    titleAndUrl ->
                val titleAndUrlList = titleAndUrl.split("\t")
                val title = titleAndUrlList.firstOrNull() ?: String()
                val url = titleAndUrlList.getOrNull(1) ?: String()
                mapOf(
                    titleKey to title,
                    urlKey to url,
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

    private fun makeBottomScriptUrlList(
    ): List<Map<String, String>> {
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
    ): List<Map<String, String>> {
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
            mapOf(
                titleKey to title,
                urlKey to replaceUrl,
                iconBase64Key to bottomFannelFileType.name
            )
        }.filter {
            it.isNotEmpty()
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
    ): List<Map<String, String>> {
        return makeUrlHistoryList().filter {
            val urlTitleSource =
                it.get(titleKey)  ?:String()
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

    private fun execDeleteUrl(
        selectedTitle: String?,
        selectedUrl: String?,
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        bindingAdapterPosition: Int,
    ){
        val bottomScriptUrlList = makeBottomScriptUrlList()
        val isBottomScript = bottomScriptUrlList.filter {
            map ->
            val url = map.get(urlKey)
                ?: return@filter false
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
        urlHistoryDisplayListAdapter.urlHistoryMapList.removeAt(bindingAdapterPosition)
        urlHistoryDisplayListAdapter.notifyItemRemoved(bindingAdapterPosition)
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


    private fun equalUrlHistoryMapKeys(
        urlHistoryMap: Map<String, String>,
        filteredUrlHistoryMap: Map<String, String>,
    ): Boolean{
        return urlHistoryMapKeys.all {
            val title = urlHistoryMap.get(titleKey)
                ?: return@all false
            title == filteredUrlHistoryMap.get(titleKey)

        }
    }

    private fun editDialog(){
        urlHistoryDialog?.dismiss()
        urlHistoryDialog = null
    }
}