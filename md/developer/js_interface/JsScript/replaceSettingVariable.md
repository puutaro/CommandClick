# replaceSettingVariable

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Replace setting variable
-> [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) contents


```js.js
jsScript.replaceSettingVariable(
  fannelContents: String,
  replaceTabList: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| fannelContents | string | js script contents |
| replaceTabList | string | variable name to value string list sepalated by tab |



ex1) 

```js.js
jsScript.replaceSettingVariable(
  "$fannelContents}",
  "replaceValName1=aa\treplaceValName2=bb"
)
-> replaced fannel contents

```

