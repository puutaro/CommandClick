# copyDir

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Copy directory


```js.js
jsFileStystem.copyDir(
	sourcePath: String,
	destiDirPath: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| sourcePath | string | src directory |
| destiDirPath | string | destination directory path |


ex1) 

```js.js
jsFileStystem.copyDir(
	"${01}/${001}/srcDir",
	"${01}/${001}/destiDir",
)

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
