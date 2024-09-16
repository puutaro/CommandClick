# jsToast.errLog

## Definition

```js.js
function jsToast.errLog(
	${contentsString},
) -> runErrLog
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runErrLog
	?func=jsToast.errLog
	?args=
		&contents=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsToast.errLog](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsToast.kt#L34)


