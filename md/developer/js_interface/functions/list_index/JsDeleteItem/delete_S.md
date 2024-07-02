# jsDeleteItem.delete_S

## Definition

```js.js
function jsDeleteItem.delete_S(
	${parentDirPathString},
	${selectedItemString},
) -> runDelete_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runDelete_S
	?func=jsDeleteItem.delete_S
	?args=
		&parentDirPath=${String}
		&selectedItem=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsDeleteItem.delete_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsDeleteItem.kt#L29)


