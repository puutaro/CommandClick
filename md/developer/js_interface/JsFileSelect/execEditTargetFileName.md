
# execEditTargetFileName


Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Edit targetVariable value(file name) and update view by form dialog


```js.js

execEditTargetFileName(  
  targetVariable: string,  
  renameVariable: string,  
  targetDirPath: string,  
  settingVariables: string,   
  commandVariables: string, 
  prefix: file select string,  
  suffix: file select string,  
  scriptFilePath: string
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| targetVariable | string | rename target command variable name |
| renameVariable | string | rename destination command variable name |
| settingVariables | string | [setting variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#setting-variable) sepalated by tab |
| commandVariables | string | [command variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) sepalated by tab |
| prefix | string | grep this prefix string |
| suffix | string | grep this suffix string |
| scriptFilePath | string | [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) path string |

- suffix macro: `NoExtend`: no extend

| macro name | description |
| -------- | -------- |
| `NoExtend`| filter no extend file name |



ex1)

```js.js
jsFileSelect.execEditTargetFileName(
  "playLogName",
  "renamePlayLogName",
  "${01}/${001}/targetDirName1",
  `playLogName:TXT:FSB=${FCB_DIR_PATH}=${PLAY_LOG_DIR_PATH}!${FCB_PREFIX}=playLog!${FCB_SUFFIX}=${NoExtend}`,
  `playLogName=${playLogName}\trenamePlayLogName=`,
  "log_prefix",
  "NoExtend",
  "${01}/${02}",
  "Edit playLogName"
);
```

-  `playLogName:TXT:FSB=${FCB_DIR_PATH}=${PLAY_LOG_DIR_PATH}!${FCB_PREFIX}=playLog!${FCB_SUFFIX}=${NoExtend}` -> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

