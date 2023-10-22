# toHtml

Table
-----------------
* [Result](#result)
* [Argument](#argument)


## Result

Convert tsv to html string  

```js.js
jsCsv.toHtml(
  tsvString: String,
  onTh: String
)  
  
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tsvString | string | tsv string |
| onTh | string | empty -> ordinaly `td tag` html, some string -> `th tag` html |



ex1) only td tag 

```js.js
jsCsv.toHtml(
  tsvString,
  ""
)  
```

ex2) add th tag 

```js.js
jsCsv.toHtml(
  tsvString,
  "on"
)  
```

