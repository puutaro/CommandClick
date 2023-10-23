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
  playMode: String(ordinaly|shuffle|reverse|number),  
  onRoop: String(empty or notEmply(roop on)),  
  playNumber: String (int string(valid in number mode),  
  toLang: text to speech language prefix string: en(english), zw(chinese), sp(spanish), ko(korean), ja(japanese))    
  onTrack: String(empty or notEmply(on Track)),    
  speed: String(int string)    
  pitch: String(int string)  
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| playListFilePath | string | play list file path (follow bellow ex |
| playMode | string | play mode switch: `ordinaly`, `shuffle`, `reverse`, `number` |
| playNumber | string | play list order number  string |
| toLang | string | Select language: `en`(english), `zw`(chinese), `sp`(spanish), `ko`(korean), `ja`(japanese) |
| onTrack | string | save track switch: `""`, `on` |
| speed | string | speech speed int string, base '50',  |
| pitch | string | speech pitch int string, base '50' |


   

ex1) speech from play list 

```js.js
jsTextToSpeech.speech(  
  "${01}/${001}/playListFile.txt",    
  "shuffle",  
  "on",  
  "",  
  "en"
  "on",    
  "50"    
  "50"  
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
jsTextToSpeech.speech(  
  "${01}/${001}/playListFile.txt",    
  "ordinaly",  
  "",  
  "1",  
  "en"
  "",    
  "50"    
  "50"  
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
