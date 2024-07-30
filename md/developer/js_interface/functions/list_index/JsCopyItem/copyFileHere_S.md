# jsCopyItem.copyFileHere_S

## Definition

```js.js
function jsCopyItem.copyFileHere_S(
	${selectedItemString},
	${listIndexPositionInt},
) -> runCopyFileHere_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCopyFileHere_S
	?func=jsCopyItem.copyFileHere_S
	?args=
		&selectedItem=${String}
		&listIndexPosition=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Copy file in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [COPY_FILE_HERE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#copy_file_here)

## selectedItem arg

file name for copy

## listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## initialPath arg

File picker initial dir path

## Example

```js.js
var=runCopyFile
   ?func=jsCopyItem.copyFileHere_S
   ?args=
       &selectedItem=${item name}
       &listIndexListPosition=NO_QUOTE:${item index}

```



## Src

-> [jsCopyItem.copyFileHere_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsCopyItem.kt#L134)


