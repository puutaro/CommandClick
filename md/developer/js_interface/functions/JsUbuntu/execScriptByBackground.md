# execScriptByBackground

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Execute shell command as background  


```js.js
jsUbuntu.runByBackground(
    backgroundShellPath: String,
    argsTabSepaStr: String,
    monitorNum: Int,
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| backgroundShellPath | string | shell script path |
| argsTabSepaStr | string | args sepalated by tab |
| monitorNum | int | select [term number](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#select-term) |


ex1) 

```js.js
jsUbuntu.runByBackground(
    "${01}/${001}/background.sh",
    "arg1\targ2\targ3": String,
    1,
)

```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
