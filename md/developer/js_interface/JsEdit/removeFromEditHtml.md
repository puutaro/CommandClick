# removeFromEditHtml

Table
-----------------

* [Result](#overview)
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
| editPath | string | edit site source tsv path |
| removeUri | string | remove uri |

ex1)

```js.js
jsEdit.removeFromEditHtml(
  "${01}/${001}/editSiteSrc.tsv",
  "https://www.google.com"
)

```

