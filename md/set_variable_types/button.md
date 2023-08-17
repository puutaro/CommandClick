
# ignoreHistoryPaths


Table
-----------------
* [Overview](#overview)
* [Usage](#usage)
* [Macro](#macro)
  * [Macro table](#macro-table)


## Overview

This option transform `command variable` into button. 


## Usage  

```js.js
setVariableTypes={variableName}:BTN=cmd={command string}(!label={button label})
```

- example
  
<img src="https://github.com/puutaro/CommandClick/assets/55217593/52b84839-b7e5-41f9-a8ea-41639bac4dec" width="500">  

- `jsf` is command for js file executing
- '${0}' -> [javascript pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/js_pre_reserved_word.md)  
- enable `termux` shell command


## Macro 

Trigger when adding as prefix  

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

