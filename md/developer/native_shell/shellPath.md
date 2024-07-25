# shellPath

Native android shell path (Not ubuntu shell)  
This is used in conjunction with bellow config etc...  

- [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)
- [editBoxTitleConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/editBoxTitleConfig.md)
- [menuConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/menuConfig.md)
- [toolbarButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/toolbarButtonConfig.md)
- [alter](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)
- [jsMusic.play](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsMusic/play.md)
- [jsTextToSpeech.speech](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsMusic/play.md)

Table
---------------------

* [Example](#example)
* [Use case](#use-case)
* [Format](#format)
* [Key-value table for shellPath](#key-value-table-for-shellpath)
* [macro for shellPath](#macro-for-shellpath)
    * [JUDGE_TSV_VALUE](#judge_tsv_value)
    * [JUDGE_LIST_DIR](#judge_list_dir)
    * [MAKE_HEADER_TITLE](#make_header_title)
        * [MAKE_HEADER_TITLE args table](#make_header_title-args-table)
        * [MAKE_HEADER_TITLE ex](#make_header_title-ex)
    * [SAVE_PLAY_LIST](#save_play_list)


## Example

- Ex to make header title in [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) 

```sh.sh
?shellPath=MAKE_HEADER_TITLE
?args=
    fannelPath=`${FANNEL_PATH}`
    &extraTitle=`file://${cmdYoutuberPlayInfoPath}`
```

- Ex to judge in [alter](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)

```sh.sh
shellIfPath=JUDGE_LIST_DIR
?ifArgs=
    tsvPath=${cmdYoutuberManagerListIndexTsvPath}
    &tsvValue="
        ${cmdYoutuberPreviousMusicPlayListPath}
        &${cmdYoutuberLikeMusicPlayListPath}"
    &alterCon="?onPut=OFF"
```

## Use case

UI setting and media player list save etc...

## Format

key-value[key-value]

## Key-value table for shellPath

| Key name      | value     | Description                                         | 
|---------------|-----------|-----------------------------------------------------| 
| `args`        | shell arg | Execute shell script to replace variables with args |


- Concat separator by config spec


## macro for shellPath

First, empirically, This shellPath is more used `macro` than user customize shell script.    
Because, android native shell speed is slow, so, more often `macro` use case.  
With macro, the internals are kotlin, so it's very fast.  
So, mainly explains macros.  

### JUDGE_TSV_VALUE

This macro is used in [alter](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)

-> [alter](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)

### JUDGE_LIST_DIR

This macro is used in [alter](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)

-> [alter](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)


### MAKE_HEADER_TITLE

Make header title in [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) and [editBoxTitleConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/editBoxTitleConfig.md)  

#### MAKE_HEADER_TITLE args table


| args name         | Description                                       | 
|-------------------|---------------------------------------------------|
| `fannelPath`         | current fannel path                               |
| `coreTitle` | [Optional] title body. if not specified, auto gen |
| `extraTitle` | [Optional] extra titlle. Put prefix with `:`      |


#### MAKE_HEADER_TITLE ex

```sh.sh
?shellPath=MAKE_HEADER_TITLE
?args=
    fannelPath=`${FANNEL_PATH}`
    &extraTitle=`file://${cmdYoutuberPlayInfoPath}`
```


### SAVE_PLAY_LIST

Save play list.   
This is used by text to speech player and music player.  

-> [music player usecase](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsMusic/play.md#shellpath)
-> [text to speech player usecase](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsTextToSpeech/speech.md#shellpath)

