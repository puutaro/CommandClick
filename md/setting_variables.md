
# Setting variable

`CommandClick`'s system setting variables  
 - Follow bellow setting variable table  
   

    | settingVariable| set value | description  |
    | --------- | --------- | ------------ |
    | `terminalDo` | `ON`/`Termux`/`OFF` | where to run in terminal; `ON`: in web terminal view, `Termux`: in termux (only shell), `OFF`: backgrond exe (only shell)  |
    | `terminalSizeType` | `LONG`/`SHORT`/`OFF` | decide web terminal view size; `LONG`: long size, `SHORT`: short size, `OFF`: no sizing   |
    | `editExecute`  | `NO`/`ONCE`/`ALWAYS` | edit mode change; `NO`: normal edit, `ONCE`: one time edit and execute, `ALWAYS`: always edit and execute
    | `terminalOutputMode`  | `NORMAL`/`REFLASH`/`REFLASH_AND_FIRST_ROW`/`DEBUG`/`NO` | `NORMAL`: normal terminal output, `REFLASH`: Before terminal output, screen resflesh, `REFLASH_AND_FIRST_ROW`: Before terminal output, screen resflesh and focus first row, `DEBUG`: stdr + stderr, `NO`: no output (bacground exec)
    | `onAutoExec`  | `NO`/`OFF` | ready for start and end script; `ON`: start or end exec on, `OFF`: exec off (default)
    | `onUpdateLastModify`  | `NO`/`OFF` | how updating file last modified status when executing; `ON`: update this, `OFF`: no update this
    | `onHistoryUrlTitle`  | `ON`/`OFF` | how adding url title to history; `ON`: add, `OFF`: no
    | `historySwitch`  | `ON`/`OFF` | switch app history with url history; `ON`: switch, `OFF`: no switch, `INHERIT`: inherit config setting
    | `urlHistoryOrButtonExec`  | `INHERIT`/`URL_HISTORY`/`BUTTON_EXEC` | switch url history or button script exec; `INHERIT`: inherit config setting, `URL_HISTORY`: switch url history, `BUTTON_EXEC`: switch url button script exec
    | `onAdBlock`  | `INHERIT`/`ON`/`OFF` | sadblock switch; `INHERIT`: inherit config setting, `ON`: on, `OFF`: off
    | `onUrlHistoryRegister`  | `ON`/`OFF` | url history update signal; `ON`: update, `OFF`: no update
    | `onUrlLaunchMacro`  | `OFF`/`RECENT`/`FREAQUENCY` | url launch macro(when set, cmdclick web terminal don't output); `OFF`: no launch, `RECENT`: recent use url launch, `FREAQUENCY`: most use url launch  
    | `disableSettingButton`  | `ON`/`OFF` | setting button diable; `ON`: on, `OFF`: off  
    | `disableEditButton`  | `ON`/`OFF` | edit button diable; `ON`: on, `OFF`: off  
    | `disablePlayButton`  | `ON`/`OFF` | play button diable; `ON`: on, `OFF`: off  
    | `execPlayBtnLongPress`  | `WEB_SEARCH`/`PAGE_SEARCH`/`{js file path}` | execute when play button long press in `editExecute=EditExecute`; `WEB_SEARCH`: apear web search bar `PAGE_SEARCH`: apear page search bar `{js file path}`: execute js file   
    | `execEditBtnLongPress`  | `WEB_SEARCH`/`PAGE_SEARCH`/`{js file path}` | execute when edit button long press in `editExecute=EditExecute`; `WEB_SEARCH`: apear web search bar `PAGE_SEARCH`: apear page search bar `{js file path}`: execute js file   
    | `execJsOrHtmlPath`  | `string` | execute javascript or html file path
    | `setReplaceVariables`  | `string` | string replaced with certain string. You also have multiple specifing this. ex) setReplaceVariables="{replaceVariablle1}={repalce string1} or setReplaceVariables="file://{file path}"  -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/set_replace_variables.md) 
    | `setVariableTypes` | `string`  | when edit, whether to set variable type to commandVariable. You also have multiple specifing this. -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/set_variable_types.md) |
    | `hideSettingVariables` | `string` | specified setting varialle you want to hidden when edit(hidden variable in default) ${setting variable name}="..." or hideSettingVariables="file://{file path} [detail](https://github.com/puutaro/CommandClick/blob/master/md/hide_setting_variables.md) |
   | `ignoreHistoryPaths` | `string` |  ignore history path like grep -v ${setting variable name}="..." or ignoreHistoryPaths="file://{file path} [detail](https://github.com/puutaro/CommandClick/blob/master/md/ignore_history_paths.md) |
    | `homeScriptUrlsPath`  | `path strings` | specified `script`, url and html put always bottom in url history |
    | `homeFannelsPath` | `path strings` | specified `fannel` put always bottom in app history |
    | `srcImageAnchorLongPressMenuFilePath` | `fannnel name` | specified `fannel` to set menu for src image anchor long press |
    | `srcAnchorLongPressMenuFilePath` | `fannnel name` | specified `fannel` to set menu for src image anchor long press |
    | `imageLongPressMenuFilePath` | `fannnel name` | specified `fannel` to set menu for src image anchor long press |
    | `noScrollSaveUrls` | `file path string` | ignore scroll yPosition save |
    | `terminalFontZoom` | `number` | adjust terminal font size (percentage) |
    | `terminalFontColor` | `string` | adjust terminal font color |
    | `terminalColor` | `string` | adjust terminal background color |
    | `beforeCommand` | `shell command string` | before run shellscript, run command |
    | `afterCommand` | `shell command string` | after run shellscript, run command |
    | `scriptFileName`  | `string` | shellscript file name  |
    | `passCmdVariableEdit`  | `string` | ON: pass cmd variable edit  |


   
