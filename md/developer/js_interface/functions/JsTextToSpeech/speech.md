# subLabelingVars

Table
-----------------

* [Result](#result)
* [Argument](#argument)


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
| playMode | string | play mode switch: `ordinaly`, `shuffle`, `reverse`, `number` |
| onRoop | string | some string: roop, `""`: no roop |
| playNumber | string | play list order number  string |
| toLang | string | Select language: `en`(english), `zw`(chinese), `sp`(spanish), `ko`(korean), `ja`(japanese) |
| onTrack | string | save track switch: `""`, `on` |
| speed | string | speech speed int string, base '50',  |
| pitch | string | speech pitch int string, base '50' |


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
