# removeFile

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Remove file


```js.js
jsFileStystem.removeFile(
	path: String,
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | remove file path |


ex1) 

```js.js
jsFileStystem.removeFile(
	"${01}/${001}/removeFile"
)
```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
