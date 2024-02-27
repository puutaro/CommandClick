# makeJsUrl

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Make javascrip applet url


```js.js
jsUrl.makeJsUrl(
  jsPath: String
)

-> javascript:(
  function() { ${jsPathCoontents} }
)();
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| jsPath | string | js script path |



ex1) 

```js.js
jsUrl.makeJsUrl(
  "${01}/${001}/jsScriptName.js"
)
-> javascript:(
  function() { ${jsScriptName.js applet contents} }
)();
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

