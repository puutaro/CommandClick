# removeExtend


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Remove extend 


```js.js
jsPath.removeExtend(  
  path: String,  
  extend: String  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | file name |
| extend | string | file extend you wont to remove |


ex1) 

```js.js
jsPath.removeExtend(  
  "${01}/${001}/remove.csv",  
  ".csv"  
)
-> ${01}/${001}/remove

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
