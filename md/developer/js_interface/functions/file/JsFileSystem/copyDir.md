# jsFileSystem.copyDir

## Definition

```js.js
function jsFileSystem.copyDir(
	${sourcePathString},
	${destiDirPathString},
) -> runCopyDir
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCopyDir
	?func=jsFileSystem.copyDir
	?args=
		&sourcePath=${String}
		&destiDirPath=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsFileSystem.copyDir](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/file/JsFileSystem.kt#L240)


