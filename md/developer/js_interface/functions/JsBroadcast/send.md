# jsBroadcast.send

## Definition

```js.js
function jsBroadcast.send(
	${actionString},
	${broadCastMapStrString},
) -> runSend
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runSend
	?func=jsBroadcast.send
	?args=
		&action=${String}
		&broadCastMapStr=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Send broad cast

### action arg

Broad cast action

-> [Action Detail](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/common/variable/broadcast/scheme)

### broadcastMapStr arg
Broad cast extra key-value map contents by separated by `keySeparator`

-> [Extra Detail](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/common/variable/broadcast/extra)

### Example js version

```js.js
jsBroadcast.send(
   "com.puutaro.commandclick.music_player.play",
   `playMode=shuffle|onLoop=on|onTrack=on
    `,
)
```

### Example js action version

```js.js
var=runMusicPlay
    ?func=jsBroadcast.send
    ?args=
        &action="com.puutaro.commandclick.music_player.play"
        &broadCastMapStr=`
            |playMode=shuffle
            |onLoop=on
            |onTrack=on
        `
```


## Src

-> [jsBroadcast.send](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsBroadcast.kt#L16)


