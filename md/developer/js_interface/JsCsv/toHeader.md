# toHeader

Table
-----------------
* [Result](#result)
* [Argument](#argument)

## Result

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


ex1) 

```js.js
jsCsv.toHeader(  
    "tag1",  
    2,  
)

```
