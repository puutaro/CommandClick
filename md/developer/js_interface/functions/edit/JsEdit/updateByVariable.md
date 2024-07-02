# jsEdit.updateByVariable

## Definition

```js.js
function jsEdit.updateByVariable(
	${fannelScriptPathString},
	${targetVariableNameString},
	${updateVariableValueString},
) -> runUpdateByVariable
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runUpdateByVariable
	?func=jsEdit.updateByVariable
	?args=
		&fannelScriptPath=${String}
		&targetVariableName=${String}
		&updateVariableValue=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsEdit.updateByVariable](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/edit/JsEdit.kt#L25)

## Detail

-> [jsEdit.updateByVariable](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/edit/JsEdit/updateByVariable.md)
