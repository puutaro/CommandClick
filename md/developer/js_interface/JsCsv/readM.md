
# readM

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)


## Overview

Read csv from memoy 

```js.js
jsCsv.readM(
  tag: String,
  csvString: String,
  csvOrTsv: String,
 )

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to put |
| csvString | string | csv data string |
| csvOrTsv | string | `CSV`/`TSV` switch csv or tsv |



ex1) 

```js.js
jsCsv.readM(
  "tag1",
  csvString,
  "TSV",
 )

```

