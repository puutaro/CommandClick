# jsCsv.read_S

## Definition

```js.js
function jsCsv.read_S(
	${tagString},
	${filePathString},
	${withNoHeaderString},
	${limitRowNumSourceInt},
) -> runRead_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runRead_S
	?func=jsCsv.read_S
	?args=
		&tag=${String}
		&filePath=${String}
		&withNoHeader=${String}
		&limitRowNumSource=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsCsv.read_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsCsv.kt#L61)


