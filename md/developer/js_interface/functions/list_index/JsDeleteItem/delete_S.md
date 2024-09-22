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

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description


Delete item from list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [DELETE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#delete)

## parentDirPath arg

parent dir path of file name for delete

## selectedItem arg

file name for delete

## Example

```js.js
var=runDelete
    ?func=jsDeleteItem.delete_S
    ?args=
        &parentDirPath=${parent dir path}
        &selectedItem=${file name}

```



## Src

-> [jsDeleteItem.delete_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsDeleteItem.kt#L18)


