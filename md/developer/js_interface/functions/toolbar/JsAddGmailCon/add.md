# jsAddGmailCon.add

## Definition

```js.js
function jsAddGmailCon.add(
	${gmailAdString},
	${extraMapConString},
) -> runAdd
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runAdd
	?func=jsAddGmailCon.add
	?args=
		&gmailAd=${String}
		&extraMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Add gmail contents from url

### Corresponding macro

-> [ADD_GMAIL_CON](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_gmail_con)

### gmailAd arg

Target gmail url address

### extraMapCon arg

-> [Args for ADD_GMAIL_CON macro](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_gmail_con)

### Example

```js.js
var=runAddGmailAdCon
    ?func=jsAddGmailCon.add
    ?args=
        &gmailAd="http://gmail.com/~"
        &extraMapCon=`
            urlConSaveParentDirPath=${cmdTtsPlayerSaveUrlConDirPath}
            |compSuffix=${TXT_SUFFIX}
        `
```



## Src

-> [jsAddGmailCon.add](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsAddGmailCon.kt#L19)


