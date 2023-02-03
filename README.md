![cmdclick_image](https://user-images.githubusercontent.com/55217593/199425521-3f088fcc-93b0-4a84-a9fd-c75418f40654.png)
# Command Click
So called 'shell browser', It's termux gui client with web browser feature

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)

It's a shellscript manager app from gui that have execution, edit, delete, and create as feature.

Pros
----
- Easily turn shellscript into a GUI application in android.
- Versatile usage for Terminal, Crome, OS setting, etc.
- Not only termux gui client but also web browser.
- Offer ritch edit dialog to termux.


Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Pre Setting(Mandatory)](#pre-setting)
* [Configration](#configration)
  * [Index Mode](#index-mode) 
  * [Edit Mode](#edit-mode) 
* [Usage](#usage)
  * [History](#history)
  * [Add](#add)
  * [Run](#run)
  * [Edit](#edit)
  	* [by gui](#by-gui)
  	* [by editor](#by-editor)
  	* [description by gui](#description-by-gui)
  * [Exit](#exit)
  * [Move](#move)
  * [Install](#install)
  * [Setting](#setting)
  * [Delete](#delete)
  * [App directory manager](#app-directory-manager)
      * [Launch](#launch)
      * [Add](#add)
      * [Change directory](#change-directory)
      * [Edit](#edit)
      * [Exit](#exit)
      * [Delete](#delete)
  * [Shell to Gui](#shell-to-gui)
  * [Shortcut table](#shortcut-table)
  * [Trouble Shouting](#trouble-shouting)
  	 * [Not Startup](#not-startup)


Pre Setting
-----
Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
For Instance, bellow process.
1. Add com.termux.permission.RUN_COMMAND permission
      `Android Settings` -> `Apps` -> `CommandClick` -> `Permissions` -> `Additional permissions` -> `Run commands in Termux environment`
3. Enable `allow-external-apps` [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#allow-external-apps-property-mandatory)
4. Add Storage permission. [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#storage-permission-optional)
5. Execute `termux-setup-storage` on termux



Usage
-----


### Index mode
This mode is main mode. Top is 'web terminal view', down is 'shellscript name list', bottom is toolbar.
Main usage is executigin shellscript by list item clicking, other usage is maintenance shellscript or app by longpress or toolbar.

![image](https://user-images.githubusercontent.com/55217593/216516311-c65c2795-30e3-4487-bd13-0fe8f7e72cdf.png)


#### history

This feature is basic and great feature in `Command Click` This always allow you to select current directory and mode which used, as if you look in Android's backstack feature's history.
torigger by left botom history button clicked.
And more you look in url history  by long press where you visited url (Afterward noting, switchable url history with history, or url history with button script exec)

#### add

This feature display when toolbar right setting button long pressed. Then, click `add`, so new shellscript adding.
At the same time, if you installed code editor, edit new file.

 -  various settingVriables feature in `CommandClick`'s shellscript

    | settingVariable| set value | description  |
    | --------- | --------- | ------------ |
    | `terminalDo` | `ON`/`Termux`/`OFF` | where to run in terminal: `ON`: in web terminal view, `Termux`: in termux, `OFF`: backgrond exe   |
    | `terminalSizeType` | `LONG`/`SHORT`/`OFF` | decide web terminal view size: `LONG`: long size, `SHORT`: short size, `OFF`: no sizing   |
    | `editExecute`  | `NO`/`ONCE`/`ALWAYS` | edit mode change: `NO`: normal edit, `ONCE`: one time edit and exedute, `ALWAYS`: always edit and execute
    | `terminalOutputMode`  | `NORMAL`/`REFLASH`/`REFLASH_AND_FIRST_ROW`/`DEBUG`/`NO` | `NORMAL`: normal terminal output, `REFLASH`: Before terminal output, screen resflesh, `REFLASH_AND_FIRST_ROW`: Before terminal output, screen resflesh and focus first row, `DEBUG`: stdr + stderr, `NO`: no output (bacground exec)
    | `onAutoExec`  | `NO`/`OFF` | ready for start and end script: `ON`: start or end exec on, `OFF`: exec off (default)
    | `onUpdateLastModify`  | `NO`/`OFF` | how updating file last modified status when executing: `ON`: update this, `OFF`: no update this
    | `onHistoryUrlTitle`  | `ON`/`OFF` | how adding url title to history: `ON`: add, `OFF`: no
    | `urlHistoryOrButtonExec`  | `INHERIT`/`URL_HISTORY`/`BUTTON_EXEC` | switch url history or button script exec: `INHERIT`: inherit config setting, `URL_HISTORY`: switch url history, `BUTTON_EXEC`: switch url button script exec
    | `setVariableType` | 'string'  | when edit, whether to set variable type to commandVariable. You also have multiple specifing this. In detail, follow bellow. |
    | `terminalFontZoom` | `number` | adjust terminal font size (percentage) |
    | `terminalFontColor` | `string` | adjust terminal font color |
    | `terminalColor` | `string` | adjust terminal background color |
    | `afterCommand` | command | before run shellscript, run command |
    | `shellFileName`  | string | shellscript file name  |

 
  - setVariableType option
    | option| description | example  |
    | --------- | --------- | ------------ |
    | `CB` | checkbox | {variablebName}:CB=value1!value2!|..   |
    | `H` | password input | {variablebName}:H={password ..etc}   |
    | `RO` | read only | {variablebName}:RO= |
    | `NUM` | increment or decrement number | {variablebName}:NUM={init_value}!{min}..{max}!{step}(!{number of decimal places}) |
    | `FL` | file select button | {variablebName}:FL=  |
    | `MFL` | file select button | {variablebName}:MFL=  |
    | `DIR`  | directory select button | {variablebName}:DIR= |
    | `MDIR`  | directory select button | {variablebName}:MDIR= |
    | `DT`  | create file button | {variablebName}:DT=  |
    | `CLR` | select color  | {variablebName}:CLR={default value}    |
    | `BTN` | botton  | {variablebName}:BTN={command}    |
    | `FBTN` | botton  | {variablebName}:FBTN={command}    |


#### run

Run shellscript by click list item in index mode or play button edit mode (editExecute variable is `Always`)


#### Edit mode
This mode main porpose is for edit shellscript. But also, execute shellscript when `editExecute` at shell variables is `ALWAYS` 

![image](https://user-images.githubusercontent.com/55217593/216524059-97c35357-c0de-48c1-953f-b1e1478cf296.png)


