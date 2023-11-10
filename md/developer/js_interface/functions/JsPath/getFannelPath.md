# getFannelPath


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Get [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) path



```js.js
jsPath.getFannelPath(  
  path: String,  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | js file path |


ex1) 

```js.js
jsPath.getFannelPath(  
  "${0}",  
)
```

- `${01}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

