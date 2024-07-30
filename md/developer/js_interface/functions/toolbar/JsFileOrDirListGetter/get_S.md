# jsFileOrDirListGetter.get_S

## Definition

```js.js
function jsFileOrDirListGetter.get_S(
	${onDirectoryPickBoolean},
	${filterMapConString},
) -> runGet_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runGet_S
	?func=jsFileOrDirListGetter.get_S
	?args=
		&onDirectoryPick=${Boolean}
		&filterMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Get files or dirs by filej picker

## Corresponding macro

-> [GET_FILES](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_files)

-> [GET_DIRS](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_dirs)

## onDirectoryPick arg

| Type        | Description                               |
|-------------|------------------------------------------|
| `true` | Pick dir |
| `false` | Pick file |

## filterMapCon arg

-> [args for GET_FILES macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_files)

- Each key-value is separated by `|`

## Example

```js.js
run=getFile
    ?func=jsFileOrDirGetter.kt.get_S
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

-> [jsFileOrDirListGetter.get_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsFileOrDirListGetter.kt#L21)


