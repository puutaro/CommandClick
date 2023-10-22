# formJsDialog


Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)


## Overview

Show form dialog.  
This feature release edit system in `CommandClick`.  
So, understanding this function requires an understanding of the edit function.  

```js.js

jsDialog.formDialog(
  title: string,
	setVariableTypes: String,
	commandVariables: String
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | csv data tag that you want to put |
| setVariableTypes  | string | tab sepalated [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) |
| commandVariables | string | tab sepalated [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) |

- In this feature, cannot use some [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) with dialog and button 


## Result

form string with newline

ex1)

```
jsDialog.formDialog(
  `Form sample`,
  `colName1:CB=ON!OFF\tcolName2=ON!OFF`,
  `colName=${colName}`,
);

-> `colName1=ON\ncolName2=OFF`
```


ex2)

```
jsDialog.formDialog(
  `Form sample2`,
  `colName:RO=\toperator:CB=ON!OFF\tfilterGain:ELCB=${LIST_PATH}=${CURRENT_FILTER_GAIN_LIST_FILE_PATH}!${LIMIT_NUM}=30`,
  `colName=${colName}\toperator=${colName}\tfilterGain=${filterGain}`,
);

-> "colName=...\noperator=ON\nfilterGain=list1"
```
