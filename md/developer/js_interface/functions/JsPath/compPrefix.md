# compPrefix

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Complete file name with prefix 


```js.js
jsPath.compPrefix(  
  path: String,  
  prefix: String,  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | file name |
| prefix | string | file prefix you wont to complete |


ex1) comp

```js.js
jsPath.compPrefix(  
  "ComFileName",  
  "prefix",  
)

-> prefixComFileName

```

ex2) no comp

```js.js
jsPath.compPrefix(  
  "prefixNoComFileName",  
  "prefix",  
)
-> prefixNoComFileName

```

