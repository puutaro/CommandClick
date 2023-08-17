
# setVariableTypes

This setting variable exist in order to transform command variable into specified edit component. 

ex) Transform `editText` command variable into select box  

- no set

<img src="https://github.com/puutaro/CommandClick/assets/55217593/3de88560-74f9-4e23-b271-42a5488a8d8a" width="400">  

- set select box  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/b750c4ef-86d9-4980-a377-120a35f47d15" width="400">  



- `setVariableTypes` options table
 
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
