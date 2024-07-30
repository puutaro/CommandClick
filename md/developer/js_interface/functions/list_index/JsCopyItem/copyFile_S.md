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

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Copy file to list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [COPY_FILE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#copy_file)

## selectedItem arg

file name for copy

## listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## initialPath arg

File picker initial dir path

## Example

```js.js
var=runCopyFile
   ?func=jsCopyItem.copyFile_S
   ?args=
       &selectedItem=${item name}
       &listIndexListPosition=NO_QUOTE:${item index}
       &initialPath="/storage/emulated/0/Music"

```



## Src

-> [jsCopyItem.copyFile_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsCopyItem.kt#L76)


