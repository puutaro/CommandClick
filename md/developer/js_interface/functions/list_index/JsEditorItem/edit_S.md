# jsEditorItem.edit_S

## Definition

```js.js
function jsEditorItem.edit_S(
	${selectedItemString},
	${listIndexPositionInt},
) -> runEdit_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runEdit_S
	?func=jsEditorItem.edit_S
	?args=
		&selectedItem=${String}
		&listIndexPosition=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Edit file by edit text in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [SIMPLE_EDIT](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#simple_edit)

## selectedItem arg

file name for desc

## listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)


## Example

```js.js
var=runSimpleEdit
    ?func=jsEditorItem.edit_S
    ?args=
        &selectedItem=${item name}
        &listIndexPosition=NO_QUOTE:${item index}

```



## Src

-> [jsEditorItem.edit_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsEditorItem.kt#L27)


