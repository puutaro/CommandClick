# toHeaderRow

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)

## Overview

Get headerList sepalated by tab

```js.js
jsCsv.toHeaderRow(
  tag: String,
  startColNumSource: Int,
  endColNumSource: Int,
)
  -> headerList sepalated by tab 
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to take |
| startColNumSource | int | start col index: `0` -> start index |
| endColNumSource | int | end col index: `0` -> end index  |

## Result

Get headerList sepalated by tab  


ex1) 

```js.js
jsCsv.toHeaderRow(
  "tag1",
  0,
  0,
)

```
