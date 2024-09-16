# jsPulseAudioReceiver.start

## Definition

```js.js
function jsPulseAudioReceiver.start(
	${pcIpv4AddressString},
	${serverPortInt},
) -> runStart
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runStart
	?func=jsPulseAudioReceiver.start
	?args=
		&pcIpv4Address=${String}
		&serverPort=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsPulseAudioReceiver.start](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsPulseAudioReceiver.kt#L18)

## Detail

-> [jsPulseAudioReceiver.start](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsPulseAudioReceiver/start.md)
