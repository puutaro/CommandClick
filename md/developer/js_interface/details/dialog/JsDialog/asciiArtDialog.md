    Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Show ascii art dialog with share button  


```js.js

jsDialog.asciiArtDialog(
	title: String,
	imagePath: String
	asciiArtMapCon: String
);

```

## Argument

| arg name | type | description                  |
| -------- | -------- |------------------------------|
| title | string | string                       |
| imagePath | string | src image path for ascii art |
| asciiArtMapCon | string | extra option map string      |


### asciiArtMapCon

Bellow, extra option key

| key            | type | description                           |
|----------------| -------- |---------------------------------------|
| savePath          | string | ascii art save path                   |
| hideButtons      | string | hide button list str separated by `&` |


- each key separated by `|`


#### hideButtons

hide button str table


| key    | type | description                              |
|--------| -------- |------------------------------------------|
| share  | string | hide share buttoon                       |
| cancel | string | hide cancel button                       |
| ok     | string | hide ok button |


ex1)

```js.js

jsDialog.asciiArtDialog(
	"title1",
	"${imagePath}",
	"savePath=/storage/emulated/0/Pictures/1124534.jpeg|hideButtons=cancel
);

```

