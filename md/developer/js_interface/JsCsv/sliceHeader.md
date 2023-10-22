# sliceHeader

Table
-----------------
* [Result](#result)
* [Argument](#argument)


## Result

Get header string sliced with tab delimiter   

```js.js
jsCsv.sliceHeader(
  tag: String,
  startColNumSource: Int,
  endColNumSource: Int,
  headerRow: String,
) 
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to slice |
| startColNumSource | string | start col index: `0` -> start index |
| endColNumSource | string | end col index: `0` -> end index |
| headerRow | string | header string sepalated by tab |


ex1) 

```js.js
jsCsv.sliceHeader(
  "tag1",
  0,
  0,
  headerRow,
) 
```

