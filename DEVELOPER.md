
# Command Click Developer page
<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="500">  

  
This page is for developer. CommandClick true value change self made script to android app.   
I hope you get that knowledge.  

Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Structure](#structure)
* [Fannel(addon) structure](#fannel_structure)
* [Labeling section](#labeling-section)
* [Cmd variables](#cmd-variables)
* [Setting variables](#setting-variable)
* [Directory structure](#directory-structure)
* [Import library](#import-library)
* [File api](#file-api)
* [QR code format](#qr-code-format)
* [JavaScript interface](#javascript-interface)
* [Javascript pre reserved word](#javascript-pre-reserved-word)
* [Include Javascript Library](#include-javascript-library)
* [Javascript TroubleShooting](#javascript-troubleshooting)
* [Include css Library](#include-css-library)
* [Custom shell command](#custom-shell-command)
* [Ubuntu env variables](#ubuntu-env-variables)
* [Broadcast actions](#broadcast-actions)
* [Use port](#use-port)
* [Html tag output](#html-tag-output)
* [CommandClick repository](#commandclick-repository)
* [Glossary](#glossary)


### Structure

<img src="https://github.com/puutaro/CommandClick/assets/55217593/88af0178-4854-4f67-bf3a-928ba4519f27" width="500">  

### Fannel(addon) structure <a id="fannel_structure"></a>

`fannel` is `add-on` in `ComamndClick`  

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

### Directory structure

-> [About directory structure](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md)   
  

### Import library  

-> [js import](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_import.md)

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!  

### File api  

CommandClick is managed by files data
So, each file data can use in CommandClick's development

-> [fileApi](https://github.com/puutaro/CommandClick/blob/master/md/developer/apiFiles.md#apifiles)

### QR code format

-> [qr code](https://github.com/puutaro/CommandClick/blob/master/md/developer/qrcode/qrcode.md)

### JavaScript interface

-> [javascript interfaces](https://github.com/puutaro/CommandClick/tree/master/md/developer/js_interface)  

`CommandClick` is javascript framework for andorid. Particularly, this methods strongly support your android app development(`fannel` development).  
This, so colled, android app row code library.


### Javascript pre reserved word

-> [About js pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md) 

Like `${0}` in shell, `CommandClick`'s js has pre reserved word

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

### Custom shell command  

It is a useful tool for `CommandClick`.
For example, send broadcast, toast, and etc.  

-> [About custom shell command](https://github.com/puutaro/CommandClick/tree/master/md/developer/custom_shell_commands)

### Ubuntu env variables

-> [About environment variable in ubuntu](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md)

### File api for ubuntu

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

-> [About glossary](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)


