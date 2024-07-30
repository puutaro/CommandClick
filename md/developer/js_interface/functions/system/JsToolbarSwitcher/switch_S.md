# jsToolbarSwitcher.switch_S

## Definition

```js.js
function jsToolbarSwitcher.switch_S(
	${execPlayBtnLongPressString},
) -> runSwitch_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runSwitch_S
	?func=jsToolbarSwitcher.switch_S
	?args=
		&execPlayBtnLongPress=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Change toolbar on edit box

### Corresponding macro

-> [WEB_SEARCH](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#web_search)

-> [PAGE_SEARCH](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#page_search)

-> [NORMAL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#normal)

### execPlayBtnLongPress

Specify long press type string

| Long press type string        | Description                               |
|-------------|------------------------------------------|
| `WEB_SEARCH` | Switch web search mode |
| `PAGE_SEARCH` | Switch page search mode |
| `NORMAL` | Switch normal toolbar |



## Src

-> [jsToolbarSwitcher.switch_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/system/JsToolbarSwitcher.kt#L29)


