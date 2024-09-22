# jsUrl.loadJsPath

## Definition

```js.js
function jsUrl.loadJsPath(
	${jsPathString},
	${replaceMapConString},
) -> runLoadJsPath
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runLoadJsPath
	?func=jsUrl.loadJsPath
	?args=
		&jsPath=${String}
		&replaceMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Load js path

### replaceMapCon arg

-> [replace variable](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md)


## Src

-> [jsUrl.loadJsPath](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsUrl.kt#L73)


