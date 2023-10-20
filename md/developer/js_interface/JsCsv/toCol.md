# toCol

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)

## Overview

Get colList sepalated by tab  

```js.js
jsCsv.toCol(
  tag: String,
  colNum: Int,
  startRowNumSource: Int,
  endRowNumSource: Int,
    )
  -> colList sepalated by tab       
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to take |
| colNum | int | get col index |
| startRowNumSource | int | get row index: `0` -> start row index |
| endRowNumSource | int | get row index: `0` -> end row index |

## Result

colList sepalated by tab  


ex1) with header

```js.js
jsCsv.read(
		"$srcTagName}",
		"${inputCTsvPath}", 
		"", 
		10,
	);
```

ex2) wighout header

```js.js
jsCsv.toCol(
  "tag1",
  10,
  0,
  0,
);
```
