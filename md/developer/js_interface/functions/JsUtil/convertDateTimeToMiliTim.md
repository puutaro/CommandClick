# convertDateTimeToMiliTime

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Convert datetime string to mili time


```js.js
jsUtil.convertDateTimeToMiliTime(
  datetimeStr: String(YYYY-MM-DDThh:mm)
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| datetimeStr | string | datetime string(`YYYY-MM-DDThh:mm`) |



ex1) 

```js.js
jsUtil.convertDateTimeToMiliTime(
  "2000-11-10T10:50"
)
-> mili time 
```

