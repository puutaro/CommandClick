# isRead

Table
-----------------
* [Result](#result)
* [Argument](#argument)


## Result

Comfirm read completed  about csv(tsv) with tag

| result value | type | description |
| -------- | -------- | -------- |
| blank | - | not readi |
| string | string | read ok |


```js.js
jsCsv.isRead(
  tag: String
) 
-> blank or string
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tag | string | csv data tag that you want to confirm |



ex1) 

```js.js
jsCsv.isRead(
  "tag1"
)
```

