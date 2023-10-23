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
  replaceTabList: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| jsContents | string | js script contents |
| replaceTabList | string | variable name to value string list sepalated by tab |



ex1) 

```js.js
jsScript.replaceSettingVariable(
  "$jsContents}",
  "editExecute=ON\tonAdBlock=OFF"
)
-> replaced js contents

```

