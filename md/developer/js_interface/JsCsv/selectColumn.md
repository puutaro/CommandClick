# selectColumn

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)


## Overview

Save column selected tsv instance with tag, also header  

```js.js
jsCsv.selectColumn(
  srcTag: String,
  destTag: String,
  tabSepaColumns: String ({column1}\t{column2}\t{column3}\t..)  
    )
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| srcTag | string | csv data tag that you want to select |
| destTag | string | csv data tag that you want to output |
| comaSepaColumns | string | columen sepalated by tab |

- comaSepaColumns example

```
{column1}\t{column2}\t{column3}\t..
```


## Result

Save column selected tsv instance with tag, also header  


ex1) 

```js.js
jsCsv.selectColumn(
  "srcTag1",
  "destTag1",
  "{column1}\t{column2}\t{column3}"  
)
```

