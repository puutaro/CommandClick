# jsDialog.qrScan_S

## Definition

```js.js
function jsDialog.qrScan_S(
	${titleString},
	${currentScriptPathString},
	${callBackJsPathString},
	${menuMapStrListStrString},
) -> runQrScan_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runQrScan_S
	?func=jsDialog.qrScan_S
	?args=
		&title=${String}
		&currentScriptPath=${String}
		&callBackJsPath=${String}
		&menuMapStrListStr=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsDialog.qrScan_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/dialog/JsDialog.kt#L364)


