# jsListSelect.wrapRemoveItemInListFileCon

## Definition

```js.js
function jsListSelect.wrapRemoveItemInListFileCon(
	${targetListFilePathString},
	${removeTargetItemString},
	${currentScriptPathString},
	${replaceTargetVariableString},
	${defaultVariableString},
) -> runWrapRemoveItemInListFileCon
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runWrapRemoveItemInListFileCon
	?func=jsListSelect.wrapRemoveItemInListFileCon
	?args=
		&targetListFilePath=${String}
		&removeTargetItem=${String}
		&currentScriptPath=${String}
		&replaceTargetVariable=${String}
		&defaultVariable=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Wrap remove item from list contents in select box and set recent item to one

### targetListFilePath arg
Remove target list file path in list contents select box

### removeTargetItem
Remove target item

### currentScriptPath
current fannel path

### replaceTargetVariable
Set recent item to here variable name's edit text

### replaceTargetVariable
Set blank value to here variable name's edit text


## Src

-> [jsListSelect.wrapRemoveItemInListFileCon](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/edit/JsListSelect.kt#L90)

## Detail

-> [jsListSelect.wrapRemoveItemInListFileCon](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/edit/JsListSelect/wrapRemoveItemInListFileCon.md)
