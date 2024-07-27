# jsIntent.launchShortcut

## Definition

```js.js
function jsIntent.launchShortcut(
	${currentAppDirPathString},
	${currentScriptFileNameString},
	${currentFannelStateString},
) -> runLaunchShortcut
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runLaunchShortcut
	?func=jsIntent.launchShortcut
	?args=
		&currentAppDirPath=${String}
		&currentScriptFileName=${String}
		&currentFannelState=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsIntent.launchShortcut](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsIntent.kt#L69)

## Detail

-> [jsIntent.launchShortcut](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsIntent/launchShortcut.md)
