# jsDialog.copyDialog_S

## Definition

```js.js
function jsDialog.copyDialog_S(
	${titleString},
	${contentsString},
	${scrollBottomBoolean},
) -> runCopyDialog_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCopyDialog_S
	?func=jsDialog.copyDialog_S
	?args=
		&title=${String}
		&contents=${String}
		&scrollBottom=${Boolean}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsDialog.copyDialog_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/dialog/JsDialog.kt#L383)


