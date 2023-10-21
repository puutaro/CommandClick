# toCol

Table
-----------------
* [Result](#result)
* [Argument](#argument)

## Result

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
