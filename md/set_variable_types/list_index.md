
# Button option


Table
-----------------
* [Overview](#overview)
* [Usage](#usage)
* [Macro](#macro)
  * [Macro table](#macro-table)


## Overview

This option transform `command variable` into list index.


## Usage  

```js.js
setVariableTypes={variableName}:LI=listDir={target list dir path}|menu={menuName1}(&subMenuName1&subMenuName2..}!{menuName2}(&subMenuName21&subMenuName22..}(|prefix={grep prefix})(|suffix={grep suffix})
```

- example
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/7a73a987-a71a-461a-8f54-12eea684b162" width="500">  

- enable only one specifing in `setVariableTypes`
- this list is script


## pre reserved menu name  

| name| description  |
| --------- | --------- |
| `sync` | sync list to current directory files |
| `delete` | delete selected script |
| `add` | add script |
| `write` | edit item by editor  |
| `cat` | show time file contents  |
| `desc` | how description |
| `copy_path` | copy item file path |
| `copy_file` | copy item file to other directory |
| `editC` | edit command variables |
| `editS` | edit setting variables |


ex)

```js.js
::TermLong:: jsf '${0}'
```


### Macro table

| name|  description  |
| --------- | --------- |
| `::NoJsTermOut::` | disable terminal output when only javascript  |
| `::TermLong::` | `web terminal view` size into long   |
| `::TermOut::`  | force terminal output
| `> /dev/null`  | no output about stdout when only shellscript
| `> /dev/null 2>&1`  | no output when only shellscript
| `::BackStack::`  | backstack, only work when prefix when only shellscript

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

