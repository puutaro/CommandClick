
# setReplaceVariables


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

`hideSettingVariables` can specify file path like bellow.   
But, [setReplaceVariable](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) cannot use in file path.  
(bellow ${01} and ${001} is pre reserved word in `CommandClick`)
  
```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
setVariableTypes="file://${01}/${001}setVariableTypes.js"
hideSettingVariables="file://${01}/${001}/hideSettingVariables.js"
/// SETTING_SECTION_END
```

hideSettingVariables.js

```hideSettingVariables.js
// setReplace variables comment
setReplaceVariables,
"setVariableTypes",
```
