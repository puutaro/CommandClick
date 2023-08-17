
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

- enable `termux` shell command


## Macro 

trigger when adding as prefix  

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


## Specify file path  

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
