
# Command Click
![cmdclick_image](https://user-images.githubusercontent.com/55217593/199425521-3f088fcc-93b0-4a84-a9fd-c75418f40654.png)  
[![Android: 8+](https://img.shields.io/badge/Android-8+-blueviolet.svg?style=popout&logo=android)]((https://opensource.org/licenses/MIT))
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)]((https://opensource.org/licenses/MIT))
[![Release: 0.0.40](https://img.shields.io/badge/Release-0.0.40-brightgreen.svg)]((https://github.com/puutaro/CommandClick/releases))
<img src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/com.mirfatif.permissionmanagerx&label=IzzyOnDroid&cacheSeconds=86400">  
So called `Script browser`, It's a enforced browser by javascript and shellscript. Also, it is termux gui client .

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)


Pros
----
- Web browser powerd by javascript and shellscript(termux)
- Javascript engine and framework.    
- Row code tool for android application development.  
- Easily turn javascript and shellscript into a GUI application in android.
- Versatile usage for Terminal, Chrome, OS setting, etc.
- Not only web browser but also termux gui client.


Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [App installation](#app-installation)
* [Usage](#usage)
  * [History](#history)
  * [Change term size](#change-term-size)
  * [Add](#add)
  * [Run](#run)
  * [Edit](#edit)
  * [Write](#write)
  * [Kill](#kill)
  * [Description](#description)
  * [Copy file](#copy-file)
  * [Copy file path](#copy-file-path)
  * [Add](#add)
  * [Change app dir](#change-app-dir)
  * [Create shortcut](#create-shortcut)
  * [Install fannel](#install-fannel)
  * [Import library](#import-library)
  * [Config](#config)
  * [Termux setting](#termux-setting)
  * [No scroll save url](#no-scroll-save-url)
  * [Term reflesh](#term-reflesh)
  * [Forward](#forward)
  * [Search mode](#search-mode)
    * [Terminal filter](#terminal-filter)
    * [Terminal search](#terminal-search)
    * [Launch web menu](#launch-web-menu)
    * [Highlight search](#highlight-search)
    * [Web search](#web-search)
  * [Auto exec script](#auto-exec-script)
    * [Startup script](#startup-script)
  * [Internet Button exec script](#internet-button-exec-script)
  * [Button exec script](#button-exec-script)
  * [Edit execute once](#edit-execute-once)
  * [Edit execute always](#edit-execute-always)
  * [Url command](#url-command)
  * [Html automaticaly creation command to edit target edit file](#html-automaticaly-creation-command-to-edit-target-edit-file)
  * [File api](#file-api)
  * [JavaScript interface](#javascript-interface)
  * [Javascript pre order word](#javascript-pre-order-word)
  * [Include Javascript Library](#include-javascript-library)
  * [Include css Library](#include-css-library)
  * [Html tag output](#html-tag-output)
  * [Javascript TroubleShooting](#javascript-troubleshooting)
  * [Generally TroubleShooting](#generally-troubleshooting)  
  * [Ubuntu debian or widnows version](#ubuntu-debian-or-widnows-version)
  * [CommandClick repository](#commandclick-repository)



App installation
-----  
- Android 8+

<a href="https://apt.izzysoft.de/fdroid/index/apk/com.puutaro.commandclick/" target="_blank"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" width="170"></a>　　

[![Release: 0.0.40](https://img.shields.io/badge/Release-0.0.40-brightgreen.svg)]((https://github.com/puutaro/CommandClick/releases)) 


Usage
-----

### Index mode
This mode is main mode. Top is `web terminal view`, down is `script name list`, bottom is toolbar.
Main usage is executoin script by net surfing and list script clicking, other usage is maintenance script or app by longpress or toolbar.

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)


#### History

This feature is basic and great feature in `Command Click`. This always allow you to select current directory and mode which used, as if you look in Android's backstack feature's history.
Torigger by left bottom history button clicked.
And more you look in url history by long press where you visited url (Afterward noting, switchable url history with history, or url history with button script exec)
* history item display mechanism {current_app_dir}__({current_script}) (when exist current script, edit execute is `Always`)

#### Change term size

Click toolbar right setting button, and terminal size change.


#### Add

This feature display when toolbar right setting button long pressed. Then, click `add`, so new script adding.
At the same time, if you installed code editor, edit new file.

 -  various settingVriables feature in `CommandClick`'s script

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
    | `setReplaceVariables`  | `string` | string replaced with certain string. You also have multiple specifing this. ex) setReplaceVariables="{replaceVariablle1}={repalce string1} or setReplaceVariables="file://{file path}"  
    | `setVariableTypes` | `string`  | when edit, whether to set variable type to commandVariable. You also have multiple specifing this. In detail, follow bellow. |
    | `hideSettingVariables` | `string` | specified setting varialle you want to hidden when edit(hidden variable in default) ${setting variable name}="..." or hideSettingVariables="file://{file path} |
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

 
  - `setVariableType` option table  
    | option| description | example  |
    | --------- | --------- | ------------ |
    | `LBL` | text label | {variableName}:LBL=${label name}   |
    | `TXT` | edit text enphasys | {variableName}:TXT=   |
    | `CB` | c(s)elect box | {variableName}:CB=value1!value2!|..   |
    | `ECB` | editable c(s)elect box | {variableName}:ECB=value1!value2!|..   |
    | `LSB` | list contents select box | {variableName}:LSB=listPath={target list file path}(!limitNum={list limit num}) |..   |
    | `ELSB` | editable list contents select box | {variableName}:ELSB=listPath={target list file path}(!limitNum={list limit num}) |..   |
    | `LMSB` | editable list contents multi select box | {variableName}:LMSB=listPath={target list file path} |..   |
    | `FSB` | file select box | {variableName}:FSB=dirPath={target direcotry path(default: current directory)}(&prefix={grep prefix})(&suffix={grep suffix})(&type={`file` or `dir` (default value is `file`}) |..   |
    | `GB` | grid select box | {variableName}:GB=listPath={target list file path}(!limitNum={list limit num}) |..   |
    | `IGB` | only image grid select box | {variableName}:IGB=listPath={target list file path}(!limitNum={list limit num}) |..   |
    | `MGB` | multi grid select box | {variableName}:MGB=listPath={target list file path}(!limitNum={list limit num}) |..   |
    | `FGB` | file select grid box | {variableName}:FGB=dirPath={target direcotry path(default: current directory)}(&prefix={grep prefix})(&suffix={grep suffix})(&type={`file` or `dir` (default value is `file`}) |..   |
    | `IFGB` | file select only image grid box | {variableName}:IFGB=dirPath={target direcotry path(default: current directory)}(&prefix={grep prefix})(&suffix={grep suffix})(&type={`file` or `dir` (default value is `file`}) |..   |
    | `MFGB` | multi file select grid box | {variableName}:MFGB=dirPath={target direcotry path(default: current directory)}(&prefix={grep prefix})(&suffix={grep suffix})(&type={`file` or `dir` (default value is `file`}) |..   |
    | `MSB` | multi select box | {variableName}:MSB={variableName}:MSB=listPath={target list file path}(!limitNum={list limit num}) |..   |
    | `FL` | file select button | {variableName}:FL=  |
    | `DIR`  | directory select button | {variableName}:DIR= |
    | `NUM` | increment or decrement number | {variableName}:NUM={init_value}!{min}..{max}!{step}(!{number of decimal places}) |
    | `BTN` | botton  | {variableName}:BTN=cmd={command string}(!label={button label})    |
    | `RO` | read only | {variableName}:RO= |
    | `H` | password input | {variableName}:H={password ..etc}   |
    | `HL` | label hidden | {variableName}:HL=   |
    | `DT`  | get date button | {variableName}:DT=  |
    | `TM`  | get time button | {variableName}:TM=  |
    | `CLR` | select color button  | {variableName}:CLR= |
    | `LI` | edit list component | {variableName}:LI=listDir={target list dir path}&#124;menu={menuName1}(&subMenuName1&subMenuName2..}!{menuName2}(&subMenuName21&subMenuName22..}(&#124;prefix={grep prefix})(&#124;suffix={grep suffix}) |..   |

- In `FSB`, `FGB`, `MFGB`,`IFGB`,`FGB`, {grep suffix} have `NoExtend` macro, It display no extend file list


`setReplaceVariables` usage  

- This option is specified global enviroment variable  
- This option is specified multiply  


ex)

```js.js
/// SETTING_SECTION_START
setReplaceVariables="settingVariables=editSettingVariables"
setReplaceVariables="currentAppDirPath=${01}"
setReplaceVariables="currentFannelDirPath=${currentAppDirPath}/${001}"
setVariableTypes:GB="file://${01}/${001}/setVariableTypes.js"
/// SETTING_SECTION_END


const currentFannelDirPath = "${currentFannelDirPath}"
.
.
.
```


**[Recommend]**   
`setReplaceVariables` can specify file path like bellow.   
But, setReplaceVariable cannot use in file path.  
(bellow ${01} and ${001} is pre reserved word in `CommandClick`)
  
```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
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


`setVariableType` Option usage  

- This setting is applied to command variable
- This setting can be specify multiply.
- `setReplaceVariables` can apply to this.

ex)

```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
setVariableTypes="txtPdfPath:GB=listPath=${currentFannelDirPath}/listfile!limitNum=10"
TTS_PLAY:BTN=cmd="jsf '{0}' play"
.
.
.
/// SETTING_SECTION_END


/// CMD_VARIABLE_SECTION_START
txtPdfPath=""
TTS_PLAY=""
.
.
.
/// CMD_VARIABLE_SECTION_END


- This option can be combined

ex1)  

```js.js
setVariableTypes="${variable name1}:LBL:TXT:LSB:BTN=label=test label|listPath=${list file path}|cmd=jsf '${0}' exmple!label=button label"
```

- `LBL` is recommended to specify at the beginning  
- `TXT` define edit text space. Without this, edit test no display.  
- Left member must be defined by ritht options order(no value option is skip)  
- Left member is sepalated by virtical var.    
- No value option's left menber is blank in `setVariableType` option table.  
- `LSB`, `ELSB`, `MSB`, and `GB` is same in how to specify.     
   
ex2)  

```js.js
setVariableTypes="${variable name2}:HL:BTN=label=cmd=jsf '${0}' exmple!label=button label2"
```

- Left member must be defined by ritht options order(no value option is skip)  

ex3)  

```js.js
/// SETTING_SECTION_START
setVariableTypes="${variable name3}:TXT:NUM:BTN=label=0!1..1000!1|cmd=jsf '${0}' exmple!label=button label3"
/// SETTING_SECTION_END
```

- `NUM` is recommended to combline `TXT` option becuase of visualizing current number

**[Recommend]**   
`setVariableType` can specify file path like bellow. 
  
```js.js
/// SETTING_SECTION_START
setVariableType="file://${01}/${001}/setVariableType.js"
/// SETTING_SECTION_END
```

setVariableType.js

```setVariableTypes.js
${variable name1}:LBL:TXT:LSB:BTN=label=test label|listPath=${list file path}|cmd=jsf '${0}' exmple!label=button label,
${variable name2}:HL:BTN=label=cmd=jsf '${0}' exmple!label=button label2,
```

- How to write about `setVariableTypes.js` is above same.  But, must be comma in variable definition end. Instead, you can use indent, newline, and comment out by `//` or `#`

```setVariableTypes.js

// variable name1 description
${variable name1}:  
	      LBL:TXT:LSB:BTN=  
			     label=test label  
			     |  
				       listPath=${list file path}  
			     |  
				       cmd=jsf '${0}' exmple  
						!label=button label,
# variable name2 description
${variable name2}:  
              HL:BTN=label=  
			     cmd=jsf '${0}' exmple  
						!label=button label2,

```

`hideSettingVariables` usage

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


**[Recommend]**   


`hideSettingVariables` can specify file path like bellow.   
But, setReplaceVariable cannot use in file path.  
(bellow ${01} and ${001} is pre reserved word in `CommandClick`)
  
```js.js
/// SETTING_SECTION_START
setReplaceVariables="file://${01}/${001}/setReplaceVariables.js"
setVariableTypes="file://${01}/${001}setVariableTypes.js"
hideSettingVariables="file://${01}/${001}/hideSettingVariables.js"
/// SETTING_SECTION_END
```

setReplaceVariables.js

```setReplaceVariables.js
// setReplace variables comment
setReplaceVariables,
"setVariableTypes",
```

ignoreHistoryPaths usage

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


**[Recommend]**   


`ignoreHistoryPaths` can specify file path like bellow.   
But, setReplaceVariable cannot use in file path.  
(bellow ${01} and ${001} is pre reserved word in `CommandClick`)
  
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

- button option usage

	ex)
	
	```js.js
	jsf '${0}' 
	```
 	- `jsf` execute javascript file path
    
	
	ex)
	 
	```js.js
	 jsf '${01}/${02}'
	```
	
	- `jsf` execute javascript parrent directory path, `${01}` is parent dir (`${02}` is current script name)
  	- [ref pre order word](#javascript-pre-order-word)  
   
	ex)
	
	```js.js
	 ::NoJsTermOut:: jsf '${0}'
	```
	
	- `::NoJsTermOut::` disable terminal output when only javascript
	
	ex)
	 
	```js.js
	 ::TermLong:: jsf '${0}'
	```
	
	 - `::TermLong::` terminal size to long
	
	ex)
	 
	```sh.sh
	echo ${0}
	```
	
	- `${0}` is current script path
	  
	ex)
	
	```sh.sh
	 ::BackStack:: ls    
	```
	
	- `::BackStack::` is backstack, only work when prefix when only shellscript
	
	ex)
	
	```sh.sh
	 ::TermOut:: ls
	```
	
	- `::TermOut::` enable terminal output
	
	ex)
	
	```sh.sh
	 top -n 1 > /dev/null  
	 - when suffix is `> /dev/null` or `> /dev/null 2>&1`, no output)
	```

- list index component usage
	- this component must be one and other component is exclude by this.  
	- `listDir` and `click` dir is made in right under the fannel directory   
	- Files in listDir is list item  
	- Under `click` dir,there are `itemClick.js`, `menuClick.js`, and `subMenuClick.js`  
	- `itemClick.js` is trigger when item click  
	- `menuClick.js` is trigger when menu click  
	- `subMenuClick.js` is trigger when subMenu click  
	- `itemClick.js`, `menuClick.js`, and `subMenuClick.js` have bellow argument:
   
```js.js 
	     let args = jsArgs.get().split("\t");   
	     var PARENT_DIR = args.at(0);  
	     var LIST_VIEW_DIR = args.at(1);  
	     var ITEM_NAME = args.at(2);  
	     var MENU_NAME = args.at(3);
```

- `itemclick.js` option  
`const override = true;` -> when click, your handling when js and shell don't execute.   

- predfine menu name in list index component  
      			`sync` -> sync list to current directory files   
			`delete` -> delete item   
			`add` -> add item   
			`write` -> edit item by editor     
			`cat` -> show time file contents   
			`desc` -> show description   
			`copy_path` -> copy item file path   
			`copy_file` -> copy item file to other directory  
			`editC` -> edit command variables  
			`editS` -> edit setting variables  
			


#### Run

Run script by click list item in index mode or play button edit mode (editExecute variable is `Always`)
Or run javascript file.

#### Edit

Edit script by form when long click list item in index mode 


#### Write

Edit script by editor when long click list item in index mode 

#### Delete

Delete script by `utility` -> `delete`  when long click list item in index mode   
At the same time, remove `fannel` direcotry (raw filename + `Dir`)  


#### Kill

(Shell only) Kill shellscript proccess by `utility` -> `kill`  when long click list item in index mode 


#### Description

Display description for script by `utility` -> `description`  when long click list item in index mode


#### Copy file

Copy file for script by `copy` -> `copy file`  when long click list item in index mode  
At the same time, copy `fannel` direcotry (raw filename + `Dir`)  

#### Copy file path

Copy file path for script by `copy` -> `copy file path`  when long click list item in index mode

#### Add

Add new script by `add`   when long click setting button(toolbar right) in index mode


#### Change app dir


Start `App directory` Manager by `setting` -> `change_app_dir` when long click setting button(toolbar right) in index mode
`App directory` is directory which index mode retreive
- when item long press, poupu `add`, `delete` and `edit` menu 
    - `add`: add `App directory` 
    - `delete`: delete `App directory`
    - `edit`: edit `App directory` name
   


#### Create shortcut
 
You can create shortcut for current `App directory` or `script` in only `index mode` or `edit execute Always`

#### Install fannel

1. long press right bottom setting button  
2. click `install_fannel`   
3. You can install `fannel` by clicking.   

When you wont to sync [git repository](https://github.com/puutaro/commandclick-repository), `sync` by long press and "sync ok" and wait until "sync ok" toast.   
	- `fannel` [detail](https://github.com/puutaro/commandclick-repository#desire)  
	  
![image](https://user-images.githubusercontent.com/55217593/226502372-ab8ad71e-bddb-4b72-8e9e-9bda2e9aff09.png)   


### Import library  

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!  

#### Local path import

```js.js
ccimport {path}   
```

* current directory -> `./`  
* move parent direcoty -> ../  
* other check [Javascript pre order word](#javascript-pre-order-word)   

#### Assets import

```js.js
ccimport /android_asset/{relative path}  
```

#### WEB import

```js.js
ccimport {URL}  
```

* It is possible to download by curl {URL}



#### Config

You can setting `CommandClick` Configration
- detail setting reference [Add](#add)


#### Termux Setting

Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
You can set by onley this menu press.

* Below is a supplement.

1. long press right bottom setting button  
2. `setting` -> `termux setup`   
3. Long press on termux
4. Click paste popup on termux  
5. Continue pressing `Enter` on termux
- clipboard contents:
   ```sh.sh
   pkg update -y && pkg upgrade -y \
   && yes | termux-setup-storage \
   && sed -r 's/^\#\s(allow-external-apps.*)/\1/' -i "$HOME/.termux/termux.properties" 
   ```

   - reference
      - Enable `allow-external-apps` [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#allow-external-apps-property-mandatory)
      - Add Storage permission. [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#storage-permission-optional)
      - Execute `termux-setup-storage` on termux

4. Set strage access again in `android 11` (Optional)

> You may get "Permission denied" error when trying to access shared storage, even though the permission has been granted.
>  
> Workaround:
>
> Go to Android Settings --> Applications --> Termux --> Permissions
> Revoke Storage permission
> Grant Storage permission again

[detail](https://wiki.termux.com/wiki/Termux-setup-storage)

5. Set `Draw Over Apps permission` in `android 11+` (Optinal)

> You can grant Termux the Draw Over Apps permission from its App Info activity:
> `Android Settings` -> `Apps` -> `Termux` -> `Advanced` -> `Draw over other apps`.

[detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent/06f1de1b262d7612497e76463d8cc34ba7f49832#draw-over-apps-permission-optional)

- When above method cannot settle down, `CommandClick` or `Termux` restart, and system reboot.



#### No scroll save url

Register scroll y position ignore domain 

#### Term reflesh

You can reflesh `web terminal view`.

#### Forward

You can forward `web terminal view` history.

#### Search mode

You can search `web terminal view` by toolbar search item.

##### Terminal filter

It's default setting in terminal short size. If you type string, realtime filter start.

##### Terminal search

When terminal mark or web mark long press, you can search typing word.

##### Launch web menu

This is shortcut menu with internet button.
When you look website, press internet button no highlight, launch menu by inernet button script


#### Highlight search

This is speed search mode.  
When you look website, highlight text and click internet button..



### Auto exec script

`Command Click` have auto exec script. This is used when `index mode` startup or end.

#### Startup script
1. This script is automaticaly executed when `index mode` startup.
But, in default, `onAutoExec` in setting variable is `OFF` so, if you enable this, you must be `ON` (reference to [add](#add)).

2. Override `config setting variable`, if you are change default value with your set value.



### Internet Button exec script

This script is executed, click when internet buton is grey globle mark and long terminal mode is active.


### Button exec script

This script is executed when history buton click or long click, if you select  `urlHistoryOrButtonExec` to `BUTTON_EXEC` in setting variable.
Also whether click or long click torigger, due to `historySwitch` setting  (reference to [add](#add)).


    
  
### Edit execute once

One time edit and execute

![image](https://user-images.githubusercontent.com/55217593/216524059-97c35357-c0de-48c1-953f-b1e1478cf296.png)


### Edit execute always

![image](https://user-images.githubusercontent.com/55217593/216652110-4bc01a73-2b8b-42f2-8253-49062e775b66.png)

Always edit and execute. So called `Script2GUI`. It's great feature. 
How the script file turns into a GUI Application! 


### Url command

Exec bellow command in `CommandClick` shellscript, so that you can launch web site.
(This command is only active when command click focus)

```sh.sh
am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "{url}"
```

```sh.sh
ex) am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "https://github.com/puutaro/CommandClick/edit/master/README.md"
```

### Html automaticaly creation command to edit target edit file 

Exec bellow command in `CommandClick` shellscript, so that you can make automaticaly make html, css and javascript.
(This command is only active when command click focus)

```sh.sh
am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "{target edit file path}" \
		--es src_path "{source file path}" \
		--es on_click_sort "boolean(sortable when link click)" \
		--es on_sortable_js "boolean(sortable link list)" \
		--es on_click_url "boolean(launch url when link click)" \
		--es filter_code "{javascript filter code}"
``` 

  - `edit_path` is file path edit by html, also file name is title  
	- ex)  Target edit file is tsv, which composed two row.
                urltitle1  urlString1
                urltitle2  urlString2
                urltitle3  urlString3
                .
                .
                .  
		  
  - (Optional) `src_path` is source file path for input text string, Ordinaly, first hit one line's title display default string, and hold first hit line's url  
	- ex)  Source file is tsv, which composed two row like above.  
  - (Optional) `on_click_sort` is how to sort top when link click.  
  - (Optional) `filter_code` filter target source file  by javascript code. default value is `true`. You can use `urlString` and `urlTitle` variable to filter.  
  
```sh.sh
ex) am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "${PARENT_DIR_PATH}/tubePlayList" \
		--es src_path "${PARENT_DIR_PATH}/cmdclickUrlHistory" \
		--es on_click_sort "false" \
		--es on_sortable_js "true" \
		--es on_click_url "true" \
		--es filter_code "urlString.startsWith('http') && urlString.includes(\"youtube\");"
```

- edit html esxample

![image](https://user-images.githubusercontent.com/55217593/222952726-f5ce0753-f299-44cd-a9b0-a021c56d3b4c.png)



### File api
`CommandClick` automaticaly create files in `App directory`/`system`/`url`. This is used by system, alse is userinterface for app signal.
- `cmdclickUrlHistory` 
      - CommandClick use recent used url launch etc.
- `urlLoadFinished`
      - This is made when url load finished. When you make `fannenl`(javascript, shell, and html application), you may use this.

### JavaScript interface
`CommandClick` is javascript framework for andorid. Particularly, this methods strongly support your android app development(`fannel` development).  
This, so colled, android app row code library.

```js.js

 - jsFileStystem

 	- jsFileStystem.showFileList(
		dirPath: String
          )
		-> return filelist tab sepalated
 
	- jsFileStystem.showDirList(
		dirPath: String
	  )
		-> return filelist tab sepalated
 
 	- jsFileStystem.readLocalFile(
		path: String
	   )
		->  read local file and return file contents string

	- jsFileStystem.writeLocalFile(
		path: String, contents: String
	  )
		- write local file

	- jsFileStystem.jsFile(
		filename: String,
		terminalOutPutOption: String
	  )
		- write local monitor file  
	- jsFileStystem.removeFile(
		path: String
          )
		- remove local file
 
	- jsFileStystem.createDir(
		path: String
	  )
		- creaate local dirctory

	- jsFileStystem.removeDir(
		path: String
	)
		- remove local direcotry

	- jsFileStystem.copyDir(
		sourcePath: String,
		destiDirPath: String
	  )
		- copy local directory

	- jsFileSystem.outputSwitch(
		switch: String
	)
		- switch == on, then enable terminal output.
                        other default.
                        (althogh being webmode, terminal mode off, this inmterface switch on)
 
	- jsFileSystem.isFile(
		filePath: String
	   )
		-> boolean

	- jsFileSystem.isDir(
		DirectoryPath: String
	   )
		-> boolean

	- jsFileSystem.removeAndCreateDir(
	        dirPath: String
	  )
		-> remove and create directory


 - JsArgs

	- jsArgs.get()
		-> tabsepalete string  
			jsArgs soruce is jsf argument in edit  
			ex) setVariableType="jsf $0 fristargment 'secondargument 2'" 
				-> `fristargment`\t`secondargument 2`  

	- jsArgs.set(
		tabsepalete string
	    )
		-> argment set (ex "{arg1}\t{arg2}\t..")  


 - JsIntent

 	- jsIntent.launchEditSite(
		editPath: String,
		srcPath: String,
		onClickSort: String(true/false),
		onSortableJs: String(true/false),
		onClickUrl: String(true/false),
		filterCode: String,
		onDialog: String(true/false)
	  )
		- ref: [html automaticaly creation command to edit target edit file]

 	- jsIntent.launchUrl(
		urlString: String
          )
		-> launch uri(not url but uri)

	- jsIntent.launchApp(
		action: String,
		uriString: String,
		extraString: tabSepalatedString,
		extraInt: tabSepalatedString,
		extraLong: tabSepalatedString,
		extraFloat: tabSepalatedString
	   )
		- launch app site

		ex) bellow, launch google calendar  
			jsIntent.launchApp(
				"android.intent.action.INSERT",
				"content://com.android.calendar/events",
				"title=title\tdescription=description\teventLocation=eventLocation\tandroid.intent.extra.EMAIL=email",
				"",
				beginTime=167889547868058\tendTime=165678973498789",
				""
			);

	- jsIntent.launchShortcut(
		currentAppDirPath: String,
		currentShellFileName: String
	    )
		- launch index and fannel  

	- jsIntent.shareImage(
		shareImageFilePath: String
	  )
		- share image intent


 - JsDialog

 	- jsDialog.listJsDialog(
		listSource: String(tab sepalate)
	   )
		-> selected list

 	- jsDialog.formJsDialog(
		formSettingVariables: String(tab sepalate),
		formCommandVariables: String(tab sepalate)
	  )
 		 -> formSettingVariables tabsepalete string  return {key}={value} contents
 		 - setting reference [Add](#add)
 		 - ex) 
 				jsDialog.formJsDialog(
					"efcb:EFCB=tube\tnumber:NUM=2!1..100!1\tpassword:H=\ttxt:TXT=\tcb:CB=aa!bb\tcb2:CB=gg!tt\tcb3:ECB=gg!tt",  
					`efcb=\tefcb=tubeCrara\tnumber=\tpassword=\ttxt=cb2=tt\tdb3=gg`  
				)      
 			        -> efcb:EFCB=tubelist\nnumber:NUM=99\npassword:H=1234\ntxt:TXT=yrcy\ncb=aa\ncb2=tt\ncb3=tt

	- jsDialog.getFormValue(
		targetVariableName: String,
        	contentsTabSepalateFormJsReturnValue: String
	  )
		-> target variable value
				
				
	- jsDialog.multiListDialog(
		title: String,  
		currentItemListStr: String(tab sepalate),  
		preSelectedItemListStr: String(tab sepalate),  
	    )
		-> tab sepalated items
 		 	- ex) 
 				jsDialog.multiListDialog(
					"{item1}\t{item2}",  
					`{item1}\t{item2}\t{item3}\t{item4}`  
				)      
 			        -> {item1}\t{item2}\t{item4}

	- jsDialog.gridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected image path

	- jsDialog.onlyImageGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected image path

	- jsDialog.onlySpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected spannable image path

	- jsDialog.multiSelectGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected spannable image paths

	- jsDialog.onlySpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only spannable image grid view without file name search)
		-> selected spannable image path

	- jsDialog.multiSelectOnlyImageGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected image paths				

	- jsDialog.multiSelectSpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected spannable image paths

	- jsDialog.asciiArtDialog(
		title: String,
        	imagePath: String
	  )
		-> display ascii art with share button

	- jsDialog.imageDialog(
		title: String,
        	imagePath: String
	  )
		-> display image with share button

	- jsDialog.webview(
		urlStr: String
	  )
		- launch webview dialog with url 

				
 - JsStop

 	- jsStop.how()
		-> Boolean
		(measure for `while roop` crush when application focus out)


 - JsToast

 	- jsToast.short(
		contents: string
	  )
		- short toast

	- jsToast.long(
		contents: string
	  )
		- long toast


 - JsCurl

 	- jsCurl.get(
		mainUrl: string,
		queryParameter: String,
		header: String(ex Authorication\tbear token,contentType\ttext/plain..),
		Timeout: Int (miliSeconds)
	  )
		-> get response

	- jsCurl.getTextOrPdf(
		url: text or pdf url
	   )
		-> download text or pdf file image to bellow `/storage/emulated/0/Document/cmdclick/temp/download`

	- jsCurl.getImage(
	        url: String
	    )
		download image to bellow `/storage/emulated/0/Document/cmdclick/temp/download`


 - JsUtil

 	- jsUtil.sleep(
		sleepMiriTime: Int
	  )
		- sleep miri seconds

	- jsUtil.copyToClipboard(
		copyString: String,
		fontSize: Int
	  )
		- copy to clipboard

	- jsUtil.echoFromClipboard()
		-> primary clipboard string

	- jsUtil.convertDateTimeToMiliTime(
		datetime: String(YYYY-MM-DDThh:mm)
	   )
		-> militime


 - JsUrl

 	- jsUrl.makeJsUrl(
		jsPath: String
	  )
		-> javascript:(
			function() { ${jsPathCoontents} }
	  	   )();

	- jsUrl.loadUrl(
		urlString: String
          )
		-> load url by webview  


 - JsScript

 	- jsScript.subLabelingVars(
		jsContents: String
	  )
		-> Labeling Section Contents

	- jsScript.subSettingVars(
		jsContents: String
	  )
		-> Setting Section Contents

	- jsScript.subCmdVars(
		jsContents: String
	  )
		-> Comamnd Section Contents

	- jsScript.subValOnlyValue(
		targetVariableName: String,
		VariableValueStringContents: String
	  )
		->  Variable value String Contents

	- jsScript.bothQuoteTrim(
		VariableValueString: String
	  ) -> VariableValueString removed both edge quote  
	- jsScript.replaceSettingVariable(
		scriptContents: String,
		replaceTabList: String
	  )
		-> File contents String

	- jsScript.replaceVariableInHolder(
		scriptContents: String,
		replaceTabList: String
	  )
		-> File contents String  


 - JsListSelect
	This interface exist for `LSB`, `ELSB`, `GB` and `MSB` `setVariableTypes` option (ref [Add]

 	update or remove method for editable list file checkbox 
 	- jsListSelect.updateListFileCon(
		targetListFilePath: String,
		itemText: String
	  )
		- update `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 

	- jsListSelect.removeItemInListFileCon(
		targetListFilePath: String,
		itemText: String
	  )
		- remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 

	- jsListSelect.wrapRemoveItemInListFileCon(
                targetListFilePath: String,  
                removeTargetItem: String,  
                currentScriptPath: String,  
                replaceTargetVariable: String = String(),  
                defaultVariable: String = String()  
          )
		- remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` and update View


 - JsFileSelect
 	This interface exist for `FCB`, `FSB` setVariableTypes` option (ref [Add]
 
	- execEditTargetFileName(  
        	targetVariable: rename target command variable string,  
        	renameVariable: rename destination command variable String,  
        	targetDirPath: file select direcoty path,  
        	settingVariables: setting variable with tab sepalator,   
        	commandVariables: command variable with tab sepalator, 
        	prefix: file select direcotry grep prefix string,  
		suffix: file select direcotry grep suffix string,  
        	scriptFilePath: fannel path string  
    	)
		- edit targetVariable value(file name) and update view by form dialog

 - JsDirSelect
 	This interface exist for `FCB`, `FSB` setVariableTypes` option (ref [Add]
 
	- execEditTargetFileName(  
        	targetVariable: rename target command variable string,  
        	renameVariable: rename destination command variable String,  
        	targetDirPath: file select direcoty path,  
        	settingVariables: setting variable with tab sepalator,   
        	commandVariables: command variable with tab sepalator, 
        	scriptFilePath: fannel path string,
		title: title string
    	)
		- edit targetVariable value(directory name) and update view by form dialog


 - JsEdit  
 	`edit component` edit tool

	- jsEdit.getFromEditText(
		targetVariableName: String,
	    )
		-> get target variable value stirng  from view
	    
 	- jsEdit.updateEditText(
		updateVariableName: String,
		updateVariableValue: String
	  )
		- update `updateVariableName` view value
	
	- jsEdit.updateSpinner(
		updateVariableName: String,
		variableValue: String
	  )
		- update `updateVariableName` spinner view selected value

	- jsEdit.updateByVariable(
		fannelScriptPath: String,
		targetVariableName: String,
		updateVariableValue: String,
	    ) 
     		-> update target variable  value

	- jsEdit.removeFromEditHtml(
		editPath: String(edit site source path),
		removeUri: String(remove uri)
	)
		-> remoev uri from edit site source  


 - JsCsv
 	csv edit tool

	- jsCsv.read(
		tag: String,
		csvPath: String,
		withNoHeader: String,
		csvOrTsv: String,
		limitRowNumSource: Int
	  )
		- save csv or tsv instance with tag, also header   
	 
	- jsCsv.readM(
		tag: String,
		csvString: String,
		csvOrTsv: String,
	 )
		- save csv or tsv instance with tag  
	 
 	- jsCsv.takeRowSize(
		tag: String
   	  )
		-> rowSize about csv(tsv) with tag

	- jsCsv.takeColSize(
		tag: String
	  )
		-> colSize about csv(tsv) with tag

	- jsCsv.isRead(
		tag: String
	   ) 
		(comfirm read completed  about csv(tsv) with tag)
		-> blank or String  
	
	- jsCsv.toHeader(  
        	tag: String,  
        	colNum: Int,  
    	)
		-> schema name  
	
	- jsCsv.toHeaderRow(
		tag: String,
		startColNumSource: Int,
		endColNumSource: Int,
	)
		-> headerList sepalated by tab   
	
	- jsCsv.toRow(
		tag: String,
		rowNum: Int,
		startColNumSource: Int,
		endColNumSource: Int,
	    )
		-> rowList sepalated by tab    
	
	- jsCsv.toCol(
		tag: String,
		colNum: Int,
		startRowNumSource: Int,
		endRowNumSource: Int,
	    )
		-> colList sepalated by tab    
	
	- jsCsv.toHtml(
		tsvString: String,
		onTh: String (empty -> ordinaly `td tag` html, some string -> `th tag` html)
	  )  
		convert tsv to html string  
		-> html string   
	
	- jsCsv.outPutTsvForDRow(
		tab: String
	   ) 
		convert row direction tsv to Tsv  
		-> tsv string

	- jsCsv.outPutTsvForDCol(
		tab: String
	  ) 
		convert col direction tsv to Tsv  
		-> tsv string

	- jsCsv.filter(
		srcTag: String,
		destTag: String,
		tabSepaFormura: String ({schema1},>,1500\t{schema2},in,Monday,\t{schema3},=,super man\t..)  
	    )
		-> save filterd tsv instance with tag, also header
 
	- jsCsv.selectColumn(
		srcTag: String,
		destTag: String,
		comaSepaColumns: String ({column1}\t{column2}\t{column3}\t..)  
	    )
		-> save culumn selected tsv instance with tag, also header
 
	- jsCsv.sliceHeader(
		tag: String,
		startColNumSource: Int,
		endColNumSource: Int,
		headerRow: String,
	    )
		-> header string sliced with tab delimiter   
	    
	    
- JsText

	- jsText.trans(
		tsvString
	   )
		-> String transposed row and col  


 - JsPath  
 	path edit tool

	- jsPath.compPrefix(  
		path: String,  
		prefix: String,  
	  )
		-> complete prefix     
	 
	- jsPath.compExtend(  
		path: String,  
		extend: String  
	    )
		-> complete suffix    
	 
 	- jsPath.checkExtend(  
 	 	tag: String,  
		extendTabSeparateStr: tab separated String  
	  )
		-> boolean (true when including tab separated extend String)

	- jsPath.checkPrefix(
		name: String,  
		prefixTabSeparateStr: String  
	    )
		-> boolean (true when including tab separated prefix String)
 
	- jsPath.removeExtend(  
	 	path: String,  
	 	extend: String  
	)
		-> remove extend 
	
	- jsPath.removePrefix(  
		path: String,  
		prefix: String  
	    )
		-> remove prefix      


- JsTextToSpeech

	- jsTextToSpeech.speech(  
		playListFilePath: String,    
		playMode: String(ordinaly|shuffle|reverse|number),  
		onRoop: String(empty or notEmply(roop on)),  
		playNumber: String (int string(valid in number mode),  
		toLang: text to speech language prefix string: en(english), zw(chinese), sp(spanish), ko(korean), ja(japanese))    
		onTrack: String(empty or notEmply(on Track)),    
		speed: String(int string)    
		pitch: String(int string)  
	)
		- execute text to speech 
	  
	- jsTextToSpeech.stop()

  
 - JsPdf
	- jsPath.extractText(  
  		path: pdf path string  
  	   )
		-> extracted text


```


### Javascript pre order word
- `${0}` -> current file path  
- `${00}` -> cmdcclik root dirctory path macro  
- `${01}` -> parent directory path  
- `${001}` -> `fannel` direcotry name (ex `cmdYoutuber` -> `cmdYoutubeDir`)  
- `${02}` -> current script name  


### Include Javascript Library  

First, I respect bellow javascript package author.  
Bellow respectable package is inclided assets. you can import like bellow.

- Sortable.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/Sortable.js"></script>`  
- jquery-ui -> Add html with `<script type="text/javascript" src="file:///android_asset/js/jquery-ui.min.js"></script>`  
- jquery-3.6.3.min.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/jquery-3.6.3.min.js"></script>`  
- long-press-event.min.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/long-press-event.min.js"></script>`  
- chart.min.js -> Add html with `<script src="file:///android_asset/js/chart.min.js" ></script>`  
- chartjs-adapter-date-fns.bundle.min.js -> Add html with `<script src="file:///android_asset/js/chartjs-adapter-date-fns.bundle.min.js"></script>`  


### Include css Library  

First, I respect bellow css package author.  
Bellow respectable package is inclided assets. you can import like bellow.

- jquery-ui.css -> Add html with `<link rel="stylesheet" href="file:///android_asset/css/jquery-ui.css">`  


### Html tag output

`CommandClick` script output trminal as html, so html tag is valid. You can use tag by bellow.
 - `<` -> `cmdclickLeastTag`
 - `>` -> `cmdclickGreatTag`

   - `Span tag` no working in script output. If you wont to use this, launch html file.
   - Url string automaticaly change anchor tag, but if you put 'href="' prefix in front of this string, no auto change.


### Javascript TroubleShooting  


- When your javascript's file cannot execute, you confirm how script step semicolon(`;`) exist except for function argument.  
	- Becuase javaxcript file convert one linear script string, as it, javascript:(function() { `${js contents}` })(); and webvoew.loadUrl().  

- Javascript's `while roop` ocationaly cuase crush. add bellow code to the roop.  

```js.js
	if(
		jsStop.how().includes("true")
	) throw new Error('exit');
```  


- Optinaly may replace delay function with `jsUtil.sleep($milisecond);`
	- The `Roop crush` is occur by memory leak.


### Generally TroubleShooting  

- When url load slow in different than before, probably due to cache, so click it's url from `url history`.
    - In many cases, occur in google search result page.


- Ocationaly first start proccess crush, try, don't worry, just reboot.  
    - Becuase app resoruce prefetch is busy, it's occur. Therefore, it' s instant problem. Rarely happens after the second time.

- When `websearch suggest` is not working, press space. if suggest  exist, fly.  
    - Prabably due to `AutovompleteTextView`'s specification.  　　
    
- When frequently crush, your smartphone reboot, or `onAdBlock` setting variable set `OFF`  
    - Becuase of low memory, as a side note when `onAdBlock` is `ON`, `CommandClick` make block list on activiry reboot. Therefore 
Crashes easily when memory is low    

- When `fannel` suddenly not working, update latest `CommandClick` and `fannel`.
    - `CommandClick` and `fannel` is new baby. Therefre, these is frequetly updated without backwards compatible.　I continuly enforce usability and fanctionality. Before long, if these grow up adult, I weight stability. But, please feel at ease, most often even now, latest version works fine.  



### Ubuntu debian or widnows version

Reference to [url](https://github.com/puutaro/cmdclick)


### Commandclick-repository

CommandClick's fannel repository

`fannel` is ComamndClick using script (javascript, and shellscript)
For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application `fannel`
  
[link](https://github.com/puutaro/commandclick-repository)  


