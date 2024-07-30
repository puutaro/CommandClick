# jsCopyItem.copyPath

## Definition

```js.js
function jsCopyItem.copyPath(
	${selectedItemString},
	${listIndexPositionInt},
) -> runCopyPath
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCopyPath
	?func=jsCopyItem.copyPath
	?args=
		&selectedItem=${String}
		&listIndexPosition=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Copy path or contents from list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

### Corresponding macro

-> [COPY_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#copy_path)

### selectedItem arg

file name for copy

### listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

### Example

```js.js
var=runCopyPath
   ?func=jsCopyItem.copyPath
   ?args=
       &selectedItem=${item name}
       &listIndexListPosition=NO_QUOTE:${item index}

```



## Src

-> [jsCopyItem.copyPath](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsCopyItem.kt#L28)


