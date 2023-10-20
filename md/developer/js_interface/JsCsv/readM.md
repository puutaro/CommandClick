
# readM

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)

## Overview

Save csv or tsv instance with tag, also header  from string 

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

## Result

Save csv or tsv instance with tag, also header  from string


ex1) 

```js.js
jsCsv.readM(
  "tag1",
  csvString,
  "TSV",
 )

```

