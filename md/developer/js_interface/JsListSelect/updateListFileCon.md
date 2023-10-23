# updateListFileCon

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Update `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 

- `listPath`, `LSB`, `ELSB`, `GB` and `MSB` -> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)



```js.js
jsListSelect.updateListFileCon(
  targetListFilePath: String,
  itemText: String
) 

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| targetListFilePath | string | target list's file path |
| itemText | string | addin item text |

ex1)

```js.js
jsListSelect.updateListFileCon(
  "${01}/${001}/listFileName.txt",
  "add item text"
) 

```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
