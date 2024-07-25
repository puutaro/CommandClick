# jsMusic.play

Play music

Table
--------------------

* [Example](#example)
* [args](#args)
    * [tempPlayListPath](#tempplaylistpath)
    * [extraSettingMapStr](#extrasettingmapstr)
    * [shellPath](#shellpath)
    * [SAVE_PLAY_LIST macro ](#save_play_list-macro-)
    * [shellArgs](#shellargs)
    * [SAVE_PLAY_LIST arg table](#save_play_list-arg-table)


## Example

```js.js
|var=runMusicPlay
    ?func=jsMusic.play
    ?args=
        tempPlayListPath=`${cmdYoutuberTempFilePath}`
        &extraSettingMapStr=`
            importance=high
            |playMode={{ PLAY_MODE:${playMode} }}
            |onLoop=on
            |onTrack=on
            |playNumber=
            |currentAppDirName=${currentAppDirName},
            |scriptRawName=${scriptRawName},
            |extraContent=
                {{ EXTRA_CONTENT }}
            |shellPath=SAVE_PLAY_LIST
            |shellArgs=
                savePath=${cmdYoutuberPreviousMusicPlayListPath}`
            ,
```

## args


### tempPlayListPath

play list. Each line is url or audio file path.  

- Audio file play list ex

```tsv.tsv
${audio file path1}
${audio file path2}
${audio file path3}
${audio file path4}
.
.
.

```

- Streaming url play list ex

```tsv.tsv
${streming url1}
${streming url2}
${streming url3}
${streming url4}
.
.
.

```

### extraSettingMapStr

Music player extra settings

| arg name | type                                                                                                         | description                                      |
| -------- |--------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| importance | `high` <br> `low`                                                                                            | notification importance level: default is `high` |
| playMode | `shuffle` <br> `ordinaly` <br> `reverse` <br> `number`                                                       | play mode: default mode is `ordinaly`            |
| onLoop | `on` <br> None                                                                                               | Enable loop                                      |
| play_Number | Int                                                                                                          | Play number order                                |
| onTrack | `on` <br> None                                                                                               | Save play info                                   |
| currentAppDirName | [current app dir path](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) string | Used by saved play info  path                    |
| scriptRawName | [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) name string                                                                                           | Used by saved play info  path                    |
| shellPath | string                                                                                                       | [detail](#shellpath)                             |
| shellArgs | string                                                                                                       | [detail](#shellargs)                             |

### shellPath

Trigger when playing one music start 
Mainly, used to store previous play info  

### SAVE_PLAY_LIST macro 

Used to store previous play info  


### shellArgs

Shell args  
Mainly, used for `SAVE_PLAY_LIST`  


### SAVE_PLAY_LIST arg table

| arg name | type                                                                                                                      | description                                       |
| -------- |---------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|
| savePath | previous play info save path                                                                                              | notification importance level: default is `hight` |
