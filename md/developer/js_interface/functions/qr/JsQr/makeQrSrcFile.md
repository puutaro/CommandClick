# jsQr.makeQrSrcFile

## Definition

```js.js
function jsQr.makeQrSrcFile(
	${qrSrcFilePathString},
	${qrSrcMapStrString},
) -> runMakeQrSrcFile
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runMakeQrSrcFile
	?func=jsQr.makeQrSrcFile
	?args=
		&qrSrcFilePath=${String}
		&qrSrcMapStr=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsQr.makeQrSrcFile](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/qr/JsQr.kt#L135)


