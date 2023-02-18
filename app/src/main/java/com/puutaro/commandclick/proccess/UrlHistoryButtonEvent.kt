package com.puutaro.commandclick.proccess

import android.R
import android.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.AutoCompleteEditTexter
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.UrlTitleTrimmer


class UrlHistoryButtonEvent(
    fragment: Fragment,
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
        urlHistoryListView.setOnItemClickListener { parent, View, pos, id
            ->
            val selectedUrlSource =
                urlHistoryList.getOrNull(
                    pos
                ) ?: return@setOnItemClickListener
            val selectedUrl = selectedUrlSource.split(tabReplaceStr).lastOrNull()
            if(selectedUrl == null) return@setOnItemClickListener
            alertDialog.dismiss()
            if(
                fragmentTag == context?.getString(
                    com.puutaro.commandclick.R.string.command_index_fragment
                )
            ) {
                val listener = context as? CommandIndexFragment.OnQueryTextChangedListener
                listener?.onQueryTextChanged(
                    selectedUrl,
                )
                return@setOnItemClickListener
            } else if(
                fragmentTag == context?.getString(
                    com.puutaro.commandclick.R.string.cmd_variable_edit_fragment
                )
            ) {
                val listener = context as? EditFragment.OnLaunchUrlByWebViewListener
                listener?.onLaunchUrlByWebView(
                    selectedUrl,
                )
                return@setOnItemClickListener
            }
        }
    }

    private fun mekeUrlHistoryList(): List<String> {
        val takeListNum = 60
        return AutoCompleteEditTexter.makeCompleteListSource(
            currentAppDirPath,
            takeListNum
        ).reversed()
    }
}