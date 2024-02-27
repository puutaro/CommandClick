# removeFromEditHtml

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Remove uri from edit site source


```js.js

jsEdit.removeFromEditHtml(
  editPath: String,
  removeUri: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| editPath | string | [edit site](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/JsIntent/launchEditSite.md) source tsv path |
| removeUri | string | remove uri |

ex1)

```js.js
jsEdit.removeFromEditHtml(
  "${01}/${001}/editSiteSrc.tsv",
  "https://www.google.com"
)

```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
