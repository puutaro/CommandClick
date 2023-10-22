# shareImage

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Share image intent


```js.js

jsIntent.shareImage(
	shareImageFilePath: String
)


```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| shareImageFilePath | string | image file path you want to share |


ex1)

```js.js
jsIntent.shareImage(
	"${01}/${001}/imageFilePath.png",
)

```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
