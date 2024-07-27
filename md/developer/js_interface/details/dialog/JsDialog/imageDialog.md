# imageDialog

Show image dialog  

Table
-----------------

* [Result](#result)
* [Argument](#argument)

## Example


```js.js
jsDialog.imageDialog(
	"title1",
	"${image path}",
	"hideButtons=ok&share,
);
```

- js action version


```js.js
```js.js
var=runImageDialog
    ?func=jsDialog.imageDialog
	?args=
	    &title="title1",
	    &imagePath="${image path}",
	    &imageDialogMapCon="hideButtons=ok&share,
```

## Format


```js.js

jsDialog.imageDialog(
	title: String,
	imageSrcFilePath: String,
	imageDialogMapCon: String,
)

```

## Argument

| arg name | type       | description                        |
| -------- |------------|------------------------------------|
| title | string     | title string                       |
| imageSrcFilePath | string     | Image path string                  |
| imageDialogMapCon | key-values | -> [Detail](#imagedialogmapcon-arg |


## imageDialogMapCon arg

Image dialog setting key-value separated by `|`

### imageDialogMapCon key-value table


| Key name      | value                           | Description                                      | 
|---------------|---------------------------------|--------------------------------------------------|
| `hideButtons` | `share` <br> `ok` <br> `cancel` | Hide button <br>  Enable multiple specify by `&` |


```
