# multiSelectGridDialog

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Show spannable multi select grid dialog    
-> selected spannable image paths


```js.js

jsDialog.multiSelectGridDialog(
	title: String,
	message: String,
	imagePathListTabSepalateString: String
)
-> selected spannable image paths


```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | string |
| message | string | string |
| imagePathListTabSepalateString | string | src image path strings sepalated by tab |

ex1)

```js.js
jsDialog.multiSelectGridDialog(
	"title1",
	"message1",
	"${iamge path1}\t${iamge path2}\t${iamge path3}\t..."
);
-> src image path
```

