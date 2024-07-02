# jsIntent.launchApp

## Definition

```js.js
function jsIntent.launchApp(
	${actionString},
	${uriStringString},
	${extraListStrTabSepaString},
	${extraListIntTabSepaString},
	${extraListLongTabSepaString},
	${extraListFloatTabSepaString},
) -> runLaunchApp
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runLaunchApp
	?func=jsIntent.launchApp
	?args=
		&actionString=${String}
		&uriStringString=${String}
		&extraListStrTabSepaString=${String}
		&extraListIntTabSepaString=${String}
		&extraListLongTabSepaString=${String}
		&extraListFloatTabSepaString=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Src

-> [jsIntent.launchApp](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsIntent.kt#L84)

## Detail

-> [jsIntent.launchApp](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsIntent/launchApp.md)
