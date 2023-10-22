# updateSpinner

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Update `updateVariableName` spinner view selected value

```js.js


jsEdit.updateSpinner(
  updateVariableName: String,
  variableValue: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| updateVariableName | string | [cmd variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) name you want to update |
| variableValue | string | update spinner value string |


ex1)

```js.js
jsEdit.updateSpinner(
  "updateVariableName1",
  "variableValue1"
);

```
