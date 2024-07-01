# isFile

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

File exist -> true
File no exist -> false


```js.js
jsFileSystem.isFile(
	filePath: String
)


```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| filePath | string | file path to confirm exist check |


ex1) 

```js.js
jsFileSystem.isFile(
	"${01}/${001}/existFile"
)
-> true
```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)



ex2) 

```js.js
jsFileSystem.isFile(
	"${01}/${001}/noExistFile"
)
-> false
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
