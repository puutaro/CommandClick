package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.util.Log
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ToastErrMessage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsText
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.*
import java.io.File
import java.lang.ref.WeakReference

class JsCsv(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    val jsText = JsText(terminalFragmentRef)
    val tableHeaderClassName = "tableHeader"
    val rowFirstClassName = "rowFirst"
    val existSign = "exist"

    @JavascriptInterface
    fun takeRowSize(
        tag: String
    ): Int {
        val terminalFragment = terminalFragmentRef.get()
            ?: return 0
        val rowSize = terminalFragment.rowsMap[tag]?.size ?: 0
        return rowSize
    }

    @JavascriptInterface
    fun takeColSize(
        tag: String
    ): Int {
        val terminalFragment = terminalFragmentRef.get()
            ?: return 0
        val tagName = terminalFragment.rowsMap[tag]?.firstOrNull()?.size ?: 0
        return tagName
    }

    @JavascriptInterface
    fun isRead(
        tag: String
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val rowsString = terminalFragment.rowsMap[tag]
            ?.joinToString("")
            ?: String()
        val headerSize = terminalFragment.headerMap[tag]?.joinToString("\t")
        if(
            rowsString.isNotEmpty()
            && !headerSize.isNullOrEmpty()
        ) return existSign
        return String()
    }

    @JavascriptInterface
    fun read_S(
        tag: String,
        filePath: String,
        withNoHeader: String,
        limitRowNumSource: Int
    ) {
        var readCompSignal = false
        var errMessage = String()
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    execRead(
                        terminalFragment,
                        tag,
                        filePath,
                        withNoHeader,
                        limitRowNumSource
                    )
                } catch (e: Exception){
                    errMessage = e.toString()
                    Log.e("csv", errMessage)
                }
            }
            withContext(Dispatchers.IO){
                readCompSignal = true
            }
        }
        runBlocking {
            for (i in 1..60){
                ToastErrMessage.launch(
                    terminalFragment,
                    errMessage
                )
                if(readCompSignal) break
                val readingMark = "reading" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(readingMark)
                }
                delay(2000)
            }
        }
    }


    private fun execRead(
        terminalFragment: TerminalFragment,
        tag: String,
        filePath: String,
        withNoHeader: String,
        limitRowNumSource: Int
    ) {
        terminalFragment.rowsMap[tag] = emptyList()
        terminalFragment.headerMap[tag] = emptyList()
        val file = File(filePath)
        val fileType = judgeFileType(filePath)
        val rowsSource = when(fileType){
            FileType.CSV -> csvReader().readAll(file)
            FileType.TSV -> {
                val tsvReader = csvReader {
                    quoteChar = '"'
                    delimiter = '\t'
                    escapeChar = '\\'
                }
                tsvReader.readAll(file)
            }
            else -> {
                terminalFragment.rowsMap[tag] = emptyList()
                terminalFragment.headerMap[tag] = emptyList()
                ToastUtils.showLong("Extend must be csv or tsv")
                return
            }
        }
        val headerList = rowsSource[0]
        terminalFragment.headerMap[tag] = headerList
        if(
            headerList.isEmpty()
        ) {
            terminalFragment.rowsMap[tag] = emptyList()
            return
        }
        val limitRowNum = if(
            limitRowNumSource == 0
            || !judgeInt(limitRowNumSource.toString())
        ) rowsSource.size
        else limitRowNumSource
        if(
            withNoHeader.isNotEmpty()
        ){
            terminalFragment.rowsMap[tag] = rowsSource.take(limitRowNum)
            return
        }
        val rowsSourceSize = rowsSource.size
        terminalFragment.rowsMap[tag] = if(
            rowsSourceSize > 1
        ) rowsSource.slice(
            1 until rowsSource.size
        ).take(limitRowNum)
        else emptyList()
    }

    @JavascriptInterface
    fun readM(
        tag: String,
        csvString: String,
        csvOrTsv: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        try {
            if(
                csvOrTsv.isEmpty()
                || csvOrTsv == FileType.CSV.name
            ){
                terminalFragment.rowsMap[tag] = csvReader().readAll(csvString)
                return
            }
            val tsvReader = csvReader {
                charset = "ISO_8859_1"
                quoteChar = '"'
                delimiter = '\t'
                escapeChar = '\\'
            }
            terminalFragment.rowsMap[tag] = tsvReader.readAll(csvString)
        } catch(e: Exception) {
            terminalFragment.rowsMap[tag] = emptyList()
            terminalFragment.headerMap[tag] = emptyList()
            ToastUtils.showLong(e.toString())
        }
    }

    @JavascriptInterface
    fun toHeader(
        tag: String,
        colNum: Int,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val headerList =  terminalFragment.headerMap[tag]
            ?: return String()
        val colSize = headerList.size
        if(
            colNum >= colSize
        ) return String()
        val colName = headerList[colNum]
        return colName
    }

    @JavascriptInterface
    fun toHeaderRow(
        tag: String,
        startColNumSource: Int,
        endColNumSource: Int,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val startCol = makeStartNum(
            terminalFragment,
            tag,
            startColNumSource,
        )
        val endColNum = makeEndNum(
            tag,
            endColNumSource,
        )
        if(
            startCol > endColNum
        ) return String()

        val headerList =  terminalFragment.headerMap[tag]
            ?: return String()
        val colSize = headerList.size
        val lastColEndNum = if(
            endColNum >= colSize
        ) colSize - 1
        else endColNum
        val headerRowWithTabSeparated = headerList
            .slice(startCol..lastColEndNum)
            .joinToString("\t")
        return headerRowWithTabSeparated
    }

    @JavascriptInterface
    fun toRow(
        tag: String,
        rowNum: Int,
        startColNumSource: Int,
        endColNumSource: Int,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val startCol = makeStartNum(
            terminalFragment,
            tag,
            startColNumSource,
        )
        val endColNum = makeEndNum(
            tag,
            endColNumSource,
        )
        if(
            startCol > endColNum
        ) return String()
        val rows = terminalFragment.rowsMap[tag]
            ?: return String()
        val cols = rows[rowNum]
        val colsSize = cols.size
        val tabSeparatedRowByConvertedCols = (0 until colsSize).filter {
            startCol <= it
                    && it <= endColNum
        }.map {
            cols[it]
        }.joinToString("\t")
        return tabSeparatedRowByConvertedCols
    }

    @JavascriptInterface
    fun toCol(
        tag: String,
        colNum: Int,
        startRowNumSource: Int,
        endRowNumSource: Int,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val startRow = makeStartNum(
            terminalFragment,
            tag,
            startRowNumSource,
        )
        val endRowNum = makeEndNum(
            tag,
            endRowNumSource,
        )
        if(startRow > endRowNum) return String()
        try {
            val rowConByConvertedCols = (startRow..endRowNum).map {
                val row = terminalFragment.rowsMap[tag]
                    ?.get(it)
                    ?: return@map String()
                val colSize = row.size
                if (
                    colNum >= colSize
                ) return@map String()
                row.getOrNull(colNum)
                    ?: String()
            }.joinToString("\n")
            return rowConByConvertedCols
        } catch (e: Exception){
            ToastUtils.showLong(e.toString())
            return String()
        }
    }

    @JavascriptInterface
    fun sliceHeader(
        tag: String,
        startColNumSource: Int,
        endColNumSource: Int,
        headerRow: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val startCol = makeStartNum(
            terminalFragment,
            tag,
            startColNumSource,
        )
        val endColNum = makeEndNum(
            tag,
            endColNumSource,
        )
        if(
            startCol > endColNum
        ) return String()
        val headers = headerRow.split("\t")
        val colsSize = headers.size
        val slicedHeader = (0 until colsSize).filter {
            startCol <= it
                    && it <= endColNum
        }.map {
            headers[it]
        }.joinToString("\t")
        return slicedHeader
    }

    @JavascriptInterface
    fun toHtml(
        tsvString: String,
        onTh: String,
    ): String {
        val sourceRows = tsvString.split("\n")
        val htmlCon = sourceRows.map {
            line ->
            val lineList = line.split("\t")
            val lineListSize = lineList.size
            val rows = (0 until lineListSize).map {
                val cell = lineList[it]
                execToHtml(
                    onTh,
                    cell,
                    it,
                )
            }.joinToString("")
            listOf("<tr>", rows,"</tr>").joinToString("")
        }.joinToString("\n")
        return htmlCon
    }

    @JavascriptInterface
    fun outPutTsvForDRow(
        tag: String
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val tsvCon = terminalFragment.rowsMap[tag]?.map {
            it.joinToString("\t")
        }?.joinToString("\n")
            ?: String()
        return tsvCon
    }

    @JavascriptInterface
    fun outPutTsvForDCol(
        tag: String
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val rows = terminalFragment.rowsMap[tag]
            ?: return String()
        val tsvCon = jsText.transpose(rows).map {
            it.joinToString("\t")
        }.joinToString("\n")
        return tsvCon
    }

    @JavascriptInterface
    fun selectColumn_S(
        srcTag: String,
        destTag: String,
        comaSepaColumns: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        var selectCompSignal = false
        var errMessage = String()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    execSelectColumn(
                        terminalFragment,
                        srcTag,
                        destTag,
                        comaSepaColumns
                    )
                } catch(e: Exception){
                    terminalFragment.rowsMap[destTag] = emptyList()
                    terminalFragment.headerMap[destTag] = emptyList()
                    errMessage = e.toString()
                    Log.e("csv", errMessage)
                }
            }
            withContext(Dispatchers.IO){
                selectCompSignal = true
            }
        }
        runBlocking {
            for (i in 1..60){
                ToastErrMessage.launch(
                    terminalFragment,
                    errMessage
                )
                if(selectCompSignal) break
                val selectingMark = "column selecting" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(selectingMark)
                }
                delay(2000)
            }
        }
    }

    private fun execSelectColumn(
        terminalFragment: TerminalFragment,
        srcTag: String,
        destTag: String,
        comaSepaColumns: String
    ){

        val rows = terminalFragment.rowsMap[srcTag]
        if(
            rows.isNullOrEmpty()
        ){
            terminalFragment.rowsMap[destTag] = emptyList()
            return
        }
        val srcHeaderList = terminalFragment.headerMap[srcTag]
        if(srcHeaderList.isNullOrEmpty()){
            terminalFragment.rowsMap[destTag] = emptyList()
            return
        }
        if(
            comaSepaColumns.trim().trim(',').isEmpty()
        ){
            terminalFragment.rowsMap[destTag] = rows
            terminalFragment.headerMap[destTag] = srcHeaderList
            return
        }
        val inputHeaderList = comaSepaColumns.split(',')
        terminalFragment.headerMap[destTag] = inputHeaderList.filter {
            srcHeaderList.contains(it) == true
        }
        val selectColumnIndexList = inputHeaderList.map {
            srcHeaderList.indexOf(it.trim())
        }.filter { it >= 0 }
        terminalFragment.rowsMap[destTag] = rows.map {
            line ->
            (line.indices).filter {
                selectColumnIndexList.contains(it)
            }.map {
                line[it]
            }
        }
    }
    @JavascriptInterface
    fun filter_S(
        srcTag: String,
        destTag: String,
        tabSepaFormura: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        var filterCompSignal = false
        var errMessage = String()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    execFilter(
                        terminalFragment,
                        srcTag,
                        destTag,
                        tabSepaFormura
                    )
                } catch(e: Exception){
                    errMessage = e.toString()
                    Log.e("csv", errMessage)
                }
            }
            withContext(Dispatchers.IO){
                filterCompSignal = true
            }
        }
        runBlocking {
            for (i in 1..60){
                ToastErrMessage.launch(
                    terminalFragment,
                    errMessage
                )
                if(filterCompSignal) break
                val filteringMark = "filtering" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(filteringMark)
                }
                delay(2000)
            }
        }
    }


    private fun execFilter(
        terminalFragment: TerminalFragment,
        srcTag: String,
        destTag: String,
        tabSepaFormura: String
    ){
        terminalFragment.rowsMap[destTag] = emptyList()
        terminalFragment.headerMap[destTag] = emptyList()
        val rows = terminalFragment.rowsMap[srcTag]
        if(
            rows.isNullOrEmpty()
        ){
            terminalFragment.rowsMap[destTag] = emptyList()
            return
        }
        terminalFragment.headerMap[srcTag]?.let {
            terminalFragment.headerMap[destTag] = it
        }
        if(
            tabSepaFormura.trim().isEmpty()
        ){
            terminalFragment.rowsMap[destTag] = rows
            return
        }

        val filterNestMap = makeFilterMap(
            tabSepaFormura
        )
        val filteredRows = execRowsBuFilter(
            terminalFragment,
            srcTag,
            filterNestMap,
            rows
        )
        terminalFragment.rowsMap.put(destTag, filteredRows)
    }

    private fun makeFilterMap(
        tabSepaFormura: String,
    ): Map<String, Map<String, String>> {
        return tabSepaFormura.split("\t").map {
            val filterLineList = QuoteTool.splitBySurroundedIgnore(
                it.trim(),
                ','
            )
//            it.trim().split(",")
            if(filterLineList.size != 3) return@map String() to mapOf()
            val schema = filterLineList.firstOrNull()?.trim()
                ?: String()
            val symbol = filterLineList.getOrNull(1)
                ?.trim()
                ?: String()
            val value = filterLineList.getOrNull(2)
                ?.trim()
                ?: String()
            schema to mapOf(
                FilterMapKey.Simbol.name to symbol,
                FilterMapKey.Gain.name to value,
            )
        }.toMap().filterValues { it.isNotEmpty() }
    }

    private fun execRowsBuFilter(
        terminalFragment: TerminalFragment,
        srcTag: String,
        filterNestMap: Map<String, Map<String, String>>,
        rows: List<List<String>>
    ): List<List<String>>{
        val rowSize = rows.size
        return (0 until rowSize).filter {
            val rowList = rows[it]
            val rowLineSize = rowList.size
            val headerList = terminalFragment.headerMap[srcTag]
                ?: emptyList()
            (0 until rowLineSize).all {
                val schema = headerList.get(it)
                val filterMap = filterNestMap.get(schema)
                    ?: return@all true
                val filterSimbol = filterMap.get(FilterMapKey.Simbol.name)
                    ?: return@all true
                val filterGain = filterMap.get(FilterMapKey.Gain.name)
                    ?: return@all true
                val cellValue = rowList.get(it)
                val enableFloat = judgeFloat(cellValue)
                        && judgeFloat(filterGain)
                when(filterSimbol){
                    FilterType.In.mark -> {
                        cellValue.contains(filterGain)
                    }
                    FilterType.EqualLarger.mark -> {
                        if(!enableFloat) return@all true
                        cellValue.toFloat() >= filterGain.toFloat()
                    }
                    FilterType.Larger.mark -> {
                        if(!enableFloat) return@all true
                        cellValue.toFloat() > filterGain.toFloat()
                    }
                    FilterType.EqualLeast.mark -> {
                        if(!enableFloat) return@all true
                        cellValue.toFloat() <= filterGain.toFloat()
                    }
                    FilterType.Least.mark -> {
                        if(!enableFloat) return@all true
                        cellValue.toFloat() < filterGain.toFloat()
                    }
                    FilterType.equal.mark -> {
                        return@all cellValue == filterGain
                    }
                    else -> return@all true
                }
            }
        }.map {
            rows[it]
        }
    }

    private fun makeStartNum(
        terminalFragment: TerminalFragment,
        tag: String,
        startRowNumSource: Int,
    ): Int {
        if(
            startRowNumSource <= 0
        ) return 0
        val rowSize = terminalFragment.rowsMap[tag]?.size
            ?: return 0
        if(
            startRowNumSource >= rowSize
        ) return rowSize - 1
        return startRowNumSource - 1
    }


    private fun makeEndNum(
        tag: String,
        endRowNumSource: Int,
    ): Int {
        val terminalFragment = terminalFragmentRef.get()
            ?: return 0

        val rowSize = terminalFragment.rowsMap[tag]?.size
            ?: return 0
        if(
            endRowNumSource <= 0
        ) return rowSize - 1
        if(
            endRowNumSource >= rowSize
        ) return rowSize - 1
        return endRowNumSource - 1
    }

    private fun execToHtml(
        onTh: String,
        cell: String,
        index: Int,
    ): String {
        if(
            onTh.isNotEmpty()
        ) return "<th class=\"${tableHeaderClassName}\">${cell}</th>"
        if(
            index == 0
        ) return "<td class=\"${rowFirstClassName}\">${cell}</td>"
        return "<td>${cell}</td>"
    }
}

private enum class FilterType(
    val mark: String,
){
    In("in"),
    EqualLarger(">="),
    EqualLeast("<="),
    Larger(">"),
    Least("<"),
    equal("="),
}


private enum class FilterMapKey{
    Simbol,
    Gain,
}

private fun judgeInt(
    intString: String
): Boolean {
  return try{
      intString.toInt()
      true
  } catch (e: Exception){
      false
  }
}

private fun judgeFileType(
    filePath: String
): FileType {
    if(
        filePath.endsWith(FileType.CSV.str)
    ) return FileType.CSV
    if(
        filePath.endsWith(FileType.TSV.str)
    ) return FileType.TSV
    return FileType.OTHER
}

private fun judgeFloat(
    floatString: String
): Boolean {
    return try{
        floatString.toFloat()
        true
    } catch (e: Exception){
        false
    }
}

private enum class FileType(
    val str: String
) {
    CSV("csv"),
    TSV("tsv"),
    OTHER("other"),
}