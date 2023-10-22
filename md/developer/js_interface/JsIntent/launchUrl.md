# launchUrl

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Launch uri(not url but uri)


```js.js

jsIntent.launchUrl(
	urlString: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| urlString | string | uri string |


ex1)

```js.js
jsIntent.launchUrl(
	"https://www.google.com/"
)
```

ex2)

```js.js
jsIntent.launchUrl(
	"file://${01}/${001}/launchFile.txt"
)
```

- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
