package com.puutaro.commandclick.proccess

import android.R
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.makeUrlHistoryList
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.*
import java.io.File


class UrlHistoryButtonEvent(
    private val fragment: Fragment,
    readSharePreffernceMap: Map<String, String>,
) {
    val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    val fragmentTag = fragment.tag
    private val context = fragment.context
    private val tabReplaceStr = "\t"


    fun invoke(
        historyButtonInnerView: View,
    ){
        val historyButtonInnerViewContext = historyButtonInnerView.context
        val urlHistoryListView = ListView(
            historyButtonInnerViewContext
        )
        val alertDialogBuilder = AlertDialog.Builder(
            historyButtonInnerViewContext
        )
            .setTitle("Select from url history")
            .setView(urlHistoryListView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog
            .getWindow()?.setGravity(Gravity.BOTTOM);
        alertDialog.show()

        val urlHistoryList = mekeUrlHistoryList()

        setUrlHistoryListView(
            urlHistoryListView,
            historyButtonInnerView,
            urlHistoryList,
        )
        setUrlHistoryListViewOnItemClickListener(
            urlHistoryListView,
            urlHistoryList,
            alertDialog
        )

        setUrlHistoryListViewOnItemLongClickListener (
            urlHistoryListView,
            urlHistoryList,
        )
    }


    private fun setUrlHistoryListView(
        urlHistoryListView: ListView,
        historyButtonInnerView: View,
        urlHistoryList: List<String>

    ){

        val urlDisplayHistoryList = urlHistoryList.map {
            val urlTitleSource =
                it.split(tabReplaceStr)
                    .firstOrNull() ?:String()
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
        urlHistoryListView.setSelection(urlHistoryDisplayListAdapter.count)
    }


    private fun setUrlHistoryListViewOnItemClickListener (
        urlHistoryListView: ListView,
        urlHistoryList: List<String>,
        alertDialog: AlertDialog
    ){
        urlHistoryListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val selectedUrlSource =
                urlHistoryList.getOrNull(
                    pos
                ) ?: return@setOnItemClickListener
            val selectedUrl = selectedUrlSource.split(tabReplaceStr).lastOrNull()
                ?: return@setOnItemClickListener
            alertDialog.dismiss()

            if(
                selectedUrl.endsWith(
                    CommandClickShellScript.SHELL_FILE_SUFFIX
                )
                || selectedUrl.endsWith(
                    CommandClickShellScript.JS_FILE_SUFFIX
                )
                || selectedUrl.endsWith(
                    CommandClickShellScript.JSX_FILE_SUFFIX,
                )
            ) {
                execShellFile(selectedUrl)
                return@setOnItemClickListener
            }

            if(
                fragmentTag == context?.getString(
                    com.puutaro.commandclick.R.string.command_index_fragment
                )
            ) {
                val listener = context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
                listener?.onLaunchUrlByWebView(
                    selectedUrl,
                )
                return@setOnItemClickListener
            } else if(
                fragmentTag == context?.getString(
                    com.puutaro.commandclick.R.string.cmd_variable_edit_fragment
                )
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
        val takeListNum = 60
        return ReadText(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
            UsePath.cmdclickUrlHistoryFileName
        ).textToList()
            .distinct()
            .take(takeListNum)
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
