
# hideSettingVariables




Table
-----------------
* [Overview](#overview)
* [Specify file path](#specify-file-path)


## Overview

- This option is used in order to take appearance sinply by hiding setting variables  
- This option is specified multiply  



ex)

```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
setVariableTypes="file://${01}/${001}/setVariableTypes.js"
hideSettingVariables="setReplaceVariables"
hideSettingVariables="setVariableTypes"
/// SETTING_SECTION_END

```


## Specify file path  

`hideSettingVariables` can specify config path (`${01}/${001}/settingVariables/hideSettingVariables.js`) like bellow.   

- `${01}`, `${001}` -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

  
```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://"
setVariableTypes="file://"
hideSettingVariables="file://"
/// SETTING_SECTION_END
```

hideSettingVariables.js

```hideSettingVariables.js
// setReplace variables comment
setReplaceVariables,
"setVariableTypes",
```
