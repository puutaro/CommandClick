# getValue

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Get variable value from [tsv contens](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#replace_variables_table) in [replace variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) 



```js.js

jsReplaceVariables.getValue(
	${replaceVariableTsvCon},
	${variableName}
);      
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| replaceVariableTsvCon | string | [replace variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) tsv contents |
| variableName | string | variable name | 

ex1)

```js.js

const replaceVariableTsvCon = jsReplaceVariables.getTsv("${0}");

jsReplaceVariables.getValue(
	${replaceVariableTsvCon},
	"variableName1"
);
// -> variableValue1
```

`replaceVariableTsvCon`

```
variableName1\tvariableValue1
variableName2\tvariableValue2
variableName3\tvariableValue3
.
.
.
```
- [replace variable tsv](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#replace_variables_table)

- `${0}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
