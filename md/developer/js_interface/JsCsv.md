# JsCsv

Csv edit interface 


```js.js

jsCsv.read(
  tag: String,
  csvPath: String,
  withNoHeader: String,
  csvOrTsv: String,
  limitRowNumSource: Int
  )
  - save csv or tsv instance with tag, also header   
 
jsCsv.readM(
  tag: String,
  csvString: String,
  csvOrTsv: String,
 )
  - save csv or tsv instance with tag  
 
jsCsv.takeRowSize(
  tag: String
    )
  -> rowSize about csv(tsv) with tag

jsCsv.takeColSize(
  tag: String
  )
  -> colSize about csv(tsv) with tag

jsCsv.isRead(
  tag: String
   ) 
  (comfirm read completed  about csv(tsv) with tag)
  -> blank or String  

jsCsv.toHeader(  
        tag: String,  
        colNum: Int,  
    )
  -> schema name  

jsCsv.toHeaderRow(
  tag: String,
  startColNumSource: Int,
  endColNumSource: Int,
)
  -> headerList sepalated by tab   

jsCsv.toRow(
  tag: String,
  rowNum: Int,
  startColNumSource: Int,
  endColNumSource: Int,
    )
  -> rowList sepalated by tab    

jsCsv.toCol(
  tag: String,
  colNum: Int,
  startRowNumSource: Int,
  endRowNumSource: Int,
    )
  -> colList sepalated by tab    

jsCsv.toHtml(
  tsvString: String,
  onTh: String (empty -> ordinaly `td tag` html, some string -> `th tag` html)
  )  
  convert tsv to html string  
  -> html string   

jsCsv.outPutTsvForDRow(
  tab: String
   ) 
  convert row direction tsv to Tsv  
  -> tsv string

jsCsv.outPutTsvForDCol(
  tab: String
  ) 
  convert col direction tsv to Tsv  
  -> tsv string

jsCsv.filter(
  srcTag: String,
  destTag: String,
  tabSepaFormura: String ({schema1},>,1500\t{schema2},in,Monday,\t{schema3},=,super man\t..)  
    )
  -> save filterd tsv instance with tag, also header

jsCsv.selectColumn(
  srcTag: String,
  destTag: String,
  comaSepaColumns: String ({column1}\t{column2}\t{column3}\t..)  
    )
  -> save culumn selected tsv instance with tag, also header

jsCsv.sliceHeader(
  tag: String,
  startColNumSource: Int,
  endColNumSource: Int,
  headerRow: String,
    )
  -> header string sliced with tab delimiter   
	    
	    

```
