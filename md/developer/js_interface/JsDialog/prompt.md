
# promptDialog

Table
-----------------
* [Result](#overview)
* [Argument](#argument)
  * [title](#title)
  * [message](#message)
  * [suggestVars](#suggestVars)
    * [Key](#key)
    * [Format](#format)


## Result

Show prompt dialog with suggest   
-> return typed string  


```js.js

jsDialog.prompt(
	title: String,
        message: String,
        suggestVars: String,
  )
	- launch prompt dialog
```

## Argument

### title

dialog title

- In blank, remove title text view

### message

dialog message

- In blank, remove message text view

current fannel(script) path  
- [About fannel](https://github.com/puutaro/commandclick-repository#commandclick-repository)

### suggestVars

You can add as many menu buttons as you like  

#### Key

| key | val type | description |  
| ------- | ------- | ------- |  
| `variableName` | string | suggest target command variable |  
| `concatFilePathList` | string | extra suggest file path in order to concat origin suggest contnts |  

-> command variable

- `concatFilePathList` example

```
extra suggest 1
extra suggest 2
extra suggest 3
.
.
```
#### Format

Use `!` as sepalator

ex1) no suggest

```js.js
jsDialog.prompt(
    "title",
    "message",
    "",
);
```

ex2) suggest

```js.js
jsDialog.prompt(
    "",
    "",
    "variableName=suggestCmdVariable!concatFilePathList=${01}/${001}/extraSuggust.txt",
);
```
