# jsQrLogoEdit.edit_S

## Definition

```js.js
function jsQrLogoEdit.edit_S(
	${clickFileNameString},
) -> runEdit_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runEdit_S
	?func=jsQrLogoEdit.edit_S
	?args=
		&clickFileName=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Edit QR image by dialog in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

### Corresponding macro

-> [EDIT_LOGO](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_logo.md#edit_logo)

### clickFileName arg

clicked file name

### Example

```js.js
var=runExecQr
   ?func=jsQrLogoEdit.exec_S
   ?args=
       &clickFileName=${file name}

```



## Src

-> [jsQrLogoEdit.edit_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/qr/JsQrLogoEdit.kt#L32)


