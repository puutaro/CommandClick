# jsQrGetter.get_S

## Definition

```js.js
function jsQrGetter.get_S(
	${stockConDirPathForTsvString},
	${compPrefixString},
	${compSuffixString},
) -> runGet_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runGet_S
	?func=jsQrGetter.get_S
	?args=
		&stockConDirPathForTsv=${String}
		&compPrefix=${String}
		&compSuffix=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Get contents from QR code

## Corresponding macro

-> [GET_QR_CON](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_qr_con)

## stockConDirPathForTsv arg

      　-> [parentDirPath in args for GET_QR_CON macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_qr_con)

## compPrefix arg

-> [compPrefix in args for GET_QR_CON macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_qr_con)

## compSuffix arg

-> [compSuffix in args for GET_QR_CON macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_qr_con)


## Example

```js.js
run=getFile
    ?func=jsQrGetter.get_S
    ?args=
        &stockConDirPathForTsv=${save dir path}
        &compSuffix=".txt"
```
        `


## Src

-> [jsQrGetter.get_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/qr/JsQrGetter.kt#L25)


