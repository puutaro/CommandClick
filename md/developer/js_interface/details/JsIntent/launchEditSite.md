# launchEditSite

Table
-----------------

* [Result](#result)
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
| filterCode | js string | javascipt code: val -> `urlString`, `urlTitle` |

- extra map key table

| key name | type | description |
| -------- | ------- | -------- |
| src_path  | string | add src path |
| on_click_sort | boolean string | Sort on click: default is "false" |
| on_sortable_js | boolean string | Enable sort: default is "true" |
| on_click_url | boolean string | Enable jump url: default is "true" |
| on_dialog | boolean string | Enable dialog mode: default is "false" |
| latest_url_title_filter_code | js string | javascript code: val -> `latestUrlTitleSrc` |

ex1)

```js.js
jsIntent.launchEditSite(
	"${01}/${001}"/editSite.tsv,
	"srcPath=${src path}|on_click_sort=true|on_sortable_js=true|on_click_url=true|on_dialog=false",
	"urlString.startsWith('http');",
);

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
