# toRow

Table
-----------------
* [Result](#result)
* [Argument](#argument)

## Result

Get rowList sepalated by tab  

```js.js
jsCsv.toRow(
  tag: String,
  rowNum: Int,
  startColNumSource: Int,
  endColNumSource: Int,
) 
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to take |
| rowNum | int | get row index |
| startColNumSource | int | get start col index: `0` -> start col index |
| endColNumSource | int | get end col index: `0` -> end col index |      


ex1) 

```js.js
jsCsv.toRow(
  "tag1",
  10,
  0,
  0,
)
```

