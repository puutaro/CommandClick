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

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsBroadcast.send](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsBroadcast.kt#L15)


