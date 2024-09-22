# jsListSelect.initListFile

## Definition

```js.js
function jsListSelect.initListFile(
	${targetListFilePathString},
	${itemTextListConString},
) -> runInitListFile
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runInitListFile
	?func=jsListSelect.initListFile
	?args=
		&targetListFilePath=${String}
		&itemTextListCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Complete list contents select box with item test list con (item separated by newline)

### targetListFilePath arg
list (item separated by newline) file list contents select box

### itemTextListCon

item test list con (item separated by newline)



## Src

-> [jsListSelect.initListFile](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/edit/JsListSelect.kt#L30)


