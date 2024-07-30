# jsFileAdder.add

## Definition

```js.js
function jsFileAdder.add(
	${compFileNameMapConString},
	${separatorString},
) -> runAdd
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runAdd
	?func=jsFileAdder.add
	?args=
		&compFileNameMapCon=${String}
		&separator=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Add file or tsv line by [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md##type) listIndex

### Corresponding macro

-> [ADD](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add)

### compFileNameMapCon arg

-> [args for add macro in toolbar macro](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add)

### separator arg

separator for compFileNameMapCon

### Example

```js.js
var=runAdd
    ?func=jsFleAdder.add
    ?args=
        &compFileNameMapCon=`
            dirPath="${image2AsciiArtGalleryDirPath}"NEW_LINE
            titleArgs="macro=CAMEL_TO_BLANK_SNAKE?compSuffix=List"NEW_LINE
            `
    ?separator=`NEW_LINE`
```



## Src

-> [jsFileAdder.add](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/toolbar/JsFileAdder.kt#L41)


