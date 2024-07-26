
# Command Click Developer page
<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="500">  

  
This page is for developer. CommandClick true value change self made script to android app.   
I hope you get that knowledge.  

Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [System structure](#system-structure)
* [Fannel(addon) structure](#fannel_structure)
    * [Fannel (addon) dir structure](#fannel_dir_structure)
    * [Labeling section](#labeling-section)
    * [Cmd variables](#cmd-variables)
    * [Setting variables](#setting-variable)
        * [editExecute](#editexecute)
        * [setReplaceVariables](#setreplacevariables)
        * [setVariableTypes](#setvariabletypes)
        * [toolbar button config](#toolbar-button-config)
        * [More for setting variables](#more-for-setting-variables)
* [Javascript](#javascript)
    * [Js action](#js-action)
    * [Js shiban](#js-shiban)
    * [JavaScript interface](#javascript-interface)
    * [Javascript pre reserved word](#javascript-pre-reserved-word)
    * [Import js library](#import-js-library)
    * [Include Javascript Library](#include-javascript-library)
    * [Html tag output](#html-tag-output)
    * [Javascript TroubleShooting](#javascript-troubleshooting)
    * [Include css Library](#include-css-library)
* [Ubuntu](#ubuntu)
    * [Custom shell command](#custom-shell-command)
    * [Ubuntu env variables](#ubuntu-env-variables)
    * [Ubuntu Extra Startup Shell Paths](#ubuntu-extra-startup-shell-paths)
* [QR code format](#qr-code-format)
* [Directory structure](#directory-structure)
* [File api](#file-api)
* [Broadcast actions](#broadcast-actions)
* [Use port](#use-port)
* [CommandClick repository](#commandclick-repository)
* [Glossary](#glossary)


## System structure

<img src="https://github.com/puutaro/CommandClick/assets/55217593/88af0178-4854-4f67-bf3a-928ba4519f27" width="500">  

## Fannel (addon) dir structure <a id="fannel_dir_structure"></a>

`fannel` is `add-on` in `ComamndClick`

Create bellow directory tree

```
├── {fannelName}.js
└── {fanneName}Dir
    ├── settingVariables
    │   ├── setReplaceVariables.js
    │   └── setVariableTypes.js
    └── README.md
```

- Base fannel file is `{fannelName}.js`
- Place other settings and data in `{fanneName}Dir` dir


## Fannel(addon) structure <a id="fannel_structure"></a>


<img src="https://github.com/puutaro/CommandClick/assets/55217593/866958e3-8643-4cf0-b610-000f8245397f" width="400">  

- labeling section

- setting variable contents  
  -> [Setting variable](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

- cmd variable contents

  -> [cmd variable](#cmd-variables)


- script contents  
  `javascript`' or `shellscript`' contents

### Labeling section

-> [About labeling section](https://github.com/puutaro/CommandClick/blob/master/md/developer/labeling_section.md)

This section is description for `fannel`(js or shell file enforced by `CommandClick`).

### Cmd variables

User difinition setting variables to enable edit by gui  
If set, display firstly in [edit](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit). 

### Setting variable 

-> [About setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

`CommandClick`'s pre-reserved system setting variables to enable edit by gui   
If set, display secondly in [edit](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit). 

#### editExecute

Edit and Execute on click play button.  
Particularly, `ALWAYS` is most used value in order to standalone app.

| Value| Description |
| -------- | --------- |
| `NO` | only edit (default) |
| `ALWAYS` | always edit -> execute |


#### setReplaceVariables

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

#### setVariableTypes

This variable is **controller** in MVVM or MVC architecture    
When edit, set variable type to [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables).

You also have multiple specifying this.   
-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)

#### toolbar button config

You can customize bellow button config

[playButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#playbuttonconfig)
[editButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editbuttonconfig)
[settingButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#settingbuttonconfig)

#### More for setting variables

-> [About setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)

## Javascript

Js is one of the core language in `CommandClick`    
CC has super customization functions.     
And Enable to control CC system.

### Js action

`js action` is developed for `CommandClick` developer  
This language is annotation-oriented language based on javascript in `CommandClick`  
You automatically have more readable, maintainable, and manageable code by `js action`.

- `prompt` ex

```js.js
var=isOkHelloWorld
  ?func=prompt
  ?args=
    comfirmMsg="Hello world, OK?"
```

-> detail is constructing...

### Js shiban

CC has shiban for javascript.  
This is used to handle normal js or js action or etc.  

-> [js shiban](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_shiban.md)

### JavaScript interface

-> [javascript interfaces](https://github.com/puutaro/CommandClick/tree/master/md/developer/js_interface/functions)

`CommandClick` is javascript framework for andorid. Particularly, this methods strongly support your android app development(`fannel` development).  
This, so colled, android app row code library.

### Javascript pre reserved word

-> [About js pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)

Like `${0}` in shell, `CommandClick`'s js has pre reserved word

### Import js library

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!

-> [js import](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_import.md)

### Javascript system args

-> [About javascript system args](https://github.com/puutaro/CommandClick/blob/master/md/developer/system_js_args.md)


Add arg on CommandClick's certain action for [js](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsArgs/get.md).  
For example, `urlHistoryClick`, `onAutoExec` action.

### Include Javascript Library

-> [About include javascript library](https://github.com/puutaro/CommandClick/blob/master/md/developer/include_javascript_library.md)

First, I respect bellow javascript package author.  
Bellow respectable package is inclided assets. you can import like bellow.

### Include css Library

-> [About include css Library](https://github.com/puutaro/CommandClick/blob/master/md/developer/include_css_library.md)

First, I respect bellow css package author.  
Bellow respectable package is inclided assets. you can import like bellow.

### Html tag output

-> [About html tag output](https://github.com/puutaro/CommandClick/blob/master/md/developer/html_tag_output.md)

`CommandClick` script output trminal as html, so html tag is valid. You can use tag .

### Javascript TroubleShooting

-> [About js trouble shouting](https://github.com/puutaro/CommandClick/blob/master/md/developer/javascript_trouble_shooting.md)

## Ubuntu

CommadClick has ubuntu22.04.  
So, this app is full end app from frontend (browser) + backend (ubuntu).

### Custom shell command

It is a useful tool for `CommandClick`.
For example, send broadcast, toast, and etc.

-> [About custom shell command](https://github.com/puutaro/CommandClick/tree/master/md/developer/custom_shell_commands)

### Ubuntu env variables

-> [About environment variable in ubuntu](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md)

### Ubuntu Extra Startup Shell Paths

Set auto start shell script path 
Usecase is startup script, service, and daemon etc...

-> [ubuntuExtraStartupShellPaths.tsv](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/ubuntuExtraStartupShellPaths.md)

## QR code format

QR code has excellent carrying capacity.  So, suit for fannel distribution.    
By `CC` qr system, you can easily distribute your app (fannel).   
Bellow, that format.  

-> [qr code](https://github.com/puutaro/CommandClick/blob/master/md/developer/qrcode/qrcode.md)

## Directory structure

-> [About directory structure](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md)   
  

## File api  

CommandClick is managed by files data
So, each file data can use in CommandClick's development

-> [fileApi](https://github.com/puutaro/CommandClick/blob/master/md/developer/apiFiles.md#apifiles)

## File api for ubuntu

-> [About ubuntu file api](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntuFileApis.md)

Support file for CommandClick's ubuntu For example, alternative data base, etc..

### Broadcast actions

-> [About broadcast actions & schema](https://github.com/puutaro/CommandClick/blob/master/md/developer/broadcastActoins.md)

### Use port

-> [About use port](https://github.com/puutaro/CommandClick/blob/master/md/developer/usePort.md)

### Commandclick-repository

-> [About CommandClick's fannel repository](https://github.com/puutaro/commandclick-repository)

`fannel` is ComamndClick using script (javascript, and shellscript)
For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application `fannel`


### Glossary

-> [About glossary](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md)


