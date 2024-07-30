# jsExecQr.exec_S

## Definition

```js.js
function jsExecQr.exec_S(
	${clickFileNameString},
) -> runExec_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runExec_S
	?func=jsExecQr.exec_S
	?args=
		&clickFileName=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Exec qr contents from file in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [EXEC_QR](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_logo.md#exec_qr)

## clickFileName arg

clicked file name

## Example

```js.js
var=runExecQr
   ?func=jsExecQr.exec_S
   ?args=
       &clickFileName=${file name}

```



## Src

-> [jsExecQr.exec_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/qr/JsExecQr.kt#L25)


