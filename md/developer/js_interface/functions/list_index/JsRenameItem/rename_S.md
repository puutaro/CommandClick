# jsRenameItem.rename_S

## Definition

```js.js
function jsRenameItem.rename_S(
	${selectedItemString},
	${listIndexPositionInt},
) -> runRename_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runRename_S
	?func=jsRenameItem.rename_S
	?args=
		&selectedItem=${String}
		&listIndexPosition=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Rename item in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [RENAME](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#rename)

## selectedItem arg

file name for rename

## listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Example

```js.js
var=runCopyFile
   ?func=jsRenameItem.rename_S
   ?args=
       &selectedItem=${item name}
       &listIndexListPosition=NO_QUOTE:${item index}

```



## Src

-> [jsRenameItem.rename_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsRenameItem.kt#L26)


