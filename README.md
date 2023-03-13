![cmdclick_image](https://user-images.githubusercontent.com/55217593/199425521-3f088fcc-93b0-4a84-a9fd-c75418f40654.png)
# Command Click
So called `Script browser`, It's a enforced browser by javascript and shellscript. Also, it is termux gui client .

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)


Pros
----
- Javascript engine.
- Javascript enforce web brawser
- Easily turn shellscript into a GUI application in android.
- Versatile usage for Terminal, Chrome, OS setting, etc.
- Not only web browser but also termux gui client.
- Offer ritch edit dialog to termux.


Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Pre Setting(Mandatory)](#pre-setting)
* [Usage](#usage)
  * [History](#history)
  * [Change term size](#change-term-size)
  * [Add](#add)
  * [Run](#run)
  * [Edit](#edit)
  * [Write](#write)
  * [Kill](#kill)
  * [Init](#init)
  * [Description](#description)
  * [Copy file](#copy-file)
  * [Copy file path](#copy-file-path)
  * [Add](#add)
  * [Change app dir](#change-app-dir)
  * [Create shortcut](#create-shortcut)
  * [Install](#install)
  * [Config](#config)
  * [Termux setting](#termux-setting)
  * [Select term](#select-term)
  * [Term reflesh](#term-reflesh)
  * [Forward](#forward)
  * [Search mode](#search-mode)
    * [Terminal filter](#terminal-filter)
    * [Terminal search](#terminal-search)
    * [Web search](#web-search)
  * [Auto exec script](#auto-exec-script)
    * [Startup script](#startup-script)
    * [end script](#end-script)
  * [Internet Button exec script](#internet-button-exec-script)
  * [Button exec script](#button-exec-script)
  * [Edit execute once](#edit-execute-once)
  * [Edit execute always](#edit-execute-always)
  * [Edit api](#edit-api)
  * [Url command](#url-command)
  * [Html automaticaly creation command to edit target edit file](#html-automaticaly-creation-command-to-edit-target-edit-file)
  * [File api](#file-api)
  * [JavaScript interface](#javascript-interface)
  * [Html tag output](#html-tag-output)
  * [Troubleshooting](#troubleshooting)
  * [Ubuntu debian or widnows version](#ubuntu-debian-or-widnows-version)



Usage
-----

### Index mode
This mode is main mode. Top is `web terminal view`, down is `script name list`, bottom is toolbar.
Main usage is executoin script by list item clicking, other usage is maintenance script or app by longpress or toolbar.

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)


#### History

This feature is basic and great feature in `Command Click`. This always allow you to select current directory and mode which used, as if you look in Android's backstack feature's history.
Torigger by left botom history button clicked.
And more you look in url history by long press where you visited url (Afterward noting, switchable url history with history, or url history with button script exec)
* history item display mechanism {current_app_dir}__({current_script}) (when exist current script, edit execute is `Always`)

#### Change term size

Click toolbar left setting button, and terminal size change.


#### Add

This feature display when toolbar right setting button long pressed. Then, click `add`, so new script adding.
At the same time, if you installed code editor, edit new file.

 -  various settingVriables feature in `CommandClick`'s script

    | settingVariable| set value | description  |
    | --------- | --------- | ------------ |
    | `terminalDo` | `ON`/`Termux`/`OFF` | where to run in terminal; `ON`: in web terminal view, `Termux`: in termux, `OFF`: backgrond exe   |
    | `terminalSizeType` | `LONG`/`SHORT`/`OFF` | decide web terminal view size; `LONG`: long size, `SHORT`: short size, `OFF`: no sizing   |
    | `editExecute`  | `NO`/`ONCE`/`ALWAYS` | edit mode change; `NO`: normal edit, `ONCE`: one time edit and exedute, `ALWAYS`: always edit and execute
    | `terminalOutputMode`  | `NORMAL`/`REFLASH`/`REFLASH_AND_FIRST_ROW`/`DEBUG`/`NO` | `NORMAL`: normal terminal output, `REFLASH`: Before terminal output, screen resflesh, `REFLASH_AND_FIRST_ROW`: Before terminal output, screen resflesh and focus first row, `DEBUG`: stdr + stderr, `NO`: no output (bacground exec)
    | `onAutoExec`  | `NO`/`OFF` | ready for start and end script; `ON`: start or end exec on, `OFF`: exec off (default)
    | `onUpdateLastModify`  | `NO`/`OFF` | how updating file last modified status when executing; `ON`: update this, `OFF`: no update this
    | `onHistoryUrlTitle`  | `ON`/`OFF` | how adding url title to history; `ON`: add, `OFF`: no
    | `historySwitch`  | `ON`/`OFF` | switch app history with url history; `ON`: switch, `OFF`: no switch, `INHERIT`: inherit config setting
    | `urlHistoryOrButtonExec`  | `INHERIT`/`URL_HISTORY`/`BUTTON_EXEC` | switch url history or button script exec; `INHERIT`: inherit config setting, `URL_HISTORY`: switch url history, `BUTTON_EXEC`: switch url button script exec
    | `onAdBlock`  | `INHERIT`/`ON`/`OFF` | sadblock switch; `INHERIT`: inherit config setting, `ON`: on, `OFF`: off
    | `onUrlLaunchMacro`  | `OFF`/`RECENT`/`FREAQUENCY` | url launch macro(when set, cmdclick web terminal don't output); `OFF`: no launch, `RECENT`: recent use url launch, `FREAQUENCY`: most use url launch
    | `execJsOrHtmlPath`  | `string` | execute javascript or html file path
    | `setVariableType` | `string`  | when edit, whether to set variable type to commandVariable. You also have multiple specifing this. In detail, follow bellow. |
    | `terminalFontZoom` | `number` | adjust terminal font size (percentage) |
    | `terminalFontColor` | `string` | adjust terminal font color |
    | `terminalColor` | `string` | adjust terminal background color |
    | `beforeCommand` | `shell command string` | before run shellscript, run command |
    | `afterCommand` | `shell command string` | after run shellscript, run command |
    | `FileName`  | `string` | shellscript file name  |

 
  - setVariableType option
    | option| description | example  |
    | --------- | --------- | ------------ |
    | `CB` | checkbox | {variablebName}:CB=value1!value2!|..   |
    | `CBB` | checkbox with exec button | {variablebName}:CBB=value1!value2!&#124;{command string} |..   |
    | `ECB` | editable checkbox | {variablebName}:ECB=value1!value2!|..   |
    | `ECBB` | editable checkbox with exec button | {variablebName}:ECB=value1!value2&#124;{command string} |..   |
    | `EFCB` | editable file checkbox | {variablebName}:EFCB={grep prefix} |..   |
    | `EFCBB` | editable file checkbox with exec button | {variablebName}:EFCB={grep prefix}&#124;{command string} |..   |
    | `H` | password input | {variablebName}:H={password ..etc}   |
    | `RO` | read only | {variablebName}:RO= |
    | `NUM` | increment or decrement number | {variablebName}:NUM={init_value}!{min}..{max}!{step}(!{number of decimal places}) |
    | `NUMB` | increment or decrement number with exec button | {variablebName}:NUMB={init_value}!{min}..{max}!{step}(!{number of decimal places})&#124;{command string} |
    | `FL` | file select button | {variablebName}:FL=  |
    | `FLB` | file select button with exec button | {variablebName}:FLB={command string}  |
    | `MFL` | file select button | {variablebName}:MFL=  |
    | `DIR`  | directory select button | {variablebName}:DIR= |
    | `DIRB`  | directory select button | {variablebName}:DIRB={command string} |
    | `MDIR`  | directory select button | {variablebName}:MDIR= |
    | `DT`  | create file button | {variablebName}:DT=  |
    | `CLR` | select color  | {variablebName}:CLR= |
    | `CLRB` | select color with exec button | {variablebName}:CLRB={command string} |
    | `BTN` | botton  | {variablebName}:BTN={{command string}}    |
    | `FBTN` | botton  | {variablebName}:FBTN={{command string}}    |
    
      - button option usage  
      		 ex) jsf '${0}' (`jsf` execute javascript file path  
		 ex) jsf '${01}' (`jsf` execute javascript parrent directory path, `${01}` is parent dir   
		 ex) ::NoJsTermOut:: jsf '${0}' (`::NoJsTermOut::` disable terminal output when only javascript  
                 ex) echo ${0}   (`${0}` is current script path  
                 ex) ::BackStack:: ls    (`::BackStack::` is backstack, only work when prefix when only shellscript
                 ex) ::TermOut:: ls      (`::TermOut::` enable terminal output  
                 ex) top -n 1 > /dev/null  (when suffix is `> /dev/null` or `> /dev/null 2>&1`, no output)  


#### Run

Run script by click list item in index mode or play button edit mode (editExecute variable is `Always`)
Or run javascript file.

#### Edit

Edit script by form when long click list item in index mode 


#### Write

Edit script by editor when long click list item in index mode 

#### Delete

Delete script by `utility` -> `delete`  when long click list item in index mode 

#### Kill

Kill shellscript proccess by `utility` -> `kill`  when long click list item in index mode 


#### Init

Revert default setting varable in script proccess by `utility` -> `kill`  when long click list item in index mode
(only setting variable, but excluede `setVariableType`)

#### Description

Display description for script by `utility` -> `description`  when long click list item in index mode


#### Copy file

Copy file for script by `copy` -> `copy file`  when long click list item in index mode


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

#### Install

You can move script from download directory.

#### Config

You can setting `CommandClick` Configration
- detail setting reference [Add](#add)


#### Termux Setting

Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
You can set by onley this menu press.

* Below is a supplement.

1. Long press on termux
2. Click paste popup on termux  
3. Continue pressing `Enter` on termux
- clipboard contents:
   ```
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



#### Select term

You can select terminal tab form `term_1` to `term_4`.

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

##### Web search

If you web mark press when terminal size long, you can web search.
Also, Click url on web terminal view, this mode is automatic set


### Auto exec script

`Command Click` have auto exec script. This is used when `index mode` startup or end.

#### Startup script
1. This script is automaticaly executed when `index mode` startup.
But, in default, `onAutoExec` in setting variable is `OFF` so, if you enable this, you must be `ON` (reference to [add](#add)).

2. Override `config setting variable`, if you are change default value with your set value.


#### End script

This script is automaticaly executed when `index mode` end.
But, in default, `onAutoExec` is `OFF` so, if you enable this, you must be `ON` (reference to [add](#add)).



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


### Edit api

Type bellow command in termux, so that you can use `Command Click Gui Edit Dialog`  from termux command line

```
am start \
-n "com.puutaro.commandclick/.activity.MainActivity" \
--es current_app_dir "{current_app_dir}" \
--es current_script_file_name "{current_script_file_name}" \
--es on_shortcut "EDIT_API"

---

ex) am start \
-n "com.puutaro.commandclick/.activity.MainActivity" \
--es current_app_dir "/storage/emulated/0/cmdclick/AppDir/default" \
--es current_script_file_name "twitter_test.js" \
--es on_shortcut "EDIT_API"
```

### Url command

Exec bellow command in `CommandClick` shellscript, so that you can launch web site.
(This command is only active when command click focus)

```
am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "{url}"

---

ex) am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "https://github.com/puutaro/CommandClick/edit/master/README.md"
```

### Html automaticaly creation command to edit target edit file 

Exec bellow command in `CommandClick` shellscript, so that you can make automaticaly make html, css and javascript.
(This command is only active when command click focus)

```
am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es title "{html title}" \
		--es edit_path "{target edit file path}" \
		--es src_path "{source file path}" \
		--es on_click_sort "boolean(sortable when link click)" \
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
  
```
ex) am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "${PARENT_DIR_PATH}/tubePlayList" \
		--es src_path "${PARENT_DIR_PATH}/cmdclickUrlHistory" \
		--es on_click_sort "false" \
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
 - jsFileStystem
 	- jsFileStystem.readLocalFile(path: String) -> String  
		- read local file and return file contents string  
	- jsFileStystem.writeLocalFile(path: String, contents: String)  
		- write local file
	- jsFileStystem.jsFile(filename: String, terminalOutPutOption: String)  
		- write local monitor file  
	- jsFileStystem.jsEcho(terminalOutPutOption: String, contents: String)   
		- write local monitor file  
 - JsArgs 
	- jsArgs.get() -> tabsepalete string  
		jsArgs soruce is jsf argument in edit  
		ex) setVariableType="jsf $0 fristargment 'secondargument 2'" 
			-> `fristargment`\t`secondargument 2`  

 - JsIntent
 	- jsIntent  
 		- launchEditSite(editPath: String, srcPath: String? = null, onClickSort: String = "true", filterCode: String? = null)
 - JsDialog
 	- jsDialog.listJsDialog(listSource: String(tab sepalate)) return selected list
 	- jsDialog.formJsDialog(formSource: String(tab sepalate))
 		 - formSource tabsepalete string  return {key}={value} contents
 		 - setting reference [Add](#add)
 		 - ex) 
 				jsDialog.formJsDialog("efcb:EFCB=tube\tnumber:NUM=2!1..100!1\tpassword:H=\ttxt:TXT=\tcb:CB=aa!bb\tcb2:CB=gg!tt\tcb3:ECB=gg!tt")      
 			        -> efcb:EFCB=tubelist\nnumber:NUM=99\npassword:H=1234\ntxt:TXT=yrcy\ncb=aa\ncb2=tt\ncb3=tt  
				

### javascript pre order word
	- `${0}` -> current file path

### Html tag output

`CommandClick` script output trminal as html, so html tag is valid. You can use tag by bellow.
 - `<` -> `cmdclickLeastTag`
 - `>` -> `cmdclickGreatTag`

   - `Span tag` no working in script output. If you wont to use this, launch html file.
   - Url string automaticaly change anchor tag, but if you put 'href="' prefix in front of this string, no auto change.


### Troubleshooting

- When url laod slow in different than before, probably due to cache, so click it's url from `url history`.
    - In many cases, occur in google search result page.



### Ubuntu debian or widnows version

Reference to [url](https://github.com/puutaro/cmdclick)


