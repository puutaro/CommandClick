# jsFileOrDirGetter.get_S

## Definition

```js.js
function jsFileOrDirGetter.get_S(
	${onDirectoryPickBoolean},
	${filterMapConString},
) -> runGet_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runGet_S
	?func=jsFileOrDirGetter.get_S
	?args=
		&onDirectoryPick=${Boolean}
		&filterMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Get file or dir by file picker

## Corresponding macro

-> [GET_FILE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_file)

-> [GET_DIR](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_dir)

## onDirectoryPick arg

| Type        | Description                               |
|-------------|------------------------------------------|
| `true` | Pick dir |
| `false` | Pick file |

## filterMapCon arg

-> [args for GET_FILE macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_file)

- Each key-value is separated by `|`

## Example

```js.js
run=getFile
    ?func=jsFileOrDirGetter.kt.kt.get_S
    ?args=
        &onDirectoryPick=false
        &filterMapCon=`
            |suffix=".mp3&m4a"
            |initialPath="${STORAGE}/Music"
            |macro=FROM_RECENT_DIR
            |tag=addByOne
```
        `


## Src

-> [jsFileOrDirGetter.get_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsFileOrDirGetter.kt#L33)


