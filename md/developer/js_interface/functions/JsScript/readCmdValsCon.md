# readCmdValsCon

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Get [comamnd section](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) contents  

- Save contents to memory

```js.js
jsScript.readCmdValsCon(
  subFannelOrFannelPath: String,
) -> to memory

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| subFannelOrFannelPath | path string | sub [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) path or [fanel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel path |

-> [subFannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#sub-fannel)https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#sub-fannel


ex1) 

```js.js
jsScript.readCmdValsCon(
  `${0}`
)


```

