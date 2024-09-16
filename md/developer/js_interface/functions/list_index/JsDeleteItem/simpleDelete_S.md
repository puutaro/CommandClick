# jsDeleteItem.simpleDelete_S

## Definition

```js.js
function jsDeleteItem.simpleDelete_S(
	${selectedItemString},
	${listIndexListPositionInt},
) -> runSimpleDelete_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runSimpleDelete_S
	?func=jsDeleteItem.simpleDelete_S
	?args=
		&selectedItem=${String}
		&listIndexListPosition=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Delete item from list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [SIMPLE_DELETE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#simple_delete)

## selectedItem arg

file name for delete

## listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Example

```js.js
var=runDelete
    ?func=jsDeleteItem.simpleDelete_S
    ?args=
        &selectedItem=${item name}
        &listIndexListPosition=NO_QUOTE:${item index}

```



## Src

-> [jsDeleteItem.simpleDelete_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsDeleteItem.kt#L84)


