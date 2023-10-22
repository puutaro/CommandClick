
# read

Table
-----------------
* [Result](#result)
* [Argument](#argument)

## Result

Save csv or tsv instance with tag, also header   

```js.js
jsCsv.read(
  tag: String,
  csvPath: String,
  withNoHeader: String,
  limitRowNumSource: Int
  )
  
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to put |
| csvPath | string | csv path string |
| withNoHeader | string | `on`/`""` switch to put header |
| limitRowNumSource | Int | low limit to read |


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
jsCsv.read(
		"${srcTagName}",
		"${inputCTsvPath}", 
		"on", 
		10,
	);
```
