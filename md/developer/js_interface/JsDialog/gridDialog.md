# gridDialog


Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Show grid dialog    
-> selected image path


```js.js

jsDialog.gridDialog(
	title: String,
	message: String,
	imagePathListTabSepalateString: String
)
	-> selected image path


```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | string |
| message | string | string |
| imagePathListTabSepalateString | string | list image path string sepalated by tab |

ex1)

```js.js
jsDialog.gridDialog(
	"ex 1",
	"ex message 1",
	"${iamge path1}\t${iamge path2}\t${iamge path3}\t..."
);

```
