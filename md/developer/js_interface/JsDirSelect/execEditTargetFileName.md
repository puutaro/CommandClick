# execEditTargetFileName

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Edit targetVariable value(directory name) and update view by form dialog    



```js.js

execEditTargetFileName(  
  targetVariable: string,  
  renameVariable: string,  
  targetDirPath: string,  
  settingVariables: string,   
  commandVariables: string, 
  scriptFilePath:  string,
  title: title string
)
  
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| targetVariable | string | target variable string |
| renameVariable | string | rename destination [command variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) string |
| targetDirPath | string | file select direcoty path string |
| settingVariables | string | [setting variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#setting-variable) with tab sepalator |
| commandVariables | string | [cmd variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) with tab sepalator |
| scriptFilePath | string | [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) path string |
| title | string | title string |

ex1)

```js.js
jsFileSelect.execEditTargetFileName(
  "musicPlayListName",
  "renameMusicPlayListName",
  cmdMusicPlayerEditDirPath,
  `musicPlayListName:TXT:FSB=${FCB_DIR_PATH}=${cmdMusicPlayerEditDirPath}!${FCB_PREFIX}=${MUSIC_PREFIX}!${FCB_SUFFIX}=${tsvExtend}`,
  `musicPlayListName=${musicPlayListName}\trenameMusicPlayListName=`,
  MUSIC_PREFIX,
  tsvExtend,
  "${01}/${02}",
  "Edit musicPlayListName"
);

```
