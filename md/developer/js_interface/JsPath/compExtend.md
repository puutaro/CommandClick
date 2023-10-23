# compExtend

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Complete file path with extend 


```js.js
jsPath.compExtend(  
  path: String,  
  extend: String  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | file name |
| extend | string | file extend you wont to complete |


ex1) comp

```js.js
jsPath.compExtend(  
  "${01}/${001}/ComFileName",  
  "extend",  
)

-> ${01}/${001}/ComFileName.extend

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

ex2) no comp

```js.js
jsPath.compExtend(  
  "${01}/${001}/extendNoComFileName.extend",  
  "extend",  
)
-> ${01}/${001}/extendNoComFileName.extend

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

