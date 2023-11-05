# By text to speech, hello world

This section is jump board in order to become `CommandClick` core developer.  
This point include common use essence for [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) development.   
Therefore, if you complete here, you acquire `CommandClick` development base skill.  
　　

Table
-----------------

* [Step 1](#step-1)
* [Step 2](#step-2)
* [Step 3](#step-3)
* [Step 4](#step-4)

## Step 1

Create `textToSpeechTutorial1.js`.  

```js.js

// [1]
/// SETTING_SECTION_START
// [2]
scriptFileName="textToSpeechTutorial1.js"
// [3]
editExecute="ALWAYS"
// [4]
setReplaceVariables="TXT_LABEL=label"
// [5]
setReplaceVariables="FANNEL_PATH=${0}"
// [6]
setReplaceVariables="FANNEL_DIR_PATH=${01}/${001}"
setReplaceVariables="PLAY_TXT_PATH=${FANNEL_DIR_PATH}/playText.txt"
setReplaceVariables="PLAY_LIST_TSV_PATH=${FANNEL_DIR_PATH}/playList.tsv"
// [7]
setVariableTypes="speechText:TXT:LBL=${TXT_LABEL}=this" 
/// SETTING_SECTION_END

// [8]
/// CMD_VARIABLE_SECTION_START
// [9]
speechText="hello world"
/// CMD_VARIABLE_SECTION_END


execTextToSpeech();

function execTextToSpeech(){
    // [10]
    jsFileSystem.createDir("${FANNEL_DIR_PATH}");
    // [11]
    jsFileSystem.writeLocalFile(
        "${PLAY_TXT_PATH}",
        speechText
    );
    // [12]
    jsFileSystem.writeLocalFile(
        "${PLAY_LIST_TSV_PATH}",
       "${PLAY_TXT_PATH}"
    );
    let extraSettingMapStr = [
        `importance=low`,
        `pitch=50`,
    ].join("|");
    jsTextToSpeech.speech(
        "${PLAY_LIST_TSV_PATH}",
        extraSettingMapStr,
    );
};

```


[1] -> [setting section](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
[2] -> [scriptFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
[3] -> [editExecute](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editexecute)  
[4] -> [setReplaceVariables](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md#overview)  
[5] -> [pre order word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)  
[6]
-> [fannel dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#fannel_dir)  
[8] -> [cmd variables](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)  
[9] -> edit text cmd variable  
[7]  
-> [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#overview)  
-> [`TXT`, `LBL`](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#setvariabletypes-options-table)  
-> [This option can be compined](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md#this-option-can-be-combined)  
[10]  
-> [jsFileSystem](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/jsFileSystem.md)  
-> [createDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/createDir.md)  
[11]
-> [writeLocalFile](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsFileStystem/writeLocalFile.md)  
[12]
-> [TextToSpeech](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsTextToSpeech/speech.md)  

- `CommandClick` js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) is bookmarklet.  

- Js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) require `;` by each line. 

- This code is equal bellow code


## Step 2

Copy `textToSpeechTutorial1.js` to `/storage/emulated/0/Documents/cmdclick/default` directory<sub>[1]</sub>   

[1] -> [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 3

Click with [this](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

## Step 4

Click Play button in toolbar

<img src="https://github.com/puutaro/CommandClick/assets/55217593/d932c31b-0987-429a-a5dc-59f2e65cad41" width="400">  
