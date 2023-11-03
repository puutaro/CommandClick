# tspeech

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [--help, -h](#help) 
  * [--launch-type, -t](#launch-type)
  * [--importance, -i](#importance)
  * [--list-file-path, -l](#list-file-path)
  * [--extra-setting-map-str, -e](#extra-setting-map-str)
  * [--current-app-dir-name, -d](#current-app-dir-name)
  * [--fannel-row-name, -f](#fannel-row-name)
* [example](#example)

## Overview

TextToSpeech management command


```sh.sh
tspeech \
  ${args}
```

## Argument

### help,  -h <a id="help"></a>

help contents


### --launch-type, -t <a id="launch-type"></a>

Launch type

| type | description |
| ------ | -------|
| launch | launch notification |
| exit | close notification |

### [Optional] --importance, -i <a id="importance"></a>

Notification importance table

| type | description |
| ------ | -------|
| high | importance high (default) |
| low | importance low|


### [Optional] --list-file-path, -l <a id="list-file-path"></a>

play list path

### [Optional] --extra-setting-map-str, -e <a id="extra-setting-map-str"></a>

[About extra setting map str](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsTextToSpeech/speech.md#extrasettingmapstr)

### [Optional] --current-app-dir-name, -d <a id="current-app-dir-name"></a>

[current app dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#app-directory) in order to track Text to speech 

### [Optional] --fannel-row-name, -f <a id="fannel-row-name"></a>

[fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) name without extend 

## example

ex1)

```sh.sh
tspeech \
  -t "${launch} \
  -l "{play list path}" \
  -e "play_mode=shuffle" \
  -e "on_roop=on" \
  -e "trans_mode=en" \
  -e "on_track=on" \
  -e "pitch_schema=50"
```
