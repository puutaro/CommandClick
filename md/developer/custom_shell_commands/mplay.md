# mplay


MusicPlayer management command

```sh.sh
--launch-type
-t
: launch/exit

--list-file-path
-l
: play list path

--extra-setting-map-str
-e
: extra map str
option
format: ${key1}=${valueFloatStr2}|${key1}=${valueFloatStr2}|..

optional key
importance: Notification importance, high/low
playMode: Play mode switch: ordinaly(default), shuffle, reverse, number
onLoop: Some string: roop, "": no roop
play_Number:  Play list order number string
onTrack: Save track switch: "", on

--current-app-dir-name
-d
: current app direcotry(fannel parent directory)

--fannel-raw-name
-f
: fannle name without extend

ex)
mplay \
-t "launch \
-l "{play list path}" \
-e "playMode=shuffle" \
-e "onLoop=on" \
-e "onTrack=on" \
```

