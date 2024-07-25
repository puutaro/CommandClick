
# Button option


Table
-----------------
* [Overview](#overview)
* [Example](#example)
* [Format for button](#format-for-button)
* [Key-value table for button value](#key-value-table-for-button-value)
  * [cmd](#cmd)
    * [format for cmd](#format-for-cmd)
    * [cmd variant](#cmd-variant)
    * [cmd Macro ](#cmd-macro-)
  * [Macro table](#macro-table)


## Overview

This option transform `command variable` into button. 


## Example
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/52b84839-b7e5-41f9-a8ea-41639bac4dec" width="500">  

- `jsf` is command for js file executing
- '${0}' -> [javascript pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)  
- enable `termux` shell command


## Format for button

key-[key-value]

- ex

```js.js
{variableName1}
  :BTN=
    {key1-1}={value1-1}
    ?{key1-2}={value1-2}
    ?{key1-3}={value1-3}
    ?{key1-4}={value1-4}
    .
    .
    .
,

{variableName2}
  :BTN=
    {key2-1}={value2-1}
    ?{key2-2}={value2-2}
    ?{key2-3}={value2-3}
    ?{key2-4}={value2-4}
    .
    .
    .
,
.
.
.
```

## Key-value table for button value

| Key name                | value                         | Description                                      | 
|-------------------------|-------------------------------|--------------------------------------------------|
| `cmd`                   | command string and macro      | command <br> -> [detail](#cmd)                |
| `label`                 | button label string and emoji | Set button label                                 |
| `isConsec`              | true / other                  | Enable continuous press                          |
| `textSize`              | Int                           | text (label) size                                |
| `onBorder`              | `OFF` / other                 | Hide border line                                 |
| `disableKeyboardHidden` | true / other                  | Hide keyboard on click button                    |
| `tag`                   | path string                   | Save to list con box on click <br> -> [detail]() |


### cmd

#### format for cmd

command string and macro

- normal javascript ex

```js.js
cmd=::TermLong::jsf '${0}' ${EDIT_NEWS_URL_LIST_NAME_MODE}
```
- js action con ex

```js.js
cmd=jsac `
  tsvVars="listDir => asciiDirPath"
      ?importPath="${image2AsciiArtAsciiListIndexTsvPath}"
  |var=asciiDirName
      ?func=jsPath.basename
      ?args=
          path=${asciiDirPath}
  |acVar=runToImageState
      ?importPath=
          "${image2AsciiArtChangeStateAction}"
      ?replace=
          ON_LIST_DIR_UPDATER=ON
          &TSV_PATH="${image2AsciiArtImageListIndexTsvPath}"
          &LIST_DIR_OR_TSV_PATH="${asciiDirPath}/image"
          &ON_INFO_SAVE=ON
          &EXTRA_SAVE_INFO=(${asciiDirName})
          &STATE="${IMAGE}"
  ,`
```

- js action con ex

```js.js
cmd=jsa `${js action file path}`
```

- ubuntu bash in background ex

```sh.sh
cmd=bashb '${FILE_MANAGER_INSTALL_SHELL_PATH}' 
```

#### cmd variant


| name    | description                                           |
|---------|-------------------------------------------------------|
| `jsf`   | Execute javascript file                               |
| `jsa`   | Execute [js action]() file                            |
| `jsac`  | Execute  [js action]() contents                       |
| `bashf` | Execute quick bash script                             |
| `bashb` | Execute long time bash script like service and etc... |
| `basht` | Execute bash script in `termux`                         |


#### cmd Macro 

Trigger when adding as prefix  

ex)

```js.js
::TermLong:: jsf '${0}'
```


### Macro table

| name               | description                                            |
|--------------------|--------------------------------------------------------|
| `::NoJsTermOut::`  | disable terminal output when only javascript           |
| `::TermLong::`     | `web terminal view` size into long                     |
| `::TermOut::`      | force terminal output                                  |
| `> /dev/null`      | no output about stdout when only shellscript           |
| `> /dev/null 2>&1` | no output when only shellscript                        |
| `::BackStack::`    | backstack, only work when prefix when only shellscript |

- example

```js.js
	 ::NoJsTermOut:: jsf '${0}'
	```
	
	ex)
	 
	```sh.sh
	echo ${0}
	```
	
	  
	ex)
	
	```sh.sh
	 ::BackStack:: ls    
	```
	
	
	ex)
	
	```sh.sh
	 ::TermOut:: ls
	```
	
	
	ex)
	
	```sh.sh
	 top -n 1 > /dev/null  

