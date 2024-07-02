# jsFileSelect.execEditTargetFileName

## Definition

```js.js
function jsFileSelect.execEditTargetFileName(
	${targetVariableString},
	${renameVariableString},
	${targetDirPathString},
	${settingVariablesString},
	${commandVariablesString},
	${prefixString},
	${suffixString},
	${scriptFilePathString},
	${titleString},
) -> runExecEditTargetFileName
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runExecEditTargetFileName
	?func=jsFileSelect.execEditTargetFileName
	?args=
		&targetVariable=${String}
		&renameVariable=${String}
		&targetDirPath=${String}
		&settingVariables=${String}
		&commandVariables=${String}
		&prefix=${String}
		&suffix=${String}
		&scriptFilePath=${String}
		&title=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsFileSelect.execEditTargetFileName](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/edit/JsFileSelect.kt#L34)

## Detail

-> [jsFileSelect.execEditTargetFileName](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/edit/JsFileSelect/execEditTargetFileName.md)
