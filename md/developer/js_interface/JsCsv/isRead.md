# isRead

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)


## Overview

Comfirm read completed  about csv(tsv) with tag  

```js.js
jsCsv.isRead(
  tag: String
) 
-> blank or string
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to confirm |

## Result

| result value | type | description |
| -------- | -------- | -------- |
| blank | - | not readi |
| string | string | read ok |


ex1) 

```js.js
jsCsv.isRead(
  "tag1"
)
```

