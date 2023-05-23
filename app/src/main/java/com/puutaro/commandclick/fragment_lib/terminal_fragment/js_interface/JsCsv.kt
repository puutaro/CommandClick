package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.util.Log
import android.view.Gravity
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsText
import kotlinx.coroutines.*
import java.io.File

class JsCsv(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val jsText = JsText(terminalFragment)
    val tableHeaderClassName = "tableHeader"
    val rowFirstClassName = "rowFirst"
    val existSign = "exist"

    @JavascriptInterface
    fun takeRowSize(
        tag: String
    ): Int {
        return terminalFragment.rowsMap[tag]?.size ?: 0
    }

    @JavascriptInterface
    fun takeColSize(
        tag: String
    ): Int {
        return terminalFragment.rowsMap[tag]?.firstOrNull()?.size ?: 0
    }

    @JavascriptInterface
    fun isRead(
        tag: String
    ): String {
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
    fun read(
        tag: String,
        filePath: String,
        withNoHeader: String,
        limitRowNumSource: Int
    ) {
        var readCompSignal = false
        var errMessage = String()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    execRead(
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
                toastErrMessage(errMessage)
                if(readCompSignal) break
                val readingMark = "reading" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        readingMark,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                delay(2000)
            }
        }
    }


    private fun execRead(
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
                Toast.makeText(
                    context,
                    "Extend must be csv or tsv",
                    Toast.LENGTH_LONG
                ).show()
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
            Toast.makeText(
                context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @JavascriptInterface
    fun toHeader(
        tag: String,
        colNum: Int,
    ): String {
        val headerList =  terminalFragment.headerMap[tag]
            ?: return String()
        val colSize = headerList.size
        if(
            colNum >= colSize
        ) return String()
        return headerList[colNum]
    }

    @JavascriptInterface
    fun toHeaderRow(
        tag: String,
        startColNumSource: Int,
        endColNumSource: Int,
    ): String {
        val startCol = makeStartNum(
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
        return headerList
            .slice(startCol..lastColEndNum)
            .joinToString("\t")
    }

    @JavascriptInterface
    fun toRow(
        tag: String,
        rowNum: Int,
        startColNumSource: Int,
        endColNumSource: Int,
    ): String {
        val startCol = makeStartNum(
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
        return (0 until colsSize).filter {
            startCol <= it
                    && it <= endColNum
        }.map {
            cols[it]
        }.joinToString("\t")
    }

    @JavascriptInterface
    fun toCol(
        tag: String,
        colNum: Int,
        startRowNumSource: Int,
        endRowNumSource: Int,
    ): String {
        val startRow = makeStartNum(
            tag,
            startRowNumSource,
        )
        val endRowNum = makeEndNum(
            tag,
            endRowNumSource,
        )
        if(startRow > endRowNum) return String()
        try {
            return (startRow..endRowNum).map {
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
        } catch (e: Exception){
            Toast.makeText(
                context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
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
        val startCol = makeStartNum(
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
        return (0 until colsSize).filter {
            startCol <= it
                    && it <= endColNum
        }.map {
            headers[it]
        }.joinToString("\t")
    }

    @JavascriptInterface
    fun toHtml(
        tsvString: String,
        onTh: String,
    ): String {
        val sourceRows = tsvString.split("\n")
        return sourceRows.map {
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
    }

    @JavascriptInterface
    fun outPutTsvForDRow(
        tag: String
    ): String {
        return terminalFragment.rowsMap[tag]?.map {
            it.joinToString("\t")
        }?.joinToString("\n")
            ?: String()
    }

    @JavascriptInterface
    fun outPutTsvForDCol(
        tag: String
    ): String {
        val rows = terminalFragment.rowsMap[tag]
            ?: return String()
        return jsText.transpose(rows).map {
            it.joinToString("\t")
        }.joinToString("\n")
    }

    @JavascriptInterface
    fun selectColumn(
        srcTag: String,
        destTag: String,
        comaSepaColumns: String
    ){
        var selectCompSignal = false
        var errMessage = String()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    execSelectColumn(
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
                toastErrMessage(errMessage)
                if(selectCompSignal) break
                val selectingMark = "column selecting" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        selectingMark,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                delay(2000)
            }
        }
    }

    private fun execSelectColumn(
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
    fun filter(
        srcTag: String,
        destTag: String,
        tabSepaFormura: String
    ){
        var filterCompSignal = false
        var errMessage = String()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    execFilter(
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
                toastErrMessage(errMessage)
                if(filterCompSignal) break
                val filteringMark = "filtering" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        filteringMark,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                delay(2000)
            }
        }
    }


    private fun execFilter(
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
            val filterLineList = it.trim().split(",")
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

    private fun toastErrMessage(
        errMessage: String
    ){
        if(
            errMessage.isEmpty()
        ) return
        val ts = Toast.makeText(
            context,
            errMessage,
            Toast.LENGTH_LONG
        )
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
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