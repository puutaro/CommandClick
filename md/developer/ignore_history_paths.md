
# ignoreHistoryPaths


Table
-----------------
* [Overview](#overview)
* [Specify file path](#specify-file-path)


## Overview

- This option is used in order to certain url ignore like `grep -v`  
- This option is specified multiply  

ex)

```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
ignoreHistoryPaths="${currentAppDirPath}"
ignoreHistoryPaths="hogehoge"
/// SETTING_SECTION_END

```



## Specify file path  

`ignoreHistoryPaths` can specify config path (`${01}/${001}/settingVariables/ignoreHistoryPaths.js`) like bellow.   

- `${01}`, `${001}` -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

  
```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
setVariableTypes="file://${01}/${001}/setVariableTypes.js"
ignoreHistoryPaths="file://${01}/${001}/hideSettingVariables.js"
/// SETTING_SECTION_END
```

ignoreHistoryPaths.js

```ignoreHistoryPaths.js
// ignoreHistoryPaths comment
"${currentAppDirPath}",
"hogehoge",
```

