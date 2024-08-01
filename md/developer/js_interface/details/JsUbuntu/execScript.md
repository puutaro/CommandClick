# execScript

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Execute shell script on ubuntu fastly  
-> output string

- std in and out to [monitor_2](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#select-term)
- timeout by 2000 mili sec

```js.js
jsUbuntu.execScript(
  executeShellPath:String,
  tabSepalateArguments: String = String(),
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| executeShellPath | string | shell script path |
| tabSepalateArguments | string | script args sepalated tab |



ex1) 

```js.js
jsUbuntu.execScript(
  "${01}/${001}",
  "arg1\targ2",
)
-> output string
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
 
