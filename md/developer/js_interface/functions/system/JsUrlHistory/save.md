# jsUrlHistory.save

## Definition

```js.js
function jsUrlHistory.save(
	${titleString},
	${urlString},
) -> runSave
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runSave
	?func=jsUrlHistory.save
	?args=
		&title=${String}
		&url=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsUrlHistory.save](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/system/JsUrlHistory.kt#L14)


