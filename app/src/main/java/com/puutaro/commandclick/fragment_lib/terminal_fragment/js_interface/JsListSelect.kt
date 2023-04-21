package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

class JsListSelect(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    private val escapeCharHyphen = "-"
    @JavascriptInterface
    fun updateListFileCon(
        searchListFilePath: String,
        searchText: String
    ) {
        val trimSearchText = searchText.trim()
        if(
            trimSearchText == escapeCharHyphen
            || trimSearchText.isEmpty()
        ) return
        val listFileObj = File(searchListFilePath)
        val searchListDirPath = listFileObj.parent
            ?: return
        val searchListFileName = listFileObj.name
        FileSystems.createDirs(searchListDirPath)
        val listContentsList = ReadText(
            searchListDirPath,
            searchListFileName
        ).textToList()
        val findSearchText = listContentsList.find {
            it.trim() == trimSearchText
        }
        if(
            !findSearchText.isNullOrEmpty()
        ) return
        val lastListContentsSourceList =
            listOf(trimSearchText) + listContentsList
        val lastListContents = lastListContentsSourceList.filter {
            it.isNotEmpty()
                    || it != escapeCharHyphen
        }.joinToString("\n")
        FileSystems.writeFile(
            searchListDirPath,
            searchListFileName,
            lastListContents
        )

    }

    @JavascriptInterface
    fun removeItemInListFileCon(
        searchListFilePath: String,
        searchText: String
    ){
        val trimSearchText = searchText.trim()
        if(
            trimSearchText == escapeCharHyphen
            || searchText.isEmpty()
        ) return
        val listFileObj = File(searchListFilePath)
        val searchListDirPath = listFileObj.parent
            ?: return
        val searchListFileName = listFileObj.name
        FileSystems.createDirs(searchListDirPath)
        val listContentsList = ReadText(
            searchListDirPath,
            searchListFileName
        ).textToList()
        val findSearchText = listContentsList.find {
            it.trim() == trimSearchText
        }
        if(
            findSearchText.isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                "no exist: $searchText",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val updateListContents = ReadText(
            searchListDirPath,
            searchListFileName,
        ).textToList().filter {
            val trimItem = it.trim()
            trimItem.isNotEmpty()
                    && trimItem != escapeCharHyphen
                    && trimItem != searchText
        }.joinToString("\n")
        FileSystems.writeFile(
            searchListDirPath,
            searchListFileName,
            updateListContents
        )
    }
}