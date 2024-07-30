# jsProcessKiller.kill_S

## Definition

```js.js
function jsProcessKiller.kill_S(
	${currentAppDirPathString},
	${fannelNameString},
) -> runKill_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runKill_S
	?func=jsProcessKiller.kill_S
	?args=
		&currentAppDirPath=${String}
		&fannelName=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Kill process by dialog

### Corresponding macro

-> [KILL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#kill)



## Src

-> [jsProcessKiller.kill_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsProcessKiller.kt#L12)


