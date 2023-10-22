# checkExtend


Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Check extend  
-> Boolean (true when including tab separated extend String)


```js.js
jsPath.checkExtend(  
  tag: String,  
  extendTabSeparateStr: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | file name |
| prefix | string | file prefix you wont to complete |


ex1) 

```js.js
jsPath.checkExtend(  
  "${01]/${001}/file.txt",  
  ".txt\t.csv"
)
-> true
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


ex2) 

```js.js
jsPath.checkExtend(  
  "${01]/${001}/file.tsv",  
  ".txt\t.csv"
)
-> false

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

