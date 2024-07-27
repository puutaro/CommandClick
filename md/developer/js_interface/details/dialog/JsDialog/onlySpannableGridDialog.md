# onlySpannableGridDialog


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

(this dialog only image grid view without file name search)

Show spannable grid dialog    
-> selected spannable image path   


```js.js

jsDialog.onlySpannableGridDialog(
	title: String,
	message: String,
	imagePathListTabSepalateString: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | title string |
| message | string | list message string |
| imagePathListTabSepalateString | string | elements strings sepalated by tab |

ex1)

```js.js
jsDialog.onlySpannableGridDialog(
	"title1",
	"message1",
	"${iamge path1}\n${iamge path2}\n${iamge path3}\n..."
);

-> selected image path

```
