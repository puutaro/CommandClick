# By text to speech, hello world2 Game

Thsi section serve you standard skill for [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) development.  
Although `CommandClick` is wide spread option for low code: file split, multiple option combination, and etc, here include common usecase.  
If you use this section knowledge, you develop wide spread UI, and feature for less low cost.  
I hope you live easily devevlopement life.　　


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

## Sample

-> [textToSpeechTutorial2](https://github.com/puutaro/CommandClick-Tutorial/tree/master/fannels/textToSpeechTutorial2)


## Step 1

Create bellow directory tree

```
├── textToSpeechTutorial2.js
└── textToSpeechTutorial2Dir
    ├── settingVariables
    │   ├── hideSettingVariables.js
    │   ├── setReplaceVariables.js
    │   └── setVariableTypes.js
    └── textToSpeechTutorial2.md
```

### Step 2

Paste bellow code to `textToSpeechTutorial2.js`  

```js.js

// [1]
/// LABELING_SECTION_START
// file://${01}/${001}/textToSpeechTutorial2.md
/// LABELING_SECTION_END

// [2]
/// SETTING_SECTION_START
// [3]
scriptFileName="textToSpeechTutorial2.js"
// [4]
editExecute="ALWAYS"
// [5]
setReplaceVariables="file://TXT_LABEL=label"
// [6]
setVariableTypes="file://"
// [7]
hideSettingVariables="file://"
/// SETTING_SECTION_END

// [8]
/// CMD_VARIABLE_SECTION_START
speechText="hello world"
pitch="50"
/// CMD_VARIABLE_SECTION_END

// [9]
let args = jsArgs.get().split("\t");
var FIRST_ARGS = args.at(0);

// [10]
switch(FIRST_ARGS){
    case "":
        execTextToSpeech();
        break;
    case "${PICH_INIT_MODE}":
        // [11]
        jsEdit.updateByVariable(
            "${FANNEL_PATH}",
            "pitch",
            "50"
        );
        break;
};

function execTextToSpeech(){
    // [12]
    jsFileSystem.createDir("${FANNEL_DIR_PATH}");
    // [13]
    jsFileSystem.writeLocalFile(
        "${PLAY_TXT_PATH}",
        speechText
    );

    jsFileSystem.writeLocalFile(
        "${PLAY_LIST_TSV_PATH}",
       "${PLAY_TXT_PATH}"
    );
    // [14]
    let extraSettingMapStr = [
        `importance=low`,
        `pitch=${pitch}`,
    ].join("|");
    jsTextToSpeech.speech(
        "${PLAY_LIST_TSV_PATH}",
        extraSettingMapStr,
    );
};

```

[1] -> [labeling section](https://github.com/puutaro/CommandClick/blob/master/md/developer/labeling_section.md)  
[2] -> [setting section](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
[3] -> [scriptFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
[4] -> [editExecute](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editexecute)  
[5]   
-> [setReplaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
-> [Specify file path](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#specify-file-path)    
[6]  
-> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#overview)  
-> [Specify config](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#specify-config)  
[7]
-> [hideSettingVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md#overview)  
-> [Specify file path](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md#specify-file-path)  
[8] -> [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)    
[9]   
-> [JsArgs](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/jsArgs.md)  
-> [JsArgs.get](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsArgs/get.md)  
[10] -> Most frequent code to handle by first argument in `CommandClick`  
[11] -> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#overview)  
[12]
-> [JsEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/JsEdit.md)  
-> [jsEdit.updateByVariable](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsEdit/updateByVariable.md)  

[13]  
-> [jsFileSystem](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/jsFileSystem.md)  
-> [createDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/createDir.md)  
[14]
-> [writeLocalFile](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/writeLocalFile.md)  
[15]
-> [TextToSpeech](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsTextToSpeech/speech.md)  


### Step 3

Paste bellow code to `textToSpeechTutorial2.js`  


[8] -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)  
[6]
-> [fannel dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#fannel_dir)  

[9] -> edit text cmd variable  


Paste [this markdown contents](https://github.com/puutaro/CommandClick-Tutorial/blob/master/fannels/textToSpeechTutorial2/textToSpeechTutorial2Dir/textToSpeechTutorial2.md) to `./textToSpeechTutorial2Dir/textToSpeechTutorial2.md`  

-> [labeling section](https://github.com/puutaro/CommandClick/blob/master/md/developer/labeling_section.md)

### Step 4

Paste bellow contents to `./textToSpeechTutorial2Dir/settingVariables/setReplaceVariables.js`

```js.js

// for edit component
BTN_LABEL=label,
BTN_CMD=cmd,

// setting
PICH_INIT_MODE=pichInitMode,

// dir path
// [1]
FANNEL_DIR_PATH="${01}/${001}",

// file path
// [2]
FANNEL_PATH="${0}",
PLAY_TXT_PATH="${FANNEL_DIR_PATH}/playText.txt",
PLAY_LIST_TSV_PATH="${FANNEL_DIR_PATH}/playList.tsv",

```
setReplaceVariables  
-> [setReplaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
-> [Specify file path](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#specify-file-path)    
[1], [2] -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)


### Step 5

Paste bellow to ./textToSpeechTutorial2Dir/settingVariables/setVariableTypes.js

```js.js

speechText:
	TXT:LBL=
		${TXT_LABEL}=this,

pitch:
	LBL:TXT:NUM:BTN=
		${TXT_LABEL}=this
		|
			!1..500!1
		|
			${BTN_CMD}=jsf '${0}' ${PICH_INIT_MODE}
				!${BTN_LABEL}=INIT,

```

setVariableTypes
-> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#overview)  
-> [Specify config](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#specify-config)  


- `CommandClick` js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) is bookmarklet.  

- Js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) require `;` by each line.

## Step 6
./textToSpeechTutorial2Dir/settingVariables/hideSettingVariables.js

```js.js

editExecute,
setReplaceVariables,
setVariableTypes,
hideSettingVariables,

```

-> [hideSettingVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/hide_setting_variables.md)

## Step 7

Copy this direcotry tree to `/storage/emulated/0/Documents/cmdclick/default` directory<sub>[1]</sub>   

[1] -> [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 8

Click with [this](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

## Step 9

Click Play button in toolbar.  
Change `Pich` and quiz to ours!  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/489ee2f7-3e20-4d1a-9f34-bc686a11265a" width="400">  


