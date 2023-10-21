# launchEditSite

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Launch html to edit target edit tsv file 


```js.js

jsIntent.launchEditSite(
	editPath: String,
	extraMapStr: format string (${key1}=value1|${key2}=value2|..),
	filterCode: javascipt code,
)
```

<img src="https://user-images.githubusercontent.com/55217593/222952726-f5ce0753-f299-44cd-a9b0-a021c56d3b4c.png" width="400">  


## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| editPath | string | edit tsv path |
| extraMapStr | string | format string (${key1}=value1|${key2}=value2|..) |
| filterCode | string | javascipt code |

- extra map key table

| key name | type | description |
| -------- | ------- | -------- |
| srcPath  | string | add src path |
| onClickSort | boolean string | Sort on click: default is "false" |
| onSortableJs | boolean string | Enable sort: default is "true" |
| onClickUrl | boolean string | Enable jump url: default is "true" |
| onDialog | boolean string | Enable dialog mode: default is "false" |


ex1)

```js.js
jsIntent.launchEditSite(
	"${01}/${001}"/editSite.tsv,
	"srcPath=${src path}|onClickSort=true|onSortableJs=true|onClickUrl=true|onDialog=false",
	"urlString.startsWith('http');",
);

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
