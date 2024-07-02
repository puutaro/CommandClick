# jsCopyItem.copyFile_S

## Definition

```js.js
function jsCopyItem.copyFile_S(
	${selectedItemString},
	${listIndexPositionInt},
	${initialPathString},
) -> runCopyFile_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCopyFile_S
	?func=jsCopyItem.copyFile_S
	?args=
		&selectedItem=${String}
		&listIndexPosition=${Int}
		&initialPath=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsCopyItem.copyFile_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsCopyItem.kt#L46)


