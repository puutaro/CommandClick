
setVariableTypes
-----------------

Table of setVariableTypes
-----------------
<!-- vim-markdown-toc GFM -->

* [Overview](#Overview)
* [setVariableTypes options table](#setvariabletypes-options-table)
* [Setting variable](#setting-variable)
* [Add](#add)
* [Edit execute once](#edit-execute-once)
* [Edit execute always](#edit-execute-always)
* [Import library](#import-library)
	* [Local path import](#local-path-import)
	* [Assets import](#assets-import)
	* [WEB import](#web-import)
* [Url command](#url-command)
* [Html automaticaly creation command to edit target edit file](#html-automaticaly-creation-command-to-edit-target-edit-file)
* [File api](#file-api)
* [JavaScript interface](#javascript-interface)
* [Javascript pre order word](#javascript-pre-order-word)
* [Include Javascript Library](#include-javascript-library)
* [Include css Library](#include-css-library)
* [Html tag output](#html-tag-output)
* [Html tag output](#html-tag-output)
* [Javascript TroubleShooting](#javascript-troubleshooting)
* [CommandClick repository](#commandclick-repository)


## Overview
This setting variable exist in order to transform command variable into specified edit component. 

ex) Transform `editText` command variable into select box  

- no set

<img src="https://github.com/puutaro/CommandClick/assets/55217593/3de88560-74f9-4e23-b271-42a5488a8d8a" width="400">  

- set select box  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/b750c4ef-86d9-4980-a377-120a35f47d15" width="400">  



## `setVariableTypes` options table
 
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
