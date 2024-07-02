# jsQr.saveQrImage

## Definition

```js.js
function jsQr.saveQrImage(
	${srcQrStrString},
	${savePathString},
) -> runSaveQrImage
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runSaveQrImage
	?func=jsQr.saveQrImage
	?args=
		&srcQrStrString=${String}
		&savePathString=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsQr.saveQrImage](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/qr/JsQr.kt#L242)


