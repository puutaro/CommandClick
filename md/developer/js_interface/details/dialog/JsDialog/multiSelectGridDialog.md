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
	imagePathListNewlineSepaStr: String
)
-> selected spannable image paths


```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | string |
| message | string | string |
| imagePathListNewlineSepaStr | string | src image path strings sepalated by tab |

ex1)

```js.js
jsDialog.multiSelectGridDialog(
	"title1",
	"message1",
	"${iamge path1}\n${iamge path2}\n${iamge path3}\n..."
);
-> src image path
```

