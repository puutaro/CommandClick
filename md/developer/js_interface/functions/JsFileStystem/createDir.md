# createDir

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Create file


```js.js
jsFileStystem.createDir(
	path: String,
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | craete file path |


ex1) 

```js.js
jsFileStystem.createDir(
	"${01}/${001}/createDir"
)
```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
