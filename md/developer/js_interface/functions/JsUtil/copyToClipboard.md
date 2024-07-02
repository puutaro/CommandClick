# jsUtil.copyToClipboard

## Definition

```js.js
function jsUtil.copyToClipboard(
	${textString?},
	${fontSizeInt},
) -> runCopyToClipboard
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runCopyToClipboard
	?func=jsUtil.copyToClipboard
	?args=
&textString?=
		&fontSize=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition

## Src

-> [jsUtil.copyToClipboard](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsUtil.kt#L22)

## Detail

-> [jsUtil.copyToClipboard](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/details/JsUtil/copyToClipboard.md)
