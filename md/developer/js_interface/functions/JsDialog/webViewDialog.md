
# webViewDialog

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [urlStr](#urlstr)
  * [currentFannelPath](#currentfannelpath)
  * [menuMapStrListStr](#menumapstrliststr)
    * [Key](#key)
    * [Format](#format)
  * [longPressMenuMapListStr](#longpressmenumapliststr)
    * [Menu type](#menu-type)
    * [Format](#format) 


## Overview

Show webview dialog.  
This feature change simple url to mini android application easily.  
So, this is one of the most used dialog in `CommandClick`.  

```js.js

jsDialog.webView_S(
    urlStr: String,
    currentFannelPath: String,
    menuMapStrListStr: String,
    longPressMenuMapListStr: String
)

```

## Argument

### urlStr

load url

### currentFannelPath

current fannel(script) path  
- [About fannel](https://github.com/puutaro/commandclick-repository#commandclick-repository)

### menuMapStrListStr

You can add as many menu buttons as you like  

#### Key

| key | val type | description |  
| ------- | ------- | ------- |  
| `clickMenuFilePath` | string | file path written click menu list |  
| `longPressMenuFilePath` | string | file path written long press menu list |  
| `dismissType` | string | which click to dismiss dismiss |  
| `dismissDelayMiliTime` | int | dissmiss delay time |  
| `iconName` | string | icon name |

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


- JsMacroType

Support certain js script name macro 

| script name | macro function |
|-------|-------|
| `HIGHLIGHT_SCH.js` | Web search about highlight text  |
| `GO_BACK.js` | Go back |
| `LAUNCH_LOCAL.js` | Launch local webview |
| `HIGHLIGHT_COPY.js` | Copy highlight text |


- `dismissType` table

| type | description | 
| ------- | ------- |  
| `click` | Dismiss in only click |  
| `longpress` | Dismiss in only long press |  
| `both` | Dismiss in click and long press |  

- enable 'iconName

copy, search, wheel, history, oeverflow, cancel, file, ok, puzzle, terminal, down, reflesh, edit, setup, shortcut, folda, setting, plus, support, play, share, launch, update, info, about

#### Format

Use `!` as sepalator

ex1)
```

jsDialog.webView_S(
    launchUrlString,
    "${0}",
    "clickMenuFilePath=${01}/${001}/clickMenu.txt!longPressMenuFilePath=${01}/${001}/longPressMenu.txt!dismissType=both!dismissDelayMiliTime=300!iconName=wheel",
    "",
);

ex2) 
```

jsDialog.webView_S(
    launchUrlString,
    "${0}",
    "dismissType=both!iconName=cancel",
    "",
);

- `$0}` -> [pre resrved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

ex2) 

```

jsDialog.webView_S(
    launchUrlString,
    "${0}",
    "dismissType=longpress!iconName=back",
    "",
);

```

`clickMenuFilePath` contents

`
GO_BACK.js
`


- Specify multiple button

Use `|` as partition

ex1)

```
jsDialog.webView_S(
    launchUrlString,
    "${0}",
    "clickMenuFilePath=${01}/${001}/clickMenu1.txt!dismissType=longpress!iconName=back|clickMenuFilePath=${01}/${001}/clickMenu.txt!longPressMenuFilePath=${01}/${001}/longPressMenu.txt!dismissType=both!|dismissDelayMiliTime=300!iconName=wheel|dismissDelayMiliTime=300!iconName=wheel|clickMenuFilePath=${01}/${001}/clickMenu3.txt!iconName=cancel!dismissType=both",
    "",
);

```

clickMenu1.txt

`
GO_BACK.js
`

### longPressMenuMapListStr

This is about long press menu in webview

#### Menu type

| key | val type | description |  
| ------- | ------- | ------- |  
| `srcImageAnchorMenuFilePath` | string | file path for source image ancker menu |  
| `srcAnchorMenuFilePath` | string | file path source anchor menu |  
| `imageMenuFilePath` | string | file path image menu |  


#### Format

Use `!` as sepalator

ex)

```
jsDialog.webView_S(
    launchUrlString,
    "${0}",
    "`clickMenuFilePath=${leftMenuListPath}!longPressMenuFilePath=${leftLongPressMenuListPath}!dismissType=longpress!iconName=back`|`clickMenuFilePath=${centerMenuListPath}!longPressMenuFilePath=${centerLongPressMenuListPath}!iconName=search`|`clickMenuFilePath=${rightMenuListPath}!iconName=wheel`",
    "`srcImageAnchorMenuFilePath=${srcImageAnchorMenuListPath}`|`srcAnchorMenuFilePath=${srcAnchorMenuListPath}`|`imageMenuFilePath=${imageMenuListPath}`",
);

```
