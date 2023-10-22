# writeLocalFile

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Write string contents to file


```js.js
jsFileStystem.writeLocalFile(
	path: String,
  contents: String
)
	
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | save file path |
| contents | string | string you won to save |



ex1) 

```js.js
jsFileStystem.writeLocalFile(
	"${01}/${001}/file.txt",
  `${save contents}`
)
```

