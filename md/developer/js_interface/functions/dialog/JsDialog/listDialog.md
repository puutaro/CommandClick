# jsDialog.listDialog

## Definition

```js.js
function jsDialog.listDialog(
	${titleString},
	${messageString},
	${listSourceString},
) -> selectedItem
```


## Definition by js action

```js.js
var=selectedItem
	?func=jsDialog.listDialog
	?args=
		&title=${String}
		&message=${String}
		&listSource=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

## Description

### About listSource
list item string separated by newline

- Enable icon specify by tab second field
- ref [icon](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md)

```js.js
listSource=`
    ${item1}	{icon name1}
    ${item2}	{icon name2}
    3${item3}	{icon name3}
    .
    .
    .
`
```



## Src

-> [jsDialog.listDialog](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/dialog/JsDialog.kt#L96)


