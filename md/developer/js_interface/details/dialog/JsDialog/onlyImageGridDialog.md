# onlyImageGridDialog


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

(this dialog only image grid view without file name search)

Show grid dialog    
-> selected image path  


```js.js

jsDialog.onlyImageGridDialog(
	title: String,
	message: String,
	imagePathListNewlineSepalateString: String
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | string |
| message | string | string |
| imagePathListNewlineSepalateString | string | list image path strings sepalated by newline |

ex1)

```js.js

jsDialog.onlyImageGridDialog(
	"ex 1",
	"message1",
	"${image path1}\n${image path1}\n..."
);

-> selected image path

```
