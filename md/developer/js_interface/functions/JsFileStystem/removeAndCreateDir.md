# removeAndCreateDir

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Remove and create dir


```js.js
jsFileSystem.removeAndCreateDir(
	directoryPath: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| directoryPath | string | directory path to remove and create |


ex1) 

```js.js
jsFileSystem.removeAndCreateDir(
	"${01}/${001}/removeAndCreateDir"
)

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
