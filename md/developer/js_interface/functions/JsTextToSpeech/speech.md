# speech

Table
-----------------

* [Result](#result)
* [Argument](#argument)
  * [playListFilePath](#playlistfilepath)
  * [extraSettingMapStr](#extrasettingmapstr)

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

| key | type | description |
| -------- | -------- | -------- |
| importance | string | notification importance: `high` (default) / `low`
| playMode | string | [Optional] play mode switch: `ordinaly`(default), `shuffle`, `reverse`, `number` |
| onRoop | string | [Optional] some string: roop, `""`: no roop |
| playNumber | string | [Optional] play list order number  string |
| toLang | string | [Optional] Select language: `en`(english), `zw`(chinese), `sp`(spanish), `ko`(korean), `ja`(japanese) |
| onTrack | string | [Optional] save track switch: `""`, `on` |
| speed | string | [Optional] speech speed int string, base '50',  |
| pitch | string | [Optional] speech pitch int string, base '50' |


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
