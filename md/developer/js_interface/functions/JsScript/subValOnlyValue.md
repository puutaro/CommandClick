# subValOnlyValue

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Substitute labeling Section Contents 


```js.js
subValOnlyValue(
  targetValName: String,
  valString: String
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| targetValName | string | subsutitute target variable name |
| valString | string | variable section contents([cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) or [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)) |



ex1) 

```js.js
subValOnlyValue(
  "targetValName1",
  "targetValName1=aa\nvalName2=bb\nvalName3=cc\n.."
)

-> aa

```

