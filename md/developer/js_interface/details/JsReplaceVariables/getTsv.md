# getTsv

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Get [replace variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) from [tsv contents](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#replace_variables_table)



```js.js

jsReplaceVariables.getTsv(
  "${currentScriptPath}"
);
      
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| currentScriptPath | string | current script path |


ex1)

```js.js
jsReplaceVariables.getTsv(
  "${0}"
);
```

- `${0}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
