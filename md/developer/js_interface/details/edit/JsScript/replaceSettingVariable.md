# replaceSettingVariable

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Replace [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)  
-> js contents


```js.js
jsScript.replaceSettingVariable(
  jsContents: String,
  replaceNewlineSepaList: String
)

```

## Argument

| arg name | type | description                                             |
| -------- | -------- |---------------------------------------------------------|
| jsContents | string | js script contents                                      |
| replaceNewlineSepaList | string | variable name to value string list sepalated by newline |



ex1) 

```js.js
jsScript.replaceSettingVariable(
  "$jsContents}",
  "editExecute=ON\tonAdBlock=OFF"
)
-> replaced js contents

```

