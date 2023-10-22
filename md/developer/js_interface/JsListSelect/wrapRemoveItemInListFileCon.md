# wrapRemoveItemInListFileCon

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` and update View

- `listPath`, `LSB`, `ELSB`, `GB` and `MSB` -> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)



```js.js
jsListSelect.wrapRemoveItemInListFileCon(
    targetListFilePath: String,  
    removeTargetItem: String,  
    currentFannelPath: String,  
    replaceTargetVariable: String,  
    defaultVariable: String,
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| targetListFilePath | string | target list's file path |
| removeTargetItem | string | remove item text |
| currentFannelPath | string | current fannel path |
| replaceTargetVariable | string | replace target variable name |
| defaultVariable | string | variable name set blank |

ex1)

```js.js
jsListSelect.wrapRemoveItemInListFileCon(
  ${01}/${02}/targetListFileName.txt",
  "remove search text1",
  "${01}/${02}",
  "replaceTargetVariableName1",
  "set_blank_varialbe_name",
);

```
- `${01}`, `${001}`, `${02}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
