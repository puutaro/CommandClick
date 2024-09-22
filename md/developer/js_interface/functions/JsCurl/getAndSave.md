# jsCurl.getAndSave 

## Definition

```js.js
function jsCurl.getAndSave (
	${savePathString},
	${mainUrlString},
	${queryParameterString},
	${headerString},
	${timeoutInt},
) -> runGetAndSave
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runGetAndSave
	?func=jsCurl.getAndSave 
	?args=
		&savePath=${String}
		&mainUrl=${String}
		&queryParameter=${String}
		&header=${String}
		&timeout=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsCurl.getAndSave ](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsCurl.kt#L50)


