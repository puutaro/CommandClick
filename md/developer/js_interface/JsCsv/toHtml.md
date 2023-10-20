# toHtml

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)


## Overview

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


## Result

html string


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

