
# promptDialog

Table
-----------------
* [Result](#result)
* [Argument](#argument)
  * [title](#title)
  * [message](#message)
  * [promptMapCon](#promptmapcon)
    * [Key](#key)
    * [Format](#format)
  * [editText](#edittext)
    * [Key](#edit_text_key)
    * [Format](#edit_text_format)  
  * [suggest](#suggest)
    * [Key](#suggest_key)
    * [Format](#suggest_format)

## Result

Show prompt dialog with suggest   
-> return typed string  


```js.js

jsDialog.prompt(
  title: String,
  message: String,
  promptMapCon: String,
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

### promptMapCon

You can add as many menu buttons as you like  


#### Key

| key | val type | description |  
| ------- | ------- | ------- |  
| `editText` | string | edit text setting |  
| `suggest` | string | suggest setting |  

#### Format

Use `,` as sepalator


### editText


#### Key <a id="edit_text_key"></a>

| key | val type | description |  
| ------- | ------- | ------- |  
| `default` | string | defualt string |  
| `shellPath` | string | shell path for set text |  
| `fannelPath` | string | fannel path in using shell |  
| `repValCon` | string | replace variable con in using shell |  


#### Format <a id="edit_text_format"></a>

Use `!` as sepalator


### suggest

#### Key <a id="suggest_key"></a>

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

#### Format <a id="suggest_format"></a>

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
    "suggest=variableName=suggestCmdVariable|
    concatFilePathList=${01}/${001}/extraSuggust.txt",
);
```
