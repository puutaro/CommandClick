# jsFileSystem.removeDir

## Definition

```js.js
function jsFileSystem.removeDir(
	${pathString},
) -> runRemoveDir
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runRemoveDir
	?func=jsFileSystem.removeDir
	?args=
		&path=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsFileSystem.removeDir](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/file/JsFileSystem.kt#L253)


