# multiSelectOnlyImageGridDialog


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

(this dialog only image grid view without file name search)

Show grid dialog    
-> selected image path


```js.js

jsDialog.multiSelectOnlyImageGridDialog(
	title: String,
	message: String,
	imagePathListNewLineSepalateString: String
);
-> selected image paths
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | string |
| message | string | string |
| imagePathListNewLineSepalateString | string | list image path string sepalated by newLine |

ex1)

```js.js
jsDialog.multiSelectOnlyImageGridDialog(
	"title1",
	"message1",
	"${iamge path1}\t${iamge path2}\t${iamge path3}\t..."
);
-> selected image path
```
