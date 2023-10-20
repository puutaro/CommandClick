# toHeader

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)

## Overview

Get schema name in header

```js.js
jsCsv.toHeader(  
    tag: String,  
    colNum: Int,  
)
  -> schema name  
   
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to take |
| colNum | int | schema index in header |

## Result

schema name   


ex1) 

```js.js
jsCsv.toHeader(  
    "tag1",  
    2,  
)

```
