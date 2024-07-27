# jsUbuntu.execScriptByBackground

## Definition

```js.js
function jsUbuntu.execScriptByBackground(
	${backgroundShellPathString},
	${argsTabSepaStrString},
	${monitorNumInt},
) -> runExecScriptByBackground
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runExecScriptByBackground
	?func=jsUbuntu.execScriptByBackground
	?args=
		&backgroundShellPath=${String}
		&argsTabSepaStr=${String}
		&monitorNum=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsUbuntu.execScriptByBackground](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsUbuntu.kt#L84)

## Detail

-> [jsUbuntu.execScriptByBackground](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsUbuntu/execScriptByBackground.md)
