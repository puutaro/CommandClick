# getFannelDirPath


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Get [fannel dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#fannel_dir) path

- [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

```js.js
jsPath.getFannelDirPath(  
  path: String,  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| path | string | js file path |


ex1) 

```js.js
jsPath.getFannelDirPath(  
  "${0}",  
)
```

- `${0}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

