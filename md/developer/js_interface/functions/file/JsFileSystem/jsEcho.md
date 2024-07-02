# jsFileSystem.jsEcho

## Definition

```js.js
function jsFileSystem.jsEcho(
	${outPutOptionString},
	${contentsString},
) -> runJsEcho
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runJsEcho
	?func=jsFileSystem.jsEcho
	?args=
		&outPutOptionString=${String}
		&contentsString=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsFileSystem.jsEcho](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/file/JsFileSystem.kt#L154)


