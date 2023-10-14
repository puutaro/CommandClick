
# setReplaceVariables


Table
-----------------
* [Overview](#overview)
* [Specify file path](#specify-file-path)


## Overview

- This option is set global variable  
- This option can specify multiply  

ex)

```js.js
/// SETTING_SECTION_START
setReplaceVariables="settingVariables=editSettingVariables"
setReplaceVariables="currentAppDirPath=${01}"
setReplaceVariables="currentFannelDirPath=${currentAppDirPath}/${001}"
setVariableTypes:GB="file://"
/// SETTING_SECTION_END


const currentFannelDirPath = "${currentFannelDirPath}"
.
.
.
```


## Specify file path  

`setReplaceVariables` can specify config path (`${01}/${001}/settingVariables/setReplaceVariables.js`) like bellow.   

- `${01}`, `${001}` -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
  
  
```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://"
/// SETTING_SECTION_END
```

setReplaceVariables.js

```setReplaceVariables.js
setReplaceVariables="settingVariables=editSettingVariables",
setReplaceVariables="currentAppDirPath=${01}",
setReplaceVariables="currentFannelDirPath=${currentAppDirPath}/${001}",
```

- How to write about `setReplaceVariables.js` is above same.  But, must be comma in variable definition end. Instead, you can use indent, newline, and comment out by `//` or `#`

```setReplaceVariables.js

# replace variable1 description
settingVariables=
	"editSettingVariables",
// replace variable2 description
currentAppDirPath=
	"${01}",
// replace variable3 description
setReplaceVariables=
	"currentFannelDirPath=${currentAppDirPath}/${001}",

```
