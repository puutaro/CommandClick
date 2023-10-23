# updateEditText

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Update `updateVariableName` view value


```js.js

jsEdit.updateEditText(
  updateVariableName: String,
  updateVariableValue: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| updateVariableName | string | [cmd variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) name you wont to update |
| updateVariableValue | string | update value string |


ex1)

```js.js
jsEdit.updateEditText(
  "updateVariableName1",
  "updateVariableValue1"
);
```
