# speech

Table
-----------------

* [Result](#result)
* [Argument](#argument)
  * [playListFilePath](#playlistfilepath)
  * [extraSettingMapStr](#extrasettingmapstr)
  * [shellPath](#shellpath)
  * [SAVE_PLAY_LIST macro](#save_play_list-macro)
  * [shellArgs](#shellargs)
  * [SAVE_PLAY_LIST arg table](#save_play_list-arg-table)

## Result

Execute text to speech


```js.js
jsTextToSpeech.speech(  
  playListFilePath: String,    
  extraSettingMapStr: String,  
)

```

## Argument

### playListFilePath

play list path [string]

### extraSettingMapStr

Setting ${key}=${value} pair to concat by `|`

| key          | type           | description                                                                                           |
|--------------|----------------|-------------------------------------------------------------------------------------------------------|
| importance   | string         | notification importance: `high` (default) / `low`                                                     |
| playMode     | string         | [Optional] play mode switch: `ordinaly`(default), `shuffle`, `reverse`, `number`                      |
| onRoop       | `on` <br> None | [Optional] some string: roop, `""`: no roop                                                           |
| playNumber   | Int            | [Optional] play list order number  string                                                             |
| toLang       | string         | [Optional] Select language: `en`(english), `zw`(chinese), `sp`(spanish), `ko`(korean), `ja`(japanese) |
| onTrack      | `on` <br> None | [Optional] save track switch: `""`, `on`                                                              |
| speed        | Int            | [Optional] speech speed int string, base '50',                                                        |
| pitch        | Int            | [Optional] speech pitch int string, base '50'                                                         |
| extraContent | string         | [Optional] Extara notification con which display content text in noti.                                |
| shellPath    | string         | [Optional] [detail](#shellpath)                                                                       |
| shellArgs    | string         | [Optional] [detail](shellargs)                                                                        |


ex1) speech from play list 

```js.js
let extraSettingMapStr = [
	`playMode=shuffle`,
	`onRoop=on`,
	`playNumber=`,
	`transMode=en`,
	`onTrack=on`,
	`speed=50`,
	`pitch=50`,
].join("|");
jsTextToSpeech.speech(  
  "${01}/${001}/playListFile.txt",    
  extraSettingMapStr,  
)
  
```

- playListFilePath

```
play text file path 1
play text file path 2
play text file path 3
.
.
.
```

ex2) speech from number


```js.js

let extraSettingMapStr = [
	`playMode=ordinaly`,
	`playNumber=1`,
	`transMode=en`,
	`speed=50`,
	`pitch=50`,
].join("|");

jsTextToSpeech.speech(  
  "${01}/${001}/playListFile.txt",    
  extraSettingMapStr,  
)
  
```

- playListFilePath

```
play text file path 1
play text file path 2
play text file path 3
.
.
.
```



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
