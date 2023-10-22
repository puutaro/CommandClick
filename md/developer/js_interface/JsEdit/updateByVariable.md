# updateByVariable

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Update target [cmd variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) value in [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)  


```js.js

jsEdit.updateByVariable(
  fannelScriptPath: String,
  targetVariableName: String,
  updateVariableValue: String,
) 

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| fannelScriptPath | string | [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) script path string |
| targetVariableName | string | target [cmd variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) name |
| updateVariableValue | string | update [cmd variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) value string |

ex1)

```js.js
jsEdit.updateByVariable(
  "${0}",
  "targetVariableName1",
  "updateVariableValue1",
) 

```

- ${0} -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
