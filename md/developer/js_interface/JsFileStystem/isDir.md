# isDir

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Directory exist -> true
Directory no exist -> false


```js.js
jsFileSystem.isDir(
	directoryPath: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| directoryPath | string | directory path to confirm exist check |


ex1) 

```js.js
jsFileSystem.isDir(
	"${01}/${001}/existDir"
)
-> true
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


ex2) 

```js.js
jsFileSystem.isDir(
	"${01}/${001}/noExistDir"
)
-> false
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

