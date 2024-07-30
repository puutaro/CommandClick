# jsUrlAdder.add_S

## Definition

```js.js
function jsUrlAdder.add_S(
	${urlStringOrMacroString},
	${onSearchBtnString},
) -> runAdd_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runAdd_S
	?func=jsUrlAdder.add_S
	?args=
		&urlStringOrMacro=${String}
		&onSearchBtn=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Add url to list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

### Corresponding macro

-> [ADD_URL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_url)

### urlStringOrMacro arg

-> [Args for add url con](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_url_con)

### onSearchBtn arg

-> [Args for add url con](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_url_con)

### Example

```js.js
var=runAddUrl
    ?args=
        urlStringOrMacro="https://www.youtube.com/"
        &onSearchBtn=OFF
```



## Src

-> [jsUrlAdder.add_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsUrlAdder.kt#L27)


