# By text to speech, hello world

This is required section if you want to `CommandClick`'s [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) creator.    
[Funnels](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) are surprisingly easy to make.  
You experience `CommandClick`'s impact as low code tool.  


Table
-----------------

* [Step 1](#step-1)
* [Step 2](#step-2)
* [Step 3](#step-3)

## Step 1

Create `hello_world_by_alert.js`.  

```js.js


/// SETTING_SECTION_START
editExecute="ALWAYS"
setReplaceVariables="TXT_LABEL=label"
setReplaceVariables="FANNEL_PATH=${0}"
setReplaceVariables="FANNEL_DIR_PATH=${01}/${001}"
setReplaceVariables="PLAY_TXT_PATH=${FANNEL_DIR_PATH}/playText.txt"
setReplaceVariables="PLAY_LIST_TSV_PATH=${FANNEL_DIR_PATH}/playList.tsv"
setVariableTypes="speechText:TXT:LBL=${TXT_LABEL}=this"
scriptFileName="textToSpeechTutorial1.js"
/// SETTING_SECTION_END


/// CMD_VARIABLE_SECTION_START
speechText="hello world"
/// CMD_VARIABLE_SECTION_END


execTextToSpeech();

function execTextToSpeech(){
    jsFileSystem.createDir("${FANNEL_DIR_PATH}");
    jsFileSystem.writeLocalFile(
        "${PLAY_TXT_PATH}",
        speechText
    );

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

- `CommandClick` js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) is bookmarklet.  

- Js [fannel](https://github.com/puutaro/commandclick-repository/blob/master/README.md#commandclick-repository) require `;` by each line. 

- This code is equal bellow code

```js.js
/// SETTING_SECTION_START // [1]
editExecute="NO" // [2]
scriptFileName="hello_world_by_alert.js" // [3]
/// SETTING_SECTION_END

alert("hello world");
```

[1] -> [setting section](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  
[2] -> [editExecute](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editexecute)  
[3] -> [scriptFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#scriptfilename)  

## Step 2

Copy `hello_world_by_alert.js` to `/storage/emulated/0/Documents/cmdclick/default`<sub>[1]</sub> directory 

[1] -> [app directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory)

## Step 3

Execute by [run](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#run)  

- Enable to execute from [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history) by bellow step

1. Click [edit startup](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#edit-startup) in `setting`
2. Click `add` button in [homeScriptUrlsPath](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#homescripturlspath)
3. Click `hello_world_by_alert.js` in grid box
4. Close edit box.
5. Click `hello_world_by_alert.js` in [url history](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#url-history)


