package com.puutaro.commandclick.proccess.history.url_history

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.puutaro.commandclick.proccess.history.libs.HistoryShareImage
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.util.UrlTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class UrlHistoryButtonEvent(
    private val fragment: androidx.fragment.app.Fragment,
    private val fannelInfoMap: Map<String, String>,
) {
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    private val fragmentTag = fragment.tag
    private val context = fragment.context
    private val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private val takeUrlListNum = 400
    private var urlHistoryDialog: Dialog? = null
    private val titleKey = UrlHistoryAdapter.Companion.UrlHistoryMapKey.TITLE.key
    private val urlKey = UrlHistoryAdapter.Companion.UrlHistoryMapKey.URL.key
    private val iconBase64Key = UrlHistoryAdapter.Companion.UrlHistoryMapKey.ICON_BASE64_STR.key
    private val bottomFannelFileType = UrlHistoryAdapter.Companion.FileType.BOTTOM_FANNEL
    private val urlHistoryMapKeys = listOf(
        titleKey,
        urlKey,
    )


    fun invoke(
    ){
        if(
            context == null
        ) return
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
        urlHistoryDialog = Dialog(
            context,
        )
        urlHistoryDialog?.setContentView(
            R.layout.url_history_list_view_layout
        )
        val urlHistoryListView = urlHistoryDialog?.findViewById<RecyclerView>(
            R.id.url_history_list_view
        )
//        val catchSize = 50 * (context.resources?.displayMetrics?.heightPixels ?: 0)
//        urlHistoryListView?.setItemViewCacheSize(catchSize)
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
        urlHistoryListView.layoutManager = GridLayoutManager(
            context,
            2,
            LinearLayoutManager.VERTICAL,
            false
        )
        val currentUrl = TargetFragmentInstance().getCurrentTerminalFragmentFromFrag(fragment.activity).let {
            terminalFragment ->
            if(
                terminalFragment == null
                || !terminalFragment.isVisible
                || terminalFragment.view?.height == 0
            ) return@let null
            terminalFragment.binding.terminalWebView.url
        }
        val urlHistoryDisplayListAdapter = UrlHistoryAdapter(
            fragment,
//            cmdclickDefaultAppDirPath,
            urlHistoryList.toMutableList(),
            currentUrl,
        )
        urlHistoryListView.setItemViewCacheSize(Integer.MIN_VALUE)
        urlHistoryListView.recycledViewPool.clear()
        urlHistoryListView.setItemViewCacheSize(0)
        urlHistoryListView.adapter = urlHistoryDisplayListAdapter
        urlHistoryListView.layoutManager?.scrollToPosition(
            urlHistoryDisplayListAdapter.itemCount - 1
        )
        makeSearchEditText(
            urlHistoryDisplayListAdapter,
            urlHistoryListView,
            searchText
        )
        setItemTouchHelper(
            urlHistoryListView,
            urlHistoryDisplayListAdapter,
            searchText,
        )
        urlHistoryDialog?.setOnCancelListener {
            exitDialog(urlHistoryListView)
        }
        urlHistoryDialog?.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        urlHistoryDialog?.window?.setGravity(Gravity.BOTTOM)
        urlHistoryDialog?.show()
        setUrlHistoryListViewOnItemClickListener(
            urlHistoryListView,
            urlHistoryDisplayListAdapter,
            searchText
        )
        setUrlHistoryListViewOnLogoItemClickListener (
            urlHistoryDisplayListAdapter,
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

    private fun setItemTouchHelper(
        recyclerView: RecyclerView,
        urlHistoryAdapter: UrlHistoryAdapter,
        searchText: AppCompatEditText,
    ){
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if(
                        direction != ItemTouchHelper.LEFT
                        && direction != ItemTouchHelper.RIGHT
                    ) return
                    val position = viewHolder.layoutPosition
                    execDeleteUrl(
                        urlHistoryAdapter,
                        searchText,
                        position
                    )
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }

    private fun makeSearchEditText(
        urlHistoryListAdapter: UrlHistoryAdapter,
        urlHistoryListView: RecyclerView?,
        searchText: AppCompatEditText
    ) {
        var updateRecyclerJob: Job? = null
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
                updateRecyclerJob?.cancel()
                updateRecyclerJob = CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(200)
                    }
                    urlHistoryListView?.layoutManager?.scrollToPosition(
                        urlHistoryListAdapter.itemCount - 1
                    )
                }
            }
        })
    }

    private fun setUrlHistoryListViewOnItemClickListener (
        urlHistoryListView: RecyclerView,
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        searchText: AppCompatEditText,
    ){
        urlHistoryDisplayListAdapter.itemClickListener = object: UrlHistoryAdapter.OnItemClickListener {
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
                            cmdclickDefaultAppDirPath
                        )
                    } ?: let {
                    exitDialog(urlHistoryListView)
                    return
                    }
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
                    exitDialog(urlHistoryListView)
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
                    exitDialog(urlHistoryListView)
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
                    exitDialog(urlHistoryListView)
                    return
                }
                exitDialog(urlHistoryListView)
            }
        }
    }

    private fun setUrlHistoryListViewOnLogoItemClickListener (
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
    ){
        urlHistoryDisplayListAdapter.logoItemClickListener = object: UrlHistoryAdapter.OnLogoItemClickListener {
            override fun onItemClick(holder: UrlHistoryAdapter.UrlHistoryViewHolder) {
                val urlHistoryAdapterRelativeLayout = holder.urlHistoryAdapterRelativeLayout
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main){
                        ToastUtils.showShort("share")
                    }
                    val pngImagePathObj = HistoryShareImage.makePngImageFromView(
                        context,
                        urlHistoryAdapterRelativeLayout
                    ) ?: return@launch
                    withContext(Dispatchers.Main) {
                        IntentVariant.sharePngImage(
                            pngImagePathObj,
                            context,
                        )
                    }
                }

            }
        }
    }

    private fun setUrlHistoryListViewOnCopyItemClickListener (
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        searchText: AppCompatEditText,
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
                            cmdclickDefaultAppDirPath
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
        searchText: AppCompatEditText,
    ){
        urlHistoryDisplayListAdapter.deleteItemClickListener = object: UrlHistoryAdapter.OnDeleteItemClickListener {
            override fun onItemClick(holder: UrlHistoryAdapter.UrlHistoryViewHolder) {
                execDeleteUrl(
                    urlHistoryDisplayListAdapter,
                    searchText,
                    holder.bindingAdapterPosition
                )
            }
        }
    }

    private fun makeUrlHistoryList(): List<Map<String, String>> {
        return makeCompleteListSourceNoJsExclude(
            cmdclickDefaultAppDirPath,
        ).reversed()
    }

    private fun makeCompleteListSourceNoJsExclude(
        currentAppDirPath: String?,
    ):List<Map<String, String>> {
        if(
            currentAppDirPath.isNullOrEmpty()
        ) return emptyList()
        val urlList = makeUrlListFromHistory()
        val urlHistoryList = makeBottomScriptUrlList() + urlList
        val urlHistoryListSize = urlHistoryList.size
        return when(
            urlHistoryListSize % 2 == 1
                    && urlHistoryListSize > 3
        ){
            true -> urlHistoryList + urlList.last()
            else -> urlHistoryList
        }
    }

    private fun makeUrlListFromHistory(): List<Map<String, String>>{
        val usedTitle = mutableSetOf<String>()
        val usedUrl = mutableSetOf<String>()
        return ReadText(
            File(
                "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
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
//        val parentDirPath =
//            shellFileObj.parent ?: return
        ExecJsOrSellHandler.handle(
            fragment,
//            parentDirPath,
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
                "${cmdclickDefaultAppDirPath}/${fannelName}",
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
//                cmdclickDefaultAppDirPath,
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
//                cmdclickDefaultAppDirPath,
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
        searchText: AppCompatEditText
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
        urlHistoryDisplayListAdapter: UrlHistoryAdapter,
        searchText: AppCompatEditText,
        position: Int,
    ){
        val filteredUrlHistoryMap =
            makeSearchFilteredUrlHistoryList(
                searchText
            ).getOrNull(position)
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
                    cmdclickDefaultAppDirPath
                )
            } ?: return
        val selectedUrl = selectedUrlHistoryMap.get(urlKey)
            ?.let {
                ScriptPreWordReplacer.settingValReplace(
                    it,
                    cmdclickDefaultAppDirPath
                )
            } ?: return
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
        val urlHistoryDirPath = "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
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
        urlHistoryDisplayListAdapter.urlHistoryMapList.removeAt(position)
        urlHistoryDisplayListAdapter.notifyItemRemoved(position)
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
            val value = urlHistoryMap.get(it)
                ?: return@all false
            value == filteredUrlHistoryMap.get(it)

        }
    }

    private fun exitDialog(
        urlHistoryListView: RecyclerView
    ){
        urlHistoryListView.layoutManager = null
        urlHistoryListView.adapter = null
        urlHistoryListView.recycledViewPool.clear()
        urlHistoryListView.removeAllViews()
        urlHistoryDialog?.dismiss()
        urlHistoryDialog = null
    }
}