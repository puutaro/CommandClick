
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
* [defaultMonitorFile](#defaultmonitorfile)
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
* [playButtonConfig](#playbuttonconfig)
* [editButtonConfig](#editbuttonconfig)
* [settingButtonConfig](#settingbuttonconfig)
* [extraButtonConfig](#extrabuttonconfig)
* [editBoxTitleConfig](#editboxtitleconfig)
* [noScrollSaveUrls](#noscrollsaveurls)
* [terminalFontZoom](#terminalfontzoom)
* [terminalFontColor](#terminalfontcolor)
* [terminalColor](#terminalcolor)
* [ubuntuSleepDelayMinInScreenOff](#ubuntusleepdelaymininscreenoff)
* [onRootfsSdCardSave](onrootfssdcardsave)
* [ubuntuAutoSetup](ubuntuautosetup)
* [passCmdVariableEdit](#passcmdvariableedit)
* [urlHistoryOrButtonExec](#urlhistoryorbuttonexec)
* [terminalDo](#terminal_do)
* [settingImport](#settingimport)
* [importDisableValList](#importdisablevallist)

 
## editExecute

Edit and Execute on click play button.  
Particularly, `ALWAYS` is most used value in order to standalone app.


| Value| Description |
| -------- | --------- |
| `NO` | only edit (default) |
| `ALWAYS` | always edit -> execute |

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


## defaultMonitorFile

Select default monitor file for screen

| Value | Description |
| -------- | --------- |
| `term_[1-4]` | [term type](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#output_monitor)  |

## execJsOrHtmlPath

Execute javascript or html file path

## setReplaceVariables

This variable **definite constant variable**.   
You also have multiple specifing this.

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md)

- ex   

```js.js
setReplaceVariables="{replaceVariablle1}={repalce string1}        
```

or

```js.js
setReplaceVariables="file://{file path}"     
```

## overrideItemClickExec

Override js executor to `itemClick.js` when click in [ListIndex](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_index.md#list-index-option)

## setVariableTypes

This variable is **controller** in MVVM or MVC architecture    
When edit, set variable type to [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables).   

You also have multiple specifying this.   
-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)

## hideSettingVariables

Specified [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md) you want to hidden when edit (hidden variable in default)   

- ex

```js.js
hideSettingVariables="setVariableTypes,editExecute,..."
hideSettingVariables="ignoreHistoryPaths,..." 
```

or 

```js.js
hideSettingVariables="file://{file path} 
```

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md) |    


## ignoreHistoryPaths

Ignore history path like grep -v 

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/ignore_history_paths.md)

- ex 

```js.js
ignoreHistoryPaths="..."    
```
or

```js.js   
ignoreHistoryPaths="file://{file path} 
```

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

## listIndexConfig

[List index](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_index.md) setting config

-> [list index config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

- ex

```js.js
/// SETTING_SECTION_START
listIndexConfig="type=normal,..." 
/// SETTING_SECTION_END
```

- file prefix ex

```js.js
/// SETTING_SECTION_START
listIndexConfig="file://{list index config path} 
/// SETTING_SECTION_END
```

## qrDialogConfig

Qr logo setting config

-> [list logo config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listLogoConfig.md)

- ex

```js.js
/// SETTING_SECTION_START
qrDialogConfig="mode=normal,..." 
/// SETTING_SECTION_END
```

- file prefix ex

```js.js
/// SETTING_SECTION_START
qrDialogConfig="file://{qr dialog config path} 
/// SETTING_SECTION_END
```


## playButtonConfig

Set play or ok button config

-> [toolbar button config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/toolbarButtonConfig.md)

- ex 

```js.js
/// SETTING_SECTION_START
playButtonConfig="icon=play,click=OK..." 
/// SETTING_SECTION_END
```

- file prefix ex 

```js.js
/// SETTING_SECTION_START
playButtonConfig="file://{play button config path} 
/// SETTING_SECTION_END
```


## editButtonConfig

Set edit button config

-> [toolbar button config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/toolbarButtonConfig.md)

- ex

```js.js
/// SETTING_SECTION_START
editButtonConfig="icon=edit,click=EDIT..." 
/// SETTING_SECTION_END
```

- file prefix ex

```js.js
/// SETTING_SECTION_START
editButtonConfig="file://{edit button config path} 
/// SETTING_SECTION_END
```

## settingButtonConfig

Set setting button config

-> [toolbar button config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/toolbarButtonConfig.md)

- ex

```js.js
/// SETTING_SECTION_START
settingButtonConfig="icon=setting,click=MENU..." 
/// SETTING_SECTION_END
```

- file prefix ex

```js.js
/// SETTING_SECTION_START
settingButtonConfig="file://{setting button config path} 
/// SETTING_SECTION_END
```

## extraButtonConfig

Set extra button config

-> [toolbar button config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/toolbarButtonConfig.md)

- ex

```js.js
/// SETTING_SECTION_START
extraButtonConfig="icon=setup,click=MENU..." 
/// SETTING_SECTION_END
```

- file prefix ex

```js.js
extraButtonConfig="file://{extra button config path} 
/// SETTING_SECTION_END
```

## editBoxTitleConfig

Manage fannel title

-> [edit box title config](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/editBoxTitleConfig.md)

## fannelStateConfig

Manage fannel state

-> [fannel state config](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateConfig.md)

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

## onRootfsSdCardSave

Ubuntu backup rootfs save switch for sd card

- ref: [Backup ubuntu rootfs](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#backup-ubuntu-rootfs)

## ubuntuAutoSetup

Enable auto setup when ubuntu start

- If you `onRootfsSdCardSave` is `ON`, available only when an SD card is inserted
- ref: [Ubuntu Setup](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#setup-ubuntu)

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

## settingImport

Import setting variable from js file

- Set js file full paths by comma separated or multiple specify this variable

## importDisableValList

`settingImport` cancel

- Set `setting variable name` by comma separated or multiple specify this variable
