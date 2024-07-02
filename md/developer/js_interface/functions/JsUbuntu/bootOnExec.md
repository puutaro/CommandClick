# jsUbuntu.bootOnExec

## Definition

```js.js
function jsUbuntu.bootOnExec(
	${execCodeString},
	${delayMiliTimeInt},
) -> runBootOnExec
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runBootOnExec
	?func=jsUbuntu.bootOnExec
	?args=
		&execCodeString=${String}
		&delayMiliTimeInt=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsUbuntu.bootOnExec](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsUbuntu.kt#L129)

## Detail

-> [jsUbuntu.bootOnExec](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsUbuntu/bootOnExec.md)
