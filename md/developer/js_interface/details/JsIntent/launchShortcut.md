# launchShortcut

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Launch fannel 


```js.js

jsIntent.launchShortcut(
	currentAppDirPath: String,
	currentFannelName: String,
	currentFannelState: String,
) 

```

## Argument

| arg name | type | description                                                                                                                                                                                                       |
| -------- | -------- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| currentAppDirPath | string | current [app dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) path                                                                                                |
| currentFannelName | string | current [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) name                                                                                                        |
| currentFannelState | string | current [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) [state](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateRootTableTsv.md#whats-fannel-state) |


ex1)

```js.js
jsIntent.launchShortcut(
	"${01}",
	"fannelName.js"
) 

```

- `${01}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
