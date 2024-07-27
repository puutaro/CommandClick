# qrScan_S


Table
-----------------
* [Overview](#overview)
* [Example](#example)
* [Argument](#argument)
    * [title](#title)
    * [currentFannelPath](#currentfannelpath)
    * [callBackJsPath](#callbackjspath)
    * [menuMapStrListStr](#menumapstrliststr)
        * [Key](#key)

## Overview

Show QR code scan dialog.

```js.js
jsDialog.qrScan_S(
    title: String,
    currentFannelPath: String,
    callBackJsPath: String,
    menuMapStrListStr: String,
)
```


## Example

Use `?` as sepalator

ex1)
```

jsDialog.qrScan_S(
    launchUrlString,
    "${0}",
    "${callBackJsPath}"
    "clickMenuFilePath=${01}/${001}/clickMenu1.txt?longPressMenuFilePath=${01}/${001}/longPressMenu1.txt?dismissType=both?dismissDelayMiliTime=300!iconName=down|clickMenuFilePath=${01}/${001}/clickMenu2.txt?longPressMenuFilePath=${01}/${001}/longPressMenu2.txt!dismissType=both!dismissDelayMiliTime=300!label=exra",
);

```

- `${0}` -> [pre resrved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


## Argument

### title

title string

### currentFannelPath

current fannel(script) path
- [About fannel](https://github.com/puutaro/commandclick-repository#commandclick-repository)


### callBackJsPath

Js triggered on decoded Qr code

- first arg in [jsArgs.get](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsArgs/get.md) is decoded text.


### menuMapStrListStr

You can add as many menu buttons as you like

#### Key

| key                     | val type    | description                            |  
|-------------------------|-------------|----------------------------------------|  
| `clickMenuFilePath`     | string      | file path written click menu list      |  
| `longPressMenuFilePath` | string      | file path written long press menu list |  
| `dismissType`           | string      | which click to dismiss dismiss         |
| `iconName`              | icon string | [icon name macro](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md)                        | 

- `clickMenuFilePath`, `longPressMenuFilePath` example

`
js script name 1
js script name 1
js script name 3
.
.
.
`
(parent dir is current app dir)

- `dismissType` table

| type | description | 
| ------- | ------- |  
| `click` | Dismiss in only click |  
| `longpress` | Dismiss in only long press |  
| `both` | Dismiss in click and long press |


- `${01}/${001}/clickMenu1.txt`

```tsv.tsv
${menu js path1}
${menu js path2}
${menu js path3}
.
.
.
```

- `${01}/${001}/longPressMenu1.txt`

```tsv.tsv
${long menu js path1}
${long menu js path2}
${long menu js path3}
.
.
.
```
