package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsText
import kotlinx.coroutines.*
import java.io.File

class JsCsv(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    var rowsMap: MutableMap<String, List<List<String>>> = mutableMapOf()
    var headerMap: MutableMap<String, List<String>> = mutableMapOf()
    val jsText = JsText(terminalFragment)
    val tableHeaderClassName = "tableHeader"
    val rowFirstClassName = "rowFirst"

    @JavascriptInterface
    fun takeRowSize(
        tag: String
    ): Int {
        return rowsMap[tag]?.size ?: 0
    }

    @JavascriptInterface
    fun takeColSize(
        tag: String
    ): Int {
        return rowsMap[tag]?.firstOrNull()?.size ?: 0
    }

    @JavascriptInterface
    fun isRead(
        tag: String
    ): String {
        return rowsMap
            .get(tag)
            ?.joinToString("")
            ?: String()
    }

    @JavascriptInterface
    fun read(
        tag: String,
        csvPath: String,
        isHeader: String,
        csvOrTsv: String,
        limitRowNumSource: Int
    ) {
        var readCompSignal = false
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                execRead(
                    tag,
                    csvPath,
                    isHeader,
                    csvOrTsv,
                    limitRowNumSource
                )
            }
            withContext(Dispatchers.IO){
                readCompSignal = true
            }
        }
        runBlocking {
            for (i in 1..60){
                if(readCompSignal) break
                val readingMark = "csv reading" +
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
        csvPath: String,
        isHeader: String,
        csvOrTsv: String,
        limitRowNumSource: Int
    ) {
        val file = File(csvPath)
        val rowsSource = if(
            csvOrTsv.isEmpty()
            || csvOrTsv == FileType.CSV.name
        ) {
            csvReader().readAll(file)
        } else {
            val tsvReader = csvReader {
                charset = "ISO_8859_1"
                quoteChar = '"'
                delimiter = '\t'
                escapeChar = '\\'
            }
            tsvReader.readAll(file)
        }
        headerMap[tag] = rowsSource[0]
        val limitRowNum = if(
            limitRowNumSource == 0
            || !judgeInt(limitRowNumSource.toString())
        ) rowsSource.size
        else limitRowNumSource
        if(
            isHeader.isEmpty()
        ){
            val rowsSourceSize = rowsSource.size
            rowsMap[tag] = if(
                rowsSourceSize > 1
            ) rowsSource.slice(
                1 until rowsSource.size
            ).take(limitRowNum)
            else emptyList()
            return
        }
        else rowsMap[tag] = rowsSource.take(limitRowNum)
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
                rowsMap[tag] = csvReader().readAll(csvString)
                return
            }
            val tsvReader = csvReader {
                charset = "ISO_8859_1"
                quoteChar = '"'
                delimiter = '\t'
                escapeChar = '\\'
            }
            rowsMap[tag] = tsvReader.readAll(csvString)
        } catch(e: Exception) {
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
        val headerList =  headerMap[tag]
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
        val headerList =  headerMap[tag]
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
        val rows = rowsMap[tag]
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
                val row = rowsMap[tag]
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
        return rowsMap[tag]?.map {
            it.joinToString("\t")
        }?.joinToString("\n")
            ?: String()
    }

    @JavascriptInterface
    fun outPutTsvForDCol(
        tag: String
    ): String {
        val rows = rowsMap[tag]
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
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                execSelectColumn(
                    srcTag,
                    destTag,
                    comaSepaColumns
                )
            }
            withContext(Dispatchers.IO){
                selectCompSignal = true
            }
        }
        runBlocking {
            for (i in 1..60){
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

        val rows = rowsMap[srcTag]
        if(
            rows.isNullOrEmpty()
        ){
            rowsMap[destTag] = emptyList()
            return
        }
        val srcHeaderList = headerMap[srcTag]
        if(srcHeaderList.isNullOrEmpty()){
            rowsMap[destTag] = emptyList()
            return
        }
        if(
            comaSepaColumns.trim().trim(',').isEmpty()
        ){
            rowsMap[destTag] = rows
            headerMap[destTag] = srcHeaderList
            return
        }
        val inputHeaderList = comaSepaColumns.split(',')
        headerMap[destTag] = inputHeaderList.filter {
            srcHeaderList.contains(it) == true
        }
        val selectColumnIndexList = inputHeaderList.map {
            srcHeaderList.indexOf(it.trim())
        }.filter { it >= 0 }
        rowsMap[destTag] = rows.map {
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
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                execFilter(
                    srcTag,
                    destTag,
                    tabSepaFormura
                )
            }
            withContext(Dispatchers.IO){
                filterCompSignal = true
            }
        }
        runBlocking {
            for (i in 1..60){
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
        val rows = rowsMap[srcTag]
        if(
            rows.isNullOrEmpty()
        ){
            rowsMap[destTag] = emptyList()
            return
        }
        headerMap[srcTag]?.let {
            headerMap[destTag] = it
        }
        if(
            tabSepaFormura.trim().isEmpty()
        ){
            rowsMap[destTag] = rows
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
        rowsMap.put(destTag, filteredRows)
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
            val headerList = headerMap[srcTag]
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
        val rowSize = rowsMap[tag]?.size
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
        val rowSize = rowsMap[tag]?.size
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

private enum class TypeString{
    Str,
    Int,
    Float,
}

private enum class FileType {
    CSV,
    TSV
}