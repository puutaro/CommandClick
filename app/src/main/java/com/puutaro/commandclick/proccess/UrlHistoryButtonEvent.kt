package com.puutaro.commandclick.proccess

import android.R
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class UrlHistoryButtonEvent(
    private val fragment: androidx.fragment.app.Fragment,
    readSharePreffernceMap: Map<String, String>,
) {
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val fragmentTag = fragment.tag
    private val context = fragment.context
    private val tabReplaceStr = "\t"
    private val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private val takeUrlListNum = 400


    fun invoke(
        historyButtonInnerView: View,
    ){
        terminalViewModel.onDialog = true
        val historyButtonInnerViewContext = historyButtonInnerView.context
        val urlHistoryListView = ListView(
            historyButtonInnerViewContext
        )

        val urlHistoryList = mekeUrlHistoryList()
        val linearLayoutForTotal = LinearLayoutForTotal.make(
            historyButtonInnerViewContext
        )

        val linearLayoutForListView = NestLinearLayout.make(
            historyButtonInnerViewContext,
            listLinearWeight
        )
        linearLayoutForListView.addView(urlHistoryListView)

        val linearLayoutForSearch = NestLinearLayout.make(
            historyButtonInnerViewContext,
            searchTextLinearWeight
        )


        val searchText = EditText(historyButtonInnerViewContext)
        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)

        val urlDisplayHistoryList = urlHistoryList.map {
            val urlTitleSource =
                it.split(tabReplaceStr)
                    .firstOrNull()
                    ?:String()
            UrlTitleTrimmer.trim(
                urlTitleSource
            )
        }

        val urlHistoryDisplayListAdapter = ArrayAdapter(
            historyButtonInnerView.context,
            R.layout.simple_list_item_1,
            urlDisplayHistoryList
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

        val alertDialogBuilder = AlertDialog.Builder(
            historyButtonInnerViewContext
        )
            .setTitle("Select from url history")
            .setView(linearLayoutForTotal)
        val alertDialog =
            alertDialogBuilder
                .create()
        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
        alertDialog.window?.setGravity(Gravity.BOTTOM);
        alertDialog.show()



        setUrlHistoryListViewOnItemClickListener(
            urlHistoryListView,
            urlHistoryList,
            alertDialog,
            searchText
        )

        setUrlHistoryListViewOnItemLongClickListener (
            urlHistoryListView,
            urlHistoryList,
        )
    }

    private fun makeSearchEditText(
        urlHistoryListView: ListView,
        urlHistoryListAdapter: ArrayAdapter<String>,
        searchText: EditText
    ) {
        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParamForSearchText.topMargin = 20
        linearLayoutParamForSearchText.bottomMargin = 20
        searchText.layoutParams = linearLayoutParamForSearchText
        searchText.inputType = InputType.TYPE_CLASS_TEXT
        searchText.background = null
        searchText.hint = "search"
        searchText.setPadding(30, 10, 20, 10)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = mekeUrlHistoryList().map {
                    val urlTitleSource =
                        it.split(tabReplaceStr)
                            .firstOrNull() ?:String()
                    UrlTitleTrimmer.trim(
                        urlTitleSource
                    )
                }.filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }

                CommandListManager.execListUpdateByEditText(
                    filteredUrlHistoryList,
                    urlHistoryListAdapter,
                    urlHistoryListView
                )
                urlHistoryListView.setSelection(
                    urlHistoryListAdapter.count
                )
            }
        })
    }


    private fun setUrlHistoryListViewOnItemClickListener (
        urlHistoryListView: ListView,
        urlHistoryList: List<String>,
        alertDialog: AlertDialog,
        searchText: EditText,
    ){
        urlHistoryListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val filteredUrlHistoryTitle = mekeUrlHistoryList().map {
                    val urlTitleSource =
                        it.split(tabReplaceStr)
                            .firstOrNull() ?:String()
                    UrlTitleTrimmer.trim(
                        urlTitleSource
                    )
                }.filter {
                        Regex(
                            searchText.text
                                .toString()
                                .lowercase()
                        ).containsMatchIn(
                            it.lowercase()
                        )
                    }.getOrNull(pos)
                        ?: return@setOnItemClickListener
            val selectedUrlSource =
                urlHistoryList.filter {
                    it.startsWith(filteredUrlHistoryTitle)
                }.firstOrNull()
                    ?: return@setOnItemClickListener
            val selectedUrl = selectedUrlSource.split(
                tabReplaceStr
            ).lastOrNull()
                ?.let {
                    ScriptPreWordReplacer.settingValreplace(
                        it,
                        currentAppDirPath
                    )
                } ?: return@setOnItemClickListener
            alertDialog.dismiss()
            CoroutineScope(Dispatchers.Main).launch{
                ScrollPosition.save(fragment.activity)
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
                execShellFile(selectedUrl)
                return@setOnItemClickListener
            }

            if (
                fragmentTag == context?.getString(
                    com.puutaro.commandclick.R.string.command_index_fragment
                )
            ) {
                val listener = context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
                listener?.onLaunchUrlByWebView(
                    selectedUrl,
                )
                return@setOnItemClickListener
            } else if (
                fragmentTag?.startsWith(
                    FragmentTagManager.Prefix.cmdEditPrefix.str
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

    private fun mekeUrlHistoryList(): List<String> {
        return makeCompleteListSourceNoJsExclude(
            currentAppDirPath,
        ).reversed()
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
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
            UsePath.cmdclickUrlHistoryFileName
        ).textToList()
            .distinct()
            .take(takeUrlListNum)
            .filter {
                makeUrlHistoryList(
                    it,
                    usedTitle,
                    usedUrl,
                )
            }
    }

    private fun execShellFile(
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
        )
    }

    private fun setUrlHistoryListViewOnItemLongClickListener (
        urlHistoryListView: ListView,
        urlHistoryList: List<String>,
    ){
        urlHistoryListView.setOnItemLongClickListener {
                parent, listSelectedView, position, id ->
            val popup = PopupMenu(context, listSelectedView)
            val selectedLine = urlHistoryList[position]
                .split("\t")
                .lastOrNull()
            val inflater = popup.menuInflater
            inflater.inflate(
                com.puutaro.commandclick.R.menu.history_admin_menu,
                popup.menu
            )
            popup.menu.add(
                UrlHistoryMenuEnums.COPY_URL.groupId,
                UrlHistoryMenuEnums.COPY_URL.itemId,
                UrlHistoryMenuEnums.COPY_URL.order,
                UrlHistoryMenuEnums.COPY_URL.itemName,

                )
            popup.setOnMenuItemClickListener {
                    menuItem ->
                execCopyUrl(
                    listSelectedView,
                    selectedLine,
                )
                true
            }
            popup.show()
            true
        }
    }

    private fun makeBottomScriptUrlList(
    ): List<String> {
        return when(
            fragment
        ){
            is com.puutaro.commandclick.fragment.CommandIndexFragment -> {
                fragment.bottomScriptUrlList.map {
                        url ->
                    val title = url.split("/")
                        .lastOrNull()
                        ?: String()
                    "${title}\t${url}"
                }
            }
            is EditFragment -> {
                fragment.bottomScriptUrlList.map {
                    url ->
                    val title = url.split("/")
                        .lastOrNull()
                        ?: String()
                    "${title}\t${url}"
                }
            }
            else -> emptyList()
        }
    }

    private fun makeUrlHistoryList(
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
}

internal val mainMenuGroupId = 70000

internal enum class UrlHistoryMenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    COPY_URL(mainMenuGroupId, 70100, 1, "copy_url"),
}


private fun execCopyUrl(
    listSelectedView: View,
    selectedLine: String?
){
    val clipboard = listSelectedView.context?.getSystemService(
        Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText(
        "url",
        selectedLine
    )
    clipboard.setPrimaryClip(clip)
}
