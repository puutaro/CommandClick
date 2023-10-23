

# JsArgs

This interfce pass argment to script.  
Mainly, this interface is used with 'BTN' option in [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) .  


```js.js
jsArgs.get()
	-> tabsepalete string  
		jsArgs soruce is jsf argument in edit  
		ex) setVariableType="jsf $0 fristargment 'secondargument 2'" 
			-> `fristargment`\t`secondargument 2`  

jsArgs.set(
	tabsepalete string
    )
	-> argment set (ex "{arg1}\t{arg2}\t..")  

```

- `${0}` -> [pre reserved  word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


- example 

```js.js

/// SETTING_SECTION_START
setVariableTypes="buttonText:BTN=cmd=jsf '${0}' firstArgs second_args!label=this"
/// SETTING_SECTION_END


/// CMD_VARIABLE_SECTION_START
buttonText=""
/// CMD_VARIABLE_SECTION_END


let args = jsArgs.get().split("\t");
const firstArgs = args.at(0); // firstArgs
const secondArgs = args.at(0); // second_args

```
