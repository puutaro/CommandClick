# readLocalFile

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Read file contents


```js.js
jsFileStystem.readLocalFile(
	path: String,
)
	
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | read file path |



ex1) 

```js.js
jsFileStystem.readLocalFile(
	"${01}/${001}/file.txt",
)
```

`${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)



