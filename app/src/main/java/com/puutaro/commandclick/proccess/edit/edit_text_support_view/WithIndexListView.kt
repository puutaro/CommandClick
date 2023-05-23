package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.*
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.UrlTitleTrimmer
import java.io.File

//class WithIndexListView {
//    fun make(
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        weight: Float,
//    ): Spinner {
//        val context = editParameters.context
//        val currentId = editParameters.currentId
//        val currentSetVariableMap = editParameters.setVariableMap
//        val historyButtonInnerViewContext = context
//        val urlHistoryListView = ListView(
//            historyButtonInnerViewContext
//        )
//
//        val urlHistoryList = mekeUrlHistoryList()
//        val linearLayoutForTotal = LinearLayoutForTotal.make(
//            historyButtonInnerViewContext
//        )
//
//        val linearLayoutForListView = NestLinearLayout.make(
//            historyButtonInnerViewContext,
//            listLinearWeight
//        )
//        linearLayoutForListView.addView(urlHistoryListView)
//
//        val linearLayoutForSearch = NestLinearLayout.make(
//            historyButtonInnerViewContext,
//            searchTextLinearWeight
//        )
//
//
//        val searchText = EditText(historyButtonInnerViewContext)
//        linearLayoutForSearch.addView(searchText)
//        linearLayoutForTotal.addView(linearLayoutForListView)
//        linearLayoutForTotal.addView(linearLayoutForSearch)
//
//        val urlDisplayHistoryList = urlHistoryList.map {
//            val urlTitleSource =
//                it.split(tabReplaceStr)
//                    .firstOrNull()
//                    ?:String()
//            UrlTitleTrimmer.trim(
//                urlTitleSource
//            )
//        }
//
//        val urlHistoryDisplayListAdapter = ArrayAdapter(
//            historyButtonInnerView.context,
//            android.R.layout.simple_list_item_1,
//            urlDisplayHistoryList
//        )
//        urlHistoryListView.adapter = urlHistoryDisplayListAdapter
//        urlHistoryListView.setSelection(
//            urlHistoryDisplayListAdapter.count
//        )
//
//        makeSearchEditText(
//            urlHistoryListView,
//            urlHistoryDisplayListAdapter,
//            searchText
//        )
//
//    }
//
//    private fun makeSearchEditText(
//        urlHistoryListView: ListView,
//        urlHistoryListAdapter: ArrayAdapter<String>,
//        searchText: EditText
//    ) {
//        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT,
//        )
//        linearLayoutParamForSearchText.topMargin = 20
//        linearLayoutParamForSearchText.bottomMargin = 20
//        searchText.layoutParams = linearLayoutParamForSearchText
//        searchText.inputType = InputType.TYPE_CLASS_TEXT
//        searchText.background = null
//        searchText.hint = "search"
//        searchText.setPadding(30, 10, 20, 10)
//        searchText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if(!searchText.hasFocus()) return
//                val filteredUrlHistoryList = mekeUrlHistoryList().map {
//                    val urlTitleSource =
//                        it.split(tabReplaceStr)
//                            .firstOrNull() ?:String()
//                    UrlTitleTrimmer.trim(
//                        urlTitleSource
//                    )
//                }.filter {
//                    Regex(
//                        searchText.text.toString()
//                            .lowercase()
//                            .replace("\n", "")
//                    ).containsMatchIn(
//                        it.lowercase()
//                    )
//                }
//
//                CommandListManager.execListUpdateByEditText(
//                    filteredUrlHistoryList,
//                    urlHistoryListAdapter,
//                    urlHistoryListView
//                )
//                urlHistoryListView.setSelection(
//                    urlHistoryListAdapter.count
//                )
//            }
//        })
//    }
//}
//
//private fun makeListFromListPath(
//    listPath: String,
//): List<String> {
//    val listPathObj = File(listPath)
//    val dirName = listPathObj.parent
//        ?: return emptyList()
//    val listFileName = listPathObj.name
//    return ReadText(
//        dirName,
//        listFileName
//    ).textToList()
//}
//
//
//private fun makeListFromListDir(
//    listDir: String,
//    filterPrefix: String,
//    filterSuffix: String,
//    filterType: String,
//): List<String> {
//    val sortedList = FileSystems.sortedFiles(
//        listDir,
//        "on"
//    )
//    if (
//        filterType != IndexListEditKey.type.name
//    ) return sortedList.filter {
//        it.startsWith(filterPrefix)
//                && judgeBySuffix(it, filterSuffix)
//                && File("$listDir/$it").isFile
//    }
//    return sortedList.filter {
//        it.startsWith(filterPrefix)
//                && judgeBySuffix(it, filterSuffix)
//                && File("$listDir/$it").isDirectory
//    }
//}
//
//private fun judgeBySuffix(
//    targetStr: String,
//    filterSuffix: String,
//): Boolean{
//    if(filterSuffix != FileSelectSpinnerViewProducer.noExtend) {
//        return filterSuffix.split("&").any {
//            targetStr.endsWith(it)
//        }
//    }
//    return !Regex("\\..*$").containsMatchIn(targetStr)
//}
//
//
//private enum class IndexListEditMenu {
//    menu,
//    subMenu
//}
//
//private enum class IndexListEditKey {
//    listFile,
//    listDir,
//    type,
//    prefix,
//    suffix
//}