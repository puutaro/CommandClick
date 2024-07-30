# jsAddUrlCon.add_S

## Definition

```js.js
function jsAddUrlCon.add_S(
	${extraMapConString},
) -> runAdd_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runAdd_S
	?func=jsAddUrlCon.add_S
	?args=
		&extraMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Add url contents to list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Corresponding macro

-> [ADD_URL_CON](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_url_con)

## extraMapCon arg

-> [Args for add url con](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_url_con)

## Example

```js
var=runAddUrlCon
    ?func=jsAddUrlCon.add_S
    ?args=
        extraMapCon=`
            urlStringOrMacro=RECENT
            |onSearchBtn=ON
            |urlConSaveParentDirPath=`${listDir}`
            |compSuffix=".txt"
        `
```



## Src

-> [jsAddUrlCon.add_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsAddUrlCon.kt#L23)


