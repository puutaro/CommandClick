# By notification, hello_world

This section serve you practical skill for [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) development.  
You can acquire MVVC architecture in js, and base skill for shellscript. 
With finishing this section, you 


Table
-----------------

* [Sample](#sample)
* [Step 1](#step-1)
* [Step 2](#step-2)
* [Step 3](#step-3)
* [Step 4](#step-4)
* [Step 5](#step-5)
* [Step 6](#step-6)
* [Step 7](#step-7)
* [Step 8](#step-8)

## Sample

-> [byNotificationHelloWorld](https://github.com/puutaro/CommandClick-Tutorial/tree/master/fannels/byNotificationHelloWorld)


## Step 1

Create bellow directory tree

```
├── byNotificationHelloWorld.js
└── byNotificationHelloWorldDir
    ├── by_notification_hello_world.md
    ├── js
    │   └── triggerHelloWorld.js
    ├── libs
    │   ├── readMessage.js
    │   └── saveArgsTsv.js
    ├── settingVariables
    │   ├── hideSettingVariables.js
    │   ├── setReplaceVariables.js
    │   └── setVariableTypes.js
    ├── shell
    │   ├── exit_hello_world.sh
    │   └── launch_hello_world.sh
    └── systemJs
        └── onAutoExec.js
```

### Step 2


Paste bellow code to `byNotificationHelloWorld.js`  

```js.js

// [1]
/// LABELING_SECTION_START
// [2]
// file://${01}/${001}/by_notification_hello_world.md
/// LABELING_SECTION_END

// [3]
/// SETTING_SECTION_START
// [4]
editExecute="ALWAYS"
// [5]
onAutoExec="ON"
// [6]
setReplaceVariables="file://"
// [7]
setVariableTypes="file://"
// [8]
hideSettingVariables="file://"
// [9]
scriptFileName="by_notification_hello_world.js"
/// SETTING_SECTION_END

// [10]
/// CMD_VARIABLE_SECTION_START
PLAY=""
MESSAGE=""
/// CMD_VARIABLE_SECTION_END


/// Please write bellow with javascript

```

[1] -> [labeling section](https://github.com/puutaro/CommandClick/blob/master/md/developer/labeling_section.md)  
[2] -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)   
[3] -> [setting section](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)   
[4] -> [editExecute](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editexecute)  
[5] -> [onAutoExec](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#onautoexec)  
[6]   
-> [setReplaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
-> [Specify file path](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#specify-file-path)    
[7]  
-> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#overview)  
-> [Specify config](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#specify-config)  
[8]
-> [hideSettingVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md#overview)  
-> [Specify file path](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md#specify-file-path)  
[9] -> [scriptFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename) 
[10] -> [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)    


- `CommandClick` js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) is bookmarklet.  

- Js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) require `;` by each line.

### Step 3

Paste [his markdown contents](https://github.com/puutaro/CommandClick-Tutorial/blob/master/fannels/byNotificationHelloWorld/byNotificationHelloWorldDir/by_notification_hello_world.md) to ``./byNotificationHelloWorldDir/by_notification_hello_world.md`  `  


[8] -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)  
[6]
-> [fannel dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#fannel_dir)  

[9] -> edit text cmd variable  

-> [labeling section](https://github.com/puutaro/CommandClick/blob/master/md/developer/labeling_section.md)


### Step 4

Paste bellow contents to `./byNotificationHelloWorldDir/settingVariables/setReplaceVariables.js`

```js.js

// basic
TXT_LABEL=label,
BTN_CMD=cmd,
BTN_LABEL=label,
LIST_PATH=listPath,
LIMIT_NUM=limitNum,

// setting
HELLO_WORLD_CHANNEL_NUM=30,

// dir path
// [1]
HELLO_WORLD_FANNEL_DIR_PATH=
	"${01}/${001}",
HELLO_WORLD_LIBS_DIR_PATH=
	"${HELLO_WORLD_FANNEL_DIR_PATH}/libs",
HELLO_WORLD_JS_DIR_PATH=
	"${HELLO_WORLD_FANNEL_DIR_PATH}/js",
HELLO_WORLD_SHELL_DIR_PATH=
	"${HELLO_WORLD_FANNEL_DIR_PATH}/shell",
HELLO_WORLD_LIST_DIR_PATH=
	"${HELLO_WORLD_FANNEL_DIR_PATH}/list",
HELLO_WORLD_TEMP_DIR_PATH=
	"${HELLO_WORLD_FANNEL_DIR_PATH}/temp",


// libs path
HELLO_WORLD_SAVE_ARGS_TSV_JS_PATH=
	"${HELLO_WORLD_LIBS_DIR_PATH}/saveArgsTsv.js",
HELLO_WORLD_READ_MESSAGE_JS_PATH=
	"${HELLO_WORLD_LIBS_DIR_PATH}/readMessage.js",

// js path
// [2]
HELLO_WORLD_FANNEL_PATH=
	"${0}",
HELLO_WORLD_TRIGGER_JS_PATH=
	"${HELLO_WORLD_JS_DIR_PATH}/triggerHelloWorld.js",

// shell path
HELLO_WORLD_LAUNCH_SHELL_PATH=
	"${HELLO_WORLD_SHELL_DIR_PATH}/launch_hello_world.sh",
HELLO_WORLD_EXIT_SHELL_PATH=
	"${HELLO_WORLD_SHELL_DIR_PATH}/exit_hello_world.sh",

// other
HELLO_WORLD_TEMP_ARGS_TSV_PATH=
	"${HELLO_WORLD_TEMP_DIR_PATH}/args.tsv",
HELLO_WORLD_MESSAGE_LIST_PATH=
	"${HELLO_WORLD_LIST_DIR_PATH}/messageList.txt",
```

setReplaceVariables  
-> [setReplaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
-> [Specify file path](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#specify-file-path)    
[1], [2] -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


### Step 5

Paste bellow to ./byNotificationHelloWorldDir/settingVariables/setVariableTypes.js

```js.js

PLAY:
	:BTN:HL=
		${BTN_CMD}=jsf '${HELLO_WORLD_TRIGGER_JS_PATH}' 
			!${BTN_LABEL}=this,

MESSAGE:
	LBL:TXT:ELSB=
		${TXT_LABEL}=THIS
		|
			${LIST_PATH}=${HELLO_WORLD_MESSAGE_LIST_PATH}
				!${LIMIT_NUM}=10,
```

setVariableTypes
-> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#overview)  
-> [Specify config](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#specify-config)  



### Step 6

Paste bellow to `./textToSpeechTutorial2Dir/settingVariables/hideSettingVariables.js`

```js.js
editExecute,
onAutoExec,
setReplaceVariables,
setVariableTypes,
```

-> [hideSettingVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md)


### Step 7

Paste bellow to `./textToSpeechTutorial2Dir/libs/readMessage.js`

```js.js

function readMessage(){
				// [1]
	const mainFannelCon = jsFileSystem.readLocalFile(
		// [2]
		"${HELLO_WORLD_FANNEL_PATH}" 
	);
			// [3]
	const cmdCon = jsScript.subCmdVars(
		mainFannelCon
	);
			// [4]
	return jsScript.subValOnlyValue(
		"MESSAGE",
		cmdCon,
	);
};

```
[1] -> [jsFileStystem.readLocalFile](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/readLocalFile.md)
[2] -> [replaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)
[3] -> [jsScript.subCmdVars](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsScript/subCmdVars.md)
[4] -> [jsScript.subCmdVars](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsScript/subCmdVars.md)

### Step 8

Paste bellow to `./textToSpeechTutorial2Dir/libs/saveArgsTsv.js`

```js.js
function saveArgsTsv(
	message
){
	const argsTsvCon = [
		`MESSAGE\t${message}`,
	].join("\n");
			// [1]
	jsFileSystem.writeLocalFile(
		"${HELLO_WORLD_TEMP_ARGS_TSV_PATH}",
		argsTsvCon
	);
};
```

[1] -> [jsFileStystem.readLocalFile](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/readLocalFile.md)


### Step 9

Paste bellow to `./textToSpeechTutorial2Dir/systemJs/onAutoExec.js`

```js.js
		// [1]
jsFileSystem.createDir(
	"${HELLO_WORLD_LIST_DIR_PATH}"
);
jsFileSystem.createDir(
	"${HELLO_WORLD_TEMP_DIR_PATH}"
);
// [2]
jsUbuntu.boot();

```

-> [on auto exec script](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#on_auto_exec)
[1] -> [jsFileStystem.createDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/createDir.md)
[2] -> [jsUbuntu.boot](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsUbuntu/boot.md)


### Step 10



## Step 6

Copy this direcotry tree to `/storage/emulated/0/Documents/cmdclick/default` directory<sub>[1]</sub>   

[1] -> [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 7

Click with [this](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

## Step 8

Click Play button in toolbar.  
Change `Pich` and quiz to ours!  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/489ee2f7-3e20-4d1a-9f34-bc686a11265a" width="400">  


