# multiSelectSpannableGridDialog

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Show spannable multi select grid dialog    
-> selected image paths


```js.js

jsDialog.multiSelectSpannableGridDialog(
	title: String,
	message: String,
	imagePathListTabSepalateString: String
);
-> selected path strings

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | string |
| message | string | string |
| imagePathListNewlineSepalateString | string | list image path string sepalated by Newline |

ex1)

```js.js

```js.js
jsDialog.multiSelectSpannableGridDialog(
	"title1",
	"message1",
	"${iamge path1}\n${iamge path2}\n${iamge path3}\n..."
);
-> selected path strings

```
