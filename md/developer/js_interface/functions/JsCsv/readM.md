# jsCsv.readM

## Definition

```js.js
function jsCsv.readM(
	${tagString},
	${csvStringString},
	${csvOrTsvString},
) -> runReadM
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runReadM
	?func=jsCsv.readM
	?args=
		&tagString=${String}
		&csvStringString=${String}
		&csvOrTsvString=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsCsv.readM](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsCsv.kt#L155)

## Detail

-> [jsCsv.readM](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsCsv/readM.md)
