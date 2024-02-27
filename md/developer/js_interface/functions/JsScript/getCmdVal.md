# getCmdVal

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Get variable value [comamnd section](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) contents that [readCmdValsCon](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsScript/readCmdValsCon.md#readcmdvalscon) read  


```js.js
jsScript.getCmdVal(
   cmdValName: String,
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| cmdValName | string | [command variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) name |



ex1) 

```js.js
jsScript.readCmdValsCon(
  `${0}`,
)

jsScript.getCmdVal(
  `${cmd val name1}`
)

```
- ${0} -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
  
