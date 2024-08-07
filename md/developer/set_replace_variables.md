
# setReplaceVariables


Table
-----------------
* [Overview](#overview)
* [Specify file path](#specify-file-path)
* [settingimport](#settingimport)


## Overview

- This setting is one of the most use setting
- This setting is set global variable by replace varaiableb name to value
- Fast about replace velocity: `2000` varaibles -> `0.06` secoonds.   
- So colled "model" in `MVC` and `MVVM`.  
- This setting can specify multiply

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
"currentFannelDirPath=${currentAppDirPath}/${001}",

```

## settingimport

Other Replace variable can import

- This is second priority

### example for settingimport

#### src replace variables

```setReplaceVariables.js
settingVariables=
	"editSettingVariables",
currentAppDirPath=
	"${01}",
currentFannelDirPath=
    "${currentAppDirPath}/${001}",
settingimport=
    "${import replace varialbe path}"
```

#### ${import replace variable path}

```js.js
currentAppDirPath=
	"${import current app dir path}",
{import variable name1}=
    {import variable value1},
{import variable name2}=
    {import variable value2},
```


#### result replace variable

```setReplaceVariables.js
settingVariables=
	"editSettingVariables",
currentAppDirPath=
	"${01}",
currentAppDirPath=
	"${import current app dir path}",
{import variable name1}=
    {import variable value1},
{import variable name2}=
    {import variable value2},
```

- Override `currentAppDirPath` variable with second click key
- Add `{import variable name1}`, `{import variable name2}` key