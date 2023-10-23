# bootOnExec

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Execute javascript on booting ubuntu


```js.js
jsUbuntu.bootOnExec(
  execJavascriptCode: String,
  delayMiliTime: Int
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| execJavascriptCode | string | js script string |
| delayMiliTime | string | delay mili sec after boot |



ex1) 

```js.js
jsUbuntu.bootOnExec(
  `jsToast.short("example")`,
  1000
)
```

- [jsToast.short](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/JsToast/short.md)
