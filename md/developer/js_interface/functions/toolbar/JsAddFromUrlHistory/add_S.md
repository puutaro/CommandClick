# jsAddFromUrlHistory.add_S

## Definition

```js.js
function jsAddFromUrlHistory.add_S(
	${argsMapConString},
	${separatorString},
) -> runAdd_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runAdd_S
	?func=jsAddFromUrlHistory.add_S
	?args=
		&argsMapCon=${String}
		&separator=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Add url to tsv from selected one in url history recent's 5s

## Corresponding macro

-> [ADD_URL_HISTORY](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_url_history)

## argsMapCon arg

| arg | type | description |
| --------- | --------- | --------- |
| [Optional] shellPath | string | shell path for filtering url |
| [Optional] replace vars | arg list | replace var with this args |

## separator arg

separator for argsMapCon

## Example without shell path

```js.js
var=runAddFromUrlHistory
    ?func=jsAddFromUrlHistory.add_S
    ?args=
        argsMapCon=""
        &separator="NEW_LINE"
```

## Example with shell path

```js.js
var=runAddFromUrlHistory
    ?func=jsAddFromUrlHistory.add_S
    args=
        argsMapCon=`
            shellPath=${shell path}NEW_LINE
            youtube_name=youtuveNEW_LINE
            httpPrefix=httpNEW_LINE
        `
        &separator="NEW_LINE"
```

- `${shell path}` con

```sh.sh

echo "${TARGET_CON}"             | ${b} grep "${youtube_name}"             | grep ^E "^${httpPrefix}"
```

- `${b}` -> busybox symlink path




## Src

-> [jsAddFromUrlHistory.add_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsAddFromUrlHistory.kt#L20)


