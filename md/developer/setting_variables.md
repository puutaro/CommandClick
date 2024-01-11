
# Setting variable

`CommandClick`'s pre-reserved system setting variables to enable edit by gui    
If set, in [edit](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit), display secondly.

Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [editExecute](#editexecute)
* [scriptFileName](#scriptfilename)
* [terminalSizeType](#terminalsizetype)
* [terminalOutputMode](#terminaloutputmode)
* [onAutoExec](#onautoexec) 
* [onUpdateLastModify](#onupdatelastmodify)
* [historySwitch](#historyswitch)
* [onAdBlock](#onadblock)
* [onUrlHistoryRegister](#onurlhistoryregister)
* [onUrlLaunchMacro](#onurllaunchmacro)
* [onTermBackendWhenStart](#ontermbackendwhenstart)
* [onTermVisibleWhenKeyboard](#ontermvisiblewhenkeyboard)
* [onTermShortWhenLoad](#ontermshortwhenload)
* [disableShowToolbarWhenHighlight](#disableshowtoolbarwhenhighlight)
* [disableEditButton](#disableeditbutton)
* [disablePlayButton](#disableplaybutton)
* [defaultMonitorFile](#defaultmonitorfile)
* [execPlayBtnLongPress](#execplaybtnlongpress)
* [execEditBtnLongPress](#execeditbtnlongpress)
* [execJsOrHtmlPath](#execjsorhtmlpath)
* [overrideItemClickExec](#overrideItemClickExec)
* [setReplaceVariables](#setreplacevariables)
* [setVariableTypes](#setvariabletypes)
* [hideSettingVariables](#hidesettingvariables)
* [ignoreHistoryPaths](#ignorehistorypaths)
* [homeScriptUrlsPath](#homescripturlspath)
* [homeFannelsPath](#homefannelspath)
* [srcImageAnchorLongPressMenuFilePath](#srcimageanchorlongpressmenufilepath)
* [srcAnchorLongPressMenuFilePath](#srcanchorlongpressmenufilepath)
* [imageLongPressMenuFilePath](#imagelongpressmenufilepath)
* [noScrollSaveUrls](#noscrollsaveurls)
* [terminalFontZoom](#terminalfontzoom)
* [terminalFontColor](#terminalfontcolor)
* [terminalColor](#terminalcolor)
* [ubuntuSleepDelayMinInScreenOff](#ubuntusleepdelaymininscreenoff)
* [passCmdVariableEdit](#passcmdvariableedit)
* [urlHistoryOrButtonExec](#urlhistoryorbuttonexec)
* [onHistoryUrlTitle](#on_history_url_title)
* [beforeCommand](#before_command)
* [afterCommand](#after_command)
* [terminalDo](#terminal_do)

 
## editExecute

Edit and Execute on click play button.  
Particularly, `ALWAYS` is most used value in order to make execute box.


| Value| Description |
| -------- | --------- |
| `NO` | only edit (default) |
| `ALWAYS` | always edit -> execute |
| `ONCE` | one time edit and execute (deprecated) |

## scriptFileName

Enable rename script file name

## terminalSizeType

Decide web terminal view size;

| Value| Description |
| -------- | --------- |
| `OFF` | no sizing (default) |
| `LONG` | long size |
| `SHORT` | short size |

## terminalOutputMode

Switch mode to output script result 

| Value | Description |
| -------- | --------- |
| `NORMAL` | Normal terminal output (default) |
| `REFLASH` | Before terminal output, screen resflesh |
| `REFLASH_AND_FIRST_ROW` | Before terminal output, screen resflesh and focus first row |
| `DEBUG` | stdr + stderr |
| `NO` | no output (bacground exec) |

## onAutoExec

Auto exec js script on startup     

| Value | Description |
| -------- | --------- |
| `OFF` | off (default) |
| `NO` | on |

## onUpdateLastModify

Switch updating file last modified status when executing     

| Value | Description |
| -------- | --------- |
| `NO` | on (default) |
| `OFF` | off |

## historySwitch

Switch app history with [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history)     

| Value | Description |
| -------- | --------- |
| `NO` | on |
| `OFF` | off (default) |

## onAdBlock

Switch adBlock     

| Value | Description |
| -------- | --------- |
| `OFF` | off (default) |
| `NO` | on |
| `INHERIT` | inherit config setting |

## onUrlHistoryRegister

Switch [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) update     

| Value | Description |
| -------- | --------- |
| `NO` | on (default) |
| `OFF` | off  |

## onUrlLaunchMacro

Url launch macro (When set, cmdclick web terminal don't output)

| Value | Description |
| -------- | --------- |
| `RECENT` | Recent use url launch  |
| `FREAQUENCY` | Most use url launch  |
| `OFF` |  No launch |
| `{js file path}` | js script path |

## onTermBackendWhenStart

Switch display terminal backend when start

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |
| `Inherit` | Inherit config setting |

## onTermVisibleWhenKeyboard

Switch terminal visible when keyboard open

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |
| `Inherit` | Inherit config setting |

## onTermShortWhenLoad

Switch terminal sizging short when load url

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |
| `Inherit` | Inherit config setting |
    
## disableShowToolbarWhenHighlight

Switch disable to show toolbar when highlight text in webview  

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |

## disableEditButton

Switch disable to edit button

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |

## disablePlayButton

Switch disable to play button

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |

## defaultMonitorFile

Select default monitor file for screen

| Value | Description |
| -------- | --------- |
| `term_[1-4]` | [term type](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#output_monitor)  |

## execPlayBtnLongPress

Execute when play button long press in `editExecute=EditExecute`

| Value | Description |
| -------- | --------- |
| `WEB_SEARCH` | Appear web search bar |
| `PAGE_SEARCH` | Appear page search bar |
| `{js file path}` | Execute js file |

## execEditBtnLongPress

Execute when edit button long press in `editExecute=EditExecute`

| Value | Description |
| -------- | --------- |
| `WEB_SEARCH` | Appear web search bar |
| `PAGE_SEARCH` | Appear page search bar |
| `{js file path}` | Execute js file |

## execJsOrHtmlPath

Execute javascript or html file path

## setReplaceVariables

String replaced with certain string.   

You also have multiple specifing this.

ex)   

```js.js
setReplaceVariables="{replaceVariablle1}={repalce string1}   
or setReplaceVariables="file://{file path}"    
-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md) 
```

## overrideItemClickExec

Override js executor to `itemClick.js` when click in [ListIndex](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_index.md#list-index-option)

## setVariableTypes

when edit, whether to set variable type to [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables).   

You also have multiple specifing this.   
-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)

## hideSettingVariables

Specified [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md) you want to hidden when edit (hidden variable in default)   

```js.js
${setting variable name}="..."
or hideSettingVariables="file://{file path} 
```

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md) |    


## ignoreHistoryPaths

Ignore history path like grep -v 

```js.js
${setting variable name}="..."   
or ignoreHistoryPaths="file://{file path} 
```
-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/ignore_history_paths.md)

## homeScriptUrlsPath

Put [`fannel`](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) always bottom in [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history)

## homeFannelsPath

Specified [`fannel`](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) to put always bottom in app history  

## srcImageAnchorLongPressMenuFilePath

Specified [`fannel`](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) name in [current app dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) to set menu for src image anchor long press  

- [srcImageAnchorLongPressMenuFilePath.txt](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#src_image_anchor_long_press_menu)

## srcAnchorLongPressMenuFilePath

Specified [`fannel`](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) name in [current app dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) to set menu for src image anchor long press  

- [srcAnchorLongPressMenuFilePath.txt](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#src_anchor_long_press_menu)

## imageLongPressMenuFilePath

Specified [`fannel`](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) name in [current app dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) to set menu for src image anchor long press  

- [imageLongPressMenuFilePath.txt](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#image_long_press_menu)
 
## noScrollSaveUrls

Ignore scroll yPosition site domain saved       
    
## terminalFontZoom

Adjust terminal font size (percentage)      
    
## terminalFontColor

Adjust terminal font color      
    
## terminalColor

Adjust terminal background color      

## ubuntuSleepDelayMinInScreenOff

Ubuntu Sleep delay minutes in screen off, Config only value.  

-> [ubuntuSleepDelayMinInScreenOff](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#change-ubuntu-sleep-minutes)

## passCmdVariableEdit

Switch pass [edit](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit) for [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)

- `ON`: pass cmd variable edit 

## urlHistoryOrButtonExec

Switch [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) or button script exec  

| Value | Description |
| -------- | --------- |
| `INHERIT` | Inherit config setting |
| `URL_HISTORY` | Switch [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) |
| `BUTTON_EXEC` | Switch url button script exec |

## onHistoryUrlTitle (deprecated) <a id="on_history_url_title"></a>

Switch adding url title to history  

| Value | Description |
| -------- | --------- |
| `OFF` | off (default)  |
| `NO` | on |

## beforeCommand (only termux, deprecated) <a id="before_command"></a>

Before run shellscript, run command  

## afterCommand (only termux, deprecated) <a id="after_command"></a>

After run shellscript, run command  

   
## terminalDo (only termux, deprecated) <a id="terminal_do"></a>

Where to display script result

| Value| Description |
| -------- | --------- |
| `ON` | In web terminal view |
| `Termux` | In termux (only shell) |
| `OFF` | backgrond exec (only shell) |

