# writeLocalFile

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Write [term](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#select-term) file


```js.js
jsFileStystem.jsFile(
	filename: String,
	terminalOutPutOption: String
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| filename | string | display file name to [term](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#select-term) |
| terminalOutPutOption | string | [terminalOutput](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md) string: `NORMAL`/`REFLASH`/`REFLASH_AND_FIRST_ROW`/`DEBUG`/`NO` |



ex1) 

```js.js
jsFileStystem.jsFile(
	"display file name",
	"NORMAL"
)
```

