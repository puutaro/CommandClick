# replaceComamndVariable

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Replace [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)   
-> js contents



```js.js
jsScript.replaceComamndVariable(
    jsContents: String,
    replaceTabList: String,
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| jsContents | string | js script contents |
| replaceTabList | string | variable name to value string list sepalated by tab |



ex1) 

```js.js
jsScript.replaceComamndVariable(
  "$jsContents}",
  "cmdValName1=aa\tcmdValName2=bb"
)
-> replaced js contents

```

