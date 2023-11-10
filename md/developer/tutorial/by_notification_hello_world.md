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
* [Step 9](#step-9)
* [Step 10](#step-10)
* [Step 11](#step-11)
* [Step 12](#step-12)
* [Step 13](#step-13)
* [Step 14](#step-14)
* [Step 13](#step-15)

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
MESSAGE="hello world"
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

Paste bellow to `./byNotificationHelloWorldDir/settingVariables/hideSettingVariables.js`

```js.js
editExecute,
onAutoExec,
setReplaceVariables,
setVariableTypes,
```

-> [hideSettingVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md)  


### Step 7

Paste bellow to `./byNotificationHelloWorldDir/libs/readMessage.js`

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

Paste bellow to `./byNotificationHelloWorldDir/libs/saveArgsTsv.js`

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

Paste bellow to `./byNotificationHelloWorldDir/systemJs/onAutoExec.js`

```js.js
		// [1]
jsFileSystem.createDir(
	// [2]
	"${HELLO_WORLD_LIST_DIR_PATH}"
);
jsFileSystem.createDir(
	// [3]
	"${HELLO_WORLD_TEMP_DIR_PATH}"
);
// [4]
jsUbuntu.boot();

```

-> [on auto exec script](https://github.com/puutaro/CommandClick/blob/master/md/developer/FileApis.md#on_auto_exec)  
[1] -> [jsFileStystem.createDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/createDir.md)  
[2], [3] -> [replaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
[4] -> [jsUbuntu.boot](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsUbuntu/boot.md)  


### Step 10

Paste bellow to `./byNotificationHelloWorldDir/js/triggerHelloWorld.js`

```js.js

// [1]
jsimport "${HELLO_WORLD_SAVE_ARGS_TSV_JS_PATH}";
jsimport "${HELLO_WORLD_READ_MESSAGE_JS_PATH}";


const message = readMessage();
if(!message) exitZero();
// [2]
jsListSelect.updateListFileCon(
	"${HELLO_WORLD_MESSAGE_LIST_PATH}",
	message,
);

saveArgsTsv(message);

// [3]
jsUbuntu.execScriptByBackground(
	"${HELLO_WORLD_LAUNCH_SHELL_PATH}",
	"",
	1,
);

```

[1]  
-> [js import](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_import.md)  
-> [replaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
[2] -> [jsListSelect.updateListFileCon](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsListSelect/updateListFileCon.md)
[3] -> [jsUbuntu.execScriptByBackground](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsUbuntu/execScriptByBackground.md)

### Step 11

Paste bellow to `./byNotificationHelloWorldDir/shell/launch_hello_world.sh`

```sh.sh
#!/bin/bash

set -ue
			# [1]
readonly REPLACE_VARS_CON="$(get_rvar "${0}")"
	# [2]
readonly HELLO_WORLD_CHANNEL_NUM="$(\
	get_rvar "${REPLACE_VARS_CON}" HELLO_WORLD_CHANNEL_NUM \
)"
readonly HELLO_WORLD_EXIT_SHELL_PATH="$(\
	get_rvar "${REPLACE_VARS_CON}" HELLO_WORLD_EXIT_SHELL_PATH \
)"
readonly HELLO_WORLD_TEMP_ARGS_TSV_PATH="$(\
	get_rvar "${REPLACE_VARS_CON}" HELLO_WORLD_TEMP_ARGS_TSV_PATH \
)"
readonly ARGS_CON="$(\
	cat "${HELLO_WORLD_TEMP_ARGS_TSV_PATH}"
)"
# [3]
readonly MESSAGE=$(\
	tsvar "${ARGS_CON}" MESSAGE\
)

# [4]
noti \
	--notification-type launch \
	-cn ${HELLO_WORLD_CHANNEL_NUM} \
	--icon-name play \
	--importance high \
	--title "Hello world" \
	--message "${MESSAGE}" \
	--alert-once \
	--delete "shellPath=${HELLO_WORLD_EXIT_SHELL_PATH}" \
	--button "label=CANCEL,shellPath=${HELLO_WORLD_EXIT_SHELL_PATH}" \
>/dev/null 2>&1

espeak "${MESSAGE}"
```

[1], [2] -> [get_rvar](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/get_rvar.md) 
[3] -> [tsvar](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/tsvar.md)
[4] -> [noti][https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md]

### Step 12

Paste bellow to `./byNotificationHelloWorldDir/shell/exit_hello_world.sh`

```sh.sh
#!/bin/bash

set -ue
			# [1]
readonly REPLACE_VARS_CON="$(get_rvar "${0}")"
	# [2]
readonly HELLO_WORLD_CHANNEL_NUM="$(\
	get_rvar "${REPLACE_VARS_CON}" HELLO_WORLD_CHANNEL_NUM \
)"
# [3]
noti \
	--notification-type exit \
	-cn ${HELLO_WORLD_CHANNEL_NUM} 

```


[1], [2] -> [get_rvar](https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/get_rvar.md) 
[3] -> [noti][https://github.com/puutaro/CommandClick/blob/master/md/developer/custom_shell_commands/noti.md]

### Step 13

Copy this direcotry tree to `/storage/emulated/0/Documents/cmdclick/default` directory<sub>[1]</sub>   

[1] -> [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 14

Click with [this](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

## Step 15

Click `PLAY` button in toolbar.  
Change `Message` and click `PLAY` !  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e473667e-0e99-4f79-b35f-dea7d954d8a4" width="400">  

