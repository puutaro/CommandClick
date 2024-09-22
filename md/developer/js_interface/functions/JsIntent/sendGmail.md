# jsIntent.sendGmail

## Definition

```js.js
function jsIntent.sendGmail(
	${titleString},
	${bodyString},
	${extraMapConString},
) -> runSendGmail
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runSendGmail
	?func=jsIntent.sendGmail
	?args=
		&title=${String}
		&body=${String}
		&extraMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsIntent.sendGmail](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsIntent.kt#L82)


