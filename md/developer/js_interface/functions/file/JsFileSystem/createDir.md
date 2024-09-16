# jsFileSystem.createDir

## Definition

```js.js
function jsFileSystem.createDir(
	${pathString},
) -> runCreateDir
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCreateDir
	?func=jsFileSystem.createDir
	?args=
		&path=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsFileSystem.createDir](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/file/JsFileSystem.kt#L248)


